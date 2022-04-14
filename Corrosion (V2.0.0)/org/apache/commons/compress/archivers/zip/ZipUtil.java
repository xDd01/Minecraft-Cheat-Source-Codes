/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.archivers.zip;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.CRC32;
import org.apache.commons.compress.archivers.zip.AbstractUnicodeExtraField;
import org.apache.commons.compress.archivers.zip.UnicodeCommentExtraField;
import org.apache.commons.compress.archivers.zip.UnicodePathExtraField;
import org.apache.commons.compress.archivers.zip.UnsupportedZipFeatureException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import org.apache.commons.compress.archivers.zip.ZipLong;
import org.apache.commons.compress.archivers.zip.ZipMethod;

public abstract class ZipUtil {
    private static final byte[] DOS_TIME_MIN = ZipLong.getBytes(8448L);

    public static ZipLong toDosTime(Date time) {
        return new ZipLong(ZipUtil.toDosTime(time.getTime()));
    }

    public static byte[] toDosTime(long t2) {
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(t2);
        int year = c2.get(1);
        if (year < 1980) {
            return ZipUtil.copy(DOS_TIME_MIN);
        }
        int month = c2.get(2) + 1;
        long value = year - 1980 << 25 | month << 21 | c2.get(5) << 16 | c2.get(11) << 11 | c2.get(12) << 5 | c2.get(13) >> 1;
        return ZipLong.getBytes(value);
    }

    public static long adjustToLong(int i2) {
        if (i2 < 0) {
            return 0x100000000L + (long)i2;
        }
        return i2;
    }

    public static byte[] reverse(byte[] array) {
        int z2 = array.length - 1;
        for (int i2 = 0; i2 < array.length / 2; ++i2) {
            byte x2 = array[i2];
            array[i2] = array[z2 - i2];
            array[z2 - i2] = x2;
        }
        return array;
    }

    static long bigToLong(BigInteger big2) {
        if (big2.bitLength() <= 63) {
            return big2.longValue();
        }
        throw new NumberFormatException("The BigInteger cannot fit inside a 64 bit java long: [" + big2 + "]");
    }

    static BigInteger longToBig(long l2) {
        if (l2 < Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Negative longs < -2^31 not permitted: [" + l2 + "]");
        }
        if (l2 < 0L && l2 >= Integer.MIN_VALUE) {
            l2 = ZipUtil.adjustToLong((int)l2);
        }
        return BigInteger.valueOf(l2);
    }

    public static int signedByteToUnsignedInt(byte b2) {
        if (b2 >= 0) {
            return b2;
        }
        return 256 + b2;
    }

    public static byte unsignedIntToSignedByte(int i2) {
        if (i2 > 255 || i2 < 0) {
            throw new IllegalArgumentException("Can only convert non-negative integers between [0,255] to byte: [" + i2 + "]");
        }
        if (i2 < 128) {
            return (byte)i2;
        }
        return (byte)(i2 - 256);
    }

    public static Date fromDosTime(ZipLong zipDosTime) {
        long dosTime = zipDosTime.getValue();
        return new Date(ZipUtil.dosToJavaTime(dosTime));
    }

    public static long dosToJavaTime(long dosTime) {
        Calendar cal = Calendar.getInstance();
        cal.set(1, (int)(dosTime >> 25 & 0x7FL) + 1980);
        cal.set(2, (int)(dosTime >> 21 & 0xFL) - 1);
        cal.set(5, (int)(dosTime >> 16) & 0x1F);
        cal.set(11, (int)(dosTime >> 11) & 0x1F);
        cal.set(12, (int)(dosTime >> 5) & 0x3F);
        cal.set(13, (int)(dosTime << 1) & 0x3E);
        cal.set(14, 0);
        return cal.getTime().getTime();
    }

    static void setNameAndCommentFromExtraFields(ZipArchiveEntry ze2, byte[] originalNameBytes, byte[] commentBytes) {
        UnicodeCommentExtraField cmt;
        String newComment;
        UnicodePathExtraField name = (UnicodePathExtraField)ze2.getExtraField(UnicodePathExtraField.UPATH_ID);
        String originalName = ze2.getName();
        String newName = ZipUtil.getUnicodeStringIfOriginalMatches(name, originalNameBytes);
        if (newName != null && !originalName.equals(newName)) {
            ze2.setName(newName);
        }
        if (commentBytes != null && commentBytes.length > 0 && (newComment = ZipUtil.getUnicodeStringIfOriginalMatches(cmt = (UnicodeCommentExtraField)ze2.getExtraField(UnicodeCommentExtraField.UCOM_ID), commentBytes)) != null) {
            ze2.setComment(newComment);
        }
    }

    private static String getUnicodeStringIfOriginalMatches(AbstractUnicodeExtraField f2, byte[] orig) {
        if (f2 != null) {
            CRC32 crc32 = new CRC32();
            crc32.update(orig);
            long origCRC32 = crc32.getValue();
            if (origCRC32 == f2.getNameCRC32()) {
                try {
                    return ZipEncodingHelper.UTF8_ZIP_ENCODING.decode(f2.getUnicodeName());
                }
                catch (IOException ex2) {
                    return null;
                }
            }
        }
        return null;
    }

    static byte[] copy(byte[] from) {
        if (from != null) {
            byte[] to2 = new byte[from.length];
            System.arraycopy(from, 0, to2, 0, to2.length);
            return to2;
        }
        return null;
    }

    static boolean canHandleEntryData(ZipArchiveEntry entry) {
        return ZipUtil.supportsEncryptionOf(entry) && ZipUtil.supportsMethodOf(entry);
    }

    private static boolean supportsEncryptionOf(ZipArchiveEntry entry) {
        return !entry.getGeneralPurposeBit().usesEncryption();
    }

    private static boolean supportsMethodOf(ZipArchiveEntry entry) {
        return entry.getMethod() == 0 || entry.getMethod() == ZipMethod.UNSHRINKING.getCode() || entry.getMethod() == ZipMethod.IMPLODING.getCode() || entry.getMethod() == 8;
    }

    static void checkRequestedFeatures(ZipArchiveEntry ze2) throws UnsupportedZipFeatureException {
        if (!ZipUtil.supportsEncryptionOf(ze2)) {
            throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.ENCRYPTION, ze2);
        }
        if (!ZipUtil.supportsMethodOf(ze2)) {
            ZipMethod m2 = ZipMethod.getMethodByCode(ze2.getMethod());
            if (m2 == null) {
                throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.METHOD, ze2);
            }
            throw new UnsupportedZipFeatureException(m2, ze2);
        }
    }
}

