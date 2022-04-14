package org.apache.commons.compress.archivers.ar;

import org.apache.commons.compress.utils.*;
import org.apache.commons.compress.archivers.*;
import java.io.*;

public class ArArchiveInputStream extends ArchiveInputStream
{
    private final InputStream input;
    private long offset;
    private boolean closed;
    private ArArchiveEntry currentEntry;
    private byte[] namebuffer;
    private long entryOffset;
    private static final int NAME_OFFSET = 0;
    private static final int NAME_LEN = 16;
    private static final int LAST_MODIFIED_OFFSET = 16;
    private static final int LAST_MODIFIED_LEN = 12;
    private static final int USER_ID_OFFSET = 28;
    private static final int USER_ID_LEN = 6;
    private static final int GROUP_ID_OFFSET = 34;
    private static final int GROUP_ID_LEN = 6;
    private static final int FILE_MODE_OFFSET = 40;
    private static final int FILE_MODE_LEN = 8;
    private static final int LENGTH_OFFSET = 48;
    private static final int LENGTH_LEN = 10;
    private final byte[] metaData;
    static final String BSD_LONGNAME_PREFIX = "#1/";
    private static final int BSD_LONGNAME_PREFIX_LEN;
    private static final String BSD_LONGNAME_PATTERN = "^#1/\\d+";
    private static final String GNU_STRING_TABLE_NAME = "//";
    private static final String GNU_LONGNAME_PATTERN = "^/\\d+";
    
    public ArArchiveInputStream(final InputStream pInput) {
        this.offset = 0L;
        this.currentEntry = null;
        this.namebuffer = null;
        this.entryOffset = -1L;
        this.metaData = new byte[58];
        this.input = pInput;
        this.closed = false;
    }
    
    public ArArchiveEntry getNextArEntry() throws IOException {
        if (this.currentEntry != null) {
            final long entryEnd = this.entryOffset + this.currentEntry.getLength();
            final long skipped = IOUtils.skip(this.input, entryEnd - this.offset);
            this.trackReadBytes(skipped);
            this.currentEntry = null;
        }
        if (this.offset == 0L) {
            final byte[] expected = ArchiveUtils.toAsciiBytes("!<arch>\n");
            final byte[] realized = new byte[expected.length];
            final int read = IOUtils.readFully(this.input, realized);
            this.trackReadBytes(read);
            if (read != expected.length) {
                throw new IOException("failed to read header. Occured at byte: " + this.getBytesRead());
            }
            for (int i = 0; i < expected.length; ++i) {
                if (expected[i] != realized[i]) {
                    throw new IOException("invalid header " + ArchiveUtils.toAsciiString(realized));
                }
            }
        }
        if (this.offset % 2L != 0L) {
            if (this.input.read() < 0) {
                return null;
            }
            this.trackReadBytes(1L);
        }
        if (this.input.available() == 0) {
            return null;
        }
        final int read2 = IOUtils.readFully(this.input, this.metaData);
        this.trackReadBytes(read2);
        if (read2 < this.metaData.length) {
            throw new IOException("truncated ar archive");
        }
        final byte[] expected = ArchiveUtils.toAsciiBytes("`\n");
        final byte[] realized = new byte[expected.length];
        final int read = IOUtils.readFully(this.input, realized);
        this.trackReadBytes(read);
        if (read != expected.length) {
            throw new IOException("failed to read entry trailer. Occured at byte: " + this.getBytesRead());
        }
        for (int i = 0; i < expected.length; ++i) {
            if (expected[i] != realized[i]) {
                throw new IOException("invalid entry trailer. not read the content? Occured at byte: " + this.getBytesRead());
            }
        }
        this.entryOffset = this.offset;
        String temp = ArchiveUtils.toAsciiString(this.metaData, 0, 16).trim();
        if (isGNUStringTable(temp)) {
            this.currentEntry = this.readGNUStringTable(this.metaData, 48, 10);
            return this.getNextArEntry();
        }
        long len = this.asLong(this.metaData, 48, 10);
        if (temp.endsWith("/")) {
            temp = temp.substring(0, temp.length() - 1);
        }
        else if (this.isGNULongName(temp)) {
            final int off = Integer.parseInt(temp.substring(1));
            temp = this.getExtendedName(off);
        }
        else if (isBSDLongName(temp)) {
            temp = this.getBSDLongName(temp);
            final int nameLen = temp.length();
            len -= nameLen;
            this.entryOffset += nameLen;
        }
        return this.currentEntry = new ArArchiveEntry(temp, len, this.asInt(this.metaData, 28, 6, true), this.asInt(this.metaData, 34, 6, true), this.asInt(this.metaData, 40, 8, 8), this.asLong(this.metaData, 16, 12));
    }
    
