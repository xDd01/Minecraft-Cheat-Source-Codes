package org.neverhook.client.feature.impl.combat;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBow;
import org.neverhook.client.event.EventTarget;
import org.neverhook.client.event.events.impl.player.EventPreMotion;
import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;
import org.neverhook.client.helpers.math.RotationHelper;

public class AimBot extends Feature {

    public AimBot() {
        super("AimBot", "", Type.Combat);
    }

    @EventTarget
    public void onPreMotion(EventPreMotion event) {
        for (Entity entity : mc.world.loadedEntityList) {
            if (mc.player.getHeldItemMainhand().getItem() instanceof ItemBow && mc.player.getDistanceToEntity(entity) < 6 && entity != null) {
                float[] rots = RotationHelper.getRotations(entity, false, 360, 360, 2, 2);
                event.setYaw(rots[0]);
                mc.player.rotationYawHead = rots[0];
                mc.player.renderYawOffset = rots[0];
            }
        }
    }
}
