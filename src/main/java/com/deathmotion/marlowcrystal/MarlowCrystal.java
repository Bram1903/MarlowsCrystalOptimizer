package com.deathmotion.marlowcrystal;

import com.deathmotion.marlowcrystal.listener.DisconnectEventListener;
import com.deathmotion.marlowcrystal.listener.OptOutPacketListener;
import com.deathmotion.marlowcrystal.packet.impl.OptOutAckPacket;
import com.deathmotion.marlowcrystal.packet.impl.OptOutPacket;
import com.deathmotion.marlowcrystal.util.Logger;
import lombok.Getter;
import lombok.Setter;
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
    @Setter
    private boolean optedOut = false;

    public MarlowCrystal() {
        instance = this;
        logger = new Logger();
    }

    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.clientboundPlay().register(OptOutPacket.TYPE, OptOutPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(OptOutAckPacket.TYPE, OptOutAckPacket.STREAM_CODEC);

        ClientPlayConnectionEvents.DISCONNECT.register(new DisconnectEventListener());
        OptOutPacketListener.register();

        logger.info("Mod initialized");
    }
}
