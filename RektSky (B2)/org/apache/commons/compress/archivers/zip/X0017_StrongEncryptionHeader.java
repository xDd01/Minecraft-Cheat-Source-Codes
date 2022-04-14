package org.apache.commons.compress.archivers.zip;

public class X0017_StrongEncryptionHeader extends PKWareExtraHeader
{
    private int format;
    private EncryptionAlgorithm algId;
    private int bitlen;
    private int flags;
    private long rcount;
    private HashAlgorithm hashAlg;
    private int hashSize;
    private byte[] ivData;
    private byte[] erdData;
    private byte[] recipientKeyHash;
    private byte[] keyBlob;
    private byte[] vData;
    private byte[] vCRC32;
    
    public X0017_StrongEncryptionHeader() {
        super(new ZipShort(23));
    }
    
    public long getRecordCount() {
        return this.rcount;
    }
    
    public HashAlgorithm getHashAlgorithm() {
        return this.hashAlg;
    }
    
    public EncryptionAlgorithm getEncryptionAlgorithm() {
        return this.algId;
    }
    
    public void parseCentralDirectoryFormat(final byte[] data, final int offset, final int length) {
        this.format = ZipShort.getValue(data, offset);
        this.algId = EncryptionAlgorithm.getAlgorithmByCode(ZipShort.getValue(data, offset + 2));
        this.bitlen = ZipShort.getValue(data, offset + 4);
        this.flags = ZipShort.getValue(data, offset + 6);
        this.rcount = ZipLong.getValue(data, offset + 8);
        if (this.rcount > 0L) {
            this.hashAlg = HashAlgorithm.getAlgorithmByCode(ZipShort.getValue(data, offset + 12));
            this.hashSize = ZipShort.getValue(data, offset + 14);
            for (long i = 0L; i < this.rcount; ++i) {
                for (int j = 0; j < this.hashSize; ++j) {}
            }
        }
    }
    
    public void parseFileFormat(final byte[] data, final int offset, final int length) {
        final int ivSize = ZipShort.getValue(data, offset);
        System.arraycopy(data, offset + 4, this.ivData = new byte[ivSize], 0, ivSize);
        this.format = ZipShort.getValue(data, offset + ivSize + 6);
        this.algId = EncryptionAlgorithm.getAlgorithmByCode(ZipShort.getValue(data, offset + ivSize + 8));
        this.bitlen = ZipShort.getValue(data, offset + ivSize + 10);
        this.flags = ZipShort.getValue(data, offset + ivSize + 12);
        final int erdSize = ZipShort.getValue(data, offset + ivSize + 14);
        System.arraycopy(data, offset + ivSize + 16, this.erdData = new byte[erdSize], 0, erdSize);
        this.rcount = ZipLong.getValue(data, offset + ivSize + 16 + erdSize);
        System.out.println("rcount: " + this.rcount);
        if (this.rcount == 0L) {
            final int vSize = ZipShort.getValue(data, offset + ivSize + 20 + erdSize);
            this.vData = new byte[vSize - 4];
            this.vCRC32 = new byte[4];
            System.arraycopy(data, offset + ivSize + 22 + erdSize, this.vData, 0, vSize - 4);
            System.arraycopy(data, offset + ivSize + 22 + erdSize + vSize - 4, this.vCRC32, 0, 4);
        }
        else {
            this.hashAlg = HashAlgorithm.getAlgorithmByCode(ZipShort.getValue(data, offset + ivSize + 20 + erdSize));
            this.hashSize = ZipShort.getValue(data, offset + ivSize + 22 + erdSize);
            final int resize = ZipShort.getValue(data, offset + ivSize + 24 + erdSize);
            this.recipientKeyHash = new byte[this.hashSize];
            this.keyBlob = new byte[resize - this.hashSize];
            System.arraycopy(data, offset + ivSize + 24 + erdSize, this.recipientKeyHash, 0, this.hashSize);
            System.arraycopy(data, offset + ivSize + 24 + erdSize + this.hashSize, this.keyBlob, 0, resize - this.hashSize);
            final int vSize2 = ZipShort.getValue(data, offset + ivSize + 26 + erdSize + resize);
            this.vData = new byte[vSize2 - 4];
            this.vCRC32 = new byte[4];
            System.arraycopy(data, offset + ivSize + 22 + erdSize + resize, this.vData, 0, vSize2 - 4);
            System.arraycopy(data, offset + ivSize + 22 + erdSize + resize + vSize2 - 4, this.vCRC32, 0, 4);
        }
    }
    
    @Override
    public void parseFromLocalFileData(final byte[] data, final int offset, final int length) {
        super.parseFromLocalFileData(data, offset, length);
        this.parseFileFormat(data, offset, length);
    }
    
    @Override
    public void parseFromCentralDirectoryData(final byte[] data, final int offset, final int length) {
        super.parseFromCentralDirectoryData(data, offset, length);
        this.parseCentralDirectoryFormat(data, offset, length);
    }
}
