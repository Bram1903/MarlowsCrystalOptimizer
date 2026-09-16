package com.deathmotion.marlowcrystal.crystal;

import com.google.common.collect.Iterables;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public final class KeptCrystals {

    // The server never acknowledges an attack, so a hit it rejected can only be undone by timing out.
    private static final long RELEASE_AFTER = TimeUnit.MILLISECONDS.toNanos(1500);

    private static boolean kept;

    private static long lastKeptAt;

    private KeptCrystals() {
    }

    public static void keep(EndCrystal crystal) {
        long now = System.nanoTime();
        ((KeptCrystal) crystal).marlowcrystal$keep(now);
        kept = true;
        lastKeptAt = now;
    }

    public static Predicate<? super Entity> hide(Predicate<? super Entity> predicate) {
        long keptSince = System.nanoTime() - RELEASE_AFTER;
        if (!anyKeptSince(keptSince)) {
            return predicate;
        }

        return entity -> !isKeptSince(entity, keptSince) && predicate.test(entity);
    }

    public static Iterable<Entity> hide(Iterable<Entity> entities) {
        long keptSince = System.nanoTime() - RELEASE_AFTER;
        if (!anyKeptSince(keptSince)) {
            return entities;
        }

        return Iterables.filter(entities, entity -> !isKeptSince(entity, keptSince));
    }

    private static boolean anyKeptSince(long keptSince) {
        return kept && lastKeptAt - keptSince > 0;
    }

    private static boolean isKeptSince(Entity entity, long keptSince) {
        return entity instanceof KeptCrystal crystal
                && crystal.marlowcrystal$isKept()
                && crystal.marlowcrystal$keptAt() - keptSince > 0;
    }
}
