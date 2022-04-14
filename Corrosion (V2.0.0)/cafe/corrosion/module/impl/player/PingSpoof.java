/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.player;

import cafe.corrosion.event.impl.EventPacketOut;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.BooleanProperty;
import cafe.corrosion.property.type.NumberProperty;
import cafe.corrosion.util.packet.PacketUtil;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

@ModuleAttributes(name="PingSpoof", description="Makes your ping increase server-sided", category=Module.Category.PLAYER)
public class PingSpoof
extends Module {
    private final NumberProperty delay = new NumberProperty(this, "Delay", 50, 50, 12500, 50);
    private final BooleanProperty keepalives = new BooleanProperty((Module)this, "KeepAlive", false);
    private final BooleanProperty transactions = new BooleanProperty((Module)this, "Transactions", false);
    private final BooleanProperty flyings = new BooleanProperty((Module)this, "Movements", false);

    public PingSpoof() {
        this.registerEventHandler(EventPacketOut.class, event -> {
            Object packet = event.getPacket();
            if (packet instanceof C00PacketKeepAlive && (Boolean)this.keepalives.getValue() != false || packet instanceof C0FPacketConfirmTransaction && (Boolean)this.transactions.getValue() != false || packet instanceof C03PacketPlayer && ((Boolean)this.flyings.getValue()).booleanValue()) {
                event.setCancelled(true);
                PacketUtil.sendPacketDelayed(event.getPacket(), ((Number)this.delay.getValue()).longValue());
            }
        });
    }
}

