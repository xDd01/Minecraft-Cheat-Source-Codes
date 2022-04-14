package org.apache.commons.compress.archivers.zip;

import java.nio.*;
import java.nio.file.attribute.*;
import java.nio.file.*;
import org.apache.commons.compress.compressors.bzip2.*;
import org.apache.commons.compress.compressors.deflate64.*;
import java.util.zip.*;
import java.io.*;
import java.util.*;
import java.nio.channels.*;
import org.apache.commons.compress.utils.*;

public class ZipFile implements Closeable
{
    private static final int HASH_SIZE = 509;
    static final int NIBLET_MASK = 15;
    static final int BYTE_SHIFT = 8;
    private static final int POS_0 = 0;
    private static final int POS_1 = 1;
    private static final int POS_2 = 2;
    private static final int POS_3 = 3;
    private static final byte[] ONE_ZERO_BYTE;
    private final List<ZipArchiveEntry> entries;
    private final Map<String, LinkedList<ZipArchiveEntry>> nameMap;
    private final String encoding;
    private final ZipEncoding zipEncoding;
    private final String archiveName;
    private final SeekableByteChannel archive;
    private final boolean useUnicodeExtraFields;
    private volatile boolean closed;
    private final byte[] dwordBuf;
    private final byte[] wordBuf;
    private final byte[] cfhBuf;
    private final byte[] shortBuf;
    private final ByteBuffer dwordBbuf;
    private final ByteBuffer wordBbuf;
    private final ByteBuffer cfhBbuf;
    private static final int CFH_LEN = 42;
    private static final long CFH_SIG;
    static final int MIN_EOCD_SIZE = 22;
    private static final int MAX_EOCD_SIZE = 65557;
    private static final int CFD_LOCATOR_OFFSET = 16;
    private static final int ZIP64_EOCDL_LENGTH = 20;
    private static final int ZIP64_EOCDL_LOCATOR_OFFSET = 8;
    private static final int ZIP64_EOCD_CFD_LOCATOR_OFFSET = 48;
    private static final long LFH_OFFSET_FOR_FILENAME_LENGTH = 26L;
    private final Comparator<ZipArchiveEntry> offsetComparator;
    
    public ZipFile(final File f) throws IOException {
        this(f, "UTF8");
    }
    
    public ZipFile(final String name) throws IOException {
        this(new File(name), "UTF8");
    }
    
    public ZipFile(final String name, final String encoding) throws IOException {
        this(new File(name), encoding, true);
    }
    
    public ZipFile(final File f, final String encoding) throws IOException {
        this(f, encoding, true);
    }
    
    public ZipFile(final File f, final String encoding, final boolean useUnicodeExtraFields) throws IOException {
        this(Files.newByteChannel(f.toPath(), EnumSet.of(StandardOpenOption.READ), (FileAttribute<?>[])new FileAttribute[0]), f.getAbsolutePath(), encoding, useUnicodeExtraFields, true);
    }
    
    public ZipFile(final SeekableByteChannel channel) throws IOException {
        this(channel, "unknown archive", "UTF8", true);
    }
    
    public ZipFile(final SeekableByteChannel channel, final String encoding) throws IOException {
        this(channel, "unknown archive", encoding, true);
    }
    
    public ZipFile(final SeekableByteChannel channel, final String archiveName, final String encoding, final boolean useUnicodeExtraFields) throws IOException {
        this(channel, archiveName, encoding, useUnicodeExtraFields, false);
    }
    
