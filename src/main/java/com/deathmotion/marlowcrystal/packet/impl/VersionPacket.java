package com.deathmotion.marlowcrystal.packet.impl;

import com.deathmotion.marlowcrystal.packet.ModPackets;
import net.minecraft.network.FriendlyByteBuf;
//? if >=1.20.5 {
import net.minecraft.network.codec.StreamCodec;
//?}
//? if >=1.20.2 {
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?}
//? if <1.20.5 {
/*import net.minecraft.resources.Identifier;
*///?}
import org.jetbrains.annotations.NotNull;

//? if >=1.20.2 {
public record VersionPacket(int major, int minor, int patch, boolean snapshot) implements CustomPacketPayload {
//?} else {
/*public record VersionPacket(int major, int minor, int patch, boolean snapshot) {
*///?}

    //? if >=1.20.5 {
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
    public @NotNull Type<VersionPacket> type() {
        return TYPE;
    }
    //?} else {
    /*public static final Identifier ID = ModPackets.id("version");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(major);
        buf.writeVarInt(minor);
        buf.writeVarInt(patch);
        buf.writeBoolean(snapshot);
    }
    *///?}

    //? if >=1.20.2 <1.20.5 {
    /*@Override
    public @NotNull Identifier id() {
        return ID;
    }
    *///?}
}
