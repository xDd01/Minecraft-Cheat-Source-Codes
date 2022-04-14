package org.apache.commons.compress.compressors;

import java.security.*;
import org.apache.commons.compress.compressors.z.*;
import org.apache.commons.compress.compressors.brotli.*;
import org.apache.commons.compress.compressors.deflate64.*;
import java.io.*;
import org.apache.commons.compress.compressors.gzip.*;
import org.apache.commons.compress.compressors.bzip2.*;
import org.apache.commons.compress.compressors.xz.*;
import org.apache.commons.compress.compressors.pack200.*;
import org.apache.commons.compress.compressors.lzma.*;
import org.apache.commons.compress.compressors.deflate.*;
import org.apache.commons.compress.compressors.snappy.*;
import org.apache.commons.compress.compressors.lz4.*;
import org.apache.commons.compress.compressors.zstandard.*;
import java.util.*;
import org.apache.commons.compress.utils.*;

public class CompressorStreamFactory implements CompressorStreamProvider
{
    private static final CompressorStreamFactory SINGLETON;
    public static final String BROTLI = "br";
    public static final String BZIP2 = "bzip2";
    public static final String GZIP = "gz";
    public static final String PACK200 = "pack200";
    public static final String XZ = "xz";
    public static final String LZMA = "lzma";
    public static final String SNAPPY_FRAMED = "snappy-framed";
    public static final String SNAPPY_RAW = "snappy-raw";
    public static final String Z = "z";
    public static final String DEFLATE = "deflate";
    public static final String DEFLATE64 = "deflate64";
    public static final String LZ4_BLOCK = "lz4-block";
    public static final String LZ4_FRAMED = "lz4-framed";
    public static final String ZSTANDARD = "zstd";
    private static final String YOU_NEED_BROTLI_DEC;
    private static final String YOU_NEED_XZ_JAVA;
    private static final String YOU_NEED_ZSTD_JNI;
    private final Boolean decompressUntilEOF;
    private SortedMap<String, CompressorStreamProvider> compressorInputStreamProviders;
    private SortedMap<String, CompressorStreamProvider> compressorOutputStreamProviders;
    private volatile boolean decompressConcatenated;
    private final int memoryLimitInKb;
    
    private static String youNeed(final String name, final String url) {
        return " In addition to Apache Commons Compress you need the " + name + " library - see " + url;
    }
    
    public static SortedMap<String, CompressorStreamProvider> findAvailableCompressorInputStreamProviders() {
        return AccessController.doPrivileged((PrivilegedAction<SortedMap<String, CompressorStreamProvider>>)new PrivilegedAction<SortedMap<String, CompressorStreamProvider>>() {
            @Override
            public SortedMap<String, CompressorStreamProvider> run() {
                final TreeMap<String, CompressorStreamProvider> map = new TreeMap<String, CompressorStreamProvider>();
                CompressorStreamFactory.putAll(CompressorStreamFactory.SINGLETON.getInputStreamCompressorNames(), CompressorStreamFactory.SINGLETON, map);
                for (final CompressorStreamProvider provider : findCompressorStreamProviders()) {
                    CompressorStreamFactory.putAll(provider.getInputStreamCompressorNames(), provider, map);
                }
                return map;
            }
        });
    }
    
    public static SortedMap<String, CompressorStreamProvider> findAvailableCompressorOutputStreamProviders() {
        return AccessController.doPrivileged((PrivilegedAction<SortedMap<String, CompressorStreamProvider>>)new PrivilegedAction<SortedMap<String, CompressorStreamProvider>>() {
            @Override
            public SortedMap<String, CompressorStreamProvider> run() {
                final TreeMap<String, CompressorStreamProvider> map = new TreeMap<String, CompressorStreamProvider>();
                CompressorStreamFactory.putAll(CompressorStreamFactory.SINGLETON.getOutputStreamCompressorNames(), CompressorStreamFactory.SINGLETON, map);
                for (final CompressorStreamProvider provider : findCompressorStreamProviders()) {
                    CompressorStreamFactory.putAll(provider.getOutputStreamCompressorNames(), provider, map);
                }
                return map;
            }
        });
    }
    
