package org.apache.commons.compress.archivers.zip;

import java.util.zip.*;
import java.util.*;

public class X000A_NTFS implements ZipExtraField
{
    private static final ZipShort HEADER_ID;
    private static final ZipShort TIME_ATTR_TAG;
    private static final ZipShort TIME_ATTR_SIZE;
    private ZipEightByteInteger modifyTime;
    private ZipEightByteInteger accessTime;
    private ZipEightByteInteger createTime;
    private static final long EPOCH_OFFSET = -116444736000000000L;
    
    public X000A_NTFS() {
        this.modifyTime = ZipEightByteInteger.ZERO;
        this.accessTime = ZipEightByteInteger.ZERO;
        this.createTime = ZipEightByteInteger.ZERO;
    }
    
    @Override
    public ZipShort getHeaderId() {
        return X000A_NTFS.HEADER_ID;
    }
    
    @Override
    public ZipShort getLocalFileDataLength() {
        return new ZipShort(32);
    }
    
    @Override
    public ZipShort getCentralDirectoryLength() {
        return this.getLocalFileDataLength();
    }
    
    @Override
    public byte[] getLocalFileDataData() {
        final byte[] data = new byte[this.getLocalFileDataLength().getValue()];
        int pos = 4;
        System.arraycopy(X000A_NTFS.TIME_ATTR_TAG.getBytes(), 0, data, pos, 2);
        pos += 2;
        System.arraycopy(X000A_NTFS.TIME_ATTR_SIZE.getBytes(), 0, data, pos, 2);
        pos += 2;
        System.arraycopy(this.modifyTime.getBytes(), 0, data, pos, 8);
        pos += 8;
        System.arraycopy(this.accessTime.getBytes(), 0, data, pos, 8);
        pos += 8;
        System.arraycopy(this.createTime.getBytes(), 0, data, pos, 8);
        return data;
    }
    
    @Override
    public byte[] getCentralDirectoryData() {
        return this.getLocalFileDataData();
    }
    
    @Override
    public void parseFromLocalFileData(final byte[] data, int offset, final int length) throws ZipException {
        ZipShort size;
        for (final int len = offset + length, offset += 4; offset + 4 <= len; offset += 2 + size.getValue()) {
            final ZipShort tag = new ZipShort(data, offset);
            offset += 2;
            if (tag.equals(X000A_NTFS.TIME_ATTR_TAG)) {
                this.readTimeAttr(data, offset, len - offset);
                break;
            }
            size = new ZipShort(data, offset);
        }
    }
    
    @Override
    public void parseFromCentralDirectoryData(final byte[] buffer, final int offset, final int length) throws ZipException {
        this.reset();
        this.parseFromLocalFileData(buffer, offset, length);
    }
    
    public ZipEightByteInteger getModifyTime() {
        return this.modifyTime;
    }
    
    public ZipEightByteInteger getAccessTime() {
        return this.accessTime;
    }
    
    public ZipEightByteInteger getCreateTime() {
        return this.createTime;
    }
    
    public Date getModifyJavaTime() {
        return zipToDate(this.modifyTime);
    }
    
    public Date getAccessJavaTime() {
        return zipToDate(this.accessTime);
    }
    
    public Date getCreateJavaTime() {
        return zipToDate(this.createTime);
    }
    
    public void setModifyTime(final ZipEightByteInteger t) {
        this.modifyTime = ((t == null) ? ZipEightByteInteger.ZERO : t);
    }
    
    public void setAccessTime(final ZipEightByteInteger t) {
        this.accessTime = ((t == null) ? ZipEightByteInteger.ZERO : t);
    }
    
    public void setCreateTime(final ZipEightByteInteger t) {
        this.createTime = ((t == null) ? ZipEightByteInteger.ZERO : t);
    }
    
    public void setModifyJavaTime(final Date d) {
        this.setModifyTime(dateToZip(d));
    }
    
    public void setAccessJavaTime(final Date d) {
        this.setAccessTime(dateToZip(d));
    }
    
    public void setCreateJavaTime(final Date d) {
        this.setCreateTime(dateToZip(d));
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("0x000A Zip Extra Field:").append(" Modify:[").append(this.getModifyJavaTime()).append("] ").append(" Access:[").append(this.getAccessJavaTime()).append("] ").append(" Create:[").append(this.getCreateJavaTime()).append("] ");
        return buf.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof X000A_NTFS) {
            final X000A_NTFS xf = (X000A_NTFS)o;
            return (this.modifyTime == xf.modifyTime || (this.modifyTime != null && this.modifyTime.equals(xf.modifyTime))) && (this.accessTime == xf.accessTime || (this.accessTime != null && this.accessTime.equals(xf.accessTime))) && (this.createTime == xf.createTime || (this.createTime != null && this.createTime.equals(xf.createTime)));
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int hc = -123;
        if (this.modifyTime != null) {
            hc ^= this.modifyTime.hashCode();
        }
        if (this.accessTime != null) {
            hc ^= Integer.rotateLeft(this.accessTime.hashCode(), 11);
        }
        if (this.createTime != null) {
            hc ^= Integer.rotateLeft(this.createTime.hashCode(), 22);
        }
        return hc;
    }
    
    private void reset() {
        this.modifyTime = ZipEightByteInteger.ZERO;
        this.accessTime = ZipEightByteInteger.ZERO;
        this.createTime = ZipEightByteInteger.ZERO;
    }
    
    private void readTimeAttr(final byte[] data, int offset, final int length) {
        if (length >= 26) {
            final ZipShort tagValueLength = new ZipShort(data, offset);
            if (X000A_NTFS.TIME_ATTR_SIZE.equals(tagValueLength)) {
                offset += 2;
                this.modifyTime = new ZipEightByteInteger(data, offset);
                offset += 8;
                this.accessTime = new ZipEightByteInteger(data, offset);
                offset += 8;
                this.createTime = new ZipEightByteInteger(data, offset);
            }
        }
    }
    
    private static ZipEightByteInteger dateToZip(final Date d) {
        if (d == null) {
            return null;
        }
        return new ZipEightByteInteger(d.getTime() * 10000L + 116444736000000000L);
    }
    
    private static Date zipToDate(final ZipEightByteInteger z) {
        if (z == null || ZipEightByteInteger.ZERO.equals(z)) {
            return null;
        }
        final long l = (z.getLongValue() - 116444736000000000L) / 10000L;
        return new Date(l);
    }
    
    static {
        HEADER_ID = new ZipShort(10);
        TIME_ATTR_TAG = new ZipShort(1);
        TIME_ATTR_SIZE = new ZipShort(24);
    }
}
