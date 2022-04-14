/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm;

import org.objectweb.asm.Constants;

public abstract class ModuleVisitor {
    protected final int api;
    protected ModuleVisitor mv;

    public ModuleVisitor(int api2) {
        this(api2, null);
    }

    public ModuleVisitor(int api2, ModuleVisitor moduleVisitor) {
        if (api2 != 589824 && api2 != 524288 && api2 != 458752 && api2 != 393216 && api2 != 327680 && api2 != 262144 && api2 != 0x10A0000) {
            throw new IllegalArgumentException("Unsupported api " + api2);
        }
        if (api2 == 0x10A0000) {
            Constants.checkAsmExperimental(this);
        }
        this.api = api2;
        this.mv = moduleVisitor;
    }

    public void visitMainClass(String mainClass) {
        if (this.mv != null) {
            this.mv.visitMainClass(mainClass);
        }
    }

    public void visitPackage(String packaze) {
        if (this.mv != null) {
            this.mv.visitPackage(packaze);
        }
    }

    public void visitRequire(String module, int access, String version) {
        if (this.mv != null) {
            this.mv.visitRequire(module, access, version);
        }
    }

    public void visitExport(String packaze, int access, String ... modules) {
        if (this.mv != null) {
            this.mv.visitExport(packaze, access, modules);
        }
    }

    public void visitOpen(String packaze, int access, String ... modules) {
        if (this.mv != null) {
            this.mv.visitOpen(packaze, access, modules);
        }
    }

    public void visitUse(String service) {
        if (this.mv != null) {
            this.mv.visitUse(service);
        }
    }

    public void visitProvide(String service, String ... providers) {
        if (this.mv != null) {
            this.mv.visitProvide(service, providers);
        }
    }

    public void visitEnd() {
        if (this.mv != null) {
            this.mv.visitEnd();
        }
    }
}

