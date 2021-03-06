package net.minecraft.command.server;

import net.minecraft.server.*;
import net.minecraft.command.*;
import net.minecraft.util.*;
import java.util.*;

public class CommandBroadcast extends CommandBase
{
    @Override
    public String getCommandName() {
        return "say";
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return 1;
    }
    
    @Override
    public String getCommandUsage(final ICommandSender sender) {
        return "commands.say.usage";
    }
    
    @Override
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length > 0 && args[0].length() > 0) {
            final IChatComponent ichatcomponent = CommandBase.getChatComponentFromNthArg(sender, args, 0, true);
            MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("chat.type.announcement", new Object[] { sender.getDisplayName(), ichatcomponent }));
            return;
        }
        throw new WrongUsageException("commands.say.usage", new Object[0]);
    }
    
    @Override
    public List<String> addTabCompletionOptions(final ICommandSender sender, final String[] args, final BlockPos pos) {
        return (args.length >= 1) ? CommandBase.getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
    }
}
