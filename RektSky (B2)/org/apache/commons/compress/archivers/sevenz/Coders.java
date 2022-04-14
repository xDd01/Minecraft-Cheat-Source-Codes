package org.apache.commons.compress.archivers.sevenz;

import java.util.*;
import org.tukaani.xz.*;
import org.apache.commons.compress.utils.*;
import java.io.*;
import java.util.zip.*;
import org.apache.commons.compress.compressors.deflate64.*;
import org.apache.commons.compress.compressors.bzip2.*;

class Coders
{
    private static final Map<SevenZMethod, CoderBase> CODER_MAP;
    
    static CoderBase findByMethod(final SevenZMethod method) {
        return Coders.CODER_MAP.get(method);
    }
    
    static InputStream addDecoder(final String archiveName, final InputStream is, final long uncompressedLength, final Coder coder, final byte[] password) throws IOException {
        final CoderBase cb = findByMethod(SevenZMethod.byId(coder.decompressionMethodId));
        if (cb == null) {
            throw new IOException("Unsupported compression method " + Arrays.toString(coder.decompressionMethodId) + " used in " + archiveName);
        }
        return cb.decode(archiveName, is, uncompressedLength, coder, password);
    }
    
    static OutputStream addEncoder(final OutputStream out, final SevenZMethod method, final Object options) throws IOException {
        final CoderBase cb = findByMethod(method);
        if (cb == null) {
            throw new IOException("Unsupported compression method " + method);
        }
        return cb.encode(out, options);
    }
    
    static {
        CODER_MAP = new HashMap<SevenZMethod, CoderBase>() {
            private static final long serialVersionUID = 1664829131806520867L;
            
            {
                ((HashMap<SevenZMethod, CopyDecoder>)this).put(SevenZMethod.COPY, new CopyDecoder());
                ((HashMap<SevenZMethod, LZMADecoder>)this).put(SevenZMethod.LZMA, new LZMADecoder());
                ((HashMap<SevenZMethod, LZMA2Decoder>)this).put(SevenZMethod.LZMA2, new LZMA2Decoder());
                ((HashMap<SevenZMethod, DeflateDecoder>)this).put(SevenZMethod.DEFLATE, new DeflateDecoder());
                ((HashMap<SevenZMethod, Deflate64Decoder>)this).put(SevenZMethod.DEFLATE64, new Deflate64Decoder());
                ((HashMap<SevenZMethod, BZIP2Decoder>)this).put(SevenZMethod.BZIP2, new BZIP2Decoder());
                ((HashMap<SevenZMethod, AES256SHA256Decoder>)this).put(SevenZMethod.AES256SHA256, new AES256SHA256Decoder());
                ((HashMap<SevenZMethod, BCJDecoder>)this).put(SevenZMethod.BCJ_X86_FILTER, new BCJDecoder((FilterOptions)new X86Options()));
                ((HashMap<SevenZMethod, BCJDecoder>)this).put(SevenZMethod.BCJ_PPC_FILTER, new BCJDecoder((FilterOptions)new PowerPCOptions()));
                ((HashMap<SevenZMethod, BCJDecoder>)this).put(SevenZMethod.BCJ_IA64_FILTER, new BCJDecoder((FilterOptions)new IA64Options()));
                ((HashMap<SevenZMethod, BCJDecoder>)this).put(SevenZMethod.BCJ_ARM_FILTER, new BCJDecoder((FilterOptions)new ARMOptions()));
                ((HashMap<SevenZMethod, BCJDecoder>)this).put(SevenZMethod.BCJ_ARM_THUMB_FILTER, new BCJDecoder((FilterOptions)new ARMThumbOptions()));
                ((HashMap<SevenZMethod, BCJDecoder>)this).put(SevenZMethod.BCJ_SPARC_FILTER, new BCJDecoder((FilterOptions)new SPARCOptions()));
                ((HashMap<SevenZMethod, DeltaDecoder>)this).put(SevenZMethod.DELTA_FILTER, new DeltaDecoder());
            }
        };
    }
    
    static class CopyDecoder extends CoderBase
    {
        CopyDecoder() {
            super((Class<?>[])new Class[0]);
        }
        
        @Override
        InputStream decode(final String archiveName, final InputStream in, final long uncompressedLength, final Coder coder, final byte[] password) throws IOException {
            return in;
        }
        
        @Override
        OutputStream encode(final OutputStream out, final Object options) {
            return out;
        }
    }
    
    static class BCJDecoder extends CoderBase
    {
        private final FilterOptions opts;
        
        BCJDecoder(final FilterOptions opts) {
            super((Class<?>[])new Class[0]);
            this.opts = opts;
        }
        
