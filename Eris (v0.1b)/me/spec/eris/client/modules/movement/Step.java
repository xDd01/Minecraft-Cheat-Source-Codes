package me.spec.eris.client.modules.movement;

import java.util.Arrays;
import java.util.List;

import me.spec.eris.Eris;
import me.spec.eris.api.event.Event;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.client.events.client.EventPacket;
import me.spec.eris.client.events.player.EventMove;
import me.spec.eris.client.events.player.EventStep;
import me.spec.eris.api.module.Module;
import me.spec.eris.client.antiflag.prioritization.ModulePrioritizer;
import me.spec.eris.client.antiflag.prioritization.enums.ModulePriority;
import me.spec.eris.client.antiflag.prioritization.enums.ModuleType;
import me.spec.eris.client.modules.combat.Criticals;
import me.spec.eris.api.value.types.ModeValue;
import me.spec.eris.client.modules.combat.Killaura;
import me.spec.eris.utils.world.BlockUtils;
import me.spec.eris.utils.world.TimerUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;

public class Step extends Module {

    public Step(String racism) {
        super("Step", ModuleCategory.MOVEMENT, racism);
        setModuleType(ModuleType.FLAGGABLE);
        setModulePriority(ModulePriority.HIGH);
    }
    private final double[] offsets = { 0.42f, 0.7532f };
    private final float timerWhenStepping = 1.0f / (offsets.length + 1);
    public boolean cancelMorePackets;
    private byte cancelledPackets;
    public static boolean cancelStep;   
    private ModeValue<Mode> mode = new ModeValue<Mode>("Mode", Mode.NCP, this);

    private enum Mode {NCP, VANILLA}

    private final TimerUtils stepDelay = new TimerUtils();
    public double height;

