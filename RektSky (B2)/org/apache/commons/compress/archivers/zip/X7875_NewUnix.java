package org.apache.commons.compress.archivers.zip;

import java.io.*;
import java.math.*;
import java.util.zip.*;

public class X7875_NewUnix implements ZipExtraField, Cloneable, Serializable
{
    private static final ZipShort HEADER_ID;
    private static final ZipShort ZERO;
    private static final BigInteger ONE_THOUSAND;
    private static final long serialVersionUID = 1L;
    private int version;
    private BigInteger uid;
    private BigInteger gid;
    
    public X7875_NewUnix() {
        this.version = 1;
        this.reset();
    }
    
    @Override
    public ZipShort getHeaderId() {
        return X7875_NewUnix.HEADER_ID;
    }
    
    public long getUID() {
        return ZipUtil.bigToLong(this.uid);
    }
    
    public long getGID() {
        return ZipUtil.bigToLong(this.gid);
    }
    
    public void setUID(final long l) {
        this.uid = ZipUtil.longToBig(l);
    }
    
    public void setGID(final long l) {
        this.gid = ZipUtil.longToBig(l);
    }
    
    @Override
    public ZipShort getLocalFileDataLength() {
        byte[] b = trimLeadingZeroesForceMinLength(this.uid.toByteArray());
        final int uidSize = (b == null) ? 0 : b.length;
        b = trimLeadingZeroesForceMinLength(this.gid.toByteArray());
        final int gidSize = (b == null) ? 0 : b.length;
        return new ZipShort(3 + uidSize + gidSize);
    }
    
    @Override
    public ZipShort getCentralDirectoryLength() {
        return X7875_NewUnix.ZERO;
    }
    
    @Override
    public byte[] getLocalFileDataData() {
        byte[] uidBytes = this.uid.toByteArray();
        byte[] gidBytes = this.gid.toByteArray();
        uidBytes = trimLeadingZeroesForceMinLength(uidBytes);
        final int uidBytesLen = (uidBytes != null) ? uidBytes.length : 0;
        gidBytes = trimLeadingZeroesForceMinLength(gidBytes);
        final int gidBytesLen = (gidBytes != null) ? gidBytes.length : 0;
        final byte[] data = new byte[3 + uidBytesLen + gidBytesLen];
        if (uidBytes != null) {
            ZipUtil.reverse(uidBytes);
        }
        if (gidBytes != null) {
            ZipUtil.reverse(gidBytes);
        }
        int pos = 0;
        data[pos++] = ZipUtil.unsignedIntToSignedByte(this.version);
        data[pos++] = ZipUtil.unsignedIntToSignedByte(uidBytesLen);
        if (uidBytes != null) {
            System.arraycopy(uidBytes, 0, data, pos, uidBytesLen);
        }
        pos += uidBytesLen;
        data[pos++] = ZipUtil.unsignedIntToSignedByte(gidBytesLen);
        if (gidBytes != null) {
            System.arraycopy(gidBytes, 0, data, pos, gidBytesLen);
        }
        return data;
    }
    
    @Override
    public byte[] getCentralDirectoryData() {
        return new byte[0];
    }
    
    @Override
    public void parseFromLocalFileData(final byte[] data, int offset, final int length) throws ZipException {
        this.reset();
        this.version = ZipUtil.signedByteToUnsignedInt(data[offset++]);
        final int uidSize = ZipUtil.signedByteToUnsignedInt(data[offset++]);
        final byte[] uidBytes = new byte[uidSize];
        System.arraycopy(data, offset, uidBytes, 0, uidSize);
        offset += uidSize;
        this.uid = new BigInteger(1, ZipUtil.reverse(uidBytes));
        final int gidSize = ZipUtil.signedByteToUnsignedInt(data[offset++]);
        final byte[] gidBytes = new byte[gidSize];
        System.arraycopy(data, offset, gidBytes, 0, gidSize);
        this.gid = new BigInteger(1, ZipUtil.reverse(gidBytes));
    }
    
    @Override
    public void parseFromCentralDirectoryData(final byte[] buffer, final int offset, final int length) throws ZipException {
    }
    
    private void reset() {
        this.uid = X7875_NewUnix.ONE_THOUSAND;
        this.gid = X7875_NewUnix.ONE_THOUSAND;
    }
    
    @Override
    public String toString() {
        return "0x7875 Zip Extra Field: UID=" + this.uid + " GID=" + this.gid;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof X7875_NewUnix) {
            final X7875_NewUnix xf = (X7875_NewUnix)o;
            return this.version == xf.version && this.uid.equals(xf.uid) && this.gid.equals(xf.gid);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int hc = -1234567 * this.version;
        hc ^= Integer.rotateLeft(this.uid.hashCode(), 16);
        hc ^= this.gid.hashCode();
        return hc;
    }
    
    static byte[] trimLeadingZeroesForceMinLength(final byte[] array) {
        if (array == null) {
            return array;
        }
        int pos = 0;
        for (final byte b : array) {
            if (b != 0) {
                break;
            }
            ++pos;
        }
        final int MIN_LENGTH = 1;
        final byte[] trimmedArray = new byte[Math.max(1, array.length - pos)];
        final int startPos = trimmedArray.length - (array.length - pos);
        System.arraycopy(array, pos, trimmedArray, startPos, trimmedArray.length - startPos);
        return trimmedArray;
    }
    
    static {
        HEADER_ID = new ZipShort(30837);
        ZERO = new ZipShort(0);
        ONE_THOUSAND = BigInteger.valueOf(1000L);
    }
}
