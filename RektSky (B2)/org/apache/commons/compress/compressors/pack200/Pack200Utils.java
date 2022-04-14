package org.apache.commons.compress.compressors.pack200;

import java.util.*;
import java.io.*;
import java.util.jar.*;

public class Pack200Utils
{
    private Pack200Utils() {
    }
    
    public static void normalize(final File jar) throws IOException {
        normalize(jar, jar, null);
    }
    
    public static void normalize(final File jar, final Map<String, String> props) throws IOException {
        normalize(jar, jar, props);
    }
    
    public static void normalize(final File from, final File to) throws IOException {
        normalize(from, to, null);
    }
    
    public static void normalize(final File from, final File to, Map<String, String> props) throws IOException {
        if (props == null) {
            props = new HashMap<String, String>();
        }
        props.put("pack.segment.limit", "-1");
        final File tempFile = File.createTempFile("commons-compress", "pack200normalize");
        try {
            try (final FileOutputStream fos = new FileOutputStream(tempFile);
                 final JarFile jarFile = new JarFile(from)) {
                final Pack200.Packer packer = Pack200.newPacker();
                packer.properties().putAll((Map<?, ?>)props);
                packer.pack(jarFile, fos);
            }
            final Pack200.Unpacker unpacker = Pack200.newUnpacker();
            try (final JarOutputStream jos = new JarOutputStream(new FileOutputStream(to))) {
                unpacker.unpack(tempFile, jos);
            }
        }
        finally {
            if (!tempFile.delete()) {
                tempFile.deleteOnExit();
            }
        }
    }
}
