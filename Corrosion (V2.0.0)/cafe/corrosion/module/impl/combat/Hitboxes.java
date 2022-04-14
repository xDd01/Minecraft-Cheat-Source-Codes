/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.combat;

import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.NumberProperty;

@ModuleAttributes(name="Hitboxes", description="Expands the hitbox of nearby entities", category=Module.Category.COMBAT)
public class Hitboxes
extends Module {
    private final NumberProperty hitboxExpansion = new NumberProperty(this, "Expansion", 0.1, 0.1, 2.0, 0.1);

    public NumberProperty getHitboxExpansion() {
        return this.hitboxExpansion;
    }
}

