/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree.analysis;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;

final class Subroutine {
    final LabelNode start;
    final boolean[] localsUsed;
    final List<JumpInsnNode> callers;

    Subroutine(LabelNode start, int maxLocals, JumpInsnNode caller) {
        this.start = start;
        this.localsUsed = new boolean[maxLocals];
        this.callers = new ArrayList<JumpInsnNode>();
        this.callers.add(caller);
    }

    Subroutine(Subroutine subroutine) {
        this.start = subroutine.start;
        this.localsUsed = (boolean[])subroutine.localsUsed.clone();
        this.callers = new ArrayList<JumpInsnNode>(subroutine.callers);
    }

    public boolean merge(Subroutine subroutine) {
        int i2;
        boolean changed = false;
        for (i2 = 0; i2 < this.localsUsed.length; ++i2) {
            if (!subroutine.localsUsed[i2] || this.localsUsed[i2]) continue;
            this.localsUsed[i2] = true;
            changed = true;
        }
        if (subroutine.start == this.start) {
            for (i2 = 0; i2 < subroutine.callers.size(); ++i2) {
                JumpInsnNode caller = subroutine.callers.get(i2);
                if (this.callers.contains(caller)) continue;
                this.callers.add(caller);
                changed = true;
            }
        }
        return changed;
    }
}

