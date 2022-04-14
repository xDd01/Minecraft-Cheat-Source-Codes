package org.apache.commons.compress.utils;

import java.io.*;

public class FlushShieldFilterOutputStream extends FilterOutputStream
{
    public FlushShieldFilterOutputStream(final OutputStream out) {
        super(out);
    }
    
    @Override
    public void flush() throws IOException {
    }
}
