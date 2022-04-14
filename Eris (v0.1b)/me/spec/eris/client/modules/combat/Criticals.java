package me.spec.eris.client.modules.combat;

import me.spec.eris.Eris;
import me.spec.eris.api.event.Event;
import me.spec.eris.client.events.client.EventPacket;
import me.spec.eris.client.events.player.EventJump;
import me.spec.eris.client.events.player.EventStep;
import me.spec.eris.client.events.player.EventUpdate;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.api.module.Module;
import me.spec.eris.client.antiflag.prioritization.enums.ModulePriority;
import me.spec.eris.client.antiflag.prioritization.enums.ModuleType;
import me.spec.eris.client.modules.movement.Flight;
import me.spec.eris.client.modules.movement.Scaffold;
import me.spec.eris.client.modules.movement.Speed;
import me.spec.eris.api.value.types.ModeValue;
import me.spec.eris.utils.player.PlayerUtils;
import net.minecraft.network.play.client.C02PacketUseEntity;

public class Criticals extends Module {
	private double groundSpoofDist = 0.001;
	private boolean forceUpdate;
	public int airTime, waitTicks;
	public ModeValue<Mode> modeValue = new ModeValue<>("Mode", Mode.SPOOF, this);
	public double accumulatedFall, posY;

	private static final double[] OFFSETS1 = new double[] {0.05225, 0.011511, 0.001, 0.001};
	private static final double[] OFFSETS2 = new double[] { 0.05D, 0.0D, 0.012511D, 0.0D };

	public Criticals(String racism) {
		super("Criticals", ModuleCategory.COMBAT, racism);
		setModuleType(ModuleType.FLAGGABLE);
		setModulePriority(ModulePriority.LOW);
	}

	public enum Mode {
		SPOOF, SJUMP, WATCHDOG
	}

	@Override
	public void onEnable() {
		super.onEnable();
		airTime = 0;
		waitTicks = 3;
		groundSpoofDist = 1.0E-13D;
	}

	@Override
	public void onDisable() {
		super.onDisable();
		airTime = 0;
		groundSpoofDist = 0.001;
	}

	@Override
	public void onEvent(Event e) {
		if (e instanceof EventJump) {
			if (airTime != 0 && mc.thePlayer.isMoving()) {
				waitTicks = 4;
				PlayerUtils.sendPosition(0, 0, 0, true, false);
				e.setCancelled();
				mc.thePlayer.motionY = .42f;
				airTime = 0;
			}
		} else if (e instanceof EventUpdate) {
			this.setMode(modeValue.getValue().toString());
			EventUpdate eu = (EventUpdate) e;
 
			if (modeValue.getValue() == Mode.SPOOF) {
				if (groundSpoofDist < 0.0001) {
					groundSpoofDist = 0.001;
				}
				if (mc.thePlayer.isSwingInProgress && mc.thePlayer.isCollidedVertically) {
					eu.setY(eu.getY() + groundSpoofDist);
					eu.setOnGround(false);
					groundSpoofDist -= 1.0E-11;
				}
			}

			if (modeValue.getValue() == Mode.SJUMP && eu.isPre()) {
				if (interferanceFree() && mc.thePlayer.hurtTime == 0) {
					if (waitTicks > 0) {
						waitTicks--;
						return;
					}
					double[] values = new double[] {.0,0.1040803780930446,0.48071087633169896,0.7850277037892397,1.015555072702199,
							1.1707870772188045,1.2491870787446828,1.24918707874468281, .1661092609382138,1.0013359791121417,0.7531999805212024,0.41999998688697815};
					//spec sir this might throw an exception
					eu.setY(mc.thePlayer.posY + (airTime == 0 ? 0 : values[airTime]));
					eu.setOnGround(eu.getY() == mc.thePlayer.posY);
					if (airTime > 0) airTime--;
				} else {
					groundSpoofDist = airTime = 0;
					waitTicks = 6;
				}
			}
		} else if (e instanceof EventStep) {
			if (mc.thePlayer == null)
				return;
			if (mc.thePlayer.getEntityBoundingBox().minY - mc.thePlayer.posY < .626
					&& mc.thePlayer.getEntityBoundingBox().minY - mc.thePlayer.posY > .4) {
				waitTicks = 4;
				airTime = 0;
			} 
		} else if (e instanceof EventPacket) {
			if (mc.thePlayer == null || !interferanceFree())
				return;
			EventPacket ep = (EventPacket) e;
			if (ep.getPacket() instanceof C02PacketUseEntity) {
				C02PacketUseEntity packet = (C02PacketUseEntity) ep.getPacket();
				if (packet.getAction() == C02PacketUseEntity.Action.ATTACK) {
					if (modeValue.getValue() == Mode.SJUMP && airTime == 0 && mc.thePlayer.hurtTime == 0
							&& waitTicks == 0 && interferanceFree()) {
						airTime = 13;
					}
				}
			}
		}
	}

	public double[] getOffsets() {
		return OFFSETS1;
	}

	public void doUpdate(EventUpdate eventPlayerUpdate) {
		Killaura aura = ((Killaura) Eris.INSTANCE.moduleManager.getModuleByClass(Killaura.class));

		if (modeValue.getValue() != Mode.WATCHDOG || !mc.thePlayer.isMoving())  return;
		if (!(!aura.isToggled() || Killaura.getTarget() == null)) {
			if (interferanceFree()) {
				if (waitTicks == 0 && accumulatedFall < 3) {
					eventPlayerUpdate.setOnGround(false);
					forceUpdate = true;
					if (airTime >= 3) {
						posY = 9.0e-4d * 2;
						airTime = 0;
					} else {
						posY = .1226 - 9.0e-4d;
						if (airTime == 2) {
							posY -= 9.0e-4d * 2;
						}
					}
					eventPlayerUpdate.setY(mc.thePlayer.posY + posY);  
					airTime++;
				} else {
					if (accumulatedFall >= 3) {
						if (mc.thePlayer.onGround) {
							sendPosition(0, 0, 0, true, false);
							accumulatedFall = 0;
							airTime = 0;
						}
					}
					
					if (waitTicks > 0) waitTicks--;
				}
			} else {
				waitTicks = 3;
			}
		} else {
			waitTicks = 0;
		}
	}

	public void forceUpdate() {
		if (!forceUpdate || airTime == 0)
			return;
		// You don't send c06s standing still, doing so flags any half decent anticheat
		// - food for thought
		sendPosition(0, 0, 0, mc.thePlayer.onGround, false);

		accumulatedFall = 0;
		forceUpdate = false;
	}

	public boolean interferanceFree() {
		if (Eris.INSTANCE.moduleManager.isEnabled(Speed.class))
			return false;
		if (Eris.INSTANCE.moduleManager.isEnabled(Flight.class))
			return false;
		if (Eris.INSTANCE.moduleManager.isEnabled(Scaffold.class))
			return false;
		if (mc.gameSettings.keyBindJump.isKeyDown() || mc.thePlayer.isInWater() || mc.thePlayer.isInLava()
				|| mc.thePlayer.isOnLadder())
			return false;
		return (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && mc.thePlayer.fallDistance == 0.0
				&& mc.thePlayer.stepHeight < .7);
	}
}
