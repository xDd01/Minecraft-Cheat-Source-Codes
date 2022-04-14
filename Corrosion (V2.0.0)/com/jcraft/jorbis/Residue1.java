/*
 * Decompiled with CFR 0.152.
 */
package com.jcraft.jorbis;

import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Residue0;

class Residue1
extends Residue0 {
    Residue1() {
    }

    int inverse(Block vb2, Object vl2, float[][] in2, int[] nonzero, int ch) {
        int used = 0;
        for (int i2 = 0; i2 < ch; ++i2) {
            if (nonzero[i2] == 0) continue;
            in2[used++] = in2[i2];
        }
        if (used != 0) {
            return Residue1._01inverse(vb2, vl2, in2, used, 1);
        }
        return 0;
    }
}

