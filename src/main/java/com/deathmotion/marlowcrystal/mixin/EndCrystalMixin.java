package com.deathmotion.marlowcrystal.mixin;

import com.deathmotion.marlowcrystal.crystal.KeptCrystal;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EndCrystal.class)
public class EndCrystalMixin implements KeptCrystal {

    @Unique
    private boolean marlowcrystal$kept;

    @Unique
    private long marlowcrystal$keptAt;

    @Override
    public void marlowcrystal$keep(long keptAt) {
        marlowcrystal$kept = true;
        marlowcrystal$keptAt = keptAt;
    }

    @Override
    public boolean marlowcrystal$isKept() {
        return marlowcrystal$kept;
    }

    @Override
    public long marlowcrystal$keptAt() {
        return marlowcrystal$keptAt;
    }
}
