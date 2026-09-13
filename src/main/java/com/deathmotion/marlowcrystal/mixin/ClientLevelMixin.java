package com.deathmotion.marlowcrystal.mixin;

import com.deathmotion.marlowcrystal.crystal.KeptCrystals;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    @Inject(method = "removeEntity(ILnet/minecraft/world/entity/Entity$RemovalReason;)V", at = @At("HEAD"))
    private void marlowcrystal$confirmRemoval(int id, Entity.RemovalReason reason, CallbackInfo ci) {
        KeptCrystals.confirmRemoval(((ClientLevel) (Object) this).getEntity(id));
    }
}
