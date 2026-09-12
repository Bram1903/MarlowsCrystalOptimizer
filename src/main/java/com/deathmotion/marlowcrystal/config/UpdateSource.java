package com.deathmotion.marlowcrystal.config;

public enum UpdateSource {
    MODRINTH("Modrinth"),
    GITHUB("GitHub");

    private final String displayName;

    UpdateSource(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
