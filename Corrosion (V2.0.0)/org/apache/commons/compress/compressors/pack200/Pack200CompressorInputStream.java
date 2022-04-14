/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.compressors.pack200;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.pack200.Pack200Strategy;
import org.apache.commons.compress.compressors.pack200.StreamBridge;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Pack200CompressorInputStream
extends CompressorInputStream {
    private final InputStream originalInput;
    private final StreamBridge streamBridge;
    private static final byte[] CAFE_DOOD = new byte[]{-54, -2, -48, 13};
    private static final int SIG_LENGTH = CAFE_DOOD.length;

    public Pack200CompressorInputStream(InputStream in2) throws IOException {
        this(in2, Pack200Strategy.IN_MEMORY);
    }

    public Pack200CompressorInputStream(InputStream in2, Pack200Strategy mode) throws IOException {
        this(in2, null, mode, null);
    }

    public Pack200CompressorInputStream(InputStream in2, Map<String, String> props) throws IOException {
        this(in2, Pack200Strategy.IN_MEMORY, props);
    }

    public Pack200CompressorInputStream(InputStream in2, Pack200Strategy mode, Map<String, String> props) throws IOException {
        this(in2, null, mode, props);
    }

    public Pack200CompressorInputStream(File f2) throws IOException {
        this(f2, Pack200Strategy.IN_MEMORY);
    }

    public Pack200CompressorInputStream(File f2, Pack200Strategy mode) throws IOException {
        this(null, f2, mode, null);
    }

    public Pack200CompressorInputStream(File f2, Map<String, String> props) throws IOException {
        this(f2, Pack200Strategy.IN_MEMORY, props);
    }

    public Pack200CompressorInputStream(File f2, Pack200Strategy mode, Map<String, String> props) throws IOException {
        this(null, f2, mode, props);
    }

    private Pack200CompressorInputStream(InputStream in2, File f2, Pack200Strategy mode, Map<String, String> props) throws IOException {
        this.originalInput = in2;
        this.streamBridge = mode.newStreamBridge();
        JarOutputStream jarOut = new JarOutputStream(this.streamBridge);
        Pack200.Unpacker u2 = Pack200.newUnpacker();
        if (props != null) {
            u2.properties().putAll(props);
        }
        if (f2 == null) {
            u2.unpack(new FilterInputStream(in2){

                public void close() {
                }
            }, jarOut);
        } else {
            u2.unpack(f2, jarOut);
        }
        jarOut.close();
    }

    @Override
    public int read() throws IOException {
        return this.streamBridge.getInput().read();
    }

    @Override
    public int read(byte[] b2) throws IOException {
        return this.streamBridge.getInput().read(b2);
    }

    @Override
    public int read(byte[] b2, int off, int count) throws IOException {
        return this.streamBridge.getInput().read(b2, off, count);
    }

    @Override
    public int available() throws IOException {
        return this.streamBridge.getInput().available();
    }

    @Override
    public boolean markSupported() {
        try {
            return this.streamBridge.getInput().markSupported();
        }
        catch (IOException ex2) {
            return false;
        }
    }

    @Override
    public void mark(int limit) {
        try {
            this.streamBridge.getInput().mark(limit);
        }
        catch (IOException ex2) {
            throw new RuntimeException(ex2);
        }
    }

    @Override
    public void reset() throws IOException {
        this.streamBridge.getInput().reset();
    }

    @Override
    public long skip(long count) throws IOException {
        return this.streamBridge.getInput().skip(count);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws IOException {
        try {
            this.streamBridge.stop();
        }
        finally {
            if (this.originalInput != null) {
                this.originalInput.close();
            }
        }
    }

    public static boolean matches(byte[] signature, int length) {
        if (length < SIG_LENGTH) {
            return false;
        }
        for (int i2 = 0; i2 < SIG_LENGTH; ++i2) {
            if (signature[i2] == CAFE_DOOD[i2]) continue;
            return false;
        }
        return true;
    }
}

