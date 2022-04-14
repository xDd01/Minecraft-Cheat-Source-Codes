/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.util;

import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.util.Printer;

public final class TraceModuleVisitor
extends ModuleVisitor {
    public final Printer p;

    public TraceModuleVisitor(Printer printer) {
        this(null, printer);
    }

    public TraceModuleVisitor(ModuleVisitor moduleVisitor, Printer printer) {
        super(589824, moduleVisitor);
        this.p = printer;
    }

    public void visitMainClass(String mainClass) {
        this.p.visitMainClass(mainClass);
        super.visitMainClass(mainClass);
    }

    public void visitPackage(String packaze) {
        this.p.visitPackage(packaze);
        super.visitPackage(packaze);
    }

    public void visitRequire(String module, int access, String version) {
        this.p.visitRequire(module, access, version);
        super.visitRequire(module, access, version);
    }

    public void visitExport(String packaze, int access, String ... modules) {
        this.p.visitExport(packaze, access, modules);
        super.visitExport(packaze, access, modules);
    }

    public void visitOpen(String packaze, int access, String ... modules) {
        this.p.visitOpen(packaze, access, modules);
        super.visitOpen(packaze, access, modules);
    }

    public void visitUse(String use) {
        this.p.visitUse(use);
        super.visitUse(use);
    }

    public void visitProvide(String service, String ... providers) {
        this.p.visitProvide(service, providers);
        super.visitProvide(service, providers);
    }

    public void visitEnd() {
        this.p.visitModuleEnd();
        super.visitEnd();
    }
}

