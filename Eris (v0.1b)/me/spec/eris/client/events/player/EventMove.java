package me.spec.eris.client.events.player;

import me.spec.eris.Eris;
import me.spec.eris.api.event.Event;
import me.spec.eris.client.modules.combat.Killaura;
import me.spec.eris.client.modules.combat.TargetStrafe;
import me.spec.eris.utils.math.rotation.RotationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;

public class EventMove extends Event {

    public static double lastDistance;
    private double x;
    private double y;
    private double z;

    public EventMove(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getMotionY(double mY) {
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.jump)) {
            mY += (Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1;
        }
        return mY;
    }

    public double getLegitMotion() {
        return 0.41999998688697815D;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getMovementSpeed() {
        double baseSpeed = 0.2873D;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }

    public double getMovementSpeed(double baseSpeed) {
        double speed = baseSpeed;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            return speed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return speed;
    }


    public void setMoveSpeed(double moveSpeed) {
        Minecraft mc = Minecraft.getMinecraft();
        TargetStrafe targetStrafe = ((TargetStrafe)Eris.getInstance().getModuleManager().getModuleByClass(TargetStrafe.class));
        double moveForward = mc.thePlayer.movementInput.getForward();
        double moveStrafe = targetStrafe.canStrafe() ? targetStrafe.strafeDirection : mc.thePlayer.movementInput.getStrafe();
        if (targetStrafe.canStrafe()) {
            if (mc.thePlayer.getDistanceToEntity(Killaura.currentEntity) <= 3)  moveForward = 0;
        }
        double yaw = targetStrafe.canStrafe() ? RotationUtils.getNeededRotations(Killaura.currentEntity)[0] : mc.thePlayer.rotationYaw;
        if (moveForward == 0.0F && moveStrafe == 0.0F) {
            setX(0);
            setZ(0);
        }
        if (moveForward != 0 && moveStrafe != 0) {
            moveForward = moveForward * Math.sin(Math.PI / 4);
            moveStrafe = moveStrafe * Math.cos(Math.PI / 4);
        }
        setX(moveForward * moveSpeed * -Math.sin(Math.toRadians(yaw)) + (moveStrafe) * moveSpeed * Math.cos(Math.toRadians(yaw)));
        setZ(moveForward * moveSpeed * Math.cos(Math.toRadians(yaw)) - (moveStrafe) * moveSpeed * -Math.sin(Math.toRadians(yaw)));
    }

    public double getJumpBoostModifier(double baseJumpHeight) {
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.jump)) {
            int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            baseJumpHeight += (float) (amplifier + 1) * 0.1F;
        }

        return baseJumpHeight;
    }
}
