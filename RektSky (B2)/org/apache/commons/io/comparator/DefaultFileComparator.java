package org.apache.commons.io.comparator;

import java.io.*;
import java.util.*;

public class DefaultFileComparator extends AbstractFileComparator implements Serializable
{
    private static final long serialVersionUID = 3260141861365313518L;
    public static final Comparator<File> DEFAULT_COMPARATOR;
    public static final Comparator<File> DEFAULT_REVERSE;
    
    @Override
    public int compare(final File file1, final File file2) {
        return file1.compareTo(file2);
    }
    
    static {
        DEFAULT_COMPARATOR = new DefaultFileComparator();
        DEFAULT_REVERSE = new ReverseComparator(DefaultFileComparator.DEFAULT_COMPARATOR);
    }
}
