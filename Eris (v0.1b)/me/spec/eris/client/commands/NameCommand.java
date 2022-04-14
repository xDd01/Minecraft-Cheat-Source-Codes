package me.spec.eris.client.commands;

import me.spec.eris.Eris;
import me.spec.eris.api.command.Command;
import me.spec.eris.utils.player.PlayerUtils;
import net.minecraft.client.Minecraft;

public class NameCommand extends Command {


    public NameCommand() {
        super("name", "gets MC name");
    }

    @Override
    public void execute(String[] commandArguments) {
        if(Minecraft.getMinecraft().theWorld != null) {
            PlayerUtils.tellUser("Your name is " + Minecraft.getMinecraft().thePlayer.getName());
        }
    }
}
