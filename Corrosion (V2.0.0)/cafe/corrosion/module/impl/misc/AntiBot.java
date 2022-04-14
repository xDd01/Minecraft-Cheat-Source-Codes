/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.misc;

import cafe.corrosion.event.impl.EventPacketIn;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.EnumProperty;
import cafe.corrosion.util.nameable.INameable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S38PacketPlayerListItem;

@ModuleAttributes(name="AntiBot", description="are you black?", category=Module.Category.MISC)
public class AntiBot
extends Module {
    private final List<UUID> badUUIDs = new ArrayList<UUID>();
    private final List<Integer> badIDs = new ArrayList<Integer>();
    public EnumProperty<EnumMode> enumProperty = new EnumProperty((Module)this, "Mode", (INameable[])EnumMode.values());

    public AntiBot() {
        this.registerEventHandler(EventPacketIn.class, eventPacketIn -> {
            switch ((EnumMode)this.enumProperty.getValue()) {
                case MINEPLEX: {
                    S0CPacketSpawnPlayer packetSpawnPlayer;
                    if (!(eventPacketIn.getPacket() instanceof S0CPacketSpawnPlayer) || (packetSpawnPlayer = (S0CPacketSpawnPlayer)eventPacketIn.getPacket()).func_148944_c().size() == 9) break;
                    this.badIDs.add(packetSpawnPlayer.getEntityID());
                    break;
                }
                case MATRIX: {
                    if (!(eventPacketIn.getPacket() instanceof S38PacketPlayerListItem)) break;
                    S38PacketPlayerListItem packetPlayerListItem = (S38PacketPlayerListItem)eventPacketIn.getPacket();
                    for (S38PacketPlayerListItem.AddPlayerData playerData : packetPlayerListItem.func_179767_a()) {
                        if (packetPlayerListItem.func_179768_b() != S38PacketPlayerListItem.Action.ADD_PLAYER) continue;
                        NetworkPlayerInfo networkplayerinfo = new NetworkPlayerInfo(playerData);
                        if (!mc.getNetHandler().getPlayerInfoMap().stream().anyMatch(i2 -> i2.getGameProfile().getName().equals(networkplayerinfo.getGameProfile().getName()))) continue;
                        this.badUUIDs.add(networkplayerinfo.getGameProfile().getId());
                        eventPacketIn.setCancelled(true);
                    }
                    break;
                }
            }
        });
    }

    @Override
    public void onDisable() {
        this.badUUIDs.clear();
    }

    public List<UUID> getBadUUIDs() {
        return this.badUUIDs;
    }

    public boolean isBad(Entity entity) {
        return this.badIDs.contains(entity.getEntityId()) || this.badUUIDs.contains(entity.getUniqueID());
    }

    @Override
    public String getMode() {
        return ((EnumMode)this.enumProperty.getValue()).getName();
    }

    public static enum EnumMode implements INameable
    {
        MINEPLEX("Mineplex"),
        MATRIX("Matrix");

        private final String name;

        @Override
        public String getName() {
            return this.name;
        }

        private EnumMode(String name) {
            this.name = name;
        }
    }
}

