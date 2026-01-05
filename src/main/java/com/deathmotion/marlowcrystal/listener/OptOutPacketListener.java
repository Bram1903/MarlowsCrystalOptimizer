package com.deathmotion.marlowcrystal.listener;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import com.deathmotion.marlowcrystal.cache.OptOutCache;
import com.deathmotion.marlowcrystal.packet.impl.OptOutAckPacket;
import com.deathmotion.marlowcrystal.packet.impl.OptOutPacket;
import com.deathmotion.marlowcrystal.util.ConnectionUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class OptOutPacketListener {

    private OptOutPacketListener() {
    }

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

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(OptOutPacket.TYPE, (payload, context) -> {
            Minecraft client = context.client();
            OptOutCache cache = MarlowCrystal.getInstance().getOptOutCache();

            String key = ConnectionUtil.currentServerKey(client);

            if (key != null) {
                cache.markOptedOut(key);
            } else {
                cache.setOptedOut(true);
            }

            if (!cache.hasNotified(key)) {
                CompletableFuture.delayedExecutor(2, TimeUnit.SECONDS, Util.backgroundExecutor()).execute(() -> client.execute(() -> {
                    if (client.player == null) return;
                    if (cache.hasNotified(key)) return;

                    cache.markNotified(key);
                    client.player.displayClientMessage(optimizerDisabledMessage(), false);
                }));
            }

            ClientPlayNetworking.send(OptOutAckPacket.INSTANCE);
        });
    }
}