    private ZipFile(final SeekableByteChannel channel, final String archiveName, final String encoding, final boolean useUnicodeExtraFields, final boolean closeOnError) throws IOException {
        this.entries = new LinkedList<ZipArchiveEntry>();
        this.nameMap = new HashMap<String, LinkedList<ZipArchiveEntry>>(509);
        this.closed = true;
        this.dwordBuf = new byte[8];
        this.wordBuf = new byte[4];
        this.cfhBuf = new byte[42];
        this.shortBuf = new byte[2];
        this.dwordBbuf = ByteBuffer.wrap(this.dwordBuf);
        this.wordBbuf = ByteBuffer.wrap(this.wordBuf);
        this.cfhBbuf = ByteBuffer.wrap(this.cfhBuf);
        this.offsetComparator = new Comparator<ZipArchiveEntry>() {
            @Override
            public int compare(final ZipArchiveEntry e1, final ZipArchiveEntry e2) {
                if (e1 == e2) {
                    return 0;
                }
                final Entry ent1 = (e1 instanceof Entry) ? ((Entry)e1) : null;
                final Entry ent2 = (e2 instanceof Entry) ? ((Entry)e2) : null;
                if (ent1 == null) {
                    return 1;
                }
                if (ent2 == null) {
                    return -1;
                }
                final long val = ent1.getLocalHeaderOffset() - ent2.getLocalHeaderOffset();
                return (val == 0L) ? 0 : ((val < 0L) ? -1 : 1);
            }
        };
        this.archiveName = archiveName;
        this.encoding = encoding;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
        this.useUnicodeExtraFields = useUnicodeExtraFields;
        this.archive = channel;
        boolean success = false;
        try {
            final Map<ZipArchiveEntry, NameAndComment> entriesWithoutUTF8Flag = this.populateFromCentralDirectory();
            this.resolveLocalFileHeaderData(entriesWithoutUTF8Flag);
            success = true;
        }
        finally {
            this.closed = !success;
            if (!success && closeOnError) {
                IOUtils.closeQuietly(this.archive);
            }
        }
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    @Override
    public void close() throws IOException {
        this.closed = true;
        this.archive.close();
    }
    
    public static void closeQuietly(final ZipFile zipfile) {
        IOUtils.closeQuietly(zipfile);
    }
    
    public Enumeration<ZipArchiveEntry> getEntries() {
        return Collections.enumeration(this.entries);
    }
    
    public Enumeration<ZipArchiveEntry> getEntriesInPhysicalOrder() {
        final ZipArchiveEntry[] allEntries = this.entries.toArray(new ZipArchiveEntry[this.entries.size()]);
        Arrays.sort(allEntries, this.offsetComparator);
        return Collections.enumeration(Arrays.asList(allEntries));
    }
    
    public ZipArchiveEntry getEntry(final String name) {
        final LinkedList<ZipArchiveEntry> entriesOfThatName = this.nameMap.get(name);
        return (entriesOfThatName != null) ? entriesOfThatName.getFirst() : null;
    }
    
    public Iterable<ZipArchiveEntry> getEntries(final String name) {
        final List<ZipArchiveEntry> entriesOfThatName = this.nameMap.get(name);
        return (entriesOfThatName != null) ? entriesOfThatName : Collections.emptyList();
    }
    
    public Iterable<ZipArchiveEntry> getEntriesInPhysicalOrder(final String name) {
        ZipArchiveEntry[] entriesOfThatName = new ZipArchiveEntry[0];
        if (this.nameMap.containsKey(name)) {
            entriesOfThatName = this.nameMap.get(name).toArray(entriesOfThatName);
            Arrays.sort(entriesOfThatName, this.offsetComparator);
        }
        return Arrays.asList(entriesOfThatName);
    }
    
    public boolean canReadEntryData(final ZipArchiveEntry ze) {
        return ZipUtil.canHandleEntryData(ze);
    }
    
    public InputStream getRawInputStream(final ZipArchiveEntry ze) {
        if (!(ze instanceof Entry)) {
            return null;
        }
        final long start = ze.getDataOffset();
        return this.createBoundedInputStream(start, ze.getCompressedSize());
    }
    
    public void copyRawEntries(final ZipArchiveOutputStream target, final ZipArchiveEntryPredicate predicate) throws IOException {
        final Enumeration<ZipArchiveEntry> src = this.getEntriesInPhysicalOrder();
        while (src.hasMoreElements()) {
            final ZipArchiveEntry entry = src.nextElement();
            if (predicate.test(entry)) {
                target.addRawArchiveEntry(entry, this.getRawInputStream(entry));
            }
        }
    }
    
    public InputStream getInputStream(final ZipArchiveEntry ze) throws IOException {
        if (!(ze instanceof Entry)) {
            return null;
        }
        ZipUtil.checkRequestedFeatures(ze);
        final long start = ze.getDataOffset();
        final InputStream is = new BufferedInputStream(this.createBoundedInputStream(start, ze.getCompressedSize()));
        switch (ZipMethod.getMethodByCode(ze.getMethod())) {
            case STORED: {
                return new StoredStatisticsStream(is);
            }
            case UNSHRINKING: {
                return new UnshrinkingInputStream(is);
            }
            case IMPLODING: {
                return new ExplodingInputStream(ze.getGeneralPurposeBit().getSlidingDictionarySize(), ze.getGeneralPurposeBit().getNumberOfShannonFanoTrees(), is);
            }
            case DEFLATED: {
                final Inflater inflater = new Inflater(true);
                return new InflaterInputStreamWithStatistics(new SequenceInputStream(is, new ByteArrayInputStream(ZipFile.ONE_ZERO_BYTE)), inflater) {
                    @Override
                    public void close() throws IOException {
                        try {
                            super.close();
                        }
                        finally {
                            inflater.end();
                        }
                    }
                };
            }
            case BZIP2: {
                return new BZip2CompressorInputStream(is);
            }
            case ENHANCED_DEFLATED: {
                return new Deflate64CompressorInputStream(is);
            }
            default: {
                throw new ZipException("Found unsupported compression method " + ze.getMethod());
            }
        }
    }
    
    public String getUnixSymlink(final ZipArchiveEntry entry) throws IOException {
        if (entry != null && entry.isUnixSymlink()) {
            try (final InputStream in = this.getInputStream(entry)) {
                return this.zipEncoding.decode(IOUtils.toByteArray(in));
            }
        }
        return null;
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            if (!this.closed) {
                System.err.println("Cleaning up unclosed ZipFile for archive " + this.archiveName);
                this.close();
            }
        }
        finally {
            super.finalize();
        }
    }
    
