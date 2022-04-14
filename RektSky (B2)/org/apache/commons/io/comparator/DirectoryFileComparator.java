package org.apache.commons.io.comparator;

import java.io.*;
import java.util.*;

public class DirectoryFileComparator extends AbstractFileComparator implements Serializable
{
    private static final long serialVersionUID = 296132640160964395L;
    public static final Comparator<File> DIRECTORY_COMPARATOR;
    public static final Comparator<File> DIRECTORY_REVERSE;
    
    @Override
    public int compare(final File file1, final File file2) {
        return this.getType(file1) - this.getType(file2);
    }
    
    private int getType(final File file) {
        if (file.isDirectory()) {
            return 1;
        }
        return 2;
    }
    
    static {
        DIRECTORY_COMPARATOR = new DirectoryFileComparator();
        DIRECTORY_REVERSE = new ReverseComparator(DirectoryFileComparator.DIRECTORY_COMPARATOR);
    }
}
