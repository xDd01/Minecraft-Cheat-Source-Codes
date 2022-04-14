package org.apache.commons.compress.compressors.bzip2;

import org.apache.commons.compress.compressors.*;
import java.util.*;

public abstract class BZip2Utils
{
    private static final FileNameUtil fileNameUtil;
    
    private BZip2Utils() {
    }
    
    public static boolean isCompressedFilename(final String filename) {
        return BZip2Utils.fileNameUtil.isCompressedFilename(filename);
    }
    
    public static String getUncompressedFilename(final String filename) {
        return BZip2Utils.fileNameUtil.getUncompressedFilename(filename);
    }
    
    public static String getCompressedFilename(final String filename) {
        return BZip2Utils.fileNameUtil.getCompressedFilename(filename);
    }
    
    static {
        final Map<String, String> uncompressSuffix = new LinkedHashMap<String, String>();
        uncompressSuffix.put(".tar.bz2", ".tar");
        uncompressSuffix.put(".tbz2", ".tar");
        uncompressSuffix.put(".tbz", ".tar");
        uncompressSuffix.put(".bz2", "");
        uncompressSuffix.put(".bz", "");
        fileNameUtil = new FileNameUtil(uncompressSuffix, ".bz2");
    }
}
