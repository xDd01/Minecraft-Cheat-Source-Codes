/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.player;

import cafe.corrosion.util.packet.PacketUtil;
import cafe.corrosion.viamcp.ViaMCP;
import java.util.Arrays;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.ChatComponentText;

public final class PlayerUtil {
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void sendMessage(String ... messages) {
        Arrays.stream(messages).map(message -> "&e&lCORROSION &7" + message).map(PlayerUtil::translateColorCodes).map(ChatComponentText::new).forEach(PlayerUtil.mc.thePlayer::addChatMessage);
    }

    public static String translateColorCodes(String textToTranslate) {
        char[] b2 = textToTranslate.toCharArray();
        for (int i2 = 0; i2 < b2.length - 1; ++i2) {
            if (b2[i2] != '&' || "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b2[i2 + 1]) <= -1) continue;
            b2[i2] = 167;
            b2[i2 + 1] = Character.toLowerCase(b2[i2 + 1]);
        }
        return new String(b2);
    }

    public static String stripColor(String input) {
        return input == null ? null : STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public static void attackSwing(Entity entity, boolean keepSprint) {
        if (!keepSprint) {
            PlayerUtil.mc.thePlayer.motionX *= 0.6;
            PlayerUtil.mc.thePlayer.motionZ *= 0.6;
            PlayerUtil.mc.thePlayer.setSprinting(false);
        }
        if (ViaMCP.getInstance().getVersion() <= 47) {
            PlayerUtil.mc.thePlayer.swingItem();
        }
        PacketUtil.send(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
        if (ViaMCP.getInstance().getVersion() > 47) {
            PlayerUtil.mc.thePlayer.swingItem();
        }
    }

    public static boolean isMoving() {
        return PlayerUtil.mc.thePlayer.movementInput.moveForward != 0.0f || PlayerUtil.mc.thePlayer.movementInput.moveStrafe != 0.0f;
    }

    public static boolean isMovingOnGround() {
        return PlayerUtil.isMoving() && PlayerUtil.mc.thePlayer.onGround;
    }

    public static double getNCPBaseSpeed() {
        double base = 0.2873;
        if (PlayerUtil.mc.thePlayer != null && PlayerUtil.mc.thePlayer.isPotionActive(1)) {
            base *= 1.0 + 0.2 * (double)(PlayerUtil.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return base;
    }

    public static double getVerusBaseSpeed() {
        double base = 0.2865;
        if (PlayerUtil.mc.thePlayer.isPotionActive(1)) {
            base *= 1.0 + 0.0495 * (double)(PlayerUtil.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return base;
    }

    public static C03PacketPlayer.C06PacketPlayerPosLook translate(C03PacketPlayer packet) {
        double x2 = PlayerUtil.mc.thePlayer.posX;
        double y2 = PlayerUtil.mc.thePlayer.posY;
        double z2 = PlayerUtil.mc.thePlayer.posZ;
        float yaw = PlayerUtil.mc.thePlayer.rotationYaw;
        float pitch = PlayerUtil.mc.thePlayer.rotationPitch;
        if (packet.getRotating()) {
            yaw = packet.getYaw();
            pitch = packet.getPitch();
        }
        if (packet.isMoving()) {
            x2 = packet.getPositionX();
            y2 = packet.getPositionY();
            z2 = packet.getPositionZ();
        }
        return new C03PacketPlayer.C06PacketPlayerPosLook(x2, y2, z2, yaw, pitch, packet.isOnGround());
    }

    private PlayerUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

