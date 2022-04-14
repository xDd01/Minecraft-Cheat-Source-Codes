package org.apache.commons.compress.compressors.lzw;

import org.apache.commons.compress.compressors.*;
import org.apache.commons.compress.utils.*;
import java.nio.*;
import java.io.*;
import org.apache.commons.compress.*;

public abstract class LZWInputStream extends CompressorInputStream implements InputStreamStatistics
{
    protected static final int DEFAULT_CODE_SIZE = 9;
    protected static final int UNUSED_PREFIX = -1;
    private final byte[] oneByte;
    protected final BitInputStream in;
    private int clearCode;
    private int codeSize;
    private byte previousCodeFirstChar;
    private int previousCode;
    private int tableSize;
    private int[] prefixes;
    private byte[] characters;
    private byte[] outputStack;
    private int outputStackLocation;
    
    protected LZWInputStream(final InputStream inputStream, final ByteOrder byteOrder) {
        this.oneByte = new byte[1];
        this.clearCode = -1;
        this.codeSize = 9;
        this.previousCode = -1;
        this.in = new BitInputStream(inputStream, byteOrder);
    }
    
    @Override
    public void close() throws IOException {
        this.in.close();
    }
    
    @Override
    public int read() throws IOException {
        final int ret = this.read(this.oneByte);
        if (ret < 0) {
            return ret;
        }
        return 0xFF & this.oneByte[0];
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        int bytesRead = this.readFromStack(b, off, len);
        while (len - bytesRead > 0) {
            final int result = this.decompressNextSymbol();
            if (result < 0) {
                if (bytesRead > 0) {
                    this.count(bytesRead);
                    return bytesRead;
                }
                return result;
            }
            else {
                bytesRead += this.readFromStack(b, off + bytesRead, len - bytesRead);
            }
        }
        this.count(bytesRead);
        return bytesRead;
    }
    
    @Override
    public long getCompressedCount() {
        return this.in.getBytesRead();
    }
    
    protected abstract int decompressNextSymbol() throws IOException;
    
    protected abstract int addEntry(final int p0, final byte p1) throws IOException;
    
    protected void setClearCode(final int codeSize) {
        this.clearCode = 1 << codeSize - 1;
    }
    
    protected void initializeTables(final int maxCodeSize, final int memoryLimitInKb) throws MemoryLimitException {
        if (memoryLimitInKb > -1) {
            final int maxTableSize = 1 << maxCodeSize;
            final long memoryUsageInBytes = maxTableSize * 6L;
            final long memoryUsageInKb = memoryUsageInBytes >> 10;
            if (memoryUsageInKb > memoryLimitInKb) {
                throw new MemoryLimitException(memoryUsageInKb, memoryLimitInKb);
            }
        }
        this.initializeTables(maxCodeSize);
    }
    
    protected void initializeTables(final int maxCodeSize) {
        final int maxTableSize = 1 << maxCodeSize;
        this.prefixes = new int[maxTableSize];
        this.characters = new byte[maxTableSize];
        this.outputStack = new byte[maxTableSize];
        this.outputStackLocation = maxTableSize;
        final int max = 256;
        for (int i = 0; i < 256; ++i) {
            this.prefixes[i] = -1;
            this.characters[i] = (byte)i;
        }
    }
    
    protected int readNextCode() throws IOException {
        if (this.codeSize > 31) {
            throw new IllegalArgumentException("code size must not be bigger than 31");
        }
        return (int)this.in.readBits(this.codeSize);
    }
    
    protected int addEntry(final int previousCode, final byte character, final int maxTableSize) {
        if (this.tableSize < maxTableSize) {
            this.prefixes[this.tableSize] = previousCode;
            this.characters[this.tableSize] = character;
            return this.tableSize++;
        }
        return -1;
    }
    
    protected int addRepeatOfPreviousCode() throws IOException {
        if (this.previousCode == -1) {
            throw new IOException("The first code can't be a reference to its preceding code");
        }
        return this.addEntry(this.previousCode, this.previousCodeFirstChar);
    }
    
    protected int expandCodeToOutputStack(final int code, final boolean addedUnfinishedEntry) throws IOException {
        for (int entry = code; entry >= 0; entry = this.prefixes[entry]) {
            this.outputStack[--this.outputStackLocation] = this.characters[entry];
        }
        if (this.previousCode != -1 && !addedUnfinishedEntry) {
            this.addEntry(this.previousCode, this.outputStack[this.outputStackLocation]);
        }
        this.previousCode = code;
        this.previousCodeFirstChar = this.outputStack[this.outputStackLocation];
        return this.outputStackLocation;
    }
    
    private int readFromStack(final byte[] b, final int off, final int len) {
        final int remainingInStack = this.outputStack.length - this.outputStackLocation;
        if (remainingInStack > 0) {
            final int maxLength = Math.min(remainingInStack, len);
            System.arraycopy(this.outputStack, this.outputStackLocation, b, off, maxLength);
            this.outputStackLocation += maxLength;
            return maxLength;
        }
        return 0;
    }
    
    protected int getCodeSize() {
        return this.codeSize;
    }
    
    protected void resetCodeSize() {
        this.setCodeSize(9);
    }
    
    protected void setCodeSize(final int cs) {
        this.codeSize = cs;
    }
    
    protected void incrementCodeSize() {
        ++this.codeSize;
    }
    
    protected void resetPreviousCode() {
        this.previousCode = -1;
    }
    
    protected int getPrefix(final int offset) {
        return this.prefixes[offset];
    }
    
    protected void setPrefix(final int offset, final int value) {
        this.prefixes[offset] = value;
    }
    
    protected int getPrefixesLength() {
        return this.prefixes.length;
    }
    
    protected int getClearCode() {
        return this.clearCode;
    }
    
    protected int getTableSize() {
        return this.tableSize;
    }
    
    protected void setTableSize(final int newSize) {
        this.tableSize = newSize;
    }
}
