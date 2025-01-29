package com.marlowcrystal.packets;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class OptOutPacket implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation("mco");

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        Unpooled.buffer();
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
