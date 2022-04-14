/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.commons.Remapper;

public class ModuleRemapper
extends ModuleVisitor {
    protected final Remapper remapper;

    public ModuleRemapper(ModuleVisitor moduleVisitor, Remapper remapper) {
        this(589824, moduleVisitor, remapper);
    }

    protected ModuleRemapper(int api2, ModuleVisitor moduleVisitor, Remapper remapper) {
        super(api2, moduleVisitor);
        this.remapper = remapper;
    }

    public void visitMainClass(String mainClass) {
        super.visitMainClass(this.remapper.mapType(mainClass));
    }

    public void visitPackage(String packaze) {
        super.visitPackage(this.remapper.mapPackageName(packaze));
    }

    public void visitRequire(String module, int access, String version) {
        super.visitRequire(this.remapper.mapModuleName(module), access, version);
    }

    public void visitExport(String packaze, int access, String ... modules) {
        String[] remappedModules = null;
        if (modules != null) {
            remappedModules = new String[modules.length];
            for (int i2 = 0; i2 < modules.length; ++i2) {
                remappedModules[i2] = this.remapper.mapModuleName(modules[i2]);
            }
        }
        super.visitExport(this.remapper.mapPackageName(packaze), access, remappedModules);
    }

    public void visitOpen(String packaze, int access, String ... modules) {
        String[] remappedModules = null;
        if (modules != null) {
            remappedModules = new String[modules.length];
            for (int i2 = 0; i2 < modules.length; ++i2) {
                remappedModules[i2] = this.remapper.mapModuleName(modules[i2]);
            }
        }
        super.visitOpen(this.remapper.mapPackageName(packaze), access, remappedModules);
    }

    public void visitUse(String service) {
        super.visitUse(this.remapper.mapType(service));
    }

    public void visitProvide(String service, String ... providers) {
        String[] remappedProviders = new String[providers.length];
        for (int i2 = 0; i2 < providers.length; ++i2) {
            remappedProviders[i2] = this.remapper.mapType(providers[i2]);
        }
        super.visitProvide(this.remapper.mapType(service), remappedProviders);
    }
}

