package org.apache.commons.io.input;

import java.security.*;
import java.io.*;

public class MessageDigestCalculatingInputStream extends ObservableInputStream
{
    private final MessageDigest messageDigest;
    
    public MessageDigestCalculatingInputStream(final InputStream pStream, final MessageDigest pDigest) {
        super(pStream);
        this.messageDigest = pDigest;
        this.add(new MessageDigestMaintainingObserver(pDigest));
    }
    
    public MessageDigestCalculatingInputStream(final InputStream pStream, final String pAlgorithm) throws NoSuchAlgorithmException {
        this(pStream, MessageDigest.getInstance(pAlgorithm));
    }
    
    public MessageDigestCalculatingInputStream(final InputStream pStream) throws NoSuchAlgorithmException {
        this(pStream, MessageDigest.getInstance("MD5"));
    }
    
    public MessageDigest getMessageDigest() {
        return this.messageDigest;
    }
    
    public static class MessageDigestMaintainingObserver extends Observer
    {
        private final MessageDigest md;
        
        public MessageDigestMaintainingObserver(final MessageDigest pMd) {
            this.md = pMd;
        }
        
        @Override
        void data(final int pByte) throws IOException {
            this.md.update((byte)pByte);
        }
        
        @Override
        void data(final byte[] pBuffer, final int pOffset, final int pLength) throws IOException {
            this.md.update(pBuffer, pOffset, pLength);
        }
    }
}
