package org.apache.commons.compress.archivers.zip;

import java.util.*;

public abstract class PKWareExtraHeader implements ZipExtraField
{
    private final ZipShort headerId;
    private byte[] localData;
    private byte[] centralData;
    
    protected PKWareExtraHeader(final ZipShort headerId) {
        this.headerId = headerId;
    }
    
    @Override
    public ZipShort getHeaderId() {
        return this.headerId;
    }
    
    public void setLocalFileDataData(final byte[] data) {
        this.localData = ZipUtil.copy(data);
    }
    
    @Override
    public ZipShort getLocalFileDataLength() {
        return new ZipShort((this.localData != null) ? this.localData.length : 0);
    }
    
    @Override
    public byte[] getLocalFileDataData() {
        return ZipUtil.copy(this.localData);
    }
    
    public void setCentralDirectoryData(final byte[] data) {
        this.centralData = ZipUtil.copy(data);
    }
    
    @Override
    public ZipShort getCentralDirectoryLength() {
        if (this.centralData != null) {
            return new ZipShort(this.centralData.length);
        }
        return this.getLocalFileDataLength();
    }
    
    @Override
    public byte[] getCentralDirectoryData() {
        if (this.centralData != null) {
            return ZipUtil.copy(this.centralData);
        }
        return this.getLocalFileDataData();
    }
    
    @Override
    public void parseFromLocalFileData(final byte[] data, final int offset, final int length) {
        final byte[] tmp = new byte[length];
        System.arraycopy(data, offset, tmp, 0, length);
        this.setLocalFileDataData(tmp);
    }
    
    @Override
    public void parseFromCentralDirectoryData(final byte[] data, final int offset, final int length) {
        final byte[] tmp = new byte[length];
        System.arraycopy(data, offset, tmp, 0, length);
        this.setCentralDirectoryData(tmp);
        if (this.localData == null) {
            this.setLocalFileDataData(tmp);
        }
    }
    
    public enum EncryptionAlgorithm
    {
        DES(26113), 
        RC2pre52(26114), 
        TripleDES168(26115), 
        TripleDES192(26121), 
        AES128(26126), 
        AES192(26127), 
        AES256(26128), 
        RC2(26370), 
        RC4(26625), 
        UNKNOWN(65535);
        
        private final int code;
        private static final Map<Integer, EncryptionAlgorithm> codeToEnum;
        
        private EncryptionAlgorithm(final int code) {
            this.code = code;
        }
        
        public int getCode() {
            return this.code;
        }
        
        public static EncryptionAlgorithm getAlgorithmByCode(final int code) {
            return EncryptionAlgorithm.codeToEnum.get(code);
        }
        
        static {
            final Map<Integer, EncryptionAlgorithm> cte = new HashMap<Integer, EncryptionAlgorithm>();
            for (final EncryptionAlgorithm method : values()) {
                cte.put(method.getCode(), method);
            }
            codeToEnum = Collections.unmodifiableMap((Map<? extends Integer, ? extends EncryptionAlgorithm>)cte);
        }
    }
    
    public enum HashAlgorithm
    {
        NONE(0), 
        CRC32(1), 
        MD5(32771), 
        SHA1(32772), 
        RIPEND160(32775), 
        SHA256(32780), 
        SHA384(32781), 
        SHA512(32782);
        
        private final int code;
        private static final Map<Integer, HashAlgorithm> codeToEnum;
        
        private HashAlgorithm(final int code) {
            this.code = code;
        }
        
        public int getCode() {
            return this.code;
        }
        
        public static HashAlgorithm getAlgorithmByCode(final int code) {
            return HashAlgorithm.codeToEnum.get(code);
        }
        
        static {
            final Map<Integer, HashAlgorithm> cte = new HashMap<Integer, HashAlgorithm>();
            for (final HashAlgorithm method : values()) {
                cte.put(method.getCode(), method);
            }
            codeToEnum = Collections.unmodifiableMap((Map<? extends Integer, ? extends HashAlgorithm>)cte);
        }
    }
}
