/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.archivers.zip;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.commons.compress.archivers.zip.BitStream;

class BinaryTree {
    private static final int UNDEFINED = -1;
    private static final int NODE = -2;
    private final int[] tree;

    public BinaryTree(int depth) {
        this.tree = new int[(1 << depth + 1) - 1];
        Arrays.fill(this.tree, -1);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void addLeaf(int node, int path, int depth, int value) {
        if (depth == 0) {
            if (this.tree[node] != -1) throw new IllegalArgumentException("Tree value at index " + node + " has already been assigned (" + this.tree[node] + ")");
            this.tree[node] = value;
            return;
        } else {
            this.tree[node] = -2;
            int nextChild = 2 * node + 1 + (path & 1);
            this.addLeaf(nextChild, path >>> 1, depth - 1, value);
        }
    }

    public int read(BitStream stream) throws IOException {
        int value;
        int bit2;
        int currentIndex = 0;
        while (true) {
            if ((bit2 = stream.nextBit()) == -1) {
                return -1;
            }
            int childIndex = 2 * currentIndex + 1 + bit2;
            value = this.tree[childIndex];
            if (value != -2) break;
            currentIndex = childIndex;
        }
        if (value != -1) {
            return value;
        }
        throw new IOException("The child " + bit2 + " of node at index " + currentIndex + " is not defined");
    }

    static BinaryTree decode(InputStream in2, int totalNumberOfValues) throws IOException {
        int size = in2.read() + 1;
        if (size == 0) {
            throw new IOException("Cannot read the size of the encoded tree, unexpected end of stream");
        }
        byte[] encodedTree = new byte[size];
        new DataInputStream(in2).readFully(encodedTree);
        int maxLength = 0;
        int[] originalBitLengths = new int[totalNumberOfValues];
        int pos = 0;
        for (byte b2 : encodedTree) {
            int numberOfValues = ((b2 & 0xF0) >> 4) + 1;
            int bitLength = (b2 & 0xF) + 1;
            for (int j2 = 0; j2 < numberOfValues; ++j2) {
                originalBitLengths[pos++] = bitLength;
            }
            maxLength = Math.max(maxLength, bitLength);
        }
        int[] permutation = new int[originalBitLengths.length];
        for (int k2 = 0; k2 < permutation.length; ++k2) {
            permutation[k2] = k2;
        }
        int c2 = 0;
        int[] sortedBitLengths = new int[originalBitLengths.length];
        for (int k3 = 0; k3 < originalBitLengths.length; ++k3) {
            for (int l2 = 0; l2 < originalBitLengths.length; ++l2) {
                if (originalBitLengths[l2] != k3) continue;
                sortedBitLengths[c2] = k3;
                permutation[c2] = l2;
                ++c2;
            }
        }
        int code = 0;
        int codeIncrement = 0;
        int lastBitLength = 0;
        int[] codes = new int[totalNumberOfValues];
        for (int i2 = totalNumberOfValues - 1; i2 >= 0; --i2) {
            code += codeIncrement;
            if (sortedBitLengths[i2] != lastBitLength) {
                lastBitLength = sortedBitLengths[i2];
                codeIncrement = 1 << 16 - lastBitLength;
            }
            codes[permutation[i2]] = code;
        }
        BinaryTree tree = new BinaryTree(maxLength);
        for (int k4 = 0; k4 < codes.length; ++k4) {
            int bitLength = originalBitLengths[k4];
            if (bitLength <= 0) continue;
            tree.addLeaf(0, Integer.reverse(codes[k4] << 16), bitLength, k4);
        }
        return tree;
    }
}

