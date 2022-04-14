/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.util;

import com.ibm.icu.impl.Utility;

public final class CompactCharArray
implements Cloneable {
    public static final int UNICODECOUNT = 65536;
    public static final int BLOCKSHIFT = 5;
    static final int BLOCKCOUNT = 32;
    static final int INDEXSHIFT = 11;
    static final int INDEXCOUNT = 2048;
    static final int BLOCKMASK = 31;
    private char[] values;
    private char[] indices;
    private int[] hashes;
    private boolean isCompact;
    char defaultValue;

    public CompactCharArray() {
        this('\u0000');
    }

    public CompactCharArray(char defaultValue) {
        int i2;
        this.values = new char[65536];
        this.indices = new char[2048];
        this.hashes = new int[2048];
        for (i2 = 0; i2 < 65536; ++i2) {
            this.values[i2] = defaultValue;
        }
        for (i2 = 0; i2 < 2048; ++i2) {
            this.indices[i2] = (char)(i2 << 5);
            this.hashes[i2] = 0;
        }
        this.isCompact = false;
        this.defaultValue = defaultValue;
    }

    public CompactCharArray(char[] indexArray, char[] newValues) {
        if (indexArray.length != 2048) {
            throw new IllegalArgumentException("Index out of bounds.");
        }
        for (int i2 = 0; i2 < 2048; ++i2) {
            char index = indexArray[i2];
            if (index >= '\u0000' && index < newValues.length + 32) continue;
            throw new IllegalArgumentException("Index out of bounds.");
        }
        this.indices = indexArray;
        this.values = newValues;
        this.isCompact = true;
    }

    public CompactCharArray(String indexArray, String valueArray) {
        this(Utility.RLEStringToCharArray(indexArray), Utility.RLEStringToCharArray(valueArray));
    }

    public char elementAt(char index) {
        int ix2 = (this.indices[index >> 5] & 0xFFFF) + (index & 0x1F);
        return ix2 >= this.values.length ? this.defaultValue : this.values[ix2];
    }

    public void setElementAt(char index, char value) {
        if (this.isCompact) {
            this.expand();
        }
        this.values[index] = value;
        this.touchBlock(index >> 5, value);
    }

    public void setElementAt(char start, char end, char value) {
        if (this.isCompact) {
            this.expand();
        }
        for (int i2 = start; i2 <= end; ++i2) {
            this.values[i2] = value;
            this.touchBlock(i2 >> 5, value);
        }
    }

    public void compact() {
        this.compact(true);
    }

    public void compact(boolean exhaustive) {
        if (!this.isCompact) {
            int iBlockStart = 0;
            int iUntouched = 65535;
            int newSize = 0;
            char[] target = exhaustive ? new char[65536] : this.values;
            int i2 = 0;
            while (i2 < this.indices.length) {
                this.indices[i2] = 65535;
                boolean touched = this.blockTouched(i2);
                if (!touched && iUntouched != 65535) {
                    this.indices[i2] = iUntouched;
                } else {
                    int jBlockStart = 0;
                    int j2 = 0;
                    while (j2 < i2) {
                        if (this.hashes[i2] == this.hashes[j2] && CompactCharArray.arrayRegionMatches(this.values, iBlockStart, this.values, jBlockStart, 32)) {
                            this.indices[i2] = this.indices[j2];
                        }
                        ++j2;
                        jBlockStart += 32;
                    }
                    if (this.indices[i2] == '\uffff') {
                        int dest = exhaustive ? this.FindOverlappingPosition(iBlockStart, target, newSize) : newSize;
                        int limit = dest + 32;
                        if (limit > newSize) {
                            for (int j3 = newSize; j3 < limit; ++j3) {
                                target[j3] = this.values[iBlockStart + j3 - dest];
                            }
                            newSize = limit;
                        }
                        this.indices[i2] = (char)dest;
                        if (!touched) {
                            iUntouched = (char)jBlockStart;
                        }
                    }
                }
                ++i2;
                iBlockStart += 32;
            }
            char[] result = new char[newSize];
            System.arraycopy(target, 0, result, 0, newSize);
            this.values = result;
            this.isCompact = true;
            this.hashes = null;
        }
    }

    private int FindOverlappingPosition(int start, char[] tempValues, int tempCount) {
        for (int i2 = 0; i2 < tempCount; ++i2) {
            int currentCount = 32;
            if (i2 + 32 > tempCount) {
                currentCount = tempCount - i2;
            }
            if (!CompactCharArray.arrayRegionMatches(this.values, start, tempValues, i2, currentCount)) continue;
            return i2;
        }
        return tempCount;
    }

    static final boolean arrayRegionMatches(char[] source, int sourceStart, char[] target, int targetStart, int len) {
        int sourceEnd = sourceStart + len;
        int delta = targetStart - sourceStart;
        for (int i2 = sourceStart; i2 < sourceEnd; ++i2) {
            if (source[i2] == target[i2 + delta]) continue;
            return false;
        }
        return true;
    }

    private final void touchBlock(int i2, int value) {
        this.hashes[i2] = this.hashes[i2] + (value << 1) | 1;
    }

    private final boolean blockTouched(int i2) {
        return this.hashes[i2] != 0;
    }

    public char[] getIndexArray() {
        return this.indices;
    }

    public char[] getValueArray() {
        return this.values;
    }

    public Object clone() {
        try {
            CompactCharArray other = (CompactCharArray)super.clone();
            other.values = (char[])this.values.clone();
            other.indices = (char[])this.indices.clone();
            if (this.hashes != null) {
                other.hashes = (int[])this.hashes.clone();
            }
            return other;
        }
        catch (CloneNotSupportedException e2) {
            throw new IllegalStateException();
        }
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        CompactCharArray other = (CompactCharArray)obj;
        for (int i2 = 0; i2 < 65536; ++i2) {
            if (this.elementAt((char)i2) == other.elementAt((char)i2)) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = 0;
        int increment = Math.min(3, this.values.length / 16);
        for (int i2 = 0; i2 < this.values.length; i2 += increment) {
            result = result * 37 + this.values[i2];
        }
        return result;
    }

    private void expand() {
        if (this.isCompact) {
            int i2;
            this.hashes = new int[2048];
            char[] tempArray = new char[65536];
            for (i2 = 0; i2 < 65536; ++i2) {
                tempArray[i2] = this.elementAt((char)i2);
            }
            for (i2 = 0; i2 < 2048; ++i2) {
                this.indices[i2] = (char)(i2 << 5);
            }
            this.values = null;
            this.values = tempArray;
            this.isCompact = false;
        }
    }
}

