package net.minecraft.command.server;

import net.minecraft.server.*;
import net.minecraft.world.*;
import net.minecraft.command.*;

public class CommandPublishLocalServer extends CommandBase
{
    @Override
    public String getCommandName() {
        return "publish";
    }
    
    @Override
    public String getCommandUsage(final ICommandSender sender) {
        return "commands.publish.usage";
    }
    
    @Override
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        final String var3 = MinecraftServer.getServer().shareToLAN(WorldSettings.GameType.SURVIVAL, false);
        if (var3 != null) {
            CommandBase.notifyOperators(sender, this, "commands.publish.started", var3);
        }
        else {
            CommandBase.notifyOperators(sender, this, "commands.publish.failed", new Object[0]);
        }
    }
}
