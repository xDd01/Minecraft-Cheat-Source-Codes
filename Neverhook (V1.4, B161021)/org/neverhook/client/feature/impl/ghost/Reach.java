package org.neverhook.client.feature.impl.ghost;

import org.neverhook.client.event.EventTarget;
import org.neverhook.client.event.events.impl.player.EventUpdate;
import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;
import org.neverhook.client.helpers.math.MathematicHelper;
import org.neverhook.client.settings.impl.NumberSetting;

public class Reach extends Feature {

    public static NumberSetting reachValue;

    public Reach() {
        super("Reach", "Увеличивает дистанцию удара", Type.Ghost);
        reachValue = new NumberSetting("Expand", 3.2F, 3, 5, 0.1F, () -> true);
        addSettings(reachValue);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        this.setSuffix("" + MathematicHelper.round(reachValue.getNumberValue(), 1));
    }
}