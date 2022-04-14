/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.archivers.sevenz;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.compress.archivers.sevenz.Coder;
import org.apache.commons.compress.archivers.sevenz.CoderBase;

class AES256SHA256Decoder
extends CoderBase {
    AES256SHA256Decoder() {
        super(new Class[0]);
    }

    InputStream decode(final InputStream in2, final Coder coder, final byte[] passwordBytes) throws IOException {
        return new InputStream(){
            private boolean isInitialized = false;
            private CipherInputStream cipherInputStream = null;

            private CipherInputStream init() throws IOException {
                byte[] aesKeyBytes;
                if (this.isInitialized) {
                    return this.cipherInputStream;
                }
                int byte0 = 0xFF & coder.properties[0];
                int numCyclesPower = byte0 & 0x3F;
                int byte1 = 0xFF & coder.properties[1];
                int saltSize = (byte0 >> 7 & 1) + (byte1 >> 4);
                int ivSize = (byte0 >> 6 & 1) + (byte1 & 0xF);
                if (2 + saltSize + ivSize > coder.properties.length) {
                    throw new IOException("Salt size + IV size too long");
                }
                byte[] salt = new byte[saltSize];
                System.arraycopy(coder.properties, 2, salt, 0, saltSize);
                byte[] iv2 = new byte[16];
                System.arraycopy(coder.properties, 2 + saltSize, iv2, 0, ivSize);
                if (passwordBytes == null) {
                    throw new IOException("Cannot read encrypted files without a password");
                }
                if (numCyclesPower == 63) {
                    aesKeyBytes = new byte[32];
                    System.arraycopy(salt, 0, aesKeyBytes, 0, saltSize);
                    System.arraycopy(passwordBytes, 0, aesKeyBytes, saltSize, Math.min(passwordBytes.length, aesKeyBytes.length - saltSize));
                } else {
                    MessageDigest digest;
                    try {
                        digest = MessageDigest.getInstance("SHA-256");
                    }
                    catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                        IOException ioe = new IOException("SHA-256 is unsupported by your Java implementation");
                        ioe.initCause(noSuchAlgorithmException);
                        throw ioe;
                    }
                    byte[] extra = new byte[8];
                    block4: for (long j2 = 0L; j2 < 1L << numCyclesPower; ++j2) {
                        digest.update(salt);
                        digest.update(passwordBytes);
                        digest.update(extra);
                        for (int k2 = 0; k2 < extra.length; ++k2) {
                            int n2 = k2;
                            extra[n2] = (byte)(extra[n2] + 1);
                            if (extra[k2] != 0) continue block4;
                        }
                    }
                    aesKeyBytes = digest.digest();
                }
                SecretKeySpec aesKey = new SecretKeySpec(aesKeyBytes, "AES");
                try {
                    Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
                    cipher.init(2, (Key)aesKey, new IvParameterSpec(iv2));
                    this.cipherInputStream = new CipherInputStream(in2, cipher);
                    this.isInitialized = true;
                    return this.cipherInputStream;
                }
                catch (GeneralSecurityException generalSecurityException) {
                    IOException ioe = new IOException("Decryption error (do you have the JCE Unlimited Strength Jurisdiction Policy Files installed?)");
                    ioe.initCause(generalSecurityException);
                    throw ioe;
                }
            }

            public int read() throws IOException {
                return this.init().read();
            }

            public int read(byte[] b2, int off, int len) throws IOException {
                return this.init().read(b2, off, len);
            }

            public void close() {
            }
        };
    }
}

