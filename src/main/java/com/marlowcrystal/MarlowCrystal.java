package com.marlowcrystal;

import com.mojang.logging.LogUtils;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;

@Getter
@Environment(EnvType.CLIENT)
public class MarlowCrystal implements ClientModInitializer {

    @Getter
    private static MarlowCrystal instance;

    private final Logger logger = LogUtils.getLogger();

    @Override
    public void onInitializeClient() {
        instance = this;

        logger.info("Mod initialized");
    }
}
