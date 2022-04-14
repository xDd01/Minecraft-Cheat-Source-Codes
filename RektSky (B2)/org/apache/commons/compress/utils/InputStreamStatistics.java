package org.apache.commons.compress.utils;

public interface InputStreamStatistics
{
    long getCompressedCount();
    
    long getUncompressedCount();
}
