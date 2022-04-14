package org.apache.commons.compress.archivers;

import java.security.*;
import org.apache.commons.compress.archivers.arj.*;
import org.apache.commons.compress.archivers.dump.*;
import org.apache.commons.compress.archivers.ar.*;
import org.apache.commons.compress.archivers.zip.*;
import org.apache.commons.compress.archivers.tar.*;
import org.apache.commons.compress.archivers.jar.*;
import org.apache.commons.compress.archivers.cpio.*;
import org.apache.commons.compress.archivers.sevenz.*;
import java.io.*;
import java.util.*;
import org.apache.commons.compress.utils.*;

public class ArchiveStreamFactory implements ArchiveStreamProvider
{
    private static final int TAR_HEADER_SIZE = 512;
    private static final int DUMP_SIGNATURE_SIZE = 32;
    private static final int SIGNATURE_SIZE = 12;
    private static final ArchiveStreamFactory SINGLETON;
    public static final String AR = "ar";
    public static final String ARJ = "arj";
    public static final String CPIO = "cpio";
    public static final String DUMP = "dump";
    public static final String JAR = "jar";
    public static final String TAR = "tar";
    public static final String ZIP = "zip";
    public static final String SEVEN_Z = "7z";
    private final String encoding;
    private volatile String entryEncoding;
    private SortedMap<String, ArchiveStreamProvider> archiveInputStreamProviders;
    private SortedMap<String, ArchiveStreamProvider> archiveOutputStreamProviders;
    
    private static ArrayList<ArchiveStreamProvider> findArchiveStreamProviders() {
        return Lists.newArrayList((Iterator<? extends ArchiveStreamProvider>)serviceLoaderIterator());
    }
    
    static void putAll(final Set<String> names, final ArchiveStreamProvider provider, final TreeMap<String, ArchiveStreamProvider> map) {
        for (final String name : names) {
            map.put(toKey(name), provider);
        }
    }
    
    private static Iterator<ArchiveStreamProvider> serviceLoaderIterator() {
        return new ServiceLoaderIterator<ArchiveStreamProvider>(ArchiveStreamProvider.class);
    }
    
    private static String toKey(final String name) {
        return name.toUpperCase(Locale.ROOT);
    }
    
    public static SortedMap<String, ArchiveStreamProvider> findAvailableArchiveInputStreamProviders() {
        return AccessController.doPrivileged((PrivilegedAction<SortedMap<String, ArchiveStreamProvider>>)new PrivilegedAction<SortedMap<String, ArchiveStreamProvider>>() {
            @Override
            public SortedMap<String, ArchiveStreamProvider> run() {
                final TreeMap<String, ArchiveStreamProvider> map = new TreeMap<String, ArchiveStreamProvider>();
                ArchiveStreamFactory.putAll(ArchiveStreamFactory.SINGLETON.getInputStreamArchiveNames(), ArchiveStreamFactory.SINGLETON, map);
                for (final ArchiveStreamProvider provider : findArchiveStreamProviders()) {
                    ArchiveStreamFactory.putAll(provider.getInputStreamArchiveNames(), provider, map);
                }
                return map;
            }
        });
    }
    
    public static SortedMap<String, ArchiveStreamProvider> findAvailableArchiveOutputStreamProviders() {
        return AccessController.doPrivileged((PrivilegedAction<SortedMap<String, ArchiveStreamProvider>>)new PrivilegedAction<SortedMap<String, ArchiveStreamProvider>>() {
            @Override
            public SortedMap<String, ArchiveStreamProvider> run() {
                final TreeMap<String, ArchiveStreamProvider> map = new TreeMap<String, ArchiveStreamProvider>();
                ArchiveStreamFactory.putAll(ArchiveStreamFactory.SINGLETON.getOutputStreamArchiveNames(), ArchiveStreamFactory.SINGLETON, map);
                for (final ArchiveStreamProvider provider : findArchiveStreamProviders()) {
                    ArchiveStreamFactory.putAll(provider.getOutputStreamArchiveNames(), provider, map);
                }
                return map;
            }
        });
    }
    
    public ArchiveStreamFactory() {
        this(null);
    }
    
    public ArchiveStreamFactory(final String encoding) {
        this.encoding = encoding;
        this.entryEncoding = encoding;
    }
    
