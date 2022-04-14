package me.spec.eris.client.commands;

import me.spec.eris.Eris;
import me.spec.eris.api.command.Command;
import me.spec.eris.utils.player.PlayerUtils;
import net.minecraft.util.EnumChatFormatting;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "gives all commands");
    }

    @Override
    public void execute(String[] commandArguments) {
        PlayerUtils.tellUserUnformatted(EnumChatFormatting.GRAY + ">-----------------------------");
        PlayerUtils.tellUserUnformatted(EnumChatFormatting.GRAY + "> " + EnumChatFormatting.RED + EnumChatFormatting.BOLD + Eris.getInstance().getClientName() + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.WHITE + Eris.getInstance().getClientBuild());
        PlayerUtils.tellUserUnformatted(EnumChatFormatting.GRAY + "> " + "Developed by " +EnumChatFormatting.RED + EnumChatFormatting.BOLD + "Eris Team" + EnumChatFormatting.GRAY + ":");
        PlayerUtils.tellUserUnformatted(EnumChatFormatting.GRAY + "> (Kaido, Spec, Vaziak)");
        PlayerUtils.tellUserUnformatted(EnumChatFormatting.GRAY + "> ");
        PlayerUtils.tellUserUnformatted(EnumChatFormatting.GRAY + "> " + EnumChatFormatting.GOLD + EnumChatFormatting.BOLD + "With credits to" + EnumChatFormatting.GRAY + ":");
        PlayerUtils.tellUserUnformatted(EnumChatFormatting.GRAY + "> (Matt (SummerDev), God (Purity), Ice, N4tive, Fr0sty)");
        PlayerUtils.tellUserUnformatted(EnumChatFormatting.GRAY + "> ");
        PlayerUtils.tellUserUnformatted(EnumChatFormatting.GRAY + "> " + "Commands:");
        Eris.getInstance().commandManager.getManagerArraylist().forEach(this::getCommandNameAndDescription);
        PlayerUtils.tellUserUnformatted(EnumChatFormatting.GRAY + ">-----------------------------");
    }

    private void getCommandNameAndDescription(Command command) {
        if(!command.getCommandName().equalsIgnoreCase("help")) {
            PlayerUtils.tellUserUnformatted(EnumChatFormatting.GRAY + "> " + EnumChatFormatting.RED + EnumChatFormatting.BOLD + Eris.getInstance().getCommandPrefix() + command.getCommandName() + EnumChatFormatting.WHITE + " - " + command.getCommandDescription());
        }
    }
}
