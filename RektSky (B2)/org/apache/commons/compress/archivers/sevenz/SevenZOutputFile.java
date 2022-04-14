package org.apache.commons.compress.archivers.sevenz;

import java.nio.channels.*;
import java.util.zip.*;
import org.apache.commons.compress.utils.*;
import java.nio.file.attribute.*;
import java.nio.file.*;
import org.apache.commons.compress.archivers.*;
import java.io.*;
import java.nio.*;
import java.util.*;

public class SevenZOutputFile implements Closeable
{
    private final SeekableByteChannel channel;
    private final List<SevenZArchiveEntry> files;
    private int numNonEmptyStreams;
    private final CRC32 crc32;
    private final CRC32 compressedCrc32;
    private long fileBytesWritten;
    private boolean finished;
    private CountingOutputStream currentOutputStream;
    private CountingOutputStream[] additionalCountingStreams;
    private Iterable<? extends SevenZMethodConfiguration> contentMethods;
    private final Map<SevenZArchiveEntry, long[]> additionalSizes;
    
    public SevenZOutputFile(final File filename) throws IOException {
        this(Files.newByteChannel(filename.toPath(), EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING), (FileAttribute<?>[])new FileAttribute[0]));
    }
    
    public SevenZOutputFile(final SeekableByteChannel channel) throws IOException {
        this.files = new ArrayList<SevenZArchiveEntry>();
        this.numNonEmptyStreams = 0;
        this.crc32 = new CRC32();
        this.compressedCrc32 = new CRC32();
        this.fileBytesWritten = 0L;
        this.finished = false;
        this.contentMethods = Collections.singletonList(new SevenZMethodConfiguration(SevenZMethod.LZMA2));
        this.additionalSizes = new HashMap<SevenZArchiveEntry, long[]>();
        (this.channel = channel).position(32L);
    }
    
    public void setContentCompression(final SevenZMethod method) {
        this.setContentMethods(Collections.singletonList(new SevenZMethodConfiguration(method)));
    }
    
    public void setContentMethods(final Iterable<? extends SevenZMethodConfiguration> methods) {
        this.contentMethods = reverse(methods);
    }
    
    @Override
    public void close() throws IOException {
        try {
            if (!this.finished) {
                this.finish();
            }
        }
        finally {
            this.channel.close();
        }
    }
    
    public SevenZArchiveEntry createArchiveEntry(final File inputFile, final String entryName) throws IOException {
        final SevenZArchiveEntry entry = new SevenZArchiveEntry();
        entry.setDirectory(inputFile.isDirectory());
        entry.setName(entryName);
        entry.setLastModifiedDate(new Date(inputFile.lastModified()));
        return entry;
    }
    
    public void putArchiveEntry(final ArchiveEntry archiveEntry) throws IOException {
        final SevenZArchiveEntry entry = (SevenZArchiveEntry)archiveEntry;
        this.files.add(entry);
    }
    
    public void closeArchiveEntry() throws IOException {
        if (this.currentOutputStream != null) {
            this.currentOutputStream.flush();
            this.currentOutputStream.close();
        }
        final SevenZArchiveEntry entry = this.files.get(this.files.size() - 1);
        if (this.fileBytesWritten > 0L) {
            entry.setHasStream(true);
            ++this.numNonEmptyStreams;
            entry.setSize(this.currentOutputStream.getBytesWritten());
            entry.setCompressedSize(this.fileBytesWritten);
            entry.setCrcValue(this.crc32.getValue());
            entry.setCompressedCrcValue(this.compressedCrc32.getValue());
            entry.setHasCrc(true);
            if (this.additionalCountingStreams != null) {
                final long[] sizes = new long[this.additionalCountingStreams.length];
                for (int i = 0; i < this.additionalCountingStreams.length; ++i) {
                    sizes[i] = this.additionalCountingStreams[i].getBytesWritten();
                }
                this.additionalSizes.put(entry, sizes);
            }
        }
        else {
            entry.setHasStream(false);
            entry.setSize(0L);
            entry.setCompressedSize(0L);
            entry.setHasCrc(false);
        }
        this.currentOutputStream = null;
        this.additionalCountingStreams = null;
        this.crc32.reset();
        this.compressedCrc32.reset();
        this.fileBytesWritten = 0L;
    }
    
    public void write(final int b) throws IOException {
        this.getCurrentOutputStream().write(b);
    }
    
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (len > 0) {
            this.getCurrentOutputStream().write(b, off, len);
        }
    }
    
    public void finish() throws IOException {
        if (this.finished) {
            throw new IOException("This archive has already been finished");
        }
        this.finished = true;
        final long headerPosition = this.channel.position();
        final ByteArrayOutputStream headerBaos = new ByteArrayOutputStream();
        final DataOutputStream header = new DataOutputStream(headerBaos);
        this.writeHeader(header);
        header.flush();
        final byte[] headerBytes = headerBaos.toByteArray();
        this.channel.write(ByteBuffer.wrap(headerBytes));
        final CRC32 crc32 = new CRC32();
        crc32.update(headerBytes);
        final ByteBuffer bb = ByteBuffer.allocate(SevenZFile.sevenZSignature.length + 2 + 4 + 8 + 8 + 4).order(ByteOrder.LITTLE_ENDIAN);
        this.channel.position(0L);
        bb.put(SevenZFile.sevenZSignature);
        bb.put((byte)0).put((byte)2);
        bb.putInt(0);
        bb.putLong(headerPosition - 32L).putLong(0xFFFFFFFFL & (long)headerBytes.length).putInt((int)crc32.getValue());
        crc32.reset();
        crc32.update(bb.array(), SevenZFile.sevenZSignature.length + 6, 20);
        bb.putInt(SevenZFile.sevenZSignature.length + 2, (int)crc32.getValue());
        bb.flip();
        this.channel.write(bb);
    }
    
    private OutputStream getCurrentOutputStream() throws IOException {
        if (this.currentOutputStream == null) {
            this.currentOutputStream = this.setupFileOutputStream();
        }
        return this.currentOutputStream;
    }
    
    private CountingOutputStream setupFileOutputStream() throws IOException {
        if (this.files.isEmpty()) {
            throw new IllegalStateException("No current 7z entry");
        }
        OutputStream out = new OutputStreamWrapper();
        final ArrayList<CountingOutputStream> moreStreams = new ArrayList<CountingOutputStream>();
        boolean first = true;
        for (final SevenZMethodConfiguration m : this.getContentMethods(this.files.get(this.files.size() - 1))) {
            if (!first) {
                final CountingOutputStream cos = new CountingOutputStream(out);
                moreStreams.add(cos);
                out = cos;
            }
            out = Coders.addEncoder(out, m.getMethod(), m.getOptions());
            first = false;
        }
        if (!moreStreams.isEmpty()) {
            this.additionalCountingStreams = moreStreams.toArray(new CountingOutputStream[moreStreams.size()]);
        }
        return new CountingOutputStream(out) {
            @Override
            public void write(final int b) throws IOException {
                super.write(b);
                SevenZOutputFile.this.crc32.update(b);
            }
            
            @Override
            public void write(final byte[] b) throws IOException {
                super.write(b);
                SevenZOutputFile.this.crc32.update(b);
            }
            
            @Override
            public void write(final byte[] b, final int off, final int len) throws IOException {
                super.write(b, off, len);
                SevenZOutputFile.this.crc32.update(b, off, len);
            }
        };
    }
    
    private Iterable<? extends SevenZMethodConfiguration> getContentMethods(final SevenZArchiveEntry entry) {
        final Iterable<? extends SevenZMethodConfiguration> ms = entry.getContentMethods();
        return (ms == null) ? this.contentMethods : ms;
    }
    
    private void writeHeader(final DataOutput header) throws IOException {
        header.write(1);
        header.write(4);
        this.writeStreamsInfo(header);
        this.writeFilesInfo(header);
        header.write(0);
    }
    
    private void writeStreamsInfo(final DataOutput header) throws IOException {
        if (this.numNonEmptyStreams > 0) {
            this.writePackInfo(header);
            this.writeUnpackInfo(header);
        }
        this.writeSubStreamsInfo(header);
        header.write(0);
    }
    
    private void writePackInfo(final DataOutput header) throws IOException {
        header.write(6);
        this.writeUint64(header, 0L);
        this.writeUint64(header, 0xFFFFFFFFL & (long)this.numNonEmptyStreams);
        header.write(9);
        for (final SevenZArchiveEntry entry : this.files) {
            if (entry.hasStream()) {
                this.writeUint64(header, entry.getCompressedSize());
            }
        }
        header.write(10);
        header.write(1);
        for (final SevenZArchiveEntry entry : this.files) {
            if (entry.hasStream()) {
                header.writeInt(Integer.reverseBytes((int)entry.getCompressedCrcValue()));
            }
        }
        header.write(0);
    }
    
    private void writeUnpackInfo(final DataOutput header) throws IOException {
        header.write(7);
        header.write(11);
        this.writeUint64(header, this.numNonEmptyStreams);
        header.write(0);
        for (final SevenZArchiveEntry entry : this.files) {
            if (entry.hasStream()) {
                this.writeFolder(header, entry);
            }
        }
        header.write(12);
        for (final SevenZArchiveEntry entry : this.files) {
            if (entry.hasStream()) {
                final long[] moreSizes = this.additionalSizes.get(entry);
                if (moreSizes != null) {
                    for (final long s : moreSizes) {
                        this.writeUint64(header, s);
                    }
                }
                this.writeUint64(header, entry.getSize());
            }
        }
        header.write(10);
        header.write(1);
        for (final SevenZArchiveEntry entry : this.files) {
            if (entry.hasStream()) {
                header.writeInt(Integer.reverseBytes((int)entry.getCrcValue()));
            }
        }
        header.write(0);
    }
    
    private void writeFolder(final DataOutput header, final SevenZArchiveEntry entry) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int numCoders = 0;
        for (final SevenZMethodConfiguration m : this.getContentMethods(entry)) {
            ++numCoders;
            this.writeSingleCodec(m, bos);
        }
        this.writeUint64(header, numCoders);
        header.write(bos.toByteArray());
        for (long i = 0L; i < numCoders - 1; ++i) {
            this.writeUint64(header, i + 1L);
            this.writeUint64(header, i);
        }
    }
    
    private void writeSingleCodec(final SevenZMethodConfiguration m, final OutputStream bos) throws IOException {
        final byte[] id = m.getMethod().getId();
        final byte[] properties = Coders.findByMethod(m.getMethod()).getOptionsAsProperties(m.getOptions());
        int codecFlags = id.length;
        if (properties.length > 0) {
            codecFlags |= 0x20;
        }
        bos.write(codecFlags);
        bos.write(id);
        if (properties.length > 0) {
            bos.write(properties.length);
            bos.write(properties);
        }
    }
    
    private void writeSubStreamsInfo(final DataOutput header) throws IOException {
        header.write(8);
        header.write(0);
    }
    
    private void writeFilesInfo(final DataOutput header) throws IOException {
        header.write(5);
        this.writeUint64(header, this.files.size());
        this.writeFileEmptyStreams(header);
        this.writeFileEmptyFiles(header);
        this.writeFileAntiItems(header);
        this.writeFileNames(header);
        this.writeFileCTimes(header);
        this.writeFileATimes(header);
        this.writeFileMTimes(header);
        this.writeFileWindowsAttributes(header);
        header.write(0);
    }
    
    private void writeFileEmptyStreams(final DataOutput header) throws IOException {
        boolean hasEmptyStreams = false;
        for (final SevenZArchiveEntry entry : this.files) {
            if (!entry.hasStream()) {
                hasEmptyStreams = true;
                break;
            }
        }
        if (hasEmptyStreams) {
            header.write(14);
            final BitSet emptyStreams = new BitSet(this.files.size());
            for (int i = 0; i < this.files.size(); ++i) {
                emptyStreams.set(i, !this.files.get(i).hasStream());
            }
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final DataOutputStream out = new DataOutputStream(baos);
            this.writeBits(out, emptyStreams, this.files.size());
            out.flush();
            final byte[] contents = baos.toByteArray();
            this.writeUint64(header, contents.length);
            header.write(contents);
        }
    }
    
    private void writeFileEmptyFiles(final DataOutput header) throws IOException {
        boolean hasEmptyFiles = false;
        int emptyStreamCounter = 0;
        final BitSet emptyFiles = new BitSet(0);
        for (final SevenZArchiveEntry file1 : this.files) {
            if (!file1.hasStream()) {
                final boolean isDir = file1.isDirectory();
                emptyFiles.set(emptyStreamCounter++, !isDir);
                hasEmptyFiles |= !isDir;
            }
        }
        if (hasEmptyFiles) {
            header.write(15);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final DataOutputStream out = new DataOutputStream(baos);
            this.writeBits(out, emptyFiles, emptyStreamCounter);
            out.flush();
            final byte[] contents = baos.toByteArray();
            this.writeUint64(header, contents.length);
            header.write(contents);
        }
    }
    
    private void writeFileAntiItems(final DataOutput header) throws IOException {
        boolean hasAntiItems = false;
        final BitSet antiItems = new BitSet(0);
        int antiItemCounter = 0;
        for (final SevenZArchiveEntry file1 : this.files) {
            if (!file1.hasStream()) {
                final boolean isAnti = file1.isAntiItem();
                antiItems.set(antiItemCounter++, isAnti);
                hasAntiItems |= isAnti;
            }
        }
        if (hasAntiItems) {
            header.write(16);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final DataOutputStream out = new DataOutputStream(baos);
            this.writeBits(out, antiItems, antiItemCounter);
            out.flush();
            final byte[] contents = baos.toByteArray();
            this.writeUint64(header, contents.length);
            header.write(contents);
        }
    }
    
    private void writeFileNames(final DataOutput header) throws IOException {
        header.write(17);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(baos);
        out.write(0);
        for (final SevenZArchiveEntry entry : this.files) {
            out.write(entry.getName().getBytes("UTF-16LE"));
            out.writeShort(0);
        }
        out.flush();
        final byte[] contents = baos.toByteArray();
        this.writeUint64(header, contents.length);
        header.write(contents);
    }
    
    private void writeFileCTimes(final DataOutput header) throws IOException {
        int numCreationDates = 0;
        for (final SevenZArchiveEntry entry : this.files) {
            if (entry.getHasCreationDate()) {
                ++numCreationDates;
            }
        }
        if (numCreationDates > 0) {
            header.write(18);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final DataOutputStream out = new DataOutputStream(baos);
            if (numCreationDates != this.files.size()) {
                out.write(0);
                final BitSet cTimes = new BitSet(this.files.size());
                for (int i = 0; i < this.files.size(); ++i) {
                    cTimes.set(i, this.files.get(i).getHasCreationDate());
                }
                this.writeBits(out, cTimes, this.files.size());
            }
            else {
                out.write(1);
            }
            out.write(0);
            for (final SevenZArchiveEntry entry2 : this.files) {
                if (entry2.getHasCreationDate()) {
                    out.writeLong(Long.reverseBytes(SevenZArchiveEntry.javaTimeToNtfsTime(entry2.getCreationDate())));
                }
            }
            out.flush();
            final byte[] contents = baos.toByteArray();
            this.writeUint64(header, contents.length);
            header.write(contents);
        }
    }
    
    private void writeFileATimes(final DataOutput header) throws IOException {
        int numAccessDates = 0;
        for (final SevenZArchiveEntry entry : this.files) {
            if (entry.getHasAccessDate()) {
                ++numAccessDates;
            }
        }
        if (numAccessDates > 0) {
            header.write(19);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final DataOutputStream out = new DataOutputStream(baos);
            if (numAccessDates != this.files.size()) {
                out.write(0);
                final BitSet aTimes = new BitSet(this.files.size());
                for (int i = 0; i < this.files.size(); ++i) {
                    aTimes.set(i, this.files.get(i).getHasAccessDate());
                }
                this.writeBits(out, aTimes, this.files.size());
            }
            else {
                out.write(1);
            }
            out.write(0);
            for (final SevenZArchiveEntry entry2 : this.files) {
                if (entry2.getHasAccessDate()) {
                    out.writeLong(Long.reverseBytes(SevenZArchiveEntry.javaTimeToNtfsTime(entry2.getAccessDate())));
                }
            }
            out.flush();
            final byte[] contents = baos.toByteArray();
            this.writeUint64(header, contents.length);
            header.write(contents);
        }
    }
    
    private void writeFileMTimes(final DataOutput header) throws IOException {
        int numLastModifiedDates = 0;
        for (final SevenZArchiveEntry entry : this.files) {
            if (entry.getHasLastModifiedDate()) {
                ++numLastModifiedDates;
            }
        }
        if (numLastModifiedDates > 0) {
            header.write(20);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final DataOutputStream out = new DataOutputStream(baos);
            if (numLastModifiedDates != this.files.size()) {
                out.write(0);
                final BitSet mTimes = new BitSet(this.files.size());
                for (int i = 0; i < this.files.size(); ++i) {
                    mTimes.set(i, this.files.get(i).getHasLastModifiedDate());
                }
                this.writeBits(out, mTimes, this.files.size());
            }
            else {
                out.write(1);
            }
            out.write(0);
            for (final SevenZArchiveEntry entry2 : this.files) {
                if (entry2.getHasLastModifiedDate()) {
                    out.writeLong(Long.reverseBytes(SevenZArchiveEntry.javaTimeToNtfsTime(entry2.getLastModifiedDate())));
                }
            }
            out.flush();
            final byte[] contents = baos.toByteArray();
            this.writeUint64(header, contents.length);
            header.write(contents);
        }
    }
    
    private void writeFileWindowsAttributes(final DataOutput header) throws IOException {
        int numWindowsAttributes = 0;
        for (final SevenZArchiveEntry entry : this.files) {
            if (entry.getHasWindowsAttributes()) {
                ++numWindowsAttributes;
            }
        }
        if (numWindowsAttributes > 0) {
            header.write(21);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final DataOutputStream out = new DataOutputStream(baos);
            if (numWindowsAttributes != this.files.size()) {
                out.write(0);
                final BitSet attributes = new BitSet(this.files.size());
                for (int i = 0; i < this.files.size(); ++i) {
                    attributes.set(i, this.files.get(i).getHasWindowsAttributes());
                }
                this.writeBits(out, attributes, this.files.size());
            }
            else {
                out.write(1);
            }
            out.write(0);
            for (final SevenZArchiveEntry entry2 : this.files) {
                if (entry2.getHasWindowsAttributes()) {
                    out.writeInt(Integer.reverseBytes(entry2.getWindowsAttributes()));
                }
            }
            out.flush();
            final byte[] contents = baos.toByteArray();
            this.writeUint64(header, contents.length);
            header.write(contents);
        }
    }
    
    private void writeUint64(final DataOutput header, long value) throws IOException {
        int firstByte = 0;
        int mask = 128;
        int i;
        for (i = 0; i < 8; ++i) {
            if (value < 1L << 7 * (i + 1)) {
                firstByte = (int)((long)firstByte | value >>> 8 * i);
                break;
            }
            firstByte |= mask;
            mask >>>= 1;
        }
        header.write(firstByte);
        while (i > 0) {
            header.write((int)(0xFFL & value));
            value >>>= 8;
            --i;
        }
    }
    
    private void writeBits(final DataOutput header, final BitSet bits, final int length) throws IOException {
        int cache = 0;
        int shift = 7;
        for (int i = 0; i < length; ++i) {
            cache |= (bits.get(i) ? 1 : 0) << shift;
            if (--shift < 0) {
                header.write(cache);
                shift = 7;
                cache = 0;
            }
        }
        if (shift != 7) {
            header.write(cache);
        }
    }
    
    private static <T> Iterable<T> reverse(final Iterable<T> i) {
        final LinkedList<T> l = new LinkedList<T>();
        for (final T t : i) {
            l.addFirst(t);
        }
        return l;
    }
    
    private class OutputStreamWrapper extends OutputStream
    {
        private static final int BUF_SIZE = 8192;
        private final ByteBuffer buffer;
        
        private OutputStreamWrapper() {
            this.buffer = ByteBuffer.allocate(8192);
        }
        
        @Override
        public void write(final int b) throws IOException {
            this.buffer.clear();
            this.buffer.put((byte)b).flip();
            SevenZOutputFile.this.channel.write(this.buffer);
            SevenZOutputFile.this.compressedCrc32.update(b);
            SevenZOutputFile.this.fileBytesWritten++;
        }
        
        @Override
        public void write(final byte[] b) throws IOException {
            this.write(b, 0, b.length);
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            if (len > 8192) {
                SevenZOutputFile.this.channel.write(ByteBuffer.wrap(b, off, len));
            }
            else {
                this.buffer.clear();
                this.buffer.put(b, off, len).flip();
                SevenZOutputFile.this.channel.write(this.buffer);
            }
            SevenZOutputFile.this.compressedCrc32.update(b, off, len);
            SevenZOutputFile.this.fileBytesWritten += len;
        }
        
        @Override
        public void flush() throws IOException {
        }
        
        @Override
        public void close() throws IOException {
        }
    }
}
