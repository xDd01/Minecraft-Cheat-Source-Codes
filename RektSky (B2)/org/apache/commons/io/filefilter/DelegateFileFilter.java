package org.apache.commons.io.filefilter;

import java.io.*;

public class DelegateFileFilter extends AbstractFileFilter implements Serializable
{
    private static final long serialVersionUID = -8723373124984771318L;
    private final FilenameFilter filenameFilter;
    private final FileFilter fileFilter;
    
    public DelegateFileFilter(final FilenameFilter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("The FilenameFilter must not be null");
        }
        this.filenameFilter = filter;
        this.fileFilter = null;
    }
    
    public DelegateFileFilter(final FileFilter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("The FileFilter must not be null");
        }
        this.fileFilter = filter;
        this.filenameFilter = null;
    }
    
    @Override
    public boolean accept(final File file) {
        if (this.fileFilter != null) {
            return this.fileFilter.accept(file);
        }
        return super.accept(file);
    }
    
    @Override
    public boolean accept(final File dir, final String name) {
        if (this.filenameFilter != null) {
            return this.filenameFilter.accept(dir, name);
        }
        return super.accept(dir, name);
    }
    
    @Override
    public String toString() {
        final String delegate = (this.fileFilter != null) ? this.fileFilter.toString() : this.filenameFilter.toString();
        return super.toString() + "(" + delegate + ")";
    }
}
