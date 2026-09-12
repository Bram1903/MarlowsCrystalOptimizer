package com.deathmotion.marlowcrystal;

import com.deathmotion.marlowcrystal.listener.ChallengePacketListener;
import com.deathmotion.marlowcrystal.listener.ConnectEventListener;
import com.deathmotion.marlowcrystal.listener.DisconnectEventListener;
import com.deathmotion.marlowcrystal.listener.OptOutPacketListener;
//? if >=1.20.5 {
import com.deathmotion.marlowcrystal.packet.impl.ChallengePacket;
import com.deathmotion.marlowcrystal.packet.impl.ChallengeResponsePacket;
import com.deathmotion.marlowcrystal.packet.impl.OptOutAckPacket;
import com.deathmotion.marlowcrystal.packet.impl.OptOutPacket;
//?}
import com.deathmotion.marlowcrystal.packet.impl.VersionPacket;
import com.deathmotion.marlowcrystal.state.OptOutState;
import com.deathmotion.marlowcrystal.util.Logger;
import com.deathmotion.marlowcrystal.versioning.MCOVersion;
import com.deathmotion.marlowcrystal.versioning.MCOVersions;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
//? if >=1.20.5 {
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
//?}
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class MarlowCrystal implements ClientModInitializer {

    public static final String MOD_ID = "marlowcrystal";

    public static final Component PREFIX = Component.literal("[").withStyle(ChatFormatting.GRAY)
            .append(Component.literal("Marlow's Crystal Optimizer").withStyle(ChatFormatting.AQUA))
            .append(Component.literal("] ").withStyle(ChatFormatting.GRAY));

    private static MarlowCrystal instance;

    private static Logger logger;

    private final OptOutState optOutState;

    private VersionPacket versionPacket;

    public MarlowCrystal() {
        instance = this;
        logger = new Logger();
        optOutState = new OptOutState();
    }

    public static MarlowCrystal getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return logger;
    }

    public OptOutState getOptOutState() {
        return optOutState;
    }

    public VersionPacket getVersionPacket() {
        return versionPacket;
    }

    @Override
    public void onInitializeClient() {
        MCOVersion version = MCOVersions.CURRENT;
        versionPacket = new VersionPacket(version.major(), version.minor(), version.patch(), version.snapshot());

        //? if >=1.20.5 {
        PayloadTypeRegistry.clientboundConfiguration().register(OptOutPacket.TYPE, OptOutPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(OptOutPacket.TYPE, OptOutPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ChallengePacket.TYPE, ChallengePacket.STREAM_CODEC);

        PayloadTypeRegistry.serverboundPlay().register(OptOutAckPacket.TYPE, OptOutAckPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(VersionPacket.TYPE, VersionPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ChallengeResponsePacket.TYPE, ChallengeResponsePacket.STREAM_CODEC);
        //?}

        ClientPlayConnectionEvents.JOIN.register(new ConnectEventListener());
        ClientPlayConnectionEvents.DISCONNECT.register(new DisconnectEventListener());
        OptOutPacketListener.register();
        ChallengePacketListener.register();

        String commit = version.commit();
        logger.info("Mod initialized, version " + version.toDisplayString()
                + " for Minecraft " + MCOVersions.MINECRAFT_RANGE
                + (commit != null ? " (" + commit + (MCOVersions.DIRTY ? ", dirty" : "") + ")" : ""));
    }
}
