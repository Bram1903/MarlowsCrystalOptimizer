package com.deathmotion.marlowcrystal.mixin;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    // The ticks Minecraft.tick ends with a ServerboundClientTickEndPacket from 1.21.2, the ones with a level and no pause
    @Inject(method = "tick", at = @At("HEAD"))
    private void marlowcrystal$countTick(CallbackInfo ci) {
        Minecraft client = (Minecraft) (Object) this;
        if (client.level != null && !client.isPaused()) {
            MarlowCrystal.get().keptCrystals().ticked();
        }
    }

    @Inject(method = "startAttack", at = @At("HEAD"))
    private void marlowcrystal$aimPastKeptCrystal(CallbackInfoReturnable<Boolean> cir) {
        MarlowCrystal mod = MarlowCrystal.get();
        if (!mod.serverSession().isOptedOut()) {
            mod.pastKeptCrystal().attackStarted((Minecraft) (Object) this);
        }
    }

    @Inject(method = "startAttack", at = @At("RETURN"))
    private void marlowcrystal$restoreAttackCrosshair(CallbackInfoReturnable<Boolean> cir) {
        MarlowCrystal.get().pastKeptCrystal().attackEnded((Minecraft) (Object) this);
    }

    @Inject(method = "startUseItem", at = @At("HEAD"))
    private void marlowcrystal$placePastKeptCrystal(CallbackInfo ci) {
        MarlowCrystal mod = MarlowCrystal.get();
        if (!mod.serverSession().isOptedOut()) {
            mod.pastKeptCrystal().useStarted((Minecraft) (Object) this);
        }
    }

    // The stack each hand tries is the one ItemStack local on every version, stored before the hit result is read.
    @ModifyVariable(method = "startUseItem", at = @At("STORE"), ordinal = 0)
    private ItemStack marlowcrystal$aimForHand(ItemStack stack) {
        MarlowCrystal.get().pastKeptCrystal().handTried((Minecraft) (Object) this, stack);
        return stack;
    }

    @Inject(method = "startUseItem", at = @At("RETURN"))
    private void marlowcrystal$restoreUseCrosshair(CallbackInfo ci) {
        MarlowCrystal.get().pastKeptCrystal().useEnded((Minecraft) (Object) this);
    }
}
