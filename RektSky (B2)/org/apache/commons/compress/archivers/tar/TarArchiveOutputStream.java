package org.apache.commons.compress.archivers.tar;

import org.apache.commons.compress.utils.*;
import org.apache.commons.compress.archivers.zip.*;
import org.apache.commons.compress.archivers.*;
import java.io.*;
import java.nio.*;
import java.util.*;

public class TarArchiveOutputStream extends ArchiveOutputStream
{
    public static final int LONGFILE_ERROR = 0;
    public static final int LONGFILE_TRUNCATE = 1;
    public static final int LONGFILE_GNU = 2;
    public static final int LONGFILE_POSIX = 3;
    public static final int BIGNUMBER_ERROR = 0;
    public static final int BIGNUMBER_STAR = 1;
    public static final int BIGNUMBER_POSIX = 2;
    private static final int RECORD_SIZE = 512;
    private long currSize;
    private String currName;
    private long currBytes;
    private final byte[] recordBuf;
    private int longFileMode;
    private int bigNumberMode;
    private int recordsWritten;
    private final int recordsPerBlock;
    private boolean closed;
    private boolean haveUnclosedEntry;
    private boolean finished;
    private final FixedLengthBlockOutputStream out;
    private final CountingOutputStream countingOut;
    private final ZipEncoding zipEncoding;
    final String encoding;
    private boolean addPaxHeadersForNonAsciiNames;
    private static final ZipEncoding ASCII;
    private static final int BLOCK_SIZE_UNSPECIFIED = -511;
    
    public TarArchiveOutputStream(final OutputStream os) {
        this(os, -511);
    }
    
    public TarArchiveOutputStream(final OutputStream os, final String encoding) {
        this(os, -511, encoding);
    }
    
    public TarArchiveOutputStream(final OutputStream os, final int blockSize) {
        this(os, blockSize, null);
    }
    
    @Deprecated
    public TarArchiveOutputStream(final OutputStream os, final int blockSize, final int recordSize) {
        this(os, blockSize, recordSize, null);
    }
    
    @Deprecated
    public TarArchiveOutputStream(final OutputStream os, final int blockSize, final int recordSize, final String encoding) {
        this(os, blockSize, encoding);
        if (recordSize != 512) {
            throw new IllegalArgumentException("Tar record size must always be 512 bytes. Attempt to set size of " + recordSize);
        }
    }
    
    public TarArchiveOutputStream(final OutputStream os, final int blockSize, final String encoding) {
        this.longFileMode = 0;
        this.bigNumberMode = 0;
        this.closed = false;
        this.haveUnclosedEntry = false;
        this.finished = false;
        this.addPaxHeadersForNonAsciiNames = false;
        int realBlockSize;
        if (-511 == blockSize) {
            realBlockSize = 512;
        }
        else {
            realBlockSize = blockSize;
        }
        if (realBlockSize <= 0 || realBlockSize % 512 != 0) {
            throw new IllegalArgumentException("Block size must be a multiple of 512 bytes. Attempt to use set size of " + blockSize);
        }
        this.out = new FixedLengthBlockOutputStream(this.countingOut = new CountingOutputStream(os), 512);
        this.encoding = encoding;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
        this.recordBuf = new byte[512];
        this.recordsPerBlock = realBlockSize / 512;
    }
    
    public void setLongFileMode(final int longFileMode) {
        this.longFileMode = longFileMode;
    }
    
    public void setBigNumberMode(final int bigNumberMode) {
        this.bigNumberMode = bigNumberMode;
    }
    
    public void setAddPaxHeadersForNonAsciiNames(final boolean b) {
        this.addPaxHeadersForNonAsciiNames = b;
    }
    
    @Deprecated
    @Override
    public int getCount() {
        return (int)this.getBytesWritten();
    }
    
    @Override
    public long getBytesWritten() {
        return this.countingOut.getBytesWritten();
    }
    
    @Override
    public void finish() throws IOException {
        if (this.finished) {
            throw new IOException("This archive has already been finished");
        }
        if (this.haveUnclosedEntry) {
            throw new IOException("This archive contains unclosed entries.");
        }
        this.writeEOFRecord();
        this.writeEOFRecord();
        this.padAsNeeded();
        this.out.flush();
        this.finished = true;
    }
    
    @Override
    public void close() throws IOException {
        try {
            if (!this.finished) {
                this.finish();
            }
        }
        finally {
            if (!this.closed) {
                this.out.close();
                this.closed = true;
            }
        }
    }
    
    @Deprecated
    public int getRecordSize() {
        return 512;
    }
    
