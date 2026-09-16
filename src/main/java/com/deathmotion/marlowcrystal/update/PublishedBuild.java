package com.deathmotion.marlowcrystal.update;

import com.deathmotion.marlowcrystal.versioning.MCOVersion;
import org.jetbrains.annotations.NotNull;

public record PublishedBuild(@NotNull MCOVersion version, @NotNull String downloadUrl, @NotNull ReleaseChannel channel) {
}