    private static ArrayList<CompressorStreamProvider> findCompressorStreamProviders() {
        return Lists.newArrayList((Iterator<? extends CompressorStreamProvider>)serviceLoaderIterator());
    }
    
    public static String getBrotli() {
        return "br";
    }
    
    public static String getBzip2() {
        return "bzip2";
    }
    
    public static String getDeflate() {
        return "deflate";
    }
    
    public static String getDeflate64() {
        return "deflate64";
    }
    
    public static String getGzip() {
        return "gz";
    }
    
    public static String getLzma() {
        return "lzma";
    }
    
    public static String getPack200() {
        return "pack200";
    }
    
    public static CompressorStreamFactory getSingleton() {
        return CompressorStreamFactory.SINGLETON;
    }
    
    public static String getSnappyFramed() {
        return "snappy-framed";
    }
    
    public static String getSnappyRaw() {
        return "snappy-raw";
    }
    
    public static String getXz() {
        return "xz";
    }
    
    public static String getZ() {
        return "z";
    }
    
    public static String getLZ4Framed() {
        return "lz4-framed";
    }
    
    public static String getLZ4Block() {
        return "lz4-block";
    }
    
    public static String getZstandard() {
        return "zstd";
    }
    
    static void putAll(final Set<String> names, final CompressorStreamProvider provider, final TreeMap<String, CompressorStreamProvider> map) {
        for (final String name : names) {
            map.put(toKey(name), provider);
        }
    }
    
    private static Iterator<CompressorStreamProvider> serviceLoaderIterator() {
        return new ServiceLoaderIterator<CompressorStreamProvider>(CompressorStreamProvider.class);
    }
    
    private static String toKey(final String name) {
        return name.toUpperCase(Locale.ROOT);
    }
    
    public CompressorStreamFactory() {
        this.decompressConcatenated = false;
        this.decompressUntilEOF = null;
        this.memoryLimitInKb = -1;
    }
    
    public CompressorStreamFactory(final boolean decompressUntilEOF, final int memoryLimitInKb) {
        this.decompressConcatenated = false;
        this.decompressUntilEOF = decompressUntilEOF;
        this.decompressConcatenated = decompressUntilEOF;
        this.memoryLimitInKb = memoryLimitInKb;
    }
    
    public CompressorStreamFactory(final boolean decompressUntilEOF) {
        this(decompressUntilEOF, -1);
    }
    
    public static String detect(final InputStream in) throws CompressorException {
        if (in == null) {
            throw new IllegalArgumentException("Stream must not be null.");
        }
        if (!in.markSupported()) {
            throw new IllegalArgumentException("Mark is not supported.");
        }
        final byte[] signature = new byte[12];
        in.mark(signature.length);
        int signatureLength = -1;
        try {
            signatureLength = IOUtils.readFully(in, signature);
            in.reset();
        }
        catch (IOException e) {
            throw new CompressorException("IOException while reading signature.", e);
        }
        if (BZip2CompressorInputStream.matches(signature, signatureLength)) {
            return "bzip2";
        }
        if (GzipCompressorInputStream.matches(signature, signatureLength)) {
            return "gz";
        }
        if (Pack200CompressorInputStream.matches(signature, signatureLength)) {
            return "pack200";
        }
        if (FramedSnappyCompressorInputStream.matches(signature, signatureLength)) {
            return "snappy-framed";
        }
        if (ZCompressorInputStream.matches(signature, signatureLength)) {
            return "z";
        }
        if (DeflateCompressorInputStream.matches(signature, signatureLength)) {
            return "deflate";
        }
        if (XZUtils.matches(signature, signatureLength)) {
            return "xz";
        }
        if (LZMAUtils.matches(signature, signatureLength)) {
            return "lzma";
        }
        if (FramedLZ4CompressorInputStream.matches(signature, signatureLength)) {
            return "lz4-framed";
        }
        if (ZstdUtils.matches(signature, signatureLength)) {
            return "zstd";
        }
        throw new CompressorException("No Compressor found for the stream signature.");
    }
    
