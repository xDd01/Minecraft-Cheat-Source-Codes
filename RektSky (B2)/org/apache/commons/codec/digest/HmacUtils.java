package org.apache.commons.codec.digest;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import org.apache.commons.codec.binary.*;
import java.nio.*;
import java.io.*;

public final class HmacUtils
{
    private static final int STREAM_BUFFER_LENGTH = 1024;
    private final Mac mac;
    
    public static boolean isAvailable(final String name) {
        try {
            Mac.getInstance(name);
            return true;
        }
        catch (NoSuchAlgorithmException e) {
            return false;
        }
    }
    
    public static boolean isAvailable(final HmacAlgorithms name) {
        try {
            Mac.getInstance(name.getName());
            return true;
        }
        catch (NoSuchAlgorithmException e) {
            return false;
        }
    }
    
    @Deprecated
    public static Mac getHmacMd5(final byte[] key) {
        return getInitializedMac(HmacAlgorithms.HMAC_MD5, key);
    }
    
    @Deprecated
    public static Mac getHmacSha1(final byte[] key) {
        return getInitializedMac(HmacAlgorithms.HMAC_SHA_1, key);
    }
    
    @Deprecated
    public static Mac getHmacSha256(final byte[] key) {
        return getInitializedMac(HmacAlgorithms.HMAC_SHA_256, key);
    }
    
    @Deprecated
    public static Mac getHmacSha384(final byte[] key) {
        return getInitializedMac(HmacAlgorithms.HMAC_SHA_384, key);
    }
    
    @Deprecated
    public static Mac getHmacSha512(final byte[] key) {
        return getInitializedMac(HmacAlgorithms.HMAC_SHA_512, key);
    }
    
    public static Mac getInitializedMac(final HmacAlgorithms algorithm, final byte[] key) {
        return getInitializedMac(algorithm.getName(), key);
    }
    
