/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree;

import org.objectweb.asm.TypePath;
import org.objectweb.asm.tree.AnnotationNode;

public class TypeAnnotationNode
extends AnnotationNode {
    public int typeRef;
    public TypePath typePath;

    public TypeAnnotationNode(int typeRef, TypePath typePath, String descriptor) {
        this(589824, typeRef, typePath, descriptor);
        if (this.getClass() != TypeAnnotationNode.class) {
            throw new IllegalStateException();
        }
    }

    public TypeAnnotationNode(int api2, int typeRef, TypePath typePath, String descriptor) {
        super(api2, descriptor);
        this.typeRef = typeRef;
        this.typePath = typePath;
    }
}

