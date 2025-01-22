package com.marlowcrystal.packets;

import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class OptOutPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OptOutPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.parse("mco"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OptOutPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(RegistryFriendlyByteBuf buffer, OptOutPacket optOutPacket) {
            Unpooled.buffer();
        }

        @Override
        public OptOutPacket decode(RegistryFriendlyByteBuf buffer) {
            return new OptOutPacket();
        }
    };

    @Override
    public @NotNull Type<OptOutPacket> type() {
        return TYPE;
    }
}
