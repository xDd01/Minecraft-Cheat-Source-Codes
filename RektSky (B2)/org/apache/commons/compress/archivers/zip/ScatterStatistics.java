package org.apache.commons.compress.archivers.zip;

public class ScatterStatistics
{
    private final long compressionElapsed;
    private final long mergingElapsed;
    
    ScatterStatistics(final long compressionElapsed, final long mergingElapsed) {
        this.compressionElapsed = compressionElapsed;
        this.mergingElapsed = mergingElapsed;
    }
    
    public long getCompressionElapsed() {
        return this.compressionElapsed;
    }
    
    public long getMergingElapsed() {
        return this.mergingElapsed;
    }
    
    @Override
    public String toString() {
        return "compressionElapsed=" + this.compressionElapsed + "ms, mergingElapsed=" + this.mergingElapsed + "ms";
    }
}
