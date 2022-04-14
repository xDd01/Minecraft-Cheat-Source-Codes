package me.spec.eris.client.antiflag.prioritization;

import java.util.ArrayList;

import me.spec.eris.Eris;
import me.spec.eris.api.module.Module;
import me.spec.eris.client.antiflag.prioritization.enums.ModulePriority;
import me.spec.eris.client.antiflag.prioritization.enums.ModuleType;
import me.spec.eris.client.modules.movement.Flight;
import me.spec.eris.client.modules.movement.Longjump;
import me.spec.eris.client.modules.movement.Speed;

/*
 *
 * Module Priority
 *
 * @author: Cystems 9/15/11:55
 *
 * Description:
 * Module Priority allows us to use multiple modules at once without flagging, perfected integration of movement modules
 *
 * Toggling on speed will no longer effect fly or cause lag backs
 *
 * Although some clients have attempted to impliment this feature by hard coding it into modules or by event system priority, it has never been done before.
 *
 * This way allows each module to have a enforced delay, a small delay of one or two ticks that is barely noticable by the end user will make the
 * user able to use longjump after fly without interruption
 *
 * So I am doing it.
 */
public class ModulePrioritizer {

    public static boolean isModuleUsable(Module module) {
        ArrayList<Module> modules = new ArrayList<Module>();
        Eris.getInstance().moduleManager.getModules().forEach(mod ->
                {
                    if (mod.getModuleType().equals(ModuleType.FLAGGABLE) && !modules.contains(mod) && mod.getModulePriority() != ModulePriority.LOWEST)
                        modules.add(mod);
                }
        );
        if (!modules.isEmpty()) {

            modules.sort((mod1, mod2) -> Integer.compare(mod1.getModulePriority().ordinal(), mod2.getModulePriority().ordinal()));
            modules.sort((mod1, mod2) -> Boolean.compare(mod1.isToggled(), mod2.isToggled()));

            if (modules.get(0) != null) {
                try {
                    modules.forEach(mod -> {

                        if (modules.get(modules.indexOf(mod)) != modules.get(0)) {
                            modules.get(modules.indexOf(mod)).forcedDelay[modules.indexOf(mod)] = modules.get(modules.indexOf(mod)).getModulePriority().ordinal() + 2;
                        } else {
                            if (modules.get(modules.indexOf(mod)).forcedDelay[modules.indexOf(mod)] > 0)
                                modules.get(modules.indexOf(mod)).forcedDelay[modules.indexOf(mod)]--;
                        }
                    });
                } catch (ArrayIndexOutOfBoundsException eeee) {
                }
                ;
                return modules.get(0) == module;
            }
        }

        return false;
    }

    public static boolean flaggableMovementModules() {
        return Eris.INSTANCE.moduleManager.isEnabled(Speed.class) || Eris.INSTANCE.moduleManager.isEnabled(Flight.class) || Eris.INSTANCE.moduleManager.isEnabled(Longjump.class);
    }
}
