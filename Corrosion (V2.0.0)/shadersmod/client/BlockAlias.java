/*
 * Decompiled with CFR 0.152.
 */
package shadersmod.client;

import optifine.MatchBlock;

public class BlockAlias {
    private int blockId;
    private MatchBlock[] matchBlocks;

    public BlockAlias(int blockId, MatchBlock[] matchBlocks) {
        this.blockId = blockId;
        this.matchBlocks = matchBlocks;
    }

    public int getBlockId() {
        return this.blockId;
    }

    public boolean matches(int id2, int metadata) {
        for (int i2 = 0; i2 < this.matchBlocks.length; ++i2) {
            MatchBlock matchblock = this.matchBlocks[i2];
            if (!matchblock.matches(id2, metadata)) continue;
            return true;
        }
        return false;
    }

    public int[] getMatchBlockIds() {
        int[] aint = new int[this.matchBlocks.length];
        for (int i2 = 0; i2 < aint.length; ++i2) {
            aint[i2] = this.matchBlocks[i2].getBlockId();
        }
        return aint;
    }
}

