package com.deathmotion.marlowcrystal.update;

import com.deathmotion.marlowcrystal.version.ModVersion;
import org.jetbrains.annotations.NotNull;

public record PublishedBuild(@NotNull ModVersion version, @NotNull String downloadUrl, @NotNull ReleaseChannel channel) {
}