    public String getEntryEncoding() {
        return this.entryEncoding;
    }
    
    @Deprecated
    public void setEntryEncoding(final String entryEncoding) {
        if (this.encoding != null) {
            throw new IllegalStateException("Cannot overide encoding set by the constructor");
        }
        this.entryEncoding = entryEncoding;
    }
    
    public ArchiveInputStream createArchiveInputStream(final String archiverName, final InputStream in) throws ArchiveException {
        return this.createArchiveInputStream(archiverName, in, this.entryEncoding);
    }
    
    @Override
    public ArchiveInputStream createArchiveInputStream(final String archiverName, final InputStream in, final String actualEncoding) throws ArchiveException {
        if (archiverName == null) {
            throw new IllegalArgumentException("Archivername must not be null.");
        }
        if (in == null) {
            throw new IllegalArgumentException("InputStream must not be null.");
        }
        if ("ar".equalsIgnoreCase(archiverName)) {
            return new ArArchiveInputStream(in);
        }
        if ("arj".equalsIgnoreCase(archiverName)) {
            if (actualEncoding != null) {
                return new ArjArchiveInputStream(in, actualEncoding);
            }
            return new ArjArchiveInputStream(in);
        }
        else if ("zip".equalsIgnoreCase(archiverName)) {
            if (actualEncoding != null) {
                return new ZipArchiveInputStream(in, actualEncoding);
            }
            return new ZipArchiveInputStream(in);
        }
        else if ("tar".equalsIgnoreCase(archiverName)) {
            if (actualEncoding != null) {
                return new TarArchiveInputStream(in, actualEncoding);
            }
            return new TarArchiveInputStream(in);
        }
        else if ("jar".equalsIgnoreCase(archiverName)) {
            if (actualEncoding != null) {
                return new JarArchiveInputStream(in, actualEncoding);
            }
            return new JarArchiveInputStream(in);
        }
        else if ("cpio".equalsIgnoreCase(archiverName)) {
            if (actualEncoding != null) {
                return new CpioArchiveInputStream(in, actualEncoding);
            }
            return new CpioArchiveInputStream(in);
        }
        else if ("dump".equalsIgnoreCase(archiverName)) {
            if (actualEncoding != null) {
                return new DumpArchiveInputStream(in, actualEncoding);
            }
            return new DumpArchiveInputStream(in);
        }
        else {
            if ("7z".equalsIgnoreCase(archiverName)) {
                throw new StreamingNotSupportedException("7z");
            }
            final ArchiveStreamProvider archiveStreamProvider = this.getArchiveInputStreamProviders().get(toKey(archiverName));
            if (archiveStreamProvider != null) {
                return archiveStreamProvider.createArchiveInputStream(archiverName, in, actualEncoding);
            }
            throw new ArchiveException("Archiver: " + archiverName + " not found.");
        }
    }
    
    public ArchiveOutputStream createArchiveOutputStream(final String archiverName, final OutputStream out) throws ArchiveException {
        return this.createArchiveOutputStream(archiverName, out, this.entryEncoding);
    }
    
    @Override
    public ArchiveOutputStream createArchiveOutputStream(final String archiverName, final OutputStream out, final String actualEncoding) throws ArchiveException {
        if (archiverName == null) {
            throw new IllegalArgumentException("Archivername must not be null.");
        }
        if (out == null) {
            throw new IllegalArgumentException("OutputStream must not be null.");
        }
        if ("ar".equalsIgnoreCase(archiverName)) {
            return new ArArchiveOutputStream(out);
        }
        if ("zip".equalsIgnoreCase(archiverName)) {
            final ZipArchiveOutputStream zip = new ZipArchiveOutputStream(out);
            if (actualEncoding != null) {
                zip.setEncoding(actualEncoding);
            }
            return zip;
        }
        if ("tar".equalsIgnoreCase(archiverName)) {
            if (actualEncoding != null) {
                return new TarArchiveOutputStream(out, actualEncoding);
            }
            return new TarArchiveOutputStream(out);
        }
        else if ("jar".equalsIgnoreCase(archiverName)) {
            if (actualEncoding != null) {
                return new JarArchiveOutputStream(out, actualEncoding);
            }
            return new JarArchiveOutputStream(out);
        }
        else if ("cpio".equalsIgnoreCase(archiverName)) {
            if (actualEncoding != null) {
                return new CpioArchiveOutputStream(out, actualEncoding);
            }
            return new CpioArchiveOutputStream(out);
        }
        else {
            if ("7z".equalsIgnoreCase(archiverName)) {
                throw new StreamingNotSupportedException("7z");
            }
            final ArchiveStreamProvider archiveStreamProvider = this.getArchiveOutputStreamProviders().get(toKey(archiverName));
            if (archiveStreamProvider != null) {
                return archiveStreamProvider.createArchiveOutputStream(archiverName, out, actualEncoding);
            }
            throw new ArchiveException("Archiver: " + archiverName + " not found.");
        }
    }
    