    @Override
    public void putArchiveEntry(final ArchiveEntry archiveEntry) throws IOException {
        if (this.finished) {
            throw new IOException("Stream has already been finished");
        }
        final TarArchiveEntry entry = (TarArchiveEntry)archiveEntry;
        if (entry.isGlobalPaxHeader()) {
            final byte[] data = this.encodeExtendedPaxHeadersContents(entry.getExtraPaxHeaders());
            entry.setSize(data.length);
            entry.writeEntryHeader(this.recordBuf, this.zipEncoding, this.bigNumberMode == 1);
            this.writeRecord(this.recordBuf);
            this.currSize = entry.getSize();
            this.currBytes = 0L;
            this.haveUnclosedEntry = true;
            this.write(data);
            this.closeArchiveEntry();
        }
        else {
            final Map<String, String> paxHeaders = new HashMap<String, String>();
            final String entryName = entry.getName();
            final boolean paxHeaderContainsPath = this.handleLongName(entry, entryName, paxHeaders, "path", (byte)76, "file name");
            final String linkName = entry.getLinkName();
            final boolean paxHeaderContainsLinkPath = linkName != null && linkName.length() > 0 && this.handleLongName(entry, linkName, paxHeaders, "linkpath", (byte)75, "link name");
            if (this.bigNumberMode == 2) {
                this.addPaxHeadersForBigNumbers(paxHeaders, entry);
            }
            else if (this.bigNumberMode != 1) {
                this.failForBigNumbers(entry);
            }
            if (this.addPaxHeadersForNonAsciiNames && !paxHeaderContainsPath && !TarArchiveOutputStream.ASCII.canEncode(entryName)) {
                paxHeaders.put("path", entryName);
            }
            if (this.addPaxHeadersForNonAsciiNames && !paxHeaderContainsLinkPath && (entry.isLink() || entry.isSymbolicLink()) && !TarArchiveOutputStream.ASCII.canEncode(linkName)) {
                paxHeaders.put("linkpath", linkName);
            }
            paxHeaders.putAll(entry.getExtraPaxHeaders());
            if (paxHeaders.size() > 0) {
                this.writePaxHeaders(entry, entryName, paxHeaders);
            }
            entry.writeEntryHeader(this.recordBuf, this.zipEncoding, this.bigNumberMode == 1);
            this.writeRecord(this.recordBuf);
            this.currBytes = 0L;
            if (entry.isDirectory()) {
                this.currSize = 0L;
            }
            else {
                this.currSize = entry.getSize();
            }
            this.currName = entryName;
            this.haveUnclosedEntry = true;
        }
    }
    
    @Override
    public void closeArchiveEntry() throws IOException {
        if (this.finished) {
            throw new IOException("Stream has already been finished");
        }
        if (!this.haveUnclosedEntry) {
            throw new IOException("No current entry to close");
        }
        this.out.flushBlock();
        if (this.currBytes < this.currSize) {
            throw new IOException("entry '" + this.currName + "' closed at '" + this.currBytes + "' before the '" + this.currSize + "' bytes specified in the header were written");
        }
        this.recordsWritten += (int)(this.currSize / 512L);
        if (0L != this.currSize % 512L) {
            ++this.recordsWritten;
        }
        this.haveUnclosedEntry = false;
    }
    
    @Override
    public void write(final byte[] wBuf, final int wOffset, final int numToWrite) throws IOException {
        if (!this.haveUnclosedEntry) {
            throw new IllegalStateException("No current tar entry");
        }
        if (this.currBytes + numToWrite > this.currSize) {
            throw new IOException("request to write '" + numToWrite + "' bytes exceeds size in header of '" + this.currSize + "' bytes for entry '" + this.currName + "'");
        }
        this.out.write(wBuf, wOffset, numToWrite);
        this.currBytes += numToWrite;
    }
    
    void writePaxHeaders(final TarArchiveEntry entry, final String entryName, final Map<String, String> headers) throws IOException {
        String name = "./PaxHeaders.X/" + this.stripTo7Bits(entryName);
        if (name.length() >= 100) {
            name = name.substring(0, 99);
        }
        final TarArchiveEntry pex = new TarArchiveEntry(name, (byte)120);
        this.transferModTime(entry, pex);
        final byte[] data = this.encodeExtendedPaxHeadersContents(headers);
        pex.setSize(data.length);
        this.putArchiveEntry(pex);
        this.write(data);
        this.closeArchiveEntry();
    }
    
    private byte[] encodeExtendedPaxHeadersContents(final Map<String, String> headers) throws UnsupportedEncodingException {
        final StringWriter w = new StringWriter();
        for (final Map.Entry<String, String> h : headers.entrySet()) {
            final String key = h.getKey();
            final String value = h.getValue();
            int len = key.length() + value.length() + 3 + 2;
            String line = len + " " + key + "=" + value + "\n";
            for (int actualLength = line.getBytes("UTF-8").length; len != actualLength; len = actualLength, line = len + " " + key + "=" + value + "\n", actualLength = line.getBytes("UTF-8").length) {}
            w.write(line);
        }
        return w.toString().getBytes("UTF-8");
    }
    
