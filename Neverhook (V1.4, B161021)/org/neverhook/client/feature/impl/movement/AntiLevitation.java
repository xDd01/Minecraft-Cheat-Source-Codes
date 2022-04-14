package org.neverhook.client.feature.impl.movement;

import net.minecraft.init.MobEffects;
import org.neverhook.client.event.EventTarget;
import org.neverhook.client.event.events.impl.player.EventUpdate;
import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;

public class AntiLevitation extends Feature {

    public AntiLevitation() {
        super("AntiLevitation", "Убирает эффект левитации", Type.Player);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.player.isPotionActive(MobEffects.LEVITATION)) {
            mc.player.removeActivePotionEffect(MobEffects.LEVITATION);
        }
    }
}