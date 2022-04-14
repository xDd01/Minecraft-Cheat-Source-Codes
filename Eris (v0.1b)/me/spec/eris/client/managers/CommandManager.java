package me.spec.eris.client.managers;

import me.spec.eris.Eris;
import me.spec.eris.api.command.Command;
import me.spec.eris.client.commands.*;
import me.spec.eris.api.event.Event;
import me.spec.eris.client.events.chat.ChatMessageEvent;
import me.spec.eris.api.manager.Manager;

import java.util.Iterator;

public class CommandManager extends Manager<Command> {
    
    private final String prefix = Eris.getInstance().getCommandPrefix();
    
    @Override
    public void loadManager() {
        addToManagerArraylist(new HelpCommand(),
                new ToggleCommand(),
                new BindCommand(),
                new ConfigCommand(),
                new FriendCommand(),
                new InfoCommand(),
                new NameCommand(),
                new ToggleCommand(),
                new IPCommand());

    }

    public void onEvent(Event e) {
        if (e instanceof  ChatMessageEvent) {
            ChatMessageEvent event = (ChatMessageEvent) e;
            String chatMessage = event.getChatMessage();

            if(chatMessage.startsWith(prefix)) {
                e.setCancelled();
                String noPrefixChatMessage = chatMessage.replace(Eris.getInstance().getCommandPrefix(), "");
                String[] commandArguments = noPrefixChatMessage.split(" ");
                for(Command command : getManagerArraylist()) {
                    if(commandArguments[0].equalsIgnoreCase(command.getCommandName())) {
                        command.execute(commandArguments);
                    }
                }
            }
        }
    }

}
