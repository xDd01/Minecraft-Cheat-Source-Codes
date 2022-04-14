package org.neverhook.client.feature.impl.ghost;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import org.neverhook.client.event.EventTarget;
import org.neverhook.client.event.events.impl.player.EventPreMotion;
import org.neverhook.client.event.events.impl.player.EventUpdate;
import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;
import org.neverhook.client.helpers.math.MathematicHelper;
import org.neverhook.client.settings.impl.NumberSetting;

public class HitBoxes extends Feature {

    public static NumberSetting expand;

    public HitBoxes() {
        super("HitBoxes", "Увеличивает хитбокс у ентити", Type.Ghost);
        expand = new NumberSetting("Expand", 0.2F, 0.01F, 2.0F, 0.01F, () -> true);
        addSettings(expand);
    }

    @EventTarget
    public void onUpdate(EventPreMotion event) {
        this.setSuffix("" + MathematicHelper.round(expand.getNumberValue(), 2));

        for (Entity entity : mc.world.playerEntities) {
            if(entity != mc.player){
                float width = entity.width;
                float height = entity.height;
                float expandValue = expand.getNumberValue();
                entity.setEntityBoundingBox(new AxisAlignedBB(entity.posX - width - expandValue, entity.posY, entity.posZ + width + expandValue, entity.posX + width + expandValue, entity.posY + height + expandValue, entity.posZ - width - expandValue));
            }
        }
    }
}
