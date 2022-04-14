/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.manager;

import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.module.impl.combat.AimAssist;
import cafe.corrosion.module.impl.combat.FastBow;
import cafe.corrosion.module.impl.combat.Hitboxes;
import cafe.corrosion.module.impl.combat.KillAura;
import cafe.corrosion.module.impl.combat.Regen;
import cafe.corrosion.module.impl.combat.TargetHud;
import cafe.corrosion.module.impl.combat.TargetStrafe;
import cafe.corrosion.module.impl.combat.Velocity;
import cafe.corrosion.module.impl.combat.auto.AutoArmor;
import cafe.corrosion.module.impl.combat.auto.AutoClicker;
import cafe.corrosion.module.impl.combat.auto.AutoPotion;
import cafe.corrosion.module.impl.combat.auto.AutoSoup;
import cafe.corrosion.module.impl.combat.auto.TriggerBot;
import cafe.corrosion.module.impl.exploit.Crasher;
import cafe.corrosion.module.impl.exploit.Disabler;
import cafe.corrosion.module.impl.exploit.Teleport;
import cafe.corrosion.module.impl.misc.AntiBot;
import cafe.corrosion.module.impl.misc.ChatBypass;
import cafe.corrosion.module.impl.misc.KillInsults;
import cafe.corrosion.module.impl.misc.Timer;
import cafe.corrosion.module.impl.movement.AntiAFK;
import cafe.corrosion.module.impl.movement.AutoJump;
import cafe.corrosion.module.impl.movement.AutoWalk;
import cafe.corrosion.module.impl.movement.Fly;
import cafe.corrosion.module.impl.movement.HighJump;
import cafe.corrosion.module.impl.movement.InventoryWalk;
import cafe.corrosion.module.impl.movement.LongJump;
import cafe.corrosion.module.impl.movement.NoSlowdown;
import cafe.corrosion.module.impl.movement.Speed;
import cafe.corrosion.module.impl.movement.Sprint;
import cafe.corrosion.module.impl.movement.Step;
import cafe.corrosion.module.impl.movement.Strafe;
import cafe.corrosion.module.impl.movement.VClip;
import cafe.corrosion.module.impl.player.ChestStealer;
import cafe.corrosion.module.impl.player.FireballBooster;
import cafe.corrosion.module.impl.player.InventoryManager;
import cafe.corrosion.module.impl.player.NoFall;
import cafe.corrosion.module.impl.player.PingSpoof;
import cafe.corrosion.module.impl.player.SafeWalk;
import cafe.corrosion.module.impl.player.Scaffold;
import cafe.corrosion.module.impl.player.SmoothChat;
import cafe.corrosion.module.impl.visual.Chams;
import cafe.corrosion.module.impl.visual.Coords;
import cafe.corrosion.module.impl.visual.ESP;
import cafe.corrosion.module.impl.visual.FullBright;
import cafe.corrosion.module.impl.visual.GUI;
import cafe.corrosion.module.impl.visual.HUD;
import cafe.corrosion.module.impl.visual.HypixelHelper;
import cafe.corrosion.module.impl.visual.ModuleList;
import cafe.corrosion.module.impl.visual.NameTags;
import cafe.corrosion.module.impl.visual.TabGUI;
import cafe.corrosion.module.impl.visual.TimeChanger;
import cafe.corrosion.util.Manager;
import java.util.stream.Stream;

public class ModuleManager
extends Manager<Module> {
    public void init() {
        Stream.of(new HUD(), new Velocity(), new Speed(), new Sprint(), new Hitboxes(), new KillAura(), new Chams(), new Fly(), new GUI(), new Disabler(), new NoFall(), new HighJump(), new Timer(), new AutoPotion(), new AutoSoup(), new Crasher(), new Regen(), new Teleport(), new AutoClicker(), new Scaffold(), new AutoArmor(), new InventoryManager(), new ChestStealer(), new InventoryWalk(), new KillInsults(), new ChatBypass(), new TimeChanger(), new NoSlowdown(), new FastBow(), new ESP(), new Strafe(), new PingSpoof(), new VClip(), new SmoothChat(), new LongJump(), new AntiBot(), new NameTags(), new Coords(), new TabGUI(), new ModuleList(), new AutoWalk(), new AutoJump(), new AntiAFK(), new HypixelHelper(), new SafeWalk(), new Step(), new TargetStrafe(), new AimAssist(), new TriggerBot(), new FullBright(), new TargetHud(), new FireballBooster()).sorted((o1, o2) -> {
            Class<?> c1 = o1.getClass();
            Class<?> c2 = o2.getClass();
            ModuleAttributes a1 = c1.getDeclaredAnnotation(ModuleAttributes.class);
            ModuleAttributes a2 = c2.getDeclaredAnnotation(ModuleAttributes.class);
            return a1.name().compareTo(a2.name());
        }).forEach(this::add);
    }

    public <T extends Module> T getModule(Class<? extends Module> clazz) {
        return (T)((Module)this.getObjects().stream().filter(object -> object.getClass().equals(clazz)).findFirst().orElse(null));
    }

    public <T extends Module> T getModule(String name) {
        return (T)((Module)this.getObjects().stream().filter(object -> object.getAttributes().name().equalsIgnoreCase(name)).findFirst().orElse(null));
    }
}

