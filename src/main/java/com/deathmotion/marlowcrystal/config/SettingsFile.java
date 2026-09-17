package com.deathmotion.marlowcrystal.config;

import com.deathmotion.marlowcrystal.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SettingsFile {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path path;

    public SettingsFile(Path path) {
        this.path = path;
    }

    public Settings load() {
        if (Files.notExists(path)) {
            return new Settings();
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            Settings settings = GSON.fromJson(reader, Settings.class);
            return settings != null ? settings : new Settings();
        } catch (IOException | JsonParseException e) {
            Log.warn("Could not read " + path.getFileName() + ", using defaults: " + e.getMessage());
            return new Settings();
        }
    }

    public synchronized void save(Settings settings) {
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(settings, writer);
            }
        } catch (IOException | JsonParseException e) {
            Log.error("Could not save " + path.getFileName() + ": " + e.getMessage());
        }
    }
}
