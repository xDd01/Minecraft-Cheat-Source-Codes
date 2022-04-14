package org.neverhook.client.feature.impl.movement;

import org.neverhook.client.event.EventTarget;
import org.neverhook.client.event.events.impl.player.EventUpdate;
import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;
import org.neverhook.client.settings.impl.NumberSetting;

public class Timer extends Feature {

    public NumberSetting timer;

    public Timer() {
        super("Timer", "Увеличивает скорость игры", Type.Movement);
        timer = new NumberSetting("Timer", 2.0F, 0.1F, 10.0F, 0.1F, () -> true);
        addSettings(timer);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!getState())
            return;
        this.setSuffix("" + timer.getNumberValue());
        mc.timer.timerSpeed = timer.getNumberValue();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.timer.timerSpeed = 1.0F;
    }
}
