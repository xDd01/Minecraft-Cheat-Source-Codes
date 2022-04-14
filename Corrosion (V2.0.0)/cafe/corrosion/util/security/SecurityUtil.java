/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.security;

import java.security.MessageDigest;
import javax.swing.filechooser.FileSystemView;

public class SecurityUtil {
    private static final MessageDigest MESSAGE_DIGEST = SecurityUtil.getMessageDigest();

    private static MessageDigest getMessageDigest() {
        return MessageDigest.getInstance("SHA-256");
    }

    public static String hash(byte[] input) {
        return SecurityUtil.bytesToHex(MESSAGE_DIGEST.digest(input));
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            int decimal = aByte & 0xFF;
            String hex = Integer.toHexString(decimal);
            if (hex.length() % 2 == 1) {
                hex = "0" + hex;
            }
            result.append(hex);
        }
        return result.toString();
    }

    public static String getHardwareIdentifier() {
        long availableProcessors = Runtime.getRuntime().availableProcessors();
        String username = System.getProperty("user.name");
        String operatingSystem = System.getProperty("os.name");
        String pIdentifier = System.getenv("PROCESSOR_IDENTIFIER");
        String root = FileSystemView.getFileSystemView().getRoots()[0].toString();
        return SecurityUtil.hash((username + operatingSystem + pIdentifier + availableProcessors + root).getBytes());
    }
}

