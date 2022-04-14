/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.client.entity;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.entity.DecompressingEntity;

@NotThreadSafe
class LazyDecompressingInputStream
extends InputStream {
    private final InputStream wrappedStream;
    private final DecompressingEntity decompressingEntity;
    private InputStream wrapperStream;

    public LazyDecompressingInputStream(InputStream wrappedStream, DecompressingEntity decompressingEntity) {
        this.wrappedStream = wrappedStream;
        this.decompressingEntity = decompressingEntity;
    }

    private void initWrapper() throws IOException {
        if (this.wrapperStream == null) {
            this.wrapperStream = this.decompressingEntity.decorate(this.wrappedStream);
        }
    }

    public int read() throws IOException {
        this.initWrapper();
        return this.wrapperStream.read();
    }

    public int read(byte[] b2) throws IOException {
        this.initWrapper();
        return this.wrapperStream.read(b2);
    }

    public int read(byte[] b2, int off, int len) throws IOException {
        this.initWrapper();
        return this.wrapperStream.read(b2, off, len);
    }

    public long skip(long n2) throws IOException {
        this.initWrapper();
        return this.wrapperStream.skip(n2);
    }

    public boolean markSupported() {
        return false;
    }

    public int available() throws IOException {
        this.initWrapper();
        return this.wrapperStream.available();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
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

