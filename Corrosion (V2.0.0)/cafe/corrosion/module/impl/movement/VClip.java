/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.movement;

import cafe.corrosion.Corrosion;
import cafe.corrosion.event.impl.EventUpdate;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.notification.Notification;
import cafe.corrosion.property.type.NumberProperty;
import java.text.DecimalFormat;

@ModuleAttributes(name="VClip", description="Moves you up vertically", category=Module.Category.MOVEMENT)
public class VClip
extends Module {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.#");
    private final NumberProperty height = new NumberProperty(this, "Height", 0.2, 0.2, 10, 0.2);

    public VClip() {
        this.registerEventHandler(EventUpdate.class, event -> {
            if (!event.isPre()) {
                return;
            }
            event.setY(event.getY() - ((Number)this.height.getValue()).doubleValue());
            VClip.mc.thePlayer.setPosition(event.getX(), event.getY(), event.getZ());
            Corrosion.INSTANCE.addNotification(new Notification("VClip Toggled", "Clipped " + DECIMAL_FORMAT.format(this.height.getValue()) + " blocks"));
            this.toggle();
        });
    }
}

