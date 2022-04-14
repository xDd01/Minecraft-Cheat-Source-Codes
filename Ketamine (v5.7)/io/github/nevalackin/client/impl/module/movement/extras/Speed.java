package io.github.nevalackin.client.impl.module.movement.extras;

import io.github.nevalackin.client.api.module.Category;
import io.github.nevalackin.client.api.module.Module;
import io.github.nevalackin.client.api.notification.NotificationType;
import io.github.nevalackin.client.impl.core.KetamineClient;
import io.github.nevalackin.client.impl.event.packet.ReceivePacketEvent;
import io.github.nevalackin.client.impl.event.player.MoveEvent;
import io.github.nevalackin.client.impl.event.player.UpdatePositionEvent;
import io.github.nevalackin.client.impl.event.render.overlay.RenderGameOverlayEvent;
import io.github.nevalackin.client.impl.module.combat.healing.AutoPot;
import io.github.nevalackin.client.impl.module.combat.rage.Aura;
import io.github.nevalackin.client.impl.module.misc.world.Scaffold;
import io.github.nevalackin.client.impl.property.BooleanProperty;
import io.github.nevalackin.client.impl.property.DoubleProperty;
import io.github.nevalackin.client.impl.property.EnumProperty;
import io.github.nevalackin.client.util.math.MathUtil;
import io.github.nevalackin.client.util.misc.ServerUtil;
import io.github.nevalackin.client.util.movement.FrictionUtil;
import io.github.nevalackin.client.util.movement.JumpUtil;
import io.github.nevalackin.client.util.movement.MovementUtil;
import io.github.nevalackin.client.util.player.RotationUtil;
import io.github.nevalackin.client.util.render.BlurUtil;
import io.github.nevalackin.client.util.render.ColourUtil;
import io.github.nevalackin.client.util.render.DrawUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.Timer;
import java.util.TimerTask;

public final class Speed extends Module {

    private static final double[] LOW_HOP_Y_POSITIONS = {
            MathUtil.round(0.4, 0.001),
            MathUtil.round(0.71, 0.001),
            MathUtil.round(0.75, 0.001),
            MathUtil.round(0.55, 0.001),
            MathUtil.round(0.41, 0.001)
    };

    private final EnumProperty<Mode> modeProperty = new EnumProperty<>("Mode", Mode.WATCHDOG);
    private final DoubleProperty vanillaSpeedProperty = new DoubleProperty("Vanilla Speed", 1.0, this::isVanilla, 0.1, 2.0, 0.1);
    private final EnumProperty<Friction> frictionProperty = new EnumProperty<>("Friction", Friction.NCP, () -> !isVanilla());
    private final BooleanProperty lagbackFixProperty = new BooleanProperty("Lagback Fix", true, () -> !isVanilla() && ServerUtil.isHypixel());
    private final DoubleProperty smoothingProperty = new DoubleProperty("Smoothing", 10.0, () -> lagbackFixProperty.getValue() && ServerUtil.isHypixel(), 1, 100.0, 1);
    private final BooleanProperty lowHopProperty = new BooleanProperty("Low Hop", true, () -> !isVanilla());

    private double moveSpeed;
    private double lastDist;
    private boolean wasOnGround;

    private boolean wasInitialLowHop;

    private boolean disable;

    private long timeOfFlag;
    private final Timer turnBackOnTimer = new Timer();
    private final long turnBackOnDelay = 2500L;

    private Aura aura;
    private AutoPot autopot;
    private Scaffold scaffold;

    public Speed() {
        super("Speed", Category.MOVEMENT, Category.SubCategory.MOVEMENT_EXTRAS);
        setSuffix(() -> modeProperty.getValue().toString());
        register(modeProperty, frictionProperty, lagbackFixProperty, smoothingProperty, vanillaSpeedProperty, lowHopProperty);
    }

    private boolean isVanilla() {
        return modeProperty.getValue() == Mode.VANILLA;
    }

    @EventLink
    private final Listener<UpdatePositionEvent> onUpdatePosition = event -> {
        if (!disable && event.isPre() && !isVanilla()) {
            final double xDist = event.getLastTickPosX() - event.getPosX();
            final double zDist = event.getLastTickPosZ() - event.getPosZ();
            lastDist = Math.sqrt(xDist * xDist + zDist * zDist);

            if (lagbackFixProperty.getValue() && ServerUtil.isHypixel()) {
                if (scaffold.isEnabled() || autopot.potting) return;
                float[] rotations = new float[]{MovementUtil.getMovementDirection(mc.thePlayer.moveForward, mc.thePlayer.moveStrafing, mc.thePlayer.rotationYaw), event.getPitch()};
                RotationUtil.applySmoothing(new float[]{event.getLastTickYaw(), event.getLastTickPitch()}, smoothingProperty.getValue().floatValue(), rotations);
                event.setYaw(rotations[0]);
            }
        }
    };

