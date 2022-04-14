package org.apache.commons.io.output;

import java.io.*;

public class ChunkedOutputStream extends FilterOutputStream
{
    private static final int DEFAULT_CHUNK_SIZE = 4096;
    private final int chunkSize;
    
    public ChunkedOutputStream(final OutputStream stream, final int chunkSize) {
        super(stream);
        if (chunkSize <= 0) {
            throw new IllegalArgumentException();
        }
        this.chunkSize = chunkSize;
    }
    
    public ChunkedOutputStream(final OutputStream stream) {
        this(stream, 4096);
    }
    
    @Override
    public void write(final byte[] data, final int srcOffset, final int length) throws IOException {
        int chunk;
        for (int bytes = length, dstOffset = srcOffset; bytes > 0; bytes -= chunk, dstOffset += chunk) {
            chunk = Math.min(bytes, this.chunkSize);
            this.out.write(data, dstOffset, chunk);
        }
    }
}
