package io.github.nevalackin.client.api.script;

public interface ScriptManager {

    String EXTENSION = ".js";

    boolean enable(final String script, final boolean enabled);
}
