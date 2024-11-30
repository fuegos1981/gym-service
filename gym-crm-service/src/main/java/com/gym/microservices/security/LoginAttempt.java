package com.gym.microservices.security;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class LoginAttempt {

    private final AtomicInteger count = new AtomicInteger(0);

    private volatile LocalDateTime blockTime;

    public void setBlockTime(LocalDateTime blockTime) {
        synchronized (this) {
            this.blockTime = blockTime;
            count.set(0);
        }
    }

    public int getCount() {
        return count.get();
    }

    public LocalDateTime getBlockTime() {
        return blockTime;
    }

    public void incrementCount() {
        count.incrementAndGet();
    }

    public boolean isBlocked() {
        LocalDateTime currentBlockTime = blockTime;
        return currentBlockTime != null && currentBlockTime.isAfter(LocalDateTime.now());
    }
}
