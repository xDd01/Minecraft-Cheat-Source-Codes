/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree.analysis;

import java.util.List;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BasicVerifier
extends BasicInterpreter {
    public BasicVerifier() {
        super(589824);
        if (this.getClass() != BasicVerifier.class) {
            throw new IllegalStateException();
        }
    }

    protected BasicVerifier(int api2) {
        super(api2);
    }

    @Override
    public BasicValue copyOperation(AbstractInsnNode insn, BasicValue value) throws AnalyzerException {
        BasicValue expected;
        switch (insn.getOpcode()) {
            case 21: 
            case 54: {
                expected = BasicValue.INT_VALUE;
                break;
            }
            case 23: 
            case 56: {
                expected = BasicValue.FLOAT_VALUE;
                break;
            }
            case 22: 
            case 55: {
                expected = BasicValue.LONG_VALUE;
                break;
            }
            case 24: 
            case 57: {
                expected = BasicValue.DOUBLE_VALUE;
                break;
            }
            case 25: {
                if (!value.isReference()) {
                    throw new AnalyzerException(insn, null, "an object reference", value);
                }
                return value;
            }
            case 58: {
                if (!value.isReference() && !BasicValue.RETURNADDRESS_VALUE.equals(value)) {
                    throw new AnalyzerException(insn, null, "an object reference or a return address", value);
                }
                return value;
            }
            default: {
                return value;
            }
        }
        if (!((Object)expected).equals(value)) {
            throw new AnalyzerException(insn, null, expected, value);
        }
        return value;
    }

    @Override
    public BasicValue unaryOperation(AbstractInsnNode insn, BasicValue value) throws AnalyzerException {
        BasicValue expected;
        switch (insn.getOpcode()) {
            case 116: 
            case 132: 
            case 133: 
            case 134: 
            case 135: 
            case 145: 
            case 146: 
            case 147: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 170: 
            case 171: 
            case 172: 
            case 188: 
            case 189: {
                expected = BasicValue.INT_VALUE;
                break;
            }
            case 118: 
            case 139: 
            case 140: 
            case 141: 
            case 174: {
                expected = BasicValue.FLOAT_VALUE;
                break;
            }
            case 117: 
            case 136: 
            case 137: 
            case 138: 
            case 173: {
                expected = BasicValue.LONG_VALUE;
                break;
            }
            case 119: 
            case 142: 
            case 143: 
            case 144: 
            case 175: {
                expected = BasicValue.DOUBLE_VALUE;
                break;
            }
            case 180: {
                expected = this.newValue(Type.getObjectType(((FieldInsnNode)insn).owner));
                break;
            }
            case 190: {
                if (!this.isArrayValue(value)) {
                    throw new AnalyzerException(insn, null, "an array reference", value);
                }
                return super.unaryOperation(insn, value);
            }
            case 176: 
            case 191: 
            case 192: 
            case 193: 
            case 194: 
            case 195: 
            case 198: 
            case 199: {
                if (!value.isReference()) {
                    throw new AnalyzerException(insn, null, "an object reference", value);
                }
                return super.unaryOperation(insn, value);
            }
            case 179: {
                expected = this.newValue(Type.getType(((FieldInsnNode)insn).desc));
                break;
            }
            default: {
                throw new AssertionError();
            }
        }
        if (!this.isSubTypeOf(value, expected)) {
            throw new AnalyzerException(insn, null, expected, value);
        }
        return super.unaryOperation(insn, value);
    }

    @Override
    public BasicValue binaryOperation(AbstractInsnNode insn, BasicValue value1, BasicValue value2) throws AnalyzerException {
        BasicValue expected2;
        BasicValue expected1;
        switch (insn.getOpcode()) {
            case 46: {
                expected1 = this.newValue(Type.getType("[I"));
                expected2 = BasicValue.INT_VALUE;
                break;
            }
            case 51: {
                expected1 = this.isSubTypeOf(value1, this.newValue(Type.getType("[Z"))) ? this.newValue(Type.getType("[Z")) : this.newValue(Type.getType("[B"));
                expected2 = BasicValue.INT_VALUE;
                break;
            }
            case 52: {
                expected1 = this.newValue(Type.getType("[C"));
                expected2 = BasicValue.INT_VALUE;
                break;
            }
            case 53: {
                expected1 = this.newValue(Type.getType("[S"));
                expected2 = BasicValue.INT_VALUE;
                break;
            }
            case 47: {
                expected1 = this.newValue(Type.getType("[J"));
                expected2 = BasicValue.INT_VALUE;
                break;
            }
            case 48: {
                expected1 = this.newValue(Type.getType("[F"));
                expected2 = BasicValue.INT_VALUE;
                break;
            }
            case 49: {
                expected1 = this.newValue(Type.getType("[D"));
                expected2 = BasicValue.INT_VALUE;
                break;
            }
            case 50: {
                expected1 = this.newValue(Type.getType("[Ljava/lang/Object;"));
                expected2 = BasicValue.INT_VALUE;
                break;
            }
            case 96: 
            case 100: 
            case 104: 
            case 108: 
            case 112: 
            case 120: 
            case 122: 
            case 124: 
            case 126: 
            case 128: 
            case 130: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: {
                expected1 = BasicValue.INT_VALUE;
                expected2 = BasicValue.INT_VALUE;
                break;
            }
            case 98: 
            case 102: 
            case 106: 
            case 110: 
            case 114: 
            case 149: 
            case 150: {
                expected1 = BasicValue.FLOAT_VALUE;
                expected2 = BasicValue.FLOAT_VALUE;
                break;
            }
            case 97: 
            case 101: 
            case 105: 
            case 109: 
            case 113: 
            case 127: 
            case 129: 
            case 131: 
            case 148: {
                expected1 = BasicValue.LONG_VALUE;
                expected2 = BasicValue.LONG_VALUE;
                break;
            }
            case 121: 
            case 123: 
            case 125: {
                expected1 = BasicValue.LONG_VALUE;
                expected2 = BasicValue.INT_VALUE;
                break;
            }
            case 99: 
            case 103: 
            case 107: 
            case 111: 
            case 115: 
            case 151: 
            case 152: {
                expected1 = BasicValue.DOUBLE_VALUE;
                expected2 = BasicValue.DOUBLE_VALUE;
                break;
            }
            case 165: 
            case 166: {
                expected1 = BasicValue.REFERENCE_VALUE;
                expected2 = BasicValue.REFERENCE_VALUE;
                break;
            }
            case 181: {
                FieldInsnNode fieldInsn = (FieldInsnNode)insn;
                expected1 = this.newValue(Type.getObjectType(fieldInsn.owner));
                expected2 = this.newValue(Type.getType(fieldInsn.desc));
                break;
            }
            default: {
                throw new AssertionError();
            }
        }
        if (!this.isSubTypeOf(value1, expected1)) {
            throw new AnalyzerException(insn, "First argument", expected1, value1);
        }
        if (!this.isSubTypeOf(value2, expected2)) {
            throw new AnalyzerException(insn, "Second argument", expected2, value2);
        }
        if (insn.getOpcode() == 50) {
            return this.getElementValue(value1);
        }
        return super.binaryOperation(insn, value1, value2);
    }

    @Override
    public BasicValue ternaryOperation(AbstractInsnNode insn, BasicValue value1, BasicValue value2, BasicValue value3) throws AnalyzerException {
        BasicValue expected3;
        BasicValue expected1;
        switch (insn.getOpcode()) {
            case 79: {
                expected1 = this.newValue(Type.getType("[I"));
                expected3 = BasicValue.INT_VALUE;
                break;
            }
            case 84: {
                expected1 = this.isSubTypeOf(value1, this.newValue(Type.getType("[Z"))) ? this.newValue(Type.getType("[Z")) : this.newValue(Type.getType("[B"));
                expected3 = BasicValue.INT_VALUE;
                break;
            }
            case 85: {
                expected1 = this.newValue(Type.getType("[C"));
                expected3 = BasicValue.INT_VALUE;
                break;
            }
            case 86: {
                expected1 = this.newValue(Type.getType("[S"));
                expected3 = BasicValue.INT_VALUE;
                break;
            }
            case 80: {
                expected1 = this.newValue(Type.getType("[J"));
                expected3 = BasicValue.LONG_VALUE;
                break;
            }
            case 81: {
                expected1 = this.newValue(Type.getType("[F"));
                expected3 = BasicValue.FLOAT_VALUE;
                break;
            }
            case 82: {
                expected1 = this.newValue(Type.getType("[D"));
                expected3 = BasicValue.DOUBLE_VALUE;
                break;
            }
            case 83: {
                expected1 = value1;
                expected3 = BasicValue.REFERENCE_VALUE;
                break;
            }
            default: {
                throw new AssertionError();
            }
        }
        if (!this.isSubTypeOf(value1, expected1)) {
            throw new AnalyzerException(insn, "First argument", "a " + expected1 + " array reference", value1);
        }
        if (!BasicValue.INT_VALUE.equals(value2)) {
            throw new AnalyzerException(insn, "Second argument", BasicValue.INT_VALUE, value2);
        }
        if (!this.isSubTypeOf(value3, expected3)) {
            throw new AnalyzerException(insn, "Third argument", expected3, value3);
        }
        return null;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public BasicValue naryOperation(AbstractInsnNode insn, List<? extends BasicValue> values) throws AnalyzerException {
        int opcode = insn.getOpcode();
        if (opcode == 197) {
            for (BasicValue basicValue : values) {
                if (BasicValue.INT_VALUE.equals(basicValue)) continue;
                throw new AnalyzerException(insn, null, BasicValue.INT_VALUE, basicValue);
            }
        } else {
            int i2 = 0;
            boolean bl2 = false;
            if (opcode != 184 && opcode != 186) {
                Type owner = Type.getObjectType(((MethodInsnNode)insn).owner);
                if (!this.isSubTypeOf(values.get(i2++), this.newValue(owner))) {
                    throw new AnalyzerException(insn, "Method owner", this.newValue(owner), values.get(0));
                }
            }
            String methodDescriptor = opcode == 186 ? ((InvokeDynamicInsnNode)insn).desc : ((MethodInsnNode)insn).desc;
            Type[] args = Type.getArgumentTypes(methodDescriptor);
            while (i2 < values.size()) {
                BasicValue actual;
                void var5_8;
                BasicValue expected = this.newValue(args[++var5_8]);
                if (this.isSubTypeOf(actual = values.get(i2++), expected)) continue;
                throw new AnalyzerException(insn, "Argument " + (int)var5_8, expected, actual);
            }
        }
        return super.naryOperation(insn, (List)values);
    }

    @Override
    public void returnOperation(AbstractInsnNode insn, BasicValue value, BasicValue expected) throws AnalyzerException {
        if (!this.isSubTypeOf(value, expected)) {
            throw new AnalyzerException(insn, "Incompatible return type", expected, value);
        }
    }

    protected boolean isArrayValue(BasicValue value) {
        return value.isReference();
    }

    protected BasicValue getElementValue(BasicValue objectArrayValue) throws AnalyzerException {
        return BasicValue.REFERENCE_VALUE;
    }

    protected boolean isSubTypeOf(BasicValue value, BasicValue expected) {
        return value.equals(expected);
    }
}

