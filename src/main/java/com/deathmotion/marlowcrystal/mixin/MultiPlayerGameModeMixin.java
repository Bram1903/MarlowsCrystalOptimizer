package com.deathmotion.marlowcrystal.mixin;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import com.deathmotion.marlowcrystal.crystal.CrystalBreaker;
import com.deathmotion.marlowcrystal.state.OptOutState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    @Unique
    private OptOutState marlowcrystal$state;

    @Inject(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;attack(Lnet/minecraft/world/entity/Entity;)V",
                    shift = At.Shift.AFTER))
    private void marlowcrystal$afterAttack(Player player, Entity target, CallbackInfo ci) {
        if (target instanceof EndCrystal crystal && !marlowcrystal$optedOut()) {
            CrystalBreaker.breakIfPossible(Minecraft.getInstance(), crystal);
        }
    }

    @Unique
    private boolean marlowcrystal$optedOut() {
        if (marlowcrystal$state == null) {
            marlowcrystal$state = MarlowCrystal.getInstance().getOptOutState();
        }

        return marlowcrystal$state.isOptedOut();
    }
}
