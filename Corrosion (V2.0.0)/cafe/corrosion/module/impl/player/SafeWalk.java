/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.player;

import cafe.corrosion.event.impl.EventSneak;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;

@ModuleAttributes(name="SafeWalk", category=Module.Category.PLAYER, description="Prevents you from walking off the edge of blocks")
public class SafeWalk
extends Module {
    public SafeWalk() {
        this.registerEventHandler(EventSneak.class, event -> event.setForcingSlowDown(true));
    }
}

