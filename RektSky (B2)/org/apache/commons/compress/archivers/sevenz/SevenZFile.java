package org.apache.commons.compress.archivers.sevenz;

import java.nio.file.attribute.*;
import java.nio.file.*;
import java.util.zip.*;
import java.util.*;
import java.io.*;
import org.apache.commons.compress.utils.*;
import java.nio.channels.*;
import java.nio.*;
import java.nio.charset.*;

public class SevenZFile implements Closeable
{
    static final int SIGNATURE_HEADER_SIZE = 32;
    private final String fileName;
    private SeekableByteChannel channel;
    private final Archive archive;
    private int currentEntryIndex;
    private int currentFolderIndex;
    private InputStream currentFolderInputStream;
    private byte[] password;
    private long compressedBytesReadFromCurrentEntry;
    private long uncompressedBytesReadFromCurrentEntry;
    private final ArrayList<InputStream> deferredBlockStreams;
    static final byte[] sevenZSignature;
    private static final CharsetEncoder PASSWORD_ENCODER;
    
    public SevenZFile(final File filename, final char[] password) throws IOException {
        this(Files.newByteChannel(filename.toPath(), EnumSet.of(StandardOpenOption.READ), (FileAttribute<?>[])new FileAttribute[0]), filename.getAbsolutePath(), utf16Decode(password), true);
    }
    
    @Deprecated
    public SevenZFile(final File filename, final byte[] password) throws IOException {
        this(Files.newByteChannel(filename.toPath(), EnumSet.of(StandardOpenOption.READ), (FileAttribute<?>[])new FileAttribute[0]), filename.getAbsolutePath(), password, true);
    }
    
    public SevenZFile(final SeekableByteChannel channel) throws IOException {
        this(channel, "unknown archive", (char[])null);
    }
    
    public SevenZFile(final SeekableByteChannel channel, final char[] password) throws IOException {
        this(channel, "unknown archive", utf16Decode(password));
    }
    
    public SevenZFile(final SeekableByteChannel channel, final String filename, final char[] password) throws IOException {
        this(channel, filename, utf16Decode(password), false);
    }
    
    public SevenZFile(final SeekableByteChannel channel, final String filename) throws IOException {
        this(channel, filename, null, false);
    }
    
    @Deprecated
    public SevenZFile(final SeekableByteChannel channel, final byte[] password) throws IOException {
        this(channel, "unknown archive", password);
    }
    
    @Deprecated
    public SevenZFile(final SeekableByteChannel channel, final String filename, final byte[] password) throws IOException {
        this(channel, filename, password, false);
    }
    
    private SevenZFile(final SeekableByteChannel channel, final String filename, final byte[] password, final boolean closeOnError) throws IOException {
        this.currentEntryIndex = -1;
        this.currentFolderIndex = -1;
        this.currentFolderInputStream = null;
        this.deferredBlockStreams = new ArrayList<InputStream>();
        boolean succeeded = false;
        this.channel = channel;
        this.fileName = filename;
        try {
            this.archive = this.readHeaders(password);
            if (password != null) {
                this.password = Arrays.copyOf(password, password.length);
            }
            else {
                this.password = null;
            }
            succeeded = true;
        }
        finally {
            if (!succeeded && closeOnError) {
                this.channel.close();
            }
        }
    }
    
    public SevenZFile(final File filename) throws IOException {
        this(filename, (char[])null);
    }
    
    @Override
    public void close() throws IOException {
        if (this.channel != null) {
            try {
                this.channel.close();
            }
            finally {
                this.channel = null;
                if (this.password != null) {
                    Arrays.fill(this.password, (byte)0);
                }
                this.password = null;
            }
        }
    }
    
    public SevenZArchiveEntry getNextEntry() throws IOException {
        if (this.currentEntryIndex >= this.archive.files.length - 1) {
            return null;
        }
        ++this.currentEntryIndex;
        final SevenZArchiveEntry entry = this.archive.files[this.currentEntryIndex];
        this.buildDecodingStream();
        final long n = 0L;
        this.compressedBytesReadFromCurrentEntry = n;
        this.uncompressedBytesReadFromCurrentEntry = n;
        return entry;
    }
    
    public Iterable<SevenZArchiveEntry> getEntries() {
        return Arrays.asList(this.archive.files);
    }
    
