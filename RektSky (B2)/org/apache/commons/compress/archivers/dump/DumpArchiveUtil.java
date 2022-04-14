package org.apache.commons.compress.archivers.dump;

import org.apache.commons.compress.utils.*;
import org.apache.commons.compress.archivers.zip.*;
import java.util.*;
import java.io.*;

class DumpArchiveUtil
{
    private DumpArchiveUtil() {
    }
    
    public static int calculateChecksum(final byte[] buffer) {
        int calc = 0;
        for (int i = 0; i < 256; ++i) {
            calc += convert32(buffer, 4 * i);
        }
        return 84446 - (calc - convert32(buffer, 28));
    }
    
    public static final boolean verify(final byte[] buffer) {
        final int magic = convert32(buffer, 24);
        if (magic != 60012) {
            return false;
        }
        final int checksum = convert32(buffer, 28);
        return checksum == calculateChecksum(buffer);
    }
    
    public static final int getIno(final byte[] buffer) {
        return convert32(buffer, 20);
    }
    
    public static final long convert64(final byte[] buffer, final int offset) {
        return ByteUtils.fromLittleEndian(buffer, offset, 8);
    }
    
    public static final int convert32(final byte[] buffer, final int offset) {
        return (int)ByteUtils.fromLittleEndian(buffer, offset, 4);
    }
    
    public static final int convert16(final byte[] buffer, final int offset) {
        return (int)ByteUtils.fromLittleEndian(buffer, offset, 2);
    }
    
    static String decode(final ZipEncoding encoding, final byte[] b, final int offset, final int len) throws IOException {
        return encoding.decode(Arrays.copyOfRange(b, offset, offset + len));
    }
}
