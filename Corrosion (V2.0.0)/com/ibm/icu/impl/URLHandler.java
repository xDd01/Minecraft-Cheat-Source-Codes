/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUDebug;
import com.ibm.icu.impl.Utility;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class URLHandler {
    public static final String PROPNAME = "urlhandler.props";
    private static final Map<String, Method> handlers;
    private static final boolean DEBUG;

    public static URLHandler get(URL url) {
        block9: {
            Method m2;
            if (url == null) {
                return null;
            }
            String protocol = url.getProtocol();
            if (handlers != null && (m2 = handlers.get(protocol)) != null) {
                try {
                    URLHandler handler = (URLHandler)m2.invoke(null, url);
                    if (handler != null) {
                        return handler;
                    }
                }
                catch (IllegalAccessException e2) {
                    if (DEBUG) {
                        System.err.println(e2);
                    }
                }
                catch (IllegalArgumentException e3) {
                    if (DEBUG) {
                        System.err.println(e3);
                    }
                }
                catch (InvocationTargetException e4) {
                    if (!DEBUG) break block9;
                    System.err.println(e4);
                }
            }
        }
        return URLHandler.getDefault(url);
    }

    protected static URLHandler getDefault(URL url) {
        URLHandler handler = null;
        String protocol = url.getProtocol();
        try {
            if (protocol.equals("file")) {
                handler = new FileURLHandler(url);
            } else if (protocol.equals("jar") || protocol.equals("wsjar")) {
                handler = new JarURLHandler(url);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return handler;
    }

    public void guide(URLVisitor visitor, boolean recurse) {
        this.guide(visitor, recurse, true);
    }

    public abstract void guide(URLVisitor var1, boolean var2, boolean var3);

    static {
        HashMap<String, Method> h2;
        block13: {
            DEBUG = ICUDebug.enabled("URLHandler");
            h2 = null;
            try {
                InputStream is2 = URLHandler.class.getResourceAsStream(PROPNAME);
                if (is2 == null) {
                    ClassLoader loader = Utility.getFallbackClassLoader();
                    is2 = loader.getResourceAsStream(PROPNAME);
                }
                if (is2 == null) break block13;
                Class[] params = new Class[]{URL.class};
                BufferedReader br2 = new BufferedReader(new InputStreamReader(is2));
                String line = br2.readLine();
                while (line != null) {
                    block14: {
                        if ((line = line.trim()).length() != 0 && line.charAt(0) != '#') {
                            int ix2 = line.indexOf(61);
                            if (ix2 == -1) {
                                if (!DEBUG) break;
                                System.err.println("bad urlhandler line: '" + line + "'");
                                break;
                            }
                            String key = line.substring(0, ix2).trim();
                            String value = line.substring(ix2 + 1).trim();
                            try {
                                Class<?> cl2 = Class.forName(value);
                                Method m2 = cl2.getDeclaredMethod("get", params);
                                if (h2 == null) {
                                    h2 = new HashMap<String, Method>();
                                }
                                h2.put(key, m2);
                            }
                            catch (ClassNotFoundException e2) {
                                if (DEBUG) {
                                    System.err.println(e2);
                                }
                            }
                            catch (NoSuchMethodException e3) {
                                if (DEBUG) {
                                    System.err.println(e3);
                                }
                            }
                            catch (SecurityException e4) {
                                if (!DEBUG) break block14;
                                System.err.println(e4);
                            }
                        }
                    }
                    line = br2.readLine();
                }
                br2.close();
            }
            catch (Throwable t2) {
                if (!DEBUG) break block13;
                System.err.println(t2);
            }
        }
        handlers = h2;
    }

    public static interface URLVisitor {
        public void visit(String var1);
    }

    private static class JarURLHandler
    extends URLHandler {
        JarFile jarFile;
        String prefix;

        JarURLHandler(URL url) {
            try {
                String urlStr;
                int idx;
                String protocol;
                this.prefix = url.getPath();
                int ix2 = this.prefix.lastIndexOf("!/");
                if (ix2 >= 0) {
                    this.prefix = this.prefix.substring(ix2 + 2);
                }
                if (!(protocol = url.getProtocol()).equals("jar") && (idx = (urlStr = url.toString()).indexOf(":")) != -1) {
                    url = new URL("jar" + urlStr.substring(idx));
                }
                JarURLConnection conn = (JarURLConnection)url.openConnection();
                this.jarFile = conn.getJarFile();
            }
            catch (Exception e2) {
                if (DEBUG) {
                    System.err.println("icurb jar error: " + e2);
                }
                throw new IllegalArgumentException("jar error: " + e2.getMessage());
            }
        }

        public void guide(URLVisitor v2, boolean recurse, boolean strip) {
            block5: {
                try {
                    Enumeration<JarEntry> entries = this.jarFile.entries();
                    while (entries.hasMoreElements()) {
                        String name;
                        JarEntry entry = entries.nextElement();
                        if (entry.isDirectory() || !(name = entry.getName()).startsWith(this.prefix)) continue;
                        int ix2 = (name = name.substring(this.prefix.length())).lastIndexOf(47);
                        if (ix2 != -1) {
                            if (!recurse) continue;
                            if (strip) {
                                name = name.substring(ix2 + 1);
                            }
                        }
                        v2.visit(name);
                    }
                }
                catch (Exception e2) {
                    if (!DEBUG) break block5;
                    System.err.println("icurb jar error: " + e2);
                }
            }
        }
    }

    private static class FileURLHandler
    extends URLHandler {
        File file;

        FileURLHandler(URL url) {
            try {
                this.file = new File(url.toURI());
            }
            catch (URISyntaxException uRISyntaxException) {
                // empty catch block
            }
            if (this.file == null || !this.file.exists()) {
                if (DEBUG) {
                    System.err.println("file does not exist - " + url.toString());
                }
                throw new IllegalArgumentException();
            }
        }

        public void guide(URLVisitor v2, boolean recurse, boolean strip) {
            if (this.file.isDirectory()) {
                this.process(v2, recurse, strip, "/", this.file.listFiles());
            } else {
                v2.visit(this.file.getName());
            }
        }

        private void process(URLVisitor v2, boolean recurse, boolean strip, String path, File[] files) {
            for (int i2 = 0; i2 < files.length; ++i2) {
                File f2 = files[i2];
                if (f2.isDirectory()) {
                    if (!recurse) continue;
                    this.process(v2, recurse, strip, path + f2.getName() + '/', f2.listFiles());
                    continue;
                }
                v2.visit(strip ? f2.getName() : path + f2.getName());
            }
        }
    }
}

