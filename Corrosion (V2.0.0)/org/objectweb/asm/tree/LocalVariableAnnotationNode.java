/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree;

import java.util.List;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.TypeAnnotationNode;
import org.objectweb.asm.tree.Util;

public class LocalVariableAnnotationNode
extends TypeAnnotationNode {
    public List<LabelNode> start;
    public List<LabelNode> end;
    public List<Integer> index;

    public LocalVariableAnnotationNode(int typeRef, TypePath typePath, LabelNode[] start, LabelNode[] end, int[] index, String descriptor) {
        this(589824, typeRef, typePath, start, end, index, descriptor);
    }

    public LocalVariableAnnotationNode(int api2, int typeRef, TypePath typePath, LabelNode[] start, LabelNode[] end, int[] index, String descriptor) {
        super(api2, typeRef, typePath, descriptor);
        this.start = Util.asArrayList(start);
        this.end = Util.asArrayList(end);
        this.index = Util.asArrayList(index);
    }

    public void accept(MethodVisitor methodVisitor, boolean visible) {
        Label[] startLabels = new Label[this.start.size()];
        Label[] endLabels = new Label[this.end.size()];
        int[] indices = new int[this.index.size()];
        int n2 = startLabels.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            startLabels[i2] = this.start.get(i2).getLabel();
            endLabels[i2] = this.end.get(i2).getLabel();
            indices[i2] = this.index.get(i2);
        }
        this.accept(methodVisitor.visitLocalVariableAnnotation(this.typeRef, this.typePath, startLabels, endLabels, indices, this.desc, visible));
    }
}

