package org.neverhook.client.feature.impl.combat;

import org.neverhook.client.event.EventTarget;
import org.neverhook.client.event.events.impl.player.EventUpdate;
import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;
import org.neverhook.client.settings.impl.NumberSetting;

public class PushAttack extends Feature {

    private final NumberSetting clickCoolDown;

    public PushAttack() {
        super("PushAttack", "Позволяет бить на ЛКМ не смотря на использование предметов", Type.Combat);
        clickCoolDown = new NumberSetting("Click CoolDown", 1F, 0.5F, 1F, 0.1F, () -> true);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.player.getCooledAttackStrength(0) == clickCoolDown.getNumberValue() && mc.gameSettings.keyBindAttack.pressed) {
            mc.clickMouse();
        }
    }
}
