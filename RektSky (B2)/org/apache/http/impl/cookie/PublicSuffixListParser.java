package org.apache.http.impl.cookie;

import org.apache.http.annotation.*;
import java.util.*;
import org.apache.http.conn.util.*;
import java.io.*;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class PublicSuffixListParser
{
    private final PublicSuffixFilter filter;
    private final org.apache.http.conn.util.PublicSuffixListParser parser;
    
    PublicSuffixListParser(final PublicSuffixFilter filter) {
        this.filter = filter;
        this.parser = new org.apache.http.conn.util.PublicSuffixListParser();
    }
    
    public void parse(final Reader reader) throws IOException {
        final PublicSuffixList suffixList = this.parser.parse(reader);
        this.filter.setPublicSuffixes(suffixList.getRules());
        this.filter.setExceptions(suffixList.getExceptions());
    }
}