    private String stripTo7Bits(final String name) {
        final int length = name.length();
        final StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            final char stripped = (char)(name.charAt(i) & '\u007f');
            if (this.shouldBeReplaced(stripped)) {
                result.append("_");
            }
            else {
                result.append(stripped);
            }
        }
        return result.toString();
    }
    
    private boolean shouldBeReplaced(final char c) {
        return c == '\0' || c == '/' || c == '\\';
    }
    
    private void writeEOFRecord() throws IOException {
        Arrays.fill(this.recordBuf, (byte)0);
        this.writeRecord(this.recordBuf);
    }
    
    @Override
    public void flush() throws IOException {
        this.out.flush();
    }
    
    @Override
    public ArchiveEntry createArchiveEntry(final File inputFile, final String entryName) throws IOException {
        if (this.finished) {
            throw new IOException("Stream has already been finished");
        }
        return new TarArchiveEntry(inputFile, entryName);
    }
    
    private void writeRecord(final byte[] record) throws IOException {
        if (record.length != 512) {
            throw new IOException("record to write has length '" + record.length + "' which is not the record size of '" + 512 + "'");
        }
        this.out.write(record);
        ++this.recordsWritten;
    }
    
    private void padAsNeeded() throws IOException {
        final int start = this.recordsWritten % this.recordsPerBlock;
        if (start != 0) {
            for (int i = start; i < this.recordsPerBlock; ++i) {
                this.writeEOFRecord();
            }
        }
    }
    
    private void addPaxHeadersForBigNumbers(final Map<String, String> paxHeaders, final TarArchiveEntry entry) {
        this.addPaxHeaderForBigNumber(paxHeaders, "size", entry.getSize(), 8589934591L);
        this.addPaxHeaderForBigNumber(paxHeaders, "gid", entry.getLongGroupId(), 2097151L);
        this.addPaxHeaderForBigNumber(paxHeaders, "mtime", entry.getModTime().getTime() / 1000L, 8589934591L);
        this.addPaxHeaderForBigNumber(paxHeaders, "uid", entry.getLongUserId(), 2097151L);
        this.addPaxHeaderForBigNumber(paxHeaders, "SCHILY.devmajor", entry.getDevMajor(), 2097151L);
        this.addPaxHeaderForBigNumber(paxHeaders, "SCHILY.devminor", entry.getDevMinor(), 2097151L);
        this.failForBigNumber("mode", entry.getMode(), 2097151L);
    }
    
    private void addPaxHeaderForBigNumber(final Map<String, String> paxHeaders, final String header, final long value, final long maxValue) {
        if (value < 0L || value > maxValue) {
            paxHeaders.put(header, String.valueOf(value));
        }
    }
    
    private void failForBigNumbers(final TarArchiveEntry entry) {
        this.failForBigNumber("entry size", entry.getSize(), 8589934591L);
        this.failForBigNumberWithPosixMessage("group id", entry.getLongGroupId(), 2097151L);
        this.failForBigNumber("last modification time", entry.getModTime().getTime() / 1000L, 8589934591L);
        this.failForBigNumber("user id", entry.getLongUserId(), 2097151L);
        this.failForBigNumber("mode", entry.getMode(), 2097151L);
        this.failForBigNumber("major device number", entry.getDevMajor(), 2097151L);
        this.failForBigNumber("minor device number", entry.getDevMinor(), 2097151L);
    }
    
    private void failForBigNumber(final String field, final long value, final long maxValue) {
        this.failForBigNumber(field, value, maxValue, "");
    }
    
    private void failForBigNumberWithPosixMessage(final String field, final long value, final long maxValue) {
        this.failForBigNumber(field, value, maxValue, " Use STAR or POSIX extensions to overcome this limit");
    }
    
    private void failForBigNumber(final String field, final long value, final long maxValue, final String additionalMsg) {
        if (value < 0L || value > maxValue) {
            throw new RuntimeException(field + " '" + value + "' is too big ( > " + maxValue + " )." + additionalMsg);
        }
    }
    
    private boolean handleLongName(final TarArchiveEntry entry, final String name, final Map<String, String> paxHeaders, final String paxHeaderName, final byte linkType, final String fieldName) throws IOException {
        final ByteBuffer encodedName = this.zipEncoding.encode(name);
        final int len = encodedName.limit() - encodedName.position();
        if (len >= 100) {
            if (this.longFileMode == 3) {
                paxHeaders.put(paxHeaderName, name);
                return true;
            }
            if (this.longFileMode == 2) {
                final TarArchiveEntry longLinkEntry = new TarArchiveEntry("././@LongLink", linkType);
                longLinkEntry.setSize(len + 1L);
                this.transferModTime(entry, longLinkEntry);
                this.putArchiveEntry(longLinkEntry);
                this.write(encodedName.array(), encodedName.arrayOffset(), len);
                this.write(0);
                this.closeArchiveEntry();
            }
            else if (this.longFileMode != 1) {
                throw new RuntimeException(fieldName + " '" + name + "' is too long ( > " + 100 + " bytes)");
            }
        }
        return false;
    }
    
    private void transferModTime(final TarArchiveEntry from, final TarArchiveEntry to) {
        Date fromModTime = from.getModTime();
        final long fromModTimeSeconds = fromModTime.getTime() / 1000L;
        if (fromModTimeSeconds < 0L || fromModTimeSeconds > 8589934591L) {
            fromModTime = new Date(0L);
        }
        to.setModTime(fromModTime);
    }
    
    static {
        ASCII = ZipEncodingHelper.getZipEncoding("ASCII");
    }
}