    private Map<ZipArchiveEntry, NameAndComment> populateFromCentralDirectory() throws IOException {
        final HashMap<ZipArchiveEntry, NameAndComment> noUTF8Flag = new HashMap<ZipArchiveEntry, NameAndComment>();
        this.positionAtCentralDirectory();
        this.wordBbuf.rewind();
        IOUtils.readFully(this.archive, this.wordBbuf);
        long sig = ZipLong.getValue(this.wordBuf);
        if (sig != ZipFile.CFH_SIG && this.startsWithLocalFileHeader()) {
            throw new IOException("central directory is empty, can't expand corrupt archive.");
        }
        while (sig == ZipFile.CFH_SIG) {
            this.readCentralDirectoryEntry(noUTF8Flag);
            this.wordBbuf.rewind();
            IOUtils.readFully(this.archive, this.wordBbuf);
            sig = ZipLong.getValue(this.wordBuf);
        }
        return noUTF8Flag;
    }
    
    private void readCentralDirectoryEntry(final Map<ZipArchiveEntry, NameAndComment> noUTF8Flag) throws IOException {
        this.cfhBbuf.rewind();
        IOUtils.readFully(this.archive, this.cfhBbuf);
        int off = 0;
        final Entry ze = new Entry();
        final int versionMadeBy = ZipShort.getValue(this.cfhBuf, off);
        off += 2;
        ze.setVersionMadeBy(versionMadeBy);
        ze.setPlatform(versionMadeBy >> 8 & 0xF);
        ze.setVersionRequired(ZipShort.getValue(this.cfhBuf, off));
        off += 2;
        final GeneralPurposeBit gpFlag = GeneralPurposeBit.parse(this.cfhBuf, off);
        final boolean hasUTF8Flag = gpFlag.usesUTF8ForNames();
        final ZipEncoding entryEncoding = hasUTF8Flag ? ZipEncodingHelper.UTF8_ZIP_ENCODING : this.zipEncoding;
        if (hasUTF8Flag) {
            ze.setNameSource(ZipArchiveEntry.NameSource.NAME_WITH_EFS_FLAG);
        }
        ze.setGeneralPurposeBit(gpFlag);
        ze.setRawFlag(ZipShort.getValue(this.cfhBuf, off));
        off += 2;
        ze.setMethod(ZipShort.getValue(this.cfhBuf, off));
        off += 2;
        final long time = ZipUtil.dosToJavaTime(ZipLong.getValue(this.cfhBuf, off));
        ze.setTime(time);
        off += 4;
        ze.setCrc(ZipLong.getValue(this.cfhBuf, off));
        off += 4;
        ze.setCompressedSize(ZipLong.getValue(this.cfhBuf, off));
        off += 4;
        ze.setSize(ZipLong.getValue(this.cfhBuf, off));
        off += 4;
        final int fileNameLen = ZipShort.getValue(this.cfhBuf, off);
        off += 2;
        final int extraLen = ZipShort.getValue(this.cfhBuf, off);
        off += 2;
        final int commentLen = ZipShort.getValue(this.cfhBuf, off);
        off += 2;
        final int diskStart = ZipShort.getValue(this.cfhBuf, off);
        off += 2;
        ze.setInternalAttributes(ZipShort.getValue(this.cfhBuf, off));
        off += 2;
        ze.setExternalAttributes(ZipLong.getValue(this.cfhBuf, off));
        off += 4;
        final byte[] fileName = new byte[fileNameLen];
        IOUtils.readFully(this.archive, ByteBuffer.wrap(fileName));
        ze.setName(entryEncoding.decode(fileName), fileName);
        ze.setLocalHeaderOffset(ZipLong.getValue(this.cfhBuf, off));
        this.entries.add(ze);
        final byte[] cdExtraData = new byte[extraLen];
        IOUtils.readFully(this.archive, ByteBuffer.wrap(cdExtraData));
        ze.setCentralDirectoryExtra(cdExtraData);
        this.setSizesAndOffsetFromZip64Extra(ze, diskStart);
        final byte[] comment = new byte[commentLen];
        IOUtils.readFully(this.archive, ByteBuffer.wrap(comment));
        ze.setComment(entryEncoding.decode(comment));
        if (!hasUTF8Flag && this.useUnicodeExtraFields) {
            noUTF8Flag.put(ze, new NameAndComment(fileName, comment));
        }
    }
    
