package com.api.token.handler;

/**
 * Configuration class for TokenHandlerService resilience patterns.
 * Provides settings for retry, circuit breaker, rate limiting, and connection management.
 */
public class TokenHandlerConfig {

    // Connection settings
    private int connectionTimeoutMs = 5000;           // 5 seconds
    private int socketTimeoutMs = 10000;              // 10 seconds
    private int maxConnections = 20;
    private int maxConnectionsPerRoute = 10;

    // Retry settings
    private int maxRetryAttempts = 3;
    private int retryDelayMs = 1000;                  // 1 second
    private double retryDelayMultiplier = 2.0;        // Exponential backoff
    private int maxRetryDelayMs = 10000;              // 10 seconds max

    // Circuit Breaker settings
    private int circuitBreakerFailureThreshold = 50;  // Fail after 50% failures
    private int circuitBreakerSlowCallRateThreshold = 70; // Slow call threshold 70%
    private long circuitBreakerWaitDurationMs = 60000; // 1 minute wait before retry
    private int circuitBreakerSlidingWindowSize = 100; // Window size for monitoring

    // Rate Limiter settings
    private int rateLimitPermits = 100;               // 100 requests
    private long rateLimitPeriodMs = 60000;           // per minute
    private int rateLimitTimeoutMs = 5000;            // timeout for acquiring permit

    // Bulkhead settings
    private int bulkheadMaxConcurrentCalls = 10;
    private long bulkheadMaxWaitDurationMs = 10000;

    // Cache settings
    private long cacheTtlMs = 3600000;                // 1 hour

    // Getter methods
    public int getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public int getSocketTimeoutMs() {
        return socketTimeoutMs;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public int getMaxConnectionsPerRoute() {
        return maxConnectionsPerRoute;
    }

    public int getMaxRetryAttempts() {
        return maxRetryAttempts;
    }

    public int getRetryDelayMs() {
        return retryDelayMs;
    }

    public double getRetryDelayMultiplier() {
        return retryDelayMultiplier;
    }

    public int getMaxRetryDelayMs() {
        return maxRetryDelayMs;
    }

    public int getCircuitBreakerFailureThreshold() {
        return circuitBreakerFailureThreshold;
    }

    public int getCircuitBreakerSlowCallRateThreshold() {
        return circuitBreakerSlowCallRateThreshold;
    }

    public long getCircuitBreakerWaitDurationMs() {
        return circuitBreakerWaitDurationMs;
    }

    public int getCircuitBreakerSlidingWindowSize() {
        return circuitBreakerSlidingWindowSize;
    }

    public int getRateLimitPermits() {
        return rateLimitPermits;
    }

    public long getRateLimitPeriodMs() {
        return rateLimitPeriodMs;
    }

    public int getRateLimitTimeoutMs() {
        return rateLimitTimeoutMs;
    }

    public int getBulkheadMaxConcurrentCalls() {
        return bulkheadMaxConcurrentCalls;
    }

    public long getBulkheadMaxWaitDurationMs() {
        return bulkheadMaxWaitDurationMs;
    }

    public long getCacheTtlMs() {
        return cacheTtlMs;
    }

    // Setter methods for fluent configuration
    public TokenHandlerConfig setConnectionTimeoutMs(int ms) {
        this.connectionTimeoutMs = ms;
        return this;
    }

    public TokenHandlerConfig setSocketTimeoutMs(int ms) {
        this.socketTimeoutMs = ms;
        return this;
    }

    public TokenHandlerConfig setMaxConnections(int max) {
        this.maxConnections = max;
        return this;
    }

    public TokenHandlerConfig setMaxConnectionsPerRoute(int max) {
        this.maxConnectionsPerRoute = max;
        return this;
    }

    public TokenHandlerConfig setMaxRetryAttempts(int attempts) {
        this.maxRetryAttempts = attempts;
        return this;
    }

    public TokenHandlerConfig setRetryDelayMs(int ms) {
        this.retryDelayMs = ms;
        return this;
    }

    public TokenHandlerConfig setRetryDelayMultiplier(double multiplier) {
        this.retryDelayMultiplier = multiplier;
        return this;
    }

    public TokenHandlerConfig setMaxRetryDelayMs(int ms) {
        this.maxRetryDelayMs = ms;
        return this;
    }

    public TokenHandlerConfig setCircuitBreakerFailureThreshold(int threshold) {
        this.circuitBreakerFailureThreshold = threshold;
        return this;
    }

    public TokenHandlerConfig setCircuitBreakerWaitDurationMs(long ms) {
        this.circuitBreakerWaitDurationMs = ms;
        return this;
    }

    public TokenHandlerConfig setRateLimitPermits(int permits) {
        this.rateLimitPermits = permits;
        return this;
    }

    public TokenHandlerConfig setCacheTtlMs(long ms) {
        this.cacheTtlMs = ms;
        return this;
    }

    @Override
    public String toString() {
        return "TokenHandlerConfig{" +
                "connectionTimeoutMs=" + connectionTimeoutMs +
                ", socketTimeoutMs=" + socketTimeoutMs +
                ", maxRetryAttempts=" + maxRetryAttempts +
                ", circuitBreakerWaitDurationMs=" + circuitBreakerWaitDurationMs +
                ", cacheTtlMs=" + cacheTtlMs +
                '}';
    }
}
