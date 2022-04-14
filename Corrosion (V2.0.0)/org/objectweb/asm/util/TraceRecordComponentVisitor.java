/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.util;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.TraceAnnotationVisitor;

public final class TraceRecordComponentVisitor
extends RecordComponentVisitor {
    public final Printer printer;

    public TraceRecordComponentVisitor(Printer printer) {
        this(null, printer);
    }

    public TraceRecordComponentVisitor(RecordComponentVisitor recordComponentVisitor, Printer printer) {
        super(589824, recordComponentVisitor);
        this.printer = printer;
    }

    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        Printer annotationPrinter = this.printer.visitRecordComponentAnnotation(descriptor, visible);
        return new TraceAnnotationVisitor(super.visitAnnotation(descriptor, visible), annotationPrinter);
    }

    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        Printer annotationPrinter = this.printer.visitRecordComponentTypeAnnotation(typeRef, typePath, descriptor, visible);
        return new TraceAnnotationVisitor(super.visitTypeAnnotation(typeRef, typePath, descriptor, visible), annotationPrinter);
    }

    public void visitAttribute(Attribute attribute) {
        this.printer.visitRecordComponentAttribute(attribute);
        super.visitAttribute(attribute);
    }

    public void visitEnd() {
        this.printer.visitRecordComponentEnd();
        super.visitEnd();
    }
}

