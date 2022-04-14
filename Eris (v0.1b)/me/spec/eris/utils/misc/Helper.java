package me.spec.eris.utils.misc;

import me.spec.eris.Eris;
import me.spec.eris.utils.world.TimerUtils;

public class Helper {

    private static TimerUtils timer = new TimerUtils();
    private static long lastSend = System.currentTimeMillis();

    public static void onTick() {
        if (timer.hasReached(5000)) {
            if (System.currentTimeMillis() - lastSend > 5500) {
                Eris.INSTANCE.getPlaytimeTracker().setStartTime(Eris.INSTANCE.getPlaytimeTracker().getStartTime() + (System.currentTimeMillis() - lastSend) + 5500);
            }
            lastSend = System.currentTimeMillis();
            timer.reset();
        }	
    }

    public static String sendMessage(String message) {
        StringBuilder sb = new StringBuilder(message);

        return sb.toString();
    }
}
