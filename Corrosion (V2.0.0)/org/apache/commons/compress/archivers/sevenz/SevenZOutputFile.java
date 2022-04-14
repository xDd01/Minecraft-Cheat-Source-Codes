/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.archivers.sevenz;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.Coders;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZMethod;
import org.apache.commons.compress.archivers.sevenz.SevenZMethodConfiguration;
import org.apache.commons.compress.utils.CountingOutputStream;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SevenZOutputFile
implements Closeable {
    private final RandomAccessFile file;
    private final List<SevenZArchiveEntry> files = new ArrayList<SevenZArchiveEntry>();
    private int numNonEmptyStreams = 0;
    private final CRC32 crc32 = new CRC32();
    private final CRC32 compressedCrc32 = new CRC32();
    private long fileBytesWritten = 0L;
    private boolean finished = false;
    private CountingOutputStream currentOutputStream;
    private CountingOutputStream[] additionalCountingStreams;
    private Iterable<? extends SevenZMethodConfiguration> contentMethods = Collections.singletonList(new SevenZMethodConfiguration(SevenZMethod.LZMA2));
    private final Map<SevenZArchiveEntry, long[]> additionalSizes = new HashMap<SevenZArchiveEntry, long[]>();

    public SevenZOutputFile(File filename) throws IOException {
        this.file = new RandomAccessFile(filename, "rw");
        this.file.seek(32L);
    }

    public void setContentCompression(SevenZMethod method) {
        this.setContentMethods(Collections.singletonList(new SevenZMethodConfiguration(method)));
    }

    public void setContentMethods(Iterable<? extends SevenZMethodConfiguration> methods) {
        this.contentMethods = SevenZOutputFile.reverse(methods);
    }

    @Override
    public void close() throws IOException {
        if (!this.finished) {
            this.finish();
        }
        this.file.close();
    }

    public SevenZArchiveEntry createArchiveEntry(File inputFile, String entryName) throws IOException {
        SevenZArchiveEntry entry = new SevenZArchiveEntry();
        entry.setDirectory(inputFile.isDirectory());
        entry.setName(entryName);
        entry.setLastModifiedDate(new Date(inputFile.lastModified()));
        return entry;
    }

    public void putArchiveEntry(ArchiveEntry archiveEntry) throws IOException {
        SevenZArchiveEntry entry = (SevenZArchiveEntry)archiveEntry;
        this.files.add(entry);
    }

    public void closeArchiveEntry() throws IOException {
        if (this.currentOutputStream != null) {
            this.currentOutputStream.flush();
            this.currentOutputStream.close();
        }
        SevenZArchiveEntry entry = this.files.get(this.files.size() - 1);
        if (this.fileBytesWritten > 0L) {
            entry.setHasStream(true);
            ++this.numNonEmptyStreams;
            entry.setSize(this.currentOutputStream.getBytesWritten());
            entry.setCompressedSize(this.fileBytesWritten);
            entry.setCrcValue(this.crc32.getValue());
            entry.setCompressedCrcValue(this.compressedCrc32.getValue());
            entry.setHasCrc(true);
            if (this.additionalCountingStreams != null) {
                long[] sizes = new long[this.additionalCountingStreams.length];
                for (int i2 = 0; i2 < this.additionalCountingStreams.length; ++i2) {
                    sizes[i2] = this.additionalCountingStreams[i2].getBytesWritten();
                }
                this.additionalSizes.put(entry, sizes);
            }
        } else {
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

    public void write(int b2) throws IOException {
        this.getCurrentOutputStream().write(b2);
    }

    public void write(byte[] b2) throws IOException {
        this.write(b2, 0, b2.length);
    }

    public void write(byte[] b2, int off, int len) throws IOException {
        if (len > 0) {
            this.getCurrentOutputStream().write(b2, off, len);
        }
    }

    public void finish() throws IOException {
        if (this.finished) {
            throw new IOException("This archive has already been finished");
        }
        this.finished = true;
        long headerPosition = this.file.getFilePointer();
        ByteArrayOutputStream headerBaos = new ByteArrayOutputStream();
        DataOutputStream header = new DataOutputStream(headerBaos);
        this.writeHeader(header);
        header.flush();
        byte[] headerBytes = headerBaos.toByteArray();
        this.file.write(headerBytes);
        CRC32 crc32 = new CRC32();
        this.file.seek(0L);
        this.file.write(SevenZFile.sevenZSignature);
        this.file.write(0);
        this.file.write(2);
        ByteArrayOutputStream startHeaderBaos = new ByteArrayOutputStream();
        DataOutputStream startHeaderStream = new DataOutputStream(startHeaderBaos);
        startHeaderStream.writeLong(Long.reverseBytes(headerPosition - 32L));
        startHeaderStream.writeLong(Long.reverseBytes(0xFFFFFFFFL & (long)headerBytes.length));
        crc32.reset();
        crc32.update(headerBytes);
        startHeaderStream.writeInt(Integer.reverseBytes((int)crc32.getValue()));
        startHeaderStream.flush();
        byte[] startHeaderBytes = startHeaderBaos.toByteArray();
        crc32.reset();
        crc32.update(startHeaderBytes);
        this.file.writeInt(Integer.reverseBytes((int)crc32.getValue()));
        this.file.write(startHeaderBytes);
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
        ArrayList<CountingOutputStream> moreStreams = new ArrayList<CountingOutputStream>();
        boolean first = true;
        for (SevenZMethodConfiguration sevenZMethodConfiguration : this.getContentMethods(this.files.get(this.files.size() - 1))) {
            if (!first) {
                CountingOutputStream cos = new CountingOutputStream(out);
                moreStreams.add(cos);
                out = cos;
            }
            out = Coders.addEncoder(out, sevenZMethodConfiguration.getMethod(), sevenZMethodConfiguration.getOptions());
            first = false;
        }
        if (!moreStreams.isEmpty()) {
            this.additionalCountingStreams = moreStreams.toArray(new CountingOutputStream[moreStreams.size()]);
        }
        return new CountingOutputStream(out){

            public void write(int b2) throws IOException {
                super.write(b2);
                SevenZOutputFile.this.crc32.update(b2);
            }

            public void write(byte[] b2) throws IOException {
                super.write(b2);
                SevenZOutputFile.this.crc32.update(b2);
            }

            public void write(byte[] b2, int off, int len) throws IOException {
                super.write(b2, off, len);
                SevenZOutputFile.this.crc32.update(b2, off, len);
            }
        };
    }

    private Iterable<? extends SevenZMethodConfiguration> getContentMethods(SevenZArchiveEntry entry) {
        Iterable<? extends SevenZMethodConfiguration> ms2 = entry.getContentMethods();
        return ms2 == null ? this.contentMethods : ms2;
    }

    private void writeHeader(DataOutput header) throws IOException {
        header.write(1);
        header.write(4);
        this.writeStreamsInfo(header);
        this.writeFilesInfo(header);
        header.write(0);
    }

    private void writeStreamsInfo(DataOutput header) throws IOException {
        if (this.numNonEmptyStreams > 0) {
            this.writePackInfo(header);
            this.writeUnpackInfo(header);
        }
        this.writeSubStreamsInfo(header);
        header.write(0);
    }

    private void writePackInfo(DataOutput header) throws IOException {
        header.write(6);
        this.writeUint64(header, 0L);
        this.writeUint64(header, 0xFFFFFFFFL & (long)this.numNonEmptyStreams);
        header.write(9);
        for (SevenZArchiveEntry entry : this.files) {
            if (!entry.hasStream()) continue;
            this.writeUint64(header, entry.getCompressedSize());
        }
        header.write(10);
        header.write(1);
        for (SevenZArchiveEntry entry : this.files) {
            if (!entry.hasStream()) continue;
            header.writeInt(Integer.reverseBytes((int)entry.getCompressedCrcValue()));
        }
        header.write(0);
    }

    private void writeUnpackInfo(DataOutput header) throws IOException {
        header.write(7);
        header.write(11);
        this.writeUint64(header, this.numNonEmptyStreams);
        header.write(0);
        for (SevenZArchiveEntry entry : this.files) {
            if (!entry.hasStream()) continue;
            this.writeFolder(header, entry);
        }
        header.write(12);
        for (SevenZArchiveEntry entry : this.files) {
            if (!entry.hasStream()) continue;
            long[] moreSizes = this.additionalSizes.get(entry);
            if (moreSizes != null) {
                for (long s2 : moreSizes) {
                    this.writeUint64(header, s2);
                }
            }
            this.writeUint64(header, entry.getSize());
        }
        header.write(10);
        header.write(1);
        for (SevenZArchiveEntry entry : this.files) {
            if (!entry.hasStream()) continue;
            header.writeInt(Integer.reverseBytes((int)entry.getCrcValue()));
        }
        header.write(0);
    }

    private void writeFolder(DataOutput header, SevenZArchiveEntry entry) throws IOException {
        ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
        int numCoders = 0;
        for (SevenZMethodConfiguration sevenZMethodConfiguration : this.getContentMethods(entry)) {
            ++numCoders;
            this.writeSingleCodec(sevenZMethodConfiguration, bos2);
        }
        this.writeUint64(header, numCoders);
        header.write(bos2.toByteArray());
        for (int i2 = 0; i2 < numCoders - 1; ++i2) {
            this.writeUint64(header, i2 + 1);
            this.writeUint64(header, i2);
        }
    }

    private void writeSingleCodec(SevenZMethodConfiguration m2, OutputStream bos2) throws IOException {
        byte[] id2 = m2.getMethod().getId();
        byte[] properties = Coders.findByMethod(m2.getMethod()).getOptionsAsProperties(m2.getOptions());
        int codecFlags = id2.length;
        if (properties.length > 0) {
            codecFlags |= 0x20;
        }
        bos2.write(codecFlags);
        bos2.write(id2);
        if (properties.length > 0) {
            bos2.write(properties.length);
            bos2.write(properties);
        }
    }

    private void writeSubStreamsInfo(DataOutput header) throws IOException {
        header.write(8);
        header.write(0);
    }

    private void writeFilesInfo(DataOutput header) throws IOException {
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

    private void writeFileEmptyStreams(DataOutput header) throws IOException {
        boolean hasEmptyStreams = false;
        for (SevenZArchiveEntry entry : this.files) {
            if (entry.hasStream()) continue;
            hasEmptyStreams = true;
            break;
        }
        if (hasEmptyStreams) {
            header.write(14);
            BitSet emptyStreams = new BitSet(this.files.size());
            for (int i2 = 0; i2 < this.files.size(); ++i2) {
                emptyStreams.set(i2, !this.files.get(i2).hasStream());
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            this.writeBits(out, emptyStreams, this.files.size());
            out.flush();
            byte[] contents = baos.toByteArray();
            this.writeUint64(header, contents.length);
            header.write(contents);
        }
    }

    private void writeFileEmptyFiles(DataOutput header) throws IOException {
        boolean hasEmptyFiles = false;
        int emptyStreamCounter = 0;
        BitSet emptyFiles = new BitSet(0);
        for (int i2 = 0; i2 < this.files.size(); ++i2) {
            if (this.files.get(i2).hasStream()) continue;
            boolean isDir = this.files.get(i2).isDirectory();
            emptyFiles.set(emptyStreamCounter++, !isDir);
            hasEmptyFiles |= !isDir;
        }
        if (hasEmptyFiles) {
            header.write(15);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            this.writeBits(out, emptyFiles, emptyStreamCounter);
            out.flush();
            byte[] contents = baos.toByteArray();
            this.writeUint64(header, contents.length);
            header.write(contents);
        }
    }

    private void writeFileAntiItems(DataOutput header) throws IOException {
        boolean hasAntiItems = false;
        BitSet antiItems = new BitSet(0);
        int antiItemCounter = 0;
        for (int i2 = 0; i2 < this.files.size(); ++i2) {
            if (this.files.get(i2).hasStream()) continue;
            boolean isAnti = this.files.get(i2).isAntiItem();
            antiItems.set(antiItemCounter++, isAnti);
            hasAntiItems |= isAnti;
        }
        if (hasAntiItems) {
            header.write(16);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            this.writeBits(out, antiItems, antiItemCounter);
            out.flush();
            byte[] contents = baos.toByteArray();
            this.writeUint64(header, contents.length);
            header.write(contents);
        }
    }

    private void writeFileNames(DataOutput header) throws IOException {
        header.write(17);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        out.write(0);
        for (SevenZArchiveEntry entry : this.files) {
            out.write(entry.getName().getBytes("UTF-16LE"));
            out.writeShort(0);
        }
        out.flush();
        byte[] contents = baos.toByteArray();
        this.writeUint64(header, contents.length);
        header.write(contents);
    }

    private void writeFileCTimes(DataOutput header) throws IOException {
        int numCreationDates = 0;
        for (SevenZArchiveEntry entry : this.files) {
            if (!entry.getHasCreationDate()) continue;
            ++numCreationDates;
        }
        if (numCreationDates > 0) {
            header.write(18);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            if (numCreationDates != this.files.size()) {
                out.write(0);
                BitSet cTimes = new BitSet(this.files.size());
                for (int i2 = 0; i2 < this.files.size(); ++i2) {
                    cTimes.set(i2, this.files.get(i2).getHasCreationDate());
                }
                this.writeBits(out, cTimes, this.files.size());
            } else {
                out.write(1);
            }
            out.write(0);
            for (SevenZArchiveEntry entry : this.files) {
                if (!entry.getHasCreationDate()) continue;
                out.writeLong(Long.reverseBytes(SevenZArchiveEntry.javaTimeToNtfsTime(entry.getCreationDate())));
            }
            out.flush();
            byte[] contents = baos.toByteArray();
            this.writeUint64(header, contents.length);
            header.write(contents);
        }
    }

    private void writeFileATimes(DataOutput header) throws IOException {
        int numAccessDates = 0;
        for (SevenZArchiveEntry entry : this.files) {
            if (!entry.getHasAccessDate()) continue;
            ++numAccessDates;
        }
        if (numAccessDates > 0) {
            header.write(19);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            if (numAccessDates != this.files.size()) {
                out.write(0);
                BitSet aTimes = new BitSet(this.files.size());
                for (int i2 = 0; i2 < this.files.size(); ++i2) {
                    aTimes.set(i2, this.files.get(i2).getHasAccessDate());
                }
                this.writeBits(out, aTimes, this.files.size());
            } else {
                out.write(1);
            }
            out.write(0);
            for (SevenZArchiveEntry entry : this.files) {
                if (!entry.getHasAccessDate()) continue;
                out.writeLong(Long.reverseBytes(SevenZArchiveEntry.javaTimeToNtfsTime(entry.getAccessDate())));
            }
            out.flush();
            byte[] contents = baos.toByteArray();
            this.writeUint64(header, contents.length);
            header.write(contents);
        }
    }

    private void writeFileMTimes(DataOutput header) throws IOException {
        int numLastModifiedDates = 0;
        for (SevenZArchiveEntry entry : this.files) {
            if (!entry.getHasLastModifiedDate()) continue;
            ++numLastModifiedDates;
        }
        if (numLastModifiedDates > 0) {
            header.write(20);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            if (numLastModifiedDates != this.files.size()) {
                out.write(0);
                BitSet mTimes = new BitSet(this.files.size());
                for (int i2 = 0; i2 < this.files.size(); ++i2) {
                    mTimes.set(i2, this.files.get(i2).getHasLastModifiedDate());
                }
                this.writeBits(out, mTimes, this.files.size());
            } else {
                out.write(1);
            }
            out.write(0);
            for (SevenZArchiveEntry entry : this.files) {
                if (!entry.getHasLastModifiedDate()) continue;
                out.writeLong(Long.reverseBytes(SevenZArchiveEntry.javaTimeToNtfsTime(entry.getLastModifiedDate())));
            }
            out.flush();
            byte[] contents = baos.toByteArray();
            this.writeUint64(header, contents.length);
            header.write(contents);
        }
    }

    private void writeFileWindowsAttributes(DataOutput header) throws IOException {
        int numWindowsAttributes = 0;
        for (SevenZArchiveEntry entry : this.files) {
            if (!entry.getHasWindowsAttributes()) continue;
            ++numWindowsAttributes;
        }
        if (numWindowsAttributes > 0) {
            header.write(21);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            if (numWindowsAttributes != this.files.size()) {
                out.write(0);
                BitSet attributes = new BitSet(this.files.size());
                for (int i2 = 0; i2 < this.files.size(); ++i2) {
                    attributes.set(i2, this.files.get(i2).getHasWindowsAttributes());
                }
                this.writeBits(out, attributes, this.files.size());
            } else {
                out.write(1);
            }
            out.write(0);
            for (SevenZArchiveEntry entry : this.files) {
                if (!entry.getHasWindowsAttributes()) continue;
                out.writeInt(Integer.reverseBytes(entry.getWindowsAttributes()));
            }
            out.flush();
            byte[] contents = baos.toByteArray();
            this.writeUint64(header, contents.length);
            header.write(contents);
        }
    }

    private void writeUint64(DataOutput header, long value) throws IOException {
        int i2;
        int firstByte = 0;
        int mask = 128;
        for (i2 = 0; i2 < 8; ++i2) {
            if (value < 1L << 7 * (i2 + 1)) {
                firstByte = (int)((long)firstByte | value >>> 8 * i2);
                break;
            }
            firstByte |= mask;
            mask >>>= 1;
        }
        header.write(firstByte);
        while (i2 > 0) {
            header.write((int)(0xFFL & value));
            value >>>= 8;
            --i2;
        }
    }

    private void writeBits(DataOutput header, BitSet bits, int length) throws IOException {
        int cache = 0;
        int shift = 7;
        for (int i2 = 0; i2 < length; ++i2) {
            cache |= (bits.get(i2) ? 1 : 0) << shift;
            if (--shift >= 0) continue;
            header.write(cache);
            shift = 7;
            cache = 0;
        }
        if (shift != 7) {
            header.write(cache);
        }
    }

    private static <T> Iterable<T> reverse(Iterable<T> i2) {
        LinkedList<T> l2 = new LinkedList<T>();
        for (T t2 : i2) {
            l2.addFirst(t2);
        }
        return l2;
    }

    private class OutputStreamWrapper
    extends OutputStream {
        private OutputStreamWrapper() {
        }

        public void write(int b2) throws IOException {
            SevenZOutputFile.this.file.write(b2);
            SevenZOutputFile.this.compressedCrc32.update(b2);
            SevenZOutputFile.this.fileBytesWritten++;
        }

        public void write(byte[] b2) throws IOException {
            this.write(b2, 0, b2.length);
        }

        public void write(byte[] b2, int off, int len) throws IOException {
            SevenZOutputFile.this.file.write(b2, off, len);
            SevenZOutputFile.this.compressedCrc32.update(b2, off, len);
            SevenZOutputFile.this.fileBytesWritten += len;
        }

        public void flush() throws IOException {
        }

        public void close() throws IOException {
        }
    }
}

