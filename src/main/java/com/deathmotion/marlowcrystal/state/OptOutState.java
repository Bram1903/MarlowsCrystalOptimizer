package com.deathmotion.marlowcrystal.state;

import lombok.Getter;

public final class OptOutState {

    @Getter
    private volatile boolean optedOut;

    private volatile boolean notified;

    public void markOptedOut() {
        optedOut = true;
    }

    public synchronized boolean claimNotification() {
        if (notified) return false;

        notified = true;
        return true;
    }

    public synchronized void reset() {
        optedOut = false;
        notified = false;
    }
}
