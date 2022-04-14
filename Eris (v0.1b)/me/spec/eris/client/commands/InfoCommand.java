package me.spec.eris.client.commands;

import me.spec.eris.Eris;
import me.spec.eris.api.command.Command;
import me.spec.eris.utils.clipboard.ClipboardUtils;
import me.spec.eris.utils.player.PlayerUtils;
import net.minecraft.client.Minecraft;

public class InfoCommand extends Command {


    public InfoCommand() {
        super("info", "gets your name and current server");
    }

    @Override
    public void execute(String[] commandArguments) {
        if(Minecraft.getMinecraft().theWorld != null) {
            String info = Minecraft.getMinecraft().thePlayer.getName() + " is on " + (Minecraft.getMinecraft().isSingleplayer() ? "Singleplayer" : Minecraft.getMinecraft().getCurrentServerData().serverIP);
            PlayerUtils.tellUser(info);
            ClipboardUtils.copy(info);
        }
    }
}
