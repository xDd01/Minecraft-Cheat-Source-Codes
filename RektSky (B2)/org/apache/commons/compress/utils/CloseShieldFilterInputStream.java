package org.apache.commons.compress.utils;

import java.io.*;

public class CloseShieldFilterInputStream extends FilterInputStream
{
    public CloseShieldFilterInputStream(final InputStream in) {
        super(in);
    }
    
    @Override
    public void close() throws IOException {
    }
}
