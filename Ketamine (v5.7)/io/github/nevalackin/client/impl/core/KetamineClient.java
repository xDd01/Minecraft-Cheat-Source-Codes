package io.github.nevalackin.client.impl.core;

import com.google.gson.JsonObject;
import io.github.nevalackin.client.api.account.AccountManager;
import io.github.nevalackin.client.api.binding.Bind;
import io.github.nevalackin.client.api.binding.BindManager;
import io.github.nevalackin.client.api.binding.BindType;
import io.github.nevalackin.client.api.binding.Bindable;
import io.github.nevalackin.client.api.command.CommandRegistry;
import io.github.nevalackin.client.api.config.ConfigManager;
import io.github.nevalackin.client.api.core.AbstractClientCore;
import io.github.nevalackin.client.api.event.Event;
import io.github.nevalackin.client.api.file.FileManager;
import io.github.nevalackin.client.api.module.ModuleManager;
import io.github.nevalackin.client.api.notification.NotificationManager;
import io.github.nevalackin.client.api.script.ScriptManager;
import io.github.nevalackin.client.api.ui.cfont.CustomFontRenderer;
import io.github.nevalackin.client.impl.account.AccountManagerImpl;
import io.github.nevalackin.client.impl.binding.BindManagerImpl;
import io.github.nevalackin.client.impl.config.ConfigManagerImpl;
import io.github.nevalackin.client.impl.event.game.CloseGameEvent;
import io.github.nevalackin.client.impl.event.game.input.InputType;
import io.github.nevalackin.client.impl.file.FileManagerImpl;
import io.github.nevalackin.client.impl.module.ModuleManagerImpl;
import io.github.nevalackin.client.impl.notification.NotificationManagerImpl;
import io.github.nevalackin.client.impl.script.ScriptManagerImpl;
import io.github.nevalackin.client.impl.ui.click.GuiUIScreen;
import io.github.nevalackin.client.impl.ui.nl.GuiNLUIScreen;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import io.github.nevalackin.homoBus.bus.Bus;
import io.github.nevalackin.homoBus.bus.impl.EventBus;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import store.intent.intentguard.annotation.Native;
import viamcp.ViaMCP;

@Native
public final class KetamineClient extends AbstractClientCore {

    private static KetamineClient instance;

    public CustomFontRenderer fontRenderer;

    private GuiScreen uiScreen;
    private GuiUIScreen dropdownGUI;
    private GuiNLUIScreen neverloseGUI;

    private long startTime;

    public ModuleManagerImpl moduleManager;

    public FileManagerImpl fileManager;
    public AccountManagerImpl accountManager;
    public BindManagerImpl bindManager;
    public ConfigManagerImpl configManager;
    public NotificationManagerImpl notificationManager;


    public KetamineClient() {
        super("Ketamine", "v5.7");
    }

    public void init() {
        this.getFileManager();
        this.getBindManager().register(openCloseUI(), new Bind(InputType.KEYBOARD, Keyboard.KEY_RSHIFT, BindType.TOGGLE));
        this.getAccountManager().load();
        this.getBindManager().load();
        this.getConfigManager().load("default");
        dropdownGUI = new GuiUIScreen();
        neverloseGUI = new GuiNLUIScreen();

        try {
            ViaMCP.getInstance().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bindable openCloseUI() {

        this.uiScreen = new GuiUIScreen();

        return new Bindable() {
            @Override
            public String getName() {
                return "UI";
            }

            @Override
            public void setActive(boolean active) {
                Minecraft.getMinecraft().displayGuiScreen(active ? dropdownGUI : null);
            }

            @Override
            public boolean isActive() {
                return Minecraft.getMinecraft().currentScreen == uiScreen;
            }

            @Override
            public void loadBind(JsonObject object) {

            }

            @Override
            public void saveBind(JsonObject object) {

            }
        };
    }

    public void updateDiscordRPC(final String msg) {
        final DiscordRichPresence rpc = new DiscordRichPresence.Builder(msg)
                .setSmallImage("rpc", "Join")
                .setBigImage("big_rpc", "discord.gg/6zmsj9m4Ta")
                .setStartTimestamps(System.currentTimeMillis())
                .build();
        DiscordRPC.discordUpdatePresence(rpc);
    }

    @EventLink
    private final Listener<CloseGameEvent> onCloseGame = event -> {
        this.getConfigManager().save("default");
        this.getBindManager().save();
        this.getAccountManager().save();
        DiscordRPC.discordShutdown();
    };

    @Override
    protected Bus<Event> getBusImpl() {
        return new EventBus<>();
    }

    @Override
    protected FileManager getFileImpl() {
        return new FileManagerImpl();
    }

    @Override
    protected ModuleManager getModuleManagerImpl() {
        return moduleManager;
    }

    @Override
    protected ConfigManager getConfigManagerImpl() {
        return new ConfigManagerImpl();
    }

    @Override
    protected AccountManager getAccountManagerImpl() {
        return new AccountManagerImpl();
    }

    @Override
    protected ScriptManager getScriptManagerImpl() {
        return new ScriptManagerImpl();
    }

    public CustomFontRenderer getFontRenderer() {
        return fontRenderer;
    }

    @Override
    protected NotificationManager getNotificationManagerImpl() {
        return new NotificationManagerImpl();
    }

    @Override
    protected BindManager getBindManagerImpl() {
        return new BindManagerImpl();
    }

    public CommandRegistry getCommandRegistryImpl() {
        return new CommandRegistry();
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public GuiUIScreen getDropdownGUI() {
        return dropdownGUI;
    }

    public static KetamineClient getInstance() {
        if (instance == null) {
            instance = new KetamineClient();
        }

        return instance;
    }
}
