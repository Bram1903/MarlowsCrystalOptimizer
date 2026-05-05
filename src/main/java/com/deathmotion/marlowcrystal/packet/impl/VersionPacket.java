package com.deathmotion.marlowcrystal.packet.impl;

import com.deathmotion.marlowcrystal.packet.ModPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public final class VersionPacket {
    public static final ResourceLocation ID = ModPackets.id("version");

    private final int major;
    private final int minor;
    private final int patch;
    private final boolean snapshot;

    public VersionPacket(int major, int minor, int patch, boolean snapshot) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.snapshot = snapshot;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(major);
        buf.writeVarInt(minor);
        buf.writeVarInt(patch);
        buf.writeBoolean(snapshot);
    }
}
