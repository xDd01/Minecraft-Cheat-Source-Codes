package me.spec.eris.utils.math.rotation;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class RotationUtils {
    /*
    * Raytracing
    *
    * Checking if the casted ray (yaw & pitch) hits an entity
    *
    * Args: yaw, pitch, ray distance
    * */
    public static EntityLivingBase rayTrace(float yaw, float pitch, double distance) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld != null && mc.thePlayer != null) {
            Vec3 position = mc.thePlayer.getPositionEyes(mc.timer.renderPartialTicks);
            Vec3 lookVector = mc.thePlayer.getVectorForRotation(pitch, yaw);
            double reachDistance = distance;
            Entity pointedEntity = null;
            List<Entity> var5 = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().addCoord(lookVector.xCoord * mc.playerController.getBlockReachDistance(), lookVector.yCoord * mc.playerController.getBlockReachDistance(), lookVector.zCoord * mc.playerController.getBlockReachDistance()).expand(reachDistance, reachDistance, reachDistance));
            for (int var6 = 0; var6 < var5.size(); ++var6) {
                Entity currentEntity = (Entity) var5.get(var6);
                if (currentEntity.canBeCollidedWith()) {
                    MovingObjectPosition objPosition = currentEntity.getEntityBoundingBox().expand((double) currentEntity.getCollisionBorderSize(), (double) currentEntity.getCollisionBorderSize(), (double) currentEntity.getCollisionBorderSize()).contract(0.1, 0.1, 0.1).calculateIntercept(position, position.addVector(lookVector.xCoord * reachDistance, lookVector.yCoord * reachDistance, lookVector.zCoord * reachDistance));
                    if (objPosition != null) {
                        double range = position.distanceTo(objPosition.hitVec);
                        if (range < reachDistance) {
                            if (currentEntity == mc.thePlayer.ridingEntity && reachDistance == 0.0D) {
                                pointedEntity = currentEntity;
                            } else {
                                pointedEntity = currentEntity;
                                reachDistance = range;
                            }
                        }
                    }
                }
            }
            if (pointedEntity != null && (pointedEntity instanceof EntityLivingBase))
                return (EntityLivingBase) pointedEntity;
        }
        return null;
    }

    /*
    * Raycasting is a method of casting a ray to see
    * if it hits an object between you and the target
    *
    * Args: Target entity
    */
    public static boolean rayCast(Entity targetEntity) {
        Minecraft mc = Minecraft.getMinecraft();
        Vec3 playerPos = new Vec3(mc.thePlayer.posZ, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        Vec3 targetPos = new Vec3(targetEntity.posZ, targetEntity.getEntityBoundingBox().minY + targetEntity.getEyeHeight(), targetEntity.posZ);

        return mc.theWorld.rayTraceBlocks(playerPos, targetPos) == null;
    }

    public static float constrainAngle(float angle) {
        angle = angle % 360F;

        while (angle <= -180) {
            angle = angle + 360;
        }

        while (angle > 180) {
            angle = angle - 360;
        }
        return angle;
    }

    public static float[] getRotations(final EntityLivingBase ent, int mode) {
        if (mode == 0) {
            final double x = ent.posX;
            final double z = ent.posZ;
            final double y = ent.posY + ent.getEyeHeight() / 2.0f;
            return getRotationFromPosition(x, z, y);
        }
        if (mode == 1) {
            final double x = ent.posX;
            final double z = ent.posZ;
            final double y = ent.posY + ent.getEyeHeight() / 2.0f - 0.75;
            return getRotationFromPosition(x, z, y);
        }
        if (mode == 2) {
            final double x = ent.posX;
            final double z = ent.posZ;
            final double y = ent.posY + ent.getEyeHeight() / 2.0f - 1.2;
            return getRotationFromPosition(x, z, y);
        }
        if (mode == 3) {
            final double x = ent.posX;
            final double z = ent.posZ;
            final double y = ent.posY + ent.getEyeHeight() / 2.0f - 1.5;
            return getRotationFromPosition(x, z, y);
        }
        final double x = ent.posX;
        final double z = ent.posZ;
        final double y = ent.posY + ent.getEyeHeight() / 2.0f - 0.5;
        return getRotationFromPosition(x, z, y);
    }

    public static float[] getAverageRotations(final List<EntityLivingBase> targetList) {
        double posX = 0.0;
        double posY = 0.0;
        double posZ = 0.0;
        for (Entity ent : targetList) {
            posX += ent.posX;
            posY += ent.getEntityBoundingBox().maxY - 2.0;
            posZ += ent.posZ;
        }
        posX /= targetList.size();
        posY /= targetList.size();
        posZ /= targetList.size();
        return new float[]{getRotationFromPosition(posX, posZ, posY)[0], getRotationFromPosition(posX, posZ, posY)[1]};
    }

    public static float[] getRotationFromPosition(final double x, final double z, final double y) {
        final double xDiff = x - Minecraft.getMinecraft().thePlayer.posX;
        final double zDiff = z - Minecraft.getMinecraft().thePlayer.posZ;
        final double yDiff = y - Minecraft.getMinecraft().thePlayer.posY - 0.6;
        final double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        final float yaw = (float) (Math.atan2(zDiff, xDiff) * 180 / Math.PI) - 90.0f;
        final float pitch = (float) (-(Math.atan2(yDiff, dist) * 180 / Math.PI));
        return new float[]{yaw, pitch};
    }

    public static float[] getNeededRotations(EntityLivingBase entityIn) {
        double d0 = (entityIn.posX - Minecraft.getMinecraft().thePlayer.posX) + (entityIn.lastTickPosX - Minecraft.getMinecraft().thePlayer.posX) / 2;
        double d1 = (entityIn.posZ - Minecraft.getMinecraft().thePlayer.posZ) + (entityIn.lastTickPosZ - Minecraft.getMinecraft().thePlayer.posZ) / 2;
        ;
        double d2 = entityIn.posY + entityIn.getEyeHeight() - (Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().minY + (Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().maxY - Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().minY));
        double d3 = MathHelper.sqrt_double(d0 * d0 + d1 * d1);
        float f = (float) (MathHelper.func_181159_b(d1, d0) * 180.0D / Math.PI) - 90.0F;
        float f1 = (float) (-(MathHelper.func_181159_b(d2, d3) * 180.0D / Math.PI));
        return new float[]{f, f1};
    }
}
