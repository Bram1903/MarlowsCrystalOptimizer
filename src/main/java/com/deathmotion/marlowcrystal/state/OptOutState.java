package com.deathmotion.marlowcrystal.state;

public final class OptOutState {

    private volatile boolean optedOut;

    private volatile boolean notified;

    public boolean isOptedOut() {
        return optedOut;
    }

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
