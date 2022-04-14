/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.archivers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;

public final class Lister {
    private static final ArchiveStreamFactory factory = new ArchiveStreamFactory();

    public static void main(String[] args) throws Exception {
        ArchiveEntry ae2;
        if (args.length == 0) {
            Lister.usage();
            return;
        }
        System.out.println("Analysing " + args[0]);
        File f2 = new File(args[0]);
        if (!f2.isFile()) {
            System.err.println(f2 + " doesn't exist or is a directory");
        }
        BufferedInputStream fis = new BufferedInputStream(new FileInputStream(f2));
        ArchiveInputStream ais2 = args.length > 1 ? factory.createArchiveInputStream(args[1], fis) : factory.createArchiveInputStream(fis);
        System.out.println("Created " + ais2.toString());
        while ((ae2 = ais2.getNextEntry()) != null) {
            System.out.println(ae2.getName());
        }
        ais2.close();
        ((InputStream)fis).close();
    }

    private static void usage() {
        System.out.println("Parameters: archive-name [archive-type]");
    }
}