    public static Mac getInitializedMac(final String algorithm, final byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException("Null key");
        }
        try {
            final SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);
            final Mac mac = Mac.getInstance(algorithm);
            mac.init(keySpec);
            return mac;
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        catch (InvalidKeyException e2) {
            throw new IllegalArgumentException(e2);
        }
    }
    
    @Deprecated
    public static byte[] hmacMd5(final byte[] key, final byte[] valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_MD5, key).hmac(valueToDigest);
    }
    
    @Deprecated
    public static byte[] hmacMd5(final byte[] key, final InputStream valueToDigest) throws IOException {
        return new HmacUtils(HmacAlgorithms.HMAC_MD5, key).hmac(valueToDigest);
    }
    
    @Deprecated
    public static byte[] hmacMd5(final String key, final String valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_MD5, key).hmac(valueToDigest);
    }
    
    @Deprecated
    public static String hmacMd5Hex(final byte[] key, final byte[] valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_MD5, key).hmacHex(valueToDigest);
    }
    
    @Deprecated
    public static String hmacMd5Hex(final byte[] key, final InputStream valueToDigest) throws IOException {
        return new HmacUtils(HmacAlgorithms.HMAC_MD5, key).hmacHex(valueToDigest);
    }
    
    @Deprecated
    public static String hmacMd5Hex(final String key, final String valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_MD5, key).hmacHex(valueToDigest);
    }
    
    @Deprecated
    public static byte[] hmacSha1(final byte[] key, final byte[] valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key).hmac(valueToDigest);
    }
    
    @Deprecated
    public static byte[] hmacSha1(final byte[] key, final InputStream valueToDigest) throws IOException {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key).hmac(valueToDigest);
    }
    
    @Deprecated
    public static byte[] hmacSha1(final String key, final String valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key).hmac(valueToDigest);
    }
    
    @Deprecated
    public static String hmacSha1Hex(final byte[] key, final byte[] valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key).hmacHex(valueToDigest);
    }
    
    @Deprecated
    public static String hmacSha1Hex(final byte[] key, final InputStream valueToDigest) throws IOException {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key).hmacHex(valueToDigest);
    }
    
    @Deprecated
    public static String hmacSha1Hex(final String key, final String valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key).hmacHex(valueToDigest);
    }
    
    @Deprecated
    public static byte[] hmacSha256(final byte[] key, final byte[] valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key).hmac(valueToDigest);
    }
    
    @Deprecated
    public static byte[] hmacSha256(final byte[] key, final InputStream valueToDigest) throws IOException {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key).hmac(valueToDigest);
    }
    
    @Deprecated
    public static byte[] hmacSha256(final String key, final String valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key).hmac(valueToDigest);
    }
    
    @Deprecated
    public static String hmacSha256Hex(final byte[] key, final byte[] valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key).hmacHex(valueToDigest);
    }
    
    @Deprecated
    public static String hmacSha256Hex(final byte[] key, final InputStream valueToDigest) throws IOException {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key).hmacHex(valueToDigest);
    }
    
    @Deprecated
    public static String hmacSha256Hex(final String key, final String valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key).hmacHex(valueToDigest);
    }
    
    @Deprecated
    public static byte[] hmacSha384(final byte[] key, final byte[] valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key).hmac(valueToDigest);
    }
    
    @Deprecated
    public static byte[] hmacSha384(final byte[] key, final InputStream valueToDigest) throws IOException {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key).hmac(valueToDigest);
    }
    
    @Deprecated
    public static byte[] hmacSha384(final String key, final String valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key).hmac(valueToDigest);
    }
    
    @Deprecated
    public static String hmacSha384Hex(final byte[] key, final byte[] valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key).hmacHex(valueToDigest);
    }
    
    @Deprecated
    public static String hmacSha384Hex(final byte[] key, final InputStream valueToDigest) throws IOException {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key).hmacHex(valueToDigest);
    }
    
    @Deprecated
    public static String hmacSha384Hex(final String key, final String valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key).hmacHex(valueToDigest);
    }
    
    @Deprecated
    public static byte[] hmacSha512(final byte[] key, final byte[] valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key).hmac(valueToDigest);
    }
    
    @Deprecated
    public static byte[] hmacSha512(final byte[] key, final InputStream valueToDigest) throws IOException {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key).hmac(valueToDigest);
    }
    
    @Deprecated
    public static byte[] hmacSha512(final String key, final String valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key).hmac(valueToDigest);
    }
    
    @Deprecated
    public static String hmacSha512Hex(final byte[] key, final byte[] valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key).hmacHex(valueToDigest);
    }
    
    @Deprecated
    public static String hmacSha512Hex(final byte[] key, final InputStream valueToDigest) throws IOException {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key).hmacHex(valueToDigest);
    }
    
    @Deprecated
    public static String hmacSha512Hex(final String key, final String valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key).hmacHex(valueToDigest);
    }
    
    public static Mac updateHmac(final Mac mac, final byte[] valueToDigest) {
        mac.reset();
        mac.update(valueToDigest);
        return mac;
    }
    
    public static Mac updateHmac(final Mac mac, final InputStream valueToDigest) throws IOException {
        mac.reset();
        final byte[] buffer = new byte[1024];
        for (int read = valueToDigest.read(buffer, 0, 1024); read > -1; read = valueToDigest.read(buffer, 0, 1024)) {
            mac.update(buffer, 0, read);
        }
        return mac;
    }
    
    public static Mac updateHmac(final Mac mac, final String valueToDigest) {
        mac.reset();
        mac.update(StringUtils.getBytesUtf8(valueToDigest));
        return mac;
    }
    
    @Deprecated
    public HmacUtils() {
        this(null);
    }
    
    private HmacUtils(final Mac mac) {
        this.mac = mac;
    }
    
    public HmacUtils(final String algorithm, final byte[] key) {
        this(getInitializedMac(algorithm, key));
    }
    
    public HmacUtils(final String algorithm, final String key) {
        this(algorithm, StringUtils.getBytesUtf8(key));
    }
    
    public HmacUtils(final HmacAlgorithms algorithm, final String key) {
        this(algorithm.getName(), StringUtils.getBytesUtf8(key));
    }
    
    public HmacUtils(final HmacAlgorithms algorithm, final byte[] key) {
        this(algorithm.getName(), key);
    }
    
    public byte[] hmac(final byte[] valueToDigest) {
        return this.mac.doFinal(valueToDigest);
    }
    
    public String hmacHex(final byte[] valueToDigest) {
        return Hex.encodeHexString(this.hmac(valueToDigest));
    }
    
    public byte[] hmac(final String valueToDigest) {
        return this.mac.doFinal(StringUtils.getBytesUtf8(valueToDigest));
    }
    
    public String hmacHex(final String valueToDigest) {
        return Hex.encodeHexString(this.hmac(valueToDigest));
    }
    
    public byte[] hmac(final ByteBuffer valueToDigest) {
        this.mac.update(valueToDigest);
        return this.mac.doFinal();
    }
    
    public String hmacHex(final ByteBuffer valueToDigest) {
        return Hex.encodeHexString(this.hmac(valueToDigest));
    }
    
    public byte[] hmac(final InputStream valueToDigest) throws IOException {
        final byte[] buffer = new byte[1024];
        int read;
        while ((read = valueToDigest.read(buffer, 0, 1024)) > -1) {
            this.mac.update(buffer, 0, read);
        }
        return this.mac.doFinal();
    }
    
    public String hmacHex(final InputStream valueToDigest) throws IOException {
        return Hex.encodeHexString(this.hmac(valueToDigest));
    }
    
    public byte[] hmac(final File valueToDigest) throws IOException {
        final BufferedInputStream stream = new BufferedInputStream(new FileInputStream(valueToDigest));
        try {
            return this.hmac(stream);
        }
        finally {
            stream.close();
        }
    }
    
    public String hmacHex(final File valueToDigest) throws IOException {
        return Hex.encodeHexString(this.hmac(valueToDigest));
    }
}
