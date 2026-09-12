package com.deathmotion.marlowcrystal.update;

import com.deathmotion.marlowcrystal.versioning.MCOVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record UpdateResult(boolean available, @Nullable MCOVersion latestVersion, @Nullable String downloadUrl,
                           @NotNull ReleaseChannel channel) {

    public static @NotNull UpdateResult none() {
        return new UpdateResult(false, null, null, ReleaseChannel.RELEASE);
    }
}
