package org.apache.commons.io.filefilter;

import java.io.*;

public class SizeFileFilter extends AbstractFileFilter implements Serializable
{
    private static final long serialVersionUID = 7388077430788600069L;
    private final long size;
    private final boolean acceptLarger;
    
    public SizeFileFilter(final long size) {
        this(size, true);
    }
    
    public SizeFileFilter(final long size, final boolean acceptLarger) {
        if (size < 0L) {
            throw new IllegalArgumentException("The size must be non-negative");
        }
        this.size = size;
        this.acceptLarger = acceptLarger;
    }
    
    @Override
    public boolean accept(final File file) {
        final boolean smaller = file.length() < this.size;
        return this.acceptLarger ? (!smaller) : smaller;
    }
    
    @Override
    public String toString() {
        final String condition = this.acceptLarger ? ">=" : "<";
        return super.toString() + "(" + condition + this.size + ")";
    }
}
