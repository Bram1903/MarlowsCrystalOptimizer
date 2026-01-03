package com.deathmotion.marlowcrystal.listener;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import com.deathmotion.marlowcrystal.packet.impl.OptOutAckPacket;
import com.deathmotion.marlowcrystal.packet.impl.OptOutPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class OptOutPacketListener {

    private static final Set<String> NOTIFIED_SERVER_KEYS = ConcurrentHashMap.newKeySet();

    private static Component optimizerDisabledMessage() {
        Component hover = Component.empty()
                .append(Component.literal("Why is this disabled?\n").withStyle(ChatFormatting.AQUA))
                .append(Component.literal("• This server has requested Marlow's Crystal Optimizer to be disabled.\n").withStyle(ChatFormatting.GRAY))
                .append(Component.literal("• This may be to enforce server rules or avoid compatibility issues.\n").withStyle(ChatFormatting.GRAY))
                .append(Component.literal("\nThis only applies while you are connected to this server.").withStyle(ChatFormatting.DARK_GRAY));

        Style hoverStyle = Style.EMPTY.withHoverEvent(new HoverEvent.ShowText(hover));
        Component message = Component.literal("Optimizer disabled on this server.")
                .withStyle(hoverStyle.withColor(ChatFormatting.RED));

        return MarlowCrystal.PREFIX.copy()
                .withStyle(hoverStyle)
                .append(message);
    }

    private static @Nullable String currentServerKey(Minecraft client) {
        if (client.getCurrentServer() == null) {
            return null;
        }

        MarlowCrystal.getLogger().info("Current server: " + client.getCurrentServer().ip);
        return client.getCurrentServer().ip;
    }

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(OptOutPacket.TYPE, (payload, context) -> {
            Minecraft client = context.client();

            String key = currentServerKey(client);
            boolean shouldNotify = (key == null) || NOTIFIED_SERVER_KEYS.add(key);

            if (shouldNotify) {
                CompletableFuture
                        .delayedExecutor(2, TimeUnit.SECONDS, Util.backgroundExecutor())
                        .execute(() -> client.execute(() -> {
                            if (client.player != null) {
                                client.player.displayClientMessage(optimizerDisabledMessage(), false);
                            }
                        }));
            }

            MarlowCrystal.getInstance().setOptedOut(true);
            ClientPlayNetworking.send(OptOutAckPacket.INSTANCE);
        });
    }
}
