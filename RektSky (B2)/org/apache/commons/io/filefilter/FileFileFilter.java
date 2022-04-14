package org.apache.commons.io.filefilter;

import java.io.*;

public class FileFileFilter extends AbstractFileFilter implements Serializable
{
    private static final long serialVersionUID = 5345244090827540862L;
    public static final IOFileFilter FILE;
    
    protected FileFileFilter() {
    }
    
    @Override
    public boolean accept(final File file) {
        return file.isFile();
    }
    
    static {
        FILE = new FileFileFilter();
    }
}
