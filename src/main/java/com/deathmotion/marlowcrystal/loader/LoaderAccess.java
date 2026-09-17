package com.deathmotion.marlowcrystal.loader;

import com.deathmotion.marlowcrystal.update.PublishedBuild;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Optional;

public interface LoaderAccess {

    Path configDirectory();

    Optional<String> minecraftVersion();

    void showUpdate(@Nullable PublishedBuild update);
}