    private void setSizesAndOffsetFromZip64Extra(final ZipArchiveEntry ze, final int diskStart) throws IOException {
        final Zip64ExtendedInformationExtraField z64 = (Zip64ExtendedInformationExtraField)ze.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
        if (z64 != null) {
            final boolean hasUncompressedSize = ze.getSize() == 4294967295L;
            final boolean hasCompressedSize = ze.getCompressedSize() == 4294967295L;
            final boolean hasRelativeHeaderOffset = ze.getLocalHeaderOffset() == 4294967295L;
            z64.reparseCentralDirectoryData(hasUncompressedSize, hasCompressedSize, hasRelativeHeaderOffset, diskStart == 65535);
            if (hasUncompressedSize) {
                ze.setSize(z64.getSize().getLongValue());
            }
            else if (hasCompressedSize) {
                z64.setSize(new ZipEightByteInteger(ze.getSize()));
            }
            if (hasCompressedSize) {
                ze.setCompressedSize(z64.getCompressedSize().getLongValue());
            }
            else if (hasUncompressedSize) {
                z64.setCompressedSize(new ZipEightByteInteger(ze.getCompressedSize()));
            }
            if (hasRelativeHeaderOffset) {
                ze.setLocalHeaderOffset(z64.getRelativeHeaderOffset().getLongValue());
            }
        }
    }
    
    private void positionAtCentralDirectory() throws IOException {
        this.positionAtEndOfCentralDirectoryRecord();
        boolean found = false;
        final boolean searchedForZip64EOCD = this.archive.position() > 20L;
        if (searchedForZip64EOCD) {
            this.archive.position(this.archive.position() - 20L);
            this.wordBbuf.rewind();
            IOUtils.readFully(this.archive, this.wordBbuf);
            found = Arrays.equals(ZipArchiveOutputStream.ZIP64_EOCD_LOC_SIG, this.wordBuf);
        }
        if (!found) {
            if (searchedForZip64EOCD) {
                this.skipBytes(16);
            }
            this.positionAtCentralDirectory32();
        }
        else {
            this.positionAtCentralDirectory64();
        }
    }
    
