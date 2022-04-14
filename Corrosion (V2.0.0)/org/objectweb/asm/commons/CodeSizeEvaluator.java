/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class CodeSizeEvaluator
extends MethodVisitor
implements Opcodes {
    private int minSize;
    private int maxSize;

    public CodeSizeEvaluator(MethodVisitor methodVisitor) {
        this(589824, methodVisitor);
    }

    protected CodeSizeEvaluator(int api2, MethodVisitor methodVisitor) {
        super(api2, methodVisitor);
    }

    public int getMinSize() {
        return this.minSize;
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public void visitInsn(int opcode) {
        ++this.minSize;
        ++this.maxSize;
        super.visitInsn(opcode);
    }

    public void visitIntInsn(int opcode, int operand) {
        if (opcode == 17) {
            this.minSize += 3;
            this.maxSize += 3;
        } else {
            this.minSize += 2;
            this.maxSize += 2;
        }
        super.visitIntInsn(opcode, operand);
    }

    public void visitVarInsn(int opcode, int var) {
        if (var < 4 && opcode != 169) {
            ++this.minSize;
            ++this.maxSize;
        } else if (var >= 256) {
            this.minSize += 4;
            this.maxSize += 4;
        } else {
            this.minSize += 2;
            this.maxSize += 2;
        }
        super.visitVarInsn(opcode, var);
    }

    public void visitTypeInsn(int opcode, String type) {
        this.minSize += 3;
        this.maxSize += 3;
        super.visitTypeInsn(opcode, type);
    }

    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        this.minSize += 3;
        this.maxSize += 3;
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }

    public void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
        if (this.api < 327680 && (opcodeAndSource & 0x100) == 0) {
            super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
            return;
        }
        int opcode = opcodeAndSource & 0xFFFFFEFF;
        if (opcode == 185) {
            this.minSize += 5;
            this.maxSize += 5;
        } else {
            this.minSize += 3;
            this.maxSize += 3;
        }
        super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
    }

    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object ... bootstrapMethodArguments) {
        this.minSize += 5;
        this.maxSize += 5;
        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
    }

    public void visitJumpInsn(int opcode, Label label) {
        this.minSize += 3;
        this.maxSize = opcode == 167 || opcode == 168 ? (this.maxSize += 5) : (this.maxSize += 8);
        super.visitJumpInsn(opcode, label);
    }

    public void visitLdcInsn(Object value) {
        if (value instanceof Long || value instanceof Double || value instanceof ConstantDynamic && ((ConstantDynamic)value).getSize() == 2) {
            this.minSize += 3;
            this.maxSize += 3;
        } else {
            this.minSize += 2;
            this.maxSize += 3;
        }
        super.visitLdcInsn(value);
    }

    public void visitIincInsn(int var, int increment) {
        if (var > 255 || increment > 127 || increment < -128) {
            this.minSize += 6;
            this.maxSize += 6;
        } else {
            this.minSize += 3;
            this.maxSize += 3;
        }
        super.visitIincInsn(var, increment);
    }

    public void visitTableSwitchInsn(int min, int max, Label dflt, Label ... labels) {
        this.minSize += 13 + labels.length * 4;
        this.maxSize += 16 + labels.length * 4;
        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        this.minSize += 9 + keys.length * 8;
        this.maxSize += 12 + keys.length * 8;
        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        this.minSize += 4;
        this.maxSize += 4;
        super.visitMultiANewArrayInsn(descriptor, numDimensions);
    }
}

