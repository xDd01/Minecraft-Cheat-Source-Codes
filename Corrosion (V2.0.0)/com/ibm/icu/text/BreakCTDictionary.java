/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.text;

import com.ibm.icu.impl.ICUBinary;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.CharacterIterator;

class BreakCTDictionary {
    private CompactTrieHeader fData;
    private CompactTrieNodes[] nodes;
    private static final byte[] DATA_FORMAT_ID = new byte[]{84, 114, 68, 99};

    private CompactTrieNodes getCompactTrieNode(int node) {
        return this.nodes[node];
    }

    public BreakCTDictionary(InputStream is2) throws IOException {
        ICUBinary.readHeader(is2, DATA_FORMAT_ID, null);
        DataInputStream in2 = new DataInputStream(is2);
        this.fData = new CompactTrieHeader();
        this.fData.size = in2.readInt();
        this.fData.magic = in2.readInt();
        this.fData.nodeCount = in2.readShort();
        this.fData.root = in2.readShort();
        this.loadBreakCTDictionary(in2);
    }

    private void loadBreakCTDictionary(DataInputStream in2) throws IOException {
        for (int i2 = 0; i2 < this.fData.nodeCount; ++i2) {
            in2.readInt();
        }
        this.nodes = new CompactTrieNodes[this.fData.nodeCount];
        this.nodes[0] = new CompactTrieNodes();
        for (int j2 = 1; j2 < this.fData.nodeCount; ++j2) {
            boolean isVerticalNode;
            this.nodes[j2] = new CompactTrieNodes();
            this.nodes[j2].flagscount = in2.readShort();
            int count = this.nodes[j2].flagscount & 0xFFF;
            if (count == 0) continue;
            boolean bl2 = isVerticalNode = (this.nodes[j2].flagscount & 0x1000) != 0;
            if (isVerticalNode) {
                this.nodes[j2].vnode = new CompactTrieVerticalNode();
                this.nodes[j2].vnode.equal = in2.readShort();
                this.nodes[j2].vnode.chars = new char[count];
                for (int l2 = 0; l2 < count; ++l2) {
                    this.nodes[j2].vnode.chars[l2] = in2.readChar();
                }
                continue;
            }
            this.nodes[j2].hnode = new CompactTrieHorizontalNode[count];
            for (int n2 = 0; n2 < count; ++n2) {
                this.nodes[j2].hnode[n2] = new CompactTrieHorizontalNode(in2.readChar(), in2.readShort());
            }
        }
    }

    public int matches(CharacterIterator text, int maxLength, int[] lengths, int[] count, int limit) {
        CompactTrieNodes node = this.getCompactTrieNode(this.fData.root);
        int mycount = 0;
        char uc2 = text.current();
        int i2 = 0;
        boolean exitFlag = false;
        block0: while (node != null) {
            int nodeCount;
            if (limit > 0 && (node.flagscount & 0x2000) != 0) {
                lengths[mycount++] = i2;
                --limit;
            }
            if (i2 >= maxLength || (nodeCount = node.flagscount & 0xFFF) == 0) break;
            if ((node.flagscount & 0x1000) != 0) {
                CompactTrieVerticalNode vnode = node.vnode;
                for (int j2 = 0; j2 < nodeCount && i2 < maxLength; ++i2, ++j2) {
                    if (uc2 != vnode.chars[j2]) {
                        exitFlag = true;
                        break;
                    }
                    text.next();
                    uc2 = text.current();
                }
                if (exitFlag) break;
                node = this.getCompactTrieNode(vnode.equal);
                continue;
            }
            CompactTrieHorizontalNode[] hnode = node.hnode;
            int low = 0;
            int high = nodeCount - 1;
            node = null;
            while (high >= low) {
                int middle = high + low >>> 1;
                if (uc2 == hnode[middle].ch) {
                    node = this.getCompactTrieNode(hnode[middle].equal);
                    text.next();
                    uc2 = text.current();
                    ++i2;
                    continue block0;
                }
                if (uc2 < hnode[middle].ch) {
                    high = middle - 1;
                    continue;
                }
                low = middle + 1;
            }
        }
        count[0] = mycount;
        return i2;
    }

    static class CompactTrieNodes {
        short flagscount = 0;
        CompactTrieHorizontalNode[] hnode = null;
        CompactTrieVerticalNode vnode = null;

        CompactTrieNodes() {
        }
    }

    static class CompactTrieVerticalNode {
        int equal = 0;
        char[] chars = null;

        CompactTrieVerticalNode() {
        }
    }

    static class CompactTrieHorizontalNode {
        char ch;
        int equal;

        CompactTrieHorizontalNode(char newCh, int newEqual) {
            this.ch = newCh;
            this.equal = newEqual;
        }
    }

    static final class CompactTrieNodeFlags {
        static final int kVerticalNode = 4096;
        static final int kParentEndsWord = 8192;
        static final int kReservedFlag1 = 16384;
        static final int kReservedFlag2 = 32768;
        static final int kCountMask = 4095;
        static final int kFlagMask = 61440;

        CompactTrieNodeFlags() {
        }
    }

    static class CompactTrieHeader {
        int size = 0;
        int magic = 0;
        int nodeCount = 0;
        int root = 0;
        int[] offset = null;

        CompactTrieHeader() {
        }
    }
}

