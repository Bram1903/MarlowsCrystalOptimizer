package com.deathmotion.marlowcrystal.update;

import com.deathmotion.marlowcrystal.config.ModConfig;
import com.deathmotion.marlowcrystal.config.UpdateSource;
import com.deathmotion.marlowcrystal.util.Logger;
import com.deathmotion.marlowcrystal.versioning.MCOVersions;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public final class UpdateService {

    private static final UpdateService INSTANCE = new UpdateService();

    private final Logger logger = new Logger();

    private final Map<UpdateSource, UpdateResult> results = new EnumMap<>(UpdateSource.class);

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

    private static Optional<String> minecraftVersion() {
        return FabricLoader.getInstance()
                .getModContainer("minecraft")
                .map(container -> container.getMetadata().getVersion().getFriendlyString());
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

    private UpdateResult query(UpdateSource source) throws InterruptedException {
        Optional<String> minecraftVersion = minecraftVersion();
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
