package com.deathmotion.marlowcrystal;

import com.deathmotion.marlowcrystal.config.Settings;
import com.deathmotion.marlowcrystal.config.SettingsFile;
import com.deathmotion.marlowcrystal.crystal.CrystalBreaker;
import com.deathmotion.marlowcrystal.crystal.KeptCrystals;
import com.deathmotion.marlowcrystal.loader.LoaderAccess;
//? if fabric {
import com.deathmotion.marlowcrystal.loader.fabric.FabricLoaderAccess;
//?} else {
/*import com.deathmotion.marlowcrystal.loader.neoforge.NeoForgeLoaderAccess;
*///?}
import com.deathmotion.marlowcrystal.network.ServerSession;
import com.deathmotion.marlowcrystal.update.UpdateCheck;
import com.deathmotion.marlowcrystal.version.CurrentBuild;
import com.deathmotion.marlowcrystal.version.ModVersion;

public final class MarlowCrystal {

    public static final String MOD_ID = "marlowcrystal";

    // Built on first use rather than by the entrypoint, because Mod Menu may run the update check first.
    //? if fabric {
    private static final MarlowCrystal INSTANCE = new MarlowCrystal(new FabricLoaderAccess());
    //?} else {
    /*private static final MarlowCrystal INSTANCE = new MarlowCrystal(new NeoForgeLoaderAccess());
    *///?}

    private final SettingsFile settingsFile;

    private final Settings settings;

    private final UpdateCheck updateCheck;

    private final ServerSession serverSession = new ServerSession();

    private final KeptCrystals keptCrystals = new KeptCrystals();

    private final CrystalBreaker crystalBreaker = new CrystalBreaker(keptCrystals);

    private MarlowCrystal(LoaderAccess loader) {
        settingsFile = new SettingsFile(loader.configDirectory().resolve(MOD_ID + ".json"));
        settings = settingsFile.load();
        updateCheck = new UpdateCheck(settings, loader);
    }

    public static MarlowCrystal get() {
        return INSTANCE;
    }

    public static void initialize() {
        ModVersion version = CurrentBuild.VERSION;
        String commit = version.commit();
        Log.info("Mod initialized, version " + version.toDisplayString()
                + " for Minecraft " + CurrentBuild.MINECRAFT_RANGE
                + " built " + CurrentBuild.TIMESTAMP
                + (commit != null ? " (" + commit + (CurrentBuild.DIRTY ? ", dirty" : "") + ")" : ""));
    }

    public Settings settings() {
        return settings;
    }

    public UpdateCheck updateCheck() {
        return updateCheck;
    }

    public ServerSession serverSession() {
        return serverSession;
    }

    public KeptCrystals keptCrystals() {
        return keptCrystals;
    }

    public CrystalBreaker crystalBreaker() {
        return crystalBreaker;
    }

    // Only the YACL settings screen calls this, and Stonecutter drops that screen below 1.20.2.
    @SuppressWarnings("unused")
    public void saveSettings() {
        settingsFile.save(settings);
        updateCheck.settingsSaved();
    }
}
