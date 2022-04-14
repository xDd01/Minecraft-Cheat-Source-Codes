package me.spec.eris.client.modules.combat;

import me.spec.eris.Eris;
import me.spec.eris.api.event.Event;
import me.spec.eris.api.module.Module;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.client.events.player.EventUpdate;
import me.spec.eris.client.modules.movement.Scaffold;
import me.spec.eris.client.modules.player.AntiVoid;
import me.spec.eris.utils.player.PlayerUtils;

public class TargetStrafe extends Module {
    public TargetStrafe(String racism) {
        super("TargetStrafe", ModuleCategory.COMBAT, racism);
    }

    public int strafeDirection = 1;
    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            EventUpdate eu = (EventUpdate)e;
            if (!eu.isPre()) return;
            boolean changeDirectionInFight = false;
            boolean thevoid = !AntiVoid.isBlockUnder() && mc.thePlayer.ticksExisted % 3 == 0;

            Killaura aura = (Killaura) Eris.getInstance().getModuleManager().getModuleByClass(Killaura.class);
            if (mc.thePlayer.ticksExisted % 25 == 0 && Killaura.target != null && aura.targetList.size() > 0) {
                if (mc.thePlayer.getDistanceToEntity(Killaura.target) <= aura.range.getValue() - .55f) changeDirectionInFight = true;
            }
            //Changing direction with conditionals
            if (mc.thePlayer.isCollidedHorizontally || thevoid || !thevoid && changeDirectionInFight && !mc.thePlayer.isCollidedHorizontally) {
                strafeDirection = -strafeDirection;
            }
            //Setting move input forward & strafe to zero to prevent player input from over riding customstrafe
            if (canStrafe()) mc.thePlayer.movementInput.setForward(0);
        }
    }

    public boolean canStrafe() {
        return mc.gameSettings.keyBindJump.isKeyDown() && !Eris.getInstance().getModuleManager().isEnabled(Scaffold.class) && Killaura.currentEntity != null;
    }

}