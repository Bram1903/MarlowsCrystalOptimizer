package com.deathmotion.marlowcrystal;

import com.deathmotion.marlowcrystal.packets.OptOutPacket;
import com.deathmotion.marlowcrystal.util.Logger;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

@Getter
@Environment(EnvType.CLIENT)
public class MarlowCrystal implements ClientModInitializer {

    @Getter
    private static MarlowCrystal instance;

    private Logger logger;

    @Override
    public void onInitializeClient() {
        instance = this;
        PayloadTypeRegistry.playC2S().register(OptOutPacket.TYPE, OptOutPacket.STREAM_CODEC);

        logger = new Logger();
        logger.info("Mod initialized");
    }
}
