package me.spec.eris.client.modules.movement;

import me.spec.eris.Eris;
import me.spec.eris.api.event.Event;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.client.events.player.EventJump;
import me.spec.eris.client.events.player.EventMove;
import me.spec.eris.client.events.player.EventStep;
import me.spec.eris.client.events.player.EventUpdate;
import me.spec.eris.api.module.Module;
import me.spec.eris.client.antiflag.prioritization.enums.ModulePriority;
import me.spec.eris.client.antiflag.prioritization.enums.ModuleType;
import me.spec.eris.client.integration.server.interfaces.Gamemode;
import me.spec.eris.client.modules.combat.Criticals;
import me.spec.eris.client.modules.combat.Killaura;
import me.spec.eris.api.value.types.ModeValue;
import me.spec.eris.utils.player.PlayerUtils;
import net.minecraft.stats.StatList;

public class Speed extends Module {
    private ModeValue<Mode> mode = new ModeValue<Mode>("Mode", Mode.WATCHDOG, this);

    private enum Mode {WATCHDOG}
	private int stage;
    public int waitTicks, hops;
	private double speed;

	public Speed(String racism) {
        super("Speed", ModuleCategory.MOVEMENT, racism);
        setModuleType(ModuleType.FLAGGABLE);
		setModulePriority(ModulePriority.MODERATE);
    }



	@Override
	public void onEnable() {
		Criticals criticals = ((Criticals)Eris.getInstance().moduleManager.getModuleByClass(Criticals.class));
		criticals.accumulatedFall = 0;
		if (criticals.airTime > 0) {
			sendPosition(0,0,0,true,false);
			criticals.airTime = 0;
			criticals.waitTicks = 3;
		}
		if (!Eris.INSTANCE.moduleManager.isEnabled(Flight.class)) {
			hops = 0;
			setLastDistance(0.0);
			stage = 0;
		}
		super.onEnable();
	}

	@Override
	public void onDisable() {
		mc.timer.timerSpeed = 1.0f;
		super.onDisable();
	}

    @Override
    public void onEvent(Event e) {

		switch (mode.getValue()) {
			case WATCHDOG:
				if (Eris.INSTANCE.moduleManager.isEnabled(Flight.class) || Eris.INSTANCE.moduleManager.isEnabled(Longjump.class) || mc.thePlayer.isOnLadder() || PlayerUtils.isInLiquid() || PlayerUtils.isOnLiquid()) {
					if (e instanceof EventUpdate) {
						EventUpdate eu = (EventUpdate) e;
						if (eu.isPre()) {
							float moveSpeed = .42f;
							if (PlayerUtils.isInLiquid()) {
								if (mc.thePlayer.ticksExisted % 2 == 0) mc.thePlayer.motionY = moveSpeed;
							} else if (PlayerUtils.isOnLiquid()) {
								mc.thePlayer.motionX = -(Math.sin(mc.thePlayer.getDirection()) * moveSpeed);
								mc.thePlayer.motionZ = Math.cos(mc.thePlayer.getDirection()) * moveSpeed;
							}
						}
					}
					hops = -1;
					return;
				}
				if (e instanceof EventJump) {
					e.setCancelled();
				}
				if (e instanceof EventUpdate) {
					setMode(mode.getValue().toString());
					EventUpdate eu = (EventUpdate) e;
					double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
					double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
					setLastDistance(Math.sqrt(xDist * xDist + zDist * zDist));
					if (eu.isPre() && mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && !Eris.INSTANCE.moduleManager.isEnabled(Scaffold.class)) {
						eu.setY(eu.getY() + 9.0E-4D / 2);
					}
				}
				if (e instanceof EventStep) {
					EventStep event = (EventStep) e;
					if (!event.isPre()) {
						double height = mc.thePlayer.getEntityBoundingBox().minY - mc.thePlayer.posY;
						if (height <= .6 && height >= -.5 && height != 0.0) {
							hops = -2;
							setLastDistance(0.0);
						}
					}
				}
				if (e instanceof EventMove) {
					boolean reset = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, mc.thePlayer.motionY, 0.0)).size() > 0 && mc.thePlayer.onGround;
					EventMove em = (EventMove) e;
					Step step = ((Step) Eris.getInstance().moduleManager.getModuleByClass(Step.class));
					if (step.cancelMorePackets) {
						mc.timer.timerSpeed = 1.0f;
						hops = -1;
					}
					if (waitTicks > 0 && mc.thePlayer.onGround) waitTicks--;
					if (waitTicks > 0 || !mc.thePlayer.isMoving() || mc.thePlayer.fallDistance > 2.25) {
						setLastDistance(0.0);
						stage = 0;
						return;
					}
					if (reset) {
						if (stage < 3) {
							hops = -1;
						}
						stage = 0;
					}
					switch (stage) {
						case 0:
							setLastDistance(0.0);
							if (mc.thePlayer.onGround) {
								if (!Eris.INSTANCE.moduleManager.isEnabled(Scaffold.class)) mc.timer.timerSpeed = 1.3f;
								mc.thePlayer.isAirBorne = true;
								mc.thePlayer.triggerAchievement(StatList.jumpStat);
								em.setY(mc.thePlayer.motionY = (float) em.getMotionY(.42f - 9.0E-4D * 2));
								speed = em.getMovementSpeed() * (Eris.INSTANCE.moduleManager.isEnabled(Scaffold.class) || hops < 0 || waitTicks > 0 ? 1.8 : hops % 3 == 0 ? 2.22 : 2.1499);
								hops++;
							}
							setLastDistance(0.0);
						break;
						case 1:
							speed = getLastDistance() - .66 * (getLastDistance() - em.getMovementSpeed());
							break;
						default:
							if ((stage == 2 || stage == 6) && mc.timer.timerSpeed > 1.0f) {
								mc.timer.timerSpeed -= stage == 2 ? .15f : .03f;
							}
							speed = getLastDistance() - getLastDistance() / 159;
						break;
				}
				em.setMoveSpeed(waitTicks > 0 ? .2 : speed);
				stage++;
			}
		}
	}
}
