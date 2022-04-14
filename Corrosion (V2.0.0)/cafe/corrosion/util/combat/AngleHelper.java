/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.combat;

import cafe.corrosion.util.math.RandomUtil;
import cafe.corrosion.util.player.extra.Rotation;
import cafe.corrosion.util.vector.impl.Vector2F;
import cafe.corrosion.util.vector.impl.Vector3F;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class AngleHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final float minYawSmoothing;
    private final float maxYawSmoothing;
    private final float minPitchSmoothing;
    private final float maxPitchSmoothing;
    private final Vector3F delta = new Vector3F(0.0f, 0.0f, 0.0f);
    private final Angle smoothedAngle = new Angle(Float.valueOf(0.0f), Float.valueOf(0.0f));
    private final Random random = new Random();
    private float height = (float)RandomUtil.random(1.1f, 1.8f);

    public static Vector2F getRotations(Vec3 origin, Vec3 position) {
        Vec3 org = new Vec3(origin.xCoord, origin.yCoord, origin.zCoord);
        Vec3 difference = position.subtract(org);
        double distance = difference.flat().lengthVector();
        float yaw = (float)Math.toDegrees(Math.atan2(difference.zCoord, difference.xCoord)) - 90.0f;
        float pitch = (float)(-Math.toDegrees(Math.atan2(difference.yCoord, distance)));
        return new Vector2F(yaw, pitch);
    }

    public static Vector2F getRotations(Vec3 position) {
        return AngleHelper.getRotations(AngleHelper.mc.thePlayer.getPositionVector().addVector(0.0, AngleHelper.mc.thePlayer.getEyeHeight(), 0.0), position);
    }

    public static Vector2F getRotations(Entity entity) {
        return AngleHelper.getRotations(AngleHelper.mc.thePlayer.getPositionVector().addVector(0.0, AngleHelper.mc.thePlayer.getEyeHeight(), 0.0), entity.getPositionVector().addVector(0.0, (double)(entity.getEyeHeight() / 2.0f) / (double)Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity), 0.0));
    }

    public static float[] getRotations(EntityLivingBase ent) {
        double x2 = ent.posX;
        double z2 = ent.posZ;
        double y2 = ent.posY + (double)(ent.getEyeHeight() / 2.0f);
        return AngleHelper.getRotationFromPosition(x2, z2, y2);
    }

    public static float[] getRotationFromPosition(double x2, double z2, double y2) {
        double xDiff = x2 - Minecraft.getMinecraft().thePlayer.posX;
        double zDiff = z2 - Minecraft.getMinecraft().thePlayer.posZ;
        double yDiff = y2 - Minecraft.getMinecraft().thePlayer.posY - 1.2;
        double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(yDiff, dist) * 180.0 / Math.PI));
        return new float[]{yaw, pitch};
    }

    public float randomFloat(float min, float max) {
        return min + this.random.nextFloat() * (max - min);
    }

    public Angle calculateAngle(Vector3F destination, Vector3F source, EntityLivingBase ent, int mode) {
        Angle angles = new Angle(Float.valueOf(0.0f), Float.valueOf(0.0f));
        this.delta.setX(destination.getX() - source.getX());
        this.delta.setY(destination.getY() + this.height - (source.getY() + this.height));
        this.delta.setZ(destination.getZ() - source.getZ());
        double hypotenuse = Math.hypot(this.delta.getX(), this.delta.getZ());
        float yawAtan = (float)Math.atan2(this.delta.getZ(), this.delta.getX());
        float pitchAtan = (float)Math.atan2(this.delta.getY(), hypotenuse);
        float deg = 57.29578f;
        float yaw = yawAtan * deg - 90.0f;
        float pitch = -(pitchAtan * deg);
        return angles.setYaw(Float.valueOf(yaw)).setPitch(Float.valueOf(pitch)).clampAngle();
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Angle smoothAngle(Angle destination, Angle source, float pitch, float yaw) {
        return this.smoothedAngle.setYaw(Float.valueOf(source.getYaw().floatValue() - destination.getYaw().floatValue() - (Math.abs(source.getYaw().floatValue() - destination.getYaw().floatValue()) > 5.0f ? Math.abs(source.getYaw().floatValue() - destination.getYaw().floatValue()) / Math.abs(source.getYaw().floatValue() - destination.getYaw().floatValue()) * 2.0f / 2.0f : 0.0f))).setPitch(Float.valueOf(source.getPitch().floatValue() - destination.getPitch().floatValue())).clampAngle().setYaw(Float.valueOf(source.getYaw().floatValue() - this.smoothedAngle.getYaw().floatValue() / yaw * this.randomFloat(this.minYawSmoothing, this.maxYawSmoothing))).clampAngle().setPitch(Float.valueOf(source.getPitch().floatValue() - this.smoothedAngle.getPitch().floatValue() / pitch * this.randomFloat(this.minPitchSmoothing, this.maxPitchSmoothing))).clampAngle();
    }

    public AngleHelper(float minYawSmoothing, float maxYawSmoothing, float minPitchSmoothing, float maxPitchSmoothing) {
        this.minYawSmoothing = minYawSmoothing;
        this.maxYawSmoothing = maxYawSmoothing;
        this.minPitchSmoothing = minPitchSmoothing;
        this.maxPitchSmoothing = maxPitchSmoothing;
    }

    public static class Angle
    extends Vector2F {
        public int calls;
        public int requests;
        public float lastPitch;
        private EntityLivingBase lastEntity;

        public Angle(Float x2, Float y2) {
            super(x2.floatValue(), y2.floatValue());
        }

        public Angle setYaw(Float yaw) {
            this.setX(yaw.floatValue());
            return this;
        }

        public Angle setPitch(Float pitch) {
            this.setY(pitch.floatValue());
            return this;
        }

        public Float getYaw() {
            return Float.valueOf(this.getX());
        }

        public Float getPitch() {
            this.lastPitch = this.getY();
            return Float.valueOf(this.lastPitch);
        }

        public void setLastEntity(EntityLivingBase entity) {
            this.lastEntity = entity;
        }

        public Angle clampAngle() {
            this.setYaw(Float.valueOf(this.getYaw().floatValue() % 360.0f));
            this.setPitch(Float.valueOf(this.getPitch().floatValue() % 360.0f));
            while (this.getYaw().floatValue() <= -180.0f) {
                this.setYaw(Float.valueOf(this.getYaw().floatValue() + 360.0f));
            }
            while (this.getPitch().floatValue() <= -180.0f) {
                this.setPitch(Float.valueOf(this.getPitch().floatValue() + 360.0f));
            }
            while (this.getYaw().floatValue() > 180.0f) {
                this.setYaw(Float.valueOf(this.getYaw().floatValue() - 360.0f));
            }
            while (this.getPitch().floatValue() > 180.0f) {
                this.setPitch(Float.valueOf(this.getPitch().floatValue() - 360.0f));
            }
            return this;
        }

        public Rotation toRotation(EntityLivingBase target) {
            return new Rotation(target.posX, target.posY, target.posZ, this.getYaw().floatValue(), this.getPitch().floatValue());
        }
    }
}

