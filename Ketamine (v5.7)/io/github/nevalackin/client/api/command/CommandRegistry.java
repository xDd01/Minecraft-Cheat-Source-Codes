package io.github.nevalackin.client.api.command;

import java.util.*;

public final class CommandRegistry {

    public final String PREFIX = ".";

    private final List<Command> commands = new ArrayList<>();
    private final Map<String, Command> aliasesMap = new HashMap<>();

    private final CommandHandler handler;

    public CommandRegistry() {
        handler = new CommandHandler(this);

        register(new ConfigCommand());
    }

    public void register(Command command) {
        commands.add(command);
        for (String alias : command.getAliases()) {
            aliasesMap.put(alias, command);
        }
    }

    public List<Command> getCommands() {
        return commands;
    }

    public Map<String, Command> getAliasesMap() {
        return aliasesMap;
    }

    public CommandHandler getHandler() {
        return handler;
    }
}