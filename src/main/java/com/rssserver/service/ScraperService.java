package com.rssserver.service;

import com.rssserver.config.AppProperties;
import com.rssserver.model.Feed;
import com.rssserver.model.Post;
import com.rssserver.model.RssFeed;
import com.rssserver.model.RssItem;
import com.rssserver.repository.FeedRepository;
import com.rssserver.repository.PostRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Equivalent of scraper.go's startScraping/scrapeFeed: a ticking background
 * worker pool that periodically pulls the least-recently-fetched feeds,
 * fetches+parses their RSS XML, and inserts any new posts.
 */
@Service
public class ScraperService {

    private static final Logger log = LoggerFactory.getLogger(ScraperService.class);

    private final FeedRepository feedRepository;
    private final PostRepository postRepository;
    private final RssFeedService rssFeedService;
    private final AppProperties appProperties;

    private ScheduledExecutorService scheduler;
    private ExecutorService workerPool;

    public ScraperService(FeedRepository feedRepository, PostRepository postRepository,
                           RssFeedService rssFeedService, AppProperties appProperties) {
        this.feedRepository = feedRepository;
        this.postRepository = postRepository;
        this.rssFeedService = rssFeedService;
        this.appProperties = appProperties;
    }

    @PostConstruct
    public void start() {
        int concurrency = appProperties.getMaxConcurrency();
        long intervalMillis = appProperties.getScrapeInterval().toMillis();

        log.info("Background scraper started workers={} interval={}", concurrency, appProperties.getScrapeInterval());

        workerPool = Executors.newFixedThreadPool(concurrency);
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::tick, intervalMillis, intervalMillis, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void stop() {
        log.info("Stopping scraper...");
        if (scheduler != null) scheduler.shutdownNow();
        if (workerPool != null) workerPool.shutdownNow();
    }

    private void tick() {
        List<Feed> feeds;
        try {
            feeds = feedRepository.findNextFeedsToFetch(PageRequest.of(0, appProperties.getMaxConcurrency()));
        } catch (Exception e) {
            log.error("Failed to fetch feeds", e);
            return;
        }

        List<Future<?>> futures = feeds.stream()
                .map(feed -> workerPool.submit(() -> scrapeFeed(feed)))
                .toList();

        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (Exception e) {
                log.error("Scrape task failed", e);
            }
        }
    }

    private void scrapeFeed(Feed feed) {
        try {
            feed.setLastFetchedAt(Instant.now());
            feed.setUpdatedAt(Instant.now());
            feedRepository.save(feed);
        } catch (Exception e) {
            log.error("Error marking feed as fetched: {}", e.getMessage());
            return;
        }

        RssFeed feedData;
        try {
            feedData = rssFeedService.fetch(feed.getUrl());
        } catch (Exception e) {
            log.error("Error fetching feed data for feed {}: {}", feed.getId(), e.getMessage());
            return;
        }

        List<RssItem> items = feedData.getChannel().getItem();
        if (items == null) {
            return;
        }

        for (RssItem item : items) {
            Post post = new Post();
            post.setId(UUID.randomUUID());
            post.setTitle(item.getTitle());
            post.setDescription(item.getDescription() == null || item.getDescription().isEmpty()
                    ? null : item.getDescription());
            post.setCreatedAt(Instant.now());
            post.setUpdatedAt(Instant.now());
            post.setUrl(item.getLink());
            post.setFeedId(feed.getId());

            try {
                // Go used time.RFC1123Z ("Mon, 02 Jan 2006 15:04:05 -0700")
                post.setPublishedAt(ZonedDateTime.parse(item.getPubDate(), DateTimeFormatter.RFC_1123_DATE_TIME).toInstant());
            } catch (Exception e) {
                log.warn("couldnt parse date {} with err {}", item.getPubDate(), e.getMessage());
                post.setPublishedAt(Instant.now());
            }

            try {
                postRepository.save(post);
            } catch (DataIntegrityViolationException e) {
                // duplicate key (unique url) -- same as Go's "duplicate key" check, skip silently
            } catch (Exception e) {
                log.error("Failed to create post title={} url={} error={}", item.getTitle(), item.getLink(), e.getMessage());
            }
        }

        log.info("Successfully fetched feed: {}", feed.getName());
    }
}
