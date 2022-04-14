/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class InstructionAdapter
extends MethodVisitor {
    public static final Type OBJECT_TYPE = Type.getType("Ljava/lang/Object;");

    public InstructionAdapter(MethodVisitor methodVisitor) {
        this(589824, methodVisitor);
        if (this.getClass() != InstructionAdapter.class) {
            throw new IllegalStateException();
        }
    }

    protected InstructionAdapter(int api2, MethodVisitor methodVisitor) {
        super(api2, methodVisitor);
    }

    public void visitInsn(int opcode) {
        switch (opcode) {
            case 0: {
                this.nop();
                break;
            }
            case 1: {
                this.aconst(null);
                break;
            }
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: {
                this.iconst(opcode - 3);
                break;
            }
            case 9: 
            case 10: {
                this.lconst(opcode - 9);
                break;
            }
            case 11: 
            case 12: 
            case 13: {
                this.fconst(opcode - 11);
                break;
            }
            case 14: 
            case 15: {
                this.dconst(opcode - 14);
                break;
            }
            case 46: {
                this.aload(Type.INT_TYPE);
                break;
            }
            case 47: {
                this.aload(Type.LONG_TYPE);
                break;
            }
            case 48: {
                this.aload(Type.FLOAT_TYPE);
                break;
            }
            case 49: {
                this.aload(Type.DOUBLE_TYPE);
                break;
            }
            case 50: {
                this.aload(OBJECT_TYPE);
                break;
            }
            case 51: {
                this.aload(Type.BYTE_TYPE);
                break;
            }
            case 52: {
                this.aload(Type.CHAR_TYPE);
                break;
            }
            case 53: {
                this.aload(Type.SHORT_TYPE);
                break;
            }
            case 79: {
                this.astore(Type.INT_TYPE);
                break;
            }
            case 80: {
                this.astore(Type.LONG_TYPE);
                break;
            }
            case 81: {
                this.astore(Type.FLOAT_TYPE);
                break;
            }
            case 82: {
                this.astore(Type.DOUBLE_TYPE);
                break;
            }
            case 83: {
                this.astore(OBJECT_TYPE);
                break;
            }
            case 84: {
                this.astore(Type.BYTE_TYPE);
                break;
            }
            case 85: {
                this.astore(Type.CHAR_TYPE);
                break;
            }
            case 86: {
                this.astore(Type.SHORT_TYPE);
                break;
            }
            case 87: {
                this.pop();
                break;
            }
            case 88: {
                this.pop2();
                break;
            }
            case 89: {
                this.dup();
                break;
            }
            case 90: {
                this.dupX1();
                break;
            }
            case 91: {
                this.dupX2();
                break;
            }
            case 92: {
                this.dup2();
                break;
            }
            case 93: {
                this.dup2X1();
                break;
            }
            case 94: {
                this.dup2X2();
                break;
            }
            case 95: {
                this.swap();
                break;
            }
            case 96: {
                this.add(Type.INT_TYPE);
                break;
            }
            case 97: {
                this.add(Type.LONG_TYPE);
                break;
            }
            case 98: {
                this.add(Type.FLOAT_TYPE);
                break;
            }
            case 99: {
                this.add(Type.DOUBLE_TYPE);
                break;
            }
            case 100: {
                this.sub(Type.INT_TYPE);
                break;
            }
            case 101: {
                this.sub(Type.LONG_TYPE);
                break;
            }
            case 102: {
                this.sub(Type.FLOAT_TYPE);
                break;
            }
            case 103: {
                this.sub(Type.DOUBLE_TYPE);
                break;
            }
            case 104: {
                this.mul(Type.INT_TYPE);
                break;
            }
            case 105: {
                this.mul(Type.LONG_TYPE);
                break;
            }
            case 106: {
                this.mul(Type.FLOAT_TYPE);
                break;
            }
            case 107: {
                this.mul(Type.DOUBLE_TYPE);
                break;
            }
            case 108: {
                this.div(Type.INT_TYPE);
                break;
            }
            case 109: {
                this.div(Type.LONG_TYPE);
                break;
            }
            case 110: {
                this.div(Type.FLOAT_TYPE);
                break;
            }
            case 111: {
                this.div(Type.DOUBLE_TYPE);
                break;
            }
            case 112: {
                this.rem(Type.INT_TYPE);
                break;
            }
            case 113: {
                this.rem(Type.LONG_TYPE);
                break;
            }
            case 114: {
                this.rem(Type.FLOAT_TYPE);
                break;
            }
            case 115: {
                this.rem(Type.DOUBLE_TYPE);
                break;
            }
            case 116: {
                this.neg(Type.INT_TYPE);
                break;
            }
            case 117: {
                this.neg(Type.LONG_TYPE);
                break;
            }
            case 118: {
                this.neg(Type.FLOAT_TYPE);
                break;
            }
            case 119: {
                this.neg(Type.DOUBLE_TYPE);
                break;
            }
            case 120: {
                this.shl(Type.INT_TYPE);
                break;
            }
            case 121: {
                this.shl(Type.LONG_TYPE);
                break;
            }
            case 122: {
                this.shr(Type.INT_TYPE);
                break;
            }
            case 123: {
                this.shr(Type.LONG_TYPE);
                break;
            }
            case 124: {
                this.ushr(Type.INT_TYPE);
                break;
            }
            case 125: {
                this.ushr(Type.LONG_TYPE);
                break;
            }
            case 126: {
                this.and(Type.INT_TYPE);
                break;
            }
            case 127: {
                this.and(Type.LONG_TYPE);
                break;
            }
            case 128: {
                this.or(Type.INT_TYPE);
                break;
            }
            case 129: {
                this.or(Type.LONG_TYPE);
                break;
            }
            case 130: {
                this.xor(Type.INT_TYPE);
                break;
            }
            case 131: {
                this.xor(Type.LONG_TYPE);
                break;
            }
            case 133: {
                this.cast(Type.INT_TYPE, Type.LONG_TYPE);
                break;
            }
            case 134: {
                this.cast(Type.INT_TYPE, Type.FLOAT_TYPE);
                break;
            }
            case 135: {
                this.cast(Type.INT_TYPE, Type.DOUBLE_TYPE);
                break;
            }
            case 136: {
                this.cast(Type.LONG_TYPE, Type.INT_TYPE);
                break;
            }
            case 137: {
                this.cast(Type.LONG_TYPE, Type.FLOAT_TYPE);
                break;
            }
            case 138: {
                this.cast(Type.LONG_TYPE, Type.DOUBLE_TYPE);
                break;
            }
            case 139: {
                this.cast(Type.FLOAT_TYPE, Type.INT_TYPE);
                break;
            }
            case 140: {
                this.cast(Type.FLOAT_TYPE, Type.LONG_TYPE);
                break;
            }
            case 141: {
                this.cast(Type.FLOAT_TYPE, Type.DOUBLE_TYPE);
                break;
            }
            case 142: {
                this.cast(Type.DOUBLE_TYPE, Type.INT_TYPE);
                break;
            }
            case 143: {
                this.cast(Type.DOUBLE_TYPE, Type.LONG_TYPE);
                break;
            }
            case 144: {
                this.cast(Type.DOUBLE_TYPE, Type.FLOAT_TYPE);
                break;
            }
            case 145: {
                this.cast(Type.INT_TYPE, Type.BYTE_TYPE);
                break;
            }
            case 146: {
                this.cast(Type.INT_TYPE, Type.CHAR_TYPE);
                break;
            }
            case 147: {
                this.cast(Type.INT_TYPE, Type.SHORT_TYPE);
                break;
            }
            case 148: {
                this.lcmp();
                break;
            }
            case 149: {
                this.cmpl(Type.FLOAT_TYPE);
                break;
            }
            case 150: {
                this.cmpg(Type.FLOAT_TYPE);
                break;
            }
            case 151: {
                this.cmpl(Type.DOUBLE_TYPE);
                break;
            }
            case 152: {
                this.cmpg(Type.DOUBLE_TYPE);
                break;
            }
            case 172: {
                this.areturn(Type.INT_TYPE);
                break;
            }
            case 173: {
                this.areturn(Type.LONG_TYPE);
                break;
            }
            case 174: {
                this.areturn(Type.FLOAT_TYPE);
                break;
            }
            case 175: {
                this.areturn(Type.DOUBLE_TYPE);
                break;
            }
            case 176: {
                this.areturn(OBJECT_TYPE);
                break;
            }
            case 177: {
                this.areturn(Type.VOID_TYPE);
                break;
            }
            case 190: {
                this.arraylength();
                break;
            }
            case 191: {
                this.athrow();
                break;
            }
            case 194: {
                this.monitorenter();
                break;
            }
            case 195: {
                this.monitorexit();
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }

    public void visitIntInsn(int opcode, int operand) {
        block0 : switch (opcode) {
            case 16: {
                this.iconst(operand);
                break;
            }
            case 17: {
                this.iconst(operand);
                break;
            }
            case 188: {
                switch (operand) {
                    case 4: {
                        this.newarray(Type.BOOLEAN_TYPE);
                        break block0;
                    }
                    case 5: {
                        this.newarray(Type.CHAR_TYPE);
                        break block0;
                    }
                    case 8: {
                        this.newarray(Type.BYTE_TYPE);
                        break block0;
                    }
                    case 9: {
                        this.newarray(Type.SHORT_TYPE);
                        break block0;
                    }
                    case 10: {
                        this.newarray(Type.INT_TYPE);
                        break block0;
                    }
                    case 6: {
                        this.newarray(Type.FLOAT_TYPE);
                        break block0;
                    }
                    case 11: {
                        this.newarray(Type.LONG_TYPE);
                        break block0;
                    }
                    case 7: {
                        this.newarray(Type.DOUBLE_TYPE);
                        break block0;
                    }
                }
                throw new IllegalArgumentException();
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }

    public void visitVarInsn(int opcode, int var) {
        switch (opcode) {
            case 21: {
                this.load(var, Type.INT_TYPE);
                break;
            }
            case 22: {
                this.load(var, Type.LONG_TYPE);
                break;
            }
            case 23: {
                this.load(var, Type.FLOAT_TYPE);
                break;
            }
            case 24: {
                this.load(var, Type.DOUBLE_TYPE);
                break;
            }
            case 25: {
                this.load(var, OBJECT_TYPE);
                break;
            }
            case 54: {
                this.store(var, Type.INT_TYPE);
                break;
            }
            case 55: {
                this.store(var, Type.LONG_TYPE);
                break;
            }
            case 56: {
                this.store(var, Type.FLOAT_TYPE);
                break;
            }
            case 57: {
                this.store(var, Type.DOUBLE_TYPE);
                break;
            }
            case 58: {
                this.store(var, OBJECT_TYPE);
                break;
            }
            case 169: {
                this.ret(var);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }

    public void visitTypeInsn(int opcode, String type) {
        Type objectType = Type.getObjectType(type);
        switch (opcode) {
            case 187: {
                this.anew(objectType);
                break;
            }
            case 189: {
                this.newarray(objectType);
                break;
            }
            case 192: {
                this.checkcast(objectType);
                break;
            }
            case 193: {
                this.instanceOf(objectType);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }

    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        switch (opcode) {
            case 178: {
                this.getstatic(owner, name, descriptor);
                break;
            }
            case 179: {
                this.putstatic(owner, name, descriptor);
                break;
            }
            case 180: {
                this.getfield(owner, name, descriptor);
                break;
            }
            case 181: {
                this.putfield(owner, name, descriptor);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }

    public void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
        if (this.api < 327680 && (opcodeAndSource & 0x100) == 0) {
            super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
            return;
        }
        int opcode = opcodeAndSource & 0xFFFFFEFF;
        switch (opcode) {
            case 183: {
                this.invokespecial(owner, name, descriptor, isInterface);
                break;
            }
            case 182: {
                this.invokevirtual(owner, name, descriptor, isInterface);
                break;
            }
            case 184: {
                this.invokestatic(owner, name, descriptor, isInterface);
                break;
            }
            case 185: {
                this.invokeinterface(owner, name, descriptor);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }

    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object ... bootstrapMethodArguments) {
        this.invokedynamic(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
    }

    public void visitJumpInsn(int opcode, Label label) {
        switch (opcode) {
            case 153: {
                this.ifeq(label);
                break;
            }
            case 154: {
                this.ifne(label);
                break;
            }
            case 155: {
                this.iflt(label);
                break;
            }
            case 156: {
                this.ifge(label);
                break;
            }
            case 157: {
                this.ifgt(label);
                break;
            }
            case 158: {
                this.ifle(label);
                break;
            }
            case 159: {
                this.ificmpeq(label);
                break;
            }
            case 160: {
                this.ificmpne(label);
                break;
            }
            case 161: {
                this.ificmplt(label);
                break;
            }
            case 162: {
                this.ificmpge(label);
                break;
            }
            case 163: {
                this.ificmpgt(label);
                break;
            }
            case 164: {
                this.ificmple(label);
                break;
            }
            case 165: {
                this.ifacmpeq(label);
                break;
            }
            case 166: {
                this.ifacmpne(label);
                break;
            }
            case 167: {
                this.goTo(label);
                break;
            }
            case 168: {
                this.jsr(label);
                break;
            }
            case 198: {
                this.ifnull(label);
                break;
            }
            case 199: {
                this.ifnonnull(label);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }

    public void visitLabel(Label label) {
        this.mark(label);
    }

    public void visitLdcInsn(Object value) {
        if (this.api < 327680 && (value instanceof Handle || value instanceof Type && ((Type)value).getSort() == 11)) {
            throw new UnsupportedOperationException("This feature requires ASM5");
        }
        if (this.api < 458752 && value instanceof ConstantDynamic) {
            throw new UnsupportedOperationException("This feature requires ASM7");
        }
        if (value instanceof Integer) {
            this.iconst((Integer)value);
        } else if (value instanceof Byte) {
            this.iconst(((Byte)value).intValue());
        } else if (value instanceof Character) {
            this.iconst(((Character)value).charValue());
        } else if (value instanceof Short) {
            this.iconst(((Short)value).intValue());
        } else if (value instanceof Boolean) {
            this.iconst((Boolean)value != false ? 1 : 0);
        } else if (value instanceof Float) {
            this.fconst(((Float)value).floatValue());
        } else if (value instanceof Long) {
            this.lconst((Long)value);
        } else if (value instanceof Double) {
            this.dconst((Double)value);
        } else if (value instanceof String) {
            this.aconst(value);
        } else if (value instanceof Type) {
            this.tconst((Type)value);
        } else if (value instanceof Handle) {
            this.hconst((Handle)value);
        } else if (value instanceof ConstantDynamic) {
            this.cconst((ConstantDynamic)value);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void visitIincInsn(int var, int increment) {
        this.iinc(var, increment);
    }

    public void visitTableSwitchInsn(int min, int max, Label dflt, Label ... labels) {
        this.tableswitch(min, max, dflt, labels);
    }

    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        this.lookupswitch(dflt, keys, labels);
    }

    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        this.multianewarray(descriptor, numDimensions);
    }

    public void nop() {
        this.mv.visitInsn(0);
    }

    public void aconst(Object value) {
        if (value == null) {
            this.mv.visitInsn(1);
        } else {
            this.mv.visitLdcInsn(value);
        }
    }

    public void iconst(int intValue) {
        if (intValue >= -1 && intValue <= 5) {
            this.mv.visitInsn(3 + intValue);
        } else if (intValue >= -128 && intValue <= 127) {
            this.mv.visitIntInsn(16, intValue);
        } else if (intValue >= Short.MIN_VALUE && intValue <= Short.MAX_VALUE) {
            this.mv.visitIntInsn(17, intValue);
        } else {
            this.mv.visitLdcInsn(intValue);
        }
    }

    public void lconst(long longValue) {
        if (longValue == 0L || longValue == 1L) {
            this.mv.visitInsn(9 + (int)longValue);
        } else {
            this.mv.visitLdcInsn(longValue);
        }
    }

    public void fconst(float floatValue) {
        int bits = Float.floatToIntBits(floatValue);
        if ((long)bits == 0L || bits == 1065353216 || bits == 0x40000000) {
            this.mv.visitInsn(11 + (int)floatValue);
        } else {
            this.mv.visitLdcInsn(Float.valueOf(floatValue));
        }
    }

    public void dconst(double doubleValue) {
        long bits = Double.doubleToLongBits(doubleValue);
        if (bits == 0L || bits == 0x3FF0000000000000L) {
            this.mv.visitInsn(14 + (int)doubleValue);
        } else {
            this.mv.visitLdcInsn(doubleValue);
        }
    }

    public void tconst(Type type) {
        this.mv.visitLdcInsn(type);
    }

    public void hconst(Handle handle) {
        this.mv.visitLdcInsn(handle);
    }

    public void cconst(ConstantDynamic constantDynamic) {
        this.mv.visitLdcInsn(constantDynamic);
    }

    public void load(int var, Type type) {
        this.mv.visitVarInsn(type.getOpcode(21), var);
    }

    public void aload(Type type) {
        this.mv.visitInsn(type.getOpcode(46));
    }

    public void store(int var, Type type) {
        this.mv.visitVarInsn(type.getOpcode(54), var);
    }

    public void astore(Type type) {
        this.mv.visitInsn(type.getOpcode(79));
    }

    public void pop() {
        this.mv.visitInsn(87);
    }

    public void pop2() {
        this.mv.visitInsn(88);
    }

    public void dup() {
        this.mv.visitInsn(89);
    }

    public void dup2() {
        this.mv.visitInsn(92);
    }

    public void dupX1() {
        this.mv.visitInsn(90);
    }

    public void dupX2() {
        this.mv.visitInsn(91);
    }

    public void dup2X1() {
        this.mv.visitInsn(93);
    }

    public void dup2X2() {
        this.mv.visitInsn(94);
    }

    public void swap() {
        this.mv.visitInsn(95);
    }

    public void add(Type type) {
        this.mv.visitInsn(type.getOpcode(96));
    }

    public void sub(Type type) {
        this.mv.visitInsn(type.getOpcode(100));
    }

    public void mul(Type type) {
        this.mv.visitInsn(type.getOpcode(104));
    }

    public void div(Type type) {
        this.mv.visitInsn(type.getOpcode(108));
    }

    public void rem(Type type) {
        this.mv.visitInsn(type.getOpcode(112));
    }

    public void neg(Type type) {
        this.mv.visitInsn(type.getOpcode(116));
    }

    public void shl(Type type) {
        this.mv.visitInsn(type.getOpcode(120));
    }

    public void shr(Type type) {
        this.mv.visitInsn(type.getOpcode(122));
    }

    public void ushr(Type type) {
        this.mv.visitInsn(type.getOpcode(124));
    }

    public void and(Type type) {
        this.mv.visitInsn(type.getOpcode(126));
    }

    public void or(Type type) {
        this.mv.visitInsn(type.getOpcode(128));
    }

    public void xor(Type type) {
        this.mv.visitInsn(type.getOpcode(130));
    }

    public void iinc(int var, int increment) {
        this.mv.visitIincInsn(var, increment);
    }

    public void cast(Type from, Type to2) {
        InstructionAdapter.cast(this.mv, from, to2);
    }

    static void cast(MethodVisitor methodVisitor, Type from, Type to2) {
        if (from != to2) {
            if (from == Type.DOUBLE_TYPE) {
                if (to2 == Type.FLOAT_TYPE) {
                    methodVisitor.visitInsn(144);
                } else if (to2 == Type.LONG_TYPE) {
                    methodVisitor.visitInsn(143);
                } else {
                    methodVisitor.visitInsn(142);
                    InstructionAdapter.cast(methodVisitor, Type.INT_TYPE, to2);
                }
            } else if (from == Type.FLOAT_TYPE) {
                if (to2 == Type.DOUBLE_TYPE) {
                    methodVisitor.visitInsn(141);
                } else if (to2 == Type.LONG_TYPE) {
                    methodVisitor.visitInsn(140);
                } else {
                    methodVisitor.visitInsn(139);
                    InstructionAdapter.cast(methodVisitor, Type.INT_TYPE, to2);
                }
            } else if (from == Type.LONG_TYPE) {
                if (to2 == Type.DOUBLE_TYPE) {
                    methodVisitor.visitInsn(138);
                } else if (to2 == Type.FLOAT_TYPE) {
                    methodVisitor.visitInsn(137);
                } else {
                    methodVisitor.visitInsn(136);
                    InstructionAdapter.cast(methodVisitor, Type.INT_TYPE, to2);
                }
            } else if (to2 == Type.BYTE_TYPE) {
                methodVisitor.visitInsn(145);
            } else if (to2 == Type.CHAR_TYPE) {
                methodVisitor.visitInsn(146);
            } else if (to2 == Type.DOUBLE_TYPE) {
                methodVisitor.visitInsn(135);
            } else if (to2 == Type.FLOAT_TYPE) {
                methodVisitor.visitInsn(134);
            } else if (to2 == Type.LONG_TYPE) {
                methodVisitor.visitInsn(133);
            } else if (to2 == Type.SHORT_TYPE) {
                methodVisitor.visitInsn(147);
            }
        }
    }

    public void lcmp() {
        this.mv.visitInsn(148);
    }

    public void cmpl(Type type) {
        this.mv.visitInsn(type == Type.FLOAT_TYPE ? 149 : 151);
    }

    public void cmpg(Type type) {
        this.mv.visitInsn(type == Type.FLOAT_TYPE ? 150 : 152);
    }

    public void ifeq(Label label) {
        this.mv.visitJumpInsn(153, label);
    }

    public void ifne(Label label) {
        this.mv.visitJumpInsn(154, label);
    }

    public void iflt(Label label) {
        this.mv.visitJumpInsn(155, label);
    }

    public void ifge(Label label) {
        this.mv.visitJumpInsn(156, label);
    }

    public void ifgt(Label label) {
        this.mv.visitJumpInsn(157, label);
    }

    public void ifle(Label label) {
        this.mv.visitJumpInsn(158, label);
    }

    public void ificmpeq(Label label) {
        this.mv.visitJumpInsn(159, label);
    }

    public void ificmpne(Label label) {
        this.mv.visitJumpInsn(160, label);
    }

    public void ificmplt(Label label) {
        this.mv.visitJumpInsn(161, label);
    }

    public void ificmpge(Label label) {
        this.mv.visitJumpInsn(162, label);
    }

    public void ificmpgt(Label label) {
        this.mv.visitJumpInsn(163, label);
    }

    public void ificmple(Label label) {
        this.mv.visitJumpInsn(164, label);
    }

    public void ifacmpeq(Label label) {
        this.mv.visitJumpInsn(165, label);
    }

    public void ifacmpne(Label label) {
        this.mv.visitJumpInsn(166, label);
    }

    public void goTo(Label label) {
        this.mv.visitJumpInsn(167, label);
    }

    public void jsr(Label label) {
        this.mv.visitJumpInsn(168, label);
    }

    public void ret(int var) {
        this.mv.visitVarInsn(169, var);
    }

    public void tableswitch(int min, int max, Label dflt, Label ... labels) {
        this.mv.visitTableSwitchInsn(min, max, dflt, labels);
    }

    public void lookupswitch(Label dflt, int[] keys, Label[] labels) {
        this.mv.visitLookupSwitchInsn(dflt, keys, labels);
    }

    public void areturn(Type type) {
        this.mv.visitInsn(type.getOpcode(172));
    }

    public void getstatic(String owner, String name, String descriptor) {
        this.mv.visitFieldInsn(178, owner, name, descriptor);
    }

    public void putstatic(String owner, String name, String descriptor) {
        this.mv.visitFieldInsn(179, owner, name, descriptor);
    }

    public void getfield(String owner, String name, String descriptor) {
        this.mv.visitFieldInsn(180, owner, name, descriptor);
    }

    public void putfield(String owner, String name, String descriptor) {
        this.mv.visitFieldInsn(181, owner, name, descriptor);
    }

    @Deprecated
    public void invokevirtual(String owner, String name, String descriptor) {
        if (this.api >= 327680) {
            this.invokevirtual(owner, name, descriptor, false);
            return;
        }
        this.mv.visitMethodInsn(182, owner, name, descriptor);
    }

    public void invokevirtual(String owner, String name, String descriptor, boolean isInterface) {
        if (this.api < 327680) {
            if (isInterface) {
                throw new UnsupportedOperationException("INVOKEVIRTUAL on interfaces require ASM 5");
            }
            this.invokevirtual(owner, name, descriptor);
            return;
        }
        this.mv.visitMethodInsn(182, owner, name, descriptor, isInterface);
    }

    @Deprecated
    public void invokespecial(String owner, String name, String descriptor) {
        if (this.api >= 327680) {
            this.invokespecial(owner, name, descriptor, false);
            return;
        }
        this.mv.visitMethodInsn(183, owner, name, descriptor, false);
    }

    public void invokespecial(String owner, String name, String descriptor, boolean isInterface) {
        if (this.api < 327680) {
            if (isInterface) {
                throw new UnsupportedOperationException("INVOKESPECIAL on interfaces require ASM 5");
            }
            this.invokespecial(owner, name, descriptor);
            return;
        }
        this.mv.visitMethodInsn(183, owner, name, descriptor, isInterface);
    }

    @Deprecated
    public void invokestatic(String owner, String name, String descriptor) {
        if (this.api >= 327680) {
            this.invokestatic(owner, name, descriptor, false);
            return;
        }
        this.mv.visitMethodInsn(184, owner, name, descriptor, false);
    }

    public void invokestatic(String owner, String name, String descriptor, boolean isInterface) {
        if (this.api < 327680) {
            if (isInterface) {
                throw new UnsupportedOperationException("INVOKESTATIC on interfaces require ASM 5");
            }
            this.invokestatic(owner, name, descriptor);
            return;
        }
        this.mv.visitMethodInsn(184, owner, name, descriptor, isInterface);
    }

    public void invokeinterface(String owner, String name, String descriptor) {
        this.mv.visitMethodInsn(185, owner, name, descriptor, true);
    }

    public void invokedynamic(String name, String descriptor, Handle bootstrapMethodHandle, Object[] bootstrapMethodArguments) {
        this.mv.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
    }

    public void anew(Type type) {
        this.mv.visitTypeInsn(187, type.getInternalName());
    }

    public void newarray(Type type) {
        InstructionAdapter.newarray(this.mv, type);
    }

    static void newarray(MethodVisitor methodVisitor, Type type) {
        int arrayType;
        switch (type.getSort()) {
            case 1: {
                arrayType = 4;
                break;
            }
            case 2: {
                arrayType = 5;
                break;
            }
            case 3: {
                arrayType = 8;
                break;
            }
            case 4: {
                arrayType = 9;
                break;
            }
            case 5: {
                arrayType = 10;
                break;
            }
            case 6: {
                arrayType = 6;
                break;
            }
            case 7: {
                arrayType = 11;
                break;
            }
            case 8: {
                arrayType = 7;
                break;
            }
            default: {
                methodVisitor.visitTypeInsn(189, type.getInternalName());
                return;
            }
        }
        methodVisitor.visitIntInsn(188, arrayType);
    }

    public void arraylength() {
        this.mv.visitInsn(190);
    }

    public void athrow() {
        this.mv.visitInsn(191);
    }

    public void checkcast(Type type) {
        this.mv.visitTypeInsn(192, type.getInternalName());
    }

    public void instanceOf(Type type) {
        this.mv.visitTypeInsn(193, type.getInternalName());
    }

    public void monitorenter() {
        this.mv.visitInsn(194);
    }

    public void monitorexit() {
        this.mv.visitInsn(195);
    }

    public void multianewarray(String descriptor, int numDimensions) {
        this.mv.visitMultiANewArrayInsn(descriptor, numDimensions);
    }

    public void ifnull(Label label) {
        this.mv.visitJumpInsn(198, label);
    }

    public void ifnonnull(Label label) {
        this.mv.visitJumpInsn(199, label);
    }

    public void mark(Label label) {
        this.mv.visitLabel(label);
    }
}

