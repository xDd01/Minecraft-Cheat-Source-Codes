package me.spec.eris.client.modules.player;

import me.spec.eris.api.event.Event;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.client.events.client.EventPacket;
import me.spec.eris.client.events.player.EventBlockPush;
import me.spec.eris.client.events.player.EventBoundingBox;
import me.spec.eris.client.events.player.EventMove;
import me.spec.eris.client.events.player.EventUpdate;
import me.spec.eris.api.module.Module;
import me.spec.eris.api.value.types.ModeValue;
import me.spec.eris.utils.math.MathUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;

public class Phase extends Module {

	public Phase(String racism) {
		super("Phase", ModuleCategory.PLAYER, racism);
    } 
    
    private ModeValue<Mode> mode = new ModeValue<Mode>("Mode", Mode.WATCHDOG, this);
    public enum Mode {WATCHDOG}
    private int counter;
    @Override
    public void onEvent(Event e) {  
    	if (e instanceof EventBoundingBox) {
    		EventBoundingBox event = (EventBoundingBox)e;
    		switch (mode.getValue()) {
				case WATCHDOG:
	    			if (mc.thePlayer.isSneaking() && isInsideBlock()) {
	    				event.setBoundingBox(null);
	    			}
				break;
    		}
    	}
    	if (e instanceof EventBlockPush) {
    		if (mc.thePlayer.isSneaking()) e.setCancelled();
    	}
    	if (e instanceof EventUpdate) {
            setMode(mode.getValue().toString());
    		EventUpdate event = (EventUpdate)e;
    		switch (mode.getValue()) {
				case WATCHDOG: 
					if (!event.isPre() && mc.thePlayer.isSneaking()) {
						event.setYaw(event.getYaw() + MathUtils.getRandomInRange(-.2f, .2f));
						mc.thePlayer.motionY = 0;
						double multiplier = 0.5 + counter * .01;
						double mx = -Math.sin(Math.toRadians(mc.thePlayer.rotationYaw));
						double mz = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw));
						double x = MovementInput.moveForward * multiplier * mx + MovementInput.moveStrafe * multiplier * mz;
						double z = MovementInput.moveForward * multiplier * mz - MovementInput.moveStrafe * multiplier * mx;
						if (mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isOnLadder()) {   
							if (counter >= 0) {
								mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z, false));
								
								event.setYaw(event.getYaw() + MathUtils.getRandomInRange(-.2f, .2f));
								mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 0 - counter, mc.thePlayer.posZ, false));
								mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z, false));

								mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z); 
								counter += 1;
								if (counter >= 3) {
									counter = -8; 
								}
							} else {	  
								counter+=1;
							}
						}
					} 
				break;
    		}
    	}
    	if (e instanceof EventMove) {
    		EventMove event = (EventMove)e;
    		switch (mode.getValue()) {
				case WATCHDOG: 
	            	if (mc.thePlayer.isSneaking()) {
	            		event.setMoveSpeed(isInsideBlock() ? .29 : .15);
	            	}
				break;
    		}
    	}
    	if (e instanceof EventPacket) {
    		EventPacket event = (EventPacket)e;
    		switch (mode.getValue()) {
				case WATCHDOG: 
		    		if (event.isReceiving()) {
		    			if (event.getPacket() instanceof S08PacketPlayerPosLook) {
		 
		    			}
		    		}
		    		if (event.isSending()) {
		    			
		    		}
				break;
    		}
    	}
    }
    
	public boolean isInsideBlock() {
		for (int x = MathHelper.floor_double(
				mc.thePlayer.boundingBox.minX); x < MathHelper.floor_double(mc.thePlayer.boundingBox.maxX) + 1; ++x) {
			for (int y = MathHelper.floor_double(
					mc.thePlayer.boundingBox.minY + 1.0D); y < MathHelper.floor_double(mc.thePlayer.boundingBox.maxY)
					+ 2; ++y) {
				for (int z = MathHelper.floor_double(
						mc.thePlayer.boundingBox.minZ); z < MathHelper.floor_double(mc.thePlayer.boundingBox.maxZ)
						+ 1; ++z) {
					Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
					if (block != null && !(block instanceof BlockAir)) {
						AxisAlignedBB boundingBox = block.getCollisionBoundingBox(mc.theWorld, new BlockPos(x, y, z),
								mc.theWorld.getBlockState(new BlockPos(x, y, z)));
						if (block instanceof BlockHopper) {
							boundingBox = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
						}

						if (boundingBox != null && mc.thePlayer.boundingBox.intersectsWith(boundingBox))
							return true;
					}
				}
			}
		}
		return false;
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