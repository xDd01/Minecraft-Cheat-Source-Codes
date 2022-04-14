/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.commons.Remapper;

public class AnnotationRemapper
extends AnnotationVisitor {
    protected final String descriptor;
    protected final Remapper remapper;

    @Deprecated
    public AnnotationRemapper(AnnotationVisitor annotationVisitor, Remapper remapper) {
        this(null, annotationVisitor, remapper);
    }

    public AnnotationRemapper(String descriptor, AnnotationVisitor annotationVisitor, Remapper remapper) {
        this(589824, descriptor, annotationVisitor, remapper);
    }

    @Deprecated
    protected AnnotationRemapper(int api2, AnnotationVisitor annotationVisitor, Remapper remapper) {
        this(api2, null, annotationVisitor, remapper);
    }

    protected AnnotationRemapper(int api2, String descriptor, AnnotationVisitor annotationVisitor, Remapper remapper) {
        super(api2, annotationVisitor);
        this.descriptor = descriptor;
        this.remapper = remapper;
    }

    public void visit(String name, Object value) {
        super.visit(this.mapAnnotationAttributeName(name), this.remapper.mapValue(value));
    }

    public void visitEnum(String name, String descriptor, String value) {
        super.visitEnum(this.mapAnnotationAttributeName(name), this.remapper.mapDesc(descriptor), value);
    }

    public AnnotationVisitor visitAnnotation(String name, String descriptor) {
        AnnotationVisitor annotationVisitor = super.visitAnnotation(this.mapAnnotationAttributeName(name), this.remapper.mapDesc(descriptor));
        if (annotationVisitor == null) {
            return null;
        }
        return annotationVisitor == this.av ? this : this.createAnnotationRemapper(descriptor, annotationVisitor);
    }

    public AnnotationVisitor visitArray(String name) {
        AnnotationVisitor annotationVisitor = super.visitArray(this.mapAnnotationAttributeName(name));
        if (annotationVisitor == null) {
            return null;
        }
        return annotationVisitor == this.av ? this : this.createAnnotationRemapper(null, annotationVisitor);
    }

    @Deprecated
    protected AnnotationVisitor createAnnotationRemapper(AnnotationVisitor annotationVisitor) {
        return new AnnotationRemapper(this.api, null, annotationVisitor, this.remapper);
    }

    protected AnnotationVisitor createAnnotationRemapper(String descriptor, AnnotationVisitor annotationVisitor) {
        return new AnnotationRemapper(this.api, descriptor, annotationVisitor, this.remapper).orDeprecatedValue(this.createAnnotationRemapper(annotationVisitor));
    }

    final AnnotationVisitor orDeprecatedValue(AnnotationVisitor deprecatedAnnotationVisitor) {
        if (deprecatedAnnotationVisitor.getClass() == this.getClass()) {
            AnnotationRemapper deprecatedAnnotationRemapper = (AnnotationRemapper)deprecatedAnnotationVisitor;
            if (deprecatedAnnotationRemapper.api == this.api && deprecatedAnnotationRemapper.av == this.av && deprecatedAnnotationRemapper.remapper == this.remapper) {
                return this;
            }
        }
        return deprecatedAnnotationVisitor;
    }

    private String mapAnnotationAttributeName(String name) {
        if (this.descriptor == null) {
            return name;
        }
        return this.remapper.mapAnnotationAttributeName(this.descriptor, name);
    }
}

