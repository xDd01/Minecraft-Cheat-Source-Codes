package me.spec.eris.client.modules.client;

import java.util.LinkedList;
import java.util.List;

import me.spec.eris.api.event.Event;
import me.spec.eris.api.module.Module;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.api.value.types.ModeValue;
import me.spec.eris.client.events.client.EventPacket;

import me.spec.eris.utils.math.MathUtils;
import me.spec.eris.utils.world.TimerUtils;

import net.minecraft.network.Packet;

import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;

public class Disabler extends Module {

	public Disabler(String racism) {
        super("Disabler", ModuleCategory.MISC, racism);
    }
    private final List<Packet> packets = new LinkedList<>();
    private ModeValue<Mode> mode = new ModeValue<Mode>("Mode", Mode.WATCHDOG, this);
    private TimerUtils timer = new TimerUtils();
    private enum Mode {WATCHDOG}
    public boolean needs;
    private int random;

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventPacket) {
        	EventPacket event = (EventPacket)e;
            setMode(mode.getValue().toString());
        	if (event.isReceiving()) {
        		switch (mode.getValue()) {
        			case WATCHDOG:
 
					break;
        		}
        	}
        	if (event.isSending()) {
        		switch (mode.getValue()) {
    			case WATCHDOG:
    				if (event.getPacket() instanceof C03PacketPlayer && needs) {
    					//Invalidated on join
						mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C00PacketKeepAlive(-1));
						mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C0FPacketConfirmTransaction(Integer.MAX_VALUE, Short.MIN_VALUE, true));
						needs = false;
					}
					if (event.getPacket() instanceof C00PacketKeepAlive || event.getPacket() instanceof C13PacketPlayerAbilities || event.getPacket() instanceof C0FPacketConfirmTransaction) {
						packets.add(event.getPacket());
						event.setCancelled();
					}
					if (timer.hasReached(random)) {
						packets.forEach(mc.thePlayer.sendQueue::addToSendQueueNoEvent);
						packets.clear();
						random = MathUtils.getRandomInRange(10000, 30000);
					}
					break;
        		}
        	}
        }
    }

    @Override
    public void onEnable() {
    	random = MathUtils.getRandomInRange(10000, 30000);
        super.onEnable();
    }

    @Override
    public void onDisable() { 
        super.onDisable();
    }
}