    /*private double calculateBoostModifier() {
        return scaffold.isEnabled() ? MovementUtil.SPRINTING_MOD : 1.7;
    }*/

   /*private void runNCPMotionSim(final double[] motion,
                                 final double baseMoveSpeed,
                                 final double lastDist,
                                 final double yDistFromGround,
                                 final int nthTick) {
        switch (nthTick) {
            case 0:
                MovementUtil.setMotion(mc.thePlayer, motion, calculateBoostModifier() * baseMoveSpeed);
                motion[1] = MovementUtil.getBaseMoveSpeed(mc.thePlayer);
                break;
            case 1:
                final double bunnySlope = lowHopProperty.getValue() ? 0.84f * (lastDist - baseMoveSpeed) : 0.81f * (lastDist - baseMoveSpeed);
                MovementUtil.setMotion(mc.thePlayer, motion, lastDist - bunnySlope);
                break;
            default:
                final double speed = Math.sqrt(motion[0] * motion[0] + motion[1] * motion[1]);
                MovementUtil.setMotion(mc.thePlayer, motion, speed);
                break;
        }
        // Apply friction
        motion[0] *= 0.91F;
        motion[2] *= 0.91F;
    }*/

    private boolean simJumpShouldDoLowHop(final double baseMoveSpeedRef) {
        // Calculate the direction moved in
        final float direction = RotationUtil.calculateYawFromSrcToDst(mc.thePlayer.rotationYaw,
                mc.thePlayer.lastReportedPosX, mc.thePlayer.lastReportedPosZ,
                mc.thePlayer.posX, mc.thePlayer.posZ);
        final Vec3 start = new Vec3(mc.thePlayer.posX,
                mc.thePlayer.posY + LOW_HOP_Y_POSITIONS[2],
                mc.thePlayer.posZ);
        // Cast a ray at waist height in the direction moved in for 10 blocks
        final MovingObjectPosition rayTrace = mc.theWorld.rayTraceBlocks(start,
                RotationUtil.getDstVec(start, direction, 0.0F, 8),
                false, true, true);
        // If did not hit anything just continue
        if (rayTrace == null) return true;
        if (rayTrace.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
            return true;
        if (rayTrace.hitVec == null) return true;

        // Check if player can fit above
        final AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox();
        if (mc.theWorld.checkBlockCollision(
                bb.offset(bb.minX - rayTrace.hitVec.xCoord,
                        bb.minY - rayTrace.hitVec.yCoord,
                        bb.minZ - rayTrace.hitVec.zCoord)))
            return false;

        // Distance to the block hit
        final double dist = start.distanceTo(rayTrace.hitVec);
        final double normalJumpDist = 4.0;
        return dist > normalJumpDist;
    }

    @EventLink
    private final Listener<MoveEvent> onMove = event -> {
        if (!disable && MovementUtil.isMoving(mc.thePlayer)) {
            final double baseMoveSpeed = MovementUtil.getBaseMoveSpeed(mc.thePlayer);

            boolean doInitialLowHop = lowHopProperty.getValue() &&
                    !mc.gameSettings.keyBindJump.isKeyDown() &&
                    !mc.thePlayer.isPotionActive(Potion.jump) &&
                    !scaffold.isEnabled() && !mc.thePlayer.isCollidedHorizontally &&
                    simJumpShouldDoLowHop(baseMoveSpeed);

            switch (modeProperty.getValue()) {
                case WATCHDOG:
                    if (!mc.thePlayer.onGround && wasInitialLowHop && mc.thePlayer.fallDistance < 0.54)
                        event.setY(mc.thePlayer.motionY = lowHopYModification(mc.thePlayer.motionY, MathUtil.round(mc.thePlayer.posY - (int) mc.thePlayer.posY, 0.001)));

                    if (mc.thePlayer.onGround && !wasOnGround) {
                        moveSpeed = baseMoveSpeed * 1.7;
                        wasInitialLowHop = doInitialLowHop;
                        event.setY(mc.thePlayer.motionY = doInitialLowHop ? 0.4F : JumpUtil.getJumpHeight(mc.thePlayer));
                        wasOnGround = true;
                    } else if (wasOnGround) {
                        wasOnGround = false;
                        final double bunnySlope = 0.66 * (lastDist - baseMoveSpeed);
                        moveSpeed = lastDist - bunnySlope;
                    } else {
                        moveSpeed = frictionProperty.getValue().getFriction().applyFriction(mc.thePlayer, moveSpeed, lastDist, baseMoveSpeed);
                    }
                    moveSpeed = Math.max(moveSpeed, baseMoveSpeed);
                    MovementUtil.setSpeed(mc.thePlayer, event, targetStrafeInstance, moveSpeed);
                    break;
                case VANILLA:
                    MovementUtil.setSpeed(mc.thePlayer, event, targetStrafeInstance, vanillaSpeedProperty.getValue());
                    break;
            }
        }
    };

    @EventLink
    private final Listener<RenderGameOverlayEvent> onRenderOverlay = event -> {
        final ScaledResolution scaledResolution = event.getScaledResolution();

        if (disable) {
            final long currentMillis = System.currentTimeMillis();
            final long timeSinceFlagged = currentMillis - timeOfFlag;

            if (timeSinceFlagged < turnBackOnDelay) {
                final double xRegionBuffer = 4;
                final double yRegionBuffer = 2;

                final String text = String.format("Re-enabling in %.1f seconds", (float) (turnBackOnDelay - timeSinceFlagged) / 1000L);

                final double width = mc.fontRendererObj.getStringWidth(text) + xRegionBuffer * 2;
                final double height = 20 + yRegionBuffer * 2;

                final double barHeight = height - yRegionBuffer * 3 - 12;

                final double centrePosX = scaledResolution.getScaledWidth() / 2.0;
                final double centrePosY = scaledResolution.getScaledHeight() / 2.0 + 100;

                final double progress = (double) timeSinceFlagged / turnBackOnDelay;

                final double leftBackground = centrePosX - width / 2.0;
                final double rightBackground = centrePosX + width / 2.0;

                BlurUtil.blurArea(leftBackground, centrePosY, width, height);
                DrawUtil.glDrawFilledQuad(leftBackground, centrePosY, width, height, 0x80000000);

                DrawUtil.glDrawGradientLine(leftBackground, centrePosY, rightBackground, centrePosY,
                        1, ColourUtil.getClientColour());


                final double left = centrePosX - (width - xRegionBuffer * 2) / 2.0;
                final double top = centrePosY + height - barHeight - yRegionBuffer;

                DrawUtil.glDrawFilledQuad(left, top,
                        width - xRegionBuffer * 2,
                        barHeight,
                        0x80000000);

                DrawUtil.glDrawSidewaysGradientRect(left, top,
                        (width - xRegionBuffer * 2) * progress,
                        barHeight,
                        ColourUtil.fadeBetween(ColourUtil.getClientColour(), ColourUtil.getSecondaryColour()), ColourUtil.fadeBetween(ColourUtil.getSecondaryColour(), ColourUtil.getClientColour()));

                mc.fontRendererObj.drawStringWithShadow(text, (float) left, (float) centrePosY + 4, 0xFFFFFFFF);
            }
        }
    };
    @EventLink
    private final Listener<ReceivePacketEvent> onReceivePacket = event -> {
        if (event.getPacket() instanceof S08PacketPlayerPosLook && !disable) {
            timeOfFlag = System.currentTimeMillis();
            disable = true;

            KetamineClient.getInstance().getNotificationManager().add(NotificationType.ERROR, "Flagged",
                    String.format("Flag detected re-enabling Speed in %.0fs", turnBackOnDelay / 1000.f),
                    turnBackOnDelay);

            turnBackOnTimer.purge();

            turnBackOnTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    reset();
                }
            }, turnBackOnDelay);
        }
    };

    @Override
    public void onEnable() {
        if (scaffold == null) {
            scaffold = KetamineClient.getInstance().getModuleManager().getModule(Scaffold.class);
        }
        if (aura == null) {
            aura = KetamineClient.getInstance().getModuleManager().getModule(Aura.class);
        }
        if (autopot == null) {
            autopot = KetamineClient.getInstance().getModuleManager().getModule(AutoPot.class);
        }
        reset();
    }

    private void reset() {
        wasInitialLowHop = false;
        moveSpeed = MovementUtil.WALK_SPEED;
        lastDist = 0.0;
        disable = false;
    }

    @Override
    public void onDisable() {

    }

    private double lowHopYModification(final double baseMotionY,
                                       final double yDistFromGround) {
        if (yDistFromGround == LOW_HOP_Y_POSITIONS[0]) {
            return 0.31;
        } else if (yDistFromGround == LOW_HOP_Y_POSITIONS[1]) {
            return 0.04;
        } else if (yDistFromGround == LOW_HOP_Y_POSITIONS[2]) {
            return -0.2;
        } else if (yDistFromGround == LOW_HOP_Y_POSITIONS[3]) {
            return -0.14;
        } else if (yDistFromGround == LOW_HOP_Y_POSITIONS[4]) {
            return -0.2;
        }

        return baseMotionY;
    }


    private enum Friction {
        VANILLA("Vanilla", FrictionUtil::applyVanillaFriction),
        NCP("NCP", FrictionUtil::applyNCPFriction);

        private final String name;
        private final FrictionUtil.Friction friction;

        Friction(String name, FrictionUtil.Friction friction) {
            this.name = name;
            this.friction = friction;
        }

        @Override
        public String toString() {
            return name;
        }

        public FrictionUtil.Friction getFriction() {
            return friction;
        }
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