    private String getExtendedName(final int offset) throws IOException {
        if (this.namebuffer == null) {
            throw new IOException("Cannot process GNU long filename as no // record was found");
        }
        for (int i = offset; i < this.namebuffer.length; ++i) {
            if (this.namebuffer[i] == 10 || this.namebuffer[i] == 0) {
                if (this.namebuffer[i - 1] == 47) {
                    --i;
                }
                return ArchiveUtils.toAsciiString(this.namebuffer, offset, i - offset);
            }
        }
        throw new IOException("Failed to read entry: " + offset);
    }
    
    private long asLong(final byte[] byteArray, final int offset, final int len) {
        return Long.parseLong(ArchiveUtils.toAsciiString(byteArray, offset, len).trim());
    }
    
    private int asInt(final byte[] byteArray, final int offset, final int len) {
        return this.asInt(byteArray, offset, len, 10, false);
    }
    
    private int asInt(final byte[] byteArray, final int offset, final int len, final boolean treatBlankAsZero) {
        return this.asInt(byteArray, offset, len, 10, treatBlankAsZero);
    }
    
    private int asInt(final byte[] byteArray, final int offset, final int len, final int base) {
        return this.asInt(byteArray, offset, len, base, false);
    }
    
    private int asInt(final byte[] byteArray, final int offset, final int len, final int base, final boolean treatBlankAsZero) {
        final String string = ArchiveUtils.toAsciiString(byteArray, offset, len).trim();
        if (string.length() == 0 && treatBlankAsZero) {
            return 0;
        }
        return Integer.parseInt(string, base);
    }
    
    @Override
    public ArchiveEntry getNextEntry() throws IOException {
        return this.getNextArEntry();
    }
    
    @Override
    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            this.input.close();
        }
        this.currentEntry = null;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (this.currentEntry == null) {
            throw new IllegalStateException("No current ar entry");
        }
        int toRead = len;
        final long entryEnd = this.entryOffset + this.currentEntry.getLength();
        if (len > 0 && entryEnd > this.offset) {
            toRead = (int)Math.min(len, entryEnd - this.offset);
            final int ret = this.input.read(b, off, toRead);
            this.trackReadBytes(ret);
            return ret;
        }
        return -1;
    }
    
    public static boolean matches(final byte[] signature, final int length) {
        return length >= 8 && signature[0] == 33 && signature[1] == 60 && signature[2] == 97 && signature[3] == 114 && signature[4] == 99 && signature[5] == 104 && signature[6] == 62 && signature[7] == 10;
    }
    
    private static boolean isBSDLongName(final String name) {
        return name != null && name.matches("^#1/\\d+");
    }
    
    private String getBSDLongName(final String bsdLongName) throws IOException {
        final int nameLen = Integer.parseInt(bsdLongName.substring(ArArchiveInputStream.BSD_LONGNAME_PREFIX_LEN));
        final byte[] name = new byte[nameLen];
        final int read = IOUtils.readFully(this.input, name);
        this.trackReadBytes(read);
        if (read != nameLen) {
            throw new EOFException();
        }
        return ArchiveUtils.toAsciiString(name);
    }
    
    private static boolean isGNUStringTable(final String name) {
        return "//".equals(name);
    }
    
    private void trackReadBytes(final long read) {
        this.count(read);
        if (read > 0L) {
            this.offset += read;
        }
    }
    
    private ArArchiveEntry readGNUStringTable(final byte[] length, final int offset, final int len) throws IOException {
        final int bufflen = this.asInt(length, offset, len);
        this.namebuffer = new byte[bufflen];
        final int read = IOUtils.readFully(this.input, this.namebuffer, 0, bufflen);
        this.trackReadBytes(read);
        if (read != bufflen) {
            throw new IOException("Failed to read complete // record: expected=" + bufflen + " read=" + read);
        }
        return new ArArchiveEntry("//", bufflen);
    }
    
    private boolean isGNULongName(final String name) {
        return name != null && name.matches("^/\\d+");
    }
    
    static {
        BSD_LONGNAME_PREFIX_LEN = "#1/".length();
    }
}
