//? if fabric {
package com.deathmotion.marlowcrystal.loader.fabric;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import com.deathmotion.marlowcrystal.network.ServerSession;
//? if <1.20.5 {
/*import com.deathmotion.marlowcrystal.network.ServerboundPacket;
*///?}
import com.deathmotion.marlowcrystal.network.packet.ChallengePacket;
//? if >=1.20.5 {
import com.deathmotion.marlowcrystal.network.packet.ChallengeResponsePacket;
import com.deathmotion.marlowcrystal.network.packet.OptOutAckPacket;
//?}
import com.deathmotion.marlowcrystal.network.packet.OptOutPacket;
//? if >=1.20.5 {
import com.deathmotion.marlowcrystal.network.packet.VersionPacket;
//?}
//? if <1.20.5 {
/*import io.netty.buffer.Unpooled;
*///?}
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
//? if >=1.20.2 {
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
//?}
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
//? if >=1.20.5 {
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
//?}
//? if <1.20.5 {
/*import net.minecraft.network.FriendlyByteBuf;
*///?}

@Environment(EnvType.CLIENT)
public class MarlowCrystalFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MarlowCrystal.initialize();

        //? if >=1.20.5 {
        registerPayloads();
        //?}
        registerListeners(MarlowCrystal.get().serverSession());
    }

    //? if >=1.20.5 {
    private static void registerPayloads() {
        PayloadTypeRegistry.clientboundConfiguration().register(OptOutPacket.TYPE, OptOutPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(OptOutPacket.TYPE, OptOutPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ChallengePacket.TYPE, ChallengePacket.STREAM_CODEC);

        PayloadTypeRegistry.serverboundPlay().register(OptOutAckPacket.TYPE, OptOutAckPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(VersionPacket.TYPE, VersionPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ChallengeResponsePacket.TYPE, ChallengeResponsePacket.STREAM_CODEC);
    }
    //?}

    @SuppressWarnings("unused") // the lambda signatures are fixed by Fabric
    private static void registerListeners(ServerSession session) {
        //? if >=1.20.5 {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> session.joined(client, sender::sendPacket));
        //?} else {
        /*ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> session.joined(client, MarlowCrystalFabric::send));
        *///?}
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> session.disconnected());
        //? if >=1.20.2 {
        ClientConfigurationConnectionEvents.DISCONNECT.register((handler, client) -> session.disconnected());
        //?}

        //? if >=1.20.5 {
        ClientPlayNetworking.registerGlobalReceiver(OptOutPacket.TYPE, (payload, context) ->
                session.optOutReceived(context.client(), ClientPlayNetworking::send));
        ClientPlayNetworking.registerGlobalReceiver(ChallengePacket.TYPE, (payload, context) ->
                session.challengeReceived(payload.challengeId(), ClientPlayNetworking::send));
        //?} else {
        /*ClientPlayNetworking.registerGlobalReceiver(OptOutPacket.ID, (client, handler, buf, sender) ->
                session.optOutReceived(client, MarlowCrystalFabric::send));
        ClientPlayNetworking.registerGlobalReceiver(ChallengePacket.ID, (client, handler, buf, sender) ->
                session.challengeReceived(buf.readInt(), MarlowCrystalFabric::send));
        *///?}
    }

    //? if <1.20.5 {
    /*private static void send(ServerboundPacket packet) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packet.write(buf);
        ClientPlayNetworking.send(packet.id(), buf);
    }
    *///?}
}
//?}
