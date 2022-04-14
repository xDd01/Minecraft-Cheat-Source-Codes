package org.apache.commons.io.filefilter;

import java.util.*;
import java.io.*;
import org.apache.commons.io.*;

public class AgeFileFilter extends AbstractFileFilter implements Serializable
{
    private static final long serialVersionUID = -2132740084016138541L;
    private final long cutoff;
    private final boolean acceptOlder;
    
    public AgeFileFilter(final long cutoff) {
        this(cutoff, true);
    }
    
    public AgeFileFilter(final long cutoff, final boolean acceptOlder) {
        this.acceptOlder = acceptOlder;
        this.cutoff = cutoff;
    }
    
    public AgeFileFilter(final Date cutoffDate) {
        this(cutoffDate, true);
    }
    
    public AgeFileFilter(final Date cutoffDate, final boolean acceptOlder) {
        this(cutoffDate.getTime(), acceptOlder);
    }
    
    public AgeFileFilter(final File cutoffReference) {
        this(cutoffReference, true);
    }
    
    public AgeFileFilter(final File cutoffReference, final boolean acceptOlder) {
        this(cutoffReference.lastModified(), acceptOlder);
    }
    
    @Override
    public boolean accept(final File file) {
        final boolean newer = FileUtils.isFileNewer(file, this.cutoff);
        return this.acceptOlder ? (!newer) : newer;
    }
    
    @Override
    public String toString() {
        final String condition = this.acceptOlder ? "<=" : ">";
        return super.toString() + "(" + condition + this.cutoff + ")";
    }
}
