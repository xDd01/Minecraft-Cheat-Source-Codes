/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.player;

import cafe.corrosion.Corrosion;
import cafe.corrosion.command.impl.FriendCommand;
import cafe.corrosion.module.impl.misc.AntiBot;
import cafe.corrosion.util.player.TargetOptions;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class TargetFilter {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static Predicate<EntityLivingBase> targetFilter(TargetOptions targetOptions) {
        AntiBot antiBot = (AntiBot)Corrosion.INSTANCE.getModuleManager().getModule(AntiBot.class);
        boolean allowAnimals = targetOptions.isAnimals();
        boolean allowPlayers = targetOptions.isPlayers();
        boolean allowHostile = targetOptions.isHostile();
        boolean allowInvisible = targetOptions.isInvisible();
        double range = targetOptions.getRange();
        return entity -> {
            if (entity instanceof EntityArmorStand) {
                return false;
            }
            if (entity.equals(TargetFilter.mc.thePlayer)) {
                return false;
            }
            if (FriendCommand.FRIEND_NAMES.contains(entity.getName().toLowerCase())) {
                return false;
            }
            if (!allowAnimals && entity instanceof EntityAnimal) {
                return false;
            }
            if (!allowPlayers & entity instanceof EntityPlayer) {
                return false;
            }
            if (!allowHostile && entity instanceof EntityMob) {
                return false;
            }
            if (!allowInvisible && entity.isInvisible()) {
                return false;
            }
            if (antiBot.isEnabled() && antiBot.isBad((Entity)entity) || entity.isDead) {
                return false;
            }
            if (targetOptions.isHitbox()) {
                return TargetFilter.isInHitbox(TargetFilter.mc.thePlayer, entity, range);
            }
            return TargetFilter.isWithinRange(entity, range);
        };
    }

    private static boolean isWithinRange(EntityLivingBase second, double range) {
        return (double)TargetFilter.mc.thePlayer.getDistanceToEntity(second) <= range;
    }

    public static boolean isInHitbox(EntityLivingBase first, EntityLivingBase second, double range) {
        float collisionSize = 0.0f;
        Vec3 vec3 = first.getPositionEyes(1.0f);
        Vec3 vec31 = first.getLook(1.0f);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range);
        AxisAlignedBB target = second.getEntityBoundingBox().expand(collisionSize, collisionSize, collisionSize);
        MovingObjectPosition raytrace = target.calculateIntercept(vec3, vec32);
        if (raytrace != null && raytrace.hitVec.distanceTo(vec3) < range) {
            return true;
        }
        return target.isVecInside(vec32);
    }
}

