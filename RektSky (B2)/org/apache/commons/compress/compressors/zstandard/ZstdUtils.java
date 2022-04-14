package org.apache.commons.compress.compressors.zstandard;

public class ZstdUtils
{
    private static final byte[] ZSTANDARD_FRAME_MAGIC;
    private static final byte[] SKIPPABLE_FRAME_MAGIC;
    private static volatile CachedAvailability cachedZstdAvailability;
    
    private ZstdUtils() {
    }
    
    public static boolean isZstdCompressionAvailable() {
        final CachedAvailability cachedResult = ZstdUtils.cachedZstdAvailability;
        if (cachedResult != CachedAvailability.DONT_CACHE) {
            return cachedResult == CachedAvailability.CACHED_AVAILABLE;
        }
        return internalIsZstdCompressionAvailable();
    }
    
    private static boolean internalIsZstdCompressionAvailable() {
        try {
            Class.forName("com.github.luben.zstd.ZstdInputStream");
            return true;
        }
        catch (NoClassDefFoundError | Exception noClassDefFoundError) {
            final Throwable t;
            final Throwable error = t;
            return false;
        }
    }
    
    public static void setCacheZstdAvailablity(final boolean doCache) {
        if (!doCache) {
            ZstdUtils.cachedZstdAvailability = CachedAvailability.DONT_CACHE;
        }
        else if (ZstdUtils.cachedZstdAvailability == CachedAvailability.DONT_CACHE) {
            final boolean hasZstd = internalIsZstdCompressionAvailable();
            ZstdUtils.cachedZstdAvailability = (hasZstd ? CachedAvailability.CACHED_AVAILABLE : CachedAvailability.CACHED_UNAVAILABLE);
        }
    }
    
    public static boolean matches(final byte[] signature, final int length) {
        if (length < ZstdUtils.ZSTANDARD_FRAME_MAGIC.length) {
            return false;
        }
        boolean isZstandard = true;
        for (int i = 0; i < ZstdUtils.ZSTANDARD_FRAME_MAGIC.length; ++i) {
            if (signature[i] != ZstdUtils.ZSTANDARD_FRAME_MAGIC[i]) {
                isZstandard = false;
                break;
            }
        }
        if (isZstandard) {
            return true;
        }
        if (0x50 == (signature[0] & 0xF0)) {
            for (int i = 0; i < ZstdUtils.SKIPPABLE_FRAME_MAGIC.length; ++i) {
                if (signature[i + 1] != ZstdUtils.SKIPPABLE_FRAME_MAGIC[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    static CachedAvailability getCachedZstdAvailability() {
        return ZstdUtils.cachedZstdAvailability;
    }
    
    static {
        ZSTANDARD_FRAME_MAGIC = new byte[] { 40, -75, 47, -3 };
        SKIPPABLE_FRAME_MAGIC = new byte[] { 42, 77, 24 };
        ZstdUtils.cachedZstdAvailability = CachedAvailability.DONT_CACHE;
        try {
            Class.forName("org.osgi.framework.BundleEvent");
        }
        catch (Exception ex) {
            setCacheZstdAvailablity(true);
        }
    }
    
    enum CachedAvailability
    {
        DONT_CACHE, 
        CACHED_AVAILABLE, 
        CACHED_UNAVAILABLE;
    }
}
