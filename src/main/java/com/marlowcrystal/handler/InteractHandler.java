package com.marlowcrystal.handler;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Unique;

public class InteractHandler implements PlayerInteractEntityC2SPacket.Handler {
    @Unique
    private final MinecraftClient mc;

    public InteractHandler(MinecraftClient mc) {
        this.mc = mc;
    }

    @Override
    public void interact(Hand hand) {
        // Empty handler (can be implemented if needed)
    }

    @Override
    public void interactAt(Hand hand, Vec3d pos) {
        // Empty handler (can be implemented if needed)
    }

    @Override
    public void attack() {
        // Check if the crosshair is targeting an entity
        HitResult hitResult = mc.crosshairTarget;
        if (!(hitResult instanceof EntityHitResult entityHitResult)) {
            return;
        }

        // Only handle End Crystals
        if (entityHitResult.getEntity() instanceof EndCrystalEntity endCrystal) {
            if (canDestroyEndCrystal()) {
                destroyEndCrystal(endCrystal);
            }
        }
    }

    /**
     * Checks if the player is allowed to destroy an End Crystal.
     */
    @Unique
    private boolean canDestroyEndCrystal() {
        StatusEffectInstance weakness = mc.player.getStatusEffect(StatusEffects.WEAKNESS);
        StatusEffectInstance strength = mc.player.getStatusEffect(StatusEffects.STRENGTH);

        // Allow destruction if no weakness, strength overcomes weakness
        return weakness == null || (strength != null && strength.getAmplifier() > weakness.getAmplifier()) || isStrongTool(mc.player.getMainHandStack());
    }

    /**
     * Checks if the given item stack is a strong tool (diamond or netherite).
     */
    @Unique
    private boolean isStrongTool(ItemStack itemStack) {
        if (itemStack == null || !(itemStack.getItem() instanceof ToolItem toolItem)) {
            return false;
        }

        ToolMaterials material = (ToolMaterials) toolItem.getMaterial();
        return material == ToolMaterials.DIAMOND || material == ToolMaterials.NETHERITE;
    }

    /**
     * Destroys the given End Crystal entity.
     */
    @Unique
    private void destroyEndCrystal(Entity entity) {
        entity.kill();
        entity.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);
        entity.discard();
    }
}
