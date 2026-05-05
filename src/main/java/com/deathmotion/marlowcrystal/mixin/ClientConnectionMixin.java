package com.deathmotion.marlowcrystal.mixin;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import com.deathmotion.marlowcrystal.cache.OptOutCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ClientConnectionMixin {

    @Unique
    private OptOutCache optOutCache;

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"))
    private void onPacketSend(Packet<?> packet, CallbackInfo ci) {
        if (!(packet instanceof ServerboundAttackPacket(int entityId))) {
            return;
        }

        if (optOutCache == null) {
            optOutCache = MarlowCrystal.getInstance().getOptOutCache();
        }
        if (optOutCache.isOptedOut()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }

        Entity target = mc.level.getEntity(entityId);
        if (!(target instanceof EndCrystal crystal)) {
            return;
        }

        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }

        if (canDestroyCrystal(player)) {
            destroyCrystal(crystal);
            retargetCrosshair(mc, crystal);
        }
    }

    @Unique
    private void retargetCrosshair(Minecraft mc, EndCrystal crystal) {
        LocalPlayer player = mc.player;
        if (player == null || mc.hitResult == null || mc.crosshairPickEntity != crystal) {
            return;
        }

        HitResult retraced = player.pick(player.blockInteractionRange(), 1.0F, false);
        mc.crosshairPickEntity = null;
        mc.hitResult = retraced;
    }

    @Unique
    private boolean canDestroyCrystal(LocalPlayer player) {
        double damage = player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
        damage += getWeaponDamage(player.getMainHandItem());

        MobEffectInstance strength = player.getEffect(MobEffects.STRENGTH);
        if (strength != null) {
            damage += 3.0D * (strength.getAmplifier() + 1);
        }

        MobEffectInstance weakness = player.getEffect(MobEffects.WEAKNESS);
        if (weakness != null) {
            damage -= 4.0D * (weakness.getAmplifier() + 1);
        }

        return damage > 0.0D;
    }

    @Unique
    private double getWeaponDamage(ItemStack item) {
        if (item.isEmpty()) return 0.0D;
        final double[] sum = {0.0D};
        item.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
            if (Attributes.ATTACK_DAMAGE.equals(attribute)) {
                sum[0] += modifier.amount();
            }
        });
        return sum[0];
    }

    @Unique
    private void destroyCrystal(EndCrystal crystal) {
        crystal.remove(Entity.RemovalReason.KILLED);
        crystal.gameEvent(GameEvent.ENTITY_DIE);
    }
}
