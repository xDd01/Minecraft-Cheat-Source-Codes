/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.archivers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.StreamingNotSupportedException;
import org.apache.commons.compress.archivers.ar.ArArchiveInputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;
import org.apache.commons.compress.archivers.arj.ArjArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
import org.apache.commons.compress.archivers.dump.DumpArchiveInputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

public class ArchiveStreamFactory {
    public static final String AR = "ar";
    public static final String ARJ = "arj";
    public static final String CPIO = "cpio";
    public static final String DUMP = "dump";
    public static final String JAR = "jar";
    public static final String TAR = "tar";
    public static final String ZIP = "zip";
    public static final String SEVEN_Z = "7z";
    private String entryEncoding = null;

    public String getEntryEncoding() {
        return this.entryEncoding;
    }

    public void setEntryEncoding(String entryEncoding) {
        this.entryEncoding = entryEncoding;
    }

    public ArchiveInputStream createArchiveInputStream(String archiverName, InputStream in2) throws ArchiveException {
        if (archiverName == null) {
            throw new IllegalArgumentException("Archivername must not be null.");
        }
        if (in2 == null) {
            throw new IllegalArgumentException("InputStream must not be null.");
        }
        if (AR.equalsIgnoreCase(archiverName)) {
            return new ArArchiveInputStream(in2);
        }
        if (ARJ.equalsIgnoreCase(archiverName)) {
            if (this.entryEncoding != null) {
                return new ArjArchiveInputStream(in2, this.entryEncoding);
            }
            return new ArjArchiveInputStream(in2);
        }
        if (ZIP.equalsIgnoreCase(archiverName)) {
            if (this.entryEncoding != null) {
                return new ZipArchiveInputStream(in2, this.entryEncoding);
            }
            return new ZipArchiveInputStream(in2);
        }
        if (TAR.equalsIgnoreCase(archiverName)) {
            if (this.entryEncoding != null) {
                return new TarArchiveInputStream(in2, this.entryEncoding);
            }
            return new TarArchiveInputStream(in2);
        }
        if (JAR.equalsIgnoreCase(archiverName)) {
            return new JarArchiveInputStream(in2);
        }
        if (CPIO.equalsIgnoreCase(archiverName)) {
            if (this.entryEncoding != null) {
                return new CpioArchiveInputStream(in2, this.entryEncoding);
            }
            return new CpioArchiveInputStream(in2);
        }
        if (DUMP.equalsIgnoreCase(archiverName)) {
            if (this.entryEncoding != null) {
                return new DumpArchiveInputStream(in2, this.entryEncoding);
            }
            return new DumpArchiveInputStream(in2);
        }
        if (SEVEN_Z.equalsIgnoreCase(archiverName)) {
            throw new StreamingNotSupportedException(SEVEN_Z);
        }
        throw new ArchiveException("Archiver: " + archiverName + " not found.");
    }

    public ArchiveOutputStream createArchiveOutputStream(String archiverName, OutputStream out) throws ArchiveException {
        if (archiverName == null) {
            throw new IllegalArgumentException("Archivername must not be null.");
        }
        if (out == null) {
            throw new IllegalArgumentException("OutputStream must not be null.");
        }
        if (AR.equalsIgnoreCase(archiverName)) {
            return new ArArchiveOutputStream(out);
        }
        if (ZIP.equalsIgnoreCase(archiverName)) {
            ZipArchiveOutputStream zip = new ZipArchiveOutputStream(out);
            if (this.entryEncoding != null) {
                zip.setEncoding(this.entryEncoding);
            }
            return zip;
        }
        if (TAR.equalsIgnoreCase(archiverName)) {
            if (this.entryEncoding != null) {
                return new TarArchiveOutputStream(out, this.entryEncoding);
            }
            return new TarArchiveOutputStream(out);
        }
        if (JAR.equalsIgnoreCase(archiverName)) {
            return new JarArchiveOutputStream(out);
        }
        if (CPIO.equalsIgnoreCase(archiverName)) {
            if (this.entryEncoding != null) {
                return new CpioArchiveOutputStream(out, this.entryEncoding);
            }
            return new CpioArchiveOutputStream(out);
        }
        if (SEVEN_Z.equalsIgnoreCase(archiverName)) {
            throw new StreamingNotSupportedException(SEVEN_Z);
        }
        throw new ArchiveException("Archiver: " + archiverName + " not found.");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public ArchiveInputStream createArchiveInputStream(InputStream in2) throws ArchiveException {
        block19: {
            TarArchiveInputStream tais;
            block20: {
                if (in2 == null) {
                    throw new IllegalArgumentException("Stream must not be null.");
                }
                if (!in2.markSupported()) {
                    throw new IllegalArgumentException("Mark is not supported.");
                }
                byte[] signature = new byte[12];
                in2.mark(signature.length);
                int signatureLength = IOUtils.readFully(in2, signature);
                in2.reset();
                if (ZipArchiveInputStream.matches(signature, signatureLength)) {
                    if (this.entryEncoding != null) {
                        return new ZipArchiveInputStream(in2, this.entryEncoding);
                    }
                    return new ZipArchiveInputStream(in2);
                }
                if (JarArchiveInputStream.matches(signature, signatureLength)) {
                    return new JarArchiveInputStream(in2);
                }
                if (ArArchiveInputStream.matches(signature, signatureLength)) {
                    return new ArArchiveInputStream(in2);
                }
                if (CpioArchiveInputStream.matches(signature, signatureLength)) {
                    return new CpioArchiveInputStream(in2);
                }
                if (ArjArchiveInputStream.matches(signature, signatureLength)) {
                    return new ArjArchiveInputStream(in2);
                }
                if (SevenZFile.matches(signature, signatureLength)) {
                    throw new StreamingNotSupportedException(SEVEN_Z);
                }
                byte[] dumpsig = new byte[32];
                in2.mark(dumpsig.length);
                signatureLength = IOUtils.readFully(in2, dumpsig);
                in2.reset();
                if (DumpArchiveInputStream.matches(dumpsig, signatureLength)) {
                    return new DumpArchiveInputStream(in2);
                }
                byte[] tarheader = new byte[512];
                in2.mark(tarheader.length);
                signatureLength = IOUtils.readFully(in2, tarheader);
                in2.reset();
                if (TarArchiveInputStream.matches(tarheader, signatureLength)) {
                    if (this.entryEncoding != null) {
                        return new TarArchiveInputStream(in2, this.entryEncoding);
                    }
                    return new TarArchiveInputStream(in2);
                }
                if (signatureLength < 512) break block19;
                tais = null;
                tais = new TarArchiveInputStream(new ByteArrayInputStream(tarheader));
                if (!tais.getNextTarEntry().isCheckSumOK()) break block20;
                TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(in2);
                IOUtils.closeQuietly(tais);
                return tarArchiveInputStream;
            }
            try {
                IOUtils.closeQuietly(tais);
                break block19;
                {
                    catch (Exception e2) {
                        IOUtils.closeQuietly(tais);
                        break block19;
                        catch (Throwable throwable) {
                            IOUtils.closeQuietly(tais);
                            throw throwable;
                        }
                    }
                }
            }
            catch (IOException e3) {
                throw new ArchiveException("Could not use reset and mark operations.", e3);
            }
        }
        throw new ArchiveException("No Archiver found for the stream signature");
    }
}

