package com.deathmotion.marlowcrystal.packet.impl;

import com.deathmotion.marlowcrystal.packet.ModPackets;
import com.deathmotion.marlowcrystal.versioning.MCOVersion;
import com.deathmotion.marlowcrystal.versioning.MCOVersions;
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
public record VersionPacket(int major, int minor, int patch, boolean snapshot, int format, String commit, boolean dirty,
                            String minecraftRange, long buildTimestamp) implements CustomPacketPayload {
//?} else {
/*public record VersionPacket(int major, int minor, int patch, boolean snapshot, int format, String commit, boolean dirty,
                            String minecraftRange, long buildTimestamp) {
*///?}

    public static final int FORMAT = 1;

    private static final int COMMIT_MAX_LENGTH = 40;

    private static final int MINECRAFT_RANGE_MAX_LENGTH = 64;

    public static VersionPacket current() {
        MCOVersion version = MCOVersions.CURRENT;
        String commit = MCOVersions.COMMIT;
        return new VersionPacket(version.major(), version.minor(), version.patch(), version.snapshot(), FORMAT,
                commit != null ? commit : "", MCOVersions.DIRTY, MCOVersions.MINECRAFT_RANGE,
                MCOVersions.BUILD_TIMESTAMP.toEpochMilli());
    }

    private static void encode(FriendlyByteBuf buf, VersionPacket packet) {
        buf.writeVarInt(packet.major());
        buf.writeVarInt(packet.minor());
        buf.writeVarInt(packet.patch());
        buf.writeBoolean(packet.snapshot());
        buf.writeVarInt(packet.format());
        buf.writeUtf(packet.commit(), COMMIT_MAX_LENGTH);
        buf.writeBoolean(packet.dirty());
        buf.writeUtf(packet.minecraftRange(), MINECRAFT_RANGE_MAX_LENGTH);
        buf.writeLong(packet.buildTimestamp());
    }

    //? if >=1.20.5 {
    public static final Type<VersionPacket> TYPE = new Type<>(ModPackets.id("version"));

    public static final StreamCodec<FriendlyByteBuf, VersionPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buf, VersionPacket packet) {
            VersionPacket.encode(buf, packet);
        }

        @Override
        public VersionPacket decode(FriendlyByteBuf buf) {
            int major = buf.readVarInt();
            int minor = buf.readVarInt();
            int patch = buf.readVarInt();
            boolean snapshot = buf.readBoolean();
            if (!buf.isReadable()) {
                return new VersionPacket(major, minor, patch, snapshot, 0, "", false, "", 0L);
            }

            VersionPacket packet = new VersionPacket(major, minor, patch, snapshot, buf.readVarInt(),
                    buf.readUtf(COMMIT_MAX_LENGTH), buf.readBoolean(), buf.readUtf(MINECRAFT_RANGE_MAX_LENGTH),
                    buf.readLong());
            buf.skipBytes(buf.readableBytes());
            return packet;
        }
    };

    @Override
    public @NotNull Type<VersionPacket> type() {
        return TYPE;
    }
    //?} else {
    /*public static final Identifier ID = ModPackets.id("version");

    public void write(FriendlyByteBuf buf) {
        encode(buf, this);
    }
    *///?}

    //? if >=1.20.2 <1.20.5 {
    /*@Override
    public @NotNull Identifier id() {
        return ID;
    }
    *///?}
}
