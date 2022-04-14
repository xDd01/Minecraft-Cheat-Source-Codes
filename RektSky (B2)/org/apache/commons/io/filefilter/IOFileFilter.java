package org.apache.commons.io.filefilter;

import java.io.*;

public interface IOFileFilter extends FileFilter, FilenameFilter
{
    boolean accept(final File p0);
    
    boolean accept(final File p0, final String p1);
}
