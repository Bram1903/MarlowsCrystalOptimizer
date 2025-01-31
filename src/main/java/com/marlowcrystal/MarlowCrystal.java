package com.marlowcrystal;

import com.marlowcrystal.util.Logger;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Getter
@Environment(EnvType.CLIENT)
public class MarlowCrystal implements ClientModInitializer {

    @Getter
    private static MarlowCrystal instance;

    private Logger logger;

    @Override
    public void onInitializeClient() {
        instance = this;

        logger = new Logger();
        logger.info("Mod initialized");
    }
}
