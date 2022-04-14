package org.apache.commons.compress.archivers.zip;

import java.util.zip.*;

public interface ZipExtraField
{
    public static final int EXTRAFIELD_HEADER_SIZE = 4;
    
    ZipShort getHeaderId();
    
    ZipShort getLocalFileDataLength();
    
    ZipShort getCentralDirectoryLength();
    
    byte[] getLocalFileDataData();
    
    byte[] getCentralDirectoryData();
    
    void parseFromLocalFileData(final byte[] p0, final int p1, final int p2) throws ZipException;
    
    void parseFromCentralDirectoryData(final byte[] p0, final int p1, final int p2) throws ZipException;
}
