package com.deathmotion.marlowcrystal.cache;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class OptOutCache {

    private final Set<String> optedOutServers = ConcurrentHashMap.newKeySet();
    private final Set<String> notifiedServers = ConcurrentHashMap.newKeySet();

    @Getter
    @Setter
    private volatile boolean optedOut;

    public void markOptedOut(String serverKey) {
        optedOutServers.add(serverKey);
        optedOut = true;
    }

    public boolean isServerOptedOut(@Nullable String serverKey) {
        return serverKey != null && optedOutServers.contains(serverKey);
    }

    public boolean shouldNotify(@Nullable String serverKey) {
        return serverKey == null || notifiedServers.add(serverKey);
    }

    public void clearCurrentSession() {
        optedOut = false;
    }
}
