/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.command.impl;

import cafe.corrosion.Corrosion;
import cafe.corrosion.attributes.CommandAttributes;
import cafe.corrosion.command.ICommand;
import cafe.corrosion.module.Module;
import cafe.corrosion.util.player.PlayerUtil;

@CommandAttributes(name="toggle")
public class ToggleCommand
implements ICommand {
    private static final String ERROR_MESSAGE = "Invalid syntax! Try -toggle (module name)";

    @Override
    public void handle(String[] args) {
        if (args.length != 1) {
            PlayerUtil.sendMessage(ERROR_MESSAGE);
            return;
        }
        String moduleName = args[0];
        Object module = Corrosion.INSTANCE.getModuleManager().getModule(moduleName);
        String toggleState = ((Module)module).isEnabled() ? "&cdisabled" : "&aenabled";
        ((Module)module).toggle();
        PlayerUtil.sendMessage("Successfully " + toggleState + " &7" + moduleName.toLowerCase() + "!");
    }
}

