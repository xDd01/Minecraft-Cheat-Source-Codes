package org.apache.commons.compress.archivers.zip;

import java.util.concurrent.*;
import org.apache.commons.compress.utils.*;
import java.util.*;
import java.io.*;
import org.apache.commons.compress.parallel.*;

public class ScatterZipOutputStream implements Closeable
{
    private final Queue<CompressedEntry> items;
    private final ScatterGatherBackingStore backingStore;
    private final StreamCompressor streamCompressor;
    
    public ScatterZipOutputStream(final ScatterGatherBackingStore backingStore, final StreamCompressor streamCompressor) {
        this.items = new ConcurrentLinkedQueue<CompressedEntry>();
        this.backingStore = backingStore;
        this.streamCompressor = streamCompressor;
    }
    
    public void addArchiveEntry(final ZipArchiveEntryRequest zipArchiveEntryRequest) throws IOException {
        try (final InputStream payloadStream = zipArchiveEntryRequest.getPayloadStream()) {
            this.streamCompressor.deflate(payloadStream, zipArchiveEntryRequest.getMethod());
        }
        this.items.add(new CompressedEntry(zipArchiveEntryRequest, this.streamCompressor.getCrc32(), this.streamCompressor.getBytesWrittenForLastEntry(), this.streamCompressor.getBytesRead()));
    }
    
    public void writeTo(final ZipArchiveOutputStream target) throws IOException {
        this.backingStore.closeForWriting();
        try (final InputStream data = this.backingStore.getInputStream()) {
            for (final CompressedEntry compressedEntry : this.items) {
                try (final BoundedInputStream rawStream = new BoundedInputStream(data, compressedEntry.compressedSize)) {
                    target.addRawArchiveEntry(compressedEntry.transferToArchiveEntry(), rawStream);
                }
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        try {
            this.backingStore.close();
        }
        finally {
            this.streamCompressor.close();
        }
    }
    
    public static ScatterZipOutputStream fileBased(final File file) throws FileNotFoundException {
        return fileBased(file, -1);
    }
    
    public static ScatterZipOutputStream fileBased(final File file, final int compressionLevel) throws FileNotFoundException {
        final ScatterGatherBackingStore bs = new FileBasedScatterGatherBackingStore(file);
        final StreamCompressor sc = StreamCompressor.create(compressionLevel, bs);
        return new ScatterZipOutputStream(bs, sc);
    }
    
    private static class CompressedEntry
    {
        final ZipArchiveEntryRequest zipArchiveEntryRequest;
        final long crc;
        final long compressedSize;
        final long size;
        
        public CompressedEntry(final ZipArchiveEntryRequest zipArchiveEntryRequest, final long crc, final long compressedSize, final long size) {
            this.zipArchiveEntryRequest = zipArchiveEntryRequest;
            this.crc = crc;
            this.compressedSize = compressedSize;
            this.size = size;
        }
        
        public ZipArchiveEntry transferToArchiveEntry() {
            final ZipArchiveEntry entry = this.zipArchiveEntryRequest.getZipArchiveEntry();
            entry.setCompressedSize(this.compressedSize);
            entry.setSize(this.size);
            entry.setCrc(this.crc);
            entry.setMethod(this.zipArchiveEntryRequest.getMethod());
            return entry;
        }
    }
}
