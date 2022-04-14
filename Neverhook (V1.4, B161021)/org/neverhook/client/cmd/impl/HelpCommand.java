package org.neverhook.client.cmd.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import org.neverhook.client.cmd.CommandAbstract;
import org.neverhook.client.helpers.misc.ChatHelper;

public class HelpCommand extends CommandAbstract {

    public HelpCommand() {
        super("help", "help", ".help", "help");
    }

    @Override
    public void execute(String... args) {
        if (args.length == 1) {
            if (args[0].equals("help")) {
                ChatHelper.addChatMessage(ChatFormatting.RED + "All Commands:");
                ChatHelper.addChatMessage(ChatFormatting.DARK_AQUA + ".bind");
                ChatHelper.addChatMessage(ChatFormatting.DARK_AQUA + ".macro");
                ChatHelper.addChatMessage(ChatFormatting.DARK_AQUA + ".clip");
                ChatHelper.addChatMessage(ChatFormatting.DARK_AQUA + ".fakehack");
                ChatHelper.addChatMessage(ChatFormatting.DARK_AQUA + ".friend");
                ChatHelper.addChatMessage(ChatFormatting.DARK_AQUA + ".config");
                ChatHelper.addChatMessage(ChatFormatting.DARK_AQUA + ".clip");
                ChatHelper.addChatMessage(ChatFormatting.DARK_AQUA + ".xray");
            }
        } else {
            ChatHelper.addChatMessage(this.getUsage());
        }
    }
}
