package com.deathmotion.marlowcrystal;

import com.deathmotion.marlowcrystal.cache.OptOutCache;
import com.deathmotion.marlowcrystal.listener.ConnectEventListener;
import com.deathmotion.marlowcrystal.listener.DisconnectEventListener;
import com.deathmotion.marlowcrystal.listener.OptOutPacketListener;
import com.deathmotion.marlowcrystal.packet.impl.OptOutAckPacket;
import com.deathmotion.marlowcrystal.packet.impl.OptOutPacket;
import com.deathmotion.marlowcrystal.packet.impl.VersionPacket;
import com.deathmotion.marlowcrystal.util.Logger;
import com.deathmotion.marlowcrystal.util.VersionUtil;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class MarlowCrystal implements ClientModInitializer {

    public static final String MOD_ID = "marlowcrystal";

    public static final Component PREFIX = Component.literal("[").withStyle(ChatFormatting.GRAY)
            .append(Component.literal("Marlow's Crystal Optimizer").withStyle(ChatFormatting.AQUA))
            .append(Component.literal("] ").withStyle(ChatFormatting.GRAY));

    @Getter
    private static MarlowCrystal instance;

    @Getter
    private static Logger logger;

    @Getter
    private final OptOutCache optOutCache;

    @Getter
    private VersionPacket versionPacket;

    public MarlowCrystal() {
        instance = this;
        logger = new Logger();
        optOutCache = new OptOutCache();
    }

    @Override
    public void onInitializeClient() {
        versionPacket = VersionUtil.createVersionPacket();

        PayloadTypeRegistry.configurationC2S().register(OptOutPacket.TYPE, OptOutPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(OptOutPacket.TYPE, OptOutPacket.STREAM_CODEC);

        PayloadTypeRegistry.playC2S().register(OptOutAckPacket.TYPE, OptOutAckPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(VersionPacket.TYPE, VersionPacket.STREAM_CODEC);

        ClientPlayConnectionEvents.JOIN.register(new ConnectEventListener());
        ClientPlayConnectionEvents.DISCONNECT.register(new DisconnectEventListener());
        OptOutPacketListener.register();

        logger.info("Mod initialized");
    }
}
