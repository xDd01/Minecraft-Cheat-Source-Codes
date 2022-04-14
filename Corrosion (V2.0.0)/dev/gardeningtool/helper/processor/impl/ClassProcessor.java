/*
 * Decompiled with CFR 0.152.
 */
package dev.gardeningtool.helper.processor.impl;

import dev.gardeningtool.helper.annotation.Native;
import dev.gardeningtool.helper.processor.Processor;
import org.objectweb.asm.tree.ClassNode;

public class ClassProcessor
extends Processor<ClassNode> {
    @Override
    public boolean process(ClassNode node) {
        if (node.visibleAnnotations == null) {
            return false;
        }
        Class<Native> annotation = Native.class;
        String name = "L" + annotation.getName().replace(".", "/") + ";";
        return node.visibleAnnotations.stream().anyMatch(annotationNode -> name.equals(annotationNode.desc));
    }
}

