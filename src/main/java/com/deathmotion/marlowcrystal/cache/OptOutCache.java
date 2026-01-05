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

        if (!optedOutServers.contains(serverKey)) {
            optedOutServers.add(serverKey);
        }

        optedOut = true;
    }

    public boolean isServerOptedOut(@Nullable String serverKey) {
        return serverKey != null && optedOutServers.contains(serverKey);
    }

    public boolean shouldNotify(@Nullable String serverKey) {
        if (serverKey == null) return true;

        if (notifiedServers.contains(serverKey)) {
            return false;
        }

        notifiedServers.add(serverKey);
        return true;
    }

    public void clearCurrentSession() {
        optedOut = false;
    }
}
