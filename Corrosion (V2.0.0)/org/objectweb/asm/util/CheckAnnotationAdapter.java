/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.util;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.CheckMethodAdapter;

public class CheckAnnotationAdapter
extends AnnotationVisitor {
    private final boolean useNamedValue;
    private boolean visitEndCalled;

    public CheckAnnotationAdapter(AnnotationVisitor annotationVisitor) {
        this(annotationVisitor, true);
    }

    CheckAnnotationAdapter(AnnotationVisitor annotationVisitor, boolean useNamedValues) {
        super(589824, annotationVisitor);
        this.useNamedValue = useNamedValues;
    }

    public void visit(String name, Object value) {
        this.checkVisitEndNotCalled();
        this.checkName(name);
        if (!(value instanceof Byte || value instanceof Boolean || value instanceof Character || value instanceof Short || value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double || value instanceof String || value instanceof Type || value instanceof byte[] || value instanceof boolean[] || value instanceof char[] || value instanceof short[] || value instanceof int[] || value instanceof long[] || value instanceof float[] || value instanceof double[])) {
            throw new IllegalArgumentException("Invalid annotation value");
        }
        if (value instanceof Type && ((Type)value).getSort() == 11) {
            throw new IllegalArgumentException("Invalid annotation value");
        }
        super.visit(name, value);
    }

    public void visitEnum(String name, String descriptor, String value) {
        this.checkVisitEndNotCalled();
        this.checkName(name);
        CheckMethodAdapter.checkDescriptor(49, descriptor, false);
        if (value == null) {
            throw new IllegalArgumentException("Invalid enum value");
        }
        super.visitEnum(name, descriptor, value);
    }

    public AnnotationVisitor visitAnnotation(String name, String descriptor) {
        this.checkVisitEndNotCalled();
        this.checkName(name);
        CheckMethodAdapter.checkDescriptor(49, descriptor, false);
        return new CheckAnnotationAdapter(super.visitAnnotation(name, descriptor));
    }

    public AnnotationVisitor visitArray(String name) {
        this.checkVisitEndNotCalled();
        this.checkName(name);
        return new CheckAnnotationAdapter(super.visitArray(name), false);
    }

    public void visitEnd() {
        this.checkVisitEndNotCalled();
        this.visitEndCalled = true;
        super.visitEnd();
    }

    private void checkName(String name) {
        if (this.useNamedValue && name == null) {
            throw new IllegalArgumentException("Annotation value name must not be null");
        }
    }

    private void checkVisitEndNotCalled() {
        if (this.visitEndCalled) {
            throw new IllegalStateException("Cannot call a visit method after visitEnd has been called");
        }
    }
}

