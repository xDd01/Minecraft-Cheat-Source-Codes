package io.github.nevalackin.client.impl.module.movement.extras;

import io.github.nevalackin.client.api.module.Category;
import io.github.nevalackin.client.api.module.Module;
import io.github.nevalackin.client.impl.event.game.GetTimerSpeedEvent;
import io.github.nevalackin.client.impl.event.player.MoveEvent;
import io.github.nevalackin.client.impl.event.player.UpdatePositionEvent;
import io.github.nevalackin.client.impl.property.BooleanProperty;
import io.github.nevalackin.client.impl.property.DoubleProperty;
import io.github.nevalackin.client.util.movement.FrictionUtil;
import io.github.nevalackin.client.util.movement.JumpUtil;
import io.github.nevalackin.client.util.movement.MovementUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;


public final class LongJump extends Module {

    private double moveSpeed;
    private double lastDist;
    private int groundTicks;
    private boolean wasOnGround;

    private final BooleanProperty hideHopProperty = new BooleanProperty("Hide Jump", true);
    private final BooleanProperty timerProperty = new BooleanProperty("Timer", false);
    private final DoubleProperty timerSpeedProperty = new DoubleProperty("Timer Boost", 1.6, timerProperty::getValue, 1, 2.0, 0.01);

    public LongJump() {
        super("Long Jump", Category.MOVEMENT, Category.SubCategory.MOVEMENT_EXTRAS);
        register(timerSpeedProperty, hideHopProperty);
    }

    @EventLink
    private final Listener<GetTimerSpeedEvent> onTimerSpeed = event -> {
        event.setTimerSpeed(timerSpeedProperty.getValue());
    };


    @EventLink
    private final Listener<UpdatePositionEvent> onUpdate = event -> {
        if (event.isPre()) {
            final double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            final double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
            lastDist = Math.sqrt(xDist * xDist + zDist * zDist);

            if (hideHopProperty.getValue()) {
                mc.thePlayer.posY -= mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
                mc.thePlayer.lastTickPosY -= mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
            }

            if (mc.thePlayer.onGround && ++groundTicks >= 1) {
                toggle();
            }
        }
    };

    @EventLink
    private final Listener<MoveEvent> onMove = event -> {
        final double baseMoveSpeed = MovementUtil.getBaseMoveSpeed(mc.thePlayer);
        if (mc.thePlayer.onGround && !wasOnGround) {
            moveSpeed = baseMoveSpeed * 2.149;
            MovementUtil.hypixelDamage(mc.thePlayer);
            event.setY(mc.thePlayer.motionY = 0.84);
            wasOnGround = true;
        }
        else if (wasOnGround) {
            wasOnGround = false;
            final double bunnySlope = 0.66 * (lastDist - baseMoveSpeed);
            moveSpeed = lastDist - bunnySlope;
        } else {
            moveSpeed = FrictionUtil.applyNCPFriction(mc.thePlayer, moveSpeed, lastDist, baseMoveSpeed);
        }
        moveSpeed = Math.max(moveSpeed, baseMoveSpeed);
        MovementUtil.setSpeed(mc.thePlayer, event, targetStrafeInstance, moveSpeed);
    };

    @Override
    public void onEnable() {
        wasOnGround = false;
        groundTicks = 0;
        moveSpeed = MovementUtil.getBaseMoveSpeed(this.mc.thePlayer);
        lastDist = 0.0;
    }

    @Override
    public void onDisable() {

    }
}