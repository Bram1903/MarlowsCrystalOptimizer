package com.deathmotion.marlowcrystal.crystal;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public final class KeptCrystals {

    private static final long FIRST_RELEASE = TimeUnit.SECONDS.toNanos(1);

    private static final long SHORTEST_RELEASE = TimeUnit.MILLISECONDS.toNanos(500);

    private static final long LONGEST_RELEASE = TimeUnit.SECONDS.toNanos(5);

    private static final long[] confirmations = new long[16];

    private static int next;

    private static int filled;

    private static long releaseAfter = FIRST_RELEASE;

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

    @SuppressWarnings("MathClampMigration") // Math.clamp is Java 21+, the 1.19 and 1.20.4 lines target Java 17
    public static void confirmRemoval(Entity entity) {
        if (!(entity instanceof KeptCrystal crystal) || !crystal.marlowcrystal$isKept()) {
            return;
        }

        long confirmation = System.nanoTime() - crystal.marlowcrystal$keptAt();
        if (confirmation > LONGEST_RELEASE) {
            return;
        }

        confirmations[next] = confirmation;
        next = (next + 1) % confirmations.length;
        filled = Math.min(filled + 1, confirmations.length);

        long slowest = 0;
        for (int i = 0; i < filled; i++) {
            slowest = Math.max(slowest, confirmations[i]);
        }
        releaseAfter = Math.max(SHORTEST_RELEASE, Math.min(LONGEST_RELEASE, slowest * 2));
    }

    public static void reset() {
        next = 0;
        filled = 0;
        releaseAfter = FIRST_RELEASE;
        kept = false;
    }

    public static Predicate<? super Entity> hide(Predicate<? super Entity> predicate) {
        long keptSince = System.nanoTime() - releaseAfter;
        if (!kept || lastKeptAt - keptSince <= 0) {
            return predicate;
        }

        return entity -> !isKeptSince(entity, keptSince) && predicate.test(entity);
    }

    private static boolean isKeptSince(Entity entity, long keptSince) {
        return entity instanceof KeptCrystal crystal
                && crystal.marlowcrystal$isKept()
                && crystal.marlowcrystal$keptAt() - keptSince > 0;
    }
}
