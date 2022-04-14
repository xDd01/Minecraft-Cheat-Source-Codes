package io.github.nevalackin.client.api.core;

import io.github.nevalackin.client.api.account.AccountManager;
import io.github.nevalackin.client.api.binding.BindManager;
import io.github.nevalackin.client.api.config.ConfigManager;
import io.github.nevalackin.client.api.event.Event;
import io.github.nevalackin.client.api.file.FileManager;
import io.github.nevalackin.client.api.module.ModuleManager;
import io.github.nevalackin.client.api.notification.NotificationManager;
import io.github.nevalackin.client.api.script.ScriptManager;
import io.github.nevalackin.client.impl.event.game.CloseGameEvent;
import io.github.nevalackin.homoBus.bus.Bus;

public abstract class AbstractClientCore {

    private final String name;
    private final String version;

    private Bus<Event> eventBus;
    private FileManager fileManager;
    private ModuleManager moduleManager;
    private ConfigManager configManager;
    private AccountManager accountManager;
    private ScriptManager scriptManager;
    private NotificationManager notificationManager;
    private BindManager bindManager;

    public AbstractClientCore(final String name, final String version) {
        this.name = name;
        this.version = version;

        // FIND ME :: CloseGameEvent

        Runtime.getRuntime().addShutdownHook(
            new Thread(
                () -> this.getEventBus().post(
                    new CloseGameEvent())));
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public Bus<Event> getEventBus() {
        if (this.eventBus == null) {
            this.eventBus = this.getBusImpl();

            this.eventBus.subscribe(this);
        }

        return this.eventBus;
    }

    public FileManager getFileManager() {
        if (this.fileManager == null) {
            this.fileManager = this.getFileImpl();
        }

        return this.fileManager;
    }

    public NotificationManager getNotificationManager() {
        if (this.notificationManager == null) {
            this.notificationManager = this.getNotificationManagerImpl();
        }

        return this.notificationManager;
    }

    public ModuleManager getModuleManager() {
        if (this.moduleManager == null) {
            this.moduleManager = this.getModuleManagerImpl();
        }

        return this.moduleManager;
    }

    public ConfigManager getConfigManager() {
        if (this.configManager == null) {
            this.configManager = this.getConfigManagerImpl();
        }

        return this.configManager;
    }

    public AccountManager getAccountManager() {
        if (this.accountManager == null) {
            this.accountManager = this.getAccountManagerImpl();
        }

        return this.accountManager;
    }

    public ScriptManager getScriptManager() {
        if (this.scriptManager == null) {
            this.scriptManager = this.getScriptManagerImpl();
        }

        return this.scriptManager;
    }

    public BindManager getBindManager() {
        if (this.bindManager == null) {
            this.bindManager = this.getBindManagerImpl();
        }

        return this.bindManager;
    }

    protected abstract Bus<Event> getBusImpl();
    protected abstract FileManager getFileImpl();
    protected abstract ModuleManager getModuleManagerImpl();
    protected abstract ConfigManager getConfigManagerImpl();
    protected abstract AccountManager getAccountManagerImpl();
    protected abstract ScriptManager getScriptManagerImpl();
    protected abstract NotificationManager getNotificationManagerImpl();
    protected abstract BindManager getBindManagerImpl();

}
