package me.spec.eris.client.commands;

import me.spec.eris.Eris;
import me.spec.eris.api.command.Command;
import me.spec.eris.api.config.ClientConfig;
import me.spec.eris.utils.player.PlayerUtils;

import java.io.IOException;

public class ConfigCommand extends Command {


    public ConfigCommand() {
        super("config", "config <list, save, load> <name>");
    }

    @Override
    public void execute(String[] commandArguments) {
        if(commandArguments.length == 3) {
            if(commandArguments[1].equalsIgnoreCase("save")) {
                Eris.getInstance().configManager.saveConfig(new ClientConfig(commandArguments[2]));
                PlayerUtils.tellUser("Saved config " + commandArguments[2]);
            } else if(commandArguments[1].equalsIgnoreCase("load")) {
                if(Eris.getInstance().configManager.getConfigByName(commandArguments[2]) != null) {
                    try {
                        Eris.getInstance().configManager.loadConfig(Eris.getInstance().configManager.getConfigByName(commandArguments[2]));
                        PlayerUtils.tellUser("Loaded config " + commandArguments[2]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    PlayerUtils.tellUser("That config doesnt exist!");
                }
            }
        } else if(commandArguments.length == 2) {
                if(commandArguments[1].equalsIgnoreCase("list")) {
                    Eris.getInstance().configManager.getConfigs().forEach(clientConfig -> PlayerUtils.tellUser("Config - " + clientConfig.getConfigName()));
                }

        } else {
            PlayerUtils.tellUser("Invalid command arguments!");
        }
    }
}