    @Override
    public void onEvent(Event e) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (e instanceof EventPacket) {
        	EventPacket event = (EventPacket)e;
            if (event.isSending() && event.getPacket() instanceof C03PacketPlayer) {
                if (cancelledPackets > 0) {
                    cancelMorePackets = false;
                    cancelledPackets = 0;
                    mc.timer.timerSpeed = 1.0f;
                }

                if (cancelMorePackets) {
                    mc.timer.timerSpeed = timerWhenStepping;
                    cancelledPackets++;
                }
            }
        }
        if (e instanceof EventMove) {
        	EventMove event = (EventMove)e;
            if (cancelMorePackets) {
            	event.setMoveSpeed(.1);
            }
        }
        if (e instanceof EventStep) {
            EventStep event = (EventStep) e;

            if (event.isPre()) {

                if (isInvalid() || Eris.INSTANCE.moduleManager.isEnabled(Speed.class)) {
                    event.setStepHeight(cancelStep || !mc.thePlayer.isCollidedHorizontally || !mc.thePlayer.isCollidedVertically ? 0.626f : 1.0f);
                } else {  
                    if (mc.thePlayer.isInWater() || mc.thePlayer.isInLava() || mc.thePlayer.isOnLadder() || ModulePrioritizer.flaggableMovementModules() || BlockUtils.isOnLiquid(mc.thePlayer)) {
                        stepDelay.reset();
                    } 
                    if (stepDelay.hasReached(250) && mc.thePlayer.isCollidedVertically && mc.thePlayer.isCollidedHorizontally) {
                        event.setStepHeight(mc.thePlayer.isPotionActive(Potion.jump) ? 1 : 2.0f);
                    } 
                    height = 0;
                }
            } else {
                Scaffold scaffold = ((Scaffold)Eris.getInstance().moduleManager.getModuleByClass(Scaffold.class));
                Criticals crits = ((Criticals)Eris.getInstance().moduleManager.getModuleByClass(Criticals.class));
                double posX = mc.thePlayer.posX;
                double posY = mc.thePlayer.posY;
                double posZ = mc.thePlayer.posZ;
            	height = mc.thePlayer.getEntityBoundingBox().minY - mc.thePlayer.posY; 
                if (isInvalid() || Eris.INSTANCE.moduleManager.isEnabled(Speed.class)) {
                    if (event.getHeightStepped() > 0.626) { 
                        if (event.getHeightStepped() > 0) {
            				if (crits.airTime > 0 || scaffold.motionBoost) {
            					sendPosition(0,0,0,mc.thePlayer.onGround,false);
            					scaffold.motionBoost = false;
                                crits.accumulatedFall = 0;
                                crits.waitTicks = 2;
                                crits.airTime = 0;
            				}
            				
                        }
                        for (double offset : offsets) {
                        	sendPosition(0,offset * event.getHeightStepped(),0, !(BlockUtils.getBlockAtPos(new BlockPos(posX, posY + offset * event.getHeightStepped(), posZ)) instanceof BlockAir), false);
                        }
                        Killaura aura = ((Killaura)Eris.getInstance().moduleManager.getModuleByClass(Killaura.class));
                        aura.fuckCheckVLs = true;
                        cancelMorePackets = true;
                    }
                } else {
                	height = mc.thePlayer.getEntityBoundingBox().minY - mc.thePlayer.posY; 	 
                    if (height > 0) {
                        if (crits.airTime > 0 && crits.waitTicks <= 0 || scaffold.motionBoost) {
                            sendPosition(0,0,0,mc.thePlayer.onGround,false);
                            scaffold.motionBoost = false;
                            crits.accumulatedFall = 0;
                            crits.waitTicks = 2;
                            crits.airTime = 0;
                        }
                    }
                    double y = 0;
                    if (height <= 1.) {
                        float first = .42f - 4.0e-9f;
                        float second = .753f;
                        if (height != 1) {
                            first *= height;
                            second *= height;
                            if (first > .425f) first = .425f;
                            if (second > .75f) second = .75f;
                            if (second < .5f) second = .5f;
                        }
                        if (first == 0.42f)
                            sendPosition(0, .41999998688698f, 0, !(BlockUtils.getBlockAtPos(new BlockPos(posX, posY + 0.41999998688698f, posZ)) instanceof BlockAir), false);
                        if (posY + second < posY + height)
                            sendPosition(0, second, 0, !(BlockUtils.getBlockAtPos(new BlockPos(posX, posY + second, posZ)) instanceof BlockAir), false);
                        return;
                    } else if (height >= 1.5 && height <= 2.01) {
                        List<Float> heights = height <= 1.5 ? Arrays.asList(0.42f, 0.333f, 0.248f, 0.083f, -0.078f) : Arrays.asList(0.4249999f, 0.821001f, 0.699f, 0.598f, 1.02217f, 1.372f, 1.652f, 1.869f);
                        for (int i = 0; i < heights.size(); i++) {
                            sendPosition(0, y + heights.get(i), 0, !(BlockUtils.getBlockAtPos(new BlockPos(posX, y + heights.get(i), posZ)) instanceof BlockAir), false);
                        }
                    }
                    Killaura aura = ((Killaura)Eris.getInstance().moduleManager.getModuleByClass(Killaura.class));
                    aura.fuckCheckVLs = true;
                    cancelMorePackets = true;
                    stepDelay.reset();
                }
            	
            }
        }
    }
    
    public static boolean isInvalid() {
        double radius = 0.50;
        if (mc.thePlayer == null) return false;
        double currentX = mc.thePlayer.posX, currentY = mc.thePlayer.posY, currentZ = mc.thePlayer.posZ;
        boolean isInvalid = false;
        String[] invalidBlocks = {"chest", "slab", "stair", "anvil", "enchant", "snow"};

        for (double x = currentX - radius; x <= currentX + radius; x++) {
            for (double y = currentY - radius; y <= currentY + radius; y++) {
                for (double z = currentZ - radius; z <= currentZ + radius; z++) {
                    if (!isInvalid) {
                        String blockName = BlockUtils.getBlockAtPos(new BlockPos(x, y, z)).getUnlocalizedName().toLowerCase();
                        for (String s : invalidBlocks) {
                            if (blockName.contains(s.toLowerCase())) isInvalid = true;
                        }
                    }
                }
            }
        }
        return isInvalid;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }
}
