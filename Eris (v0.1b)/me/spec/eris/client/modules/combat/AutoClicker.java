package me.spec.eris.client.modules.combat;

import me.spec.eris.api.event.Event;
import me.spec.eris.api.module.Module;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.api.value.types.NumberValue;
import me.spec.eris.client.events.player.EventUpdate;
import me.spec.eris.utils.world.TimerUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Mouse;

import java.util.concurrent.ThreadLocalRandom;

//TODO: Better randomization method
public class AutoClicker extends Module {

    private final TimerUtils stopwatch = new TimerUtils();
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private NumberValue<Double> cpsMin = new NumberValue<Double>("Min CPS", 8.0, 1.0, 20.0, this, false, null, "The minimum CPS for the AutoClicker");
    private NumberValue<Double> cpsMax = new NumberValue<Double>("Max CPS", 8.0, 1.0, 20.0, this, false, null, "The maximum CPS for the AutoClicker");
    private NumberValue<Integer> randomizationLevel = new NumberValue<Integer>("Randomization level", 1, 0, 5, this, false, null, "How much the AutoClicker will randomize");

    public AutoClicker(String racism) {
        super("AutoClicker", ModuleCategory.COMBAT, racism);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            if (mc.currentScreen != null || !((EventUpdate) event).isPre()) return;
            if (Mouse.isButtonDown(0)) {
                if (stopwatch.hasReached(1000 / getRandom())) {
                    mc.clickMouse();
                    stopwatch.reset();
                }
            }
        }
    }

    private long getRandom() {
        if (cpsMin.getValue() >= cpsMax.getValue()) {
            return 10;
        }
        double[] randoms = new double[randomizationLevel.getValue().intValue()];
        for(int i = 0; i < randomizationLevel.getValue(); i++) {
            randoms[i] = random.nextDouble(cpsMin.getValue(), cpsMax.getValue());
        }
        double value = 0;
        for(int i = 0; i < randoms.length; i++) {
            value += randoms[i];
        }
        value /= Math.max(randomizationLevel.getValue(), 1);
        return Math.round(value);
    }
}
