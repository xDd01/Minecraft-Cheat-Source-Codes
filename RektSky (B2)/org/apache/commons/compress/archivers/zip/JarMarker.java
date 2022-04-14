package org.apache.commons.compress.archivers.zip;

import java.util.zip.*;

public final class JarMarker implements ZipExtraField
{
    private static final ZipShort ID;
    private static final ZipShort NULL;
    private static final byte[] NO_BYTES;
    private static final JarMarker DEFAULT;
    
    public static JarMarker getInstance() {
        return JarMarker.DEFAULT;
    }
    
    @Override
    public ZipShort getHeaderId() {
        return JarMarker.ID;
    }
    
    @Override
    public ZipShort getLocalFileDataLength() {
        return JarMarker.NULL;
    }
    
    @Override
    public ZipShort getCentralDirectoryLength() {
        return JarMarker.NULL;
    }
    
    @Override
    public byte[] getLocalFileDataData() {
        return JarMarker.NO_BYTES;
    }
    
    @Override
    public byte[] getCentralDirectoryData() {
        return JarMarker.NO_BYTES;
    }
    
    @Override
    public void parseFromLocalFileData(final byte[] data, final int offset, final int length) throws ZipException {
        if (length != 0) {
            throw new ZipException("JarMarker doesn't expect any data");
        }
    }
    
    @Override
    public void parseFromCentralDirectoryData(final byte[] buffer, final int offset, final int length) throws ZipException {
        this.parseFromLocalFileData(buffer, offset, length);
    }
    
    static {
        ID = new ZipShort(51966);
        NULL = new ZipShort(0);
        NO_BYTES = new byte[0];
        DEFAULT = new JarMarker();
    }
}
