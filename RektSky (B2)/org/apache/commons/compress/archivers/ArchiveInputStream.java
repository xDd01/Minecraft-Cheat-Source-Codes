package org.apache.commons.compress.archivers;

import java.io.*;

public abstract class ArchiveInputStream extends InputStream
{
    private final byte[] single;
    private static final int BYTE_MASK = 255;
    private long bytesRead;
    
    public ArchiveInputStream() {
        this.single = new byte[1];
        this.bytesRead = 0L;
    }
    
    public abstract ArchiveEntry getNextEntry() throws IOException;
    
    @Override
    public int read() throws IOException {
        final int num = this.read(this.single, 0, 1);
        return (num == -1) ? -1 : (this.single[0] & 0xFF);
    }
    
    protected void count(final int read) {
        this.count((long)read);
    }
    
    protected void count(final long read) {
        if (read != -1L) {
            this.bytesRead += read;
        }
    }
    
    protected void pushedBackBytes(final long pushedBack) {
        this.bytesRead -= pushedBack;
    }
    
    @Deprecated
    public int getCount() {
        return (int)this.bytesRead;
    }
    
    public long getBytesRead() {
        return this.bytesRead;
    }
    
    public boolean canReadEntryData(final ArchiveEntry archiveEntry) {
        return true;
    }
}
