/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.compressors.pack200;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.zip.ZipFile;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Pack200Utils {
    private Pack200Utils() {
    }

    public static void normalize(File jar) throws IOException {
        Pack200Utils.normalize(jar, jar, null);
    }

    public static void normalize(File jar, Map<String, String> props) throws IOException {
        Pack200Utils.normalize(jar, jar, props);
    }

    public static void normalize(File from, File to2) throws IOException {
        Pack200Utils.normalize(from, to2, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void normalize(File from, File to2, Map<String, String> props) throws IOException {
        if (props == null) {
            props = new HashMap<String, String>();
        }
        props.put("pack.segment.limit", "-1");
        File f2 = File.createTempFile("commons-compress", "pack200normalize");
        f2.deleteOnExit();
        try {
            OutputStream os2 = new FileOutputStream(f2);
            ZipFile j2 = null;
            try {
                Pack200.Packer p2 = Pack200.newPacker();
                p2.properties().putAll(props);
                j2 = new JarFile(from);
                p2.pack((JarFile)j2, os2);
                j2 = null;
                os2.close();
                os2 = null;
                Pack200.Unpacker u2 = Pack200.newUnpacker();
                os2 = new JarOutputStream(new FileOutputStream(to2));
                u2.unpack(f2, (JarOutputStream)os2);
            }
            finally {
                if (j2 != null) {
                    j2.close();
                }
                if (os2 != null) {
                    os2.close();
                }
            }
        }
        finally {
            f2.delete();
        }
    }
}

