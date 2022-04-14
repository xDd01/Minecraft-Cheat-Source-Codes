/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.visual;

import cafe.corrosion.event.impl.EventPacketIn;
import cafe.corrosion.event.impl.EventUpdate;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.NumberProperty;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

@ModuleAttributes(name="TimeChanger", description="Modifies your in-game time", category=Module.Category.VISUAL)
public class TimeChanger
extends Module {
    private final NumberProperty time = new NumberProperty(this, "Time", 0, 0, 24000, 100);

    public TimeChanger() {
        this.registerEventHandler(EventPacketIn.class, eventPacketIn -> {
            if (eventPacketIn.getPacket() instanceof S03PacketTimeUpdate) {
                eventPacketIn.setCancelled(true);
            }
        });
        this.registerEventHandler(EventUpdate.class, eventUpdate -> TimeChanger.mc.theWorld.setWorldTime(((Number)this.time.getValue()).longValue()));
    }
}

