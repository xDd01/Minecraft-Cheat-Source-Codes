/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.player;

import cafe.corrosion.event.impl.EventMove;
import cafe.corrosion.util.packet.PacketUtil;
import javax.vecmath.Vector2d;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MovementInput;

public final class MovementUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static float movementDirection;

    public static Vector2d getMotion(double moveSpeed) {
        MovementInput movementInput = MovementUtil.mc.thePlayer.movementInput;
        double moveForward = movementInput.moveForward;
        double moveStrafe = movementInput.moveStrafe;
        double rotationYaw = MovementUtil.mc.thePlayer.rotationYaw;
        if (moveForward != 0.0 || moveStrafe != 0.0) {
            if (moveStrafe > 0.0) {
                moveStrafe = 1.0;
            } else if (moveStrafe < 0.0) {
                moveStrafe = -1.0;
            }
            if (moveForward != 0.0) {
                if (moveStrafe > 0.0) {
                    rotationYaw += moveForward > 0.0 ? -45.0 : 45.0;
                } else if (moveStrafe < 0.0) {
                    rotationYaw += moveForward > 0.0 ? 45.0 : -45.0;
                }
                moveStrafe = 0.0;
                if (moveForward > 0.0) {
                    moveForward = 1.0;
                } else if (moveForward < 0.0) {
                    moveForward = -1.0;
                }
            }
            double cos = Math.cos(Math.toRadians(rotationYaw + 90.0));
            double sin = Math.sin(Math.toRadians(rotationYaw + 90.0));
            return new Vector2d(moveForward * moveSpeed * cos + moveStrafe * moveSpeed * sin, moveForward * moveSpeed * sin - moveStrafe * moveSpeed * cos);
        }
        return new Vector2d(0.0, 0.0);
    }

    public static void sendMotion(double speed, double dist) {
        for (double d2 = 0.0; d2 < speed; d2 += dist) {
            Vector2d motion = MovementUtil.getMotion(dist);
            double x2 = MovementUtil.mc.thePlayer.posX;
            double y2 = MovementUtil.mc.thePlayer.posY;
            double z2 = MovementUtil.mc.thePlayer.posZ;
            PacketUtil.sendNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(x2, y2, z2, MovementUtil.mc.thePlayer.rotationYaw, MovementUtil.mc.thePlayer.rotationPitch, true));
            MovementUtil.mc.thePlayer.moveEntityNoEvent(motion.x, 0.0, motion.y);
        }
    }

    public static void setMotion(double moveSpeed) {
        MovementInput movementInput = MovementUtil.mc.thePlayer.movementInput;
        double moveForward = movementInput.moveForward;
        double moveStrafe = movementInput.moveStrafe;
        double rotationYaw = MovementUtil.mc.thePlayer.rotationYaw;
        if (moveForward == 0.0 && moveStrafe == 0.0) {
            MovementUtil.mc.thePlayer.motionZ = 0.0;
            MovementUtil.mc.thePlayer.motionX = 0.0;
        } else {
            if (moveStrafe > 0.0) {
                moveStrafe = 1.0;
            } else if (moveStrafe < 0.0) {
                moveStrafe = -1.0;
            }
            if (moveForward != 0.0) {
                if (moveStrafe > 0.0) {
                    rotationYaw += (double)(moveForward > 0.0 ? -45 : 45);
                } else if (moveStrafe < 0.0) {
                    rotationYaw += (double)(moveForward > 0.0 ? 45 : -45);
                }
                moveStrafe = 0.0;
                if (moveForward > 0.0) {
                    moveForward = 1.0;
                } else if (moveForward < 0.0) {
                    moveForward = -1.0;
                }
            }
            double cos = Math.cos(Math.toRadians(rotationYaw + 90.0));
            double sin = Math.sin(Math.toRadians(rotationYaw + 90.0));
            MovementUtil.mc.thePlayer.motionX = moveForward * moveSpeed * cos + moveStrafe * moveSpeed * sin;
            MovementUtil.mc.thePlayer.motionZ = moveForward * moveSpeed * sin - moveStrafe * moveSpeed * cos;
        }
        movementDirection = (float)rotationYaw;
    }

    public static void setMotion(EventMove eventMove, double moveSpeed) {
        MovementInput movementInput = MovementUtil.mc.thePlayer.movementInput;
        double moveForward = movementInput.moveForward;
        double moveStrafe = movementInput.moveStrafe;
        double rotationYaw = MovementUtil.mc.thePlayer.rotationYaw;
        if (moveForward == 0.0 && moveStrafe == 0.0) {
            MovementUtil.mc.thePlayer.motionZ = 0.0;
            MovementUtil.mc.thePlayer.motionX = 0.0;
        } else {
            if (moveStrafe > 0.0) {
                moveStrafe = 1.0;
            } else if (moveStrafe < 0.0) {
                moveStrafe = -1.0;
            }
            if (moveForward != 0.0) {
                if (moveStrafe > 0.0) {
                    rotationYaw += (double)(moveForward > 0.0 ? -45 : 45);
                } else if (moveStrafe < 0.0) {
                    rotationYaw += (double)(moveForward > 0.0 ? 45 : -45);
                }
                moveStrafe = 0.0;
                if (moveForward > 0.0) {
                    moveForward = 1.0;
                } else if (moveForward < 0.0) {
                    moveForward = -1.0;
                }
            }
            double cos = Math.cos(Math.toRadians(rotationYaw + 90.0));
            double sin = Math.sin(Math.toRadians(rotationYaw + 90.0));
            eventMove.setX(moveForward * moveSpeed * cos + moveStrafe * moveSpeed * sin);
            eventMove.setZ(moveForward * moveSpeed * sin - moveStrafe * moveSpeed * cos);
        }
        movementDirection = (float)rotationYaw;
    }

    public static double roundPos(double y2) {
        return y2 - y2 % 0.015625;
    }

    public static double roundPos(double y2, double round) {
        return y2 - y2 % round;
    }

    private MovementUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

