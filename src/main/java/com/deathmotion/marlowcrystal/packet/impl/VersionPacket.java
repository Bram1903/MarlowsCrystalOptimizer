package com.deathmotion.marlowcrystal.packet.impl;

import com.deathmotion.marlowcrystal.packet.ModPackets;
import com.deathmotion.marlowcrystal.versioning.MCOVersion;
import com.deathmotion.marlowcrystal.versioning.MCOVersions;
import com.deathmotion.marlowcrystal.versioning.ModLoader;
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
//? if >=1.20.2 {
import org.jetbrains.annotations.NotNull;
//?}

//? if >=1.20.2 {
public record VersionPacket(int major, int minor, int patch, boolean snapshot, ModLoader loader, String commit,
                            boolean dirty, long buildTimestamp) implements CustomPacketPayload {
//?} else {
/*public record VersionPacket(int major, int minor, int patch, boolean snapshot, ModLoader loader, String commit,
                            boolean dirty, long buildTimestamp) {
*///?}

    private static final int COMMIT_MAX_LENGTH = 40;

    @SuppressWarnings("ConstantValue") // MCOVersions.COMMIT is generated and is null when git is unavailable
    public static VersionPacket current() {
        MCOVersion version = MCOVersions.CURRENT;
        String commit = MCOVersions.COMMIT;
        return new VersionPacket(version.major(), version.minor(), version.patch(), version.snapshot(), MCOVersions.LOADER,
                commit != null ? commit : "", MCOVersions.DIRTY, MCOVersions.BUILD_TIMESTAMP.toEpochMilli());
    }

    private static void encode(FriendlyByteBuf buf, VersionPacket packet) {
        buf.writeVarInt(packet.major());
        buf.writeVarInt(packet.minor());
        buf.writeVarInt(packet.patch());
        buf.writeBoolean(packet.snapshot());
        buf.writeVarInt(packet.loader().networkId());
        buf.writeUtf(packet.commit(), COMMIT_MAX_LENGTH);
        buf.writeBoolean(packet.dirty());
        buf.writeLong(packet.buildTimestamp());
    }

    //? if >=1.20.5 {
    public static final Type<VersionPacket> TYPE = new Type<>(ModPackets.id("version"));

    public static final StreamCodec<FriendlyByteBuf, VersionPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(@NotNull FriendlyByteBuf buf, @NotNull VersionPacket packet) {
            VersionPacket.encode(buf, packet);
        }

        @Override
        public @NotNull VersionPacket decode(@NotNull FriendlyByteBuf buf) {
            int major = buf.readVarInt();
            int minor = buf.readVarInt();
            int patch = buf.readVarInt();
            boolean snapshot = buf.readBoolean();
            if (!buf.isReadable()) {
                return new VersionPacket(major, minor, patch, snapshot, null, "", false, 0L);
            }

            VersionPacket packet = new VersionPacket(major, minor, patch, snapshot, ModLoader.byNetworkId(buf.readVarInt()),
                    buf.readUtf(COMMIT_MAX_LENGTH), buf.readBoolean(), buf.readLong());
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
