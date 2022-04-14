package me.spec.eris.utils.network;

import java.io.File;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class CryptUtils {

    static Cipher ecipher;
    static Cipher dcipher;
    static byte[] salt = {
            (byte) 0xB9, (byte) 0x2B, (byte) 0xC8, (byte) 0x1C,
            (byte) 0x16, (byte) 0x55, (byte) 0xA3, (byte) 0x367
    };
    static int iterationCount = 19;

    public static String encrypt(String secretKey, String plainText) {
        try {
            KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, iterationCount);
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

            ecipher = Cipher.getInstance(key.getAlgorithm());
            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            String charSet = "UTF-8";
            byte[] in = plainText.getBytes(charSet);
            byte[] out = ecipher.doFinal(in);
            String encStr = new String(Base64.getEncoder().encode(out));
            return encStr;
        } catch (Exception e) {
        }
        return "";
    }

    public static String decrypt(String secretKey, String encryptedText) {
        try {
            KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, iterationCount);
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
            dcipher = Cipher.getInstance(key.getAlgorithm());
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
            byte[] enc = Base64.getDecoder().decode(encryptedText);
            byte[] utf8 = dcipher.doFinal(enc);
            String charSet = "UTF-8";
            String plainStr = new String(utf8, charSet);
            return plainStr;
        } catch (Exception e) {
        }
        return "";
    }

    public static String hash(File input, String algorithm) {
        if (!input.isFile()) {
            return "";
        }
        try (java.io.InputStream in = new java.io.FileInputStream(input)) {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance(algorithm);
            byte[] block = new byte[4096];
            int length;
            while ((length = in.read(block)) > 0) {
                digest.update(block, 0, length);
            }
            return javax.xml.bind.DatatypeConverter.printHexBinary(digest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String hash(String input, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
