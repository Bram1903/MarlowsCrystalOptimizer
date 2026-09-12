package com.deathmotion.marlowcrystal.mixin;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import com.deathmotion.marlowcrystal.crystal.CrystalBreaker;
import com.deathmotion.marlowcrystal.state.OptOutState;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
//? if >=26.1 {
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
//?} else {
/*import com.deathmotion.marlowcrystal.handler.InteractHandler;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ClientConnectionMixin {

    @Unique
    private OptOutState marlowcrystal$state;

    //? if <26.1 {
    /*@Unique
    private InteractHandler marlowcrystal$handler;
    *///?}

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"))
    private void marlowcrystal$onPacketSend(Packet<?> packet, CallbackInfo ci) {
        //? if >=26.1 {
        if (!(packet instanceof ServerboundAttackPacket(int entityId))) {
            return;
        }

        if (marlowcrystal$optedOut()) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        if (client.level == null) {
            return;
        }

        if (client.level.getEntity(entityId) instanceof EndCrystal crystal) {
            CrystalBreaker.breakIfPossible(client, crystal);
        }
        //?} else {
        /*if (!(packet instanceof ServerboundInteractPacket interactPacket)) {
            return;
        }

        if (marlowcrystal$optedOut()) {
            return;
        }

        if (marlowcrystal$handler == null) {
            marlowcrystal$handler = new InteractHandler(Minecraft.getInstance());
        }

        interactPacket.dispatch(marlowcrystal$handler);
        *///?}
    }

    @Unique
    private boolean marlowcrystal$optedOut() {
        if (marlowcrystal$state == null) {
            marlowcrystal$state = MarlowCrystal.getInstance().getOptOutState();
        }

        return marlowcrystal$state.isOptedOut();
    }
}
