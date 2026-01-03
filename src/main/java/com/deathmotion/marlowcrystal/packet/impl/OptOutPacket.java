package com.deathmotion.marlowcrystal.packet.impl;

import com.deathmotion.marlowcrystal.packet.ModPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public final class OptOutPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<@NotNull OptOutPacket> TYPE = new CustomPacketPayload.Type<>(ModPackets.id("opt_out"));

    public static final OptOutPacket INSTANCE = new OptOutPacket();

    public static final StreamCodec<@NotNull FriendlyByteBuf, @NotNull OptOutPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buffer, OptOutPacket value) {
            // No data to encode
        }

        @Override
        public @NotNull OptOutPacket decode(FriendlyByteBuf buffer) {
            return INSTANCE;
        }
    };

    private OptOutPacket() {
    }

    @Override
    public @NotNull Type<@NotNull OptOutPacket> type() {
        return TYPE;
    }
}
