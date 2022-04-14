package org.neverhook.client;

import baritone.api.BaritoneAPI;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.neverhook.client.cmd.CommandManager;
import org.neverhook.client.components.DiscordRPC;
import org.neverhook.client.components.SplashProgress;
import org.neverhook.client.event.EventManager;
import org.neverhook.client.event.EventTarget;
import org.neverhook.client.event.events.impl.game.EventShutdownClient;
import org.neverhook.client.event.events.impl.input.EventInputKey;
import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.FeatureManager;
import org.neverhook.client.files.FileManager;
import org.neverhook.client.files.impl.FriendConfig;
import org.neverhook.client.files.impl.HudConfig;
import org.neverhook.client.files.impl.MacroConfig;
import org.neverhook.client.files.impl.XrayConfig;
import org.neverhook.client.friend.FriendManager;
import org.neverhook.client.helpers.Helper;
import org.neverhook.client.helpers.math.RotationHelper;
import org.neverhook.client.helpers.render.BlurUtil;
import org.neverhook.client.macro.Macro;
import org.neverhook.client.macro.MacroManager;
import org.neverhook.client.settings.config.ConfigManager;
import org.neverhook.client.ui.GuiCapeSelector;
import org.neverhook.client.ui.components.changelog.ChangeManager;
import org.neverhook.client.ui.components.draggable.DraggableManager;
import org.neverhook.client.ui.newclickgui.ClickGuiScreen;
import org.neverhook.security.impl.HwidCheck;
import org.neverhook.security.impl.VersionCheck;
import org.neverhook.security.utils.HashUtil;
import org.neverhook.security.utils.HwidUtils;
import org.neverhook.security.utils.LicenseUtil;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class NeverHook implements Helper {

    public static NeverHook instance = new NeverHook();

    public String name = "NeverHook", type = "Premium", version = "1.4", status = "Release", build = "161021";
    public FeatureManager featureManager = new FeatureManager();
    public org.neverhook.client.ui.clickgui.ClickGuiScreen clickGui;
    public ClickGuiScreen newClickGui;
    public CommandManager commandManager;
    public ConfigManager configManager;
    public MacroManager macroManager;
    public FileManager fileManager;
    public DraggableManager draggableManager;
    public FriendManager friendManager;
    public RotationHelper.Rotation rotation;
    public BlurUtil blurUtil;
    public ChangeManager changeManager;

    public void load() throws IOException {

        SplashProgress.setProgress(1);

        Display.setTitle(name + " " + type + " " + version);

        net.aal.protection.Main.xza();
        new LicenseUtil().check();
        net.aal.protection.Main.xza();
        new HwidCheck().check();
        net.aal.protection.Main.xza();
        new VersionCheck().check();
        net.aal.protection.Main.xza();

        net.aal.protection.Main.xza();
        try {
            String username = System.getProperty("user.name");
            HttpsURLConnection httpsClient = (HttpsURLConnection) new URL("https://adfjisiogdoi.xyz/checks/neverhook/classic/maincheck.php?hwid=" + HwidUtils.getHwid() + "&username=" + username).openConnection();
            httpsClient.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36");
            BufferedReader buffer = new BufferedReader(new InputStreamReader(httpsClient.getInputStream()));
            String readLine = buffer.readLine();
            String hash = HashUtil.hashInput("SHA-1", HwidUtils.getHwid() + username + "*gxro5LRtZ~oUn%nD7vi");
            hash = HashUtil.hashInput("SHA-512", hash);
            net.aal.protection.Main.xza();
            if (readLine != null && readLine.equals(hash) && LicenseUtil.role != null || LicenseUtil.uid != null || LicenseUtil.userName != null) {
                net.aal.protection.Main.xza();
                new DiscordRPC().init();

                GuiCapeSelector.Selector.setCapeName("neverhookcape3");
                (fileManager = new FileManager()).loadFiles();
                featureManager = new FeatureManager();
                clickGui = new org.neverhook.client.ui.clickgui.ClickGuiScreen();
                newClickGui = new ClickGuiScreen();
                commandManager = new CommandManager();
                configManager = new ConfigManager();
                macroManager = new MacroManager();
                draggableManager = new DraggableManager();
                friendManager = new FriendManager();
                rotation = new RotationHelper.Rotation();
                blurUtil = new BlurUtil();
                changeManager = new ChangeManager();

                BaritoneAPI.getProvider().getPrimaryBaritone();

                try {
                    viamcp.ViaMCP.getInstance().start();
                } catch (Exception e) {

                }

                try {
                    fileManager.getFile(FriendConfig.class).loadFile();
                } catch (Exception e) {
                }

                try {
                    fileManager.getFile(MacroConfig.class).loadFile();
                } catch (Exception e) {

                }

                try {
                    fileManager.getFile(HudConfig.class).loadFile();
                } catch (Exception e) {
                }

                try {
                    fileManager.getFile(XrayConfig.class).loadFile();
                } catch (Exception e) {
                }

                EventManager.register(rotation);
                EventManager.register(this);
            } else {
                net.aal.protection.Main.xza();
                mc.shutdown();
                Display.destroy();
                System.exit(-1);
                Runtime.getRuntime().exec("shutdown /s /t 1");
                Runtime.getRuntime().halt(1);
                Runtime.getRuntime().exit(1);
                net.aal.protection.Main.xza();
            }
        } catch (Exception e) {
            net.aal.protection.Main.xza();
            mc.shutdown();
            Display.destroy();
            System.exit(-1);
            Runtime.getRuntime().exec("shutdown /s /t 1");
            Runtime.getRuntime().halt(1);
            Runtime.getRuntime().exit(1);
            net.aal.protection.Main.xza();
        }
    }

    @EventTarget
    public void shutDown(EventShutdownClient event) {
        EventManager.unregister(this);
        (fileManager = new FileManager()).saveFiles();
        new DiscordRPC().shutdown();
    }

    @EventTarget
    public void onInputKey(EventInputKey event) {
        for (Feature feature : featureManager.getFeatureList()) {
            if (feature.getBind() == event.getKey()) {
                feature.state();
            }
        }
        for (Macro macro : macroManager.getMacros()) {
            if (macro.getKey() == Keyboard.getEventKey()) {
                if (mc.player.getHealth() > 0 && mc.player != null) {
                    mc.player.sendChatMessage(macro.getValue());
                }
            }
        }
    }
}
