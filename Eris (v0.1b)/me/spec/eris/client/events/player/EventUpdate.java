package me.spec.eris.client.events.player;

import me.spec.eris.api.event.Event;

public class EventUpdate extends Event {

    private float yaw;
    private float pitch;
    private double posX;
    private double posY;
    private double posZ;
    private boolean onGround;
    private boolean pre;
    private boolean sneaking;

    public static float lastYaw;
    public static float lastPitch;
    public static float lastLastPitch;

    public EventUpdate(float yaw, float pitch, double x, double y, double z, boolean onGround, boolean sneaking, boolean pre) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.pre = pre;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.onGround = onGround;
        this.sneaking = sneaking;
    }

    @Override
    public void call() {
        super.call();
    }

    public boolean isPre() {
        return this.pre;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public double getX() {
        return posX;
    }

    public void setX(double posX) {
        this.posX = posX;
    }

    public double getY() {
        return posY;
    }

    public void setY(double posY) {
        this.posY = posY;
    }

    public double getZ() {
        return posZ;
    }

    public void setZ(double posZ) {
        this.posZ = posZ;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isSneaking() {
        return sneaking;
    }

    public void setSneaking(boolean sneaking) {
        this.sneaking = sneaking;
    }

}
