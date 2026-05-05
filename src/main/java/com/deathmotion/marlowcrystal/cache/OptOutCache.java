package com.deathmotion.marlowcrystal.cache;

import com.deathmotion.marlowcrystal.util.datastructure.EvictingList;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

public final class OptOutCache {

    private static final int MAX_SERVERS = 10;

    private final EvictingList<String> optedOutServers = new EvictingList<>(MAX_SERVERS);
    private final EvictingList<String> notifiedServers = new EvictingList<>(MAX_SERVERS);

    @Getter
    @Setter
    private volatile boolean optedOut;

    public void markOptedOut(@Nullable String serverKey) {
        if (serverKey == null) return;

        synchronized (optedOutServers) {
            if (!optedOutServers.contains(serverKey)) {
                optedOutServers.add(serverKey);
            }
        }

        optedOut = true;
    }

    public boolean isServerOptedOut(@Nullable String serverKey) {
        if (serverKey == null) return false;
        synchronized (optedOutServers) {
            return optedOutServers.contains(serverKey);
        }
    }

    public boolean hasNotified(@Nullable String serverKey) {
        if (serverKey == null) return false;
        synchronized (notifiedServers) {
            return notifiedServers.contains(serverKey);
        }
    }

    public void markNotified(@Nullable String serverKey) {
        if (serverKey == null) return;

        synchronized (notifiedServers) {
            if (!notifiedServers.contains(serverKey)) {
                notifiedServers.add(serverKey);
            }
        }
    }

    public void clearCurrentSession() {
        optedOut = false;
    }
}