    private void positionAtCentralDirectory64() throws IOException {
        this.skipBytes(4);
        this.dwordBbuf.rewind();
        IOUtils.readFully(this.archive, this.dwordBbuf);
        this.archive.position(ZipEightByteInteger.getLongValue(this.dwordBuf));
        this.wordBbuf.rewind();
        IOUtils.readFully(this.archive, this.wordBbuf);
        if (!Arrays.equals(this.wordBuf, ZipArchiveOutputStream.ZIP64_EOCD_SIG)) {
            throw new ZipException("archive's ZIP64 end of central directory locator is corrupt.");
        }
        this.skipBytes(44);
        this.dwordBbuf.rewind();
        IOUtils.readFully(this.archive, this.dwordBbuf);
        this.archive.position(ZipEightByteInteger.getLongValue(this.dwordBuf));
    }
    
    private void positionAtCentralDirectory32() throws IOException {
        this.skipBytes(16);
        this.wordBbuf.rewind();
        IOUtils.readFully(this.archive, this.wordBbuf);
        this.archive.position(ZipLong.getValue(this.wordBuf));
    }
    
    private void positionAtEndOfCentralDirectoryRecord() throws IOException {
        final boolean found = this.tryToLocateSignature(22L, 65557L, ZipArchiveOutputStream.EOCD_SIG);
        if (!found) {
            throw new ZipException("archive is not a ZIP archive");
        }
    }
    
    private boolean tryToLocateSignature(final long minDistanceFromEnd, final long maxDistanceFromEnd, final byte[] sig) throws IOException {
        boolean found = false;
        long off = this.archive.size() - minDistanceFromEnd;
        final long stopSearching = Math.max(0L, this.archive.size() - maxDistanceFromEnd);
        if (off >= 0L) {
            while (off >= stopSearching) {
                this.archive.position(off);
                try {
                    this.wordBbuf.rewind();
                    IOUtils.readFully(this.archive, this.wordBbuf);
                    this.wordBbuf.flip();
                }
                catch (EOFException ex) {
                    break;
                }
                int curr = this.wordBbuf.get();
                if (curr == sig[0]) {
                    curr = this.wordBbuf.get();
                    if (curr == sig[1]) {
                        curr = this.wordBbuf.get();
                        if (curr == sig[2]) {
                            curr = this.wordBbuf.get();
                            if (curr == sig[3]) {
                                found = true;
                                break;
                            }
                        }
                    }
                }
                --off;
            }
        }
        if (found) {
            this.archive.position(off);
        }
        return found;
    }
    
    private void skipBytes(final int count) throws IOException {
        final long currentPosition = this.archive.position();
        final long newPosition = currentPosition + count;
        if (newPosition > this.archive.size()) {
            throw new EOFException();
        }
        this.archive.position(newPosition);
    }
    
    private void resolveLocalFileHeaderData(final Map<ZipArchiveEntry, NameAndComment> entriesWithoutUTF8Flag) throws IOException {
        for (final ZipArchiveEntry zipArchiveEntry : this.entries) {
            final Entry ze = (Entry)zipArchiveEntry;
            final long offset = ze.getLocalHeaderOffset();
            this.archive.position(offset + 26L);
            this.wordBbuf.rewind();
            IOUtils.readFully(this.archive, this.wordBbuf);
            this.wordBbuf.flip();
            this.wordBbuf.get(this.shortBuf);
            final int fileNameLen = ZipShort.getValue(this.shortBuf);
            this.wordBbuf.get(this.shortBuf);
            final int extraFieldLen = ZipShort.getValue(this.shortBuf);
            this.skipBytes(fileNameLen);
            final byte[] localExtraData = new byte[extraFieldLen];
            IOUtils.readFully(this.archive, ByteBuffer.wrap(localExtraData));
            ze.setExtra(localExtraData);
            ze.setDataOffset(offset + 26L + 2L + 2L + fileNameLen + extraFieldLen);
            ze.setStreamContiguous(true);
            if (entriesWithoutUTF8Flag.containsKey(ze)) {
                final NameAndComment nc = entriesWithoutUTF8Flag.get(ze);
                ZipUtil.setNameAndCommentFromExtraFields(ze, nc.name, nc.comment);
            }
            final String name = ze.getName();
            LinkedList<ZipArchiveEntry> entriesOfThatName = this.nameMap.get(name);
            if (entriesOfThatName == null) {
                entriesOfThatName = new LinkedList<ZipArchiveEntry>();
                this.nameMap.put(name, entriesOfThatName);
            }
            entriesOfThatName.addLast(ze);
        }
    }
    
