package com.deathmotion.marlowcrystal.integration.modmenu;

//? if >=1.20.5 {
import com.deathmotion.marlowcrystal.update.UpdateResult;
import com.deathmotion.marlowcrystal.update.UpdateService;
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

public class ModMenuIntegration implements ModMenuApi {

    //? if >=1.20.2 {
    private static final String YACL_MOD_ID = "yet_another_config_lib_v3";

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (FabricLoader.getInstance().isModLoaded(YACL_MOD_ID)) {
            return YaclScreenFactory::create;
        }
        return ModMenuApi.super.getModConfigScreenFactory();
    }
    //?}

    //? if >=1.20.5 {
    @Override
    public UpdateChecker getUpdateChecker() {
        return () -> {
            UpdateService.getInstance().check();
            return LatestUpdateInfo.INSTANCE;
        };
    }

    private enum LatestUpdateInfo implements UpdateInfo {
        INSTANCE;

        private static UpdateResult latest() {
            return UpdateService.getInstance().latest();
        }

        @Override
        public boolean isUpdateAvailable() {
            return latest().available();
        }

        @Override
        public String getDownloadLink() {
            return latest().downloadUrl();
        }

        @Override
        public UpdateChannel getUpdateChannel() {
            return switch (latest().channel()) {
                case RELEASE -> UpdateChannel.RELEASE;
                case BETA -> UpdateChannel.BETA;
                case ALPHA -> UpdateChannel.ALPHA;
            };
        }
    }
    //?}
}
