package org.apache.commons.compress.compressors.deflate;

public class DeflateParameters
{
    private boolean zlibHeader;
    private int compressionLevel;
    
    public DeflateParameters() {
        this.zlibHeader = true;
        this.compressionLevel = -1;
    }
    
    public boolean withZlibHeader() {
        return this.zlibHeader;
    }
    
    public void setWithZlibHeader(final boolean zlibHeader) {
        this.zlibHeader = zlibHeader;
    }
    
    public int getCompressionLevel() {
        return this.compressionLevel;
    }
    
    public void setCompressionLevel(final int compressionLevel) {
        if (compressionLevel < -1 || compressionLevel > 9) {
            throw new IllegalArgumentException("Invalid Deflate compression level: " + compressionLevel);
        }
        this.compressionLevel = compressionLevel;
    }
}
