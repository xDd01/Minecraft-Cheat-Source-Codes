/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree;

import java.util.List;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.TypeAnnotationNode;
import org.objectweb.asm.tree.UnsupportedClassVersionException;
import org.objectweb.asm.tree.Util;

public class RecordComponentNode
extends RecordComponentVisitor {
    public String name;
    public String descriptor;
    public String signature;
    public List<AnnotationNode> visibleAnnotations;
    public List<AnnotationNode> invisibleAnnotations;
    public List<TypeAnnotationNode> visibleTypeAnnotations;
    public List<TypeAnnotationNode> invisibleTypeAnnotations;
    public List<Attribute> attrs;

    public RecordComponentNode(String name, String descriptor, String signature) {
        this(589824, name, descriptor, signature);
        if (this.getClass() != RecordComponentNode.class) {
            throw new IllegalStateException();
        }
    }

    public RecordComponentNode(int api2, String name, String descriptor, String signature) {
        super(api2);
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
    }

    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        AnnotationNode annotation = new AnnotationNode(descriptor);
        if (visible) {
            this.visibleAnnotations = Util.add(this.visibleAnnotations, annotation);
        } else {
            this.invisibleAnnotations = Util.add(this.invisibleAnnotations, annotation);
        }
        return annotation;
    }

    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        TypeAnnotationNode typeAnnotation = new TypeAnnotationNode(typeRef, typePath, descriptor);
        if (visible) {
            this.visibleTypeAnnotations = Util.add(this.visibleTypeAnnotations, typeAnnotation);
        } else {
            this.invisibleTypeAnnotations = Util.add(this.invisibleTypeAnnotations, typeAnnotation);
        }
        return typeAnnotation;
    }

    public void visitAttribute(Attribute attribute) {
        this.attrs = Util.add(this.attrs, attribute);
    }

    public void visitEnd() {
    }

    public void check(int api2) {
        if (api2 < 524288) {
            throw new UnsupportedClassVersionException();
        }
    }

    public void accept(ClassVisitor classVisitor) {
        TypeAnnotationNode typeAnnotation;
        AnnotationNode annotation;
        int i2;
        int n2;
        RecordComponentVisitor recordComponentVisitor = classVisitor.visitRecordComponent(this.name, this.descriptor, this.signature);
        if (recordComponentVisitor == null) {
            return;
        }
        if (this.visibleAnnotations != null) {
            n2 = this.visibleAnnotations.size();
            for (i2 = 0; i2 < n2; ++i2) {
                annotation = this.visibleAnnotations.get(i2);
                annotation.accept(recordComponentVisitor.visitAnnotation(annotation.desc, true));
            }
        }
        if (this.invisibleAnnotations != null) {
            n2 = this.invisibleAnnotations.size();
            for (i2 = 0; i2 < n2; ++i2) {
                annotation = this.invisibleAnnotations.get(i2);
                annotation.accept(recordComponentVisitor.visitAnnotation(annotation.desc, false));
            }
        }
        if (this.visibleTypeAnnotations != null) {
            n2 = this.visibleTypeAnnotations.size();
            for (i2 = 0; i2 < n2; ++i2) {
                typeAnnotation = this.visibleTypeAnnotations.get(i2);
                typeAnnotation.accept(recordComponentVisitor.visitTypeAnnotation(typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, true));
            }
        }
        if (this.invisibleTypeAnnotations != null) {
            n2 = this.invisibleTypeAnnotations.size();
            for (i2 = 0; i2 < n2; ++i2) {
                typeAnnotation = this.invisibleTypeAnnotations.get(i2);
                typeAnnotation.accept(recordComponentVisitor.visitTypeAnnotation(typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, false));
            }
        }
        if (this.attrs != null) {
            n2 = this.attrs.size();
            for (i2 = 0; i2 < n2; ++i2) {
                recordComponentVisitor.visitAttribute(this.attrs.get(i2));
            }
        }
        recordComponentVisitor.visitEnd();
    }
}

