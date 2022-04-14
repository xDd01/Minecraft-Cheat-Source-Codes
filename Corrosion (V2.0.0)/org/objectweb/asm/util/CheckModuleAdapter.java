/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.util;

import java.util.HashSet;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.CheckMethodAdapter;

public class CheckModuleAdapter
extends ModuleVisitor {
    private final boolean isOpen;
    private final NameSet requiredModules = new NameSet("Modules requires");
    private final NameSet exportedPackages = new NameSet("Module exports");
    private final NameSet openedPackages = new NameSet("Module opens");
    private final NameSet usedServices = new NameSet("Module uses");
    private final NameSet providedServices = new NameSet("Module provides");
    int classVersion;
    private boolean visitEndCalled;

    public CheckModuleAdapter(ModuleVisitor moduleVisitor, boolean isOpen) {
        this(589824, moduleVisitor, isOpen);
        if (this.getClass() != CheckModuleAdapter.class) {
            throw new IllegalStateException();
        }
    }

    protected CheckModuleAdapter(int api2, ModuleVisitor moduleVisitor, boolean isOpen) {
        super(api2, moduleVisitor);
        this.isOpen = isOpen;
    }

    public void visitMainClass(String mainClass) {
        CheckMethodAdapter.checkInternalName(53, mainClass, "module main class");
        super.visitMainClass(mainClass);
    }

    public void visitPackage(String packaze) {
        CheckMethodAdapter.checkInternalName(53, packaze, "module package");
        super.visitPackage(packaze);
    }

    public void visitRequire(String module, int access, String version) {
        this.checkVisitEndNotCalled();
        CheckClassAdapter.checkFullyQualifiedName(53, module, "required module");
        this.requiredModules.checkNameNotAlreadyDeclared(module);
        CheckClassAdapter.checkAccess(access, 36960);
        if (this.classVersion >= 54 && module.equals("java.base") && (access & 0x60) != 0) {
            throw new IllegalArgumentException("Invalid access flags: " + access + " java.base can not be declared ACC_TRANSITIVE or ACC_STATIC_PHASE");
        }
        super.visitRequire(module, access, version);
    }

    public void visitExport(String packaze, int access, String ... modules) {
        this.checkVisitEndNotCalled();
        CheckMethodAdapter.checkInternalName(53, packaze, "package name");
        this.exportedPackages.checkNameNotAlreadyDeclared(packaze);
        CheckClassAdapter.checkAccess(access, 36864);
        if (modules != null) {
            for (String module : modules) {
                CheckClassAdapter.checkFullyQualifiedName(53, module, "module export to");
            }
        }
        super.visitExport(packaze, access, modules);
    }

    public void visitOpen(String packaze, int access, String ... modules) {
        this.checkVisitEndNotCalled();
        if (this.isOpen) {
            throw new UnsupportedOperationException("An open module can not use open directive");
        }
        CheckMethodAdapter.checkInternalName(53, packaze, "package name");
        this.openedPackages.checkNameNotAlreadyDeclared(packaze);
        CheckClassAdapter.checkAccess(access, 36864);
        if (modules != null) {
            for (String module : modules) {
                CheckClassAdapter.checkFullyQualifiedName(53, module, "module open to");
            }
        }
        super.visitOpen(packaze, access, modules);
    }

    public void visitUse(String service) {
        this.checkVisitEndNotCalled();
        CheckMethodAdapter.checkInternalName(53, service, "service");
        this.usedServices.checkNameNotAlreadyDeclared(service);
        super.visitUse(service);
    }

    public void visitProvide(String service, String ... providers) {
        this.checkVisitEndNotCalled();
        CheckMethodAdapter.checkInternalName(53, service, "service");
        this.providedServices.checkNameNotAlreadyDeclared(service);
        if (providers == null || providers.length == 0) {
            throw new IllegalArgumentException("Providers cannot be null or empty");
        }
        for (String provider : providers) {
            CheckMethodAdapter.checkInternalName(53, provider, "provider");
        }
        super.visitProvide(service, providers);
    }

    public void visitEnd() {
        this.checkVisitEndNotCalled();
        this.visitEndCalled = true;
        super.visitEnd();
    }

    private void checkVisitEndNotCalled() {
        if (this.visitEndCalled) {
            throw new IllegalStateException("Cannot call a visit method after visitEnd has been called");
        }
    }

    private static class NameSet {
        private final String type;
        private final HashSet<String> names;

        NameSet(String type) {
            this.type = type;
            this.names = new HashSet();
        }

        void checkNameNotAlreadyDeclared(String name) {
            if (!this.names.add(name)) {
                throw new IllegalArgumentException(this.type + " '" + name + "' already declared");
            }
        }
    }
}

