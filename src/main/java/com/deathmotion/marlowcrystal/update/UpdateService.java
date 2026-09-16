package com.deathmotion.marlowcrystal.update;

import com.deathmotion.marlowcrystal.config.ModConfig;
import com.deathmotion.marlowcrystal.config.UpdateSource;
import com.deathmotion.marlowcrystal.loader.LoaderAccess;
import com.deathmotion.marlowcrystal.util.Logger;
import com.deathmotion.marlowcrystal.versioning.MCOVersions;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"unused", "UnusedReturnValue"}) // on Fabric only the Mod Menu and YACL integrations call this, and Stonecutter drops them below 1.20.2
public final class UpdateService {

    private static final UpdateService INSTANCE = new UpdateService();

    private final Logger logger = new Logger();

    // Every build per source, so turning Experimental Builds on or off needs no new request.
    private final Map<UpdateSource, List<PublishedBuild>> builds = new ConcurrentHashMap<>();

    private UpdateService() {
    }

    public static UpdateService getInstance() {
        return INSTANCE;
    }

    private static UpdateSourceClient client(UpdateSource source) {
        return switch (source) {
            case MODRINTH -> new ModrinthSource();
            case GITHUB -> new GitHubSource();
        };
    }

    public synchronized UpdateResult check() {
        UpdateSource source = ModConfig.getInstance().getUpdateSource();
        if (!builds.containsKey(source)) {
            try {
                builds.put(source, fetch(source));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return UpdateResult.none();
            }
        }

        UpdateResult result = latest();
        if (result.available() && result.latestVersion() != null) {
            logger.info("Version " + result.latestVersion().toDisplayString() + " is available at " + result.downloadUrl());
        }
        LoaderAccess.INSTANCE.showUpdate(result);
        return result;
    }

    public UpdateResult latest() {
        ModConfig config = ModConfig.getInstance();
        List<PublishedBuild> published = builds.get(config.getUpdateSource());
        if (published == null) {
            return UpdateResult.none();
        }

        return published.stream()
                .filter(build -> config.isExperimentalBuilds() || build.channel() == ReleaseChannel.RELEASE)
                .max(Comparator.comparing(PublishedBuild::version))
                .map(build -> new UpdateResult(build.version().isNewerThan(MCOVersions.CURRENT), build.version(),
                        build.downloadUrl(), build.channel()))
                .orElseGet(UpdateResult::none);
    }

    public boolean hasChecked() {
        return !builds.isEmpty();
    }

    public void onSettingsSaved() {
        if (!hasChecked()) {
            return;
        }

        // Shown before the settings screen closes when the source was already checked, otherwise once it answers.
        if (builds.containsKey(ModConfig.getInstance().getUpdateSource())) {
            LoaderAccess.INSTANCE.showUpdate(latest());
        } else {
            CompletableFuture.runAsync(this::check);
        }
    }

    private List<PublishedBuild> fetch(UpdateSource source) throws InterruptedException {
        Optional<String> minecraftVersion = LoaderAccess.INSTANCE.minecraftVersion();
        if (minecraftVersion.isEmpty()) {
            return List.of();
        }

        logger.info("Checking " + source.getDisplayName() + " for updates");
        try {
            return client(source).builds(minecraftVersion.get());
        } catch (IOException | RuntimeException e) {
            logger.warn("Could not check " + source.getDisplayName() + " for updates: " + e.getMessage());
            return List.of();
        }
    }
}
