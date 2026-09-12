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
public record VersionPacket(int major, int minor, int patch, boolean snapshot, String commit, boolean dirty,
                            String minecraftMin, String minecraftMax, long buildTimestamp) implements CustomPacketPayload {
//?} else {
/*public record VersionPacket(int major, int minor, int patch, boolean snapshot, String commit, boolean dirty,
                            String minecraftMin, String minecraftMax, long buildTimestamp) {
*///?}

    private static final int COMMIT_MAX_LENGTH = 40;

    private static final int MINECRAFT_VERSION_MAX_LENGTH = 32;

    public static VersionPacket current() {
        MCOVersion version = MCOVersions.CURRENT;
        String commit = MCOVersions.COMMIT;
        String minecraftMax = MCOVersions.MINECRAFT_MAX;
        return new VersionPacket(version.major(), version.minor(), version.patch(), version.snapshot(),
                commit != null ? commit : "", MCOVersions.DIRTY, MCOVersions.MINECRAFT_MIN,
                minecraftMax != null ? minecraftMax : "", MCOVersions.BUILD_TIMESTAMP.toEpochMilli());
    }

    private static void encode(FriendlyByteBuf buf, VersionPacket packet) {
        buf.writeVarInt(packet.major());
        buf.writeVarInt(packet.minor());
        buf.writeVarInt(packet.patch());
        buf.writeBoolean(packet.snapshot());
        buf.writeUtf(packet.commit(), COMMIT_MAX_LENGTH);
        buf.writeBoolean(packet.dirty());
        buf.writeUtf(packet.minecraftMin(), MINECRAFT_VERSION_MAX_LENGTH);
        buf.writeUtf(packet.minecraftMax(), MINECRAFT_VERSION_MAX_LENGTH);
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
                return new VersionPacket(major, minor, patch, snapshot, "", false, "", "", 0L);
            }

            VersionPacket packet = new VersionPacket(major, minor, patch, snapshot, buf.readUtf(COMMIT_MAX_LENGTH),
                    buf.readBoolean(), buf.readUtf(MINECRAFT_VERSION_MAX_LENGTH),
                    buf.readUtf(MINECRAFT_VERSION_MAX_LENGTH), buf.readLong());
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
