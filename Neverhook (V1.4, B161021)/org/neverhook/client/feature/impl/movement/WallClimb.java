package org.neverhook.client.feature.impl.movement;

import org.neverhook.client.event.EventTarget;
import org.neverhook.client.event.events.impl.player.EventPreMotion;
import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;
import org.neverhook.client.helpers.misc.TimerHelper;
import org.neverhook.client.helpers.player.MovementHelper;
import org.neverhook.client.settings.impl.NumberSetting;

public class WallClimb extends Feature {

    public static NumberSetting climbTicks;
    private final TimerHelper timerHelper = new TimerHelper();

    public WallClimb() {
        super("WallClimb", "Автоматически взберается на стены", Type.Movement);
        climbTicks = new NumberSetting("Climb Ticks", 1, 0, 5, 0.1F, () -> true);
        addSettings(climbTicks);
    }

    @EventTarget
    public void onPreMotion(EventPreMotion event) {
        this.setSuffix("" + climbTicks.getNumberValue());
        if (MovementHelper.isMoving() && mc.player.isCollidedHorizontally) {
            if (timerHelper.hasReached(climbTicks.getNumberValue() * 100)) {
                event.setOnGround(true);
                mc.player.onGround = true;
                mc.player.isCollidedVertically = true;
                mc.player.isCollidedHorizontally = true;
                mc.player.isAirBorne = true;
                mc.player.jump();
                timerHelper.reset();
            }
        }
    }
}
