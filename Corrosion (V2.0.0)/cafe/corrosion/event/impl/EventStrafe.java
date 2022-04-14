/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.event.impl;

import cafe.corrosion.event.Event;
import net.minecraft.client.Minecraft;

public class EventStrafe
extends Event {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private float strafe;
    private float forward;
    private float friction;
    private float yaw;

    public void setMotion(double speed) {
        EventStrafe.mc.thePlayer.motionX = 0.0;
        EventStrafe.mc.thePlayer.motionZ = 0.0;
        this.setFriction((float)(speed *= this.strafe != 0.0f && this.forward != 0.0f ? 0.91 : 1.0));
    }

    public void setMotionLegit(float friction) {
        this.setFriction(EventStrafe.mc.thePlayer.onGround ? friction : friction * 0.43f);
    }

    public void setMotionPartialStrafe(float friction, float strafeComponent) {
        float remainder = 1.0f - strafeComponent;
        if (this.forward != 0.0f && this.strafe != 0.0f) {
            friction = (float)((double)friction * 0.91);
        }
        if (EventStrafe.mc.thePlayer.onGround) {
            this.setMotion(friction);
        } else {
            EventStrafe.mc.thePlayer.motionX *= (double)strafeComponent;
            EventStrafe.mc.thePlayer.motionZ *= (double)strafeComponent;
            this.setFriction(friction * remainder);
        }
    }

    public EventStrafe(float strafe, float forward, float friction, float yaw) {
        this.strafe = strafe;
        this.forward = forward;
        this.friction = friction;
        this.yaw = yaw;
    }

    public float getStrafe() {
        return this.strafe;
    }

    public float getForward() {
        return this.forward;
    }

    public float getFriction() {
        return this.friction;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    public void setForward(float forward) {
        this.forward = forward;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}

