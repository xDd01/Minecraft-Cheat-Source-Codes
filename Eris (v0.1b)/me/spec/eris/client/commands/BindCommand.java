package me.spec.eris.client.commands;

import me.spec.eris.Eris;
import me.spec.eris.api.command.Command;
import me.spec.eris.utils.player.PlayerUtils;
import org.lwjgl.input.Keyboard;

public class BindCommand extends Command {


    public BindCommand() {
        super("bind", "bind <module> <key>");
    }

    @Override
    public void execute(String[] commandArguments) {
        if(commandArguments.length == 3) {
            if(Eris.getInstance().moduleManager.getModuleByName(commandArguments[1]) != null) {
                int keyStringToCode = Keyboard.getKeyIndex(commandArguments[2].toUpperCase());
                Eris.getInstance().moduleManager.getModuleByName(commandArguments[1]).setKey(keyStringToCode, true);
                PlayerUtils.tellUser("Binded " + commandArguments[1] + " to " + commandArguments[2].toUpperCase());
            } else {
                PlayerUtils.tellUser("That module or key doesnt exist!!");
            }
        } else if(commandArguments.length != 3) {
            PlayerUtils.tellUser("Invalid command arguments!");
        }
    }
}
