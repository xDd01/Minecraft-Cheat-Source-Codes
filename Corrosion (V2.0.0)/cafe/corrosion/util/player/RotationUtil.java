/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.player;

import cafe.corrosion.util.math.RandomUtil;
import cafe.corrosion.util.player.extra.Rotation;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.apache.commons.lang3.RandomUtils;

public final class RotationUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Random random = new Random();
    private static double lastXOffset;
    private static double lastYOffset;
    private static double lastZOffset;
    private static int streak;
    private static int streakAttempt;
    private static boolean decreasing;

    public static void turnToEntityClient(Rotation destinationRotations) {
        RotationUtil.mc.thePlayer.rotationYaw = destinationRotations.getRotationYaw();
        RotationUtil.mc.thePlayer.rotationPitch = destinationRotations.getRotationPitch();
    }

    public static Rotation getRotationsRandom(EntityLivingBase entity, boolean randomizedAim) {
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        double randomXZ = randomizedAim ? threadLocalRandom.nextDouble(-0.05, 0.1) : 0.0;
        double randomY = randomizedAim ? threadLocalRandom.nextDouble(-0.05, 0.1) : 0.0;
        double x2 = entity.posX + randomXZ;
        double y2 = entity.posY + (double)entity.getEyeHeight() / 2.05 + randomY;
        double z2 = entity.posZ + randomXZ;
        return RotationUtil.attemptFacePosition(x2, y2, z2);
    }

    public static Rotation getAdvancedRotations(EntityLivingBase entity) {
        AxisAlignedBB bb2 = entity.getEntityBoundingBox();
        double posX = bb2.maxX - (bb2.maxX - bb2.minX) / 2.0;
        double posY = bb2.maxY - (bb2.maxY - bb2.minY) / 2.0;
        double posZ = bb2.maxZ - (bb2.maxZ - bb2.minZ) / 2.0;
        double randomizedOffsetX = RandomUtil.random(-0.05, 0.05);
        double randomizedOffsetY = RandomUtil.random(-0.08, 0.08);
        double randomizedOffsetZ = RandomUtil.random(-0.05, 0.05);
        return RotationUtil.attemptFacePosition(posX + randomizedOffsetX, posY + randomizedOffsetY, posZ + randomizedOffsetZ);
    }

    public static Rotation getSmartRotations(Entity entity) {
        double diffY;
        double diffX = entity.posX - RotationUtil.mc.thePlayer.posX;
        double diffZ = entity.posZ - RotationUtil.mc.thePlayer.posZ;
        double dist = Math.hypot(diffX, diffZ);
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
            diffY = entityLivingBase.posY + (double)entityLivingBase.getEyeHeight() * 0.9 - (RotationUtil.mc.thePlayer.posY + (double)RotationUtil.mc.thePlayer.getEyeHeight());
        } else {
            diffY = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0 - (RotationUtil.mc.thePlayer.posY + (double)RotationUtil.mc.thePlayer.getEyeHeight());
        }
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        float pitch = MathHelper.clamp_float((float)(-(Math.atan2(diffY, dist) * 180.0 / Math.PI)), -90.0f, 90.0f);
        return new Rotation(RotationUtil.mc.thePlayer.posX, RotationUtil.mc.thePlayer.posY, RotationUtil.mc.thePlayer.posZ, RotationUtil.mc.thePlayer.rotationYaw + RotationUtil.wrapAngle(yaw - RotationUtil.mc.thePlayer.rotationYaw), RotationUtil.mc.thePlayer.rotationPitch + RotationUtil.wrapAngle(pitch - RotationUtil.mc.thePlayer.rotationPitch));
    }

    public static float wrapAngle(float angle) {
        if ((angle %= 360.0f) >= 180.0f) {
            angle -= 360.0f;
        }
        if (angle < -180.0f) {
            angle += 360.0f;
        }
        return angle;
    }

    public static Rotation getBowAngles(Entity entity) {
        double xDelta = entity.posX - entity.lastTickPosX;
        double zDelta = entity.posZ - entity.lastTickPosZ;
        double distance = (double)RotationUtil.mc.thePlayer.getDistanceToEntity(entity) % 0.8;
        boolean sprint = entity.isSprinting();
        double xMulti = distance / 0.8 * xDelta * (sprint ? 1.45 : 1.3);
        double zMulti = distance / 0.8 * zDelta * (sprint ? 1.45 : 1.3);
        double x2 = entity.posX + xMulti - RotationUtil.mc.thePlayer.posX;
        double y2 = RotationUtil.mc.thePlayer.posY + (double)RotationUtil.mc.thePlayer.getEyeHeight() - (entity.posY + (double)entity.getEyeHeight());
        double z2 = entity.posZ + zMulti - RotationUtil.mc.thePlayer.posZ;
        double distanceToEntity = RotationUtil.mc.thePlayer.getDistanceToEntity(entity);
        float yaw = (float)Math.toDegrees(Math.atan2(z2, x2)) - 90.0f;
        float pitch = (float)Math.toDegrees(Math.atan2(y2, distanceToEntity));
        return new Rotation(x2, y2, z2, yaw, pitch);
    }

    public static Rotation attemptFacePosition(double x2, double y2, double z2) {
        double xDiff = x2 - RotationUtil.mc.thePlayer.posX;
        double yDiff = y2 - RotationUtil.mc.thePlayer.posY - (double)RotationUtil.mc.thePlayer.getEyeHeight();
        double zDiff = z2 - RotationUtil.mc.thePlayer.posZ;
        double dist = Math.hypot(xDiff, zDiff);
        float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(yDiff, dist) * 180.0 / Math.PI));
        return new Rotation(x2, y2, z2, yaw, pitch);
    }

    public static Rotation getScaffoldRotations(BlockPos position) {
        double direction = RotationUtil.direction();
        double posX = -Math.sin(direction) * 0.5;
        double posZ = Math.cos(direction) * 0.5;
        double x2 = (double)position.getX() - RotationUtil.mc.thePlayer.posX - posX;
        double y2 = (double)position.getY() - RotationUtil.mc.thePlayer.prevPosY - (double)RotationUtil.mc.thePlayer.getEyeHeight();
        double z2 = (double)position.getZ() - RotationUtil.mc.thePlayer.posZ - posZ;
        double distance = Math.hypot(x2, z2);
        float yaw = (float)(Math.atan2(z2, x2) * 180.0 / Math.PI - 90.0);
        float pitch = (float)(-(Math.atan2(y2, distance) * 180.0 / Math.PI));
        return new Rotation(x2, y2, z2, RotationUtil.mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - RotationUtil.mc.thePlayer.rotationYaw), RotationUtil.mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - RotationUtil.mc.thePlayer.rotationPitch));
    }

    private static double direction() {
        float rotationYaw = RotationUtil.mc.thePlayer.rotationYaw;
        if (RotationUtil.mc.thePlayer.movementInput.moveForward < 0.0f) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (RotationUtil.mc.thePlayer.movementInput.moveForward < 0.0f) {
            forward = -0.5f;
        } else if (RotationUtil.mc.thePlayer.movementInput.moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (RotationUtil.mc.thePlayer.movementInput.moveStrafe > 0.0f) {
            rotationYaw -= 90.0f * forward;
        }
        if (RotationUtil.mc.thePlayer.movementInput.moveStrafe < 0.0f) {
            rotationYaw += 90.0f * forward;
        }
        return Math.toRadians(rotationYaw);
    }

    public static float[] doSRTBypassIRotations(Entity entity) {
        double diffY;
        double diffX = entity.posX - RotationUtil.mc.thePlayer.posX;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
            diffY = entityLivingBase.posY + (double)entityLivingBase.getEyeHeight() * 0.9 - (RotationUtil.mc.thePlayer.posY + (double)RotationUtil.mc.thePlayer.getEyeHeight());
        } else {
            diffY = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0 - (RotationUtil.mc.thePlayer.posY + (double)RotationUtil.mc.thePlayer.getEyeHeight());
        }
        double diffZ = entity.posZ - RotationUtil.mc.thePlayer.posZ;
        double dist = Math.hypot(diffX, diffZ);
        float sensitivity = RotationUtil.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        float gcd = sensitivity * sensitivity * sensitivity * 1.2f;
        float yawRand = random.nextBoolean() ? -RandomUtils.nextFloat(0.0f, 3.0f) : RandomUtils.nextFloat(0.0f, 3.0f);
        float pitchRand = random.nextBoolean() ? -RandomUtils.nextFloat(0.0f, 3.0f) : RandomUtils.nextFloat(0.0f, 3.0f);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f + yawRand;
        float pitch = MathHelper.clamp_float((float)(-(Math.atan2(diffY, dist) * 180.0 / Math.PI)) + pitchRand + RotationUtil.mc.thePlayer.getDistanceToEntity(entity) * 1.25f, -90.0f, 90.0f);
        if (RotationUtil.mc.thePlayer.ticksExisted % 2 == 0) {
            pitch = MathHelper.clamp_float(pitch + (random.nextBoolean() ? RandomUtils.nextFloat(2.0f, 8.0f) : -RandomUtils.nextFloat(2.0f, 8.0f)), -90.0f, 90.0f);
        }
        pitch -= pitch % gcd;
        yaw -= yaw % gcd;
        return new float[]{RotationUtil.mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - RotationUtil.mc.thePlayer.rotationYaw), RotationUtil.mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - RotationUtil.mc.thePlayer.rotationPitch)};
    }

    public static EntityLivingBase rayCast(float yaw, float pitch, double range) {
        Minecraft mc2 = Minecraft.getMinecraft();
        if (mc2.theWorld == null && mc2.thePlayer == null) {
            return null;
        }
        Vec3 position = mc2.thePlayer.getLook(mc2.timer.renderPartialTicks);
        Vec3 lookVector = mc2.thePlayer.getVectorForRotation(pitch, yaw);
        Entity pointedEntity = null;
        List<Entity> entities = mc2.theWorld.getEntitiesWithinAABBExcludingEntity(mc2.thePlayer, mc2.thePlayer.getEntityBoundingBox().addCoord(lookVector.xCoord * (double)mc2.playerController.getBlockReachDistance(), lookVector.yCoord * (double)mc2.playerController.getBlockReachDistance(), lookVector.zCoord * (double)mc2.playerController.getBlockReachDistance()).expand(range, range, range));
        for (Entity currentEntity : entities) {
            double distance;
            MovingObjectPosition objPosition;
            if (!currentEntity.canBeCollidedWith() || (objPosition = currentEntity.getEntityBoundingBox().expand(currentEntity.getCollisionBorderSize(), currentEntity.getCollisionBorderSize(), currentEntity.getCollisionBorderSize()).contract(0.1, 0.1, 0.1).calculateIntercept(position, position.addVector(lookVector.xCoord * range, lookVector.yCoord * range, lookVector.zCoord * range))) == null || !((distance = position.distanceTo(objPosition.hitVec)) < range)) continue;
            pointedEntity = currentEntity;
        }
        return (EntityLivingBase)pointedEntity;
    }

    public static float clampAngle(float angle) {
        angle %= 360.0f;
        while (angle <= -180.0f) {
            angle += 360.0f;
        }
        while (angle > 180.0f) {
            angle -= 360.0f;
        }
        return angle;
    }

    public static double preciseRound(double value, double precision) {
        double scale = Math.pow(10.0, precision);
        return (double)Math.round(value * scale) / scale;
    }

    private RotationUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

