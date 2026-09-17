package com.deathmotion.marlowcrystal.version;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ModLoader {
    FABRIC(0, "fabric"),
    NEOFORGE(1, "neoforge");

    private final int networkId;

    private final String id;

    ModLoader(int networkId, String id) {
        this.networkId = networkId;
        this.id = id;
    }

    public static @Nullable ModLoader byNetworkId(int networkId) {
        for (ModLoader loader : values()) {
            if (loader.networkId == networkId) {
                return loader;
            }
        }
        return null;
    }

    public int networkId() {
        return networkId;
    }

    public @NotNull String id() {
        return id;
    }
}
