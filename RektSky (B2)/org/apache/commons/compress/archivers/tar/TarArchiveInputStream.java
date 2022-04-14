package org.apache.commons.compress.archivers.tar;

import java.util.*;
import org.apache.commons.compress.archivers.zip.*;
import java.io.*;
import org.apache.commons.compress.utils.*;
import org.apache.commons.compress.archivers.*;

public class TarArchiveInputStream extends ArchiveInputStream
{
    private static final int SMALL_BUFFER_SIZE = 256;
    private final byte[] smallBuf;
    private final int recordSize;
    private final int blockSize;
    private boolean hasHitEOF;
    private long entrySize;
    private long entryOffset;
    private final InputStream is;
    private TarArchiveEntry currEntry;
    private final ZipEncoding zipEncoding;
    final String encoding;
    private Map<String, String> globalPaxHeaders;
    
    public TarArchiveInputStream(final InputStream is) {
        this(is, 10240, 512);
    }
    
    public TarArchiveInputStream(final InputStream is, final String encoding) {
        this(is, 10240, 512, encoding);
    }
    
    public TarArchiveInputStream(final InputStream is, final int blockSize) {
        this(is, blockSize, 512);
    }
    
    public TarArchiveInputStream(final InputStream is, final int blockSize, final String encoding) {
        this(is, blockSize, 512, encoding);
    }
    
    public TarArchiveInputStream(final InputStream is, final int blockSize, final int recordSize) {
        this(is, blockSize, recordSize, null);
    }
    
    public TarArchiveInputStream(final InputStream is, final int blockSize, final int recordSize, final String encoding) {
        this.smallBuf = new byte[256];
        this.globalPaxHeaders = new HashMap<String, String>();
        this.is = is;
        this.hasHitEOF = false;
        this.encoding = encoding;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
        this.recordSize = recordSize;
        this.blockSize = blockSize;
    }
    
    @Override
    public void close() throws IOException {
        this.is.close();
    }
    
    public int getRecordSize() {
        return this.recordSize;
    }
    
    @Override
    public int available() throws IOException {
        if (this.isDirectory()) {
            return 0;
        }
        if (this.entrySize - this.entryOffset > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int)(this.entrySize - this.entryOffset);
    }
    
