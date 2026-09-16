package com.deathmotion.marlowcrystal.mixin;

import com.deathmotion.marlowcrystal.config.ModConfig;
import com.deathmotion.marlowcrystal.crystal.KeptCrystals;
import com.deathmotion.marlowcrystal.crystal.SequencedLevel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin implements SequencedLevel {

    @Shadow
    @Final
    private BlockStatePredictionHandler blockStatePredictionHandler;

    @Override
    public int marlowcrystal$blockSequence() {
        return blockStatePredictionHandler.currentSequence();
    }

    // Hidden instead of removed, so a crystal the server did not break comes back once it is released.
    @Inject(method = "entitiesForRendering", at = @At("RETURN"), cancellable = true)
    private void marlowcrystal$hideKeptCrystals(CallbackInfoReturnable<Iterable<Entity>> cir) {
        if (!ModConfig.getInstance().isKeepRender()) {
            cir.setReturnValue(KeptCrystals.hide(cir.getReturnValue()));
        }
    }

    @Inject(method = "handleBlockChangedAck", at = @At("HEAD"))
    private void marlowcrystal$releaseKeptCrystals(int sequence, CallbackInfo ci) {
        KeptCrystals.acknowledged(sequence);
    }
}
