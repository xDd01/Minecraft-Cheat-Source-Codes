package me.spec.eris.client.events.player;

import me.spec.eris.api.event.Event;
import net.minecraft.entity.player.EntityPlayer;

public class EventPlayerDeath extends Event {

    private EntityPlayer killedPlayer;

    public EventPlayerDeath(EntityPlayer killedPlayer) {
        this.killedPlayer = killedPlayer;
    }

    public EntityPlayer getKilledPlayer() {
        return killedPlayer;
    }

    public void setKilledPlayer(EntityPlayer killedPlayer) {
        this.killedPlayer = killedPlayer;
    }
}
