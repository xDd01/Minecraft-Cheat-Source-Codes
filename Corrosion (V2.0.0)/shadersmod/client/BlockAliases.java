/*
 * Decompiled with CFR 0.152.
 */
package shadersmod.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import optifine.Config;
import optifine.ConnectedParser;
import optifine.MatchBlock;
import optifine.PropertiesOrdered;
import optifine.StrUtils;
import shadersmod.client.BlockAlias;
import shadersmod.client.IShaderPack;

public class BlockAliases {
    private static BlockAlias[][] blockAliases = null;

    public static int getMappedBlockId(int blockId, int metadata) {
        if (blockAliases == null) {
            return blockId;
        }
        if (blockId >= 0 && blockId < blockAliases.length) {
            BlockAlias[] ablockalias = blockAliases[blockId];
            if (ablockalias == null) {
                return blockId;
            }
            for (int i2 = 0; i2 < ablockalias.length; ++i2) {
                BlockAlias blockalias = ablockalias[i2];
                if (!blockalias.matches(blockId, metadata)) continue;
                return blockalias.getBlockId();
            }
            return blockId;
        }
        return blockId;
    }

    public static void update(IShaderPack shaderPack) {
        BlockAliases.reset();
        String s2 = "/shaders/block.properties";
        try {
            InputStream inputstream = shaderPack.getResourceAsStream(s2);
            if (inputstream == null) {
                return;
            }
            PropertiesOrdered properties = new PropertiesOrdered();
            properties.load(inputstream);
            inputstream.close();
            Config.dbg("[Shaders] Parsing block mappings: " + s2);
            ArrayList<List<BlockAlias>> list = new ArrayList<List<BlockAlias>>();
            ConnectedParser connectedparser = new ConnectedParser("Shaders");
            for (Object s10 : ((Hashtable)properties).keySet()) {
                String s1 = (String)s10;
                String s22 = properties.getProperty(s1);
                String s3 = "block.";
                if (!s1.startsWith(s3)) {
                    Config.warn("[Shaders] Invalid block ID: " + s1);
                    continue;
                }
                String s4 = StrUtils.removePrefix(s1, s3);
                int i2 = Config.parseInt(s4, -1);
                if (i2 < 0) {
                    Config.warn("[Shaders] Invalid block ID: " + s1);
                    continue;
                }
                MatchBlock[] amatchblock = connectedparser.parseMatchBlocks(s22);
                if (amatchblock != null && amatchblock.length >= 1) {
                    BlockAlias blockalias = new BlockAlias(i2, amatchblock);
                    BlockAliases.addToList(list, blockalias);
                    continue;
                }
                Config.warn("[Shaders] Invalid block ID mapping: " + s1 + "=" + s22);
            }
            if (list.size() <= 0) {
                return;
            }
            blockAliases = BlockAliases.toArrays(list);
        }
        catch (IOException var15) {
            Config.warn("[Shaders] Error reading: " + s2);
        }
    }

    private static void addToList(List<List<BlockAlias>> blocksAliases, BlockAlias ba2) {
        int[] aint = ba2.getMatchBlockIds();
        for (int i2 = 0; i2 < aint.length; ++i2) {
            int j2 = aint[i2];
            while (j2 >= blocksAliases.size()) {
                blocksAliases.add(null);
            }
            List<BlockAlias> list = blocksAliases.get(j2);
            if (list == null) {
                list = new ArrayList<BlockAlias>();
                blocksAliases.set(j2, list);
            }
            list.add(ba2);
        }
    }

    private static BlockAlias[][] toArrays(List<List<BlockAlias>> listBlocksAliases) {
        BlockAlias[][] ablockalias = new BlockAlias[listBlocksAliases.size()][];
        for (int i2 = 0; i2 < ablockalias.length; ++i2) {
            List<BlockAlias> list = listBlocksAliases.get(i2);
            if (list == null) continue;
            ablockalias[i2] = list.toArray(new BlockAlias[list.size()]);
        }
        return ablockalias;
    }

    public static void reset() {
        blockAliases = null;
    }
}

