package me.spec.eris.utils.network;

import net.minecraft.client.Minecraft;

public class ServerUtils {

    public static boolean onServer(String name) {
        if (Minecraft.getMinecraft().getCurrentServerData() != null) {
            return Minecraft.getMinecraft().getCurrentServerData().serverIP.toLowerCase().contains(name);
        }
        return false;
    }
}
