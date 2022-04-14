package org.apache.commons.io.filefilter;

import java.io.*;

public class FalseFileFilter implements IOFileFilter, Serializable
{
    private static final long serialVersionUID = 6210271677940926200L;
    public static final IOFileFilter FALSE;
    public static final IOFileFilter INSTANCE;
    
    protected FalseFileFilter() {
    }
    
    @Override
    public boolean accept(final File file) {
        return false;
    }
    
    @Override
    public boolean accept(final File dir, final String name) {
        return false;
    }
    
    static {
        FALSE = new FalseFileFilter();
        INSTANCE = FalseFileFilter.FALSE;
    }
}
