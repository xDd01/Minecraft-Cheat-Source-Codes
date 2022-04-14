package me.spec.eris.client.modules.movement;

import java.util.LinkedList;
import java.util.List;

import me.spec.eris.Eris;
import me.spec.eris.api.event.Event;
import me.spec.eris.client.events.client.EventPacket;
import me.spec.eris.client.events.player.EventJump;
import me.spec.eris.client.events.player.EventMove;
import me.spec.eris.client.events.player.EventStep;
import me.spec.eris.client.events.player.EventUpdate;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.api.module.Module;
import me.spec.eris.client.antiflag.prioritization.enums.ModulePriority;
import me.spec.eris.client.antiflag.prioritization.enums.ModuleType;
import me.spec.eris.api.value.types.BooleanValue;
import me.spec.eris.api.value.types.ModeValue;
import me.spec.eris.api.value.types.NumberValue;
import me.spec.eris.client.modules.combat.Killaura;
import me.spec.eris.utils.math.MathUtils;
import me.spec.eris.utils.world.TimerUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class Flight extends Module {

	public Flight(String racism) {
		super("Flight", ModuleCategory.MOVEMENT, racism);;
        setModuleType(ModuleType.FLAGGABLE);
        setModulePriority(ModulePriority.HIGHEST);
    }
    
    private ModeValue<Mode> mode = new ModeValue<Mode>("Mode", Mode.VANILLA, this);
    public enum Mode { VANILLA, WATCHDOG }
    
    public BooleanValue<Boolean> blink = new BooleanValue<>("Blink", false, this, () -> mode.getValue().equals(Mode.WATCHDOG), "Blink while flying");
    public BooleanValue<Boolean> timerAbuse = new BooleanValue<>("Timer abuse", false, this, () -> mode.getValue().equals(Mode.WATCHDOG), "Abuse timer speed");

    private NumberValue<Float> timerSpeedAbuse = new NumberValue<Float>("Timer", 1.5F, 1F, 5F, this, () -> timerAbuse.getValue() && mode.getValue().equals(Mode.WATCHDOG), "Timer speed abused?");
    private NumberValue<Float> timerDelay = new NumberValue<Float>("Timer Delay", 1.5F, .1F, 5F, this, () -> timerAbuse.getValue() && mode.getValue().equals(Mode.WATCHDOG), "How long in seconds to abuse timer for?");
    private NumberValue<Float> flySpeed = new NumberValue<Float>("Speed", 1F, 0.3F, 3F, this, "Speed");
    
    private final TimerUtils timerAbuseStopwatch = new TimerUtils();
    private final TimerUtils damageStopwatch = new TimerUtils();
    private final List<Packet> packets = new LinkedList<>();

    private boolean onGroundCheck, damagePlayer, damageBoost;
    private double speed;
    private int counter;

	private boolean damaged;

    @Override
    public void onEvent(Event e) {
        setMode(mode.getValue().toString());
        if (e instanceof EventJump || e instanceof EventStep) e.setCancelled();
        if (e instanceof EventMove) {
            EventMove event = (EventMove) e;
            switch (mode.getValue()) {
                case VANILLA:
                    if (mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
                    	event.setY(mc.thePlayer.motionY = flySpeed.getValue());
                    } else if (!mc.gameSettings.keyBindJump.isKeyDown() && mc.gameSettings.keyBindSneak.isKeyDown()) {
                    	event.setY(mc.thePlayer.motionY = -flySpeed.getValue());
                    } else {
                    	event.setY(mc.thePlayer.motionY = 0);
                    }
                    event.setMoveSpeed(flySpeed.getValue());
                    break;
			case WATCHDOG:
	        	if (onGroundCheck && damageBoost) {
		        		switch (counter) {
		        		case 0:
		        			if (!damagePlayer) {
								if (damageStopwatch.hasReached(150)) {
									for (int i = 0; i < 9; i++) {
										mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + event.getMotionY(event.getLegitMotion()), mc.thePlayer.posZ, false));
										mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + event.getMotionY(event.getLegitMotion()) % .0000625, mc.thePlayer.posZ, false));
										mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer(false));
									}
									mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer(true));
									speed = flySpeed.getValue() /4;
									damagePlayer = true;
								} else {
									event.setX(0);
									event.setY(0);
									event.setZ(0);
								}
						}

		        			break;
		        		case 1:
		        			if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) {
		        				event.setY(mc.thePlayer.motionY = event.getJumpBoostModifier(event.getLegitMotion()));
								speed *= 2.149999;
		        			}
		        			break;
		        		case 2:
							if (mc.thePlayer.isPotionActive(Potion.jump)) event.setY(mc.thePlayer.motionY = -event.getJumpBoostModifier(event.getLegitMotion() - .1));
		        			speed = flySpeed.getValue();
		        			break;
		        		default:
		        			speed = getLastDistance() - getLastDistance() / 159.9;
		        			break;
		        		}
		        		if (damagePlayer) {
		        			speed = Math.max(speed, event.getMovementSpeed());
		        			counter++;
		        		}
		        		event.setMoveSpeed(speed);
		        		damageBoost = !mc.thePlayer.isCollidedHorizontally;
	        	}
				break;
			default:
				break;
            }
        }

        if (e instanceof EventUpdate) {
            EventUpdate event = (EventUpdate) e;
            setMode(mode.getValue().toString());
            switch (mode.getValue()) {
                case VANILLA:

                    break;
			case WATCHDOG:
				if (onGroundCheck) {  
					mc.thePlayer.onGround = true;
					//Yes, indeed I am being scummy and fucking with fov to make it look faster, come for me mad skids
	        		if (timerAbuse.getValue() && counter >= 15 & damaged) {
	                    if (!timerAbuseStopwatch.hasReached(timerDelay.getValue() * 1000)) {
	                    	if (mc.gameSettings.ofDynamicFov) {
	                    		mc.gameSettings.fovSetting += .05;
							}
	                        mc.timer.timerSpeed = timerSpeedAbuse.getValue();
	                    } else {
							if (mc.gameSettings.ofDynamicFov) {
								if (mc.gameSettings.fovSetting > 90) mc.gameSettings.fovSetting = 90;
							}
	                        mc.timer.timerSpeed = 1F;
	                    }
	                }
	                if (counter < 15 && !damaged) {
	                    timerAbuseStopwatch.reset();
	                }
	                if (!damaged && mc.thePlayer.hurtTime > 0)  damaged = true;
                    if (event.isPre()) {  
                        double xDif = mc.thePlayer.posX - mc.thePlayer.prevPosX;
                        double zDif = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
                        setLastDistance(Math.sqrt(xDif * xDif + zDif * zDif));
                        if (counter > 2 || !damageBoost) {
                        	double val = 8.25E-6;
							mc.thePlayer.motionY = 0;
                        	if (mc.thePlayer.ticksExisted % 4 == 0)val += MathUtils.secRanDouble(1.24E-14D, 1.25E-13D);
                        	event.setY(mc.thePlayer.posY + (mc.thePlayer.ticksExisted % 2 == 0 ? val : -val));
                        	event.setOnGround(mc.thePlayer.ticksExisted % 2 == 0);

                        	if (!mc.thePlayer.isMoving()) forceMove();
                        }
                    }
				} else if (mc.thePlayer.ticksExisted % 12 == 0){
					onGroundCheck = mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically;
				} else {
					setLastDistance(mc.thePlayer.motionX = mc.thePlayer.motionZ = 0);
				}
				break;
			default:
				break;
            }
        }
        
        if (e instanceof EventPacket) {
        	EventPacket event = (EventPacket) e;
            switch (mode.getValue()) {
            	case VANILLA:

            	break;
            	case WATCHDOG:
            		if (event.isSending()) {
            			if (event.getPacket() instanceof C03PacketPlayer) {
            				C03PacketPlayer packet = (C03PacketPlayer)event.getPacket();
            				
            				if (blink.getValue()){
	            				if (packet.isMoving()) {
	            					packets.add(packet);
	            					event.setCancelled();
	            				}
            				} else if (counter == 0 && onGroundCheck) {
            					event.setCancelled();
            				}

							if (packets.size() >= 25 && blink.getValue()) {
								flush();
							}
            			}
            		}
            		break;
            	default:
            		break;
            }
        }
    }
    
    public void forceMove() {
		double speed = .15;
		mc.thePlayer.motionX = (-Math.sin(mc.thePlayer.getDirection())) * speed;
		mc.thePlayer.motionZ = Math.cos(mc.thePlayer.getDirection()) * speed;
    }
    
    public void flush() {
		packets.forEach(mc.thePlayer.sendQueue::addToSendQueueNoEvent);
		packets.clear();
    }

    @Override
    public void onEnable() {
		mc.thePlayer.stepHeight = 0.0f;
    	if (Eris.INSTANCE.moduleManager.isEnabled(Speed.class)) {
        	Eris.INSTANCE.moduleManager.getModuleByClass(Speed.class).toggle(false);
    	}
    	damagePlayer = false;
    	damaged = false;
    	damageBoost = mc.thePlayer.isMoving();
    	onGroundCheck = mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically; 
    	timerAbuseStopwatch.reset();
    	damageStopwatch.reset();
        counter = 0;
        speed = 0;
        setLastDistance(0.0);
        super.onEnable();
    }

    @Override
    public void onDisable() {
		mc.thePlayer.stepHeight = 0.626f;
        switch (mode.getValue()) {
	    	case VANILLA:
	
	    	break;
	    	case WATCHDOG:
				Killaura aura = ((Killaura)Eris.getInstance().moduleManager.getModuleByClass(Killaura.class));
				aura.fuckCheckVLs = true;
	    		Speed sped = ((Speed)Eris.INSTANCE.moduleManager.getModuleByClass(Speed.class));
	    		sped.waitTicks = 5;
	    		mc.thePlayer.onGround = false;
	        	mc.timer.timerSpeed = 1.0f;
	        	mc.thePlayer.motionY = 0;
	        	mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
	    		
				if (blink.getValue()) {
					flush();
				}
	    		break;
	    	default:
	    		break;
	    }
        super.onDisable();
    }
}
