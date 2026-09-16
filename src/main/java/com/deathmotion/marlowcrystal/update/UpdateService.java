package com.deathmotion.marlowcrystal.update;

import com.deathmotion.marlowcrystal.config.ModConfig;
import com.deathmotion.marlowcrystal.config.UpdateSource;
import com.deathmotion.marlowcrystal.loader.LoaderAccess;
import com.deathmotion.marlowcrystal.util.Logger;
import com.deathmotion.marlowcrystal.versioning.MCOVersions;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"unused", "UnusedReturnValue"}) // only the Mod Menu and YACL integrations call this, and Stonecutter drops them below 1.20.2
public final class UpdateService {

    private static final UpdateService INSTANCE = new UpdateService();

    private final Logger logger = new Logger();

    private final Map<UpdateSource, UpdateResult> results = new ConcurrentHashMap<>();

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
        UpdateResult cached = results.get(source);
        if (cached != null) {
            return cached;
        }

        try {
            UpdateResult result = query(source);
            results.put(source, result);
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return UpdateResult.none();
        }
    }

    public UpdateResult latest() {
        return results.getOrDefault(ModConfig.getInstance().getUpdateSource(), UpdateResult.none());
    }

    public boolean hasChecked() {
        return !results.isEmpty();
    }

    private UpdateResult query(UpdateSource source) throws InterruptedException {
        Optional<String> minecraftVersion = LoaderAccess.INSTANCE.minecraftVersion();
        if (minecraftVersion.isEmpty()) {
            return UpdateResult.none();
        }

        logger.info("Checking " + source.getDisplayName() + " for updates");
        try {
            UpdateResult result = client(source).check(MCOVersions.CURRENT, minecraftVersion.get());
            if (result.available() && result.latestVersion() != null) {
                logger.info("Version " + result.latestVersion().toDisplayString() + " is available at " + result.downloadUrl());
            }
            return result;
        } catch (IOException | RuntimeException e) {
            logger.warn("Could not check " + source.getDisplayName() + " for updates: " + e.getMessage());
            return UpdateResult.none();
        }
    }
}
