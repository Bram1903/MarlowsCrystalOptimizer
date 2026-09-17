//? if fabric {
package com.deathmotion.marlowcrystal.loader.fabric;

import com.deathmotion.marlowcrystal.loader.LoaderAccess;
import com.deathmotion.marlowcrystal.update.PublishedBuild;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Optional;

public final class FabricLoaderAccess implements LoaderAccess {

    @Override
    public Path configDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Optional<String> minecraftVersion() {
        return FabricLoader.getInstance()
                .getModContainer("minecraft")
                .map(container -> container.getMetadata().getVersion().getFriendlyString());
    }

    // Mod Menu reads the available update itself.
    @Override
    public void showUpdate(@Nullable PublishedBuild update) {
    }
}
//?}
