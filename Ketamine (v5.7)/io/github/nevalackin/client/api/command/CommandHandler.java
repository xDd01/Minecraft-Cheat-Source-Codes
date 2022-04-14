package io.github.nevalackin.client.api.command;

import io.github.nevalackin.client.impl.core.KetamineClient;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public final class CommandHandler {

    private final KetamineClient instance = KetamineClient.getInstance();
    private final CommandRegistry registry;

    public CommandHandler(CommandRegistry registry) {
        this.registry = registry;
    }

    public void handle(String message) {
        Map<String, Command> aliasesMap = registry.getAliasesMap();

        String[] args = message.replace("\\s{2,}", " ").substring(1).split(" ");
        String inputCommand = args[0].toLowerCase(Locale.ROOT);
        if (!args[0].isEmpty()) {
            Command command = aliasesMap.get(inputCommand);
            if (command != null) {
                command.execute(args);
                return;
            }
        }
        String similarCommands = aliasesMap.keySet().stream().filter(c -> c.startsWith(inputCommand)).collect(Collectors.joining(", "));
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("\u00A7cCommand Not Found"));
        if (similarCommands.length() > 0) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Similar commands: " + similarCommands));
        }
    }
}