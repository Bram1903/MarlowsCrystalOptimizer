package com.deathmotion.marlowcrystal.update;

import com.deathmotion.marlowcrystal.Log;
import com.deathmotion.marlowcrystal.config.Settings;
import com.deathmotion.marlowcrystal.config.UpdateSource;
import com.deathmotion.marlowcrystal.loader.LoaderAccess;
import com.deathmotion.marlowcrystal.update.source.GitHubSource;
import com.deathmotion.marlowcrystal.update.source.ModrinthSource;
import com.deathmotion.marlowcrystal.version.CurrentBuild;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused") // on Fabric only the Mod Menu and YACL integrations call this, and Stonecutter drops them below 1.20.2
public final class UpdateCheck {

    private final Settings settings;

    private final LoaderAccess loader;

    // Every build per source, so turning Experimental Builds on or off needs no new request.
    private final Map<UpdateSource, List<PublishedBuild>> builds = new ConcurrentHashMap<>();

    public UpdateCheck(Settings settings, LoaderAccess loader) {
        this.settings = settings;
        this.loader = loader;
    }

    private static BuildSource sourceFor(UpdateSource source) {
        return switch (source) {
            case MODRINTH -> new ModrinthSource();
            case GITHUB -> new GitHubSource();
        };
    }

    public synchronized void run() {
        UpdateSource source = settings.getUpdateSource();
        if (!builds.containsKey(source)) {
            try {
                builds.put(source, fetch(source));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        Optional<PublishedBuild> update = available();
        update.ifPresent(build -> Log.info("Version " + build.version().toDisplayString() + " is available at " + build.downloadUrl()));
        loader.showUpdate(update.orElse(null));
    }

    public Optional<PublishedBuild> available() {
        List<PublishedBuild> published = builds.get(settings.getUpdateSource());
        if (published == null) {
            return Optional.empty();
        }

        return published.stream()
                .filter(build -> settings.isExperimentalBuilds() || build.channel() == ReleaseChannel.RELEASE)
                .max(Comparator.comparing(PublishedBuild::version))
                .filter(build -> build.version().isNewerThan(CurrentBuild.VERSION));
    }

    public void settingsSaved() {
        if (builds.isEmpty()) {
            return;
        }

        // Shown before the settings screen closes when the source was already checked, otherwise once it answers.
        if (builds.containsKey(settings.getUpdateSource())) {
            loader.showUpdate(available().orElse(null));
        } else {
            CompletableFuture.runAsync(this::run);
        }
    }

    private List<PublishedBuild> fetch(UpdateSource source) throws InterruptedException {
        Optional<String> minecraftVersion = loader.minecraftVersion();
        if (minecraftVersion.isEmpty()) {
            return List.of();
        }

        Log.info("Checking " + source.displayName() + " for updates");
        try {
            return sourceFor(source).builds(minecraftVersion.get());
        } catch (IOException | RuntimeException e) {
            Log.warn("Could not check " + source.displayName() + " for updates: " + e.getMessage());
            return List.of();
        }
    }
}
