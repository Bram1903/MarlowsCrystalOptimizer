package com.deathmotion.marlowcrystal.mixin;

import com.deathmotion.marlowcrystal.config.ModConfig;
import com.deathmotion.marlowcrystal.crystal.KeptCrystals;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    // Hidden instead of removed, so a crystal the server did not break comes back once it is released.
    @Inject(method = "entitiesForRendering", at = @At("RETURN"), cancellable = true)
    private void marlowcrystal$hideKeptCrystals(CallbackInfoReturnable<Iterable<Entity>> cir) {
        if (!ModConfig.getInstance().isKeepRender()) {
            cir.setReturnValue(KeptCrystals.hide(cir.getReturnValue()));
        }
    }
}
