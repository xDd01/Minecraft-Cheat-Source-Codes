package org.apache.commons.compress.archivers.zip;

public class X0015_CertificateIdForFile extends PKWareExtraHeader
{
    private int rcount;
    private HashAlgorithm hashAlg;
    
    public X0015_CertificateIdForFile() {
        super(new ZipShort(21));
    }
    
    public int getRecordCount() {
        return this.rcount;
    }
    
    public HashAlgorithm getHashAlgorithm() {
        return this.hashAlg;
    }
    
    @Override
    public void parseFromCentralDirectoryData(final byte[] data, final int offset, final int length) {
        super.parseFromCentralDirectoryData(data, offset, length);
        this.rcount = ZipShort.getValue(data, offset);
        this.hashAlg = HashAlgorithm.getAlgorithmByCode(ZipShort.getValue(data, offset + 2));
    }
}
