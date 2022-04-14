/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

public abstract class AdviceAdapter
extends GeneratorAdapter
implements Opcodes {
    private static final Object UNINITIALIZED_THIS = new Object();
    private static final Object OTHER = new Object();
    private static final String INVALID_OPCODE = "Invalid opcode ";
    protected int methodAccess;
    protected String methodDesc;
    private final boolean isConstructor;
    private boolean superClassConstructorCalled;
    private List<Object> stackFrame;
    private Map<Label, List<Object>> forwardJumpStackFrames;

    protected AdviceAdapter(int api2, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api2, methodVisitor, access, name, descriptor);
        this.methodAccess = access;
        this.methodDesc = descriptor;
        this.isConstructor = "<init>".equals(name);
    }

    public void visitCode() {
        super.visitCode();
        if (this.isConstructor) {
            this.stackFrame = new ArrayList<Object>();
            this.forwardJumpStackFrames = new HashMap<Label, List<Object>>();
        } else {
            this.onMethodEnter();
        }
    }

    public void visitLabel(Label label) {
        List<Object> labelStackFrame;
        super.visitLabel(label);
        if (this.isConstructor && this.forwardJumpStackFrames != null && (labelStackFrame = this.forwardJumpStackFrames.get(label)) != null) {
            this.stackFrame = labelStackFrame;
            this.superClassConstructorCalled = false;
            this.forwardJumpStackFrames.remove(label);
        }
    }

    public void visitInsn(int opcode) {
        block23: {
            block22: {
                if (!this.isConstructor || this.superClassConstructorCalled) break block22;
                switch (opcode) {
                    case 172: 
                    case 173: 
                    case 174: 
                    case 175: 
                    case 176: {
                        throw new IllegalArgumentException("Invalid return in constructor");
                    }
                    case 177: {
                        this.onMethodExit(opcode);
                        this.endConstructorBasicBlockWithoutSuccessor();
                        break block23;
                    }
                    case 191: {
                        this.popValue();
                        this.onMethodExit(opcode);
                        this.endConstructorBasicBlockWithoutSuccessor();
                        break block23;
                    }
                    case 0: 
                    case 47: 
                    case 49: 
                    case 116: 
                    case 117: 
                    case 118: 
                    case 119: 
                    case 134: 
                    case 138: 
                    case 139: 
                    case 143: 
                    case 145: 
                    case 146: 
                    case 147: 
                    case 190: {
                        break block23;
                    }
                    case 1: 
                    case 2: 
                    case 3: 
                    case 4: 
                    case 5: 
                    case 6: 
                    case 7: 
                    case 8: 
                    case 11: 
                    case 12: 
                    case 13: 
                    case 133: 
                    case 135: 
                    case 140: 
                    case 141: {
                        this.pushValue(OTHER);
                        break block23;
                    }
                    case 9: 
                    case 10: 
                    case 14: 
                    case 15: {
                        this.pushValue(OTHER);
                        this.pushValue(OTHER);
                        break block23;
                    }
                    case 46: 
                    case 48: 
                    case 50: 
                    case 51: 
                    case 52: 
                    case 53: 
                    case 87: 
                    case 96: 
                    case 98: 
                    case 100: 
                    case 102: 
                    case 104: 
                    case 106: 
                    case 108: 
                    case 110: 
                    case 112: 
                    case 114: 
                    case 120: 
                    case 121: 
                    case 122: 
                    case 123: 
                    case 124: 
                    case 125: 
                    case 126: 
                    case 128: 
                    case 130: 
                    case 136: 
                    case 137: 
                    case 142: 
                    case 144: 
                    case 149: 
                    case 150: 
                    case 194: 
                    case 195: {
                        this.popValue();
                        break block23;
                    }
                    case 88: 
                    case 97: 
                    case 99: 
                    case 101: 
                    case 103: 
                    case 105: 
                    case 107: 
                    case 109: 
                    case 111: 
                    case 113: 
                    case 115: 
                    case 127: 
                    case 129: 
                    case 131: {
                        this.popValue();
                        this.popValue();
                        break block23;
                    }
                    case 79: 
                    case 81: 
                    case 83: 
                    case 84: 
                    case 85: 
                    case 86: 
                    case 148: 
                    case 151: 
                    case 152: {
                        this.popValue();
                        this.popValue();
                        this.popValue();
                        break block23;
                    }
                    case 80: 
                    case 82: {
                        this.popValue();
                        this.popValue();
                        this.popValue();
                        this.popValue();
                        break block23;
                    }
                    case 89: {
                        this.pushValue(this.peekValue());
                        break block23;
                    }
                    case 90: {
                        int stackSize = this.stackFrame.size();
                        this.stackFrame.add(stackSize - 2, this.stackFrame.get(stackSize - 1));
                        break block23;
                    }
                    case 91: {
                        int stackSize = this.stackFrame.size();
                        this.stackFrame.add(stackSize - 3, this.stackFrame.get(stackSize - 1));
                        break block23;
                    }
                    case 92: {
                        int stackSize = this.stackFrame.size();
                        this.stackFrame.add(stackSize - 2, this.stackFrame.get(stackSize - 1));
                        this.stackFrame.add(stackSize - 2, this.stackFrame.get(stackSize - 1));
                        break block23;
                    }
                    case 93: {
                        int stackSize = this.stackFrame.size();
                        this.stackFrame.add(stackSize - 3, this.stackFrame.get(stackSize - 1));
                        this.stackFrame.add(stackSize - 3, this.stackFrame.get(stackSize - 1));
                        break block23;
                    }
                    case 94: {
                        int stackSize = this.stackFrame.size();
                        this.stackFrame.add(stackSize - 4, this.stackFrame.get(stackSize - 1));
                        this.stackFrame.add(stackSize - 4, this.stackFrame.get(stackSize - 1));
                        break block23;
                    }
                    case 95: {
                        int stackSize = this.stackFrame.size();
                        this.stackFrame.add(stackSize - 2, this.stackFrame.get(stackSize - 1));
                        this.stackFrame.remove(stackSize);
                        break block23;
                    }
                    default: {
                        throw new IllegalArgumentException(INVALID_OPCODE + opcode);
                    }
                }
            }
            switch (opcode) {
                case 172: 
                case 173: 
                case 174: 
                case 175: 
                case 176: 
                case 177: 
                case 191: {
                    this.onMethodExit(opcode);
                    break;
                }
            }
        }
        super.visitInsn(opcode);
    }

    public void visitVarInsn(int opcode, int var) {
        super.visitVarInsn(opcode, var);
        if (this.isConstructor && !this.superClassConstructorCalled) {
            switch (opcode) {
                case 21: 
                case 23: {
                    this.pushValue(OTHER);
                    break;
                }
                case 22: 
                case 24: {
                    this.pushValue(OTHER);
                    this.pushValue(OTHER);
                    break;
                }
                case 25: {
                    this.pushValue(var == 0 ? UNINITIALIZED_THIS : OTHER);
                    break;
                }
                case 54: 
                case 56: 
                case 58: {
                    this.popValue();
                    break;
                }
                case 55: 
                case 57: {
                    this.popValue();
                    this.popValue();
                    break;
                }
                case 169: {
                    this.endConstructorBasicBlockWithoutSuccessor();
                    break;
                }
                default: {
                    throw new IllegalArgumentException(INVALID_OPCODE + opcode);
                }
            }
        }
    }

    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        super.visitFieldInsn(opcode, owner, name, descriptor);
        if (this.isConstructor && !this.superClassConstructorCalled) {
            char firstDescriptorChar = descriptor.charAt(0);
            boolean longOrDouble = firstDescriptorChar == 'J' || firstDescriptorChar == 'D';
            switch (opcode) {
                case 178: {
                    this.pushValue(OTHER);
                    if (!longOrDouble) break;
                    this.pushValue(OTHER);
                    break;
                }
                case 179: {
                    this.popValue();
                    if (!longOrDouble) break;
                    this.popValue();
                    break;
                }
                case 181: {
                    this.popValue();
                    this.popValue();
                    if (!longOrDouble) break;
                    this.popValue();
                    break;
                }
                case 180: {
                    if (!longOrDouble) break;
                    this.pushValue(OTHER);
                    break;
                }
                default: {
                    throw new IllegalArgumentException(INVALID_OPCODE + opcode);
                }
            }
        }
    }

    public void visitIntInsn(int opcode, int operand) {
        super.visitIntInsn(opcode, operand);
        if (this.isConstructor && !this.superClassConstructorCalled && opcode != 188) {
            this.pushValue(OTHER);
        }
    }

    public void visitLdcInsn(Object value) {
        super.visitLdcInsn(value);
        if (this.isConstructor && !this.superClassConstructorCalled) {
            this.pushValue(OTHER);
            if (value instanceof Double || value instanceof Long || value instanceof ConstantDynamic && ((ConstantDynamic)value).getSize() == 2) {
                this.pushValue(OTHER);
            }
        }
    }

    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        super.visitMultiANewArrayInsn(descriptor, numDimensions);
        if (this.isConstructor && !this.superClassConstructorCalled) {
            for (int i2 = 0; i2 < numDimensions; ++i2) {
                this.popValue();
            }
            this.pushValue(OTHER);
        }
    }

    public void visitTypeInsn(int opcode, String type) {
        super.visitTypeInsn(opcode, type);
        if (this.isConstructor && !this.superClassConstructorCalled && opcode == 187) {
            this.pushValue(OTHER);
        }
    }

    public void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
        if (this.api < 327680 && (opcodeAndSource & 0x100) == 0) {
            super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
            return;
        }
        super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
        int opcode = opcodeAndSource & 0xFFFFFEFF;
        this.doVisitMethodInsn(opcode, name, descriptor);
    }

    private void doVisitMethodInsn(int opcode, String name, String descriptor) {
        if (this.isConstructor && !this.superClassConstructorCalled) {
            for (Type argumentType : Type.getArgumentTypes(descriptor)) {
                this.popValue();
                if (argumentType.getSize() != 2) continue;
                this.popValue();
            }
            switch (opcode) {
                case 182: 
                case 185: {
                    this.popValue();
                    break;
                }
                case 183: {
                    Object value = this.popValue();
                    if (value != UNINITIALIZED_THIS || this.superClassConstructorCalled || !name.equals("<init>")) break;
                    this.superClassConstructorCalled = true;
                    this.onMethodEnter();
                    break;
                }
            }
            Type returnType = Type.getReturnType(descriptor);
            if (returnType != Type.VOID_TYPE) {
                this.pushValue(OTHER);
                if (returnType.getSize() == 2) {
                    this.pushValue(OTHER);
                }
            }
        }
    }

    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object ... bootstrapMethodArguments) {
        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
        this.doVisitMethodInsn(186, name, descriptor);
    }

    public void visitJumpInsn(int opcode, Label label) {
        super.visitJumpInsn(opcode, label);
        if (this.isConstructor && !this.superClassConstructorCalled) {
            switch (opcode) {
                case 153: 
                case 154: 
                case 155: 
                case 156: 
                case 157: 
                case 158: 
                case 198: 
                case 199: {
                    this.popValue();
                    break;
                }
                case 159: 
                case 160: 
                case 161: 
                case 162: 
                case 163: 
                case 164: 
                case 165: 
                case 166: {
                    this.popValue();
                    this.popValue();
                    break;
                }
                case 168: {
                    this.pushValue(OTHER);
                    break;
                }
                case 167: {
                    this.endConstructorBasicBlockWithoutSuccessor();
                    break;
                }
            }
            this.addForwardJump(label);
        }
    }

    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        super.visitLookupSwitchInsn(dflt, keys, labels);
        if (this.isConstructor && !this.superClassConstructorCalled) {
            this.popValue();
            this.addForwardJumps(dflt, labels);
            this.endConstructorBasicBlockWithoutSuccessor();
        }
    }

    public void visitTableSwitchInsn(int min, int max, Label dflt, Label ... labels) {
        super.visitTableSwitchInsn(min, max, dflt, labels);
        if (this.isConstructor && !this.superClassConstructorCalled) {
            this.popValue();
            this.addForwardJumps(dflt, labels);
            this.endConstructorBasicBlockWithoutSuccessor();
        }
    }

    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        super.visitTryCatchBlock(start, end, handler, type);
        if (this.isConstructor && !this.forwardJumpStackFrames.containsKey(handler)) {
            ArrayList<Object> handlerStackFrame = new ArrayList<Object>();
            handlerStackFrame.add(OTHER);
            this.forwardJumpStackFrames.put(handler, handlerStackFrame);
        }
    }

    private void addForwardJumps(Label dflt, Label[] labels) {
        this.addForwardJump(dflt);
        for (Label label : labels) {
            this.addForwardJump(label);
        }
    }

    private void addForwardJump(Label label) {
        if (this.forwardJumpStackFrames.containsKey(label)) {
            return;
        }
        this.forwardJumpStackFrames.put(label, new ArrayList<Object>(this.stackFrame));
    }

    private void endConstructorBasicBlockWithoutSuccessor() {
        this.superClassConstructorCalled = true;
    }

    private Object popValue() {
        return this.stackFrame.remove(this.stackFrame.size() - 1);
    }

    private Object peekValue() {
        return this.stackFrame.get(this.stackFrame.size() - 1);
    }

    private void pushValue(Object value) {
        this.stackFrame.add(value);
    }

    protected void onMethodEnter() {
    }

    protected void onMethodExit(int opcode) {
    }
}

