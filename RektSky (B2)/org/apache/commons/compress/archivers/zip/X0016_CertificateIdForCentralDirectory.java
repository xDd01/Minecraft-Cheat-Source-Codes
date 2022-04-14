package org.apache.commons.compress.archivers.zip;

public class X0016_CertificateIdForCentralDirectory extends PKWareExtraHeader
{
    private int rcount;
    private HashAlgorithm hashAlg;
    
    public X0016_CertificateIdForCentralDirectory() {
        super(new ZipShort(22));
    }
    
    public int getRecordCount() {
        return this.rcount;
    }
    
    public HashAlgorithm getHashAlgorithm() {
        return this.hashAlg;
    }
    
    @Override
    public void parseFromCentralDirectoryData(final byte[] data, final int offset, final int length) {
        this.rcount = ZipShort.getValue(data, offset);
        this.hashAlg = HashAlgorithm.getAlgorithmByCode(ZipShort.getValue(data, offset + 2));
    }
}
