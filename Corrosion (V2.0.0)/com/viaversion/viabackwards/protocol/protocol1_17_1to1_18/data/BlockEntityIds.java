/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_17_1to1_18.data;

import java.util.Arrays;

public final class BlockEntityIds {
    private static final int[] IDS;

    public static int mappedId(int id2) {
        if (id2 < 0 || id2 > IDS.length) {
            return -1;
        }
        return IDS[id2];
    }

    static {
        int[] ids = com.viaversion.viaversion.protocols.protocol1_18to1_17_1.BlockEntityIds.getIds();
        IDS = new int[Arrays.stream(ids).max().getAsInt() + 1];
        Arrays.fill(IDS, -1);
        for (int i2 = 0; i2 < ids.length; ++i2) {
            int id2 = ids[i2];
            if (id2 == -1) continue;
            BlockEntityIds.IDS[id2] = i2;
        }
    }
}

