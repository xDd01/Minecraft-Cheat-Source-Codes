/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.objectweb.asm.tree.analysis.Subroutine;
import org.objectweb.asm.tree.analysis.Value;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Analyzer<V extends Value>
implements Opcodes {
    private final Interpreter<V> interpreter;
    private InsnList insnList;
    private int insnListSize;
    private List<TryCatchBlockNode>[] handlers;
    private Frame<V>[] frames;
    private Subroutine[] subroutines;
    private boolean[] inInstructionsToProcess;
    private int[] instructionsToProcess;
    private int numInstructionsToProcess;

    public Analyzer(Interpreter<V> interpreter) {
        this.interpreter = interpreter;
    }

    public Frame<V>[] analyze(String owner, MethodNode method) throws AnalyzerException {
        if ((method.access & 0x500) != 0) {
            this.frames = new Frame[0];
            return this.frames;
        }
        this.insnList = method.instructions;
        this.insnListSize = this.insnList.size();
        this.handlers = new List[this.insnListSize];
        this.frames = new Frame[this.insnListSize];
        this.subroutines = new Subroutine[this.insnListSize];
        this.inInstructionsToProcess = new boolean[this.insnListSize];
        this.instructionsToProcess = new int[this.insnListSize];
        this.numInstructionsToProcess = 0;
        for (int i2 = 0; i2 < method.tryCatchBlocks.size(); ++i2) {
            TryCatchBlockNode tryCatchBlock = method.tryCatchBlocks.get(i2);
            int startIndex = this.insnList.indexOf(tryCatchBlock.start);
            int endIndex = this.insnList.indexOf(tryCatchBlock.end);
            for (int j2 = startIndex; j2 < endIndex; ++j2) {
                List<TryCatchBlockNode> insnHandlers = this.handlers[j2];
                if (insnHandlers == null) {
                    this.handlers[j2] = insnHandlers = new ArrayList<TryCatchBlockNode>();
                }
                insnHandlers.add(tryCatchBlock);
            }
        }
        Subroutine main = new Subroutine(null, method.maxLocals, null);
        ArrayList<AbstractInsnNode> jsrInsns = new ArrayList<AbstractInsnNode>();
        this.findSubroutine(0, main, jsrInsns);
        HashMap<LabelNode, Subroutine> jsrSubroutines = new HashMap<LabelNode, Subroutine>();
        while (!jsrInsns.isEmpty()) {
            JumpInsnNode jsrInsn = (JumpInsnNode)jsrInsns.remove(0);
            Subroutine subroutine = (Subroutine)jsrSubroutines.get(jsrInsn.label);
            if (subroutine == null) {
                subroutine = new Subroutine(jsrInsn.label, method.maxLocals, jsrInsn);
                jsrSubroutines.put(jsrInsn.label, subroutine);
                this.findSubroutine(this.insnList.indexOf(jsrInsn.label), subroutine, jsrInsns);
                continue;
            }
            subroutine.callers.add(jsrInsn);
        }
        for (int i3 = 0; i3 < this.insnListSize; ++i3) {
            if (this.subroutines[i3] == null || this.subroutines[i3].start != null) continue;
            this.subroutines[i3] = null;
        }
        Frame<V> currentFrame = this.computeInitialFrame(owner, method);
        this.merge(0, currentFrame, null);
        this.init(owner, method);
        while (this.numInstructionsToProcess > 0) {
            int insnIndex = this.instructionsToProcess[--this.numInstructionsToProcess];
            Frame<V> oldFrame = this.frames[insnIndex];
            Subroutine subroutine = this.subroutines[insnIndex];
            this.inInstructionsToProcess[insnIndex] = false;
            AbstractInsnNode insnNode = null;
            try {
                List<TryCatchBlockNode> insnHandlers;
                insnNode = method.instructions.get(insnIndex);
                int insnOpcode = insnNode.getOpcode();
                int insnType = insnNode.getType();
                if (insnType == 8 || insnType == 15 || insnType == 14) {
                    this.merge(insnIndex + 1, oldFrame, subroutine);
                    this.newControlFlowEdge(insnIndex, insnIndex + 1);
                } else {
                    LabelNode label;
                    int i4;
                    currentFrame.init(oldFrame).execute(insnNode, this.interpreter);
                    Subroutine subroutine2 = subroutine = subroutine == null ? null : new Subroutine(subroutine);
                    if (insnNode instanceof JumpInsnNode) {
                        JumpInsnNode jumpInsn = (JumpInsnNode)insnNode;
                        if (insnOpcode != 167 && insnOpcode != 168) {
                            currentFrame.initJumpTarget(insnOpcode, null);
                            this.merge(insnIndex + 1, currentFrame, subroutine);
                            this.newControlFlowEdge(insnIndex, insnIndex + 1);
                        }
                        int jumpInsnIndex = this.insnList.indexOf(jumpInsn.label);
                        currentFrame.initJumpTarget(insnOpcode, jumpInsn.label);
                        if (insnOpcode == 168) {
                            this.merge(jumpInsnIndex, currentFrame, new Subroutine(jumpInsn.label, method.maxLocals, jumpInsn));
                        } else {
                            this.merge(jumpInsnIndex, currentFrame, subroutine);
                        }
                        this.newControlFlowEdge(insnIndex, jumpInsnIndex);
                    } else if (insnNode instanceof LookupSwitchInsnNode) {
                        LookupSwitchInsnNode lookupSwitchInsn = (LookupSwitchInsnNode)insnNode;
                        int targetInsnIndex = this.insnList.indexOf(lookupSwitchInsn.dflt);
                        currentFrame.initJumpTarget(insnOpcode, lookupSwitchInsn.dflt);
                        this.merge(targetInsnIndex, currentFrame, subroutine);
                        this.newControlFlowEdge(insnIndex, targetInsnIndex);
                        for (i4 = 0; i4 < lookupSwitchInsn.labels.size(); ++i4) {
                            label = lookupSwitchInsn.labels.get(i4);
                            targetInsnIndex = this.insnList.indexOf(label);
                            currentFrame.initJumpTarget(insnOpcode, label);
                            this.merge(targetInsnIndex, currentFrame, subroutine);
                            this.newControlFlowEdge(insnIndex, targetInsnIndex);
                        }
                    } else if (insnNode instanceof TableSwitchInsnNode) {
                        TableSwitchInsnNode tableSwitchInsn = (TableSwitchInsnNode)insnNode;
                        int targetInsnIndex = this.insnList.indexOf(tableSwitchInsn.dflt);
                        currentFrame.initJumpTarget(insnOpcode, tableSwitchInsn.dflt);
                        this.merge(targetInsnIndex, currentFrame, subroutine);
                        this.newControlFlowEdge(insnIndex, targetInsnIndex);
                        for (i4 = 0; i4 < tableSwitchInsn.labels.size(); ++i4) {
                            label = tableSwitchInsn.labels.get(i4);
                            currentFrame.initJumpTarget(insnOpcode, label);
                            targetInsnIndex = this.insnList.indexOf(label);
                            this.merge(targetInsnIndex, currentFrame, subroutine);
                            this.newControlFlowEdge(insnIndex, targetInsnIndex);
                        }
                    } else if (insnOpcode == 169) {
                        if (subroutine == null) {
                            throw new AnalyzerException(insnNode, "RET instruction outside of a subroutine");
                        }
                        for (int i5 = 0; i5 < subroutine.callers.size(); ++i5) {
                            JumpInsnNode caller = subroutine.callers.get(i5);
                            int jsrInsnIndex = this.insnList.indexOf(caller);
                            if (this.frames[jsrInsnIndex] == null) continue;
                            this.merge(jsrInsnIndex + 1, this.frames[jsrInsnIndex], currentFrame, this.subroutines[jsrInsnIndex], subroutine.localsUsed);
                            this.newControlFlowEdge(insnIndex, jsrInsnIndex + 1);
                        }
                    } else if (insnOpcode != 191 && (insnOpcode < 172 || insnOpcode > 177)) {
                        if (subroutine != null) {
                            if (insnNode instanceof VarInsnNode) {
                                int var = ((VarInsnNode)insnNode).var;
                                subroutine.localsUsed[var] = true;
                                if (insnOpcode == 22 || insnOpcode == 24 || insnOpcode == 55 || insnOpcode == 57) {
                                    subroutine.localsUsed[var + 1] = true;
                                }
                            } else if (insnNode instanceof IincInsnNode) {
                                int var = ((IincInsnNode)insnNode).var;
                                subroutine.localsUsed[var] = true;
                            }
                        }
                        this.merge(insnIndex + 1, currentFrame, subroutine);
                        this.newControlFlowEdge(insnIndex, insnIndex + 1);
                    }
                }
                if ((insnHandlers = this.handlers[insnIndex]) == null) continue;
                for (TryCatchBlockNode tryCatchBlock : insnHandlers) {
                    Type catchType = tryCatchBlock.type == null ? Type.getObjectType("java/lang/Throwable") : Type.getObjectType(tryCatchBlock.type);
                    if (!this.newControlFlowExceptionEdge(insnIndex, tryCatchBlock)) continue;
                    Frame<V> handler = this.newFrame(oldFrame);
                    handler.clearStack();
                    handler.push(this.interpreter.newExceptionValue(tryCatchBlock, handler, catchType));
                    this.merge(this.insnList.indexOf(tryCatchBlock.handler), handler, subroutine);
                }
            }
            catch (AnalyzerException e2) {
                throw new AnalyzerException(e2.node, "Error at instruction " + insnIndex + ": " + e2.getMessage(), e2);
            }
            catch (RuntimeException e3) {
                throw new AnalyzerException(insnNode, "Error at instruction " + insnIndex + ": " + e3.getMessage(), e3);
            }
        }
        return this.frames;
    }

    public Frame<V>[] analyzeAndComputeMaxs(String owner, MethodNode method) throws AnalyzerException {
        method.maxLocals = Analyzer.computeMaxLocals(method);
        method.maxStack = -1;
        this.analyze(owner, method);
        method.maxStack = Analyzer.computeMaxStack(this.frames);
        return this.frames;
    }

    private static int computeMaxLocals(MethodNode method) {
        int maxLocals = Type.getArgumentsAndReturnSizes(method.desc) >> 2;
        for (AbstractInsnNode insnNode : method.instructions) {
            int local;
            if (insnNode instanceof VarInsnNode) {
                local = ((VarInsnNode)insnNode).var;
                int size = insnNode.getOpcode() == 22 || insnNode.getOpcode() == 24 || insnNode.getOpcode() == 55 || insnNode.getOpcode() == 57 ? 2 : 1;
                maxLocals = Math.max(maxLocals, local + size);
                continue;
            }
            if (!(insnNode instanceof IincInsnNode)) continue;
            local = ((IincInsnNode)insnNode).var;
            maxLocals = Math.max(maxLocals, local + 1);
        }
        return maxLocals;
    }

    private static int computeMaxStack(Frame<?>[] frames) {
        int maxStack = 0;
        for (Frame<?> frame : frames) {
            if (frame == null) continue;
            int stackSize = 0;
            for (int i2 = 0; i2 < frame.getStackSize(); ++i2) {
                stackSize += frame.getStack(i2).getSize();
            }
            maxStack = Math.max(maxStack, stackSize);
        }
        return maxStack;
    }

    private void findSubroutine(int insnIndex, Subroutine subroutine, List<AbstractInsnNode> jsrInsns) throws AnalyzerException {
        ArrayList<Integer> instructionIndicesToProcess = new ArrayList<Integer>();
        instructionIndicesToProcess.add(insnIndex);
        block3: while (!instructionIndicesToProcess.isEmpty()) {
            LabelNode labelNode;
            int currentInsnIndex = (Integer)instructionIndicesToProcess.remove(instructionIndicesToProcess.size() - 1);
            if (currentInsnIndex < 0 || currentInsnIndex >= this.insnListSize) {
                throw new AnalyzerException(null, "Execution can fall off the end of the code");
            }
            if (this.subroutines[currentInsnIndex] != null) continue;
            this.subroutines[currentInsnIndex] = new Subroutine(subroutine);
            AbstractInsnNode currentInsn = this.insnList.get(currentInsnIndex);
            if (currentInsn instanceof JumpInsnNode) {
                if (currentInsn.getOpcode() == 168) {
                    jsrInsns.add(currentInsn);
                } else {
                    JumpInsnNode jumpInsn = (JumpInsnNode)currentInsn;
                    instructionIndicesToProcess.add(this.insnList.indexOf(jumpInsn.label));
                }
            } else if (currentInsn instanceof TableSwitchInsnNode) {
                TableSwitchInsnNode tableSwitchInsn = (TableSwitchInsnNode)currentInsn;
                this.findSubroutine(this.insnList.indexOf(tableSwitchInsn.dflt), subroutine, jsrInsns);
                for (int i2 = tableSwitchInsn.labels.size() - 1; i2 >= 0; --i2) {
                    labelNode = tableSwitchInsn.labels.get(i2);
                    instructionIndicesToProcess.add(this.insnList.indexOf(labelNode));
                }
            } else if (currentInsn instanceof LookupSwitchInsnNode) {
                LookupSwitchInsnNode lookupSwitchInsn = (LookupSwitchInsnNode)currentInsn;
                this.findSubroutine(this.insnList.indexOf(lookupSwitchInsn.dflt), subroutine, jsrInsns);
                for (int i3 = lookupSwitchInsn.labels.size() - 1; i3 >= 0; --i3) {
                    labelNode = lookupSwitchInsn.labels.get(i3);
                    instructionIndicesToProcess.add(this.insnList.indexOf(labelNode));
                }
            }
            List<TryCatchBlockNode> insnHandlers = this.handlers[currentInsnIndex];
            if (insnHandlers != null) {
                for (TryCatchBlockNode tryCatchBlock : insnHandlers) {
                    instructionIndicesToProcess.add(this.insnList.indexOf(tryCatchBlock.handler));
                }
            }
            switch (currentInsn.getOpcode()) {
                case 167: 
                case 169: 
                case 170: 
                case 171: 
                case 172: 
                case 173: 
                case 174: 
                case 175: 
                case 176: 
                case 177: 
                case 191: {
                    continue block3;
                }
            }
            instructionIndicesToProcess.add(currentInsnIndex + 1);
        }
    }

    private Frame<V> computeInitialFrame(String owner, MethodNode method) {
        Type[] argumentTypes;
        boolean isInstanceMethod;
        Frame<V> frame = this.newFrame(method.maxLocals, method.maxStack);
        int currentLocal = 0;
        boolean bl2 = isInstanceMethod = (method.access & 8) == 0;
        if (isInstanceMethod) {
            Type ownerType = Type.getObjectType(owner);
            frame.setLocal(currentLocal, this.interpreter.newParameterValue(isInstanceMethod, currentLocal, ownerType));
            ++currentLocal;
        }
        for (Type argumentType : argumentTypes = Type.getArgumentTypes(method.desc)) {
            frame.setLocal(currentLocal, this.interpreter.newParameterValue(isInstanceMethod, currentLocal, argumentType));
            ++currentLocal;
            if (argumentType.getSize() != 2) continue;
            frame.setLocal(currentLocal, this.interpreter.newEmptyValue(currentLocal));
            ++currentLocal;
        }
        while (currentLocal < method.maxLocals) {
            frame.setLocal(currentLocal, this.interpreter.newEmptyValue(currentLocal));
            ++currentLocal;
        }
        frame.setReturn(this.interpreter.newReturnTypeValue(Type.getReturnType(method.desc)));
        return frame;
    }

    public Frame<V>[] getFrames() {
        return this.frames;
    }

    public List<TryCatchBlockNode> getHandlers(int insnIndex) {
        return this.handlers[insnIndex];
    }

    protected void init(String owner, MethodNode method) throws AnalyzerException {
    }

    protected Frame<V> newFrame(int numLocals, int numStack) {
        return new Frame(numLocals, numStack);
    }

    protected Frame<V> newFrame(Frame<? extends V> frame) {
        return new Frame<V>(frame);
    }

    protected void newControlFlowEdge(int insnIndex, int successorIndex) {
    }

    protected boolean newControlFlowExceptionEdge(int insnIndex, int successorIndex) {
        return true;
    }

    protected boolean newControlFlowExceptionEdge(int insnIndex, TryCatchBlockNode tryCatchBlock) {
        return this.newControlFlowExceptionEdge(insnIndex, this.insnList.indexOf(tryCatchBlock.handler));
    }

    private void merge(int insnIndex, Frame<V> frame, Subroutine subroutine) throws AnalyzerException {
        boolean changed;
        Frame<V> oldFrame = this.frames[insnIndex];
        if (oldFrame == null) {
            this.frames[insnIndex] = this.newFrame(frame);
            changed = true;
        } else {
            changed = oldFrame.merge(frame, this.interpreter);
        }
        Subroutine oldSubroutine = this.subroutines[insnIndex];
        if (oldSubroutine == null) {
            if (subroutine != null) {
                this.subroutines[insnIndex] = new Subroutine(subroutine);
                changed = true;
            }
        } else if (subroutine != null) {
            changed |= oldSubroutine.merge(subroutine);
        }
        if (changed && !this.inInstructionsToProcess[insnIndex]) {
            this.inInstructionsToProcess[insnIndex] = true;
            this.instructionsToProcess[this.numInstructionsToProcess++] = insnIndex;
        }
    }

    private void merge(int insnIndex, Frame<V> frameBeforeJsr, Frame<V> frameAfterRet, Subroutine subroutineBeforeJsr, boolean[] localsUsed) throws AnalyzerException {
        boolean changed;
        frameAfterRet.merge(frameBeforeJsr, localsUsed);
        Frame<V> oldFrame = this.frames[insnIndex];
        if (oldFrame == null) {
            this.frames[insnIndex] = this.newFrame(frameAfterRet);
            changed = true;
        } else {
            changed = oldFrame.merge(frameAfterRet, this.interpreter);
        }
        Subroutine oldSubroutine = this.subroutines[insnIndex];
        if (oldSubroutine != null && subroutineBeforeJsr != null) {
            changed |= oldSubroutine.merge(subroutineBeforeJsr);
        }
        if (changed && !this.inInstructionsToProcess[insnIndex]) {
            this.inInstructionsToProcess[insnIndex] = true;
            this.instructionsToProcess[this.numInstructionsToProcess++] = insnIndex;
        }
    }
}

