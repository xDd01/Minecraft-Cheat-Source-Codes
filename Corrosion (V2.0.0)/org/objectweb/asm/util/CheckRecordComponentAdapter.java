/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.util;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;
import org.objectweb.asm.util.CheckAnnotationAdapter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.CheckMethodAdapter;

public class CheckRecordComponentAdapter
extends RecordComponentVisitor {
    private boolean visitEndCalled;

    public CheckRecordComponentAdapter(RecordComponentVisitor recordComponentVisitor) {
        this(589824, recordComponentVisitor);
        if (this.getClass() != CheckRecordComponentAdapter.class) {
            throw new IllegalStateException();
        }
    }

    protected CheckRecordComponentAdapter(int api2, RecordComponentVisitor recordComponentVisitor) {
        super(api2, recordComponentVisitor);
    }

    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        this.checkVisitEndNotCalled();
        CheckMethodAdapter.checkDescriptor(49, descriptor, false);
        return new CheckAnnotationAdapter(super.visitAnnotation(descriptor, visible));
    }

    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        this.checkVisitEndNotCalled();
        int sort = new TypeReference(typeRef).getSort();
        if (sort != 19) {
            throw new IllegalArgumentException("Invalid type reference sort 0x" + Integer.toHexString(sort));
        }
        CheckClassAdapter.checkTypeRef(typeRef);
        CheckMethodAdapter.checkDescriptor(49, descriptor, false);
        return new CheckAnnotationAdapter(super.visitTypeAnnotation(typeRef, typePath, descriptor, visible));
    }

    public void visitAttribute(Attribute attribute) {
        this.checkVisitEndNotCalled();
        if (attribute == null) {
            throw new IllegalArgumentException("Invalid attribute (must not be null)");
        }
        super.visitAttribute(attribute);
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
}

