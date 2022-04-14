/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree.analysis;

import java.util.ArrayList;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.objectweb.asm.tree.analysis.Value;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Frame<V extends Value> {
    private static final int MAX_STACK_SIZE = 65536;
    private V returnValue;
    private V[] values;
    private int numLocals;
    private int numStack;
    private int maxStack;

    public Frame(int numLocals, int maxStack) {
        this.values = new Value[numLocals + (maxStack >= 0 ? maxStack : 4)];
        this.numLocals = numLocals;
        this.numStack = 0;
        this.maxStack = maxStack >= 0 ? maxStack : 65536;
    }

    public Frame(Frame<? extends V> frame) {
        this(frame.numLocals, frame.values.length - frame.numLocals);
        this.init(frame);
    }

    public Frame<V> init(Frame<? extends V> frame) {
        this.returnValue = frame.returnValue;
        if (this.values.length < frame.values.length) {
            this.values = (Value[])frame.values.clone();
        } else {
            System.arraycopy(frame.values, 0, this.values, 0, frame.values.length);
        }
        this.numLocals = frame.numLocals;
        this.numStack = frame.numStack;
        this.maxStack = frame.maxStack;
        return this;
    }

    public void initJumpTarget(int opcode, LabelNode target) {
    }

    public void setReturn(V v2) {
        this.returnValue = v2;
    }

    public int getLocals() {
        return this.numLocals;
    }

    public int getMaxStackSize() {
        return this.maxStack;
    }

    public V getLocal(int index) {
        if (index >= this.numLocals) {
            throw new IndexOutOfBoundsException("Trying to get an inexistant local variable " + index);
        }
        return this.values[index];
    }

    public void setLocal(int index, V value) {
        if (index >= this.numLocals) {
            throw new IndexOutOfBoundsException("Trying to set an inexistant local variable " + index);
        }
        this.values[index] = value;
    }

    public int getStackSize() {
        return this.numStack;
    }

    public V getStack(int index) {
        return this.values[this.numLocals + index];
    }

    public void setStack(int index, V value) {
        this.values[this.numLocals + index] = value;
    }

    public void clearStack() {
        this.numStack = 0;
    }

    public V pop() {
        if (this.numStack == 0) {
            throw new IndexOutOfBoundsException("Cannot pop operand off an empty stack.");
        }
        return this.values[this.numLocals + --this.numStack];
    }

    public void push(V value) {
        if (this.numLocals + this.numStack >= this.values.length) {
            if (this.numLocals + this.numStack >= this.maxStack) {
                throw new IndexOutOfBoundsException("Insufficient maximum stack size.");
            }
            V[] oldValues = this.values;
            this.values = new Value[2 * this.values.length];
            System.arraycopy(oldValues, 0, this.values, 0, oldValues.length);
        }
        this.values[this.numLocals + this.numStack++] = value;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void execute(AbstractInsnNode insn, Interpreter<V> interpreter) throws AnalyzerException {
        switch (insn.getOpcode()) {
            case 0: {
                return;
            }
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 14: 
            case 15: 
            case 16: 
            case 17: 
            case 18: {
                this.push(interpreter.newOperation(insn));
                return;
            }
            case 21: 
            case 22: 
            case 23: 
            case 24: 
            case 25: {
                this.push(interpreter.copyOperation(insn, this.getLocal(((VarInsnNode)insn).var)));
                return;
            }
            case 54: 
            case 55: 
            case 56: 
            case 57: 
            case 58: {
                V local;
                Object value1 = interpreter.copyOperation(insn, this.pop());
                int var = ((VarInsnNode)insn).var;
                this.setLocal(var, value1);
                if (value1.getSize() == 2) {
                    this.setLocal(var + 1, interpreter.newEmptyValue(var + 1));
                }
                if (var <= 0 || (local = this.getLocal(var - 1)) == null || local.getSize() != 2) return;
                this.setLocal(var - 1, interpreter.newEmptyValue(var - 1));
                return;
            }
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 86: {
                V value3 = this.pop();
                V value2 = this.pop();
                V value1 = this.pop();
                interpreter.ternaryOperation(insn, value1, value2, value3);
                return;
            }
            case 87: {
                if (this.pop().getSize() != 2) return;
                throw new AnalyzerException(insn, "Illegal use of POP");
            }
            case 88: {
                if (this.pop().getSize() != 1 || this.pop().getSize() == 1) return;
                throw new AnalyzerException(insn, "Illegal use of POP2");
            }
            case 89: {
                V value1 = this.pop();
                if (value1.getSize() != 1) {
                    throw new AnalyzerException(insn, "Illegal use of DUP");
                }
                this.push(value1);
                this.push(interpreter.copyOperation(insn, value1));
                return;
            }
            case 90: {
                V value1 = this.pop();
                V value2 = this.pop();
                if (value1.getSize() != 1 || value2.getSize() != 1) {
                    throw new AnalyzerException(insn, "Illegal use of DUP_X1");
                }
                this.push(interpreter.copyOperation(insn, value1));
                this.push(value2);
                this.push(value1);
                return;
            }
            case 91: {
                V value1 = this.pop();
                if (value1.getSize() == 1 && this.executeDupX2(insn, value1, interpreter)) return;
                throw new AnalyzerException(insn, "Illegal use of DUP_X2");
            }
            case 92: {
                V value1 = this.pop();
                if (value1.getSize() == 1) {
                    V value2 = this.pop();
                    if (value2.getSize() != 1) throw new AnalyzerException(insn, "Illegal use of DUP2");
                    this.push(value2);
                    this.push(value1);
                    this.push(interpreter.copyOperation(insn, value2));
                    this.push(interpreter.copyOperation(insn, value1));
                    return;
                }
                this.push(value1);
                this.push(interpreter.copyOperation(insn, value1));
                return;
            }
            case 93: {
                V value1 = this.pop();
                if (value1.getSize() == 1) {
                    V value3;
                    V value2 = this.pop();
                    if (value2.getSize() != 1 || (value3 = this.pop()).getSize() != 1) throw new AnalyzerException(insn, "Illegal use of DUP2_X1");
                    this.push(interpreter.copyOperation(insn, value2));
                    this.push(interpreter.copyOperation(insn, value1));
                    this.push(value3);
                    this.push(value2);
                    this.push(value1);
                    return;
                }
                V value2 = this.pop();
                if (value2.getSize() != 1) throw new AnalyzerException(insn, "Illegal use of DUP2_X1");
                this.push(interpreter.copyOperation(insn, value1));
                this.push(value2);
                this.push(value1);
                return;
            }
            case 94: {
                V value1 = this.pop();
                if (value1.getSize() == 1) {
                    V value2 = this.pop();
                    if (value2.getSize() != 1) throw new AnalyzerException(insn, "Illegal use of DUP2_X2");
                    V value3 = this.pop();
                    if (value3.getSize() == 1) {
                        V value4 = this.pop();
                        if (value4.getSize() != 1) throw new AnalyzerException(insn, "Illegal use of DUP2_X2");
                        this.push(interpreter.copyOperation(insn, value2));
                        this.push(interpreter.copyOperation(insn, value1));
                        this.push(value4);
                        this.push(value3);
                        this.push(value2);
                        this.push(value1);
                        return;
                    }
                    this.push(interpreter.copyOperation(insn, value2));
                    this.push(interpreter.copyOperation(insn, value1));
                    this.push(value3);
                    this.push(value2);
                    this.push(value1);
                    return;
                }
                if (this.executeDupX2(insn, value1, interpreter)) return;
                throw new AnalyzerException(insn, "Illegal use of DUP2_X2");
            }
            case 95: {
                V value2 = this.pop();
                V value1 = this.pop();
                if (value1.getSize() != 1 || value2.getSize() != 1) {
                    throw new AnalyzerException(insn, "Illegal use of SWAP");
                }
                this.push(interpreter.copyOperation(insn, value2));
                this.push(interpreter.copyOperation(insn, value1));
                return;
            }
            case 46: 
            case 47: 
            case 48: 
            case 49: 
            case 50: 
            case 51: 
            case 52: 
            case 53: 
            case 96: 
            case 97: 
            case 98: 
            case 99: 
            case 100: 
            case 101: 
            case 102: 
            case 103: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 113: 
            case 114: 
            case 115: 
            case 120: 
            case 121: 
            case 122: 
            case 123: 
            case 124: 
            case 125: 
            case 126: 
            case 127: 
            case 128: 
            case 129: 
            case 130: 
            case 131: 
            case 148: 
            case 149: 
            case 150: 
            case 151: 
            case 152: {
                V value2 = this.pop();
                V value1 = this.pop();
                this.push(interpreter.binaryOperation(insn, value1, value2));
                return;
            }
            case 116: 
            case 117: 
            case 118: 
            case 119: {
                this.push(interpreter.unaryOperation(insn, this.pop()));
                return;
            }
            case 132: {
                int var = ((IincInsnNode)insn).var;
                this.setLocal(var, interpreter.unaryOperation(insn, this.getLocal(var)));
                return;
            }
            case 133: 
            case 134: 
            case 135: 
            case 136: 
            case 137: 
            case 138: 
            case 139: 
            case 140: 
            case 141: 
            case 142: 
            case 143: 
            case 144: 
            case 145: 
            case 146: 
            case 147: {
                this.push(interpreter.unaryOperation(insn, this.pop()));
                return;
            }
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: {
                interpreter.unaryOperation(insn, this.pop());
                return;
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
                V value2 = this.pop();
                V value1 = this.pop();
                interpreter.binaryOperation(insn, value1, value2);
                return;
            }
            case 167: {
                return;
            }
            case 168: {
                this.push(interpreter.newOperation(insn));
                return;
            }
            case 169: {
                return;
            }
            case 170: 
            case 171: {
                interpreter.unaryOperation(insn, this.pop());
                return;
            }
            case 172: 
            case 173: 
            case 174: 
            case 175: 
            case 176: {
                V value1 = this.pop();
                interpreter.unaryOperation(insn, value1);
                interpreter.returnOperation(insn, value1, this.returnValue);
                return;
            }
            case 177: {
                if (this.returnValue == null) return;
                throw new AnalyzerException(insn, "Incompatible return type");
            }
            case 178: {
                this.push(interpreter.newOperation(insn));
                return;
            }
            case 179: {
                interpreter.unaryOperation(insn, this.pop());
                return;
            }
            case 180: {
                this.push(interpreter.unaryOperation(insn, this.pop()));
                return;
            }
            case 182: 
            case 183: 
            case 184: 
            case 185: {
                this.executeInvokeInsn(insn, ((MethodInsnNode)insn).desc, interpreter);
                return;
            }
            case 186: {
                this.executeInvokeInsn(insn, ((InvokeDynamicInsnNode)insn).desc, interpreter);
                return;
            }
            case 187: {
                this.push(interpreter.newOperation(insn));
                return;
            }
            case 188: 
            case 189: 
            case 190: {
                this.push(interpreter.unaryOperation(insn, this.pop()));
                return;
            }
            case 191: {
                interpreter.unaryOperation(insn, this.pop());
                return;
            }
            case 192: 
            case 193: {
                this.push(interpreter.unaryOperation(insn, this.pop()));
                return;
            }
            case 194: 
            case 195: {
                interpreter.unaryOperation(insn, this.pop());
                return;
            }
            case 197: {
                ArrayList<V> valueList = new ArrayList<V>();
                for (int i2 = ((MultiANewArrayInsnNode)insn).dims; i2 > 0; --i2) {
                    valueList.add(0, this.pop());
                }
                this.push(interpreter.naryOperation(insn, valueList));
                return;
            }
            case 198: 
            case 199: {
                interpreter.unaryOperation(insn, this.pop());
                return;
            }
            default: {
                throw new AnalyzerException(insn, "Illegal opcode " + insn.getOpcode());
            }
        }
    }

    private boolean executeDupX2(AbstractInsnNode insn, V value1, Interpreter<V> interpreter) throws AnalyzerException {
        V value2 = this.pop();
        if (value2.getSize() == 1) {
            V value3 = this.pop();
            if (value3.getSize() == 1) {
                this.push(interpreter.copyOperation(insn, value1));
                this.push(value3);
                this.push(value2);
                this.push(value1);
                return true;
            }
        } else {
            this.push(interpreter.copyOperation(insn, value1));
            this.push(value2);
            this.push(value1);
            return true;
        }
        return false;
    }

    private void executeInvokeInsn(AbstractInsnNode insn, String methodDescriptor, Interpreter<V> interpreter) throws AnalyzerException {
        ArrayList<V> valueList = new ArrayList<V>();
        for (int i2 = Type.getArgumentTypes(methodDescriptor).length; i2 > 0; --i2) {
            valueList.add(0, this.pop());
        }
        if (insn.getOpcode() != 184 && insn.getOpcode() != 186) {
            valueList.add(0, this.pop());
        }
        if (Type.getReturnType(methodDescriptor) == Type.VOID_TYPE) {
            interpreter.naryOperation(insn, valueList);
        } else {
            this.push(interpreter.naryOperation(insn, valueList));
        }
    }

    public boolean merge(Frame<? extends V> frame, Interpreter<V> interpreter) throws AnalyzerException {
        if (this.numStack != frame.numStack) {
            throw new AnalyzerException(null, "Incompatible stack heights");
        }
        boolean changed = false;
        for (int i2 = 0; i2 < this.numLocals + this.numStack; ++i2) {
            V v2 = interpreter.merge(this.values[i2], frame.values[i2]);
            if (v2.equals(this.values[i2])) continue;
            this.values[i2] = v2;
            changed = true;
        }
        return changed;
    }

    public boolean merge(Frame<? extends V> frame, boolean[] localsUsed) {
        boolean changed = false;
        for (int i2 = 0; i2 < this.numLocals; ++i2) {
            if (localsUsed[i2] || this.values[i2].equals(frame.values[i2])) continue;
            this.values[i2] = frame.values[i2];
            changed = true;
        }
        return changed;
    }

    public String toString() {
        int i2;
        StringBuilder stringBuilder = new StringBuilder();
        for (i2 = 0; i2 < this.getLocals(); ++i2) {
            stringBuilder.append(this.getLocal(i2));
        }
        stringBuilder.append(' ');
        for (i2 = 0; i2 < this.getStackSize(); ++i2) {
            stringBuilder.append(this.getStack(i2).toString());
        }
        return stringBuilder.toString();
    }
}

