package org.apache.commons.compress;

import java.io.*;

public class MemoryLimitException extends IOException
{
    private static final long serialVersionUID = 1L;
    private final long memoryNeededInKb;
    private final int memoryLimitInKb;
    
    public MemoryLimitException(final long memoryNeededInKb, final int memoryLimitInKb) {
        super(buildMessage(memoryNeededInKb, memoryLimitInKb));
        this.memoryNeededInKb = memoryNeededInKb;
        this.memoryLimitInKb = memoryLimitInKb;
    }
    
    public MemoryLimitException(final long memoryNeededInKb, final int memoryLimitInKb, final Exception e) {
        super(buildMessage(memoryNeededInKb, memoryLimitInKb), e);
        this.memoryNeededInKb = memoryNeededInKb;
        this.memoryLimitInKb = memoryLimitInKb;
    }
    
    public long getMemoryNeededInKb() {
        return this.memoryNeededInKb;
    }
    
    public int getMemoryLimitInKb() {
        return this.memoryLimitInKb;
    }
    
    private static String buildMessage(final long memoryNeededInKb, final int memoryLimitInKb) {
        return memoryNeededInKb + " kb of memory would be needed; limit was " + memoryLimitInKb + " kb. If the file is not corrupt, consider increasing the memory limit.";
    }
}