    public ArchiveInputStream createArchiveInputStream(final InputStream in) throws ArchiveException {
        return this.createArchiveInputStream(detect(in), in);
    }
    
    public static String detect(final InputStream in) throws ArchiveException {
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
            throw new ArchiveException("IOException while reading signature.", e);
        }
        if (ZipArchiveInputStream.matches(signature, signatureLength)) {
            return "zip";
        }
        if (JarArchiveInputStream.matches(signature, signatureLength)) {
            return "jar";
        }
        if (ArArchiveInputStream.matches(signature, signatureLength)) {
            return "ar";
        }
        if (CpioArchiveInputStream.matches(signature, signatureLength)) {
            return "cpio";
        }
        if (ArjArchiveInputStream.matches(signature, signatureLength)) {
            return "arj";
        }
        if (SevenZFile.matches(signature, signatureLength)) {
            return "7z";
        }
        final byte[] dumpsig = new byte[32];
        in.mark(dumpsig.length);
        try {
            signatureLength = IOUtils.readFully(in, dumpsig);
            in.reset();
        }
        catch (IOException e2) {
            throw new ArchiveException("IOException while reading dump signature", e2);
        }
        if (DumpArchiveInputStream.matches(dumpsig, signatureLength)) {
            return "dump";
        }
        final byte[] tarHeader = new byte[512];
        in.mark(tarHeader.length);
        try {
            signatureLength = IOUtils.readFully(in, tarHeader);
            in.reset();
        }
        catch (IOException e3) {
            throw new ArchiveException("IOException while reading tar signature", e3);
        }
        if (TarArchiveInputStream.matches(tarHeader, signatureLength)) {
            return "tar";
        }
        if (signatureLength >= 512) {
            TarArchiveInputStream tais = null;
            try {
                tais = new TarArchiveInputStream(new ByteArrayInputStream(tarHeader));
                if (tais.getNextTarEntry().isCheckSumOK()) {
                    return "tar";
                }
            }
            catch (Exception ex) {}
            finally {
                IOUtils.closeQuietly(tais);
            }
        }
        throw new ArchiveException("No Archiver found for the stream signature");
    }
    
    public SortedMap<String, ArchiveStreamProvider> getArchiveInputStreamProviders() {
        if (this.archiveInputStreamProviders == null) {
            this.archiveInputStreamProviders = Collections.unmodifiableSortedMap((SortedMap<String, ? extends ArchiveStreamProvider>)findAvailableArchiveInputStreamProviders());
        }
        return this.archiveInputStreamProviders;
    }
    
    public SortedMap<String, ArchiveStreamProvider> getArchiveOutputStreamProviders() {
        if (this.archiveOutputStreamProviders == null) {
            this.archiveOutputStreamProviders = Collections.unmodifiableSortedMap((SortedMap<String, ? extends ArchiveStreamProvider>)findAvailableArchiveOutputStreamProviders());
        }
        return this.archiveOutputStreamProviders;
    }
    
    @Override
    public Set<String> getInputStreamArchiveNames() {
        return Sets.newHashSet("ar", "arj", "zip", "tar", "jar", "cpio", "dump", "7z");
    }
    
    @Override
    public Set<String> getOutputStreamArchiveNames() {
        return Sets.newHashSet("ar", "zip", "tar", "jar", "cpio", "7z");
    }
    
    static {
        SINGLETON = new ArchiveStreamFactory();
    }
}
