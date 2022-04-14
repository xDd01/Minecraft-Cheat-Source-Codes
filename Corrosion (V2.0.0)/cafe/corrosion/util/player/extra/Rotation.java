/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.player.extra;

public class Rotation {
    private final double posX;
    private final double posY;
    private final double posZ;
    private float rotationYaw;
    private float rotationPitch;

    public double getPosX() {
        return this.posX;
    }

    public double getPosY() {
        return this.posY;
    }

    public double getPosZ() {
        return this.posZ;
    }

    public float getRotationYaw() {
        return this.rotationYaw;
    }

    public float getRotationPitch() {
        return this.rotationPitch;
    }

    public void setRotationYaw(float rotationYaw) {
        this.rotationYaw = rotationYaw;
    }

    public void setRotationPitch(float rotationPitch) {
        this.rotationPitch = rotationPitch;
    }

    public Rotation(double posX, double posY, double posZ, float rotationYaw, float rotationPitch) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
    }
}

