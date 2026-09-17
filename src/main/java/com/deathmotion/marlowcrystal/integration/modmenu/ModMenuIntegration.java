//? if fabric {
package com.deathmotion.marlowcrystal.integration.modmenu;

//? if >=1.20.5 {
import com.deathmotion.marlowcrystal.MarlowCrystal;
//?}
//? if >=1.20.2 {
import com.deathmotion.marlowcrystal.integration.yacl.SettingsScreen;
//?}
//? if >=1.20.5 {
import com.deathmotion.marlowcrystal.update.PublishedBuild;
//?}
//? if >=1.20.2 {
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
//?}
import com.terraformersmc.modmenu.api.ModMenuApi;
//? if >=1.20.5 {
import com.terraformersmc.modmenu.api.UpdateChannel;
import com.terraformersmc.modmenu.api.UpdateChecker;
import com.terraformersmc.modmenu.api.UpdateInfo;
//?}
//? if >=1.20.2 {
import net.fabricmc.loader.api.FabricLoader;
//?}

//? if >=1.20.5 {
import java.util.Optional;
//?}

public class ModMenuIntegration implements ModMenuApi {

    //? if >=1.20.2 {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (FabricLoader.getInstance().isModLoaded(SettingsScreen.YACL_MOD_ID)) {
            return SettingsScreen::create;
        }
        return ModMenuApi.super.getModConfigScreenFactory();
    }
    //?}

    //? if >=1.20.5 {
    @Override
    public UpdateChecker getUpdateChecker() {
        return () -> {
            MarlowCrystal.get().updateCheck().run();
            return AvailableUpdate.INSTANCE;
        };
    }

    private enum AvailableUpdate implements UpdateInfo {
        INSTANCE;

        private static Optional<PublishedBuild> update() {
            return MarlowCrystal.get().updateCheck().available();
        }

        @Override
        public boolean isUpdateAvailable() {
            return update().isPresent();
        }

        @Override
        public String getDownloadLink() {
            return update().map(PublishedBuild::downloadUrl).orElse(null);
        }

        // Experimental Builds already decides whether betas are offered. Mod Menu would also hide anything below its
        // own Update Channel setting, which defaults to Release.
        @Override
        public UpdateChannel getUpdateChannel() {
            return UpdateChannel.RELEASE;
        }
    }
    //?}
}
//?}
