package org.apache.commons.compress.archivers.examples;

import java.nio.file.*;
import java.nio.channels.*;
import org.apache.commons.compress.archivers.sevenz.*;
import org.apache.commons.compress.archivers.*;
import java.io.*;
import org.apache.commons.compress.utils.*;
import java.util.*;
import org.apache.commons.compress.archivers.zip.*;

public class Expander
{
    public void expand(final File archive, final File targetDirectory) throws IOException, ArchiveException {
        String format = null;
        try (final InputStream i = new BufferedInputStream(Files.newInputStream(archive.toPath(), new OpenOption[0]))) {
            new ArchiveStreamFactory();
            format = ArchiveStreamFactory.detect(i);
        }
        this.expand(format, archive, targetDirectory);
    }
    
    public void expand(final String format, final File archive, final File targetDirectory) throws IOException, ArchiveException {
        if (this.prefersSeekableByteChannel(format)) {
            try (final SeekableByteChannel c = FileChannel.open(archive.toPath(), StandardOpenOption.READ)) {
                this.expand(format, c, targetDirectory);
            }
            return;
        }
        try (final InputStream i = new BufferedInputStream(Files.newInputStream(archive.toPath(), new OpenOption[0]))) {
            this.expand(format, i, targetDirectory);
        }
    }
    
    public void expand(final InputStream archive, final File targetDirectory) throws IOException, ArchiveException {
        this.expand(new ArchiveStreamFactory().createArchiveInputStream(archive), targetDirectory);
    }
    
    public void expand(final String format, final InputStream archive, final File targetDirectory) throws IOException, ArchiveException {
        this.expand(new ArchiveStreamFactory().createArchiveInputStream(format, archive), targetDirectory);
    }
    
    public void expand(final String format, final SeekableByteChannel archive, final File targetDirectory) throws IOException, ArchiveException {
        if (!this.prefersSeekableByteChannel(format)) {
            this.expand(format, Channels.newInputStream(archive), targetDirectory);
        }
        else if ("zip".equalsIgnoreCase(format)) {
            this.expand(new ZipFile(archive), targetDirectory);
        }
        else {
            if (!"7z".equalsIgnoreCase(format)) {
                throw new ArchiveException("don't know how to handle format " + format);
            }
            this.expand(new SevenZFile(archive), targetDirectory);
        }
    }
    
    public void expand(final ArchiveInputStream archive, final File targetDirectory) throws IOException, ArchiveException {
        this.expand(new ArchiveEntrySupplier() {
            @Override
            public ArchiveEntry getNextReadableEntry() throws IOException {
                ArchiveEntry next;
                for (next = archive.getNextEntry(); next != null && !archive.canReadEntryData(next); next = archive.getNextEntry()) {}
                return next;
            }
        }, new EntryWriter() {
            @Override
            public void writeEntryDataTo(final ArchiveEntry entry, final OutputStream out) throws IOException {
                IOUtils.copy(archive, out);
            }
        }, targetDirectory);
    }
    
    public void expand(final ZipFile archive, final File targetDirectory) throws IOException, ArchiveException {
        final Enumeration<ZipArchiveEntry> entries = archive.getEntries();
        this.expand(new ArchiveEntrySupplier() {
            @Override
            public ArchiveEntry getNextReadableEntry() throws IOException {
                ZipArchiveEntry next;
                for (next = (entries.hasMoreElements() ? entries.nextElement() : null); next != null && !archive.canReadEntryData(next); next = (entries.hasMoreElements() ? entries.nextElement() : null)) {}
                return next;
            }
        }, new EntryWriter() {
            @Override
            public void writeEntryDataTo(final ArchiveEntry entry, final OutputStream out) throws IOException {
                try (final InputStream in = archive.getInputStream((ZipArchiveEntry)entry)) {
                    IOUtils.copy(in, out);
                }
            }
        }, targetDirectory);
    }
    
    public void expand(final SevenZFile archive, final File targetDirectory) throws IOException, ArchiveException {
        this.expand(new ArchiveEntrySupplier() {
            @Override
            public ArchiveEntry getNextReadableEntry() throws IOException {
                return archive.getNextEntry();
            }
        }, new EntryWriter() {
            @Override
            public void writeEntryDataTo(final ArchiveEntry entry, final OutputStream out) throws IOException {
                final byte[] buffer = new byte[8024];
                int n = 0;
                long count = 0L;
                while (-1 != (n = archive.read(buffer))) {
                    out.write(buffer, 0, n);
                    count += n;
                }
            }
        }, targetDirectory);
    }
    
    private boolean prefersSeekableByteChannel(final String format) {
        return "zip".equalsIgnoreCase(format) || "7z".equalsIgnoreCase(format);
    }
    
    private void expand(final ArchiveEntrySupplier supplier, final EntryWriter writer, final File targetDirectory) throws IOException {
        String targetDirPath = targetDirectory.getCanonicalPath();
        if (!targetDirPath.endsWith(File.separator)) {
            targetDirPath += File.separatorChar;
        }
        for (ArchiveEntry nextEntry = supplier.getNextReadableEntry(); nextEntry != null; nextEntry = supplier.getNextReadableEntry()) {
            final File f = new File(targetDirectory, nextEntry.getName());
            if (!f.getCanonicalPath().startsWith(targetDirPath)) {
                throw new IOException("expanding " + nextEntry.getName() + " would create file outside of " + targetDirectory);
            }
            if (nextEntry.isDirectory()) {
                if (!f.isDirectory() && !f.mkdirs()) {
                    throw new IOException("failed to create directory " + f);
                }
            }
            else {
                final File parent = f.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("failed to create directory " + parent);
                }
                try (final OutputStream o = Files.newOutputStream(f.toPath(), new OpenOption[0])) {
                    writer.writeEntryDataTo(nextEntry, o);
                }
            }
        }
    }
    
    private interface EntryWriter
    {
        void writeEntryDataTo(final ArchiveEntry p0, final OutputStream p1) throws IOException;
    }
    
    private interface ArchiveEntrySupplier
    {
        ArchiveEntry getNextReadableEntry() throws IOException;
    }
}
