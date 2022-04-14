package me.spec.eris.client.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import me.spec.eris.api.manager.Manager;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.api.module.Module;
import me.spec.eris.client.modules.client.AntiCrash;
import me.spec.eris.client.modules.client.ClickGUI;
import me.spec.eris.client.modules.client.Disabler;
import me.spec.eris.client.modules.combat.*;
import me.spec.eris.client.modules.misc.AntiDesync;
import me.spec.eris.client.modules.movement.*;
import me.spec.eris.client.modules.persistance.FlagDetection;
import me.spec.eris.client.modules.player.AntiVoid;
import me.spec.eris.client.modules.player.ChestSteal;
import me.spec.eris.client.modules.player.InventoryManager;
import me.spec.eris.client.modules.player.NoFall;
import me.spec.eris.client.modules.player.NoRotate;
import me.spec.eris.client.modules.player.Phase;
import me.spec.eris.client.modules.render.Animations;
import me.spec.eris.client.modules.render.ESP;
import me.spec.eris.client.modules.render.HUD;
import me.spec.eris.client.modules.render.Racist;
import me.spec.eris.client.security.checks.Heartbeat;

public class ModuleManager extends Manager<Module> {

    @Override
    public void loadManager() {

        addToManagerArraylist(new Racist("Racism"));
        /*
        Combat
         */
        addToManagerArraylist(new AntiBot ("GhostNiggers"));
        addToManagerArraylist(new AutoClicker("AutoClicker"));
        addToManagerArraylist(new Killaura("BeanerBeater"));
        addToManagerArraylist(new Velocity("FatRomanian"));
        addToManagerArraylist(new Criticals("BalkinHardHitter"));
        addToManagerArraylist(new TargetStrafe("UrAsianBolt"));

        /*
        Movement
         */
        addToManagerArraylist(new Step("WhitesCantJump"));
        addToManagerArraylist(new Speed("ChasedByCops"));
        addToManagerArraylist(new Flight("SpiritAirlines"));
        addToManagerArraylist(new Sprint("AirforceOnes"));
        addToManagerArraylist(new GuiMove("T-Mobile"));
        addToManagerArraylist(new Scaffold("StevePlaceBlockForYou"));
        addToManagerArraylist(new Longjump("NiggerJump"));
        addToManagerArraylist(new NoSlowDown("Treadmill"));

        /*
        Misc
         */
        addToManagerArraylist(new Disabler("AnticheatAutism"));
        addToManagerArraylist(new ChestSteal("AutoNigger"));
        addToManagerArraylist(new InventoryManager("HomelessShoppingkart"));
        addToManagerArraylist(new AntiDesync("StopStealingBlocks"));

        /*
        Player
         */
        addToManagerArraylist(new Phase("GoThroughBlocks"));
        addToManagerArraylist(new NoFall("AirforceActivity"));
        addToManagerArraylist(new AntiVoid("ConsutrctionWorker"));
        addToManagerArraylist(new NoRotate("AntiBackhand"));

        /*
        Visual
         */
        addToManagerArraylist(new Animations("FancyGlock17"));
        addToManagerArraylist(new HUD("SeeShitAppear"));
        addToManagerArraylist(new ESP("NiggerFinder"));

        /*
        Client
        */
        addToManagerArraylist(new ClickGUI());
        addToManagerArraylist(new AntiCrash());

        /*
        Persist
         */
        addToManagerArraylist(new FlagDetection());
        addToManagerArraylist(new Heartbeat());
    }

    public void onKey(int key) {  
        managerArraylist.stream().filter(module -> module.getKey() == key).forEach(module -> module.toggle(true)); 
    }

    public Module getModuleByName(String name) {
        return managerArraylist.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<Module> getModules() {
        return getManagerArraylist();
    }

    public List<Module> getModulesInCategory(ModuleCategory moduleCategory) {
        return managerArraylist.stream().filter(module -> module.getCategory() == moduleCategory).collect(Collectors.toList());
    }

    public boolean isEnabled(Class<?> clazz) {
        return getModuleByClass(clazz).isToggled();
    }

    public Module getModuleByClass(Class<?> clazz) {
        return getManagerArraylist().stream().filter(module -> module.getClass().equals(clazz)).findFirst().orElse(null);
    }

    public List<Module> getModulesForRender() {
        return getManagerArraylist().stream().filter(module -> module.isToggled() && checkVisibility(module)).collect(Collectors.toList());
    }

    public boolean checkVisibility(Module module) {
        return module.getClass() != HUD.class && !module.isHidden();
    }
}
