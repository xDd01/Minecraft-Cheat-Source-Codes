package me.spec.eris.client.modules.movement;

import me.spec.eris.Eris;
import me.spec.eris.api.event.Event;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.client.events.client.EventPacket;
import me.spec.eris.client.events.player.EventPlayerSlow;
import me.spec.eris.client.events.player.EventUpdate;
import me.spec.eris.api.module.Module;
import me.spec.eris.client.modules.combat.Killaura;
import me.spec.eris.api.value.types.ModeValue;
import me.spec.eris.utils.player.PlayerUtils;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoSlowDown extends Module {

    public NoSlowDown(String racism) {
        super("NoSlowDown", ModuleCategory.MOVEMENT, racism);
    }

    private ModeValue<Mode> mode = new ModeValue<Mode>("Mode", Mode.WATCHDOG, this);
    private enum Mode {WATCHDOG}
    private boolean blocking;

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventPacket) { 
        	EventPacket event = (EventPacket)e;
        	
        	if (event.isSending() && PlayerUtils.isHoldingSword()) {
        		if (event.getPacket() instanceof C08PacketPlayerBlockPlacement) blocking = true;
        		if (event.getPacket() instanceof C07PacketPlayerDigging) blocking = false;
        	}
        }
        if (e instanceof EventUpdate) { 
        	switch (mode.getValue()) {
        		case WATCHDOG:
                	EventUpdate event = (EventUpdate)e;
                	Killaura aura = (Killaura) Eris.getInstance().moduleManager.getModuleByClass(Killaura.class);
                	if (aura.getTarget() != null || !PlayerUtils.isHoldingSword()) return;
                	if (!mc.thePlayer.isUsingItem()) {
                        if (blocking) mc.getNetHandler().addToSendQueueNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-.8f, -.8f, -.8f), EnumFacing.DOWN));
                        
                        return;
                	}
                	if (event.isPre()) {
                      if (blocking) mc.getNetHandler().addToSendQueueNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-.8f, -.8f, -.8f), EnumFacing.DOWN));
                	} else {
                		if (!blocking) mc.getNetHandler().addToSendQueueNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0, 0, 0));
                	}
				break; 
        	}
        }
        if (e instanceof EventPlayerSlow) {
        	((EventPlayerSlow) e).setSpeed(1);
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() { 
        super.onDisable();
    }
}