    private Archive readHeaders(final byte[] password) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN);
        this.readFully(buf);
        final byte[] signature = new byte[6];
        buf.get(signature);
        if (!Arrays.equals(signature, SevenZFile.sevenZSignature)) {
            throw new IOException("Bad 7z signature");
        }
        final byte archiveVersionMajor = buf.get();
        final byte archiveVersionMinor = buf.get();
        if (archiveVersionMajor != 0) {
            throw new IOException(String.format("Unsupported 7z version (%d,%d)", archiveVersionMajor, archiveVersionMinor));
        }
        final long startHeaderCrc = 0xFFFFFFFFL & (long)buf.getInt();
        final StartHeader startHeader = this.readStartHeader(startHeaderCrc);
        final int nextHeaderSizeInt = (int)startHeader.nextHeaderSize;
        if (nextHeaderSizeInt != startHeader.nextHeaderSize) {
            throw new IOException("cannot handle nextHeaderSize " + startHeader.nextHeaderSize);
        }
        this.channel.position(32L + startHeader.nextHeaderOffset);
        buf = ByteBuffer.allocate(nextHeaderSizeInt).order(ByteOrder.LITTLE_ENDIAN);
        this.readFully(buf);
        final CRC32 crc = new CRC32();
        crc.update(buf.array());
        if (startHeader.nextHeaderCrc != crc.getValue()) {
            throw new IOException("NextHeader CRC mismatch");
        }
        Archive archive = new Archive();
        int nid = getUnsignedByte(buf);
        if (nid == 23) {
            buf = this.readEncodedHeader(buf, archive, password);
            archive = new Archive();
            nid = getUnsignedByte(buf);
        }
        if (nid == 1) {
            this.readHeader(buf, archive);
            return archive;
        }
        throw new IOException("Broken or unsupported archive: no Header");
    }
    
    private StartHeader readStartHeader(final long startHeaderCrc) throws IOException {
        final StartHeader startHeader = new StartHeader();
        try (final DataInputStream dataInputStream = new DataInputStream(new CRC32VerifyingInputStream(new BoundedSeekableByteChannelInputStream(this.channel, 20L), 20L, startHeaderCrc))) {
            startHeader.nextHeaderOffset = Long.reverseBytes(dataInputStream.readLong());
            startHeader.nextHeaderSize = Long.reverseBytes(dataInputStream.readLong());
            startHeader.nextHeaderCrc = (0xFFFFFFFFL & (long)Integer.reverseBytes(dataInputStream.readInt()));
            return startHeader;
        }
    }
    
    private void readHeader(final ByteBuffer header, final Archive archive) throws IOException {
        int nid = getUnsignedByte(header);
        if (nid == 2) {
            this.readArchiveProperties(header);
            nid = getUnsignedByte(header);
        }
        if (nid == 3) {
            throw new IOException("Additional streams unsupported");
        }
        if (nid == 4) {
            this.readStreamsInfo(header, archive);
            nid = getUnsignedByte(header);
        }
        if (nid == 5) {
            this.readFilesInfo(header, archive);
            nid = getUnsignedByte(header);
        }
        if (nid != 0) {
            throw new IOException("Badly terminated header, found " + nid);
        }
    }
    
    private void readArchiveProperties(final ByteBuffer input) throws IOException {
        for (int nid = getUnsignedByte(input); nid != 0; nid = getUnsignedByte(input)) {
            final long propertySize = readUint64(input);
            final byte[] property = new byte[(int)propertySize];
            input.get(property);
        }
    }
    
    private ByteBuffer readEncodedHeader(final ByteBuffer header, final Archive archive, final byte[] password) throws IOException {
        this.readStreamsInfo(header, archive);
        final Folder folder = archive.folders[0];
        final int firstPackStreamIndex = 0;
        final long folderOffset = 32L + archive.packPos + 0L;
        this.channel.position(folderOffset);
        InputStream inputStreamStack = new BoundedSeekableByteChannelInputStream(this.channel, archive.packSizes[0]);
        for (final Coder coder : folder.getOrderedCoders()) {
            if (coder.numInStreams != 1L || coder.numOutStreams != 1L) {
                throw new IOException("Multi input/output stream coders are not yet supported");
            }
            inputStreamStack = Coders.addDecoder(this.fileName, inputStreamStack, folder.getUnpackSizeForCoder(coder), coder, password);
        }
        if (folder.hasCrc) {
            inputStreamStack = new CRC32VerifyingInputStream(inputStreamStack, folder.getUnpackSize(), folder.crc);
        }
        final byte[] nextHeader = new byte[(int)folder.getUnpackSize()];
        try (final DataInputStream nextHeaderInputStream = new DataInputStream(inputStreamStack)) {
            nextHeaderInputStream.readFully(nextHeader);
        }
        return ByteBuffer.wrap(nextHeader).order(ByteOrder.LITTLE_ENDIAN);
    }
    
    private void readStreamsInfo(final ByteBuffer header, final Archive archive) throws IOException {
        int nid = getUnsignedByte(header);
        if (nid == 6) {
            this.readPackInfo(header, archive);
            nid = getUnsignedByte(header);
        }
        if (nid == 7) {
            this.readUnpackInfo(header, archive);
            nid = getUnsignedByte(header);
        }
        else {
            archive.folders = new Folder[0];
        }
        if (nid == 8) {
            this.readSubStreamsInfo(header, archive);
            nid = getUnsignedByte(header);
        }
        if (nid != 0) {
            throw new IOException("Badly terminated StreamsInfo");
        }
    }
    
    private void readPackInfo(final ByteBuffer header, final Archive archive) throws IOException {
        archive.packPos = readUint64(header);
        final long numPackStreams = readUint64(header);
        int nid = getUnsignedByte(header);
        if (nid == 9) {
            archive.packSizes = new long[(int)numPackStreams];
            for (int i = 0; i < archive.packSizes.length; ++i) {
                archive.packSizes[i] = readUint64(header);
            }
            nid = getUnsignedByte(header);
        }
        if (nid == 10) {
            archive.packCrcsDefined = this.readAllOrBits(header, (int)numPackStreams);
            archive.packCrcs = new long[(int)numPackStreams];
            for (int i = 0; i < (int)numPackStreams; ++i) {
                if (archive.packCrcsDefined.get(i)) {
                    archive.packCrcs[i] = (0xFFFFFFFFL & (long)header.getInt());
                }
            }
            nid = getUnsignedByte(header);
        }
        if (nid != 0) {
            throw new IOException("Badly terminated PackInfo (" + nid + ")");
        }
    }
    
    private void readUnpackInfo(final ByteBuffer header, final Archive archive) throws IOException {
        int nid = getUnsignedByte(header);
        if (nid != 11) {
            throw new IOException("Expected kFolder, got " + nid);
        }
        final long numFolders = readUint64(header);
        final Folder[] folders = new Folder[(int)numFolders];
        archive.folders = folders;
        final int external = getUnsignedByte(header);
        if (external != 0) {
            throw new IOException("External unsupported");
        }
        for (int i = 0; i < (int)numFolders; ++i) {
            folders[i] = this.readFolder(header);
        }
        nid = getUnsignedByte(header);
        if (nid != 12) {
            throw new IOException("Expected kCodersUnpackSize, got " + nid);
        }
        for (final Folder folder : folders) {
            folder.unpackSizes = new long[(int)folder.totalOutputStreams];
            for (int j = 0; j < folder.totalOutputStreams; ++j) {
                folder.unpackSizes[j] = readUint64(header);
            }
        }
        nid = getUnsignedByte(header);
        if (nid == 10) {
            final BitSet crcsDefined = this.readAllOrBits(header, (int)numFolders);
            for (int k = 0; k < (int)numFolders; ++k) {
                if (crcsDefined.get(k)) {
                    folders[k].hasCrc = true;
                    folders[k].crc = (0xFFFFFFFFL & (long)header.getInt());
                }
                else {
                    folders[k].hasCrc = false;
                }
            }
            nid = getUnsignedByte(header);
        }
        if (nid != 0) {
            throw new IOException("Badly terminated UnpackInfo");
        }
    }
    
    private void readSubStreamsInfo(final ByteBuffer header, final Archive archive) throws IOException {
        for (final Folder folder : archive.folders) {
            folder.numUnpackSubStreams = 1;
        }
        int totalUnpackStreams = archive.folders.length;
        int nid = getUnsignedByte(header);
        if (nid == 13) {
            totalUnpackStreams = 0;
            for (final Folder folder2 : archive.folders) {
                final long numStreams = readUint64(header);
                folder2.numUnpackSubStreams = (int)numStreams;
                totalUnpackStreams += (int)numStreams;
            }
            nid = getUnsignedByte(header);
        }
        final SubStreamsInfo subStreamsInfo = new SubStreamsInfo();
        subStreamsInfo.unpackSizes = new long[totalUnpackStreams];
        subStreamsInfo.hasCrc = new BitSet(totalUnpackStreams);
        subStreamsInfo.crcs = new long[totalUnpackStreams];
        int nextUnpackStream = 0;
        for (final Folder folder3 : archive.folders) {
            if (folder3.numUnpackSubStreams != 0) {
                long sum = 0L;
                if (nid == 9) {
                    for (int i = 0; i < folder3.numUnpackSubStreams - 1; ++i) {
                        final long size = readUint64(header);
                        subStreamsInfo.unpackSizes[nextUnpackStream++] = size;
                        sum += size;
                    }
                }
                subStreamsInfo.unpackSizes[nextUnpackStream++] = folder3.getUnpackSize() - sum;
            }
        }
        if (nid == 9) {
            nid = getUnsignedByte(header);
        }
        int numDigests = 0;
        for (final Folder folder4 : archive.folders) {
            if (folder4.numUnpackSubStreams != 1 || !folder4.hasCrc) {
                numDigests += folder4.numUnpackSubStreams;
            }
        }
        if (nid == 10) {
            final BitSet hasMissingCrc = this.readAllOrBits(header, numDigests);
            final long[] missingCrcs = new long[numDigests];
            for (int j = 0; j < numDigests; ++j) {
                if (hasMissingCrc.get(j)) {
                    missingCrcs[j] = (0xFFFFFFFFL & (long)header.getInt());
                }
            }
            int nextCrc = 0;
            int nextMissingCrc = 0;
            for (final Folder folder5 : archive.folders) {
                if (folder5.numUnpackSubStreams == 1 && folder5.hasCrc) {
                    subStreamsInfo.hasCrc.set(nextCrc, true);
                    subStreamsInfo.crcs[nextCrc] = folder5.crc;
                    ++nextCrc;
                }
                else {
                    for (int k = 0; k < folder5.numUnpackSubStreams; ++k) {
                        subStreamsInfo.hasCrc.set(nextCrc, hasMissingCrc.get(nextMissingCrc));
                        subStreamsInfo.crcs[nextCrc] = missingCrcs[nextMissingCrc];
                        ++nextCrc;
                        ++nextMissingCrc;
                    }
                }
            }
            nid = getUnsignedByte(header);
        }
        if (nid != 0) {
            throw new IOException("Badly terminated SubStreamsInfo");
        }
        archive.subStreamsInfo = subStreamsInfo;
    }
    
    private Folder readFolder(final ByteBuffer header) throws IOException {
        final Folder folder = new Folder();
        final long numCoders = readUint64(header);
        final Coder[] coders = new Coder[(int)numCoders];
        long totalInStreams = 0L;
        long totalOutStreams = 0L;
        for (int i = 0; i < coders.length; ++i) {
            coders[i] = new Coder();
            final int bits = getUnsignedByte(header);
            final int idSize = bits & 0xF;
            final boolean isSimple = (bits & 0x10) == 0x0;
            final boolean hasAttributes = (bits & 0x20) != 0x0;
            final boolean moreAlternativeMethods = (bits & 0x80) != 0x0;
            header.get(coders[i].decompressionMethodId = new byte[idSize]);
            if (isSimple) {
                coders[i].numInStreams = 1L;
                coders[i].numOutStreams = 1L;
            }
            else {
                coders[i].numInStreams = readUint64(header);
                coders[i].numOutStreams = readUint64(header);
            }
            totalInStreams += coders[i].numInStreams;
            totalOutStreams += coders[i].numOutStreams;
            if (hasAttributes) {
                final long propertiesSize = readUint64(header);
                header.get(coders[i].properties = new byte[(int)propertiesSize]);
            }
            if (moreAlternativeMethods) {
                throw new IOException("Alternative methods are unsupported, please report. The reference implementation doesn't support them either.");
            }
        }
        folder.coders = coders;
        folder.totalInputStreams = totalInStreams;
        folder.totalOutputStreams = totalOutStreams;
        if (totalOutStreams == 0L) {
            throw new IOException("Total output streams can't be 0");
        }
        final long numBindPairs = totalOutStreams - 1L;
        final BindPair[] bindPairs = new BindPair[(int)numBindPairs];
        for (int j = 0; j < bindPairs.length; ++j) {
            bindPairs[j] = new BindPair();
            bindPairs[j].inIndex = readUint64(header);
            bindPairs[j].outIndex = readUint64(header);
        }
        folder.bindPairs = bindPairs;
        if (totalInStreams < numBindPairs) {
            throw new IOException("Total input streams can't be less than the number of bind pairs");
        }
        final long numPackedStreams = totalInStreams - numBindPairs;
        final long[] packedStreams = new long[(int)numPackedStreams];
        if (numPackedStreams == 1L) {
            int k;
            for (k = 0; k < (int)totalInStreams && folder.findBindPairForInStream(k) >= 0; ++k) {}
            if (k == (int)totalInStreams) {
                throw new IOException("Couldn't find stream's bind pair index");
            }
            packedStreams[0] = k;
        }
        else {
            for (int k = 0; k < (int)numPackedStreams; ++k) {
                packedStreams[k] = readUint64(header);
            }
        }
        folder.packedStreams = packedStreams;
        return folder;
    }
    
    private BitSet readAllOrBits(final ByteBuffer header, final int size) throws IOException {
        final int areAllDefined = getUnsignedByte(header);
        BitSet bits;
        if (areAllDefined != 0) {
            bits = new BitSet(size);
            for (int i = 0; i < size; ++i) {
                bits.set(i, true);
            }
        }
        else {
            bits = this.readBits(header, size);
        }
        return bits;
    }
    
    private BitSet readBits(final ByteBuffer header, final int size) throws IOException {
        final BitSet bits = new BitSet(size);
        int mask = 0;
        int cache = 0;
        for (int i = 0; i < size; ++i) {
            if (mask == 0) {
                mask = 128;
                cache = getUnsignedByte(header);
            }
            bits.set(i, (cache & mask) != 0x0);
            mask >>>= 1;
        }
        return bits;
    }
    
    private void readFilesInfo(final ByteBuffer header, final Archive archive) throws IOException {
        final long numFiles = readUint64(header);
        final SevenZArchiveEntry[] files = new SevenZArchiveEntry[(int)numFiles];
        for (int i = 0; i < files.length; ++i) {
            files[i] = new SevenZArchiveEntry();
        }
        BitSet isEmptyStream = null;
        BitSet isEmptyFile = null;
        BitSet isAnti = null;
        while (true) {
            final int propertyType = getUnsignedByte(header);
            if (propertyType == 0) {
                int nonEmptyFileCounter = 0;
                int emptyFileCounter = 0;
                for (int j = 0; j < files.length; ++j) {
                    files[j].setHasStream(isEmptyStream == null || !isEmptyStream.get(j));
                    if (files[j].hasStream()) {
                        files[j].setDirectory(false);
                        files[j].setAntiItem(false);
                        files[j].setHasCrc(archive.subStreamsInfo.hasCrc.get(nonEmptyFileCounter));
                        files[j].setCrcValue(archive.subStreamsInfo.crcs[nonEmptyFileCounter]);
                        files[j].setSize(archive.subStreamsInfo.unpackSizes[nonEmptyFileCounter]);
                        ++nonEmptyFileCounter;
                    }
                    else {
                        files[j].setDirectory(isEmptyFile == null || !isEmptyFile.get(emptyFileCounter));
                        files[j].setAntiItem(isAnti != null && isAnti.get(emptyFileCounter));
                        files[j].setHasCrc(false);
                        files[j].setSize(0L);
                        ++emptyFileCounter;
                    }
                }
                archive.files = files;
                this.calculateStreamMap(archive);
                return;
            }
            final long size = readUint64(header);
            switch (propertyType) {
                case 14: {
                    isEmptyStream = this.readBits(header, files.length);
                    continue;
                }
                case 15: {
                    if (isEmptyStream == null) {
                        throw new IOException("Header format error: kEmptyStream must appear before kEmptyFile");
                    }
                    isEmptyFile = this.readBits(header, isEmptyStream.cardinality());
                    continue;
                }
                case 16: {
                    if (isEmptyStream == null) {
                        throw new IOException("Header format error: kEmptyStream must appear before kAnti");
                    }
                    isAnti = this.readBits(header, isEmptyStream.cardinality());
                    continue;
                }
                case 17: {
                    final int external = getUnsignedByte(header);
                    if (external != 0) {
                        throw new IOException("Not implemented");
                    }
                    if ((size - 1L & 0x1L) != 0x0L) {
                        throw new IOException("File names length invalid");
                    }
                    final byte[] names = new byte[(int)(size - 1L)];
                    header.get(names);
                    int nextFile = 0;
                    int nextName = 0;
                    for (int k = 0; k < names.length; k += 2) {
                        if (names[k] == 0 && names[k + 1] == 0) {
                            files[nextFile++].setName(new String(names, nextName, k - nextName, "UTF-16LE"));
                            nextName = k + 2;
                        }
                    }
                    if (nextName != names.length || nextFile != files.length) {
                        throw new IOException("Error parsing file names");
                    }
                    continue;
                }
                case 18: {
                    final BitSet timesDefined = this.readAllOrBits(header, files.length);
                    final int external2 = getUnsignedByte(header);
                    if (external2 != 0) {
                        throw new IOException("Unimplemented");
                    }
                    for (int l = 0; l < files.length; ++l) {
                        files[l].setHasCreationDate(timesDefined.get(l));
                        if (files[l].getHasCreationDate()) {
                            files[l].setCreationDate(header.getLong());
                        }
                    }
                    continue;
                }
                case 19: {
                    final BitSet timesDefined = this.readAllOrBits(header, files.length);
                    final int external2 = getUnsignedByte(header);
                    if (external2 != 0) {
                        throw new IOException("Unimplemented");
                    }
                    for (int l = 0; l < files.length; ++l) {
                        files[l].setHasAccessDate(timesDefined.get(l));
                        if (files[l].getHasAccessDate()) {
                            files[l].setAccessDate(header.getLong());
                        }
                    }
                    continue;
                }
                case 20: {
                    final BitSet timesDefined = this.readAllOrBits(header, files.length);
                    final int external2 = getUnsignedByte(header);
                    if (external2 != 0) {
                        throw new IOException("Unimplemented");
                    }
                    for (int l = 0; l < files.length; ++l) {
                        files[l].setHasLastModifiedDate(timesDefined.get(l));
                        if (files[l].getHasLastModifiedDate()) {
                            files[l].setLastModifiedDate(header.getLong());
                        }
                    }
                    continue;
                }
                case 21: {
                    final BitSet attributesDefined = this.readAllOrBits(header, files.length);
                    final int external2 = getUnsignedByte(header);
                    if (external2 != 0) {
                        throw new IOException("Unimplemented");
                    }
                    for (int l = 0; l < files.length; ++l) {
                        files[l].setHasWindowsAttributes(attributesDefined.get(l));
                        if (files[l].getHasWindowsAttributes()) {
                            files[l].setWindowsAttributes(header.getInt());
                        }
                    }
                    continue;
                }
                case 24: {
                    throw new IOException("kStartPos is unsupported, please report");
                }
                case 25: {
                    if (skipBytesFully(header, size) < size) {
                        throw new IOException("Incomplete kDummy property");
                    }
                    continue;
                }
                default: {
                    if (skipBytesFully(header, size) < size) {
                        throw new IOException("Incomplete property of type " + propertyType);
                    }
                    continue;
                }
            }
        }
    }
    
    private void calculateStreamMap(final Archive archive) throws IOException {
        final StreamMap streamMap = new StreamMap();
        int nextFolderPackStreamIndex = 0;
        final int numFolders = (archive.folders != null) ? archive.folders.length : 0;
        streamMap.folderFirstPackStreamIndex = new int[numFolders];
        for (int i = 0; i < numFolders; ++i) {
            streamMap.folderFirstPackStreamIndex[i] = nextFolderPackStreamIndex;
            nextFolderPackStreamIndex += archive.folders[i].packedStreams.length;
        }
        long nextPackStreamOffset = 0L;
        final int numPackSizes = (archive.packSizes != null) ? archive.packSizes.length : 0;
        streamMap.packStreamOffsets = new long[numPackSizes];
        for (int j = 0; j < numPackSizes; ++j) {
            streamMap.packStreamOffsets[j] = nextPackStreamOffset;
            nextPackStreamOffset += archive.packSizes[j];
        }
        streamMap.folderFirstFileIndex = new int[numFolders];
        streamMap.fileFolderIndex = new int[archive.files.length];
        int nextFolderIndex = 0;
        int nextFolderUnpackStreamIndex = 0;
        for (int k = 0; k < archive.files.length; ++k) {
            if (!archive.files[k].hasStream() && nextFolderUnpackStreamIndex == 0) {
                streamMap.fileFolderIndex[k] = -1;
            }
            else {
                if (nextFolderUnpackStreamIndex == 0) {
                    while (nextFolderIndex < archive.folders.length) {
                        streamMap.folderFirstFileIndex[nextFolderIndex] = k;
                        if (archive.folders[nextFolderIndex].numUnpackSubStreams > 0) {
                            break;
                        }
                        ++nextFolderIndex;
                    }
                    if (nextFolderIndex >= archive.folders.length) {
                        throw new IOException("Too few folders in archive");
                    }
                }
                streamMap.fileFolderIndex[k] = nextFolderIndex;
                if (archive.files[k].hasStream()) {
                    if (++nextFolderUnpackStreamIndex >= archive.folders[nextFolderIndex].numUnpackSubStreams) {
                        ++nextFolderIndex;
                        nextFolderUnpackStreamIndex = 0;
                    }
                }
            }
        }
        archive.streamMap = streamMap;
    }
    
    private void buildDecodingStream() throws IOException {
        final int folderIndex = this.archive.streamMap.fileFolderIndex[this.currentEntryIndex];
        if (folderIndex < 0) {
            this.deferredBlockStreams.clear();
            return;
        }
        final SevenZArchiveEntry file = this.archive.files[this.currentEntryIndex];
        if (this.currentFolderIndex == folderIndex) {
            file.setContentMethods(this.archive.files[this.currentEntryIndex - 1].getContentMethods());
        }
        else {
            this.currentFolderIndex = folderIndex;
            this.deferredBlockStreams.clear();
            if (this.currentFolderInputStream != null) {
                this.currentFolderInputStream.close();
                this.currentFolderInputStream = null;
            }
            final Folder folder = this.archive.folders[folderIndex];
            final int firstPackStreamIndex = this.archive.streamMap.folderFirstPackStreamIndex[folderIndex];
            final long folderOffset = 32L + this.archive.packPos + this.archive.streamMap.packStreamOffsets[firstPackStreamIndex];
            this.currentFolderInputStream = this.buildDecoderStack(folder, folderOffset, firstPackStreamIndex, file);
        }
        InputStream fileStream = new BoundedInputStream(this.currentFolderInputStream, file.getSize());
        if (file.getHasCrc()) {
            fileStream = new CRC32VerifyingInputStream(fileStream, file.getSize(), file.getCrcValue());
        }
        this.deferredBlockStreams.add(fileStream);
    }
    
    private InputStream buildDecoderStack(final Folder folder, final long folderOffset, final int firstPackStreamIndex, final SevenZArchiveEntry entry) throws IOException {
        this.channel.position(folderOffset);
        InputStream inputStreamStack = new FilterInputStream(new BufferedInputStream(new BoundedSeekableByteChannelInputStream(this.channel, this.archive.packSizes[firstPackStreamIndex]))) {
            @Override
            public int read() throws IOException {
                final int r = this.in.read();
                if (r >= 0) {
                    this.count(1);
                }
                return r;
            }
            
            @Override
            public int read(final byte[] b) throws IOException {
                return this.read(b, 0, b.length);
            }
            
            @Override
            public int read(final byte[] b, final int off, final int len) throws IOException {
                final int r = this.in.read(b, off, len);
                if (r >= 0) {
                    this.count(r);
                }
                return r;
            }
            
            private void count(final int c) {
                SevenZFile.this.compressedBytesReadFromCurrentEntry += c;
            }
        };
        final LinkedList<SevenZMethodConfiguration> methods = new LinkedList<SevenZMethodConfiguration>();
        for (final Coder coder : folder.getOrderedCoders()) {
            if (coder.numInStreams != 1L || coder.numOutStreams != 1L) {
                throw new IOException("Multi input/output stream coders are not yet supported");
            }
            final SevenZMethod method = SevenZMethod.byId(coder.decompressionMethodId);
            inputStreamStack = Coders.addDecoder(this.fileName, inputStreamStack, folder.getUnpackSizeForCoder(coder), coder, this.password);
            methods.addFirst(new SevenZMethodConfiguration(method, Coders.findByMethod(method).getOptionsFromCoder(coder, inputStreamStack)));
        }
        entry.setContentMethods(methods);
        if (folder.hasCrc) {
            return new CRC32VerifyingInputStream(inputStreamStack, folder.getUnpackSize(), folder.crc);
        }
        return inputStreamStack;
    }
    
    public int read() throws IOException {
        final int b = this.getCurrentStream().read();
        if (b >= 0) {
            ++this.uncompressedBytesReadFromCurrentEntry;
        }
        return b;
    }
    
    private InputStream getCurrentStream() throws IOException {
        if (this.archive.files[this.currentEntryIndex].getSize() == 0L) {
            return new ByteArrayInputStream(new byte[0]);
        }
        if (this.deferredBlockStreams.isEmpty()) {
            throw new IllegalStateException("No current 7z entry (call getNextEntry() first).");
        }
        while (this.deferredBlockStreams.size() > 1) {
            try (final InputStream stream = this.deferredBlockStreams.remove(0)) {
                IOUtils.skip(stream, Long.MAX_VALUE);
            }
            this.compressedBytesReadFromCurrentEntry = 0L;
        }
        return this.deferredBlockStreams.get(0);
    }
    
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int cnt = this.getCurrentStream().read(b, off, len);
        if (cnt > 0) {
            this.uncompressedBytesReadFromCurrentEntry += cnt;
        }
        return cnt;
    }
    
    public InputStreamStatistics getStatisticsForCurrentEntry() {
        return new InputStreamStatistics() {
            @Override
            public long getCompressedCount() {
                return SevenZFile.this.compressedBytesReadFromCurrentEntry;
            }
            
            @Override
            public long getUncompressedCount() {
                return SevenZFile.this.uncompressedBytesReadFromCurrentEntry;
            }
        };
    }
    
    private static long readUint64(final ByteBuffer in) throws IOException {
        final long firstByte = getUnsignedByte(in);
        int mask = 128;
        long value = 0L;
        for (int i = 0; i < 8; ++i) {
            if ((firstByte & (long)mask) == 0x0L) {
                return value | (firstByte & (long)(mask - 1)) << 8 * i;
            }
            final long nextByte = getUnsignedByte(in);
            value |= nextByte << 8 * i;
            mask >>>= 1;
        }
        return value;
    }
    
    private static int getUnsignedByte(final ByteBuffer buf) {
        return buf.get() & 0xFF;
    }
    
    public static boolean matches(final byte[] signature, final int length) {
        if (length < SevenZFile.sevenZSignature.length) {
            return false;
        }
        for (int i = 0; i < SevenZFile.sevenZSignature.length; ++i) {
            if (signature[i] != SevenZFile.sevenZSignature[i]) {
                return false;
            }
        }
        return true;
    }
    
    private static long skipBytesFully(final ByteBuffer input, long bytesToSkip) throws IOException {
        if (bytesToSkip < 1L) {
            return 0L;
        }
        final int current = input.position();
        final int maxSkip = input.remaining();
        if (maxSkip < bytesToSkip) {
            bytesToSkip = maxSkip;
        }
        input.position(current + (int)bytesToSkip);
        return bytesToSkip;
    }
    
    private void readFully(final ByteBuffer buf) throws IOException {
        buf.rewind();
        IOUtils.readFully(this.channel, buf);
        buf.flip();
    }
    
    @Override
    public String toString() {
        return this.archive.toString();
    }
    
    private static byte[] utf16Decode(final char[] chars) throws IOException {
        if (chars == null) {
            return null;
        }
        final ByteBuffer encoded = SevenZFile.PASSWORD_ENCODER.encode(CharBuffer.wrap(chars));
        if (encoded.hasArray()) {
            return encoded.array();
        }
        final byte[] e = new byte[encoded.remaining()];
        encoded.get(e);
        return e;
    }
    
    static {
        sevenZSignature = new byte[] { 55, 122, -68, -81, 39, 28 };
        PASSWORD_ENCODER = StandardCharsets.UTF_16LE.newEncoder();
    }
}
