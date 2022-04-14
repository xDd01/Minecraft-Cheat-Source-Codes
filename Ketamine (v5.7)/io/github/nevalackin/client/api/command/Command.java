package io.github.nevalackin.client.api.command;

import io.github.nevalackin.client.impl.core.KetamineClient;

public abstract class Command {

    private final String description;
    private final String[] aliases;

    public Command(String description, String... aliases) {
        this.description = description;
        this.aliases = aliases;
    }

    public abstract void execute(String[] args);

    public String getDescription() {
        return description;
    }

    public String[] getAliases() {
        return aliases;
    }

    @Override
    public String toString() {
        return KetamineClient.getInstance().getCommandRegistryImpl().PREFIX + aliases[0] + " - " + description;
    }
}