package org.apache.commons.io.comparator;

import java.io.*;
import java.util.*;

abstract class AbstractFileComparator implements Comparator<File>
{
    public File[] sort(final File... files) {
        if (files != null) {
            Arrays.sort(files, this);
        }
        return files;
    }
    
    public List<File> sort(final List<File> files) {
        if (files != null) {
            Collections.sort(files, this);
        }
        return files;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
