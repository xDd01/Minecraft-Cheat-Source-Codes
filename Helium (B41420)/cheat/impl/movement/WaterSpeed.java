package rip.helium.cheat.impl.movement;

import me.hippo.systems.lwjeb.annotation.Collect;
import rip.helium.cheat.Cheat;
import rip.helium.cheat.CheatCategory;
import rip.helium.event.minecraft.PlayerUpdateEvent;
import rip.helium.utils.Mafs;
import rip.helium.utils.PlayerUtils;
import rip.helium.utils.SpeedUtils;

public class WaterSpeed extends Cheat {

    public WaterSpeed() {
        super ("WaterSpeed", "yes", CheatCategory.MOVEMENT);
    }

    @Collect
    public void playerUpdateEvent(PlayerUpdateEvent event) {
        if (mc.thePlayer.isInWater()) {
            SpeedUtils.setPlayerSpeed(0.4);
        }
    }

}
