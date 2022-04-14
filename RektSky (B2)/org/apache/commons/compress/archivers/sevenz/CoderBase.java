package org.apache.commons.compress.archivers.sevenz;

import java.io.*;

abstract class CoderBase
{
    private final Class<?>[] acceptableOptions;
    private static final byte[] NONE;
    
    protected CoderBase(final Class<?>... acceptableOptions) {
        this.acceptableOptions = acceptableOptions;
    }
    
    boolean canAcceptOptions(final Object opts) {
        for (final Class<?> c : this.acceptableOptions) {
            if (c.isInstance(opts)) {
                return true;
            }
        }
        return false;
    }
    
    byte[] getOptionsAsProperties(final Object options) throws IOException {
        return CoderBase.NONE;
    }
    
    Object getOptionsFromCoder(final Coder coder, final InputStream in) throws IOException {
        return null;
    }
    
    abstract InputStream decode(final String p0, final InputStream p1, final long p2, final Coder p3, final byte[] p4) throws IOException;
    
    OutputStream encode(final OutputStream out, final Object options) throws IOException {
        throw new UnsupportedOperationException("method doesn't support writing");
    }
    
    protected static int numberOptionOrDefault(final Object options, final int defaultValue) {
        return (options instanceof Number) ? ((Number)options).intValue() : defaultValue;
    }
    
    static {
        NONE = new byte[0];
    }
}
