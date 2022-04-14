package org.apache.commons.compress.utils;

import java.io.*;

public class SkipShieldingInputStream extends FilterInputStream
{
    private static final int SKIP_BUFFER_SIZE = 8192;
    private static final byte[] SKIP_BUFFER;
    
    public SkipShieldingInputStream(final InputStream in) {
        super(in);
    }
    
    @Override
    public long skip(final long n) throws IOException {
        return (n < 0L) ? 0L : this.read(SkipShieldingInputStream.SKIP_BUFFER, 0, (int)Math.min(n, 8192L));
    }
    
    static {
        SKIP_BUFFER = new byte[8192];
    }
}