    @Override
    public long skip(final long n) throws IOException {
        if (n <= 0L || this.isDirectory()) {
            return 0L;
        }
        final long available = this.entrySize - this.entryOffset;
        final long skipped = IOUtils.skip(this.is, Math.min(n, available));
        this.count(skipped);
        this.entryOffset += skipped;
        return skipped;
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public void mark(final int markLimit) {
    }
    
    @Override
    public synchronized void reset() {
    }
    
    public TarArchiveEntry getNextTarEntry() throws IOException {
        if (this.isAtEOF()) {
            return null;
        }
        if (this.currEntry != null) {
            IOUtils.skip(this, Long.MAX_VALUE);
            this.skipRecordPadding();
        }
        final byte[] headerBuf = this.getRecord();
        if (headerBuf == null) {
            return this.currEntry = null;
        }
        try {
            this.currEntry = new TarArchiveEntry(headerBuf, this.zipEncoding);
        }
        catch (IllegalArgumentException e) {
            throw new IOException("Error detected parsing the header", e);
        }
        this.entryOffset = 0L;
        this.entrySize = this.currEntry.getSize();
        if (this.currEntry.isGNULongLinkEntry()) {
            final byte[] longLinkData = this.getLongNameData();
            if (longLinkData == null) {
                return null;
            }
            this.currEntry.setLinkName(this.zipEncoding.decode(longLinkData));
        }
        if (this.currEntry.isGNULongNameEntry()) {
            final byte[] longNameData = this.getLongNameData();
            if (longNameData == null) {
                return null;
            }
            this.currEntry.setName(this.zipEncoding.decode(longNameData));
        }
        if (this.currEntry.isGlobalPaxHeader()) {
            this.readGlobalPaxHeaders();
        }
        if (this.currEntry.isPaxHeader()) {
            this.paxHeaders();
        }
        else if (!this.globalPaxHeaders.isEmpty()) {
            this.applyPaxHeadersToCurrentEntry(this.globalPaxHeaders);
        }
        if (this.currEntry.isOldGNUSparse()) {
            this.readOldGNUSparse();
        }
        this.entrySize = this.currEntry.getSize();
        return this.currEntry;
    }
    
    private void skipRecordPadding() throws IOException {
        if (!this.isDirectory() && this.entrySize > 0L && this.entrySize % this.recordSize != 0L) {
            final long numRecords = this.entrySize / this.recordSize + 1L;
            final long padding = numRecords * this.recordSize - this.entrySize;
            final long skipped = IOUtils.skip(this.is, padding);
            this.count(skipped);
        }
    }
    
    protected byte[] getLongNameData() throws IOException {
        final ByteArrayOutputStream longName = new ByteArrayOutputStream();
        int length = 0;
        while ((length = this.read(this.smallBuf)) >= 0) {
            longName.write(this.smallBuf, 0, length);
        }
        this.getNextEntry();
        if (this.currEntry == null) {
            return null;
        }
        byte[] longNameData;
        for (longNameData = longName.toByteArray(), length = longNameData.length; length > 0 && longNameData[length - 1] == 0; --length) {}
        if (length != longNameData.length) {
            final byte[] l = new byte[length];
            System.arraycopy(longNameData, 0, l, 0, length);
            longNameData = l;
        }
        return longNameData;
    }
    
    private byte[] getRecord() throws IOException {
        byte[] headerBuf = this.readRecord();
        this.setAtEOF(this.isEOFRecord(headerBuf));
        if (this.isAtEOF() && headerBuf != null) {
            this.tryToConsumeSecondEOFRecord();
            this.consumeRemainderOfLastBlock();
            headerBuf = null;
        }
        return headerBuf;
    }
    
    protected boolean isEOFRecord(final byte[] record) {
        return record == null || ArchiveUtils.isArrayZero(record, this.recordSize);
    }
    
    protected byte[] readRecord() throws IOException {
        final byte[] record = new byte[this.recordSize];
        final int readNow = IOUtils.readFully(this.is, record);
        this.count(readNow);
        if (readNow != this.recordSize) {
            return null;
        }
        return record;
    }
    
    private void readGlobalPaxHeaders() throws IOException {
        this.globalPaxHeaders = this.parsePaxHeaders(this);
        this.getNextEntry();
    }
    
    private void paxHeaders() throws IOException {
        final Map<String, String> headers = this.parsePaxHeaders(this);
        this.getNextEntry();
        this.applyPaxHeadersToCurrentEntry(headers);
    }
    
    Map<String, String> parsePaxHeaders(final InputStream i) throws IOException {
        final Map<String, String> headers = new HashMap<String, String>(this.globalPaxHeaders);
        while (true) {
            int len = 0;
            int read = 0;
            int ch;
            while ((ch = i.read()) != -1) {
                ++read;
                if (ch == 10) {
                    break;
                }
                if (ch == 32) {
                    final ByteArrayOutputStream coll = new ByteArrayOutputStream();
                    while ((ch = i.read()) != -1) {
                        ++read;
                        if (ch == 61) {
                            final String keyword = coll.toString("UTF-8");
                            final int restLen = len - read;
                            if (restLen == 1) {
                                headers.remove(keyword);
                                break;
                            }
                            final byte[] rest = new byte[restLen];
                            final int got = IOUtils.readFully(i, rest);
                            if (got != restLen) {
                                throw new IOException("Failed to read Paxheader. Expected " + restLen + " bytes, read " + got);
                            }
                            final String value = new String(rest, 0, restLen - 1, "UTF-8");
                            headers.put(keyword, value);
                            break;
                        }
                        else {
                            coll.write((byte)ch);
                        }
                    }
                    break;
                }
                len *= 10;
                len += ch - 48;
            }
            if (ch == -1) {
                return headers;
            }
        }
    }
    
    private void applyPaxHeadersToCurrentEntry(final Map<String, String> headers) {
        this.currEntry.updateEntryFromPaxHeaders(headers);
    }
    
    private void readOldGNUSparse() throws IOException {
        if (this.currEntry.isExtended()) {
            TarArchiveSparseEntry entry;
            do {
                final byte[] headerBuf = this.getRecord();
                if (headerBuf == null) {
                    this.currEntry = null;
                    break;
                }
                entry = new TarArchiveSparseEntry(headerBuf);
            } while (entry.isExtended());
        }
    }
    
    private boolean isDirectory() {
        return this.currEntry != null && this.currEntry.isDirectory();
    }
    
    @Override
    public ArchiveEntry getNextEntry() throws IOException {
        return this.getNextTarEntry();
    }
    
    private void tryToConsumeSecondEOFRecord() throws IOException {
        boolean shouldReset = true;
        final boolean marked = this.is.markSupported();
        if (marked) {
            this.is.mark(this.recordSize);
        }
        try {
            shouldReset = !this.isEOFRecord(this.readRecord());
        }
        finally {
            if (shouldReset && marked) {
                this.pushedBackBytes(this.recordSize);
                this.is.reset();
            }
        }
    }
    
    @Override
    public int read(final byte[] buf, final int offset, int numToRead) throws IOException {
        int totalRead = 0;
        if (this.isAtEOF() || this.isDirectory() || this.entryOffset >= this.entrySize) {
            return -1;
        }
        if (this.currEntry == null) {
            throw new IllegalStateException("No current tar entry");
        }
        numToRead = Math.min(numToRead, this.available());
        totalRead = this.is.read(buf, offset, numToRead);
        if (totalRead == -1) {
            if (numToRead > 0) {
                throw new IOException("Truncated TAR archive");
            }
            this.setAtEOF(true);
        }
        else {
            this.count(totalRead);
            this.entryOffset += totalRead;
        }
        return totalRead;
    }
    
    @Override
    public boolean canReadEntryData(final ArchiveEntry ae) {
        if (ae instanceof TarArchiveEntry) {
            final TarArchiveEntry te = (TarArchiveEntry)ae;
            return !te.isSparse();
        }
        return false;
    }
    
    public TarArchiveEntry getCurrentEntry() {
        return this.currEntry;
    }
    
    protected final void setCurrentEntry(final TarArchiveEntry e) {
        this.currEntry = e;
    }
    
    protected final boolean isAtEOF() {
        return this.hasHitEOF;
    }
    
    protected final void setAtEOF(final boolean b) {
        this.hasHitEOF = b;
    }
    
    private void consumeRemainderOfLastBlock() throws IOException {
        final long bytesReadOfLastBlock = this.getBytesRead() % this.blockSize;
        if (bytesReadOfLastBlock > 0L) {
            final long skipped = IOUtils.skip(this.is, this.blockSize - bytesReadOfLastBlock);
            this.count(skipped);
        }
    }
    
    public static boolean matches(final byte[] signature, final int length) {
        return length >= 265 && ((ArchiveUtils.matchAsciiBuffer("ustar\u0000", signature, 257, 6) && ArchiveUtils.matchAsciiBuffer("00", signature, 263, 2)) || (ArchiveUtils.matchAsciiBuffer("ustar ", signature, 257, 6) && (ArchiveUtils.matchAsciiBuffer(" \u0000", signature, 263, 2) || ArchiveUtils.matchAsciiBuffer("0\u0000", signature, 263, 2))) || (ArchiveUtils.matchAsciiBuffer("ustar\u0000", signature, 257, 6) && ArchiveUtils.matchAsciiBuffer("\u0000\u0000", signature, 263, 2)));
    }
}
