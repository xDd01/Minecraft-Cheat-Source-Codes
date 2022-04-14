/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.util;

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class OverlayBundle
extends ResourceBundle {
    private String[] baseNames;
    private Locale locale;
    private ResourceBundle[] bundles;

    public OverlayBundle(String[] baseNames, Locale locale) {
        this.baseNames = baseNames;
        this.locale = locale;
        this.bundles = new ResourceBundle[baseNames.length];
    }

    @Override
    protected Object handleGetObject(String key) throws MissingResourceException {
        Object o2 = null;
        for (int i2 = 0; i2 < this.bundles.length; ++i2) {
            block3: {
                this.load(i2);
                try {
                    o2 = this.bundles[i2].getObject(key);
                }
                catch (MissingResourceException e2) {
                    if (i2 != this.bundles.length - 1) break block3;
                    throw e2;
                }
            }
            if (o2 != null) break;
        }
        return o2;
    }

    @Override
    public Enumeration<String> getKeys() {
        int i2 = this.bundles.length - 1;
        this.load(i2);
        return this.bundles[i2].getKeys();
    }

    private void load(int i2) throws MissingResourceException {
        block9: {
            if (this.bundles[i2] == null) {
                boolean tryWildcard = false;
                try {
                    this.bundles[i2] = ResourceBundle.getBundle(this.baseNames[i2], this.locale);
                    if (this.bundles[i2].getLocale().equals(this.locale)) {
                        return;
                    }
                    if (this.locale.getCountry().length() != 0 && i2 != this.bundles.length - 1) {
                        tryWildcard = true;
                    }
                }
                catch (MissingResourceException e2) {
                    if (i2 == this.bundles.length - 1) {
                        throw e2;
                    }
                    tryWildcard = true;
                }
                if (tryWildcard) {
                    Locale wildcard = new Locale("xx", this.locale.getCountry(), this.locale.getVariant());
                    try {
                        this.bundles[i2] = ResourceBundle.getBundle(this.baseNames[i2], wildcard);
                    }
                    catch (MissingResourceException e3) {
                        if (this.bundles[i2] != null) break block9;
                        throw e3;
                    }
                }
            }
        }
    }
}

