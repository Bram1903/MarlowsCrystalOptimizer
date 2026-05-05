package com.deathmotion.marlowcrystal.packet.impl;

import com.deathmotion.marlowcrystal.packet.ModPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public final class OptOutPacket implements CustomPacketPayload {
    public static final ResourceLocation ID = ModPackets.id("opt_out");

    public static final OptOutPacket INSTANCE = new OptOutPacket();

    private OptOutPacket() {
    }

    @Override
    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
