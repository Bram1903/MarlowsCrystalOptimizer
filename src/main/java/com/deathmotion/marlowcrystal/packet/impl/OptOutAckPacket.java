package com.deathmotion.marlowcrystal.packet.impl;

import com.deathmotion.marlowcrystal.packet.ModPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public final class OptOutAckPacket implements CustomPacketPayload {
    public static final ResourceLocation ID = ModPackets.id("opt_out_ack");

    public static final OptOutAckPacket INSTANCE = new OptOutAckPacket();

    private OptOutAckPacket() {
    }

    @Override
    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
