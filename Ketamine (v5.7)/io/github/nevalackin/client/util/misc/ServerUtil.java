package io.github.nevalackin.client.util.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

public class ServerUtil {

    public static String lastIP;
    public static int lastPort;
    public static ServerData lastData;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static String currentHypixelMap;

    public static boolean isHypixel() {
        return mc.getCurrentServerData() != null && (lastIP.endsWith(".hypixel.net") || lastIP.endsWith(".hypixel.net.")) && !mc.isSingleplayer();
    }

    public static String getCurrentHypixelMap() {
        return currentHypixelMap;
    }

    public static void setCurrentHypixelMap(String currentHypixelMap1) {
        currentHypixelMap = currentHypixelMap1;
    }
}
