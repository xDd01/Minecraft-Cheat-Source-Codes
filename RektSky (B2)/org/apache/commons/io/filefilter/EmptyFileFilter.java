package org.apache.commons.io.filefilter;

import java.io.*;

public class EmptyFileFilter extends AbstractFileFilter implements Serializable
{
    private static final long serialVersionUID = 3631422087512832211L;
    public static final IOFileFilter EMPTY;
    public static final IOFileFilter NOT_EMPTY;
    
    protected EmptyFileFilter() {
    }
    
    @Override
    public boolean accept(final File file) {
        if (file.isDirectory()) {
            final File[] files = file.listFiles();
            return files == null || files.length == 0;
        }
        return file.length() == 0L;
    }
    
    static {
        EMPTY = new EmptyFileFilter();
        NOT_EMPTY = new NotFileFilter(EmptyFileFilter.EMPTY);
    }
}