        @Override
        InputStream decode(final String archiveName, final InputStream in, final long uncompressedLength, final Coder coder, final byte[] password) throws IOException {
            try {
                return this.opts.getInputStream(in);
            }
            catch (AssertionError e) {
                throw new IOException("BCJ filter used in " + archiveName + " needs XZ for Java > 1.4 - see https://commons.apache.org/proper/commons-compress/limitations.html#7Z", e);
            }
        }
        
        @Override
        OutputStream encode(final OutputStream out, final Object options) {
            return new FlushShieldFilterOutputStream((OutputStream)this.opts.getOutputStream((FinishableOutputStream)new FinishableWrapperOutputStream(out)));
        }
    }
    
    static class DeflateDecoder extends CoderBase
    {
        private static final byte[] ONE_ZERO_BYTE;
        
        DeflateDecoder() {
            super((Class<?>[])new Class[] { Number.class });
        }
        
        @Override
        InputStream decode(final String archiveName, final InputStream in, final long uncompressedLength, final Coder coder, final byte[] password) throws IOException {
            final Inflater inflater = new Inflater(true);
            final InflaterInputStream inflaterInputStream = new InflaterInputStream(new SequenceInputStream(in, new ByteArrayInputStream(DeflateDecoder.ONE_ZERO_BYTE)), inflater);
            return new DeflateDecoderInputStream(inflaterInputStream, inflater);
        }
        
        @Override
        OutputStream encode(final OutputStream out, final Object options) {
            final int level = CoderBase.numberOptionOrDefault(options, 9);
            final Deflater deflater = new Deflater(level, true);
            final DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(out, deflater);
            return new DeflateDecoderOutputStream(deflaterOutputStream, deflater);
        }
        
        static {
            ONE_ZERO_BYTE = new byte[1];
        }
        
        static class DeflateDecoderInputStream extends InputStream
        {
            InflaterInputStream inflaterInputStream;
            Inflater inflater;
            
            public DeflateDecoderInputStream(final InflaterInputStream inflaterInputStream, final Inflater inflater) {
                this.inflaterInputStream = inflaterInputStream;
                this.inflater = inflater;
            }
            
            @Override
            public int read() throws IOException {
                return this.inflaterInputStream.read();
            }
            
            @Override
            public int read(final byte[] b, final int off, final int len) throws IOException {
                return this.inflaterInputStream.read(b, off, len);
            }
            
            @Override
            public int read(final byte[] b) throws IOException {
                return this.inflaterInputStream.read(b);
            }
            
            @Override
            public void close() throws IOException {
                try {
                    this.inflaterInputStream.close();
                }
                finally {
                    this.inflater.end();
                }
            }
        }
        
        static class DeflateDecoderOutputStream extends OutputStream
        {
            DeflaterOutputStream deflaterOutputStream;
            Deflater deflater;
            
            public DeflateDecoderOutputStream(final DeflaterOutputStream deflaterOutputStream, final Deflater deflater) {
                this.deflaterOutputStream = deflaterOutputStream;
                this.deflater = deflater;
            }
            
            @Override
            public void write(final int b) throws IOException {
                this.deflaterOutputStream.write(b);
            }
            
            @Override
            public void write(final byte[] b) throws IOException {
                this.deflaterOutputStream.write(b);
            }
            
            @Override
            public void write(final byte[] b, final int off, final int len) throws IOException {
                this.deflaterOutputStream.write(b, off, len);
            }
            
            @Override
            public void close() throws IOException {
                try {
                    this.deflaterOutputStream.close();
                }
                finally {
                    this.deflater.end();
                }
            }
        }
    }
    
    static class Deflate64Decoder extends CoderBase
    {
        Deflate64Decoder() {
            super((Class<?>[])new Class[] { Number.class });
        }
        
        @Override
        InputStream decode(final String archiveName, final InputStream in, final long uncompressedLength, final Coder coder, final byte[] password) throws IOException {
            return new Deflate64CompressorInputStream(in);
        }
    }
    
    static class BZIP2Decoder extends CoderBase
    {
        BZIP2Decoder() {
            super((Class<?>[])new Class[] { Number.class });
        }
        
        @Override
        InputStream decode(final String archiveName, final InputStream in, final long uncompressedLength, final Coder coder, final byte[] password) throws IOException {
            return new BZip2CompressorInputStream(in);
        }
        
        @Override
        OutputStream encode(final OutputStream out, final Object options) throws IOException {
            final int blockSize = CoderBase.numberOptionOrDefault(options, 9);
            return new BZip2CompressorOutputStream(out, blockSize);
        }
    }
}
