package org.apache.commons.compress.compressors.xz;

import org.apache.commons.compress.compressors.*;
import java.util.*;

public class XZUtils
{
    private static final FileNameUtil fileNameUtil;
    private static final byte[] HEADER_MAGIC;
    private static volatile CachedAvailability cachedXZAvailability;
    
    private XZUtils() {
    }
    
    public static boolean matches(final byte[] signature, final int length) {
        if (length < XZUtils.HEADER_MAGIC.length) {
            return false;
        }
        for (int i = 0; i < XZUtils.HEADER_MAGIC.length; ++i) {
            if (signature[i] != XZUtils.HEADER_MAGIC[i]) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isXZCompressionAvailable() {
        final CachedAvailability cachedResult = XZUtils.cachedXZAvailability;
        if (cachedResult != CachedAvailability.DONT_CACHE) {
            return cachedResult == CachedAvailability.CACHED_AVAILABLE;
        }
        return internalIsXZCompressionAvailable();
    }
    
    private static boolean internalIsXZCompressionAvailable() {
        try {
            XZCompressorInputStream.matches(null, 0);
            return true;
        }
        catch (NoClassDefFoundError error) {
            return false;
        }
    }
    
    public static boolean isCompressedFilename(final String filename) {
        return XZUtils.fileNameUtil.isCompressedFilename(filename);
    }
    
    public static String getUncompressedFilename(final String filename) {
        return XZUtils.fileNameUtil.getUncompressedFilename(filename);
    }
    
    public static String getCompressedFilename(final String filename) {
        return XZUtils.fileNameUtil.getCompressedFilename(filename);
    }
    
    public static void setCacheXZAvailablity(final boolean doCache) {
        if (!doCache) {
            XZUtils.cachedXZAvailability = CachedAvailability.DONT_CACHE;
        }
        else if (XZUtils.cachedXZAvailability == CachedAvailability.DONT_CACHE) {
            final boolean hasXz = internalIsXZCompressionAvailable();
            XZUtils.cachedXZAvailability = (hasXz ? CachedAvailability.CACHED_AVAILABLE : CachedAvailability.CACHED_UNAVAILABLE);
        }
    }
    
    static CachedAvailability getCachedXZAvailability() {
        return XZUtils.cachedXZAvailability;
    }
    
    static {
        HEADER_MAGIC = new byte[] { -3, 55, 122, 88, 90, 0 };
        final Map<String, String> uncompressSuffix = new HashMap<String, String>();
        uncompressSuffix.put(".txz", ".tar");
        uncompressSuffix.put(".xz", "");
        uncompressSuffix.put("-xz", "");
        fileNameUtil = new FileNameUtil(uncompressSuffix, ".xz");
        XZUtils.cachedXZAvailability = CachedAvailability.DONT_CACHE;
        try {
            Class.forName("org.osgi.framework.BundleEvent");
        }
        catch (Exception ex) {
            setCacheXZAvailablity(true);
        }
    }
    
    enum CachedAvailability
    {
        DONT_CACHE, 
        CACHED_AVAILABLE, 
        CACHED_UNAVAILABLE;
    }
}