    public CompressorInputStream createCompressorInputStream(final InputStream in) throws CompressorException {
        return this.createCompressorInputStream(detect(in), in);
    }
    
    public CompressorInputStream createCompressorInputStream(final String name, final InputStream in) throws CompressorException {
        return this.createCompressorInputStream(name, in, this.decompressConcatenated);
    }
    
    @Override
    public CompressorInputStream createCompressorInputStream(final String name, final InputStream in, final boolean actualDecompressConcatenated) throws CompressorException {
        if (name == null || in == null) {
            throw new IllegalArgumentException("Compressor name and stream must not be null.");
        }
        try {
            if ("gz".equalsIgnoreCase(name)) {
                return new GzipCompressorInputStream(in, actualDecompressConcatenated);
            }
            if ("bzip2".equalsIgnoreCase(name)) {
                return new BZip2CompressorInputStream(in, actualDecompressConcatenated);
            }
            if ("br".equalsIgnoreCase(name)) {
                if (!BrotliUtils.isBrotliCompressionAvailable()) {
                    throw new CompressorException("Brotli compression is not available." + CompressorStreamFactory.YOU_NEED_BROTLI_DEC);
                }
                return new BrotliCompressorInputStream(in);
            }
            else if ("xz".equalsIgnoreCase(name)) {
                if (!XZUtils.isXZCompressionAvailable()) {
                    throw new CompressorException("XZ compression is not available." + CompressorStreamFactory.YOU_NEED_XZ_JAVA);
                }
                return new XZCompressorInputStream(in, actualDecompressConcatenated, this.memoryLimitInKb);
            }
            else if ("zstd".equalsIgnoreCase(name)) {
                if (!ZstdUtils.isZstdCompressionAvailable()) {
                    throw new CompressorException("Zstandard compression is not available." + CompressorStreamFactory.YOU_NEED_ZSTD_JNI);
                }
                return new ZstdCompressorInputStream(in);
            }
            else if ("lzma".equalsIgnoreCase(name)) {
                if (!LZMAUtils.isLZMACompressionAvailable()) {
                    throw new CompressorException("LZMA compression is not available" + CompressorStreamFactory.YOU_NEED_XZ_JAVA);
                }
                return new LZMACompressorInputStream(in, this.memoryLimitInKb);
            }
            else {
                if ("pack200".equalsIgnoreCase(name)) {
                    return new Pack200CompressorInputStream(in);
                }
                if ("snappy-raw".equalsIgnoreCase(name)) {
                    return new SnappyCompressorInputStream(in);
                }
                if ("snappy-framed".equalsIgnoreCase(name)) {
                    return new FramedSnappyCompressorInputStream(in);
                }
                if ("z".equalsIgnoreCase(name)) {
                    return new ZCompressorInputStream(in, this.memoryLimitInKb);
                }
                if ("deflate".equalsIgnoreCase(name)) {
                    return new DeflateCompressorInputStream(in);
                }
                if ("deflate64".equalsIgnoreCase(name)) {
                    return new Deflate64CompressorInputStream(in);
                }
                if ("lz4-block".equalsIgnoreCase(name)) {
                    return new BlockLZ4CompressorInputStream(in);
                }
                if ("lz4-framed".equalsIgnoreCase(name)) {
                    return new FramedLZ4CompressorInputStream(in, actualDecompressConcatenated);
                }
            }
        }
        catch (IOException e) {
            throw new CompressorException("Could not create CompressorInputStream.", e);
        }
        final CompressorStreamProvider compressorStreamProvider = this.getCompressorInputStreamProviders().get(toKey(name));
        if (compressorStreamProvider != null) {
            return compressorStreamProvider.createCompressorInputStream(name, in, actualDecompressConcatenated);
        }
        throw new CompressorException("Compressor: " + name + " not found.");
    }
    
