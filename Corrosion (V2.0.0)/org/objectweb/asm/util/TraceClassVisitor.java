/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.util;

import java.io.PrintWriter;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceAnnotationVisitor;
import org.objectweb.asm.util.TraceFieldVisitor;
import org.objectweb.asm.util.TraceMethodVisitor;
import org.objectweb.asm.util.TraceModuleVisitor;
import org.objectweb.asm.util.TraceRecordComponentVisitor;

public final class TraceClassVisitor
extends ClassVisitor {
    private final PrintWriter printWriter;
    public final Printer p;

    public TraceClassVisitor(PrintWriter printWriter) {
        this(null, printWriter);
    }

    public TraceClassVisitor(ClassVisitor classVisitor, PrintWriter printWriter) {
        this(classVisitor, new Textifier(), printWriter);
    }

    public TraceClassVisitor(ClassVisitor classVisitor, Printer printer, PrintWriter printWriter) {
        super(0x10A0000, classVisitor);
        this.printWriter = printWriter;
        this.p = printer;
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.p.visit(version, access, name, signature, superName, interfaces);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    public void visitSource(String file, String debug) {
        this.p.visitSource(file, debug);
        super.visitSource(file, debug);
    }

    public ModuleVisitor visitModule(String name, int flags, String version) {
        Printer modulePrinter = this.p.visitModule(name, flags, version);
        return new TraceModuleVisitor(super.visitModule(name, flags, version), modulePrinter);
    }

    public void visitNestHost(String nestHost) {
        this.p.visitNestHost(nestHost);
        super.visitNestHost(nestHost);
    }

    public void visitOuterClass(String owner, String name, String descriptor) {
        this.p.visitOuterClass(owner, name, descriptor);
        super.visitOuterClass(owner, name, descriptor);
    }

    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        Printer annotationPrinter = this.p.visitClassAnnotation(descriptor, visible);
        return new TraceAnnotationVisitor(super.visitAnnotation(descriptor, visible), annotationPrinter);
    }

    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        Printer annotationPrinter = this.p.visitClassTypeAnnotation(typeRef, typePath, descriptor, visible);
        return new TraceAnnotationVisitor(super.visitTypeAnnotation(typeRef, typePath, descriptor, visible), annotationPrinter);
    }

    public void visitAttribute(Attribute attribute) {
        this.p.visitClassAttribute(attribute);
        super.visitAttribute(attribute);
    }

    public void visitNestMember(String nestMember) {
        this.p.visitNestMember(nestMember);
        super.visitNestMember(nestMember);
    }

    public void visitPermittedSubclass(String permittedSubclass) {
        this.p.visitPermittedSubclass(permittedSubclass);
        super.visitPermittedSubclass(permittedSubclass);
    }

    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        this.p.visitInnerClass(name, outerName, innerName, access);
        super.visitInnerClass(name, outerName, innerName, access);
    }

    public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
        Printer recordComponentPrinter = this.p.visitRecordComponent(name, descriptor, signature);
        return new TraceRecordComponentVisitor(super.visitRecordComponent(name, descriptor, signature), recordComponentPrinter);
    }

    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        Printer fieldPrinter = this.p.visitField(access, name, descriptor, signature, value);
        return new TraceFieldVisitor(super.visitField(access, name, descriptor, signature, value), fieldPrinter);
    }

    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        Printer methodPrinter = this.p.visitMethod(access, name, descriptor, signature, exceptions);
        return new TraceMethodVisitor(super.visitMethod(access, name, descriptor, signature, exceptions), methodPrinter);
    }

    public void visitEnd() {
        this.p.visitClassEnd();
        if (this.printWriter != null) {
            this.p.print(this.printWriter);
            this.printWriter.flush();
        }
        super.visitEnd();
    }
}

