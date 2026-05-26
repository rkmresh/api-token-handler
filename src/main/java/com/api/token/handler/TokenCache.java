package com.api.token.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TokenCache provides in-memory caching of API tokens with Time-To-Live (TTL) support.
 * Reduces redundant API calls by caching previously retrieved tokens.
 */
public class TokenCache {
    private static final Logger logger = LoggerFactory.getLogger(TokenCache.class);

    private static class CachedToken {
        String token;
        long expiryTime;

        CachedToken(String token, long ttlMillis) {
            this.token = token;
            this.expiryTime = System.currentTimeMillis() + ttlMillis;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    private final Map<String, CachedToken> cache = new ConcurrentHashMap<>();
    private final long defaultTtlMillis;

    /**
     * Creates a TokenCache with default TTL.
     *
     * @param defaultTtlMillis Default time-to-live in milliseconds (default: 3600000ms = 1 hour)
     */
    public TokenCache(long defaultTtlMillis) {
        this.defaultTtlMillis = defaultTtlMillis;
    }

    /**
     * Creates a TokenCache with 1 hour default TTL.
     */
    public TokenCache() {
        this(3600000); // 1 hour default
    }

    /**
     * Stores a token in the cache.
     *
     * @param key   The cache key (usually the endpoint URL)
     * @param token The token to cache
     */
    public void put(String key, String token) {
        put(key, token, defaultTtlMillis);
    }

    /**
     * Stores a token in the cache with custom TTL.
     *
     * @param key        The cache key (usually the endpoint URL)
     * @param token      The token to cache
     * @param ttlMillis  Time-to-live in milliseconds
     */
    public void put(String key, String token, long ttlMillis) {
        cache.put(key, new CachedToken(token, ttlMillis));
        logger.debug("Token cached for key: {} with TTL: {}ms", key, ttlMillis);
    }

    /**
     * Retrieves a token from the cache if it exists and is not expired.
     *
     * @param key The cache key
     * @return The cached token, or null if not found or expired
     */
    public String get(String key) {
        CachedToken cached = cache.get(key);
        if (cached == null) {
            logger.debug("Token cache miss for key: {}", key);
            return null;
        }

        if (cached.isExpired()) {
            logger.debug("Token cache expired for key: {}", key);
            cache.remove(key);
            return null;
        }

        logger.debug("Token cache hit for key: {}", key);
        return cached.token;
    }

    /**
     * Removes a token from the cache.
     *
     * @param key The cache key
     */
    public void invalidate(String key) {
        cache.remove(key);
        logger.debug("Token cache invalidated for key: {}", key);
    }

    /**
     * Clears all cached tokens.
     */
    public void clear() {
        cache.clear();
        logger.debug("Token cache cleared");
    }

    /**
     * Returns the number of cached tokens.
     *
     * @return Number of tokens in cache
     */
    public int size() {
        return cache.size();
    }
}
