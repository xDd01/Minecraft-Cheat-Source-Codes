/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Constants;
import org.objectweb.asm.TypePath;

public abstract class RecordComponentVisitor {
    protected final int api;
    RecordComponentVisitor delegate;

    public RecordComponentVisitor(int api2) {
        this(api2, null);
    }

    public RecordComponentVisitor(int api2, RecordComponentVisitor recordComponentVisitor) {
        if (api2 != 589824 && api2 != 524288 && api2 != 458752 && api2 != 393216 && api2 != 327680 && api2 != 262144 && api2 != 0x10A0000) {
            throw new IllegalArgumentException("Unsupported api " + api2);
        }
        if (api2 == 0x10A0000) {
            Constants.checkAsmExperimental(this);
        }
        this.api = api2;
        this.delegate = recordComponentVisitor;
    }

    public RecordComponentVisitor getDelegate() {
        return this.delegate;
    }

    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (this.delegate != null) {
            return this.delegate.visitAnnotation(descriptor, visible);
        }
        return null;
    }

    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        if (this.delegate != null) {
            return this.delegate.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
        }
        return null;
    }

    public void visitAttribute(Attribute attribute) {
        if (this.delegate != null) {
            this.delegate.visitAttribute(attribute);
        }
    }

    public void visitEnd() {
        if (this.delegate != null) {
            this.delegate.visitEnd();
        }
    }
}

