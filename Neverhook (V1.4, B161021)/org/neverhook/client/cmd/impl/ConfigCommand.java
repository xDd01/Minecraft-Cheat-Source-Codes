package org.neverhook.client.cmd.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import org.neverhook.client.NeverHook;
import org.neverhook.client.cmd.CommandAbstract;
import org.neverhook.client.helpers.misc.ChatHelper;
import org.neverhook.client.settings.config.Config;
import org.neverhook.client.settings.config.ConfigManager;
import org.neverhook.client.ui.notification.NotificationManager;
import org.neverhook.client.ui.notification.NotificationType;

public class ConfigCommand extends CommandAbstract {

    public ConfigCommand() {
        super("config", "configurations", "§6.config" + ChatFormatting.LIGHT_PURPLE + " save | load | delete " + "§3<name>", "config");
    }

    @Override
    public void execute(String... args) {
        try {
            if (args.length >= 2) {
                String upperCase = args[1].toUpperCase();
                if (args.length == 3) {
                    switch (upperCase) {
                        case "LOAD":
                            if (NeverHook.instance.configManager.loadConfig(args[2])) {
                                ChatHelper.addChatMessage(ChatFormatting.GREEN + "Successfully " + ChatFormatting.WHITE + "loaded config: " + ChatFormatting.RED + "\"" + args[2] + "\"");
                                NotificationManager.publicity("Config", ChatFormatting.GREEN + "Successfully " + ChatFormatting.WHITE + "loaded config: " + ChatFormatting.RED + "\"" + args[2] + "\"", 4, NotificationType.SUCCESS);
                            } else {
                                ChatHelper.addChatMessage(ChatFormatting.RED + "Failed " + ChatFormatting.WHITE + "load config: " + ChatFormatting.RED + "\"" + args[2] + "\"");
                                NotificationManager.publicity("Config", ChatFormatting.RED + "Failed " + ChatFormatting.WHITE + "load config: " + ChatFormatting.RED + "\"" + args[2] + "\"", 4, NotificationType.ERROR);
                            }
                            break;
                        case "SAVE":
                            if (NeverHook.instance.configManager.saveConfig(args[2])) {
                                ChatHelper.addChatMessage(ChatFormatting.GREEN + "Successfully " + ChatFormatting.WHITE + "saved config: " + ChatFormatting.RED + "\"" + args[2] + "\"");
                                NotificationManager.publicity("Config", ChatFormatting.GREEN + "Successfully " + ChatFormatting.WHITE + "saved config: " + ChatFormatting.RED + "\"" + args[2] + "\"", 4, NotificationType.SUCCESS);
                                ConfigManager.getLoadedConfigs().clear();
                                NeverHook.instance.configManager.load();
                            } else {
                                ChatHelper.addChatMessage(ChatFormatting.RED + "Failed " + ChatFormatting.WHITE + "to save config: " + ChatFormatting.RED + "\"" + args[2] + "\"");
                                NotificationManager.publicity("Config", ChatFormatting.RED + "Failed " + ChatFormatting.WHITE + "to save config: " + ChatFormatting.RED + "\"" + args[2] + "\"", 4, NotificationType.ERROR);
                            }
                            break;
                        case "DELETE":
                            if (NeverHook.instance.configManager.deleteConfig(args[2])) {
                                ChatHelper.addChatMessage(ChatFormatting.GREEN + "Successfully " + ChatFormatting.WHITE + "deleted config: " + ChatFormatting.RED + "\"" + args[2] + "\"");
                                NotificationManager.publicity("Config", ChatFormatting.GREEN + "Successfully " + ChatFormatting.WHITE + "deleted config: " + ChatFormatting.RED + "\"" + args[2] + "\"", 4, NotificationType.SUCCESS);
                            } else {
                                ChatHelper.addChatMessage(ChatFormatting.RED + "Failed " + ChatFormatting.WHITE + "to delete config: " + ChatFormatting.RED + "\"" + args[2] + "\"");
                                NotificationManager.publicity("Config", ChatFormatting.RED + "Failed " + ChatFormatting.WHITE + "to delete config: " + ChatFormatting.RED + "\"" + args[2] + "\"", 4, NotificationType.ERROR);
                            }
                            break;
                    }
                } else if (args.length == 2 && upperCase.equalsIgnoreCase("LIST")) {
                    ChatHelper.addChatMessage(ChatFormatting.GREEN + "Configs:");
                    for (Config config : NeverHook.instance.configManager.getContents()) {
                        ChatHelper.addChatMessage(ChatFormatting.RED + config.getName());
                    }
                }
            } else {
                ChatHelper.addChatMessage(this.getUsage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}