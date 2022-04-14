package org.apache.commons.io.filefilter;

import java.io.*;

public class CanWriteFileFilter extends AbstractFileFilter implements Serializable
{
    private static final long serialVersionUID = 5132005214688990379L;
    public static final IOFileFilter CAN_WRITE;
    public static final IOFileFilter CANNOT_WRITE;
    
    protected CanWriteFileFilter() {
    }
    
    @Override
    public boolean accept(final File file) {
        return file.canWrite();
    }
    
    static {
        CAN_WRITE = new CanWriteFileFilter();
        CANNOT_WRITE = new NotFileFilter(CanWriteFileFilter.CAN_WRITE);
    }
}
