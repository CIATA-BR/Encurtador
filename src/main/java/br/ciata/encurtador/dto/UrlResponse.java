package br.ciata.encurtador.dto;

import java.time.Instant;

public class UrlResponse {

    private String id;
    private String shortUrl;
    private String originalUrl;
    private Instant createdAt;
    private Instant expirationDate;
    private long clickCount;

    public UrlResponse(String id, String shortUrl, String originalUrl,
                       Instant createdAt, Instant expirationDate, long clickCount) {
        this.id = id;
        this.shortUrl = shortUrl;
        this.originalUrl = originalUrl;
        this.createdAt = createdAt;
        this.expirationDate = expirationDate;
        this.clickCount = clickCount;
    }

    public String getId() {
        return id;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpirationDate() {
        return expirationDate;
    }

    public long getClickCount() {
        return clickCount;
    }
}