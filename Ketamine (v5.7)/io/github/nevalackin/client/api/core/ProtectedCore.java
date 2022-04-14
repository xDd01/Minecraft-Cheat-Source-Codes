package io.github.nevalackin.client.api.core;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import io.github.nevalackin.client.api.module.Module;
import io.github.nevalackin.client.impl.account.AccountManagerImpl;
import io.github.nevalackin.client.impl.binding.BindManagerImpl;
import io.github.nevalackin.client.impl.config.ConfigManagerImpl;
import io.github.nevalackin.client.impl.core.KetamineClient;
import io.github.nevalackin.client.impl.file.FileManagerImpl;
import io.github.nevalackin.client.impl.module.ModuleManagerImpl;
import io.github.nevalackin.client.impl.module.combat.healing.AutoPot;
import io.github.nevalackin.client.impl.module.combat.healing.Regen;
import io.github.nevalackin.client.impl.module.combat.miniGames.CripsVsBloods;
import io.github.nevalackin.client.impl.module.combat.rage.Aura;
import io.github.nevalackin.client.impl.module.combat.rage.TargetStrafe;
import io.github.nevalackin.client.impl.module.combat.rage.Velocity;
import io.github.nevalackin.client.impl.module.misc.inventory.AutoTool;
import io.github.nevalackin.client.impl.module.misc.inventory.ChestStealer;
import io.github.nevalackin.client.impl.module.misc.inventory.Inventory;
import io.github.nevalackin.client.impl.module.misc.inventory.InventoryManager;
import io.github.nevalackin.client.impl.module.misc.player.*;
import io.github.nevalackin.client.impl.module.misc.world.FastBreak;
import io.github.nevalackin.client.impl.module.misc.world.Phase;
import io.github.nevalackin.client.impl.module.misc.world.Scaffold;
import io.github.nevalackin.client.impl.module.misc.world.Timer;
import io.github.nevalackin.client.impl.module.movement.extras.Flight;
import io.github.nevalackin.client.impl.module.movement.extras.LongJump;
import io.github.nevalackin.client.impl.module.movement.extras.Speed;
import io.github.nevalackin.client.impl.module.movement.main.*;
import io.github.nevalackin.client.impl.module.render.esp.Glow;
import io.github.nevalackin.client.impl.module.render.esp.OffScreenESP;
import io.github.nevalackin.client.impl.module.render.esp.esp.ESP;
import io.github.nevalackin.client.impl.module.render.model.Chams;
import io.github.nevalackin.client.impl.module.render.model.HurtEffect;
import io.github.nevalackin.client.impl.module.render.model.NoRender;
import io.github.nevalackin.client.impl.module.render.overlay.Camera;
import io.github.nevalackin.client.impl.module.render.overlay.Crosshair;
import io.github.nevalackin.client.impl.module.render.overlay.NoFOV;
import io.github.nevalackin.client.impl.module.render.overlay.NoOverlays;
import io.github.nevalackin.client.impl.module.render.self.ChinaHat;
import io.github.nevalackin.client.impl.module.render.self.SwingModifier;
import io.github.nevalackin.client.impl.module.render.self.ThirdPerson;
import io.github.nevalackin.client.impl.module.render.world.BlockOverlay;
import io.github.nevalackin.client.impl.module.render.world.WorldTime;
import io.github.nevalackin.client.impl.module.render.world.XRay;
import io.github.nevalackin.client.impl.notification.NotificationManagerImpl;
import io.github.nevalackin.client.impl.ui.cfont.FontGlyphs;
import io.github.nevalackin.client.impl.ui.cfont.MipMappedFontRenderer;
import io.github.nevalackin.client.impl.ui.hud.rendered.ArraylistModule;
import io.github.nevalackin.client.impl.ui.hud.rendered.PlayerInfoModule;
import io.github.nevalackin.client.impl.ui.hud.rendered.TargetHudModule;
import io.github.nevalackin.client.impl.ui.hud.rendered.WatermarkModule;
import io.github.nevalackin.client.util.misc.ResourceUtil;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import store.intent.intentguard.annotation.Bootstrap;
import store.intent.intentguard.annotation.Native;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

@Native
public class ProtectedCore {

    static CopyOnWriteArrayList<Module> modulesList = new CopyOnWriteArrayList<>();

    @Native
    @Bootstrap
    public static void start() {
        ClassToInstanceMap<Module> modules = populateInstanceMap(
                new Aura(),
                new CripsVsBloods(),
                new TargetStrafe(),
                new Velocity(),
                new Regen(),
                new AutoPot(),
                new Inventory(),
                new NoFall(),
                new ChestStealer(),
                new InventoryManager(),
                new NoRotate(),
                new AutoTool(),
                new PingSpoof(),
                new Blink(),
                new Phase(),
                new FastUse(),
                new Sprint(),
                new NoSlowDown(),
                new Speed(),
                new LongJump(),
                new Step(),
                new AutoTool(),
                new Scaffold(),
                new WorldTime(),
                new FastBreak(),
                new Timer(),
                new AutoHypixel(),
                new NoRender(),
                new OffScreenESP(),
                new Crosshair(),
                new Camera(),
                new NoOverlays(),
                new SwingModifier(),
                new AntiVoid(),
                new HurtEffect(),
                new XRay(),
                new Chams(),
                new BlockOverlay(),
                new ChinaHat(),
                new NoFOV(),
                new ThirdPerson(),
                new Glow(),
                new ArraylistModule(),
                new WatermarkModule(),
                new TargetHudModule(),
                new StaffAnalyzer(),
                new ESP(),
                new Flight(),
                new PlayerInfoModule()
        );

        KetamineClient.getInstance().notificationManager = new NotificationManagerImpl();
        KetamineClient.getInstance().fileManager = new FileManagerImpl();
        KetamineClient.getInstance().bindManager = new BindManagerImpl();
        KetamineClient.getInstance().accountManager = new AccountManagerImpl();

        KetamineClient.getInstance().fontRenderer = new MipMappedFontRenderer(new FontGlyphs(ResourceUtil.createFontTTF("fonts/Regular.ttf"), 500),
                new FontGlyphs(ResourceUtil.createFontTTF("fonts/Medium.ttf"), 700),
                new FontGlyphs(ResourceUtil.createFontTTF("fonts/Bold.ttf"), 900));

        KetamineClient.getInstance().moduleManager = new ModuleManagerImpl(modules);

        DiscordRPC.discordInitialize("877275167653498941", new DiscordEventHandlers.Builder().build(), true);

    }

    private static ClassToInstanceMap<Module> populateInstanceMap(Module... modules) {
        final ClassToInstanceMap<Module> instanceMap = MutableClassToInstanceMap.create();
        Arrays.stream(modules).forEach(module -> instanceMap.putInstance((Class<Module>) module.getClass(), module));
        return instanceMap;
    }
}
