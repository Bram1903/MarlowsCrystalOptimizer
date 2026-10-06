package com.deathmotion.marlowcrystal.crystal;

import com.google.common.collect.Iterables;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class KeptCrystals {

    // Only reached when no block action followed the hit, so no acknowledgement can tell a rejected hit apart. Counted
    // in client ticks rather than time, so a stalled client or another tick rate shows the crystal on the same tick.
    private static final long HIDDEN_TICKS = 30;

    private final List<EndCrystal> kept = new ArrayList<>();

    private long lastKeptAt;

    private volatile long ticks;

    private boolean seeingPast;

    private static boolean isHidden(Entity entity, long keptSince) {
        return entity instanceof KeptCrystal crystal
                && crystal.marlowcrystal$isKept()
                && crystal.marlowcrystal$keptAt() - keptSince > 0;
    }

    public boolean isHidden(Entity entity) {
        return isHidden(entity, ticks - HIDDEN_TICKS);
    }

    public void keep(EndCrystal crystal, int sequence) {
        long now = ticks;
        forgetSettled(now - HIDDEN_TICKS);
        // Striking a hidden crystal again does not hide it for longer.
        if (isHidden(crystal, now - HIDDEN_TICKS)) {
            return;
        }

        ((KeptCrystal) crystal).marlowcrystal$keep(now, sequence);
        kept.add(crystal);
        lastKeptAt = now;
    }

    // Each tick of a level, at the head of Minecraft.tick, so a crystal kept during a tick stays hidden for that tick and
    // the 29 after it.
    public void ticked() {
        ticks++;
    }

    // The server sends a removal while it handles the hit, and an acknowledgement only after every packet before it.
    public void acknowledged(int sequence) {
        forgetSettled(ticks - HIDDEN_TICKS);
        kept.removeIf(crystal -> {
            KeptCrystal keptCrystal = (KeptCrystal) crystal;
            if (keptCrystal.marlowcrystal$sequence() >= sequence) {
                return false;
            }

            keptCrystal.marlowcrystal$release();
            return true;
        });
    }

    // A hidden crystal still stands in every entity lookup, the way the server still holds it, except inside this.
    public <T> T seePast(Supplier<T> lookup) {
        seeingPast = true;
        try {
            return lookup.get();
        } finally {
            seeingPast = false;
        }
    }

    public Predicate<? super Entity> hide(Predicate<? super Entity> predicate) {
        long keptSince = ticks - HIDDEN_TICKS;
        if (!seeingPast || nothingKept(keptSince)) {
            return predicate;
        }

        return entity -> !isHidden(entity, keptSince) && predicate.test(entity);
    }

    // Only the entities EndCrystalItem.useOn finds above the clicked block. The packet is sent whatever useOn answers,
    // so this changes nothing but the swing and the stack on the client. A server never holds a hidden crystal.
    public List<Entity> withoutHidden(List<Entity> entities) {
        long keptSince = ticks - HIDDEN_TICKS;
        for (Entity entity : entities) {
            if (isHidden(entity, keptSince)) {
                List<Entity> shown = new ArrayList<>(entities);
                shown.removeIf(other -> isHidden(other, keptSince));
                return shown;
            }
        }

        return entities;
    }

    public Iterable<Entity> hide(Iterable<Entity> entities) {
        long keptSince = ticks - HIDDEN_TICKS;
        if (nothingKept(keptSince)) {
            return entities;
        }

        return Iterables.filter(entities, entity -> !isHidden(entity, keptSince));
    }

    private void forgetSettled(long keptSince) {
        kept.removeIf(crystal -> crystal.isRemoved() || !isHidden(crystal, keptSince));
    }

    // Cleared once everything has timed out, so a level left behind is not held on to.
    private boolean nothingKept(long keptSince) {
        if (kept.isEmpty()) {
            return true;
        }

        if (lastKeptAt - keptSince <= 0) {
            kept.clear();
            return true;
        }

        return false;
    }
}
