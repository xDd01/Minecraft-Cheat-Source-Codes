package org.neverhook.client.feature.impl.misc;

import net.minecraft.network.play.server.SPacketPlayerPosLook;
import org.neverhook.client.NeverHook;
import org.neverhook.client.event.EventTarget;
import org.neverhook.client.event.events.impl.packet.EventReceivePacket;
import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;
import org.neverhook.client.feature.impl.combat.KillAura;
import org.neverhook.client.feature.impl.combat.TargetStrafe;
import org.neverhook.client.feature.impl.movement.*;
import org.neverhook.client.ui.notification.NotificationManager;
import org.neverhook.client.ui.notification.NotificationType;

public class FlagDetector extends Feature {

    public FlagDetector() {
        super("FlagDetector", "Автоматически выключает модуль при его детекте", Type.Misc);
    }

    @EventTarget
    public void onReceivePacket(EventReceivePacket event) {
        if (this.getState()) {
            if (event.getPacket() instanceof SPacketPlayerPosLook) {
                if (NeverHook.instance.featureManager.getFeatureByClass(Speed.class).getState()) {
                    featureAlert("Speed");
                    NeverHook.instance.featureManager.getFeatureByClass(Speed.class).state();
                } else if (NeverHook.instance.featureManager.getFeatureByClass(Flight.class).getState()) {
                    featureAlert("Flight");
                    NeverHook.instance.featureManager.getFeatureByClass(Flight.class).state();
                } else if (NeverHook.instance.featureManager.getFeatureByClass(FastClimb.class).getState() && mc.player.isOnLadder() && !mc.player.isUsingItem()) {
                    featureAlert("FastClimb");
                    NeverHook.instance.featureManager.getFeatureByClass(FastClimb.class).state();
                } else if (NeverHook.instance.featureManager.getFeatureByClass(TargetStrafe.class).getState() && KillAura.target != null) {
                    featureAlert("TargetStrafe");
                    NeverHook.instance.featureManager.getFeatureByClass(TargetStrafe.class).state();
                } else if (NeverHook.instance.featureManager.getFeatureByClass(LongJump.class).getState()) {
                    featureAlert("LongJump");
                    NeverHook.instance.featureManager.getFeatureByClass(LongJump.class).state();
                } else if (NeverHook.instance.featureManager.getFeatureByClass(LiquidWalk.class).getState() && mc.player.isInLiquid()) {
                    featureAlert("LiquidWalk");
                    NeverHook.instance.featureManager.getFeatureByClass(LiquidWalk.class).state();
                } else if (NeverHook.instance.featureManager.getFeatureByClass(Timer.class).getState()) {
                    featureAlert("Timer");
                    NeverHook.instance.featureManager.getFeatureByClass(Timer.class).state();
                }
            }
        }
    }

    public void featureAlert(String feature) {
        NotificationManager.publicity(feature, "Disabling due to lag back", 3, NotificationType.WARNING);
    }
}