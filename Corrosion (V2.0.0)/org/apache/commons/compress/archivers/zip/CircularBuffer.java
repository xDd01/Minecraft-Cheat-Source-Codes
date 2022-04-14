/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.archivers.zip;

class CircularBuffer {
    private final int size;
    private final byte[] buffer;
    private int readIndex;
    private int writeIndex;

    CircularBuffer(int size) {
        this.size = size;
        this.buffer = new byte[size];
    }

    public boolean available() {
        return this.readIndex != this.writeIndex;
    }

    public void put(int value) {
        this.buffer[this.writeIndex] = (byte)value;
        this.writeIndex = (this.writeIndex + 1) % this.size;
    }

    public int get() {
        if (this.available()) {
            byte value = this.buffer[this.readIndex];
            this.readIndex = (this.readIndex + 1) % this.size;
            return value & 0xFF;
        }
        return -1;
    }

    public void copy(int distance, int length) {
        int pos1 = this.writeIndex - distance;
        int pos2 = pos1 + length;
        for (int i2 = pos1; i2 < pos2; ++i2) {
            this.buffer[this.writeIndex] = this.buffer[(i2 + this.size) % this.size];
            this.writeIndex = (this.writeIndex + 1) % this.size;
        }
    }
}

