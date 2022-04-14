package me.spec.eris.client.modules.player;

import me.spec.eris.Eris;
import me.spec.eris.api.event.Event;
import me.spec.eris.client.events.client.EventPacket;
import me.spec.eris.client.events.player.EventMove;
import me.spec.eris.client.events.player.EventUpdate;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.api.module.Module;
import me.spec.eris.client.modules.combat.Criticals;
import me.spec.eris.client.modules.combat.Killaura;
import me.spec.eris.client.modules.movement.Longjump;
import me.spec.eris.api.value.types.ModeValue;
import me.spec.eris.client.modules.movement.Speed;
import net.minecraft.network.play.client.C03PacketPlayer;

public class NoFall extends Module {

	public NoFall(String racism) {
		super("NoFall", ModuleCategory.PLAYER, racism);
    } 
    private ModeValue<Mode> mode = new ModeValue<Mode>("Mode", Mode.WATCHDOG, this);
    private enum Mode {EDIT, WATCHDOG}
    
    private boolean fallen;
    
    @Override
    public void onEvent(Event e) {
    	if (e instanceof EventPacket && ((EventPacket) e).getPacket() instanceof C03PacketPlayer) {
    		if (mode.getValue().equals(Mode.EDIT)) {
				(((C03PacketPlayer) ((EventPacket) e).getPacket())).onGround = true;
			}
		}
        if (e instanceof EventUpdate) {
			EventUpdate event = (EventUpdate)e;
            setMode(mode.getValue().toString());
    		switch (mode.getValue()) {
				case WATCHDOG:
					if (fallen && mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround) {
						Speed speed = ((Speed)Eris.getInstance().moduleManager.getModuleByClass(Speed.class));
						mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
						speed.hops = -1;
						fallen = false;
					}
				break;
    		}
        } 
        if (e instanceof EventMove) {
    		switch (mode.getValue()) {
				case WATCHDOG: 
		        	if (AntiVoid.isBlockUnder()) {
						if (mc.thePlayer.fallDistance > 2.0) {
							mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.serverSideYaw, mc.thePlayer.serverSidePitch, true));
							Killaura aura = ((Killaura)Eris.getInstance().moduleManager.getModuleByClass(Killaura.class));
							Criticals crits = ((Criticals)Eris.getInstance().moduleManager.getModuleByClass(Criticals.class));
							mc.thePlayer.fallDistance = 0;
							crits.accumulatedFall = 0;
							aura.fuckCheckVLs = true;
							fallen = true;
						}
		        	}
				break;
    		}
        }
    }

    @Override
    public void onEnable() {  
    	fallen = false;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}