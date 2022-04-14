package org.apache.commons.compress.archivers;

public interface EntryStreamOffsets
{
    public static final long OFFSET_UNKNOWN = -1L;
    
    long getDataOffset();
    
    boolean isStreamContiguous();
}
