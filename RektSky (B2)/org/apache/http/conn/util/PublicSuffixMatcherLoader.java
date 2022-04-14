package org.apache.http.conn.util;

import org.apache.http.annotation.*;
import org.apache.http.*;
import java.net.*;
import org.apache.http.util.*;
import java.io.*;
import java.util.*;
import org.apache.commons.logging.*;

@Contract(threading = ThreadingBehavior.SAFE)
public final class PublicSuffixMatcherLoader
{
    private static volatile PublicSuffixMatcher DEFAULT_INSTANCE;
    
    private static PublicSuffixMatcher load(final InputStream in) throws IOException {
        final List<PublicSuffixList> lists = new PublicSuffixListParser().parseByType(new InputStreamReader(in, Consts.UTF_8));
        return new PublicSuffixMatcher(lists);
    }
    
    public static PublicSuffixMatcher load(final URL url) throws IOException {
        Args.notNull(url, "URL");
        final InputStream in = url.openStream();
        try {
            return load(in);
        }
        finally {
            in.close();
        }
    }
    
    public static PublicSuffixMatcher load(final File file) throws IOException {
        Args.notNull(file, "File");
        final InputStream in = new FileInputStream(file);
        try {
            return load(in);
        }
        finally {
            in.close();
        }
    }
    
    public static PublicSuffixMatcher getDefault() {
        if (PublicSuffixMatcherLoader.DEFAULT_INSTANCE == null) {
            synchronized (PublicSuffixMatcherLoader.class) {
                if (PublicSuffixMatcherLoader.DEFAULT_INSTANCE == null) {
                    final URL url = PublicSuffixMatcherLoader.class.getResource("/mozilla/public-suffix-list.txt");
                    if (url != null) {
                        try {
                            PublicSuffixMatcherLoader.DEFAULT_INSTANCE = load(url);
                        }
                        catch (IOException ex) {
                            final Log log = LogFactory.getLog(PublicSuffixMatcherLoader.class);
                            if (log.isWarnEnabled()) {
                                log.warn("Failure loading public suffix list from default resource", ex);
                            }
                        }
                    }
                    else {
                        PublicSuffixMatcherLoader.DEFAULT_INSTANCE = new PublicSuffixMatcher(Arrays.asList("com"), null);
                    }
                }
            }
        }
        return PublicSuffixMatcherLoader.DEFAULT_INSTANCE;
    }
}
