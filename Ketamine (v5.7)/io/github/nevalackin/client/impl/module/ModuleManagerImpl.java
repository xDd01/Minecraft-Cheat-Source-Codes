package io.github.nevalackin.client.impl.module;

import com.google.common.collect.ClassToInstanceMap;
import io.github.nevalackin.client.api.module.Module;
import io.github.nevalackin.client.api.module.ModuleManager;
import io.github.nevalackin.client.impl.core.KetamineClient;
import io.github.nevalackin.client.impl.ui.hud.components.HudComponent;

import java.util.Collection;
import java.util.stream.Collectors;

public final class ModuleManagerImpl implements ModuleManager {

    private final ClassToInstanceMap<Module> moduleInstances;

    public ModuleManagerImpl(ClassToInstanceMap<Module> modules) {
        this.moduleInstances = modules;
    }

    @Override
    public <T extends Module> T getModule(final Class<T> clazz) {
        return this.moduleInstances.getInstance(clazz);
    }

    @Override
    public Module getModule(String name) {
        return null;
    }

    @Override
    public <T extends Module> void registerModule(final Class<T> clazz, final T module) {
        this.moduleInstances.putInstance(clazz, module);
    }

    public static Collection<HudComponent> getHudComponents() {
        return KetamineClient.getInstance().getModuleManager()
                .getModules().stream()
                .filter(feature -> feature instanceof HudComponent)
                .map(feature -> (HudComponent) feature)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Module> getModules() {
        return this.moduleInstances.values();
    }
}
