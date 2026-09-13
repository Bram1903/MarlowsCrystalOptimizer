package com.deathmotion.marlowcrystal.mixin;

import com.deathmotion.marlowcrystal.crystal.KeptCrystals;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Predicate;

@Mixin(Level.class)
public abstract class LevelMixin {

    @ModifyVariable(
            method = "getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
            at = @At("HEAD"),
            argsOnly = true)
    private Predicate<? super Entity> marlowcrystal$hideKeptCrystals(Predicate<? super Entity> predicate) {
        if (!((Level) (Object) this).isClientSide()) {
            return predicate;
        }

        return KeptCrystals.hide(predicate);
    }
}