    private boolean startsWithLocalFileHeader() throws IOException {
        this.archive.position(0L);
        this.wordBbuf.rewind();
        IOUtils.readFully(this.archive, this.wordBbuf);
        return Arrays.equals(this.wordBuf, ZipArchiveOutputStream.LFH_SIG);
    }
    
    private BoundedInputStream createBoundedInputStream(final long start, final long remaining) {
        return (this.archive instanceof FileChannel) ? new BoundedFileChannelInputStream(start, remaining) : new BoundedInputStream(start, remaining);
    }
    
    static {
        ONE_ZERO_BYTE = new byte[1];
        CFH_SIG = ZipLong.getValue(ZipArchiveOutputStream.CFH_SIG);
    }
    
    private class BoundedInputStream extends InputStream
    {
        private ByteBuffer singleByteBuffer;
        private final long end;
        private long loc;
        
        BoundedInputStream(final long start, final long remaining) {
            this.end = start + remaining;
            if (this.end < start) {
                throw new IllegalArgumentException("Invalid length of stream at offset=" + start + ", length=" + remaining);
            }
            this.loc = start;
        }
        
        @Override
        public synchronized int read() throws IOException {
            if (this.loc >= this.end) {
                return -1;
            }
            if (this.singleByteBuffer == null) {
                this.singleByteBuffer = ByteBuffer.allocate(1);
            }
            else {
                this.singleByteBuffer.rewind();
            }
            final int read = this.read(this.loc, this.singleByteBuffer);
            if (read < 0) {
                return read;
            }
            ++this.loc;
            return this.singleByteBuffer.get() & 0xFF;
        }
        
        @Override
        public synchronized int read(final byte[] b, final int off, int len) throws IOException {
            if (len <= 0) {
                return 0;
            }
            if (len > this.end - this.loc) {
                if (this.loc >= this.end) {
                    return -1;
                }
                len = (int)(this.end - this.loc);
            }
            final ByteBuffer buf = ByteBuffer.wrap(b, off, len);
            final int ret = this.read(this.loc, buf);
            if (ret > 0) {
                this.loc += ret;
                return ret;
            }
            return ret;
        }
        
        protected int read(final long pos, final ByteBuffer buf) throws IOException {
            final int read;
            synchronized (ZipFile.this.archive) {
                ZipFile.this.archive.position(pos);
                read = ZipFile.this.archive.read(buf);
            }
            buf.flip();
            return read;
        }
    }
    
    private class BoundedFileChannelInputStream extends BoundedInputStream
    {
        private final FileChannel archive;
        
        BoundedFileChannelInputStream(final long start, final long remaining) {
            super(start, remaining);
            this.archive = (FileChannel)ZipFile.this.archive;
        }
        
        @Override
        protected int read(final long pos, final ByteBuffer buf) throws IOException {
            final int read = this.archive.read(buf, pos);
            buf.flip();
            return read;
        }
    }
    
    private static final class NameAndComment
    {
        private final byte[] name;
        private final byte[] comment;
        
        private NameAndComment(final byte[] name, final byte[] comment) {
            this.name = name;
            this.comment = comment;
        }
    }
    
    private static class Entry extends ZipArchiveEntry
    {
        Entry() {
        }
        
        @Override
        public int hashCode() {
            return 3 * super.hashCode() + (int)this.getLocalHeaderOffset() + (int)(this.getLocalHeaderOffset() >> 32);
        }
        
        @Override
        public boolean equals(final Object other) {
            if (super.equals(other)) {
                final Entry otherEntry = (Entry)other;
                return this.getLocalHeaderOffset() == otherEntry.getLocalHeaderOffset() && this.getDataOffset() == otherEntry.getDataOffset();
            }
            return false;
        }
    }
    
    private static class StoredStatisticsStream extends CountingInputStream implements InputStreamStatistics
    {
        StoredStatisticsStream(final InputStream in) {
            super(in);
        }
        
        @Override
        public long getCompressedCount() {
            return super.getBytesRead();
        }
        
        @Override
        public long getUncompressedCount() {
            return this.getCompressedCount();
        }
    }
}
