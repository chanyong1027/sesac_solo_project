package com.stagelog.Stagelog.spotify.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class SpotifyRateLimiter {

    private static final long MIN_INTERVAL_MS = 667;  // ~1.5 req/sec
    private final AtomicLong lastRequestTime = new AtomicLong(0);

    /**
     * Rate limit 체크 및 대기
     */
    public void acquire() {
        long now = System.currentTimeMillis();
        long last = lastRequestTime.get();
        long elapsed = now - last;

        if (elapsed < MIN_INTERVAL_MS) {
            long sleepTime = MIN_INTERVAL_MS - elapsed;
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Rate limiter interrupted");
            }
        }

        lastRequestTime.set(System.currentTimeMillis());
    }

    /**
     * 429 에러 후 대기
     */
    public void waitForRetry(int retryAfterSeconds) {
        log.warn("⏸️  Rate limit hit! Waiting {} seconds...", retryAfterSeconds);
        try {
            Thread.sleep(retryAfterSeconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Retry wait interrupted");
        }
    }
}
