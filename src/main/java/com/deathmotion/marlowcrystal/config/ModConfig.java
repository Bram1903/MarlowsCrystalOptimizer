package com.deathmotion.marlowcrystal.config;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import com.deathmotion.marlowcrystal.util.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ModConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Logger LOGGER = new Logger();

    private static ModConfig instance;

    private volatile UpdateSource updateSource = UpdateSource.MODRINTH;

    private volatile boolean keepRender;

    private ModConfig() {
    }

    public static synchronized ModConfig getInstance() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve(MarlowCrystal.MOD_ID + ".json");
    }

    private static ModConfig load() {
        Path path = path();
        if (Files.notExists(path)) {
            return new ModConfig();
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            ModConfig config = GSON.fromJson(reader, ModConfig.class);
            return config != null ? config : new ModConfig();
        } catch (IOException | JsonParseException e) {
            LOGGER.warn("Could not read " + path.getFileName() + ", using defaults: " + e.getMessage());
            return new ModConfig();
        }
    }

    public UpdateSource getUpdateSource() {
        UpdateSource source = updateSource;
        return source != null ? source : UpdateSource.MODRINTH;
    }

    // Only the YACL settings screen calls this, and Stonecutter drops that screen below 1.20.2.
    @SuppressWarnings("unused")
    public void setUpdateSource(UpdateSource updateSource) {
        this.updateSource = updateSource;
    }

    public boolean isKeepRender() {
        return keepRender;
    }

    // Only the YACL settings screen calls this, and Stonecutter drops that screen below 1.20.2.
    @SuppressWarnings("unused")
    public void setKeepRender(boolean keepRender) {
        this.keepRender = keepRender;
    }

    // Only the YACL settings screen calls this, and Stonecutter drops that screen below 1.20.2.
    @SuppressWarnings("unused")
    public synchronized void save() {
        Path path = path();
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException | JsonParseException e) {
            LOGGER.error("Could not save " + path.getFileName() + ": " + e.getMessage());
        }
    }
}
