/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree.analysis;

import java.util.List;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Interpreter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BasicInterpreter
extends Interpreter<BasicValue>
implements Opcodes {
    public static final Type NULL_TYPE = Type.getObjectType("null");

    public BasicInterpreter() {
        super(589824);
        if (this.getClass() != BasicInterpreter.class) {
            throw new IllegalStateException();
        }
    }

    protected BasicInterpreter(int api2) {
        super(api2);
    }

    @Override
    public BasicValue newValue(Type type) {
        if (type == null) {
            return BasicValue.UNINITIALIZED_VALUE;
        }
        switch (type.getSort()) {
            case 0: {
                return null;
            }
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: {
                return BasicValue.INT_VALUE;
            }
            case 6: {
                return BasicValue.FLOAT_VALUE;
            }
            case 7: {
                return BasicValue.LONG_VALUE;
            }
            case 8: {
                return BasicValue.DOUBLE_VALUE;
            }
            case 9: 
            case 10: {
                return BasicValue.REFERENCE_VALUE;
            }
        }
        throw new AssertionError();
    }

    @Override
    public BasicValue newOperation(AbstractInsnNode insn) throws AnalyzerException {
        switch (insn.getOpcode()) {
            case 1: {
                return this.newValue(NULL_TYPE);
            }
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: {
                return BasicValue.INT_VALUE;
            }
            case 9: 
            case 10: {
                return BasicValue.LONG_VALUE;
            }
            case 11: 
            case 12: 
            case 13: {
                return BasicValue.FLOAT_VALUE;
            }
            case 14: 
            case 15: {
                return BasicValue.DOUBLE_VALUE;
            }
            case 16: 
            case 17: {
                return BasicValue.INT_VALUE;
            }
            case 18: {
                Object value = ((LdcInsnNode)insn).cst;
                if (value instanceof Integer) {
                    return BasicValue.INT_VALUE;
                }
                if (value instanceof Float) {
                    return BasicValue.FLOAT_VALUE;
                }
                if (value instanceof Long) {
                    return BasicValue.LONG_VALUE;
                }
                if (value instanceof Double) {
                    return BasicValue.DOUBLE_VALUE;
                }
                if (value instanceof String) {
                    return this.newValue(Type.getObjectType("java/lang/String"));
                }
                if (value instanceof Type) {
                    int sort = ((Type)value).getSort();
                    if (sort == 10 || sort == 9) {
                        return this.newValue(Type.getObjectType("java/lang/Class"));
                    }
                    if (sort == 11) {
                        return this.newValue(Type.getObjectType("java/lang/invoke/MethodType"));
                    }
                    throw new AnalyzerException(insn, "Illegal LDC value " + value);
                }
                if (value instanceof Handle) {
                    return this.newValue(Type.getObjectType("java/lang/invoke/MethodHandle"));
                }
                if (value instanceof ConstantDynamic) {
                    return this.newValue(Type.getType(((ConstantDynamic)value).getDescriptor()));
                }
                throw new AnalyzerException(insn, "Illegal LDC value " + value);
            }
            case 168: {
                return BasicValue.RETURNADDRESS_VALUE;
            }
            case 178: {
                return this.newValue(Type.getType(((FieldInsnNode)insn).desc));
            }
            case 187: {
                return this.newValue(Type.getObjectType(((TypeInsnNode)insn).desc));
            }
        }
        throw new AssertionError();
    }

    @Override
    public BasicValue copyOperation(AbstractInsnNode insn, BasicValue value) throws AnalyzerException {
        return value;
    }

    @Override
    public BasicValue unaryOperation(AbstractInsnNode insn, BasicValue value) throws AnalyzerException {
        switch (insn.getOpcode()) {
            case 116: 
            case 132: 
            case 136: 
            case 139: 
            case 142: 
            case 145: 
            case 146: 
            case 147: {
                return BasicValue.INT_VALUE;
            }
            case 118: 
            case 134: 
            case 137: 
            case 144: {
                return BasicValue.FLOAT_VALUE;
            }
            case 117: 
            case 133: 
            case 140: 
            case 143: {
                return BasicValue.LONG_VALUE;
            }
            case 119: 
            case 135: 
            case 138: 
            case 141: {
                return BasicValue.DOUBLE_VALUE;
            }
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 170: 
            case 171: 
            case 172: 
            case 173: 
            case 174: 
            case 175: 
            case 176: 
            case 179: {
                return null;
            }
            case 180: {
                return this.newValue(Type.getType(((FieldInsnNode)insn).desc));
            }
            case 188: {
                switch (((IntInsnNode)insn).operand) {
                    case 4: {
                        return this.newValue(Type.getType("[Z"));
                    }
                    case 5: {
                        return this.newValue(Type.getType("[C"));
                    }
                    case 8: {
                        return this.newValue(Type.getType("[B"));
                    }
                    case 9: {
                        return this.newValue(Type.getType("[S"));
                    }
                    case 10: {
                        return this.newValue(Type.getType("[I"));
                    }
                    case 6: {
                        return this.newValue(Type.getType("[F"));
                    }
                    case 7: {
                        return this.newValue(Type.getType("[D"));
                    }
                    case 11: {
                        return this.newValue(Type.getType("[J"));
                    }
                }
                throw new AnalyzerException(insn, "Invalid array type");
            }
            case 189: {
                return this.newValue(Type.getType("[" + Type.getObjectType(((TypeInsnNode)insn).desc)));
            }
            case 190: {
                return BasicValue.INT_VALUE;
            }
            case 191: {
                return null;
            }
            case 192: {
                return this.newValue(Type.getObjectType(((TypeInsnNode)insn).desc));
            }
            case 193: {
                return BasicValue.INT_VALUE;
            }
            case 194: 
            case 195: 
            case 198: 
            case 199: {
                return null;
            }
        }
        throw new AssertionError();
    }

    @Override
    public BasicValue binaryOperation(AbstractInsnNode insn, BasicValue value1, BasicValue value2) throws AnalyzerException {
        switch (insn.getOpcode()) {
            case 46: 
            case 51: 
            case 52: 
            case 53: 
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
            case 130: {
                return BasicValue.INT_VALUE;
            }
            case 48: 
            case 98: 
            case 102: 
            case 106: 
            case 110: 
            case 114: {
                return BasicValue.FLOAT_VALUE;
            }
            case 47: 
            case 97: 
            case 101: 
            case 105: 
            case 109: 
            case 113: 
            case 121: 
            case 123: 
            case 125: 
            case 127: 
            case 129: 
            case 131: {
                return BasicValue.LONG_VALUE;
            }
            case 49: 
            case 99: 
            case 103: 
            case 107: 
            case 111: 
            case 115: {
                return BasicValue.DOUBLE_VALUE;
            }
            case 50: {
                return BasicValue.REFERENCE_VALUE;
            }
            case 148: 
            case 149: 
            case 150: 
            case 151: 
            case 152: {
                return BasicValue.INT_VALUE;
            }
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 166: 
            case 181: {
                return null;
            }
        }
        throw new AssertionError();
    }

    @Override
    public BasicValue ternaryOperation(AbstractInsnNode insn, BasicValue value1, BasicValue value2, BasicValue value3) throws AnalyzerException {
        return null;
    }

    @Override
    public BasicValue naryOperation(AbstractInsnNode insn, List<? extends BasicValue> values) throws AnalyzerException {
        int opcode = insn.getOpcode();
        if (opcode == 197) {
            return this.newValue(Type.getType(((MultiANewArrayInsnNode)insn).desc));
        }
        if (opcode == 186) {
            return this.newValue(Type.getReturnType(((InvokeDynamicInsnNode)insn).desc));
        }
        return this.newValue(Type.getReturnType(((MethodInsnNode)insn).desc));
    }

    @Override
    public void returnOperation(AbstractInsnNode insn, BasicValue value, BasicValue expected) throws AnalyzerException {
    }

    @Override
    public BasicValue merge(BasicValue value1, BasicValue value2) {
        if (!value1.equals(value2)) {
            return BasicValue.UNINITIALIZED_VALUE;
        }
        return value1;
    }
}

