//? if fabric {
package com.deathmotion.marlowcrystal.loader.fabric;

import com.deathmotion.marlowcrystal.loader.LoaderAccess;
import com.deathmotion.marlowcrystal.update.UpdateResult;
import net.fabricmc.loader.api.FabricLoader;

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

    // Mod Menu reads the latest result itself.
    @Override
    public void showUpdate(UpdateResult result) {
    }
}
//?}
