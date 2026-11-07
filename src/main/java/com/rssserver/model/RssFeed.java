package com.rssserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

/** Equivalent of RSS.go's RSSFeed struct. */
@JacksonXmlRootElement(localName = "rss")
public class RssFeed {

    @JacksonXmlProperty(localName = "channel")
    private Channel channel = new Channel();

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public static class Channel {

        @JacksonXmlProperty(localName = "title")
        private String title;

        @JacksonXmlProperty(localName = "link")
        private String link;

        @JacksonXmlProperty(localName = "description")
        private String description;

        @JacksonXmlProperty(localName = "language")
        private String language;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "item")
        private List<RssItem> item;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public List<RssItem> getItem() {
            return item;
        }

        public void setItem(List<RssItem> item) {
            this.item = item;
        }
    }
}
