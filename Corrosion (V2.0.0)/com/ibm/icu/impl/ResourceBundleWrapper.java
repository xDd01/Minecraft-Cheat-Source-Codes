/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUDebug;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ResourceBundleWrapper
extends UResourceBundle {
    private ResourceBundle bundle = null;
    private String localeID = null;
    private String baseName = null;
    private List<String> keys = null;
    private static final boolean DEBUG = ICUDebug.enabled("resourceBundleWrapper");

    private ResourceBundleWrapper(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    @Override
    protected void setLoadingStatus(int newStatus) {
    }

    @Override
    protected Object handleGetObject(String aKey) {
        Object obj = null;
        for (ResourceBundleWrapper current = this; current != null; current = (ResourceBundleWrapper)current.getParent()) {
            try {
                obj = current.bundle.getObject(aKey);
                break;
            }
            catch (MissingResourceException ex2) {
                continue;
            }
        }
        if (obj == null) {
            throw new MissingResourceException("Can't find resource for bundle " + this.baseName + ", key " + aKey, this.getClass().getName(), aKey);
        }
        return obj;
    }

    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(this.keys);
    }

    private void initKeysVector() {
        this.keys = new ArrayList<String>();
        for (ResourceBundleWrapper current = this; current != null; current = (ResourceBundleWrapper)current.getParent()) {
            Enumeration<String> e2 = current.bundle.getKeys();
            while (e2.hasMoreElements()) {
                String elem = e2.nextElement();
                if (this.keys.contains(elem)) continue;
                this.keys.add(elem);
            }
        }
    }

    @Override
    protected String getLocaleID() {
        return this.localeID;
    }

    @Override
    protected String getBaseName() {
        return this.bundle.getClass().getName().replace('.', '/');
    }

    @Override
    public ULocale getULocale() {
        return new ULocale(this.localeID);
    }

    @Override
    public UResourceBundle getParent() {
        return (UResourceBundle)this.parent;
    }

    public static UResourceBundle getBundleInstance(String baseName, String localeID, ClassLoader root, boolean disableFallback) {
        UResourceBundle b2 = ResourceBundleWrapper.instantiateBundle(baseName, localeID, root, disableFallback);
        if (b2 == null) {
            String separator = "_";
            if (baseName.indexOf(47) >= 0) {
                separator = "/";
            }
            throw new MissingResourceException("Could not find the bundle " + baseName + separator + localeID, "", "");
        }
        return b2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected static synchronized UResourceBundle instantiateBundle(String baseName, String localeID, ClassLoader root, boolean disableFallback) {
        ResourceBundleWrapper b2;
        if (root == null) {
            root = Utility.getFallbackClassLoader();
        }
        final ClassLoader cl2 = root;
        String name = baseName;
        ULocale defaultLocale = ULocale.getDefault();
        if (localeID.length() != 0) {
            name = name + "_" + localeID;
        }
        if ((b2 = (ResourceBundleWrapper)ResourceBundleWrapper.loadFromCache(cl2, name, defaultLocale)) == null) {
            block38: {
                boolean loadFromProperties;
                ResourceBundleWrapper parent;
                block36: {
                    parent = null;
                    int i2 = localeID.lastIndexOf(95);
                    loadFromProperties = false;
                    if (i2 != -1) {
                        String locName = localeID.substring(0, i2);
                        parent = (ResourceBundleWrapper)ResourceBundleWrapper.loadFromCache(cl2, baseName + "_" + locName, defaultLocale);
                        if (parent == null) {
                            parent = (ResourceBundleWrapper)ResourceBundleWrapper.instantiateBundle(baseName, locName, cl2, disableFallback);
                        }
                    } else if (localeID.length() > 0 && (parent = (ResourceBundleWrapper)ResourceBundleWrapper.loadFromCache(cl2, baseName, defaultLocale)) == null) {
                        parent = (ResourceBundleWrapper)ResourceBundleWrapper.instantiateBundle(baseName, "", cl2, disableFallback);
                    }
                    try {
                        Class<ResourceBundle> cls = cl2.loadClass(name).asSubclass(ResourceBundle.class);
                        ResourceBundle bx2 = cls.newInstance();
                        b2 = new ResourceBundleWrapper(bx2);
                        if (parent != null) {
                            b2.setParent(parent);
                        }
                        b2.baseName = baseName;
                        b2.localeID = localeID;
                    }
                    catch (ClassNotFoundException e2) {
                        loadFromProperties = true;
                    }
                    catch (NoClassDefFoundError e3) {
                        loadFromProperties = true;
                    }
                    catch (Exception e4) {
                        if (DEBUG) {
                            System.out.println("failure");
                        }
                        if (!DEBUG) break block36;
                        System.out.println(e4);
                    }
                }
                if (loadFromProperties) {
                    try {
                        final String resName = name.replace('.', '/') + ".properties";
                        InputStream stream = AccessController.doPrivileged(new PrivilegedAction<InputStream>(){

                            @Override
                            public InputStream run() {
                                if (cl2 != null) {
                                    return cl2.getResourceAsStream(resName);
                                }
                                return ClassLoader.getSystemResourceAsStream(resName);
                            }
                        });
                        if (stream != null) {
                            stream = new BufferedInputStream(stream);
                            try {
                                b2 = new ResourceBundleWrapper(new PropertyResourceBundle(stream));
                                if (parent != null) {
                                    b2.setParent(parent);
                                }
                                b2.baseName = baseName;
                                b2.localeID = localeID;
                            }
                            catch (Exception ex2) {
                            }
                            finally {
                                try {
                                    stream.close();
                                }
                                catch (Exception ex3) {}
                            }
                        }
                        if (b2 == null) {
                            String defaultName = defaultLocale.toString();
                            if (localeID.length() > 0 && localeID.indexOf(95) < 0 && defaultName.indexOf(localeID) == -1 && (b2 = (ResourceBundleWrapper)ResourceBundleWrapper.loadFromCache(cl2, baseName + "_" + defaultName, defaultLocale)) == null) {
                                b2 = (ResourceBundleWrapper)ResourceBundleWrapper.instantiateBundle(baseName, defaultName, cl2, disableFallback);
                            }
                        }
                        if (b2 == null) {
                            b2 = parent;
                        }
                    }
                    catch (Exception e5) {
                        if (DEBUG) {
                            System.out.println("failure");
                        }
                        if (!DEBUG) break block38;
                        System.out.println(e5);
                    }
                }
            }
            b2 = (ResourceBundleWrapper)ResourceBundleWrapper.addToCache(cl2, name, defaultLocale, b2);
        }
        if (b2 != null) {
            b2.initKeysVector();
        } else if (DEBUG) {
            System.out.println("Returning null for " + baseName + "_" + localeID);
        }
        return b2;
    }
}

