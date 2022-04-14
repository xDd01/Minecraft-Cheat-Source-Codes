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
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.TextifierSupport;
import org.objectweb.asm.util.TraceSignatureVisitor;

public class Textifier
extends Printer {
    private static final String USAGE = "Prints a disassembled view of the given class.\nUsage: Textifier [-nodebug] <fully qualified class name or class file name>";
    public static final int INTERNAL_NAME = 0;
    public static final int FIELD_DESCRIPTOR = 1;
    public static final int FIELD_SIGNATURE = 2;
    public static final int METHOD_DESCRIPTOR = 3;
    public static final int METHOD_SIGNATURE = 4;
    public static final int CLASS_SIGNATURE = 5;
    public static final int HANDLE_DESCRIPTOR = 9;
    private static final String CLASS_SUFFIX = ".class";
    private static final String DEPRECATED = "// DEPRECATED\n";
    private static final String RECORD = "// RECORD\n";
    private static final String INVISIBLE = " // invisible\n";
    private static final List<String> FRAME_TYPES = Collections.unmodifiableList(Arrays.asList("T", "I", "F", "D", "J", "N", "U"));
    protected String tab = "  ";
    protected String tab2 = "    ";
    protected String tab3 = "      ";
    protected String ltab = "   ";
    protected Map<Label, String> labelNames;
    private int access;
    private int numAnnotationValues;

    public Textifier() {
        this(589824);
        if (this.getClass() != Textifier.class) {
            throw new IllegalStateException();
        }
    }

    protected Textifier(int api2) {
        super(api2);
    }

    public static void main(String[] args) throws IOException {
        Textifier.main(args, new PrintWriter(System.out, true), new PrintWriter(System.err, true));
    }

    static void main(String[] args, PrintWriter output, PrintWriter logger) throws IOException {
        Textifier.main(args, USAGE, new Textifier(), output, logger);
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if ((access & 0x8000) != 0) {
            return;
        }
        this.access = access;
        int majorVersion = version & 0xFFFF;
        int minorVersion = version >>> 16;
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("// class version ").append(majorVersion).append('.').append(minorVersion).append(" (").append(version).append(")\n");
        if ((access & 0x20000) != 0) {
            this.stringBuilder.append(DEPRECATED);
        }
        if ((access & 0x10000) != 0) {
            this.stringBuilder.append(RECORD);
        }
        this.appendRawAccess(access);
        this.appendDescriptor(5, signature);
        if (signature != null) {
            this.appendJavaDeclaration(name, signature);
        }
        this.appendAccess(access & 0xFFFF7FDF);
        if ((access & 0x2000) != 0) {
            this.stringBuilder.append("@interface ");
        } else if ((access & 0x200) != 0) {
            this.stringBuilder.append("interface ");
        } else if ((access & 0x4000) == 0) {
            this.stringBuilder.append("class ");
        }
        this.appendDescriptor(0, name);
        if (superName != null && !"java/lang/Object".equals(superName)) {
            this.stringBuilder.append(" extends ");
            this.appendDescriptor(0, superName);
        }
        if (interfaces != null && interfaces.length > 0) {
            this.stringBuilder.append(" implements ");
            for (int i2 = 0; i2 < interfaces.length; ++i2) {
                this.appendDescriptor(0, interfaces[i2]);
                if (i2 == interfaces.length - 1) continue;
                this.stringBuilder.append(' ');
            }
        }
        this.stringBuilder.append(" {\n\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitSource(String file, String debug) {
        this.stringBuilder.setLength(0);
        if (file != null) {
            this.stringBuilder.append(this.tab).append("// compiled from: ").append(file).append('\n');
        }
        if (debug != null) {
            this.stringBuilder.append(this.tab).append("// debug info: ").append(debug).append('\n');
        }
        if (this.stringBuilder.length() > 0) {
            this.text.add(this.stringBuilder.toString());
        }
    }

    public Printer visitModule(String name, int access, String version) {
        this.stringBuilder.setLength(0);
        if ((access & 0x20) != 0) {
            this.stringBuilder.append("open ");
        }
        this.stringBuilder.append("module ").append(name).append(" { ").append(version == null ? "" : "// " + version).append("\n\n");
        this.text.add(this.stringBuilder.toString());
        return this.addNewTextifier(null);
    }

    public void visitNestHost(String nestHost) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append("NESTHOST ");
        this.appendDescriptor(0, nestHost);
        this.stringBuilder.append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public void visitOuterClass(String owner, String name, String descriptor) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append("OUTERCLASS ");
        this.appendDescriptor(0, owner);
        this.stringBuilder.append(' ');
        if (name != null) {
            this.stringBuilder.append(name).append(' ');
        }
        this.appendDescriptor(3, descriptor);
        this.stringBuilder.append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public Textifier visitClassAnnotation(String descriptor, boolean visible) {
        this.text.add("\n");
        return this.visitAnnotation(descriptor, visible);
    }

    public Printer visitClassTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        this.text.add("\n");
        return this.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }

    public void visitClassAttribute(Attribute attribute) {
        this.text.add("\n");
        this.visitAttribute(attribute);
    }

    public void visitNestMember(String nestMember) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append("NESTMEMBER ");
        this.appendDescriptor(0, nestMember);
        this.stringBuilder.append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public void visitPermittedSubclass(String permittedSubclass) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append("PERMITTEDSUBCLASS ");
        this.appendDescriptor(0, permittedSubclass);
        this.stringBuilder.append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab);
        this.appendRawAccess(access & 0xFFFFFFDF);
        this.stringBuilder.append(this.tab);
        this.appendAccess(access);
        this.stringBuilder.append("INNERCLASS ");
        this.appendDescriptor(0, name);
        this.stringBuilder.append(' ');
        this.appendDescriptor(0, outerName);
        this.stringBuilder.append(' ');
        this.appendDescriptor(0, innerName);
        this.stringBuilder.append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public Printer visitRecordComponent(String name, String descriptor, String signature) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append("RECORDCOMPONENT ");
        if (signature != null) {
            this.stringBuilder.append(this.tab);
            this.appendDescriptor(2, signature);
            this.stringBuilder.append(this.tab);
            this.appendJavaDeclaration(name, signature);
        }
        this.stringBuilder.append(this.tab);
        this.appendDescriptor(1, descriptor);
        this.stringBuilder.append(' ').append(name);
        this.stringBuilder.append('\n');
        this.text.add(this.stringBuilder.toString());
        return this.addNewTextifier(null);
    }

    public Textifier visitField(int access, String name, String descriptor, String signature, Object value) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append('\n');
        if ((access & 0x20000) != 0) {
            this.stringBuilder.append(this.tab).append(DEPRECATED);
        }
        this.stringBuilder.append(this.tab);
        this.appendRawAccess(access);
        if (signature != null) {
            this.stringBuilder.append(this.tab);
            this.appendDescriptor(2, signature);
            this.stringBuilder.append(this.tab);
            this.appendJavaDeclaration(name, signature);
        }
        this.stringBuilder.append(this.tab);
        this.appendAccess(access);
        this.appendDescriptor(1, descriptor);
        this.stringBuilder.append(' ').append(name);
        if (value != null) {
            this.stringBuilder.append(" = ");
            if (value instanceof String) {
                this.stringBuilder.append('\"').append(value).append('\"');
            } else {
                this.stringBuilder.append(value);
            }
        }
        this.stringBuilder.append('\n');
        this.text.add(this.stringBuilder.toString());
        return this.addNewTextifier(null);
    }

    public Textifier visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append('\n');
        if ((access & 0x20000) != 0) {
            this.stringBuilder.append(this.tab).append(DEPRECATED);
        }
        this.stringBuilder.append(this.tab);
        this.appendRawAccess(access);
        if (signature != null) {
            this.stringBuilder.append(this.tab);
            this.appendDescriptor(4, signature);
            this.stringBuilder.append(this.tab);
            this.appendJavaDeclaration(name, signature);
        }
        this.stringBuilder.append(this.tab);
        this.appendAccess(access & 0xFFFFFF3F);
        if ((access & 0x100) != 0) {
            this.stringBuilder.append("native ");
        }
        if ((access & 0x80) != 0) {
            this.stringBuilder.append("varargs ");
        }
        if ((access & 0x40) != 0) {
            this.stringBuilder.append("bridge ");
        }
        if ((this.access & 0x200) != 0 && (access & 0x408) == 0) {
            this.stringBuilder.append("default ");
        }
        this.stringBuilder.append(name);
        this.appendDescriptor(3, descriptor);
        if (exceptions != null && exceptions.length > 0) {
            this.stringBuilder.append(" throws ");
            for (String exception : exceptions) {
                this.appendDescriptor(0, exception);
                this.stringBuilder.append(' ');
            }
        }
        this.stringBuilder.append('\n');
        this.text.add(this.stringBuilder.toString());
        return this.addNewTextifier(null);
    }

    public void visitClassEnd() {
        this.text.add("}\n");
    }

    public void visitMainClass(String mainClass) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("  // main class ").append(mainClass).append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public void visitPackage(String packaze) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("  // package ").append(packaze).append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public void visitRequire(String require, int access, String version) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append("requires ");
        if ((access & 0x20) != 0) {
            this.stringBuilder.append("transitive ");
        }
        if ((access & 0x40) != 0) {
            this.stringBuilder.append("static ");
        }
        this.stringBuilder.append(require).append(';');
        this.appendRawAccess(access);
        if (version != null) {
            this.stringBuilder.append("  // version ").append(version).append('\n');
        }
        this.text.add(this.stringBuilder.toString());
    }

    public void visitExport(String packaze, int access, String ... modules) {
        this.visitExportOrOpen("exports ", packaze, access, modules);
    }

    public void visitOpen(String packaze, int access, String ... modules) {
        this.visitExportOrOpen("opens ", packaze, access, modules);
    }

    private void visitExportOrOpen(String method, String packaze, int access, String ... modules) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append(method);
        this.stringBuilder.append(packaze);
        if (modules != null && modules.length > 0) {
            this.stringBuilder.append(" to");
        } else {
            this.stringBuilder.append(';');
        }
        this.appendRawAccess(access);
        if (modules != null && modules.length > 0) {
            for (int i2 = 0; i2 < modules.length; ++i2) {
                this.stringBuilder.append(this.tab2).append(modules[i2]);
                this.stringBuilder.append(i2 != modules.length - 1 ? ",\n" : ";\n");
            }
        }
        this.text.add(this.stringBuilder.toString());
    }

    public void visitUse(String use) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append("uses ");
        this.appendDescriptor(0, use);
        this.stringBuilder.append(";\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitProvide(String provide, String ... providers) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append("provides ");
        this.appendDescriptor(0, provide);
        this.stringBuilder.append(" with\n");
        for (int i2 = 0; i2 < providers.length; ++i2) {
            this.stringBuilder.append(this.tab2);
            this.appendDescriptor(0, providers[i2]);
            this.stringBuilder.append(i2 != providers.length - 1 ? ",\n" : ";\n");
        }
        this.text.add(this.stringBuilder.toString());
    }

    public void visitModuleEnd() {
    }

    public void visit(String name, Object value) {
        this.visitAnnotationValue(name);
        if (value instanceof String) {
            this.visitString((String)value);
        } else if (value instanceof Type) {
            this.visitType((Type)value);
        } else if (value instanceof Byte) {
            this.visitByte((Byte)value);
        } else if (value instanceof Boolean) {
            this.visitBoolean((Boolean)value);
        } else if (value instanceof Short) {
            this.visitShort((Short)value);
        } else if (value instanceof Character) {
            this.visitChar(((Character)value).charValue());
        } else if (value instanceof Integer) {
            this.visitInt((Integer)value);
        } else if (value instanceof Float) {
            this.visitFloat(((Float)value).floatValue());
        } else if (value instanceof Long) {
            this.visitLong((Long)value);
        } else if (value instanceof Double) {
            this.visitDouble((Double)value);
        } else if (value.getClass().isArray()) {
            this.stringBuilder.append('{');
            if (value instanceof byte[]) {
                byte[] byteArray = (byte[])value;
                for (int i2 = 0; i2 < byteArray.length; ++i2) {
                    this.maybeAppendComma(i2);
                    this.visitByte(byteArray[i2]);
                }
            } else if (value instanceof boolean[]) {
                boolean[] booleanArray = (boolean[])value;
                for (int i3 = 0; i3 < booleanArray.length; ++i3) {
                    this.maybeAppendComma(i3);
                    this.visitBoolean(booleanArray[i3]);
                }
            } else if (value instanceof short[]) {
                short[] shortArray = (short[])value;
                for (int i4 = 0; i4 < shortArray.length; ++i4) {
                    this.maybeAppendComma(i4);
                    this.visitShort(shortArray[i4]);
                }
            } else if (value instanceof char[]) {
                char[] charArray = (char[])value;
                for (int i5 = 0; i5 < charArray.length; ++i5) {
                    this.maybeAppendComma(i5);
                    this.visitChar(charArray[i5]);
                }
            } else if (value instanceof int[]) {
                int[] intArray = (int[])value;
                for (int i6 = 0; i6 < intArray.length; ++i6) {
                    this.maybeAppendComma(i6);
                    this.visitInt(intArray[i6]);
                }
            } else if (value instanceof long[]) {
                long[] longArray = (long[])value;
                for (int i7 = 0; i7 < longArray.length; ++i7) {
                    this.maybeAppendComma(i7);
                    this.visitLong(longArray[i7]);
                }
            } else if (value instanceof float[]) {
                float[] floatArray = (float[])value;
                for (int i8 = 0; i8 < floatArray.length; ++i8) {
                    this.maybeAppendComma(i8);
                    this.visitFloat(floatArray[i8]);
                }
            } else if (value instanceof double[]) {
                double[] doubleArray = (double[])value;
                for (int i9 = 0; i9 < doubleArray.length; ++i9) {
                    this.maybeAppendComma(i9);
                    this.visitDouble(doubleArray[i9]);
                }
            }
            this.stringBuilder.append('}');
        }
        this.text.add(this.stringBuilder.toString());
    }

    private void visitInt(int value) {
        this.stringBuilder.append(value);
    }

    private void visitLong(long value) {
        this.stringBuilder.append(value).append('L');
    }

    private void visitFloat(float value) {
        this.stringBuilder.append(value).append('F');
    }

    private void visitDouble(double value) {
        this.stringBuilder.append(value).append('D');
    }

    private void visitChar(char value) {
        this.stringBuilder.append("(char)").append((int)value);
    }

    private void visitShort(short value) {
        this.stringBuilder.append("(short)").append(value);
    }

    private void visitByte(byte value) {
        this.stringBuilder.append("(byte)").append(value);
    }

    private void visitBoolean(boolean value) {
        this.stringBuilder.append(value);
    }

    private void visitString(String value) {
        Textifier.appendString(this.stringBuilder, value);
    }

    private void visitType(Type value) {
        this.stringBuilder.append(value.getClassName()).append(CLASS_SUFFIX);
    }

    public void visitEnum(String name, String descriptor, String value) {
        this.visitAnnotationValue(name);
        this.appendDescriptor(1, descriptor);
        this.stringBuilder.append('.').append(value);
        this.text.add(this.stringBuilder.toString());
    }

    public Textifier visitAnnotation(String name, String descriptor) {
        this.visitAnnotationValue(name);
        this.stringBuilder.append('@');
        this.appendDescriptor(1, descriptor);
        this.stringBuilder.append('(');
        this.text.add(this.stringBuilder.toString());
        return this.addNewTextifier(")");
    }

    public Textifier visitArray(String name) {
        this.visitAnnotationValue(name);
        this.stringBuilder.append('{');
        this.text.add(this.stringBuilder.toString());
        return this.addNewTextifier("}");
    }

    public void visitAnnotationEnd() {
    }

    private void visitAnnotationValue(String name) {
        this.stringBuilder.setLength(0);
        this.maybeAppendComma(this.numAnnotationValues++);
        if (name != null) {
            this.stringBuilder.append(name).append('=');
        }
    }

    public Textifier visitRecordComponentAnnotation(String descriptor, boolean visible) {
        return this.visitAnnotation(descriptor, visible);
    }

    public Printer visitRecordComponentTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return this.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }

    public void visitRecordComponentAttribute(Attribute attribute) {
        this.visitAttribute(attribute);
    }

    public void visitRecordComponentEnd() {
    }

    public Textifier visitFieldAnnotation(String descriptor, boolean visible) {
        return this.visitAnnotation(descriptor, visible);
    }

    public Printer visitFieldTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return this.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }

    public void visitFieldAttribute(Attribute attribute) {
        this.visitAttribute(attribute);
    }

    public void visitFieldEnd() {
    }

    public void visitParameter(String name, int access) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append("// parameter ");
        this.appendAccess(access);
        this.stringBuilder.append(' ').append(name == null ? "<no name>" : name).append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public Textifier visitAnnotationDefault() {
        this.text.add(this.tab2 + "default=");
        return this.addNewTextifier("\n");
    }

    public Textifier visitMethodAnnotation(String descriptor, boolean visible) {
        return this.visitAnnotation(descriptor, visible);
    }

    public Printer visitMethodTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return this.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }

    public Textifier visitAnnotableParameterCount(int parameterCount, boolean visible) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append("// annotable parameter count: ");
        this.stringBuilder.append(parameterCount);
        this.stringBuilder.append(visible ? " (visible)\n" : " (invisible)\n");
        this.text.add(this.stringBuilder.toString());
        return this;
    }

    public Textifier visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append('@');
        this.appendDescriptor(1, descriptor);
        this.stringBuilder.append('(');
        this.text.add(this.stringBuilder.toString());
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(visible ? ") // parameter " : ") // invisible, parameter ").append(parameter).append('\n');
        return this.addNewTextifier(this.stringBuilder.toString());
    }

    public void visitMethodAttribute(Attribute attribute) {
        this.visitAttribute(attribute);
    }

    public void visitCode() {
    }

    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.ltab);
        this.stringBuilder.append("FRAME ");
        switch (type) {
            case -1: 
            case 0: {
                this.stringBuilder.append("FULL [");
                this.appendFrameTypes(numLocal, local);
                this.stringBuilder.append("] [");
                this.appendFrameTypes(numStack, stack);
                this.stringBuilder.append(']');
                break;
            }
            case 1: {
                this.stringBuilder.append("APPEND [");
                this.appendFrameTypes(numLocal, local);
                this.stringBuilder.append(']');
                break;
            }
            case 2: {
                this.stringBuilder.append("CHOP ").append(numLocal);
                break;
            }
            case 3: {
                this.stringBuilder.append("SAME");
                break;
            }
            case 4: {
                this.stringBuilder.append("SAME1 ");
                this.appendFrameTypes(1, stack);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        this.stringBuilder.append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public void visitInsn(int opcode) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append(OPCODES[opcode]).append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public void visitIntInsn(int opcode, int operand) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append(OPCODES[opcode]).append(' ').append(opcode == 188 ? TYPES[operand] : Integer.toString(operand)).append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public void visitVarInsn(int opcode, int var) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append(OPCODES[opcode]).append(' ').append(var).append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public void visitTypeInsn(int opcode, String type) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append(OPCODES[opcode]).append(' ');
        this.appendDescriptor(0, type);
        this.stringBuilder.append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append(OPCODES[opcode]).append(' ');
        this.appendDescriptor(0, owner);
        this.stringBuilder.append('.').append(name).append(" : ");
        this.appendDescriptor(1, descriptor);
        this.stringBuilder.append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append(OPCODES[opcode]).append(' ');
        this.appendDescriptor(0, owner);
        this.stringBuilder.append('.').append(name).append(' ');
        this.appendDescriptor(3, descriptor);
        if (isInterface) {
            this.stringBuilder.append(" (itf)");
        }
        this.stringBuilder.append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object ... bootstrapMethodArguments) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append("INVOKEDYNAMIC").append(' ');
        this.stringBuilder.append(name);
        this.appendDescriptor(3, descriptor);
        this.stringBuilder.append(" [");
        this.stringBuilder.append('\n');
        this.stringBuilder.append(this.tab3);
        this.appendHandle(bootstrapMethodHandle);
        this.stringBuilder.append('\n');
        this.stringBuilder.append(this.tab3).append("// arguments:");
        if (bootstrapMethodArguments.length == 0) {
            this.stringBuilder.append(" none");
        } else {
            this.stringBuilder.append('\n');
            for (Object value : bootstrapMethodArguments) {
                this.stringBuilder.append(this.tab3);
                if (value instanceof String) {
                    Printer.appendString(this.stringBuilder, (String)value);
                } else if (value instanceof Type) {
                    Type type = (Type)value;
                    if (type.getSort() == 11) {
                        this.appendDescriptor(3, type.getDescriptor());
                    } else {
                        this.visitType(type);
                    }
                } else if (value instanceof Handle) {
                    this.appendHandle((Handle)value);
                } else {
                    this.stringBuilder.append(value);
                }
                this.stringBuilder.append(", \n");
            }
            this.stringBuilder.setLength(this.stringBuilder.length() - 3);
        }
        this.stringBuilder.append('\n');
        this.stringBuilder.append(this.tab2).append("]\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitJumpInsn(int opcode, Label label) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append(OPCODES[opcode]).append(' ');
        this.appendLabel(label);
        this.stringBuilder.append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public void visitLabel(Label label) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.ltab);
        this.appendLabel(label);
        this.stringBuilder.append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public void visitLdcInsn(Object value) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append("LDC ");
        if (value instanceof String) {
            Printer.appendString(this.stringBuilder, (String)value);
        } else if (value instanceof Type) {
            this.stringBuilder.append(((Type)value).getDescriptor()).append(CLASS_SUFFIX);
        } else {
            this.stringBuilder.append(value);
        }
        this.stringBuilder.append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public void visitIincInsn(int var, int increment) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append("IINC ").append(var).append(' ').append(increment).append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public void visitTableSwitchInsn(int min, int max, Label dflt, Label ... labels) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append("TABLESWITCH\n");
        for (int i2 = 0; i2 < labels.length; ++i2) {
            this.stringBuilder.append(this.tab3).append(min + i2).append(": ");
            this.appendLabel(labels[i2]);
            this.stringBuilder.append('\n');
        }
        this.stringBuilder.append(this.tab3).append("default: ");
        this.appendLabel(dflt);
        this.stringBuilder.append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append("LOOKUPSWITCH\n");
        for (int i2 = 0; i2 < labels.length; ++i2) {
            this.stringBuilder.append(this.tab3).append(keys[i2]).append(": ");
            this.appendLabel(labels[i2]);
            this.stringBuilder.append('\n');
        }
        this.stringBuilder.append(this.tab3).append("default: ");
        this.appendLabel(dflt);
        this.stringBuilder.append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append("MULTIANEWARRAY ");
        this.appendDescriptor(1, descriptor);
        this.stringBuilder.append(' ').append(numDimensions).append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public Printer visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return this.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }

    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append("TRYCATCHBLOCK ");
        this.appendLabel(start);
        this.stringBuilder.append(' ');
        this.appendLabel(end);
        this.stringBuilder.append(' ');
        this.appendLabel(handler);
        this.stringBuilder.append(' ');
        this.appendDescriptor(0, type);
        this.stringBuilder.append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public Printer visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append("TRYCATCHBLOCK @");
        this.appendDescriptor(1, descriptor);
        this.stringBuilder.append('(');
        this.text.add(this.stringBuilder.toString());
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(") : ");
        this.appendTypeReference(typeRef);
        this.stringBuilder.append(", ").append(typePath);
        this.stringBuilder.append(visible ? "\n" : INVISIBLE);
        return this.addNewTextifier(this.stringBuilder.toString());
    }

    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append("LOCALVARIABLE ").append(name).append(' ');
        this.appendDescriptor(1, descriptor);
        this.stringBuilder.append(' ');
        this.appendLabel(start);
        this.stringBuilder.append(' ');
        this.appendLabel(end);
        this.stringBuilder.append(' ').append(index).append('\n');
        if (signature != null) {
            this.stringBuilder.append(this.tab2);
            this.appendDescriptor(2, signature);
            this.stringBuilder.append(this.tab2);
            this.appendJavaDeclaration(name, signature);
        }
        this.text.add(this.stringBuilder.toString());
    }

    public Printer visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append("LOCALVARIABLE @");
        this.appendDescriptor(1, descriptor);
        this.stringBuilder.append('(');
        this.text.add(this.stringBuilder.toString());
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(") : ");
        this.appendTypeReference(typeRef);
        this.stringBuilder.append(", ").append(typePath);
        for (int i2 = 0; i2 < start.length; ++i2) {
            this.stringBuilder.append(" [ ");
            this.appendLabel(start[i2]);
            this.stringBuilder.append(" - ");
            this.appendLabel(end[i2]);
            this.stringBuilder.append(" - ").append(index[i2]).append(" ]");
        }
        this.stringBuilder.append(visible ? "\n" : INVISIBLE);
        return this.addNewTextifier(this.stringBuilder.toString());
    }

    public void visitLineNumber(int line, Label start) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append("LINENUMBER ").append(line).append(' ');
        this.appendLabel(start);
        this.stringBuilder.append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public void visitMaxs(int maxStack, int maxLocals) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append("MAXSTACK = ").append(maxStack).append('\n');
        this.text.add(this.stringBuilder.toString());
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append("MAXLOCALS = ").append(maxLocals).append('\n');
        this.text.add(this.stringBuilder.toString());
    }

    public void visitMethodEnd() {
    }

    public Textifier visitAnnotation(String descriptor, boolean visible) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append('@');
        this.appendDescriptor(1, descriptor);
        this.stringBuilder.append('(');
        this.text.add(this.stringBuilder.toString());
        return this.addNewTextifier(visible ? ")\n" : ") // invisible\n");
    }

    public Textifier visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append('@');
        this.appendDescriptor(1, descriptor);
        this.stringBuilder.append('(');
        this.text.add(this.stringBuilder.toString());
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(") : ");
        this.appendTypeReference(typeRef);
        this.stringBuilder.append(", ").append(typePath);
        this.stringBuilder.append(visible ? "\n" : INVISIBLE);
        return this.addNewTextifier(this.stringBuilder.toString());
    }

    public void visitAttribute(Attribute attribute) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append("ATTRIBUTE ");
        this.appendDescriptor(-1, attribute.type);
        if (attribute instanceof TextifierSupport) {
            if (this.labelNames == null) {
                this.labelNames = new HashMap<Label, String>();
            }
            ((TextifierSupport)((Object)attribute)).textify(this.stringBuilder, this.labelNames);
        } else {
            this.stringBuilder.append(" : unknown\n");
        }
        this.text.add(this.stringBuilder.toString());
    }

    private void appendAccess(int accessFlags) {
        if ((accessFlags & 1) != 0) {
            this.stringBuilder.append("public ");
        }
        if ((accessFlags & 2) != 0) {
            this.stringBuilder.append("private ");
        }
        if ((accessFlags & 4) != 0) {
            this.stringBuilder.append("protected ");
        }
        if ((accessFlags & 0x10) != 0) {
            this.stringBuilder.append("final ");
        }
        if ((accessFlags & 8) != 0) {
            this.stringBuilder.append("static ");
        }
        if ((accessFlags & 0x20) != 0) {
            this.stringBuilder.append("synchronized ");
        }
        if ((accessFlags & 0x40) != 0) {
            this.stringBuilder.append("volatile ");
        }
        if ((accessFlags & 0x80) != 0) {
            this.stringBuilder.append("transient ");
        }
        if ((accessFlags & 0x400) != 0) {
            this.stringBuilder.append("abstract ");
        }
        if ((accessFlags & 0x800) != 0) {
            this.stringBuilder.append("strictfp ");
        }
        if ((accessFlags & 0x1000) != 0) {
            this.stringBuilder.append("synthetic ");
        }
        if ((accessFlags & 0x8000) != 0) {
            this.stringBuilder.append("mandated ");
        }
        if ((accessFlags & 0x4000) != 0) {
            this.stringBuilder.append("enum ");
        }
    }

    private void appendRawAccess(int accessFlags) {
        this.stringBuilder.append("// access flags 0x").append(Integer.toHexString(accessFlags).toUpperCase()).append('\n');
    }

    protected void appendDescriptor(int type, String value) {
        if (type == 5 || type == 2 || type == 4) {
            if (value != null) {
                this.stringBuilder.append("// signature ").append(value).append('\n');
            }
        } else {
            this.stringBuilder.append(value);
        }
    }

    private void appendJavaDeclaration(String name, String signature) {
        TraceSignatureVisitor traceSignatureVisitor = new TraceSignatureVisitor(this.access);
        new SignatureReader(signature).accept(traceSignatureVisitor);
        this.stringBuilder.append("// declaration: ");
        if (traceSignatureVisitor.getReturnType() != null) {
            this.stringBuilder.append(traceSignatureVisitor.getReturnType());
            this.stringBuilder.append(' ');
        }
        this.stringBuilder.append(name);
        this.stringBuilder.append(traceSignatureVisitor.getDeclaration());
        if (traceSignatureVisitor.getExceptions() != null) {
            this.stringBuilder.append(" throws ").append(traceSignatureVisitor.getExceptions());
        }
        this.stringBuilder.append('\n');
    }

    protected void appendLabel(Label label) {
        String name;
        if (this.labelNames == null) {
            this.labelNames = new HashMap<Label, String>();
        }
        if ((name = this.labelNames.get(label)) == null) {
            name = "L" + this.labelNames.size();
            this.labelNames.put(label, name);
        }
        this.stringBuilder.append(name);
    }

    protected void appendHandle(Handle handle) {
        int tag = handle.getTag();
        this.stringBuilder.append("// handle kind 0x").append(Integer.toHexString(tag)).append(" : ");
        boolean isMethodHandle = false;
        switch (tag) {
            case 1: {
                this.stringBuilder.append("GETFIELD");
                break;
            }
            case 2: {
                this.stringBuilder.append("GETSTATIC");
                break;
            }
            case 3: {
                this.stringBuilder.append("PUTFIELD");
                break;
            }
            case 4: {
                this.stringBuilder.append("PUTSTATIC");
                break;
            }
            case 9: {
                this.stringBuilder.append("INVOKEINTERFACE");
                isMethodHandle = true;
                break;
            }
            case 7: {
                this.stringBuilder.append("INVOKESPECIAL");
                isMethodHandle = true;
                break;
            }
            case 6: {
                this.stringBuilder.append("INVOKESTATIC");
                isMethodHandle = true;
                break;
            }
            case 5: {
                this.stringBuilder.append("INVOKEVIRTUAL");
                isMethodHandle = true;
                break;
            }
            case 8: {
                this.stringBuilder.append("NEWINVOKESPECIAL");
                isMethodHandle = true;
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        this.stringBuilder.append('\n');
        this.stringBuilder.append(this.tab3);
        this.appendDescriptor(0, handle.getOwner());
        this.stringBuilder.append('.');
        this.stringBuilder.append(handle.getName());
        if (!isMethodHandle) {
            this.stringBuilder.append('(');
        }
        this.appendDescriptor(9, handle.getDesc());
        if (!isMethodHandle) {
            this.stringBuilder.append(')');
        }
        if (handle.isInterface()) {
            this.stringBuilder.append(" itf");
        }
    }

    private void maybeAppendComma(int numValues) {
        if (numValues > 0) {
            this.stringBuilder.append(", ");
        }
    }

    private void appendTypeReference(int typeRef) {
        TypeReference typeReference = new TypeReference(typeRef);
        switch (typeReference.getSort()) {
            case 0: {
                this.stringBuilder.append("CLASS_TYPE_PARAMETER ").append(typeReference.getTypeParameterIndex());
                break;
            }
            case 1: {
                this.stringBuilder.append("METHOD_TYPE_PARAMETER ").append(typeReference.getTypeParameterIndex());
                break;
            }
            case 16: {
                this.stringBuilder.append("CLASS_EXTENDS ").append(typeReference.getSuperTypeIndex());
                break;
            }
            case 17: {
                this.stringBuilder.append("CLASS_TYPE_PARAMETER_BOUND ").append(typeReference.getTypeParameterIndex()).append(", ").append(typeReference.getTypeParameterBoundIndex());
                break;
            }
            case 18: {
                this.stringBuilder.append("METHOD_TYPE_PARAMETER_BOUND ").append(typeReference.getTypeParameterIndex()).append(", ").append(typeReference.getTypeParameterBoundIndex());
                break;
            }
            case 19: {
                this.stringBuilder.append("FIELD");
                break;
            }
            case 20: {
                this.stringBuilder.append("METHOD_RETURN");
                break;
            }
            case 21: {
                this.stringBuilder.append("METHOD_RECEIVER");
                break;
            }
            case 22: {
                this.stringBuilder.append("METHOD_FORMAL_PARAMETER ").append(typeReference.getFormalParameterIndex());
                break;
            }
            case 23: {
                this.stringBuilder.append("THROWS ").append(typeReference.getExceptionIndex());
                break;
            }
            case 64: {
                this.stringBuilder.append("LOCAL_VARIABLE");
                break;
            }
            case 65: {
                this.stringBuilder.append("RESOURCE_VARIABLE");
                break;
            }
            case 66: {
                this.stringBuilder.append("EXCEPTION_PARAMETER ").append(typeReference.getTryCatchBlockIndex());
                break;
            }
            case 67: {
                this.stringBuilder.append("INSTANCEOF");
                break;
            }
            case 68: {
                this.stringBuilder.append("NEW");
                break;
            }
            case 69: {
                this.stringBuilder.append("CONSTRUCTOR_REFERENCE");
                break;
            }
            case 70: {
                this.stringBuilder.append("METHOD_REFERENCE");
                break;
            }
            case 71: {
                this.stringBuilder.append("CAST ").append(typeReference.getTypeArgumentIndex());
                break;
            }
            case 72: {
                this.stringBuilder.append("CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT ").append(typeReference.getTypeArgumentIndex());
                break;
            }
            case 73: {
                this.stringBuilder.append("METHOD_INVOCATION_TYPE_ARGUMENT ").append(typeReference.getTypeArgumentIndex());
                break;
            }
            case 74: {
                this.stringBuilder.append("CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT ").append(typeReference.getTypeArgumentIndex());
                break;
            }
            case 75: {
                this.stringBuilder.append("METHOD_REFERENCE_TYPE_ARGUMENT ").append(typeReference.getTypeArgumentIndex());
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }

    private void appendFrameTypes(int numTypes, Object[] frameTypes) {
        for (int i2 = 0; i2 < numTypes; ++i2) {
            if (i2 > 0) {
                this.stringBuilder.append(' ');
            }
            if (frameTypes[i2] instanceof String) {
                String descriptor = (String)frameTypes[i2];
                if (descriptor.charAt(0) == '[') {
                    this.appendDescriptor(1, descriptor);
                    continue;
                }
                this.appendDescriptor(0, descriptor);
                continue;
            }
            if (frameTypes[i2] instanceof Integer) {
                this.stringBuilder.append(FRAME_TYPES.get((Integer)frameTypes[i2]));
                continue;
            }
            this.appendLabel((Label)frameTypes[i2]);
        }
    }

    private Textifier addNewTextifier(String endText) {
        Textifier textifier = this.createTextifier();
        this.text.add(textifier.getText());
        if (endText != null) {
            this.text.add(endText);
        }
        return textifier;
    }

    protected Textifier createTextifier() {
        return new Textifier(this.api);
    }
}

