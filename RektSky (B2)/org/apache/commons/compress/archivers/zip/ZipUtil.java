package org.apache.commons.compress.archivers.zip;

import java.util.*;
import java.math.*;
import java.util.zip.*;
import java.io.*;

public abstract class ZipUtil
{
    private static final byte[] DOS_TIME_MIN;
    
    public static ZipLong toDosTime(final Date time) {
        return new ZipLong(toDosTime(time.getTime()));
    }
    
    public static byte[] toDosTime(final long t) {
        final byte[] result = new byte[4];
        toDosTime(t, result, 0);
        return result;
    }
    
    public static void toDosTime(final long t, final byte[] buf, final int offset) {
        toDosTime(Calendar.getInstance(), t, buf, offset);
    }
    
    static void toDosTime(final Calendar c, final long t, final byte[] buf, final int offset) {
        c.setTimeInMillis(t);
        final int year = c.get(1);
        if (year < 1980) {
            System.arraycopy(ZipUtil.DOS_TIME_MIN, 0, buf, offset, ZipUtil.DOS_TIME_MIN.length);
            return;
        }
        final int month = c.get(2) + 1;
        final long value = year - 1980 << 25 | month << 21 | c.get(5) << 16 | c.get(11) << 11 | c.get(12) << 5 | c.get(13) >> 1;
        ZipLong.putLong(value, buf, offset);
    }
    
    public static long adjustToLong(final int i) {
        if (i < 0) {
            return 4294967296L + i;
        }
        return i;
    }
    
    public static byte[] reverse(final byte[] array) {
        final int z = array.length - 1;
        for (int i = 0; i < array.length / 2; ++i) {
            final byte x = array[i];
            array[i] = array[z - i];
            array[z - i] = x;
        }
        return array;
    }
    
    static long bigToLong(final BigInteger big) {
        if (big.bitLength() <= 63) {
            return big.longValue();
        }
        throw new NumberFormatException("The BigInteger cannot fit inside a 64 bit java long: [" + big + "]");
    }
    
    static BigInteger longToBig(long l) {
        if (l < -2147483648L) {
            throw new IllegalArgumentException("Negative longs < -2^31 not permitted: [" + l + "]");
        }
        if (l < 0L && l >= -2147483648L) {
            l = adjustToLong((int)l);
        }
        return BigInteger.valueOf(l);
    }
    
    public static int signedByteToUnsignedInt(final byte b) {
        if (b >= 0) {
            return b;
        }
        return 256 + b;
    }
    
    public static byte unsignedIntToSignedByte(final int i) {
        if (i > 255 || i < 0) {
            throw new IllegalArgumentException("Can only convert non-negative integers between [0,255] to byte: [" + i + "]");
        }
        if (i < 128) {
            return (byte)i;
        }
        return (byte)(i - 256);
    }
    
    public static Date fromDosTime(final ZipLong zipDosTime) {
        final long dosTime = zipDosTime.getValue();
        return new Date(dosToJavaTime(dosTime));
    }
    
    public static long dosToJavaTime(final long dosTime) {
        final Calendar cal = Calendar.getInstance();
        cal.set(1, (int)(dosTime >> 25 & 0x7FL) + 1980);
        cal.set(2, (int)(dosTime >> 21 & 0xFL) - 1);
        cal.set(5, (int)(dosTime >> 16) & 0x1F);
        cal.set(11, (int)(dosTime >> 11) & 0x1F);
        cal.set(12, (int)(dosTime >> 5) & 0x3F);
        cal.set(13, (int)(dosTime << 1) & 0x3E);
        cal.set(14, 0);
        return cal.getTime().getTime();
    }
    
    static void setNameAndCommentFromExtraFields(final ZipArchiveEntry ze, final byte[] originalNameBytes, final byte[] commentBytes) {
        final UnicodePathExtraField name = (UnicodePathExtraField)ze.getExtraField(UnicodePathExtraField.UPATH_ID);
        final String newName = getUnicodeStringIfOriginalMatches(name, originalNameBytes);
        if (newName != null) {
            ze.setName(newName);
            ze.setNameSource(ZipArchiveEntry.NameSource.UNICODE_EXTRA_FIELD);
        }
        if (commentBytes != null && commentBytes.length > 0) {
            final UnicodeCommentExtraField cmt = (UnicodeCommentExtraField)ze.getExtraField(UnicodeCommentExtraField.UCOM_ID);
            final String newComment = getUnicodeStringIfOriginalMatches(cmt, commentBytes);
            if (newComment != null) {
                ze.setComment(newComment);
                ze.setCommentSource(ZipArchiveEntry.CommentSource.UNICODE_EXTRA_FIELD);
            }
        }
    }
    
    private static String getUnicodeStringIfOriginalMatches(final AbstractUnicodeExtraField f, final byte[] orig) {
        if (f != null) {
            final CRC32 crc32 = new CRC32();
            crc32.update(orig);
            final long origCRC32 = crc32.getValue();
            if (origCRC32 == f.getNameCRC32()) {
                try {
                    return ZipEncodingHelper.UTF8_ZIP_ENCODING.decode(f.getUnicodeName());
                }
                catch (IOException ex) {
                    return null;
                }
            }
        }
        return null;
    }
    
    static byte[] copy(final byte[] from) {
        if (from != null) {
            final byte[] to = new byte[from.length];
            System.arraycopy(from, 0, to, 0, to.length);
            return to;
        }
        return null;
    }
    
    static void copy(final byte[] from, final byte[] to, final int offset) {
        if (from != null) {
            System.arraycopy(from, 0, to, offset, from.length);
        }
    }
    
    static boolean canHandleEntryData(final ZipArchiveEntry entry) {
        return supportsEncryptionOf(entry) && supportsMethodOf(entry);
    }
    
    private static boolean supportsEncryptionOf(final ZipArchiveEntry entry) {
        return !entry.getGeneralPurposeBit().usesEncryption();
    }
    
    private static boolean supportsMethodOf(final ZipArchiveEntry entry) {
        return entry.getMethod() == 0 || entry.getMethod() == ZipMethod.UNSHRINKING.getCode() || entry.getMethod() == ZipMethod.IMPLODING.getCode() || entry.getMethod() == 8 || entry.getMethod() == ZipMethod.ENHANCED_DEFLATED.getCode() || entry.getMethod() == ZipMethod.BZIP2.getCode();
    }
    
    static void checkRequestedFeatures(final ZipArchiveEntry ze) throws UnsupportedZipFeatureException {
        if (!supportsEncryptionOf(ze)) {
            throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.ENCRYPTION, ze);
        }
        if (supportsMethodOf(ze)) {
            return;
        }
        final ZipMethod m = ZipMethod.getMethodByCode(ze.getMethod());
        if (m == null) {
            throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.METHOD, ze);
        }
        throw new UnsupportedZipFeatureException(m, ze);
    }
    
    static {
        DOS_TIME_MIN = ZipLong.getBytes(8448L);
    }
}
