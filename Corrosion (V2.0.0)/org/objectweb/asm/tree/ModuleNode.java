/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.tree.ModuleExportNode;
import org.objectweb.asm.tree.ModuleOpenNode;
import org.objectweb.asm.tree.ModuleProvideNode;
import org.objectweb.asm.tree.ModuleRequireNode;
import org.objectweb.asm.tree.Util;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ModuleNode
extends ModuleVisitor {
    public String name;
    public int access;
    public String version;
    public String mainClass;
    public List<String> packages;
    public List<ModuleRequireNode> requires;
    public List<ModuleExportNode> exports;
    public List<ModuleOpenNode> opens;
    public List<String> uses;
    public List<ModuleProvideNode> provides;

    public ModuleNode(String name, int access, String version) {
        super(589824);
        if (this.getClass() != ModuleNode.class) {
            throw new IllegalStateException();
        }
        this.name = name;
        this.access = access;
        this.version = version;
    }

    public ModuleNode(int api2, String name, int access, String version, List<ModuleRequireNode> requires, List<ModuleExportNode> exports, List<ModuleOpenNode> opens, List<String> uses, List<ModuleProvideNode> provides) {
        super(api2);
        this.name = name;
        this.access = access;
        this.version = version;
        this.requires = requires;
        this.exports = exports;
        this.opens = opens;
        this.uses = uses;
        this.provides = provides;
    }

    @Override
    public void visitMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    @Override
    public void visitPackage(String packaze) {
        if (this.packages == null) {
            this.packages = new ArrayList<String>(5);
        }
        this.packages.add(packaze);
    }

    @Override
    public void visitRequire(String module, int access, String version) {
        if (this.requires == null) {
            this.requires = new ArrayList<ModuleRequireNode>(5);
        }
        this.requires.add(new ModuleRequireNode(module, access, version));
    }

    @Override
    public void visitExport(String packaze, int access, String ... modules) {
        if (this.exports == null) {
            this.exports = new ArrayList<ModuleExportNode>(5);
        }
        this.exports.add(new ModuleExportNode(packaze, access, Util.asArrayList(modules)));
    }

    @Override
    public void visitOpen(String packaze, int access, String ... modules) {
        if (this.opens == null) {
            this.opens = new ArrayList<ModuleOpenNode>(5);
        }
        this.opens.add(new ModuleOpenNode(packaze, access, Util.asArrayList(modules)));
    }

    @Override
    public void visitUse(String service) {
        if (this.uses == null) {
            this.uses = new ArrayList<String>(5);
        }
        this.uses.add(service);
    }

    @Override
    public void visitProvide(String service, String ... providers) {
        if (this.provides == null) {
            this.provides = new ArrayList<ModuleProvideNode>(5);
        }
        this.provides.add(new ModuleProvideNode(service, Util.asArrayList(providers)));
    }

    @Override
    public void visitEnd() {
    }

    public void accept(ClassVisitor classVisitor) {
        int i2;
        int n2;
        ModuleVisitor moduleVisitor = classVisitor.visitModule(this.name, this.access, this.version);
        if (moduleVisitor == null) {
            return;
        }
        if (this.mainClass != null) {
            moduleVisitor.visitMainClass(this.mainClass);
        }
        if (this.packages != null) {
            n2 = this.packages.size();
            for (i2 = 0; i2 < n2; ++i2) {
                moduleVisitor.visitPackage(this.packages.get(i2));
            }
        }
        if (this.requires != null) {
            n2 = this.requires.size();
            for (i2 = 0; i2 < n2; ++i2) {
                this.requires.get(i2).accept(moduleVisitor);
            }
        }
        if (this.exports != null) {
            n2 = this.exports.size();
            for (i2 = 0; i2 < n2; ++i2) {
                this.exports.get(i2).accept(moduleVisitor);
            }
        }
        if (this.opens != null) {
            n2 = this.opens.size();
            for (i2 = 0; i2 < n2; ++i2) {
                this.opens.get(i2).accept(moduleVisitor);
            }
        }
        if (this.uses != null) {
            n2 = this.uses.size();
            for (i2 = 0; i2 < n2; ++i2) {
                moduleVisitor.visitUse(this.uses.get(i2));
            }
        }
        if (this.provides != null) {
            n2 = this.provides.size();
            for (i2 = 0; i2 < n2; ++i2) {
                this.provides.get(i2).accept(moduleVisitor);
            }
        }
    }
}

