/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class StaticInitMerger
extends ClassVisitor {
    private String owner;
    private final String renamedClinitMethodPrefix;
    private int numClinitMethods;
    private MethodVisitor mergedClinitVisitor;

    public StaticInitMerger(String prefix, ClassVisitor classVisitor) {
        this(589824, prefix, classVisitor);
    }

    protected StaticInitMerger(int api2, String prefix, ClassVisitor classVisitor) {
        super(api2, classVisitor);
        this.renamedClinitMethodPrefix = prefix;
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.owner = name;
    }

    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor;
        if ("<clinit>".equals(name)) {
            int newAccess = 10;
            String newName = this.renamedClinitMethodPrefix + this.numClinitMethods++;
            methodVisitor = super.visitMethod(newAccess, newName, descriptor, signature, exceptions);
            if (this.mergedClinitVisitor == null) {
                this.mergedClinitVisitor = super.visitMethod(newAccess, name, descriptor, null, null);
            }
            this.mergedClinitVisitor.visitMethodInsn(184, this.owner, newName, descriptor, false);
        } else {
            methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        }
        return methodVisitor;
    }

    public void visitEnd() {
        if (this.mergedClinitVisitor != null) {
            this.mergedClinitVisitor.visitInsn(177);
            this.mergedClinitVisitor.visitMaxs(0, 0);
        }
        super.visitEnd();
    }
}

