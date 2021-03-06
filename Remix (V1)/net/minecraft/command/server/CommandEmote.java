package net.minecraft.command.server;

import net.minecraft.entity.player.*;
import net.minecraft.server.*;
import net.minecraft.command.*;
import net.minecraft.util.*;
import java.util.*;

public class CommandEmote extends CommandBase
{
    @Override
    public String getCommandName() {
        return "me";
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
    
    @Override
    public String getCommandUsage(final ICommandSender sender) {
        return "commands.me.usage";
    }
    
    @Override
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length <= 0) {
            throw new WrongUsageException("commands.me.usage", new Object[0]);
        }
        final IChatComponent var3 = CommandBase.getChatComponentFromNthArg(sender, args, 0, !(sender instanceof EntityPlayer));
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("chat.type.emote", new Object[] { sender.getDisplayName(), var3 }));
    }
    
    @Override
    public List addTabCompletionOptions(final ICommandSender sender, final String[] args, final BlockPos pos) {
        return CommandBase.getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
    }
}
