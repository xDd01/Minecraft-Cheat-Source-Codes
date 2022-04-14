package org.apache.commons.compress.archivers.zip;

import java.util.zip.*;

public class ResourceAlignmentExtraField implements ZipExtraField
{
    public static final ZipShort ID;
    public static final int BASE_SIZE = 2;
    private static final int ALLOW_METHOD_MESSAGE_CHANGE_FLAG = 32768;
    private short alignment;
    private boolean allowMethodChange;
    private int padding;
    
    public ResourceAlignmentExtraField() {
        this.padding = 0;
    }
    
    public ResourceAlignmentExtraField(final int alignment) {
        this(alignment, false);
    }
    
    public ResourceAlignmentExtraField(final int alignment, final boolean allowMethodChange) {
        this(alignment, allowMethodChange, 0);
    }
    
    public ResourceAlignmentExtraField(final int alignment, final boolean allowMethodChange, final int padding) {
        this.padding = 0;
        if (alignment < 0 || alignment > 32767) {
            throw new IllegalArgumentException("Alignment must be between 0 and 0x7fff, was: " + alignment);
        }
        this.alignment = (short)alignment;
        this.allowMethodChange = allowMethodChange;
        this.padding = padding;
    }
    
    public short getAlignment() {
        return this.alignment;
    }
    
    public boolean allowMethodChange() {
        return this.allowMethodChange;
    }
    
    @Override
    public ZipShort getHeaderId() {
        return ResourceAlignmentExtraField.ID;
    }
    
    @Override
    public ZipShort getLocalFileDataLength() {
        return new ZipShort(2 + this.padding);
    }
    
    @Override
    public ZipShort getCentralDirectoryLength() {
        return new ZipShort(2);
    }
    
    @Override
    public byte[] getLocalFileDataData() {
        final byte[] content = new byte[2 + this.padding];
        ZipShort.putShort(this.alignment | (this.allowMethodChange ? 32768 : 0), content, 0);
        return content;
    }
    
    @Override
    public byte[] getCentralDirectoryData() {
        return ZipShort.getBytes(this.alignment | (this.allowMethodChange ? 32768 : 0));
    }
    
    @Override
    public void parseFromLocalFileData(final byte[] buffer, final int offset, final int length) throws ZipException {
        this.parseFromCentralDirectoryData(buffer, offset, length);
        this.padding = length - 2;
    }
    
    @Override
    public void parseFromCentralDirectoryData(final byte[] buffer, final int offset, final int length) throws ZipException {
        if (length < 2) {
            throw new ZipException("Too short content for ResourceAlignmentExtraField (0xa11e): " + length);
        }
        final int alignmentValue = ZipShort.getValue(buffer, offset);
        this.alignment = (short)(alignmentValue & 0x7FFF);
        this.allowMethodChange = ((alignmentValue & 0x8000) != 0x0);
    }
    
    static {
        ID = new ZipShort(41246);
    }
}
