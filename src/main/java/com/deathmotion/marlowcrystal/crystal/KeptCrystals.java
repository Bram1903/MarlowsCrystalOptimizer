package com.deathmotion.marlowcrystal.crystal;

import com.google.common.collect.Iterables;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public final class KeptCrystals {

    // Only reached when no block action followed the hit, so no acknowledgement can tell a rejected hit apart.
    private static final long RELEASE_AFTER = TimeUnit.MILLISECONDS.toNanos(1500);

    private static final List<EndCrystal> kept = new ArrayList<>();

    private static long lastKeptAt;

    private KeptCrystals() {
    }

    public static void keep(EndCrystal crystal, int sequence) {
        long now = System.nanoTime();
        forgetSettled(now - RELEASE_AFTER);
        ((KeptCrystal) crystal).marlowcrystal$keep(now, sequence);
        kept.add(crystal);
        lastKeptAt = now;
    }

    // The server sends a removal while it handles the hit, and an acknowledgement only after every packet before it.
    public static void acknowledged(int sequence) {
        forgetSettled(System.nanoTime() - RELEASE_AFTER);
        kept.removeIf(crystal -> {
            KeptCrystal keptCrystal = (KeptCrystal) crystal;
            if (keptCrystal.marlowcrystal$sequence() >= sequence) {
                return false;
            }

            keptCrystal.marlowcrystal$release();
            return true;
        });
    }

    public static Predicate<? super Entity> hide(Predicate<? super Entity> predicate) {
        long keptSince = System.nanoTime() - RELEASE_AFTER;
        if (!stillKeeping(keptSince)) {
            return predicate;
        }

        return entity -> !isKeptSince(entity, keptSince) && predicate.test(entity);
    }

    public static Iterable<Entity> hide(Iterable<Entity> entities) {
        long keptSince = System.nanoTime() - RELEASE_AFTER;
        if (!stillKeeping(keptSince)) {
            return entities;
        }

        return Iterables.filter(entities, entity -> !isKeptSince(entity, keptSince));
    }

    private static void forgetSettled(long keptSince) {
        kept.removeIf(crystal -> crystal.isRemoved() || !isKeptSince(crystal, keptSince));
    }

    // Cleared once everything has timed out, so a level left behind is not held on to.
    private static boolean stillKeeping(long keptSince) {
        if (kept.isEmpty()) {
            return false;
        }

        if (lastKeptAt - keptSince <= 0) {
            kept.clear();
            return false;
        }

        return true;
    }

    private static boolean isKeptSince(Entity entity, long keptSince) {
        return entity instanceof KeptCrystal crystal
                && crystal.marlowcrystal$isKept()
                && crystal.marlowcrystal$keptAt() - keptSince > 0;
    }
}
