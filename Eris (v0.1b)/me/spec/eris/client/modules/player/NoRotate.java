package me.spec.eris.client.modules.player;
import me.spec.eris.api.event.Event;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.client.events.client.EventPacket;
import me.spec.eris.api.module.Module;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class NoRotate extends Module {

    public NoRotate(String racism) {
        super("NoRotate", ModuleCategory.PLAYER, racism);
    } 
     
    @Override
    public void onEvent(Event e) {  
    	if (e instanceof EventPacket) {
    		EventPacket event = (EventPacket)e;
    		
    		if (event.isReceiving()) {
    			if (event.getPacket() instanceof S08PacketPlayerPosLook) {
    				S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook)event.getPacket();
    				packet.setPitch(mc.thePlayer.rotationPitch);
    				packet.setYaw(mc.thePlayer.rotationYaw);
    			}
    		}
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