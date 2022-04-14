package org.apache.http.client.entity;

import java.io.*;

class LazyDecompressingInputStream extends InputStream
{
    private final InputStream wrappedStream;
    private final InputStreamFactory inputStreamFactory;
    private InputStream wrapperStream;
    
    public LazyDecompressingInputStream(final InputStream wrappedStream, final InputStreamFactory inputStreamFactory) {
        this.wrappedStream = wrappedStream;
        this.inputStreamFactory = inputStreamFactory;
    }
    
    private void initWrapper() throws IOException {
        if (this.wrapperStream == null) {
            this.wrapperStream = this.inputStreamFactory.create(this.wrappedStream);
        }
    }
    
    @Override
    public int read() throws IOException {
        this.initWrapper();
        return this.wrapperStream.read();
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        this.initWrapper();
        return this.wrapperStream.read(b);
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        this.initWrapper();
        return this.wrapperStream.read(b, off, len);
    }
    
    @Override
    public long skip(final long n) throws IOException {
        this.initWrapper();
        return this.wrapperStream.skip(n);
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public int available() throws IOException {
        this.initWrapper();
        return this.wrapperStream.available();
    }
    
    @Override
    public void close() throws IOException {
        try {
            if (this.wrapperStream != null) {
                this.wrapperStream.close();
            }
        }
        finally {
            this.wrappedStream.close();
        }
    }
}
