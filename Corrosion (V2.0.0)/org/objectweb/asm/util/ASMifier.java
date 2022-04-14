/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.util.ASMifierSupport;
import org.objectweb.asm.util.Printer;

public class ASMifier
extends Printer {
    private static final String USAGE = "Prints the ASM code to generate the given class.\nUsage: ASMifier [-nodebug] <fully qualified class name or class file name>";
    private static final int ACCESS_CLASS = 262144;
    private static final int ACCESS_FIELD = 524288;
    private static final int ACCESS_INNER = 0x100000;
    private static final int ACCESS_MODULE = 0x200000;
    private static final String ANNOTATION_VISITOR = "annotationVisitor";
    private static final String ANNOTATION_VISITOR0 = "annotationVisitor0 = ";
    private static final String COMMA = "\", \"";
    private static final String END_ARRAY = " });\n";
    private static final String END_PARAMETERS = ");\n\n";
    private static final String NEW_OBJECT_ARRAY = ", new Object[] {";
    private static final String VISIT_END = ".visitEnd();\n";
    private static final List<String> FRAME_TYPES = Collections.unmodifiableList(Arrays.asList("Opcodes.TOP", "Opcodes.INTEGER", "Opcodes.FLOAT", "Opcodes.DOUBLE", "Opcodes.LONG", "Opcodes.NULL", "Opcodes.UNINITIALIZED_THIS"));
    private static final Map<Integer, String> CLASS_VERSIONS;
    protected final String name;
    protected final int id;
    protected Map<Label, String> labelNames;

    public ASMifier() {
        this(589824, "classWriter", 0);
        if (this.getClass() != ASMifier.class) {
            throw new IllegalStateException();
        }
    }

    protected ASMifier(int api2, String visitorVariableName, int annotationVisitorId) {
        super(api2);
        this.name = visitorVariableName;
        this.id = annotationVisitorId;
    }

    public static void main(String[] args) throws IOException {
        ASMifier.main(args, new PrintWriter(System.out, true), new PrintWriter(System.err, true));
    }

    static void main(String[] args, PrintWriter output, PrintWriter logger) throws IOException {
        ASMifier.main(args, USAGE, new ASMifier(), output, logger);
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        String simpleName;
        if (name == null) {
            simpleName = "module-info";
        } else {
            int lastSlashIndex = name.lastIndexOf(47);
            if (lastSlashIndex == -1) {
                simpleName = name;
            } else {
                this.text.add("package asm." + name.substring(0, lastSlashIndex).replace('/', '.') + ";\n");
                simpleName = name.substring(lastSlashIndex + 1).replaceAll("[-\\(\\)]", "_");
            }
        }
        this.text.add("import org.objectweb.asm.AnnotationVisitor;\n");
        this.text.add("import org.objectweb.asm.Attribute;\n");
        this.text.add("import org.objectweb.asm.ClassReader;\n");
        this.text.add("import org.objectweb.asm.ClassWriter;\n");
        this.text.add("import org.objectweb.asm.ConstantDynamic;\n");
        this.text.add("import org.objectweb.asm.FieldVisitor;\n");
        this.text.add("import org.objectweb.asm.Handle;\n");
        this.text.add("import org.objectweb.asm.Label;\n");
        this.text.add("import org.objectweb.asm.MethodVisitor;\n");
        this.text.add("import org.objectweb.asm.Opcodes;\n");
        this.text.add("import org.objectweb.asm.RecordComponentVisitor;\n");
        this.text.add("import org.objectweb.asm.Type;\n");
        this.text.add("import org.objectweb.asm.TypePath;\n");
        this.text.add("public class " + simpleName + "Dump implements Opcodes {\n\n");
        this.text.add("public static byte[] dump () throws Exception {\n\n");
        this.text.add("ClassWriter classWriter = new ClassWriter(0);\n");
        this.text.add("FieldVisitor fieldVisitor;\n");
        this.text.add("RecordComponentVisitor recordComponentVisitor;\n");
        this.text.add("MethodVisitor methodVisitor;\n");
        this.text.add("AnnotationVisitor annotationVisitor0;\n\n");
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("classWriter.visit(");
        String versionString = CLASS_VERSIONS.get(version);
        if (versionString != null) {
            this.stringBuilder.append(versionString);
        } else {
            this.stringBuilder.append(version);
        }
        this.stringBuilder.append(", ");
        this.appendAccessFlags(access | 0x40000);
        this.stringBuilder.append(", ");
        this.appendConstant(name);
        this.stringBuilder.append(", ");
        this.appendConstant(signature);
        this.stringBuilder.append(", ");
        this.appendConstant(superName);
        this.stringBuilder.append(", ");
        if (interfaces != null && interfaces.length > 0) {
            this.stringBuilder.append("new String[] {");
            for (int i2 = 0; i2 < interfaces.length; ++i2) {
                this.stringBuilder.append(i2 == 0 ? " " : ", ");
                this.appendConstant(interfaces[i2]);
            }
            this.stringBuilder.append(" }");
        } else {
            this.stringBuilder.append("null");
        }
        this.stringBuilder.append(END_PARAMETERS);
        this.text.add(this.stringBuilder.toString());
    }

    public void visitSource(String file, String debug) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("classWriter.visitSource(");
        this.appendConstant(file);
        this.stringBuilder.append(", ");
        this.appendConstant(debug);
        this.stringBuilder.append(END_PARAMETERS);
        this.text.add(this.stringBuilder.toString());
    }

    public Printer visitModule(String name, int flags, String version) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("ModuleVisitor moduleVisitor = classWriter.visitModule(");
        this.appendConstant(name);
        this.stringBuilder.append(", ");
        this.appendAccessFlags(flags | 0x200000);
        this.stringBuilder.append(", ");
        this.appendConstant(version);
        this.stringBuilder.append(END_PARAMETERS);
        this.text.add(this.stringBuilder.toString());
        ASMifier asmifier = this.createASMifier("moduleVisitor", 0);
        this.text.add(asmifier.getText());
        this.text.add("}\n");
        return asmifier;
    }

    public void visitNestHost(String nestHost) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("classWriter.visitNestHost(");
        this.appendConstant(nestHost);
        this.stringBuilder.append(END_PARAMETERS);
        this.text.add(this.stringBuilder.toString());
    }

    public void visitOuterClass(String owner, String name, String descriptor) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("classWriter.visitOuterClass(");
        this.appendConstant(owner);
        this.stringBuilder.append(", ");
        this.appendConstant(name);
        this.stringBuilder.append(", ");
        this.appendConstant(descriptor);
        this.stringBuilder.append(END_PARAMETERS);
        this.text.add(this.stringBuilder.toString());
    }

    public ASMifier visitClassAnnotation(String descriptor, boolean visible) {
        return this.visitAnnotation(descriptor, visible);
    }

    public ASMifier visitClassTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return this.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }

    public void visitClassAttribute(Attribute attribute) {
        this.visitAttribute(attribute);
    }

    public void visitNestMember(String nestMember) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("classWriter.visitNestMember(");
        this.appendConstant(nestMember);
        this.stringBuilder.append(END_PARAMETERS);
        this.text.add(this.stringBuilder.toString());
    }

    public void visitPermittedSubclass(String permittedSubclass) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("classWriter.visitPermittedSubclass(");
        this.appendConstant(permittedSubclass);
        this.stringBuilder.append(END_PARAMETERS);
        this.text.add(this.stringBuilder.toString());
    }

    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("classWriter.visitInnerClass(");
        this.appendConstant(name);
        this.stringBuilder.append(", ");
        this.appendConstant(outerName);
        this.stringBuilder.append(", ");
        this.appendConstant(innerName);
        this.stringBuilder.append(", ");
        this.appendAccessFlags(access | 0x100000);
        this.stringBuilder.append(END_PARAMETERS);
        this.text.add(this.stringBuilder.toString());
    }

    public ASMifier visitRecordComponent(String name, String descriptor, String signature) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("{\n");
        this.stringBuilder.append("recordComponentVisitor = classWriter.visitRecordComponent(");
        this.appendConstant(name);
        this.stringBuilder.append(", ");
        this.appendConstant(descriptor);
        this.stringBuilder.append(", ");
        this.appendConstant(signature);
        this.stringBuilder.append(");\n");
        this.text.add(this.stringBuilder.toString());
        ASMifier asmifier = this.createASMifier("recordComponentVisitor", 0);
        this.text.add(asmifier.getText());
        this.text.add("}\n");
        return asmifier;
    }

    public ASMifier visitField(int access, String name, String descriptor, String signature, Object value) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("{\n");
        this.stringBuilder.append("fieldVisitor = classWriter.visitField(");
        this.appendAccessFlags(access | 0x80000);
        this.stringBuilder.append(", ");
        this.appendConstant(name);
        this.stringBuilder.append(", ");
        this.appendConstant(descriptor);
        this.stringBuilder.append(", ");
        this.appendConstant(signature);
        this.stringBuilder.append(", ");
        this.appendConstant(value);
        this.stringBuilder.append(");\n");
        this.text.add(this.stringBuilder.toString());
        ASMifier asmifier = this.createASMifier("fieldVisitor", 0);
        this.text.add(asmifier.getText());
        this.text.add("}\n");
        return asmifier;
    }

    public ASMifier visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("{\n");
        this.stringBuilder.append("methodVisitor = classWriter.visitMethod(");
        this.appendAccessFlags(access);
        this.stringBuilder.append(", ");
        this.appendConstant(name);
        this.stringBuilder.append(", ");
        this.appendConstant(descriptor);
        this.stringBuilder.append(", ");
        this.appendConstant(signature);
        this.stringBuilder.append(", ");
        if (exceptions != null && exceptions.length > 0) {
            this.stringBuilder.append("new String[] {");
            for (int i2 = 0; i2 < exceptions.length; ++i2) {
                this.stringBuilder.append(i2 == 0 ? " " : ", ");
                this.appendConstant(exceptions[i2]);
            }
            this.stringBuilder.append(" }");
        } else {
            this.stringBuilder.append("null");
        }
        this.stringBuilder.append(");\n");
        this.text.add(this.stringBuilder.toString());
        ASMifier asmifier = this.createASMifier("methodVisitor", 0);
        this.text.add(asmifier.getText());
        this.text.add("}\n");
        return asmifier;
    }

    public void visitClassEnd() {
        this.text.add("classWriter.visitEnd();\n\n");
        this.text.add("return classWriter.toByteArray();\n");
        this.text.add("}\n");
        this.text.add("}\n");
    }

    public void visitMainClass(String mainClass) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("moduleVisitor.visitMainClass(");
        this.appendConstant(mainClass);
        this.stringBuilder.append(");\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitPackage(String packaze) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("moduleVisitor.visitPackage(");
        this.appendConstant(packaze);
        this.stringBuilder.append(");\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitRequire(String module, int access, String version) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("moduleVisitor.visitRequire(");
        this.appendConstant(module);
        this.stringBuilder.append(", ");
        this.appendAccessFlags(access | 0x200000);
        this.stringBuilder.append(", ");
        this.appendConstant(version);
        this.stringBuilder.append(");\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitExport(String packaze, int access, String ... modules) {
        this.visitExportOrOpen("moduleVisitor.visitExport(", packaze, access, modules);
    }

    public void visitOpen(String packaze, int access, String ... modules) {
        this.visitExportOrOpen("moduleVisitor.visitOpen(", packaze, access, modules);
    }

    private void visitExportOrOpen(String visitMethod, String packaze, int access, String ... modules) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(visitMethod);
        this.appendConstant(packaze);
        this.stringBuilder.append(", ");
        this.appendAccessFlags(access | 0x200000);
        if (modules != null && modules.length > 0) {
            this.stringBuilder.append(", new String[] {");
            for (int i2 = 0; i2 < modules.length; ++i2) {
                this.stringBuilder.append(i2 == 0 ? " " : ", ");
                this.appendConstant(modules[i2]);
            }
            this.stringBuilder.append(" }");
        }
        this.stringBuilder.append(");\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitUse(String service) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("moduleVisitor.visitUse(");
        this.appendConstant(service);
        this.stringBuilder.append(");\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitProvide(String service, String ... providers) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("moduleVisitor.visitProvide(");
        this.appendConstant(service);
        this.stringBuilder.append(",  new String[] {");
        for (int i2 = 0; i2 < providers.length; ++i2) {
            this.stringBuilder.append(i2 == 0 ? " " : ", ");
            this.appendConstant(providers[i2]);
        }
        this.stringBuilder.append(END_ARRAY);
        this.text.add(this.stringBuilder.toString());
    }

    public void visitModuleEnd() {
        this.text.add("moduleVisitor.visitEnd();\n");
    }

    public void visit(String name, Object value) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(ANNOTATION_VISITOR).append(this.id).append(".visit(");
        this.appendConstant(name);
        this.stringBuilder.append(", ");
        this.appendConstant(value);
        this.stringBuilder.append(");\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitEnum(String name, String descriptor, String value) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(ANNOTATION_VISITOR).append(this.id).append(".visitEnum(");
        this.appendConstant(name);
        this.stringBuilder.append(", ");
        this.appendConstant(descriptor);
        this.stringBuilder.append(", ");
        this.appendConstant(value);
        this.stringBuilder.append(");\n");
        this.text.add(this.stringBuilder.toString());
    }

    public ASMifier visitAnnotation(String name, String descriptor) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("{\n").append("AnnotationVisitor annotationVisitor").append(this.id + 1).append(" = annotationVisitor");
        this.stringBuilder.append(this.id).append(".visitAnnotation(");
        this.appendConstant(name);
        this.stringBuilder.append(", ");
        this.appendConstant(descriptor);
        this.stringBuilder.append(");\n");
        this.text.add(this.stringBuilder.toString());
        ASMifier asmifier = this.createASMifier(ANNOTATION_VISITOR, this.id + 1);
        this.text.add(asmifier.getText());
        this.text.add("}\n");
        return asmifier;
    }

    public ASMifier visitArray(String name) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("{\n");
        this.stringBuilder.append("AnnotationVisitor annotationVisitor").append(this.id + 1).append(" = annotationVisitor");
        this.stringBuilder.append(this.id).append(".visitArray(");
        this.appendConstant(name);
        this.stringBuilder.append(");\n");
        this.text.add(this.stringBuilder.toString());
        ASMifier asmifier = this.createASMifier(ANNOTATION_VISITOR, this.id + 1);
        this.text.add(asmifier.getText());
        this.text.add("}\n");
        return asmifier;
    }

    public void visitAnnotationEnd() {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(ANNOTATION_VISITOR).append(this.id).append(VISIT_END);
        this.text.add(this.stringBuilder.toString());
    }

    public ASMifier visitRecordComponentAnnotation(String descriptor, boolean visible) {
        return this.visitAnnotation(descriptor, visible);
    }

    public ASMifier visitRecordComponentTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return this.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }

    public void visitRecordComponentAttribute(Attribute attribute) {
        this.visitAttribute(attribute);
    }

    public void visitRecordComponentEnd() {
        this.visitMemberEnd();
    }

    public ASMifier visitFieldAnnotation(String descriptor, boolean visible) {
        return this.visitAnnotation(descriptor, visible);
    }

    public ASMifier visitFieldTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return this.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }

    public void visitFieldAttribute(Attribute attribute) {
        this.visitAttribute(attribute);
    }

    public void visitFieldEnd() {
        this.visitMemberEnd();
    }

    public void visitParameter(String parameterName, int access) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.name).append(".visitParameter(");
        ASMifier.appendString(this.stringBuilder, parameterName);
        this.stringBuilder.append(", ");
        this.appendAccessFlags(access);
        this.text.add(this.stringBuilder.append(");\n").toString());
    }

    public ASMifier visitAnnotationDefault() {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("{\n").append(ANNOTATION_VISITOR0).append(this.name).append(".visitAnnotationDefault();\n");
        this.text.add(this.stringBuilder.toString());
        ASMifier asmifier = this.createASMifier(ANNOTATION_VISITOR, 0);
        this.text.add(asmifier.getText());
        this.text.add("}\n");
        return asmifier;
    }

    public ASMifier visitMethodAnnotation(String descriptor, boolean visible) {
        return this.visitAnnotation(descriptor, visible);
    }

    public ASMifier visitMethodTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return this.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }

    public ASMifier visitAnnotableParameterCount(int parameterCount, boolean visible) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.name).append(".visitAnnotableParameterCount(").append(parameterCount).append(", ").append(visible).append(");\n");
        this.text.add(this.stringBuilder.toString());
        return this;
    }

    public ASMifier visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("{\n").append(ANNOTATION_VISITOR0).append(this.name).append(".visitParameterAnnotation(").append(parameter).append(", ");
        this.appendConstant(descriptor);
        this.stringBuilder.append(", ").append(visible).append(");\n");
        this.text.add(this.stringBuilder.toString());
        ASMifier asmifier = this.createASMifier(ANNOTATION_VISITOR, 0);
        this.text.add(asmifier.getText());
        this.text.add("}\n");
        return asmifier;
    }

    public void visitMethodAttribute(Attribute attribute) {
        this.visitAttribute(attribute);
    }

    public void visitCode() {
        this.text.add(this.name + ".visitCode();\n");
    }

    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        this.stringBuilder.setLength(0);
        switch (type) {
            case -1: 
            case 0: {
                this.declareFrameTypes(numLocal, local);
                this.declareFrameTypes(numStack, stack);
                if (type == -1) {
                    this.stringBuilder.append(this.name).append(".visitFrame(Opcodes.F_NEW, ");
                } else {
                    this.stringBuilder.append(this.name).append(".visitFrame(Opcodes.F_FULL, ");
                }
                this.stringBuilder.append(numLocal).append(NEW_OBJECT_ARRAY);
                this.appendFrameTypes(numLocal, local);
                this.stringBuilder.append("}, ").append(numStack).append(NEW_OBJECT_ARRAY);
                this.appendFrameTypes(numStack, stack);
                this.stringBuilder.append('}');
                break;
            }
            case 1: {
                this.declareFrameTypes(numLocal, local);
                this.stringBuilder.append(this.name).append(".visitFrame(Opcodes.F_APPEND,").append(numLocal).append(NEW_OBJECT_ARRAY);
                this.appendFrameTypes(numLocal, local);
                this.stringBuilder.append("}, 0, null");
                break;
            }
            case 2: {
                this.stringBuilder.append(this.name).append(".visitFrame(Opcodes.F_CHOP,").append(numLocal).append(", null, 0, null");
                break;
            }
            case 3: {
                this.stringBuilder.append(this.name).append(".visitFrame(Opcodes.F_SAME, 0, null, 0, null");
                break;
            }
            case 4: {
                this.declareFrameTypes(1, stack);
                this.stringBuilder.append(this.name).append(".visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {");
                this.appendFrameTypes(1, stack);
                this.stringBuilder.append('}');
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        this.stringBuilder.append(");\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitInsn(int opcode) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.name).append(".visitInsn(").append(OPCODES[opcode]).append(");\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitIntInsn(int opcode, int operand) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.name).append(".visitIntInsn(").append(OPCODES[opcode]).append(", ").append(opcode == 188 ? TYPES[operand] : Integer.toString(operand)).append(");\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitVarInsn(int opcode, int var) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.name).append(".visitVarInsn(").append(OPCODES[opcode]).append(", ").append(var).append(");\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitTypeInsn(int opcode, String type) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.name).append(".visitTypeInsn(").append(OPCODES[opcode]).append(", ");
        this.appendConstant(type);
        this.stringBuilder.append(");\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.name).append(".visitFieldInsn(").append(OPCODES[opcode]).append(", ");
        this.appendConstant(owner);
        this.stringBuilder.append(", ");
        this.appendConstant(name);
        this.stringBuilder.append(", ");
        this.appendConstant(descriptor);
        this.stringBuilder.append(");\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.name).append(".visitMethodInsn(").append(OPCODES[opcode]).append(", ");
        this.appendConstant(owner);
        this.stringBuilder.append(", ");
        this.appendConstant(name);
        this.stringBuilder.append(", ");
        this.appendConstant(descriptor);
        this.stringBuilder.append(", ");
        this.stringBuilder.append(isInterface ? "true" : "false");
        this.stringBuilder.append(");\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object ... bootstrapMethodArguments) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.name).append(".visitInvokeDynamicInsn(");
        this.appendConstant(name);
        this.stringBuilder.append(", ");
        this.appendConstant(descriptor);
        this.stringBuilder.append(", ");
        this.appendConstant(bootstrapMethodHandle);
        this.stringBuilder.append(", new Object[]{");
        for (int i2 = 0; i2 < bootstrapMethodArguments.length; ++i2) {
            this.appendConstant(bootstrapMethodArguments[i2]);
            if (i2 == bootstrapMethodArguments.length - 1) continue;
            this.stringBuilder.append(", ");
        }
        this.stringBuilder.append("});\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitJumpInsn(int opcode, Label label) {
        this.stringBuilder.setLength(0);
        this.declareLabel(label);
        this.stringBuilder.append(this.name).append(".visitJumpInsn(").append(OPCODES[opcode]).append(", ");
        this.appendLabel(label);
        this.stringBuilder.append(");\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitLabel(Label label) {
        this.stringBuilder.setLength(0);
        this.declareLabel(label);
        this.stringBuilder.append(this.name).append(".visitLabel(");
        this.appendLabel(label);
        this.stringBuilder.append(");\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitLdcInsn(Object value) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.name).append(".visitLdcInsn(");
        this.appendConstant(value);
        this.stringBuilder.append(");\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitIincInsn(int var, int increment) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.name).append(".visitIincInsn(").append(var).append(", ").append(increment).append(");\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitTableSwitchInsn(int min, int max, Label dflt, Label ... labels) {
        this.stringBuilder.setLength(0);
        for (Label label : labels) {
            this.declareLabel(label);
        }
        this.declareLabel(dflt);
        this.stringBuilder.append(this.name).append(".visitTableSwitchInsn(").append(min).append(", ").append(max).append(", ");
        this.appendLabel(dflt);
        this.stringBuilder.append(", new Label[] {");
        for (int i2 = 0; i2 < labels.length; ++i2) {
            this.stringBuilder.append(i2 == 0 ? " " : ", ");
            this.appendLabel(labels[i2]);
        }
        this.stringBuilder.append(END_ARRAY);
        this.text.add(this.stringBuilder.toString());
    }

    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        int i2;
        this.stringBuilder.setLength(0);
        for (Label label : labels) {
            this.declareLabel(label);
        }
        this.declareLabel(dflt);
        this.stringBuilder.append(this.name).append(".visitLookupSwitchInsn(");
        this.appendLabel(dflt);
        this.stringBuilder.append(", new int[] {");
        for (i2 = 0; i2 < keys.length; ++i2) {
            this.stringBuilder.append(i2 == 0 ? " " : ", ").append(keys[i2]);
        }
        this.stringBuilder.append(" }, new Label[] {");
        for (i2 = 0; i2 < labels.length; ++i2) {
            this.stringBuilder.append(i2 == 0 ? " " : ", ");
            this.appendLabel(labels[i2]);
        }
        this.stringBuilder.append(END_ARRAY);
        this.text.add(this.stringBuilder.toString());
    }

    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.name).append(".visitMultiANewArrayInsn(");
        this.appendConstant(descriptor);
        this.stringBuilder.append(", ").append(numDimensions).append(");\n");
        this.text.add(this.stringBuilder.toString());
    }

    public ASMifier visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return this.visitTypeAnnotation("visitInsnAnnotation", typeRef, typePath, descriptor, visible);
    }

    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        this.stringBuilder.setLength(0);
        this.declareLabel(start);
        this.declareLabel(end);
        this.declareLabel(handler);
        this.stringBuilder.append(this.name).append(".visitTryCatchBlock(");
        this.appendLabel(start);
        this.stringBuilder.append(", ");
        this.appendLabel(end);
        this.stringBuilder.append(", ");
        this.appendLabel(handler);
        this.stringBuilder.append(", ");
        this.appendConstant(type);
        this.stringBuilder.append(");\n");
        this.text.add(this.stringBuilder.toString());
    }

    public ASMifier visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return this.visitTypeAnnotation("visitTryCatchAnnotation", typeRef, typePath, descriptor, visible);
    }

    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.name).append(".visitLocalVariable(");
        this.appendConstant(name);
        this.stringBuilder.append(", ");
        this.appendConstant(descriptor);
        this.stringBuilder.append(", ");
        this.appendConstant(signature);
        this.stringBuilder.append(", ");
        this.appendLabel(start);
        this.stringBuilder.append(", ");
        this.appendLabel(end);
        this.stringBuilder.append(", ").append(index).append(");\n");
        this.text.add(this.stringBuilder.toString());
    }

    public Printer visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
        int i2;
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("{\n").append(ANNOTATION_VISITOR0).append(this.name).append(".visitLocalVariableAnnotation(").append(typeRef);
        if (typePath == null) {
            this.stringBuilder.append(", null, ");
        } else {
            this.stringBuilder.append(", TypePath.fromString(\"").append(typePath).append("\"), ");
        }
        this.stringBuilder.append("new Label[] {");
        for (i2 = 0; i2 < start.length; ++i2) {
            this.stringBuilder.append(i2 == 0 ? " " : ", ");
            this.appendLabel(start[i2]);
        }
        this.stringBuilder.append(" }, new Label[] {");
        for (i2 = 0; i2 < end.length; ++i2) {
            this.stringBuilder.append(i2 == 0 ? " " : ", ");
            this.appendLabel(end[i2]);
        }
        this.stringBuilder.append(" }, new int[] {");
        for (i2 = 0; i2 < index.length; ++i2) {
            this.stringBuilder.append(i2 == 0 ? " " : ", ").append(index[i2]);
        }
        this.stringBuilder.append(" }, ");
        this.appendConstant(descriptor);
        this.stringBuilder.append(", ").append(visible).append(");\n");
        this.text.add(this.stringBuilder.toString());
        ASMifier asmifier = this.createASMifier(ANNOTATION_VISITOR, 0);
        this.text.add(asmifier.getText());
        this.text.add("}\n");
        return asmifier;
    }

    public void visitLineNumber(int line, Label start) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.name).append(".visitLineNumber(").append(line).append(", ");
        this.appendLabel(start);
        this.stringBuilder.append(");\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitMaxs(int maxStack, int maxLocals) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.name).append(".visitMaxs(").append(maxStack).append(", ").append(maxLocals).append(");\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitMethodEnd() {
        this.visitMemberEnd();
    }

    public ASMifier visitAnnotation(String descriptor, boolean visible) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("{\n").append(ANNOTATION_VISITOR0).append(this.name).append(".visitAnnotation(");
        this.appendConstant(descriptor);
        this.stringBuilder.append(", ").append(visible).append(");\n");
        this.text.add(this.stringBuilder.toString());
        ASMifier asmifier = this.createASMifier(ANNOTATION_VISITOR, 0);
        this.text.add(asmifier.getText());
        this.text.add("}\n");
        return asmifier;
    }

    public ASMifier visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return this.visitTypeAnnotation("visitTypeAnnotation", typeRef, typePath, descriptor, visible);
    }

    public ASMifier visitTypeAnnotation(String method, int typeRef, TypePath typePath, String descriptor, boolean visible) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("{\n").append(ANNOTATION_VISITOR0).append(this.name).append(".").append(method).append("(").append(typeRef);
        if (typePath == null) {
            this.stringBuilder.append(", null, ");
        } else {
            this.stringBuilder.append(", TypePath.fromString(\"").append(typePath).append("\"), ");
        }
        this.appendConstant(descriptor);
        this.stringBuilder.append(", ").append(visible).append(");\n");
        this.text.add(this.stringBuilder.toString());
        ASMifier asmifier = this.createASMifier(ANNOTATION_VISITOR, 0);
        this.text.add(asmifier.getText());
        this.text.add("}\n");
        return asmifier;
    }

    public void visitAttribute(Attribute attribute) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("// ATTRIBUTE ").append(attribute.type).append('\n');
        if (attribute instanceof ASMifierSupport) {
            if (this.labelNames == null) {
                this.labelNames = new HashMap<Label, String>();
            }
            this.stringBuilder.append("{\n");
            ((ASMifierSupport)((Object)attribute)).asmify(this.stringBuilder, "attribute", this.labelNames);
            this.stringBuilder.append(this.name).append(".visitAttribute(attribute);\n");
            this.stringBuilder.append("}\n");
        }
        this.text.add(this.stringBuilder.toString());
    }

    private void visitMemberEnd() {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.name).append(VISIT_END);
        this.text.add(this.stringBuilder.toString());
    }

    protected ASMifier createASMifier(String visitorVariableName, int annotationVisitorId) {
        return new ASMifier(this.api, visitorVariableName, annotationVisitorId);
    }

    private void appendAccessFlags(int accessFlags) {
        boolean isEmpty = true;
        if ((accessFlags & 1) != 0) {
            this.stringBuilder.append("ACC_PUBLIC");
            isEmpty = false;
        }
        if ((accessFlags & 2) != 0) {
            this.stringBuilder.append("ACC_PRIVATE");
            isEmpty = false;
        }
        if ((accessFlags & 4) != 0) {
            this.stringBuilder.append("ACC_PROTECTED");
            isEmpty = false;
        }
        if ((accessFlags & 0x10) != 0) {
            if (!isEmpty) {
                this.stringBuilder.append(" | ");
            }
            if ((accessFlags & 0x200000) == 0) {
                this.stringBuilder.append("ACC_FINAL");
            } else {
                this.stringBuilder.append("ACC_TRANSITIVE");
            }
            isEmpty = false;
        }
        if ((accessFlags & 8) != 0) {
            if (!isEmpty) {
                this.stringBuilder.append(" | ");
            }
            this.stringBuilder.append("ACC_STATIC");
            isEmpty = false;
        }
        if ((accessFlags & 0x20) != 0) {
            if (!isEmpty) {
                this.stringBuilder.append(" | ");
            }
            if ((accessFlags & 0x40000) == 0) {
                if ((accessFlags & 0x200000) == 0) {
                    this.stringBuilder.append("ACC_SYNCHRONIZED");
                } else {
                    this.stringBuilder.append("ACC_TRANSITIVE");
                }
            } else {
                this.stringBuilder.append("ACC_SUPER");
            }
            isEmpty = false;
        }
        if ((accessFlags & 0x40) != 0) {
            if (!isEmpty) {
                this.stringBuilder.append(" | ");
            }
            if ((accessFlags & 0x80000) == 0) {
                if ((accessFlags & 0x200000) == 0) {
                    this.stringBuilder.append("ACC_BRIDGE");
                } else {
                    this.stringBuilder.append("ACC_STATIC_PHASE");
                }
            } else {
                this.stringBuilder.append("ACC_VOLATILE");
            }
            isEmpty = false;
        }
        if ((accessFlags & 0x80) != 0 && (accessFlags & 0xC0000) == 0) {
            if (!isEmpty) {
                this.stringBuilder.append(" | ");
            }
            this.stringBuilder.append("ACC_VARARGS");
            isEmpty = false;
        }
        if ((accessFlags & 0x80) != 0 && (accessFlags & 0x80000) != 0) {
            if (!isEmpty) {
                this.stringBuilder.append(" | ");
            }
            this.stringBuilder.append("ACC_TRANSIENT");
            isEmpty = false;
        }
        if ((accessFlags & 0x100) != 0 && (accessFlags & 0xC0000) == 0) {
            if (!isEmpty) {
                this.stringBuilder.append(" | ");
            }
            this.stringBuilder.append("ACC_NATIVE");
            isEmpty = false;
        }
        if ((accessFlags & 0x4000) != 0 && (accessFlags & 0x1C0000) != 0) {
            if (!isEmpty) {
                this.stringBuilder.append(" | ");
            }
            this.stringBuilder.append("ACC_ENUM");
            isEmpty = false;
        }
        if ((accessFlags & 0x2000) != 0 && (accessFlags & 0x140000) != 0) {
            if (!isEmpty) {
                this.stringBuilder.append(" | ");
            }
            this.stringBuilder.append("ACC_ANNOTATION");
            isEmpty = false;
        }
        if ((accessFlags & 0x400) != 0) {
            if (!isEmpty) {
                this.stringBuilder.append(" | ");
            }
            this.stringBuilder.append("ACC_ABSTRACT");
            isEmpty = false;
        }
        if ((accessFlags & 0x200) != 0) {
            if (!isEmpty) {
                this.stringBuilder.append(" | ");
            }
            this.stringBuilder.append("ACC_INTERFACE");
            isEmpty = false;
        }
        if ((accessFlags & 0x800) != 0) {
            if (!isEmpty) {
                this.stringBuilder.append(" | ");
            }
            this.stringBuilder.append("ACC_STRICT");
            isEmpty = false;
        }
        if ((accessFlags & 0x1000) != 0) {
            if (!isEmpty) {
                this.stringBuilder.append(" | ");
            }
            this.stringBuilder.append("ACC_SYNTHETIC");
            isEmpty = false;
        }
        if ((accessFlags & 0x20000) != 0) {
            if (!isEmpty) {
                this.stringBuilder.append(" | ");
            }
            this.stringBuilder.append("ACC_DEPRECATED");
            isEmpty = false;
        }
        if ((accessFlags & 0x10000) != 0) {
            if (!isEmpty) {
                this.stringBuilder.append(" | ");
            }
            this.stringBuilder.append("ACC_RECORD");
            isEmpty = false;
        }
        if ((accessFlags & 0x8000) != 0) {
            if (!isEmpty) {
                this.stringBuilder.append(" | ");
            }
            if ((accessFlags & 0x40000) == 0) {
                this.stringBuilder.append("ACC_MANDATED");
            } else {
                this.stringBuilder.append("ACC_MODULE");
            }
            isEmpty = false;
        }
        if (isEmpty) {
            this.stringBuilder.append('0');
        }
    }

    protected void appendConstant(Object value) {
        if (value == null) {
            this.stringBuilder.append("null");
        } else if (value instanceof String) {
            ASMifier.appendString(this.stringBuilder, (String)value);
        } else if (value instanceof Type) {
            this.stringBuilder.append("Type.getType(\"");
            this.stringBuilder.append(((Type)value).getDescriptor());
            this.stringBuilder.append("\")");
        } else if (value instanceof Handle) {
            this.stringBuilder.append("new Handle(");
            Handle handle = (Handle)value;
            this.stringBuilder.append("Opcodes.").append(HANDLE_TAG[handle.getTag()]).append(", \"");
            this.stringBuilder.append(handle.getOwner()).append(COMMA);
            this.stringBuilder.append(handle.getName()).append(COMMA);
            this.stringBuilder.append(handle.getDesc()).append("\", ");
            this.stringBuilder.append(handle.isInterface()).append(")");
        } else if (value instanceof ConstantDynamic) {
            this.stringBuilder.append("new ConstantDynamic(\"");
            ConstantDynamic constantDynamic = (ConstantDynamic)value;
            this.stringBuilder.append(constantDynamic.getName()).append(COMMA);
            this.stringBuilder.append(constantDynamic.getDescriptor()).append("\", ");
            this.appendConstant(constantDynamic.getBootstrapMethod());
            this.stringBuilder.append(NEW_OBJECT_ARRAY);
            int bootstrapMethodArgumentCount = constantDynamic.getBootstrapMethodArgumentCount();
            for (int i2 = 0; i2 < bootstrapMethodArgumentCount; ++i2) {
                this.appendConstant(constantDynamic.getBootstrapMethodArgument(i2));
                if (i2 == bootstrapMethodArgumentCount - 1) continue;
                this.stringBuilder.append(", ");
            }
            this.stringBuilder.append("})");
        } else if (value instanceof Byte) {
            this.stringBuilder.append("new Byte((byte)").append(value).append(')');
        } else if (value instanceof Boolean) {
            this.stringBuilder.append((Boolean)value != false ? "Boolean.TRUE" : "Boolean.FALSE");
        } else if (value instanceof Short) {
            this.stringBuilder.append("new Short((short)").append(value).append(')');
        } else if (value instanceof Character) {
            this.stringBuilder.append("new Character((char)").append((int)((Character)value).charValue()).append(')');
        } else if (value instanceof Integer) {
            this.stringBuilder.append("new Integer(").append(value).append(')');
        } else if (value instanceof Float) {
            this.stringBuilder.append("new Float(\"").append(value).append("\")");
        } else if (value instanceof Long) {
            this.stringBuilder.append("new Long(").append(value).append("L)");
        } else if (value instanceof Double) {
            this.stringBuilder.append("new Double(\"").append(value).append("\")");
        } else if (value instanceof byte[]) {
            byte[] byteArray = (byte[])value;
            this.stringBuilder.append("new byte[] {");
            for (int i3 = 0; i3 < byteArray.length; ++i3) {
                this.stringBuilder.append(i3 == 0 ? "" : ",").append(byteArray[i3]);
            }
            this.stringBuilder.append('}');
        } else if (value instanceof boolean[]) {
            boolean[] booleanArray = (boolean[])value;
            this.stringBuilder.append("new boolean[] {");
            for (int i4 = 0; i4 < booleanArray.length; ++i4) {
                this.stringBuilder.append(i4 == 0 ? "" : ",").append(booleanArray[i4]);
            }
            this.stringBuilder.append('}');
        } else if (value instanceof short[]) {
            short[] shortArray = (short[])value;
            this.stringBuilder.append("new short[] {");
            for (int i5 = 0; i5 < shortArray.length; ++i5) {
                this.stringBuilder.append(i5 == 0 ? "" : ",").append("(short)").append(shortArray[i5]);
            }
            this.stringBuilder.append('}');
        } else if (value instanceof char[]) {
            char[] charArray = (char[])value;
            this.stringBuilder.append("new char[] {");
            for (int i6 = 0; i6 < charArray.length; ++i6) {
                this.stringBuilder.append(i6 == 0 ? "" : ",").append("(char)").append((int)charArray[i6]);
            }
            this.stringBuilder.append('}');
        } else if (value instanceof int[]) {
            int[] intArray = (int[])value;
            this.stringBuilder.append("new int[] {");
            for (int i7 = 0; i7 < intArray.length; ++i7) {
                this.stringBuilder.append(i7 == 0 ? "" : ",").append(intArray[i7]);
            }
            this.stringBuilder.append('}');
        } else if (value instanceof long[]) {
            long[] longArray = (long[])value;
            this.stringBuilder.append("new long[] {");
            for (int i8 = 0; i8 < longArray.length; ++i8) {
                this.stringBuilder.append(i8 == 0 ? "" : ",").append(longArray[i8]).append('L');
            }
            this.stringBuilder.append('}');
        } else if (value instanceof float[]) {
            float[] floatArray = (float[])value;
            this.stringBuilder.append("new float[] {");
            for (int i9 = 0; i9 < floatArray.length; ++i9) {
                this.stringBuilder.append(i9 == 0 ? "" : ",").append(floatArray[i9]).append('f');
            }
            this.stringBuilder.append('}');
        } else if (value instanceof double[]) {
            double[] doubleArray = (double[])value;
            this.stringBuilder.append("new double[] {");
            for (int i10 = 0; i10 < doubleArray.length; ++i10) {
                this.stringBuilder.append(i10 == 0 ? "" : ",").append(doubleArray[i10]).append('d');
            }
            this.stringBuilder.append('}');
        }
    }

    private void declareFrameTypes(int numTypes, Object[] frameTypes) {
        for (int i2 = 0; i2 < numTypes; ++i2) {
            if (!(frameTypes[i2] instanceof Label)) continue;
            this.declareLabel((Label)frameTypes[i2]);
        }
    }

    private void appendFrameTypes(int numTypes, Object[] frameTypes) {
        for (int i2 = 0; i2 < numTypes; ++i2) {
            if (i2 > 0) {
                this.stringBuilder.append(", ");
            }
            if (frameTypes[i2] instanceof String) {
                this.appendConstant(frameTypes[i2]);
                continue;
            }
            if (frameTypes[i2] instanceof Integer) {
                this.stringBuilder.append(FRAME_TYPES.get((Integer)frameTypes[i2]));
                continue;
            }
            this.appendLabel((Label)frameTypes[i2]);
        }
    }

    protected void declareLabel(Label label) {
        String labelName;
        if (this.labelNames == null) {
            this.labelNames = new HashMap<Label, String>();
        }
        if ((labelName = this.labelNames.get(label)) == null) {
            labelName = "label" + this.labelNames.size();
            this.labelNames.put(label, labelName);
            this.stringBuilder.append("Label ").append(labelName).append(" = new Label();\n");
        }
    }

    protected void appendLabel(Label label) {
        this.stringBuilder.append(this.labelNames.get(label));
    }

    static {
        HashMap<Integer, String> classVersions = new HashMap<Integer, String>();
        classVersions.put(196653, "V1_1");
        classVersions.put(46, "V1_2");
        classVersions.put(47, "V1_3");
        classVersions.put(48, "V1_4");
        classVersions.put(49, "V1_5");
        classVersions.put(50, "V1_6");
        classVersions.put(51, "V1_7");
        classVersions.put(52, "V1_8");
        classVersions.put(53, "V9");
        classVersions.put(54, "V10");
        classVersions.put(55, "V11");
        classVersions.put(56, "V12");
        classVersions.put(57, "V13");
        classVersions.put(58, "V14");
        classVersions.put(59, "V15");
        classVersions.put(60, "V16");
        classVersions.put(61, "V17");
        classVersions.put(62, "V18");
        CLASS_VERSIONS = Collections.unmodifiableMap(classVersions);
    }
}

