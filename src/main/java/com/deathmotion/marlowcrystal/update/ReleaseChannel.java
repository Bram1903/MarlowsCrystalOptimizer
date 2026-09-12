package com.deathmotion.marlowcrystal.update;

public enum ReleaseChannel {
    RELEASE,
    BETA,
    ALPHA;

    public static ReleaseChannel fromName(String name) {
        for (ReleaseChannel channel : values()) {
            if (channel.name().equalsIgnoreCase(name)) {
                return channel;
            }
        }
        return RELEASE;
    }
}
