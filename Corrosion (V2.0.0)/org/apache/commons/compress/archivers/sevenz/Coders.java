/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.tukaani.xz.ARMOptions
 *  org.tukaani.xz.ARMThumbOptions
 *  org.tukaani.xz.FilterOptions
 *  org.tukaani.xz.FinishableOutputStream
 *  org.tukaani.xz.FinishableWrapperOutputStream
 *  org.tukaani.xz.IA64Options
 *  org.tukaani.xz.LZMAInputStream
 *  org.tukaani.xz.PowerPCOptions
 *  org.tukaani.xz.SPARCOptions
 *  org.tukaani.xz.X86Options
 */
package org.apache.commons.compress.archivers.sevenz;

import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import org.apache.commons.compress.archivers.sevenz.AES256SHA256Decoder;
import org.apache.commons.compress.archivers.sevenz.Coder;
import org.apache.commons.compress.archivers.sevenz.CoderBase;
import org.apache.commons.compress.archivers.sevenz.DeltaDecoder;
import org.apache.commons.compress.archivers.sevenz.LZMA2Decoder;
import org.apache.commons.compress.archivers.sevenz.SevenZMethod;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.tukaani.xz.ARMOptions;
import org.tukaani.xz.ARMThumbOptions;
import org.tukaani.xz.FilterOptions;
import org.tukaani.xz.FinishableOutputStream;
import org.tukaani.xz.FinishableWrapperOutputStream;
import org.tukaani.xz.IA64Options;
import org.tukaani.xz.LZMAInputStream;
import org.tukaani.xz.PowerPCOptions;
import org.tukaani.xz.SPARCOptions;
import org.tukaani.xz.X86Options;

class Coders {
    private static final Map<SevenZMethod, CoderBase> CODER_MAP = new HashMap<SevenZMethod, CoderBase>(){
        private static final long serialVersionUID = 1664829131806520867L;
        {
            this.put(SevenZMethod.COPY, new CopyDecoder());
            this.put(SevenZMethod.LZMA, new LZMADecoder());
            this.put(SevenZMethod.LZMA2, new LZMA2Decoder());
            this.put(SevenZMethod.DEFLATE, new DeflateDecoder());
            this.put(SevenZMethod.BZIP2, new BZIP2Decoder());
            this.put(SevenZMethod.AES256SHA256, new AES256SHA256Decoder());
            this.put(SevenZMethod.BCJ_X86_FILTER, new BCJDecoder((FilterOptions)new X86Options()));
            this.put(SevenZMethod.BCJ_PPC_FILTER, new BCJDecoder((FilterOptions)new PowerPCOptions()));
            this.put(SevenZMethod.BCJ_IA64_FILTER, new BCJDecoder((FilterOptions)new IA64Options()));
            this.put(SevenZMethod.BCJ_ARM_FILTER, new BCJDecoder((FilterOptions)new ARMOptions()));
            this.put(SevenZMethod.BCJ_ARM_THUMB_FILTER, new BCJDecoder((FilterOptions)new ARMThumbOptions()));
            this.put(SevenZMethod.BCJ_SPARC_FILTER, new BCJDecoder((FilterOptions)new SPARCOptions()));
            this.put(SevenZMethod.DELTA_FILTER, new DeltaDecoder());
        }
    };

    Coders() {
    }

    static CoderBase findByMethod(SevenZMethod method) {
        return CODER_MAP.get((Object)method);
    }

    static InputStream addDecoder(InputStream is2, Coder coder, byte[] password) throws IOException {
        CoderBase cb2 = Coders.findByMethod(SevenZMethod.byId(coder.decompressionMethodId));
        if (cb2 == null) {
            throw new IOException("Unsupported compression method " + Arrays.toString(coder.decompressionMethodId));
        }
        return cb2.decode(is2, coder, password);
    }

    static OutputStream addEncoder(OutputStream out, SevenZMethod method, Object options) throws IOException {
        CoderBase cb2 = Coders.findByMethod(method);
        if (cb2 == null) {
            throw new IOException("Unsupported compression method " + (Object)((Object)method));
        }
        return cb2.encode(out, options);
    }

    private static class DummyByteAddingInputStream
    extends FilterInputStream {
        private boolean addDummyByte = true;

        private DummyByteAddingInputStream(InputStream in2) {
            super(in2);
        }

        public int read() throws IOException {
            int result = super.read();
            if (result == -1 && this.addDummyByte) {
                this.addDummyByte = false;
                result = 0;
            }
            return result;
        }

        public int read(byte[] b2, int off, int len) throws IOException {
            int result = super.read(b2, off, len);
            if (result == -1 && this.addDummyByte) {
                this.addDummyByte = false;
                b2[off] = 0;
                return 1;
            }
            return result;
        }
    }

    static class BZIP2Decoder
    extends CoderBase {
        BZIP2Decoder() {
            super(Number.class);
        }

        InputStream decode(InputStream in2, Coder coder, byte[] password) throws IOException {
            return new BZip2CompressorInputStream(in2);
        }

        OutputStream encode(OutputStream out, Object options) throws IOException {
            int blockSize = BZIP2Decoder.numberOptionOrDefault(options, 9);
            return new BZip2CompressorOutputStream(out, blockSize);
        }
    }

    static class DeflateDecoder
    extends CoderBase {
        DeflateDecoder() {
            super(Number.class);
        }

        InputStream decode(InputStream in2, Coder coder, byte[] password) throws IOException {
            return new InflaterInputStream(new DummyByteAddingInputStream(in2), new Inflater(true));
        }

        OutputStream encode(OutputStream out, Object options) {
            int level = DeflateDecoder.numberOptionOrDefault(options, 9);
            return new DeflaterOutputStream(out, new Deflater(level, true));
        }
    }

    static class BCJDecoder
    extends CoderBase {
        private final FilterOptions opts;

        BCJDecoder(FilterOptions opts) {
            super(new Class[0]);
            this.opts = opts;
        }

        InputStream decode(InputStream in2, Coder coder, byte[] password) throws IOException {
            try {
                return this.opts.getInputStream(in2);
            }
            catch (AssertionError e2) {
                IOException ex2 = new IOException("BCJ filter needs XZ for Java > 1.4 - see http://commons.apache.org/proper/commons-compress/limitations.html#7Z");
                ex2.initCause((Throwable)((Object)e2));
                throw ex2;
            }
        }

        OutputStream encode(OutputStream out, Object options) {
            FinishableOutputStream fo2 = this.opts.getOutputStream((FinishableOutputStream)new FinishableWrapperOutputStream(out));
            return new FilterOutputStream((OutputStream)fo2){

                public void flush() {
                }
            };
        }
    }

    static class LZMADecoder
    extends CoderBase {
        LZMADecoder() {
            super(new Class[0]);
        }

        InputStream decode(InputStream in2, Coder coder, byte[] password) throws IOException {
            byte propsByte = coder.properties[0];
            long dictSize = coder.properties[1];
            for (int i2 = 1; i2 < 4; ++i2) {
                dictSize |= ((long)coder.properties[i2 + 1] & 0xFFL) << 8 * i2;
            }
            if (dictSize > 0x7FFFFFF0L) {
                throw new IOException("Dictionary larger than 4GiB maximum size");
            }
            return new LZMAInputStream(in2, -1L, propsByte, (int)dictSize);
        }
    }

    static class CopyDecoder
    extends CoderBase {
        CopyDecoder() {
            super(new Class[0]);
        }

        InputStream decode(InputStream in2, Coder coder, byte[] password) throws IOException {
            return in2;
        }

        OutputStream encode(OutputStream out, Object options) {
            return out;
        }
    }
}

