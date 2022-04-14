package org.apache.http.conn.util;

import org.apache.http.annotation.*;
import java.util.*;
import java.io.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public final class PublicSuffixListParser
{
    public PublicSuffixList parse(final Reader reader) throws IOException {
        final List<String> rules = new ArrayList<String>();
        final List<String> exceptions = new ArrayList<String>();
        final BufferedReader r = new BufferedReader(reader);
        String line;
        while ((line = r.readLine()) != null) {
            if (line.isEmpty()) {
                continue;
            }
            if (line.startsWith("//")) {
                continue;
            }
            if (line.startsWith(".")) {
                line = line.substring(1);
            }
            final boolean isException = line.startsWith("!");
            if (isException) {
                line = line.substring(1);
            }
            if (isException) {
                exceptions.add(line);
            }
            else {
                rules.add(line);
            }
        }
        return new PublicSuffixList(DomainType.UNKNOWN, rules, exceptions);
    }
    
    public List<PublicSuffixList> parseByType(final Reader reader) throws IOException {
        final List<PublicSuffixList> result = new ArrayList<PublicSuffixList>(2);
        final BufferedReader r = new BufferedReader(reader);
        final StringBuilder sb = new StringBuilder(256);
        DomainType domainType = null;
        List<String> rules = null;
        List<String> exceptions = null;
        String line;
        while ((line = r.readLine()) != null) {
            if (line.isEmpty()) {
                continue;
            }
            if (line.startsWith("//")) {
                if (domainType == null) {
                    if (line.contains("===BEGIN ICANN DOMAINS===")) {
                        domainType = DomainType.ICANN;
                    }
                    else {
                        if (!line.contains("===BEGIN PRIVATE DOMAINS===")) {
                            continue;
                        }
                        domainType = DomainType.PRIVATE;
                    }
                }
                else {
                    if (!line.contains("===END ICANN DOMAINS===") && !line.contains("===END PRIVATE DOMAINS===")) {
                        continue;
                    }
                    if (rules != null) {
                        result.add(new PublicSuffixList(domainType, rules, exceptions));
                    }
                    domainType = null;
                    rules = null;
                    exceptions = null;
                }
            }
            else {
                if (domainType == null) {
                    continue;
                }
                if (line.startsWith(".")) {
                    line = line.substring(1);
                }
                final boolean isException = line.startsWith("!");
                if (isException) {
                    line = line.substring(1);
                }
                if (isException) {
                    if (exceptions == null) {
                        exceptions = new ArrayList<String>();
                    }
                    exceptions.add(line);
                }
                else {
                    if (rules == null) {
                        rules = new ArrayList<String>();
                    }
                    rules.add(line);
                }
            }
        }
        return result;
    }
}
