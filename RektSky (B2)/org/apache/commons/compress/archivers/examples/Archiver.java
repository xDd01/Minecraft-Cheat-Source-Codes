package org.apache.commons.compress.archivers.examples;

import java.nio.file.*;
import java.nio.channels.*;
import org.apache.commons.compress.archivers.zip.*;
import org.apache.commons.compress.archivers.sevenz.*;
import org.apache.commons.compress.archivers.*;
import org.apache.commons.compress.utils.*;
import java.io.*;

public class Archiver
{
    public void create(final String format, final File target, final File directory) throws IOException, ArchiveException {
        if (this.prefersSeekableByteChannel(format)) {
            try (final SeekableByteChannel c = FileChannel.open(target.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                this.create(format, c, directory);
            }
            return;
        }
        try (final OutputStream o = Files.newOutputStream(target.toPath(), new OpenOption[0])) {
            this.create(format, o, directory);
        }
    }
    
    public void create(final String format, final OutputStream target, final File directory) throws IOException, ArchiveException {
        this.create(new ArchiveStreamFactory().createArchiveOutputStream(format, target), directory);
    }
    
    public void create(final String format, final SeekableByteChannel target, final File directory) throws IOException, ArchiveException {
        if (!this.prefersSeekableByteChannel(format)) {
            this.create(format, Channels.newOutputStream(target), directory);
        }
        else if ("zip".equalsIgnoreCase(format)) {
            this.create(new ZipArchiveOutputStream(target), directory);
        }
        else {
            if (!"7z".equalsIgnoreCase(format)) {
                throw new ArchiveException("don't know how to handle format " + format);
            }
            this.create(new SevenZOutputFile(target), directory);
        }
    }
    
    public void create(final ArchiveOutputStream target, final File directory) throws IOException, ArchiveException {
        this.create(directory, new ArchiveEntryCreator() {
            @Override
            public ArchiveEntry create(final File f, final String entryName) throws IOException {
                return target.createArchiveEntry(f, entryName);
            }
        }, new ArchiveEntryConsumer() {
            @Override
            public void accept(final File source, final ArchiveEntry e) throws IOException {
                target.putArchiveEntry(e);
                if (!e.isDirectory()) {
                    try (final InputStream in = new BufferedInputStream(Files.newInputStream(source.toPath(), new OpenOption[0]))) {
                        IOUtils.copy(in, target);
                    }
                }
                target.closeArchiveEntry();
            }
        }, new Finisher() {
            @Override
            public void finish() throws IOException {
                target.finish();
            }
        });
    }
    
    public void create(final SevenZOutputFile target, final File directory) throws IOException {
        this.create(directory, new ArchiveEntryCreator() {
            @Override
            public ArchiveEntry create(final File f, final String entryName) throws IOException {
                return target.createArchiveEntry(f, entryName);
            }
        }, new ArchiveEntryConsumer() {
            @Override
            public void accept(final File source, final ArchiveEntry e) throws IOException {
                target.putArchiveEntry(e);
                if (!e.isDirectory()) {
                    final byte[] buffer = new byte[8024];
                    int n = 0;
                    long count = 0L;
                    try (final InputStream in = new BufferedInputStream(Files.newInputStream(source.toPath(), new OpenOption[0]))) {
                        while (-1 != (n = in.read(buffer))) {
                            target.write(buffer, 0, n);
                            count += n;
                        }
                    }
                }
                target.closeArchiveEntry();
            }
        }, new Finisher() {
            @Override
            public void finish() throws IOException {
                target.finish();
            }
        });
    }
    
    private boolean prefersSeekableByteChannel(final String format) {
        return "zip".equalsIgnoreCase(format) || "7z".equalsIgnoreCase(format);
    }
    
    private void create(final File directory, final ArchiveEntryCreator creator, final ArchiveEntryConsumer consumer, final Finisher finisher) throws IOException {
        this.create("", directory, creator, consumer);
        finisher.finish();
    }
    
    private void create(final String prefix, final File directory, final ArchiveEntryCreator creator, final ArchiveEntryConsumer consumer) throws IOException {
        final File[] children = directory.listFiles();
        if (children == null) {
            return;
        }
        for (final File f : children) {
            final String entryName = prefix + f.getName() + (f.isDirectory() ? "/" : "");
            consumer.accept(f, creator.create(f, entryName));
            if (f.isDirectory()) {
                this.create(entryName, f, creator, consumer);
            }
        }
    }
    
    private interface Finisher
    {
        void finish() throws IOException;
    }
    
    private interface ArchiveEntryConsumer
    {
        void accept(final File p0, final ArchiveEntry p1) throws IOException;
    }
    
    private interface ArchiveEntryCreator
    {
        ArchiveEntry create(final File p0, final String p1) throws IOException;
    }
}
