package com.deathmotion.marlowcrystal;

import com.deathmotion.marlowcrystal.crystal.KeptCrystals;
import com.deathmotion.marlowcrystal.packet.impl.VersionPacket;
import com.deathmotion.marlowcrystal.state.OptOutState;
import com.deathmotion.marlowcrystal.util.Logger;
import com.deathmotion.marlowcrystal.versioning.MCOVersion;
import com.deathmotion.marlowcrystal.versioning.MCOVersions;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public final class MarlowCrystal {

    public static final String MOD_ID = "marlowcrystal";

    public static final Component PREFIX = Component.literal("[").withStyle(ChatFormatting.GRAY)
            .append(Component.literal("Marlow's Crystal Optimizer").withStyle(ChatFormatting.AQUA))
            .append(Component.literal("] ").withStyle(ChatFormatting.GRAY));

    private static MarlowCrystal instance;

    private final OptOutState optOutState = new OptOutState();

    private final VersionPacket versionPacket = VersionPacket.current();

    private MarlowCrystal() {
    }

    public static MarlowCrystal getInstance() {
        return instance;
    }

    public static void initialize() {
        instance = new MarlowCrystal();

        MCOVersion version = MCOVersions.CURRENT;
        String commit = version.commit();
        new Logger().info("Mod initialized, version " + version.toDisplayString()
                + " for Minecraft " + MCOVersions.MINECRAFT_RANGE
                + " built " + MCOVersions.BUILD_TIMESTAMP
                + (commit != null ? " (" + commit + (MCOVersions.DIRTY ? ", dirty" : "") + ")" : ""));
    }

    public OptOutState getOptOutState() {
        return optOutState;
    }

    public VersionPacket getVersionPacket() {
        return versionPacket;
    }

    public void onDisconnect() {
        optOutState.reset();
        KeptCrystals.reset();
    }
}
