package org.apache.commons.io.comparator;

import java.util.*;
import java.io.*;

class ReverseComparator extends AbstractFileComparator implements Serializable
{
    private static final long serialVersionUID = -4808255005272229056L;
    private final Comparator<File> delegate;
    
    public ReverseComparator(final Comparator<File> delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("Delegate comparator is missing");
        }
        this.delegate = delegate;
    }
    
    @Override
    public int compare(final File file1, final File file2) {
        return this.delegate.compare(file2, file1);
    }
    
    @Override
    public String toString() {
        return super.toString() + "[" + this.delegate.toString() + "]";
    }
}
