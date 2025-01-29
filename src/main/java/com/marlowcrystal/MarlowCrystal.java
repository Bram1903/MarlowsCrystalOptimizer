package com.marlowcrystal;

import com.marlowcrystal.packets.OptOutPacket;
import com.marlowcrystal.util.Logger;
import com.mojang.logging.LogUtils;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.slf4j.Logger;

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
