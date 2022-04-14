/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import java.util.Collections;
import java.util.Comparator;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

public class TryCatchBlockSorter
extends MethodNode {
    public TryCatchBlockSorter(MethodVisitor methodVisitor, int access, String name, String descriptor, String signature, String[] exceptions) {
        this(589824, methodVisitor, access, name, descriptor, signature, exceptions);
        if (this.getClass() != TryCatchBlockSorter.class) {
            throw new IllegalStateException();
        }
    }

    protected TryCatchBlockSorter(int api2, MethodVisitor methodVisitor, int access, String name, String descriptor, String signature, String[] exceptions) {
        super(api2, access, name, descriptor, signature, exceptions);
        this.mv = methodVisitor;
    }

    public void visitEnd() {
        Collections.sort(this.tryCatchBlocks, new Comparator<TryCatchBlockNode>(){

            @Override
            public int compare(TryCatchBlockNode tryCatchBlockNode1, TryCatchBlockNode tryCatchBlockNode2) {
                return this.blockLength(tryCatchBlockNode1) - this.blockLength(tryCatchBlockNode2);
            }

            private int blockLength(TryCatchBlockNode tryCatchBlockNode) {
                int startIndex = TryCatchBlockSorter.this.instructions.indexOf(tryCatchBlockNode.start);
                int endIndex = TryCatchBlockSorter.this.instructions.indexOf(tryCatchBlockNode.end);
                return endIndex - startIndex;
            }
        });
        for (int i2 = 0; i2 < this.tryCatchBlocks.size(); ++i2) {
            ((TryCatchBlockNode)this.tryCatchBlocks.get(i2)).updateIndex(i2);
        }
        if (this.mv != null) {
            this.accept(this.mv);
        }
    }
}

