/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl.duration.impl;

import com.ibm.icu.impl.ICUData;
import com.ibm.icu.impl.duration.impl.DataRecord;
import com.ibm.icu.impl.duration.impl.PeriodFormatterData;
import com.ibm.icu.impl.duration.impl.PeriodFormatterDataService;
import com.ibm.icu.impl.duration.impl.XMLRecordReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ResourceBasedPeriodFormatterDataService
extends PeriodFormatterDataService {
    private Collection<String> availableLocales;
    private PeriodFormatterData lastData = null;
    private String lastLocale = null;
    private Map<String, PeriodFormatterData> cache = new HashMap<String, PeriodFormatterData>();
    private static final String PATH = "data/";
    private static final ResourceBasedPeriodFormatterDataService singleton = new ResourceBasedPeriodFormatterDataService();

    public static ResourceBasedPeriodFormatterDataService getInstance() {
        return singleton;
    }

    private ResourceBasedPeriodFormatterDataService() {
        ArrayList<String> localeNames = new ArrayList<String>();
        InputStream is2 = ICUData.getRequiredStream(this.getClass(), "data/index.txt");
        try {
            BufferedReader br2 = new BufferedReader(new InputStreamReader(is2, "UTF-8"));
            String string = null;
            while (null != (string = br2.readLine())) {
                if ((string = string.trim()).startsWith("#") || string.length() == 0) continue;
                localeNames.add(string);
            }
            br2.close();
        }
        catch (IOException e2) {
            throw new IllegalStateException("IO Error reading data/index.txt: " + e2.toString());
        }
        this.availableLocales = Collections.unmodifiableList(localeNames);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public PeriodFormatterData get(String localeName) {
        int x2 = localeName.indexOf(64);
        if (x2 != -1) {
            localeName = localeName.substring(0, x2);
        }
        ResourceBasedPeriodFormatterDataService resourceBasedPeriodFormatterDataService = this;
        synchronized (resourceBasedPeriodFormatterDataService) {
            if (this.lastLocale != null && this.lastLocale.equals(localeName)) {
                return this.lastData;
            }
            PeriodFormatterData ld2 = this.cache.get(localeName);
            if (ld2 == null) {
                String ln2 = localeName;
                while (!this.availableLocales.contains(ln2)) {
                    int ix2 = ln2.lastIndexOf("_");
                    if (ix2 > -1) {
                        ln2 = ln2.substring(0, ix2);
                        continue;
                    }
                    if (!"test".equals(ln2)) {
                        ln2 = "test";
                        continue;
                    }
                    ln2 = null;
                    break;
                }
                if (ln2 == null) throw new MissingResourceException("Duration data not found for  " + localeName, PATH, localeName);
                String name = "data/pfd_" + ln2 + ".xml";
                try {
                    InputStream is2 = ICUData.getStream(this.getClass(), name);
                    if (is2 == null) {
                        throw new MissingResourceException("no resource named " + name, name, "");
                    }
                    DataRecord dr2 = DataRecord.read(ln2, new XMLRecordReader(new InputStreamReader(is2, "UTF-8")));
                    if (dr2 != null) {
                        ld2 = new PeriodFormatterData(localeName, dr2);
                    }
                }
                catch (UnsupportedEncodingException e2) {
                    throw new MissingResourceException("Unhandled Encoding for resource " + name, name, "");
                }
                this.cache.put(localeName, ld2);
            }
            this.lastData = ld2;
            this.lastLocale = localeName;
            return ld2;
        }
    }

    @Override
    public Collection<String> getAvailableLocales() {
        return this.availableLocales;
    }
}

