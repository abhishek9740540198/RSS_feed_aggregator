package com.rssserver.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.rssserver.model.RssFeed;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/** Equivalent of RSS.go: fetchWithRetry + urlToFeed. */
@Service
public class RssFeedService {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final XmlMapper xmlMapper = new XmlMapper();

    public RssFeed fetch(String url) throws Exception {
        HttpResponse<String> response = fetchWithRetry(url);
        return xmlMapper.readValue(response.body(), RssFeed.class);
    }

    private HttpResponse<String> fetchWithRetry(String url) throws Exception {
        Duration backoff = Duration.ofSeconds(2);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        Exception lastError = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response;
                }
                lastError = new RuntimeException("request failed with status " + response.statusCode());
            } catch (Exception e) {
                lastError = e;
            }

            if (attempt == 3) {
                throw lastError;
            }
            Thread.sleep(backoff.toMillis());
            backoff = backoff.multipliedBy(2);
        }
        throw new RuntimeException("failed to fetch feed", lastError);
    }
}
