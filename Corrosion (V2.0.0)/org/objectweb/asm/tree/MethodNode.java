/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableAnnotationNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.ParameterNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeAnnotationNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.UnsupportedClassVersionException;
import org.objectweb.asm.tree.Util;
import org.objectweb.asm.tree.VarInsnNode;

public class MethodNode
extends MethodVisitor {
    public int access;
    public String name;
    public String desc;
    public String signature;
    public List<String> exceptions;
    public List<ParameterNode> parameters;
    public List<AnnotationNode> visibleAnnotations;
    public List<AnnotationNode> invisibleAnnotations;
    public List<TypeAnnotationNode> visibleTypeAnnotations;
    public List<TypeAnnotationNode> invisibleTypeAnnotations;
    public List<Attribute> attrs;
    public Object annotationDefault;
    public int visibleAnnotableParameterCount;
    public List<AnnotationNode>[] visibleParameterAnnotations;
    public int invisibleAnnotableParameterCount;
    public List<AnnotationNode>[] invisibleParameterAnnotations;
    public InsnList instructions;
    public List<TryCatchBlockNode> tryCatchBlocks;
    public int maxStack;
    public int maxLocals;
    public List<LocalVariableNode> localVariables;
    public List<LocalVariableAnnotationNode> visibleLocalVariableAnnotations;
    public List<LocalVariableAnnotationNode> invisibleLocalVariableAnnotations;
    private boolean visited;

    public MethodNode() {
        this(589824);
        if (this.getClass() != MethodNode.class) {
            throw new IllegalStateException();
        }
    }

    public MethodNode(int api2) {
        super(api2);
        this.instructions = new InsnList();
    }

    public MethodNode(int access, String name, String descriptor, String signature, String[] exceptions) {
        this(589824, access, name, descriptor, signature, exceptions);
        if (this.getClass() != MethodNode.class) {
            throw new IllegalStateException();
        }
    }

    public MethodNode(int api2, int access, String name, String descriptor, String signature, String[] exceptions) {
        super(api2);
        this.access = access;
        this.name = name;
        this.desc = descriptor;
        this.signature = signature;
        this.exceptions = Util.asArrayList(exceptions);
        if ((access & 0x400) == 0) {
            this.localVariables = new ArrayList<LocalVariableNode>(5);
        }
        this.tryCatchBlocks = new ArrayList<TryCatchBlockNode>();
        this.instructions = new InsnList();
    }

    public void visitParameter(String name, int access) {
        if (this.parameters == null) {
            this.parameters = new ArrayList<ParameterNode>(5);
        }
        this.parameters.add(new ParameterNode(name, access));
    }

    public AnnotationVisitor visitAnnotationDefault() {
        return new AnnotationNode((List<Object>)new ArrayList<Object>(0){

            @Override
            public boolean add(Object o2) {
                MethodNode.this.annotationDefault = o2;
                return super.add(o2);
            }
        });
    }

    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        AnnotationNode annotation = new AnnotationNode(descriptor);
        if (visible) {
            this.visibleAnnotations = Util.add(this.visibleAnnotations, annotation);
        } else {
            this.invisibleAnnotations = Util.add(this.invisibleAnnotations, annotation);
        }
        return annotation;
    }

    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        TypeAnnotationNode typeAnnotation = new TypeAnnotationNode(typeRef, typePath, descriptor);
        if (visible) {
            this.visibleTypeAnnotations = Util.add(this.visibleTypeAnnotations, typeAnnotation);
        } else {
            this.invisibleTypeAnnotations = Util.add(this.invisibleTypeAnnotations, typeAnnotation);
        }
        return typeAnnotation;
    }

    public void visitAnnotableParameterCount(int parameterCount, boolean visible) {
        if (visible) {
            this.visibleAnnotableParameterCount = parameterCount;
        } else {
            this.invisibleAnnotableParameterCount = parameterCount;
        }
    }

    public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
        AnnotationNode annotation = new AnnotationNode(descriptor);
        if (visible) {
            if (this.visibleParameterAnnotations == null) {
                int params = Type.getArgumentTypes(this.desc).length;
                this.visibleParameterAnnotations = new List[params];
            }
            this.visibleParameterAnnotations[parameter] = Util.add(this.visibleParameterAnnotations[parameter], annotation);
        } else {
            if (this.invisibleParameterAnnotations == null) {
                int params = Type.getArgumentTypes(this.desc).length;
                this.invisibleParameterAnnotations = new List[params];
            }
            this.invisibleParameterAnnotations[parameter] = Util.add(this.invisibleParameterAnnotations[parameter], annotation);
        }
        return annotation;
    }

    public void visitAttribute(Attribute attribute) {
        this.attrs = Util.add(this.attrs, attribute);
    }

    public void visitCode() {
    }

    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        this.instructions.add(new FrameNode(type, numLocal, local == null ? null : this.getLabelNodes(local), numStack, stack == null ? null : this.getLabelNodes(stack)));
    }

    public void visitInsn(int opcode) {
        this.instructions.add(new InsnNode(opcode));
    }

    public void visitIntInsn(int opcode, int operand) {
        this.instructions.add(new IntInsnNode(opcode, operand));
    }

    public void visitVarInsn(int opcode, int var) {
        this.instructions.add(new VarInsnNode(opcode, var));
    }

    public void visitTypeInsn(int opcode, String type) {
        this.instructions.add(new TypeInsnNode(opcode, type));
    }

    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        this.instructions.add(new FieldInsnNode(opcode, owner, name, descriptor));
    }

    public void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
        if (this.api < 327680 && (opcodeAndSource & 0x100) == 0) {
            super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
            return;
        }
        int opcode = opcodeAndSource & 0xFFFFFEFF;
        this.instructions.add(new MethodInsnNode(opcode, owner, name, descriptor, isInterface));
    }

    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object ... bootstrapMethodArguments) {
        this.instructions.add(new InvokeDynamicInsnNode(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments));
    }

    public void visitJumpInsn(int opcode, Label label) {
        this.instructions.add(new JumpInsnNode(opcode, this.getLabelNode(label)));
    }

    public void visitLabel(Label label) {
        this.instructions.add(this.getLabelNode(label));
    }

    public void visitLdcInsn(Object value) {
        this.instructions.add(new LdcInsnNode(value));
    }

    public void visitIincInsn(int var, int increment) {
        this.instructions.add(new IincInsnNode(var, increment));
    }

    public void visitTableSwitchInsn(int min, int max, Label dflt, Label ... labels) {
        this.instructions.add(new TableSwitchInsnNode(min, max, this.getLabelNode(dflt), this.getLabelNodes(labels)));
    }

    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        this.instructions.add(new LookupSwitchInsnNode(this.getLabelNode(dflt), keys, this.getLabelNodes(labels)));
    }

    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        this.instructions.add(new MultiANewArrayInsnNode(descriptor, numDimensions));
    }

    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        AbstractInsnNode currentInsn = this.instructions.getLast();
        while (currentInsn.getOpcode() == -1) {
            currentInsn = currentInsn.getPrevious();
        }
        TypeAnnotationNode typeAnnotation = new TypeAnnotationNode(typeRef, typePath, descriptor);
        if (visible) {
            currentInsn.visibleTypeAnnotations = Util.add(currentInsn.visibleTypeAnnotations, typeAnnotation);
        } else {
            currentInsn.invisibleTypeAnnotations = Util.add(currentInsn.invisibleTypeAnnotations, typeAnnotation);
        }
        return typeAnnotation;
    }

    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        TryCatchBlockNode tryCatchBlock = new TryCatchBlockNode(this.getLabelNode(start), this.getLabelNode(end), this.getLabelNode(handler), type);
        this.tryCatchBlocks = Util.add(this.tryCatchBlocks, tryCatchBlock);
    }

    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        TryCatchBlockNode tryCatchBlock = this.tryCatchBlocks.get((typeRef & 0xFFFF00) >> 8);
        TypeAnnotationNode typeAnnotation = new TypeAnnotationNode(typeRef, typePath, descriptor);
        if (visible) {
            tryCatchBlock.visibleTypeAnnotations = Util.add(tryCatchBlock.visibleTypeAnnotations, typeAnnotation);
        } else {
            tryCatchBlock.invisibleTypeAnnotations = Util.add(tryCatchBlock.invisibleTypeAnnotations, typeAnnotation);
        }
        return typeAnnotation;
    }

    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        LocalVariableNode localVariable = new LocalVariableNode(name, descriptor, signature, this.getLabelNode(start), this.getLabelNode(end), index);
        this.localVariables = Util.add(this.localVariables, localVariable);
    }

    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
        LocalVariableAnnotationNode localVariableAnnotation = new LocalVariableAnnotationNode(typeRef, typePath, this.getLabelNodes(start), this.getLabelNodes(end), index, descriptor);
        if (visible) {
            this.visibleLocalVariableAnnotations = Util.add(this.visibleLocalVariableAnnotations, localVariableAnnotation);
        } else {
            this.invisibleLocalVariableAnnotations = Util.add(this.invisibleLocalVariableAnnotations, localVariableAnnotation);
        }
        return localVariableAnnotation;
    }

    public void visitLineNumber(int line, Label start) {
        this.instructions.add(new LineNumberNode(line, this.getLabelNode(start)));
    }

    public void visitMaxs(int maxStack, int maxLocals) {
        this.maxStack = maxStack;
        this.maxLocals = maxLocals;
    }

    public void visitEnd() {
    }

    protected LabelNode getLabelNode(Label label) {
        if (!(label.info instanceof LabelNode)) {
            label.info = new LabelNode();
        }
        return (LabelNode)label.info;
    }

    private LabelNode[] getLabelNodes(Label[] labels) {
        LabelNode[] labelNodes = new LabelNode[labels.length];
        int n2 = labels.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            labelNodes[i2] = this.getLabelNode(labels[i2]);
        }
        return labelNodes;
    }

    private Object[] getLabelNodes(Object[] objects) {
        Object[] labelNodes = new Object[objects.length];
        for (Object o2 : objects) {
            if (o2 instanceof Label) {
                o2 = this.getLabelNode((Label)o2);
            }
            labelNodes[i] = o2;
        }
        return labelNodes;
    }

    public void check(int api2) {
        AbstractInsnNode insn;
        int i2;
        if (api2 == 262144) {
            if (this.parameters != null && !this.parameters.isEmpty()) {
                throw new UnsupportedClassVersionException();
            }
            if (this.visibleTypeAnnotations != null && !this.visibleTypeAnnotations.isEmpty()) {
                throw new UnsupportedClassVersionException();
            }
            if (this.invisibleTypeAnnotations != null && !this.invisibleTypeAnnotations.isEmpty()) {
                throw new UnsupportedClassVersionException();
            }
            if (this.tryCatchBlocks != null) {
                for (i2 = this.tryCatchBlocks.size() - 1; i2 >= 0; --i2) {
                    TryCatchBlockNode tryCatchBlock = this.tryCatchBlocks.get(i2);
                    if (tryCatchBlock.visibleTypeAnnotations != null && !tryCatchBlock.visibleTypeAnnotations.isEmpty()) {
                        throw new UnsupportedClassVersionException();
                    }
                    if (tryCatchBlock.invisibleTypeAnnotations == null || tryCatchBlock.invisibleTypeAnnotations.isEmpty()) continue;
                    throw new UnsupportedClassVersionException();
                }
            }
            for (i2 = this.instructions.size() - 1; i2 >= 0; --i2) {
                Object value;
                boolean isInterface;
                insn = this.instructions.get(i2);
                if (insn.visibleTypeAnnotations != null && !insn.visibleTypeAnnotations.isEmpty()) {
                    throw new UnsupportedClassVersionException();
                }
                if (insn.invisibleTypeAnnotations != null && !insn.invisibleTypeAnnotations.isEmpty()) {
                    throw new UnsupportedClassVersionException();
                }
                if (!(insn instanceof MethodInsnNode ? (isInterface = ((MethodInsnNode)insn).itf) != (insn.opcode == 185) : insn instanceof LdcInsnNode && ((value = ((LdcInsnNode)insn).cst) instanceof Handle || value instanceof Type && ((Type)value).getSort() == 11))) continue;
                throw new UnsupportedClassVersionException();
            }
            if (this.visibleLocalVariableAnnotations != null && !this.visibleLocalVariableAnnotations.isEmpty()) {
                throw new UnsupportedClassVersionException();
            }
            if (this.invisibleLocalVariableAnnotations != null && !this.invisibleLocalVariableAnnotations.isEmpty()) {
                throw new UnsupportedClassVersionException();
            }
        }
        if (api2 < 458752) {
            for (i2 = this.instructions.size() - 1; i2 >= 0; --i2) {
                Object value;
                insn = this.instructions.get(i2);
                if (!(insn instanceof LdcInsnNode) || !((value = ((LdcInsnNode)insn).cst) instanceof ConstantDynamic)) continue;
                throw new UnsupportedClassVersionException();
            }
        }
    }

    public void accept(ClassVisitor classVisitor) {
        String[] exceptionsArray = this.exceptions == null ? null : this.exceptions.toArray(new String[0]);
        MethodVisitor methodVisitor = classVisitor.visitMethod(this.access, this.name, this.desc, this.signature, exceptionsArray);
        if (methodVisitor != null) {
            this.accept(methodVisitor);
        }
    }

    public void accept(MethodVisitor methodVisitor) {
        AnnotationNode annotation;
        int j2;
        int m2;
        List<AnnotationNode> parameterAnnotations;
        TypeAnnotationNode typeAnnotation;
        AnnotationNode annotation2;
        int i2;
        int n2;
        if (this.parameters != null) {
            n2 = this.parameters.size();
            for (i2 = 0; i2 < n2; ++i2) {
                this.parameters.get(i2).accept(methodVisitor);
            }
        }
        if (this.annotationDefault != null) {
            AnnotationVisitor annotationVisitor = methodVisitor.visitAnnotationDefault();
            AnnotationNode.accept(annotationVisitor, null, this.annotationDefault);
            if (annotationVisitor != null) {
                annotationVisitor.visitEnd();
            }
        }
        if (this.visibleAnnotations != null) {
            n2 = this.visibleAnnotations.size();
            for (i2 = 0; i2 < n2; ++i2) {
                annotation2 = this.visibleAnnotations.get(i2);
                annotation2.accept(methodVisitor.visitAnnotation(annotation2.desc, true));
            }
        }
        if (this.invisibleAnnotations != null) {
            n2 = this.invisibleAnnotations.size();
            for (i2 = 0; i2 < n2; ++i2) {
                annotation2 = this.invisibleAnnotations.get(i2);
                annotation2.accept(methodVisitor.visitAnnotation(annotation2.desc, false));
            }
        }
        if (this.visibleTypeAnnotations != null) {
            n2 = this.visibleTypeAnnotations.size();
            for (i2 = 0; i2 < n2; ++i2) {
                typeAnnotation = this.visibleTypeAnnotations.get(i2);
                typeAnnotation.accept(methodVisitor.visitTypeAnnotation(typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, true));
            }
        }
        if (this.invisibleTypeAnnotations != null) {
            n2 = this.invisibleTypeAnnotations.size();
            for (i2 = 0; i2 < n2; ++i2) {
                typeAnnotation = this.invisibleTypeAnnotations.get(i2);
                typeAnnotation.accept(methodVisitor.visitTypeAnnotation(typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, false));
            }
        }
        if (this.visibleAnnotableParameterCount > 0) {
            methodVisitor.visitAnnotableParameterCount(this.visibleAnnotableParameterCount, true);
        }
        if (this.visibleParameterAnnotations != null) {
            n2 = this.visibleParameterAnnotations.length;
            for (i2 = 0; i2 < n2; ++i2) {
                parameterAnnotations = this.visibleParameterAnnotations[i2];
                if (parameterAnnotations == null) continue;
                m2 = parameterAnnotations.size();
                for (j2 = 0; j2 < m2; ++j2) {
                    annotation = parameterAnnotations.get(j2);
                    annotation.accept(methodVisitor.visitParameterAnnotation(i2, annotation.desc, true));
                }
            }
        }
        if (this.invisibleAnnotableParameterCount > 0) {
            methodVisitor.visitAnnotableParameterCount(this.invisibleAnnotableParameterCount, false);
        }
        if (this.invisibleParameterAnnotations != null) {
            n2 = this.invisibleParameterAnnotations.length;
            for (i2 = 0; i2 < n2; ++i2) {
                parameterAnnotations = this.invisibleParameterAnnotations[i2];
                if (parameterAnnotations == null) continue;
                m2 = parameterAnnotations.size();
                for (j2 = 0; j2 < m2; ++j2) {
                    annotation = parameterAnnotations.get(j2);
                    annotation.accept(methodVisitor.visitParameterAnnotation(i2, annotation.desc, false));
                }
            }
        }
        if (this.visited) {
            this.instructions.resetLabels();
        }
        if (this.attrs != null) {
            n2 = this.attrs.size();
            for (i2 = 0; i2 < n2; ++i2) {
                methodVisitor.visitAttribute(this.attrs.get(i2));
            }
        }
        if (this.instructions.size() > 0) {
            methodVisitor.visitCode();
            if (this.tryCatchBlocks != null) {
                n2 = this.tryCatchBlocks.size();
                for (i2 = 0; i2 < n2; ++i2) {
                    this.tryCatchBlocks.get(i2).updateIndex(i2);
                    this.tryCatchBlocks.get(i2).accept(methodVisitor);
                }
            }
            this.instructions.accept(methodVisitor);
            if (this.localVariables != null) {
                n2 = this.localVariables.size();
                for (i2 = 0; i2 < n2; ++i2) {
                    this.localVariables.get(i2).accept(methodVisitor);
                }
            }
            if (this.visibleLocalVariableAnnotations != null) {
                n2 = this.visibleLocalVariableAnnotations.size();
                for (i2 = 0; i2 < n2; ++i2) {
                    this.visibleLocalVariableAnnotations.get(i2).accept(methodVisitor, true);
                }
            }
            if (this.invisibleLocalVariableAnnotations != null) {
                n2 = this.invisibleLocalVariableAnnotations.size();
                for (i2 = 0; i2 < n2; ++i2) {
                    this.invisibleLocalVariableAnnotations.get(i2).accept(methodVisitor, false);
                }
            }
            methodVisitor.visitMaxs(this.maxStack, this.maxLocals);
            this.visited = true;
        }
        methodVisitor.visitEnd();
    }
}

