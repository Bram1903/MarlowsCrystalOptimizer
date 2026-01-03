package com.deathmotion.marlowcrystal.packet.impl;

import com.deathmotion.marlowcrystal.packet.ModPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record VersionPacket(int major, int minor, int patch, boolean snapshot) implements CustomPacketPayload {

    public static final Type<VersionPacket> TYPE = new Type<>(ModPackets.id("version"));

    public static final StreamCodec<FriendlyByteBuf, VersionPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buf, VersionPacket v) {
            buf.writeVarInt(v.major());
            buf.writeVarInt(v.minor());
            buf.writeVarInt(v.patch());
            buf.writeBoolean(v.snapshot());
        }

        @Override
        public VersionPacket decode(FriendlyByteBuf buf) {
            return new VersionPacket(
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readBoolean()
            );
        }
    };

    @Override
    public @NonNull Type<VersionPacket> type() {
        return TYPE;
    }
}


