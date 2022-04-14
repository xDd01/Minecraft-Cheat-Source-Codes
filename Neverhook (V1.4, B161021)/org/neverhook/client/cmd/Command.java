package org.neverhook.client.cmd;

@FunctionalInterface
public interface Command {
    void execute(String... strings);
}