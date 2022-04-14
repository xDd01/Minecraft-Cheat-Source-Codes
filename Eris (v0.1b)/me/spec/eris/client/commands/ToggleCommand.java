package me.spec.eris.client.commands;

import me.spec.eris.Eris;
import me.spec.eris.api.command.Command;
import me.spec.eris.utils.player.PlayerUtils;

public class ToggleCommand extends Command {


    public ToggleCommand() {
        super("toggle", "toggle <module>");
    }

    @Override
    public void execute(String[] commandArguments) {
        if(commandArguments.length == 2) {
            if(Eris.getInstance().moduleManager.getModuleByName(commandArguments[1]) != null) {
                Eris.getInstance().moduleManager.getModuleByName(commandArguments[1]).toggle(true);
                PlayerUtils.tellUser("Toggled " + commandArguments[1]);
            } else {
                PlayerUtils.tellUser("That module doesnt exist!");
            }
         } else if(commandArguments.length < 2 || commandArguments.length > 2) {
            PlayerUtils.tellUser("Invalid command arguments!");
        }
    }
}
