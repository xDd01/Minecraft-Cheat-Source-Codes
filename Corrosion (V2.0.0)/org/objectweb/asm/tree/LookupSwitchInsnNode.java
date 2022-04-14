/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree;

import java.util.List;
import java.util.Map;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.Util;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LookupSwitchInsnNode
extends AbstractInsnNode {
    public LabelNode dflt;
    public List<Integer> keys;
    public List<LabelNode> labels;

    public LookupSwitchInsnNode(LabelNode dflt, int[] keys, LabelNode[] labels) {
        super(171);
        this.dflt = dflt;
        this.keys = Util.asArrayList(keys);
        this.labels = Util.asArrayList(labels);
    }

    @Override
    public int getType() {
        return 12;
    }

    @Override
    public void accept(MethodVisitor methodVisitor) {
        int[] keysArray = new int[this.keys.size()];
        int n2 = keysArray.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            keysArray[i2] = this.keys.get(i2);
        }
        Label[] labelsArray = new Label[this.labels.size()];
        int n3 = labelsArray.length;
        for (int i3 = 0; i3 < n3; ++i3) {
            labelsArray[i3] = this.labels.get(i3).getLabel();
        }
        methodVisitor.visitLookupSwitchInsn(this.dflt.getLabel(), keysArray, labelsArray);
        this.acceptAnnotations(methodVisitor);
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> clonedLabels) {
        LookupSwitchInsnNode clone = new LookupSwitchInsnNode(LookupSwitchInsnNode.clone(this.dflt, clonedLabels), null, LookupSwitchInsnNode.clone(this.labels, clonedLabels));
        clone.keys.addAll(this.keys);
        return clone.cloneAnnotations(this);
    }
}

