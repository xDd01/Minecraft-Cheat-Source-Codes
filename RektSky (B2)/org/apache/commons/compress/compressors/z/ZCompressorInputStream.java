package org.apache.commons.compress.compressors.z;

import org.apache.commons.compress.compressors.lzw.*;
import java.nio.*;
import java.io.*;

public class ZCompressorInputStream extends LZWInputStream
{
    private static final int MAGIC_1 = 31;
    private static final int MAGIC_2 = 157;
    private static final int BLOCK_MODE_MASK = 128;
    private static final int MAX_CODE_SIZE_MASK = 31;
    private final boolean blockMode;
    private final int maxCodeSize;
    private long totalCodesRead;
    
    public ZCompressorInputStream(final InputStream inputStream, final int memoryLimitInKb) throws IOException {
        super(inputStream, ByteOrder.LITTLE_ENDIAN);
        this.totalCodesRead = 0L;
        final int firstByte = (int)this.in.readBits(8);
        final int secondByte = (int)this.in.readBits(8);
        final int thirdByte = (int)this.in.readBits(8);
        if (firstByte != 31 || secondByte != 157 || thirdByte < 0) {
            throw new IOException("Input is not in .Z format");
        }
        this.blockMode = ((thirdByte & 0x80) != 0x0);
        this.maxCodeSize = (thirdByte & 0x1F);
        if (this.blockMode) {
            this.setClearCode(9);
        }
        this.initializeTables(this.maxCodeSize, memoryLimitInKb);
        this.clearEntries();
    }
    
    public ZCompressorInputStream(final InputStream inputStream) throws IOException {
        this(inputStream, -1);
    }
    
    private void clearEntries() {
        this.setTableSize(256 + (this.blockMode ? 1 : 0));
    }
    
    @Override
    protected int readNextCode() throws IOException {
        final int code = super.readNextCode();
        if (code >= 0) {
            ++this.totalCodesRead;
        }
        return code;
    }
    
    private void reAlignReading() throws IOException {
        long codeReadsToThrowAway = 8L - this.totalCodesRead % 8L;
        if (codeReadsToThrowAway == 8L) {
            codeReadsToThrowAway = 0L;
        }
        for (long i = 0L; i < codeReadsToThrowAway; ++i) {
            this.readNextCode();
        }
        this.in.clearBitCache();
    }
    
    @Override
    protected int addEntry(final int previousCode, final byte character) throws IOException {
        final int maxTableSize = 1 << this.getCodeSize();
        final int r = this.addEntry(previousCode, character, maxTableSize);
        if (this.getTableSize() == maxTableSize && this.getCodeSize() < this.maxCodeSize) {
            this.reAlignReading();
            this.incrementCodeSize();
        }
        return r;
    }
    
    @Override
    protected int decompressNextSymbol() throws IOException {
        final int code = this.readNextCode();
        if (code < 0) {
            return -1;
        }
        if (this.blockMode && code == this.getClearCode()) {
            this.clearEntries();
            this.reAlignReading();
            this.resetCodeSize();
            this.resetPreviousCode();
            return 0;
        }
        boolean addedUnfinishedEntry = false;
        if (code == this.getTableSize()) {
            this.addRepeatOfPreviousCode();
            addedUnfinishedEntry = true;
        }
        else if (code > this.getTableSize()) {
            throw new IOException(String.format("Invalid %d bit code 0x%x", this.getCodeSize(), code));
        }
        return this.expandCodeToOutputStack(code, addedUnfinishedEntry);
    }
    
    public static boolean matches(final byte[] signature, final int length) {
        return length > 3 && signature[0] == 31 && signature[1] == -99;
    }
}
