/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.command.impl;

import cafe.corrosion.Corrosion;
import cafe.corrosion.attributes.CommandAttributes;
import cafe.corrosion.command.ICommand;
import cafe.corrosion.config.ConfigManager;
import cafe.corrosion.config.base.Config;
import cafe.corrosion.config.base.impl.DynamicConfig;
import cafe.corrosion.util.player.PlayerUtil;
import java.util.List;

@CommandAttributes(name="config")
public class ConfigCommand
implements ICommand {
    private static final String BASE_ERROR = "Try -config (load/save/web/list) [name]";

    @Override
    public void handle(String[] args) {
        ConfigManager configManager = Corrosion.INSTANCE.getConfigManager();
        if (args.length != 2) {
            if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
                List<String> configNames = configManager.getConfigNames();
                if (configNames.size() == 0) {
                    PlayerUtil.sendMessage("No configs were found!");
                    return;
                }
                String[] array = (String[])configNames.stream().map(config -> {
                    if (!config.contains("-web")) {
                        return config;
                    }
                    String replaced = config.replace("-web", "");
                    return replaced + " (&cWeb&7)";
                }).toArray(String[]::new);
                PlayerUtil.sendMessage("Found the following configurations:");
                PlayerUtil.sendMessage(array);
                return;
            }
            PlayerUtil.sendMessage(BASE_ERROR);
            return;
        }
        String action = args[0];
        String name = args[1];
        switch (action.toLowerCase()) {
            case "load": {
                Config config2 = configManager.getConfig(name, false);
                if (config2 == null) {
                    PlayerUtil.sendMessage(BASE_ERROR);
                    return;
                }
                config2.load(name);
                PlayerUtil.sendMessage("Successfully loaded config " + name + "!");
                break;
            }
            case "save": {
                configManager.saveConfig(name);
                PlayerUtil.sendMessage("Successfully saved config " + name + "!");
                break;
            }
            case "web": {
                Config config3 = configManager.getConfig(name, true);
                if (config3 == null) {
                    PlayerUtil.sendMessage("Searching for a web config named " + name + "!");
                    new DynamicConfig(name);
                    return;
                }
                config3.load(name);
            }
        }
    }
}

