/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

public class LocalVariablesSorter
extends MethodVisitor {
    private static final Type OBJECT_TYPE = Type.getObjectType("java/lang/Object");
    private int[] remappedVariableIndices = new int[40];
    private Object[] remappedLocalTypes = new Object[20];
    protected final int firstLocal;
    protected int nextLocal;

    public LocalVariablesSorter(int access, String descriptor, MethodVisitor methodVisitor) {
        this(589824, access, descriptor, methodVisitor);
        if (this.getClass() != LocalVariablesSorter.class) {
            throw new IllegalStateException();
        }
    }

    protected LocalVariablesSorter(int api2, int access, String descriptor, MethodVisitor methodVisitor) {
        super(api2, methodVisitor);
        this.nextLocal = (8 & access) == 0 ? 1 : 0;
        for (Type argumentType : Type.getArgumentTypes(descriptor)) {
            this.nextLocal += argumentType.getSize();
        }
        this.firstLocal = this.nextLocal;
    }

    public void visitVarInsn(int opcode, int var) {
        Type varType;
        switch (opcode) {
            case 22: 
            case 55: {
                varType = Type.LONG_TYPE;
                break;
            }
            case 24: 
            case 57: {
                varType = Type.DOUBLE_TYPE;
                break;
            }
            case 23: 
            case 56: {
                varType = Type.FLOAT_TYPE;
                break;
            }
            case 21: 
            case 54: {
                varType = Type.INT_TYPE;
                break;
            }
            case 25: 
            case 58: 
            case 169: {
                varType = OBJECT_TYPE;
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid opcode " + opcode);
            }
        }
        super.visitVarInsn(opcode, this.remap(var, varType));
    }

    public void visitIincInsn(int var, int increment) {
        super.visitIincInsn(this.remap(var, Type.INT_TYPE), increment);
    }

    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(maxStack, this.nextLocal);
    }

    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        int remappedIndex = this.remap(index, Type.getType(descriptor));
        super.visitLocalVariable(name, descriptor, signature, start, end, remappedIndex);
    }

    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
        Type type = Type.getType(descriptor);
        int[] remappedIndex = new int[index.length];
        for (int i2 = 0; i2 < remappedIndex.length; ++i2) {
            remappedIndex[i2] = this.remap(index[i2], type);
        }
        return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, remappedIndex, descriptor, visible);
    }

    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        Object localType;
        if (type != -1) {
            throw new IllegalArgumentException("LocalVariablesSorter only accepts expanded frames (see ClassReader.EXPAND_FRAMES)");
        }
        Object[] oldRemappedLocals = new Object[this.remappedLocalTypes.length];
        System.arraycopy(this.remappedLocalTypes, 0, oldRemappedLocals, 0, oldRemappedLocals.length);
        this.updateNewLocals(this.remappedLocalTypes);
        int oldVar = 0;
        for (int i2 = 0; i2 < numLocal; ++i2) {
            Object localType2 = local[i2];
            if (localType2 != Opcodes.TOP) {
                Type varType = OBJECT_TYPE;
                if (localType2 == Opcodes.INTEGER) {
                    varType = Type.INT_TYPE;
                } else if (localType2 == Opcodes.FLOAT) {
                    varType = Type.FLOAT_TYPE;
                } else if (localType2 == Opcodes.LONG) {
                    varType = Type.LONG_TYPE;
                } else if (localType2 == Opcodes.DOUBLE) {
                    varType = Type.DOUBLE_TYPE;
                } else if (localType2 instanceof String) {
                    varType = Type.getObjectType((String)localType2);
                }
                this.setFrameLocal(this.remap(oldVar, varType), localType2);
            }
            oldVar += localType2 == Opcodes.LONG || localType2 == Opcodes.DOUBLE ? 2 : 1;
        }
        int newVar = 0;
        int remappedNumLocal = 0;
        for (oldVar = 0; oldVar < this.remappedLocalTypes.length; oldVar += (localType = this.remappedLocalTypes[oldVar]) == Opcodes.LONG || localType == Opcodes.DOUBLE ? 2 : 1) {
            if (localType != null && localType != Opcodes.TOP) {
                this.remappedLocalTypes[newVar++] = localType;
                remappedNumLocal = newVar;
                continue;
            }
            this.remappedLocalTypes[newVar++] = Opcodes.TOP;
        }
        super.visitFrame(type, remappedNumLocal, this.remappedLocalTypes, numStack, stack);
        this.remappedLocalTypes = oldRemappedLocals;
    }

    public int newLocal(Type type) {
        Object localType;
        switch (type.getSort()) {
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: {
                localType = Opcodes.INTEGER;
                break;
            }
            case 6: {
                localType = Opcodes.FLOAT;
                break;
            }
            case 7: {
                localType = Opcodes.LONG;
                break;
            }
            case 8: {
                localType = Opcodes.DOUBLE;
                break;
            }
            case 9: {
                localType = type.getDescriptor();
                break;
            }
            case 10: {
                localType = type.getInternalName();
                break;
            }
            default: {
                throw new AssertionError();
            }
        }
        int local = this.newLocalMapping(type);
        this.setLocalType(local, type);
        this.setFrameLocal(local, localType);
        return local;
    }

    protected void updateNewLocals(Object[] newLocals) {
    }

    protected void setLocalType(int local, Type type) {
    }

    private void setFrameLocal(int local, Object type) {
        int numLocals = this.remappedLocalTypes.length;
        if (local >= numLocals) {
            Object[] newRemappedLocalTypes = new Object[Math.max(2 * numLocals, local + 1)];
            System.arraycopy(this.remappedLocalTypes, 0, newRemappedLocalTypes, 0, numLocals);
            this.remappedLocalTypes = newRemappedLocalTypes;
        }
        this.remappedLocalTypes[local] = type;
    }

    private int remap(int var, Type type) {
        int value;
        int size;
        if (var + type.getSize() <= this.firstLocal) {
            return var;
        }
        int key = 2 * var + type.getSize() - 1;
        if (key >= (size = this.remappedVariableIndices.length)) {
            int[] newRemappedVariableIndices = new int[Math.max(2 * size, key + 1)];
            System.arraycopy(this.remappedVariableIndices, 0, newRemappedVariableIndices, 0, size);
            this.remappedVariableIndices = newRemappedVariableIndices;
        }
        if ((value = this.remappedVariableIndices[key]) == 0) {
            value = this.newLocalMapping(type);
            this.setLocalType(value, type);
            this.remappedVariableIndices[key] = value + 1;
        } else {
            --value;
        }
        return value;
    }

    protected int newLocalMapping(Type type) {
        int local = this.nextLocal;
        this.nextLocal += type.getSize();
        return local;
    }
}

