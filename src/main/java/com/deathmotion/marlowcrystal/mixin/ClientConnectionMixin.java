package com.deathmotion.marlowcrystal.mixin;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import com.deathmotion.marlowcrystal.cache.OptOutCache;
import com.deathmotion.marlowcrystal.handler.InteractHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ClientConnectionMixin {

    @Unique
    private final OptOutCache optOutCache;

    @Unique
    private InteractHandler cachedHandler;

    public ClientConnectionMixin() {
        optOutCache = MarlowCrystal.getInstance().getOptOutCache();
    }

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"))
    private void onPacketSend(Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof ServerboundInteractPacket interactionPacket) {
            if (optOutCache.isOptedOut()) return;

            if (cachedHandler == null) {
                cachedHandler = new InteractHandler(Minecraft.getInstance());
            }
            interactionPacket.dispatch(cachedHandler);
        }
    }
}
