package com.deathmotion.marlowcrystal.config;

public final class Settings {

    private volatile UpdateSource updateSource = UpdateSource.MODRINTH;

    private volatile boolean experimentalBuilds;

    private volatile boolean keepRender;

    public UpdateSource getUpdateSource() {
        UpdateSource source = updateSource;
        return source != null ? source : UpdateSource.MODRINTH;
    }

    // Only the YACL settings screen calls this, and Stonecutter drops that screen below 1.20.2.
    @SuppressWarnings("unused")
    public void setUpdateSource(UpdateSource updateSource) {
        this.updateSource = updateSource;
    }

    public boolean isExperimentalBuilds() {
        return experimentalBuilds;
    }

    // Only the YACL settings screen calls this, and Stonecutter drops that screen below 1.20.2.
    @SuppressWarnings("unused")
    public void setExperimentalBuilds(boolean experimentalBuilds) {
        this.experimentalBuilds = experimentalBuilds;
    }

    public boolean isKeepRender() {
        return keepRender;
    }

    // Only the YACL settings screen calls this, and Stonecutter drops that screen below 1.20.2.
    @SuppressWarnings("unused")
    public void setKeepRender(boolean keepRender) {
        this.keepRender = keepRender;
    }
}
