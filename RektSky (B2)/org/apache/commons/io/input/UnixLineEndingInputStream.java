package org.apache.commons.io.input;

import java.io.*;

public class UnixLineEndingInputStream extends InputStream
{
    private boolean slashNSeen;
    private boolean slashRSeen;
    private boolean eofSeen;
    private final InputStream target;
    private final boolean ensureLineFeedAtEndOfFile;
    
    public UnixLineEndingInputStream(final InputStream in, final boolean ensureLineFeedAtEndOfFile) {
        this.slashNSeen = false;
        this.slashRSeen = false;
        this.eofSeen = false;
        this.target = in;
        this.ensureLineFeedAtEndOfFile = ensureLineFeedAtEndOfFile;
    }
    
    private int readWithUpdate() throws IOException {
        final int target = this.target.read();
        this.eofSeen = (target == -1);
        if (this.eofSeen) {
            return target;
        }
        this.slashNSeen = (target == 10);
        this.slashRSeen = (target == 13);
        return target;
    }
    
    @Override
    public int read() throws IOException {
        final boolean previousWasSlashR = this.slashRSeen;
        if (this.eofSeen) {
            return this.eofGame(previousWasSlashR);
        }
        final int target = this.readWithUpdate();
        if (this.eofSeen) {
            return this.eofGame(previousWasSlashR);
        }
        if (this.slashRSeen) {
            return 10;
        }
        if (previousWasSlashR && this.slashNSeen) {
            return this.read();
        }
        return target;
    }
    
    private int eofGame(final boolean previousWasSlashR) {
        if (previousWasSlashR || !this.ensureLineFeedAtEndOfFile) {
            return -1;
        }
        if (!this.slashNSeen) {
            this.slashNSeen = true;
            return 10;
        }
        return -1;
    }
    
    @Override
    public void close() throws IOException {
        super.close();
        this.target.close();
    }
    
    @Override
    public synchronized void mark(final int readlimit) {
        throw new UnsupportedOperationException("Mark notsupported");
    }
}
