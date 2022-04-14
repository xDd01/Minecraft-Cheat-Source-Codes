package me.spec.eris.client.commands;

import me.spec.eris.Eris;
import me.spec.eris.api.command.Command;
import me.spec.eris.utils.clipboard.ClipboardUtils;
import me.spec.eris.utils.player.PlayerUtils;
import net.minecraft.client.Minecraft;

public class IPCommand extends Command {


    public IPCommand() {
        super("ip", "gets server ip");
    }

    @Override
    public void execute(String[] commandArguments) {
        if(Minecraft.getMinecraft().theWorld != null && !Minecraft.getMinecraft().isSingleplayer()) {
            PlayerUtils.tellUser("Copied IP " + Minecraft.getMinecraft().getCurrentServerData().serverIP);
            ClipboardUtils.copy(Minecraft.getMinecraft().getCurrentServerData().serverIP);
        } else {
            PlayerUtils.tellUser("Your not in multiplayer");
        }
    }
}
