//? if neoforge {
/*package com.deathmotion.marlowcrystal.loader.neoforge;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import com.deathmotion.marlowcrystal.integration.yacl.SettingsScreen;
import com.deathmotion.marlowcrystal.network.ServerSession;
import com.deathmotion.marlowcrystal.network.ServerboundPacket;
import com.deathmotion.marlowcrystal.network.packet.ChallengePacket;
//? if >=1.20.5 {
import com.deathmotion.marlowcrystal.network.packet.ChallengeResponsePacket;
import com.deathmotion.marlowcrystal.network.packet.OptOutAckPacket;
//?}
import com.deathmotion.marlowcrystal.network.packet.OptOutPacket;
//? if >=1.20.5 {
import com.deathmotion.marlowcrystal.network.packet.VersionPacket;
//?}
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLConfig;
//? if >=1.20.5 {
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
//?} else {
/^import net.neoforged.neoforge.client.ConfigScreenHandler;
^///?}
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
//? if >=1.20.5 {
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
//?} else {
/^import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
^///?}

import java.util.concurrent.CompletableFuture;

@Mod(MarlowCrystal.MOD_ID)
public class MarlowCrystalNeoForge {

    public MarlowCrystalNeoForge(IEventBus modBus, ModContainer container, Dist dist) {
        if (!dist.isClient()) {
            return;
        }

        MarlowCrystal.initialize();

        modBus.addListener(MarlowCrystalNeoForge::registerPayloads);
        NeoForge.EVENT_BUS.addListener(MarlowCrystalNeoForge::onLoggingIn);
        NeoForge.EVENT_BUS.addListener(MarlowCrystalNeoForge::onLoggingOut);

        if (ModList.get().isLoaded(SettingsScreen.YACL_MOD_ID)) {
            registerConfigScreen(container);
        }

        // Turning off NeoForge's own version check in fml.toml turns this one off too.
        if (FMLConfig.getBoolConfigValue(FMLConfig.ConfigValue.VERSION_CHECK)) {
            CompletableFuture.runAsync(MarlowCrystal.get().updateCheck()::run);
        }
    }

    @SuppressWarnings("unused") // the lambda signature is fixed by NeoForge
    private static void registerConfigScreen(ModContainer container) {
        //? if >=1.20.5 {
        container.registerExtensionPoint(IConfigScreenFactory.class, (owner, parent) -> SettingsScreen.create(parent));
        //?} else {
        /^container.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> SettingsScreen.create(parent)));
        ^///?}
    }

    //? if >=1.20.5 {
    @SuppressWarnings("unused") // the lambda signatures are fixed by NeoForge
    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();

        registrar.playToClient(OptOutPacket.TYPE, OptOutPacket.STREAM_CODEC, (payload, context) -> onOptOut());
        registrar.playToClient(ChallengePacket.TYPE, ChallengePacket.STREAM_CODEC, (payload, context) -> onChallenge(payload));

        // Registered so NeoForge can encode them; the server side handlers never run on a client.
        registrar.playToServer(OptOutAckPacket.TYPE, OptOutAckPacket.STREAM_CODEC, (payload, context) -> {});
        registrar.playToServer(VersionPacket.TYPE, VersionPacket.STREAM_CODEC, (payload, context) -> {});
        registrar.playToServer(ChallengeResponsePacket.TYPE, ChallengeResponsePacket.STREAM_CODEC, (payload, context) -> {});
    }
    //?} else {
    /^@SuppressWarnings("unused") // the lambda signatures are fixed by NeoForge
    private static void registerPayloads(RegisterPayloadHandlerEvent event) {
        IPayloadRegistrar registrar = event.registrar(MarlowCrystal.MOD_ID).optional();

        registrar.play(OptOutPacket.ID, buf -> OptOutPacket.INSTANCE, handlers -> handlers.client(
                (OptOutPacket payload, PlayPayloadContext context) -> context.workHandler().execute(MarlowCrystalNeoForge::onOptOut)));
        registrar.play(ChallengePacket.ID, buf -> new ChallengePacket(buf.readInt()), handlers -> handlers.client(
                (ChallengePacket payload, PlayPayloadContext context) -> context.workHandler().execute(() -> onChallenge(payload))));
    }
    ^///?}

    private static ServerSession session() {
        return MarlowCrystal.get().serverSession();
    }

    private static void onOptOut() {
        session().optOutReceived(Minecraft.getInstance(), MarlowCrystalNeoForge::send);
    }

    private static void onChallenge(ChallengePacket payload) {
        session().challengeReceived(payload.challengeId(), MarlowCrystalNeoForge::send);
    }

    private static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        session().joined(Minecraft.getInstance(), MarlowCrystalNeoForge::send);
    }

    private static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        session().disconnected();
    }

    // Through the connection because NeoForge's packet listener drops payloads on channels the server never
    // registered, which a Fabric client sends anyway.
    private static void send(ServerboundPacket packet) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            connection.getConnection().send(new ServerboundCustomPayloadPacket(packet));
        }
    }
}
*///?}
