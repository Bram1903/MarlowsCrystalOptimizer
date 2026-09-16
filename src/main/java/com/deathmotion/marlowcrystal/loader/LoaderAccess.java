package com.deathmotion.marlowcrystal.loader;

//? if fabric {
import com.deathmotion.marlowcrystal.loader.fabric.FabricLoaderAccess;
//?} else {
/*import com.deathmotion.marlowcrystal.loader.neoforge.NeoForgeLoaderAccess;
*///?}
import com.deathmotion.marlowcrystal.update.UpdateResult;

import java.nio.file.Path;
import java.util.Optional;

public interface LoaderAccess {

    //? if fabric {
    LoaderAccess INSTANCE = new FabricLoaderAccess();
    //?} else {
    /*LoaderAccess INSTANCE = new NeoForgeLoaderAccess();
    *///?}

    Path configDirectory();

    Optional<String> minecraftVersion();

    void showUpdate(UpdateResult result);
}