    @Override
    public CompressorOutputStream createCompressorOutputStream(final String name, final OutputStream out) throws CompressorException {
        if (name == null || out == null) {
            throw new IllegalArgumentException("Compressor name and stream must not be null.");
        }
        try {
            if ("gz".equalsIgnoreCase(name)) {
                return new GzipCompressorOutputStream(out);
            }
            if ("bzip2".equalsIgnoreCase(name)) {
                return new BZip2CompressorOutputStream(out);
            }
            if ("xz".equalsIgnoreCase(name)) {
                return new XZCompressorOutputStream(out);
            }
            if ("pack200".equalsIgnoreCase(name)) {
                return new Pack200CompressorOutputStream(out);
            }
            if ("lzma".equalsIgnoreCase(name)) {
                return new LZMACompressorOutputStream(out);
            }
            if ("deflate".equalsIgnoreCase(name)) {
                return new DeflateCompressorOutputStream(out);
            }
            if ("snappy-framed".equalsIgnoreCase(name)) {
                return new FramedSnappyCompressorOutputStream(out);
            }
            if ("lz4-block".equalsIgnoreCase(name)) {
                return new BlockLZ4CompressorOutputStream(out);
            }
            if ("lz4-framed".equalsIgnoreCase(name)) {
                return new FramedLZ4CompressorOutputStream(out);
            }
            if ("zstd".equalsIgnoreCase(name)) {
                return new ZstdCompressorOutputStream(out);
            }
        }
        catch (IOException e) {
            throw new CompressorException("Could not create CompressorOutputStream", e);
        }
        final CompressorStreamProvider compressorStreamProvider = this.getCompressorOutputStreamProviders().get(toKey(name));
        if (compressorStreamProvider != null) {
            return compressorStreamProvider.createCompressorOutputStream(name, out);
        }
        throw new CompressorException("Compressor: " + name + " not found.");
    }
    
    public SortedMap<String, CompressorStreamProvider> getCompressorInputStreamProviders() {
        if (this.compressorInputStreamProviders == null) {
            this.compressorInputStreamProviders = Collections.unmodifiableSortedMap((SortedMap<String, ? extends CompressorStreamProvider>)findAvailableCompressorInputStreamProviders());
        }
        return this.compressorInputStreamProviders;
    }
    
    public SortedMap<String, CompressorStreamProvider> getCompressorOutputStreamProviders() {
        if (this.compressorOutputStreamProviders == null) {
            this.compressorOutputStreamProviders = Collections.unmodifiableSortedMap((SortedMap<String, ? extends CompressorStreamProvider>)findAvailableCompressorOutputStreamProviders());
        }
        return this.compressorOutputStreamProviders;
    }
    
    boolean getDecompressConcatenated() {
        return this.decompressConcatenated;
    }
    
    public Boolean getDecompressUntilEOF() {
        return this.decompressUntilEOF;
    }
    
    @Override
    public Set<String> getInputStreamCompressorNames() {
        return Sets.newHashSet("gz", "br", "bzip2", "xz", "lzma", "pack200", "deflate", "snappy-raw", "snappy-framed", "z", "lz4-block", "lz4-framed", "zstd", "deflate64");
    }
    
    @Override
    public Set<String> getOutputStreamCompressorNames() {
        return Sets.newHashSet("gz", "bzip2", "xz", "lzma", "pack200", "deflate", "snappy-framed", "lz4-block", "lz4-framed", "zstd");
    }
    
    @Deprecated
    public void setDecompressConcatenated(final boolean decompressConcatenated) {
        if (this.decompressUntilEOF != null) {
            throw new IllegalStateException("Cannot override the setting defined by the constructor");
        }
        this.decompressConcatenated = decompressConcatenated;
    }
    
    static {
        SINGLETON = new CompressorStreamFactory();
        YOU_NEED_BROTLI_DEC = youNeed("Google Brotli Dec", "https://github.com/google/brotli/");
        YOU_NEED_XZ_JAVA = youNeed("XZ for Java", "https://tukaani.org/xz/java.html");
        YOU_NEED_ZSTD_JNI = youNeed("Zstd JNI", "https://github.com/luben/zstd-jni");
    }
}
