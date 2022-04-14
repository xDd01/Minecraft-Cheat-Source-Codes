/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.movement;

import cafe.corrosion.event.impl.EventJump;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.NumberProperty;

@ModuleAttributes(name="HighJump", description="Increases your jump height", category=Module.Category.MOVEMENT)
public class HighJump
extends Module {
    private final NumberProperty height = new NumberProperty(this, "Height", 0.5, 0.5, 8.5, 0.1);

    public HighJump() {
        this.registerEventHandler(EventJump.class, eventJump -> eventJump.setMotion(((Number)this.height.getValue()).floatValue()));
    }
}

