package io.github.nevalackin.client.impl.module.movement.extras;

import io.github.nevalackin.client.api.module.Category;
import io.github.nevalackin.client.api.module.Module;
import io.github.nevalackin.client.api.notification.NotificationType;
import io.github.nevalackin.client.impl.core.KetamineClient;
import io.github.nevalackin.client.impl.event.game.GetTimerSpeedEvent;
import io.github.nevalackin.client.impl.event.packet.ReceivePacketEvent;
import io.github.nevalackin.client.impl.event.player.MoveEvent;
import io.github.nevalackin.client.impl.event.player.UpdatePositionEvent;
import io.github.nevalackin.client.impl.property.BooleanProperty;
import io.github.nevalackin.client.impl.property.DoubleProperty;
import io.github.nevalackin.client.impl.property.EnumProperty;
import io.github.nevalackin.client.util.math.MathUtil;
import io.github.nevalackin.client.util.movement.MovementUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;

import java.text.DecimalFormat;

public class Flight extends Module {

    private Vec3 startPos;
    private boolean jumped;
    private boolean clipped;
    private int ticksAboveGround;
    private DecimalFormat format = new DecimalFormat("0.0");
    private final EnumProperty<Mode> modeProperty = new EnumProperty<>("Mode", Mode.VANILLA);
    private final DoubleProperty speedProperty = new DoubleProperty("Speed", 1.0, this::isVanilla, 0.1, 10.0, 0.1);
    private final DoubleProperty vertMotionProperty = new DoubleProperty("Vertical Motion", 1.0, this::isVanilla, 0.1, 2.0, 0.1);


    private final BooleanProperty autoDisableProperty = new BooleanProperty("Automatically Disable", true, () -> !isVanilla());
    private final DoubleProperty groundTicksProperty = new DoubleProperty("Ticks OnGround Until Disable", 25.0, autoDisableProperty::getValue, 1.0, 50.0, 1.0);

    private final BooleanProperty timerProperty = new BooleanProperty("Timer", false);
    private final DoubleProperty timerSpeedProperty = new DoubleProperty("Timer Speed", 1.7, timerProperty::getValue, 0.1, 5.0, 0.1);

    public Flight() {
        super("Flight", Category.MOVEMENT, Category.SubCategory.MOVEMENT_EXTRAS);
        register(modeProperty, vertMotionProperty, speedProperty, autoDisableProperty, groundTicksProperty, timerProperty, timerSpeedProperty);
    }

    private boolean isVanilla() {
        return modeProperty.getValue() == Mode.VANILLA;
    }

    @EventLink
    private final Listener<GetTimerSpeedEvent> onTimerSpeed = event -> {
        if (timerProperty.getValue()) {
            if (isVanilla() || clipped) {
                event.setTimerSpeed(timerSpeedProperty.getValue().floatValue());
            }
        }
    };

    @EventLink
    private final Listener<ReceivePacketEvent> onReceive = event -> {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            ticksAboveGround = 0;
        }
    };

    @EventLink
    private final Listener<UpdatePositionEvent> onUpdatePosition = event -> {

        final double dffX = getDifference(startPos.xCoord, mc.thePlayer.posX),
                dffZ = getDifference(startPos.zCoord, mc.thePlayer.posZ),
                distXZ = Math.sqrt(dffX * dffX + dffZ * dffZ);

        if (event.isPre()) {
            switch (modeProperty.getValue()) {
                case VANILLA:
                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.thePlayer.motionY += vertMotionProperty.getValue();
                    } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                        mc.thePlayer.motionY -= vertMotionProperty.getValue();
                    } else {
                        mc.thePlayer.motionY = 0.0f;
                    }
                    break;
                case WATCHDOG:
                    if (!jumped && mc.thePlayer.onGround) {
                        mc.thePlayer.motionY = 0.075f;
                        jumped = true;
                        return;
                    }
                    if (mc.thePlayer.onGround && !clipped) {
                        event.posY -= 0.075f;
                        event.onGround = true;
                        clipped = true;
                    }
                    if (clipped) {
                        mc.thePlayer.motionY = 0.0;
                    }
                    if (!MovementUtil.isOverVoid(mc)) {
                        ticksAboveGround++;
                        if (ticksAboveGround >= groundTicksProperty.getValue()) {
                            setEnabled(false);
                            KetamineClient.getInstance().getNotificationManager().add(NotificationType.ERROR, getName(), "Disabled After Traveling " + format.format(distXZ) + " Blocks", 2500L);
                        }
                    }
                    break;
            }
        }
    };

    @EventLink
    private final Listener<MoveEvent> onMove = event -> {
        final double baseMoveSpeed = MovementUtil.getBaseMoveSpeed(mc.thePlayer);
        switch (modeProperty.getValue()) {
            case VANILLA:
                MovementUtil.setSpeed(mc.thePlayer, event, targetStrafeInstance, baseMoveSpeed * speedProperty.getValue());
                break;
            case WATCHDOG:
                MovementUtil.setSpeed(mc.thePlayer, event, targetStrafeInstance, baseMoveSpeed);
                break;
        }
    };

    private double getDifference(double base, double yaw) {
        final double bigger;
        if (base >= yaw) {
            bigger = base - yaw;
        } else {
            bigger = yaw - base;
        }
        return bigger;
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer != null) {
            startPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        }
        jumped = false;
        clipped = false;
        ticksAboveGround = 0;
    }

    @Override
    public void onDisable() {

    }

    private enum Mode {
        VANILLA("Vanilla"),
        WATCHDOG("Watchdog");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
