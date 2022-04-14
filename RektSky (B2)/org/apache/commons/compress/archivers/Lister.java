package org.apache.commons.compress.archivers;

import java.nio.file.*;
import java.io.*;
import org.apache.commons.compress.archivers.sevenz.*;

public final class Lister
{
    private static final ArchiveStreamFactory factory;
    
    public static void main(final String[] args) throws Exception {
        if (args.length == 0) {
            usage();
            return;
        }
        System.out.println("Analysing " + args[0]);
        final File f = new File(args[0]);
        if (!f.isFile()) {
            System.err.println(f + " doesn't exist or is a directory");
        }
        final String format = (args.length > 1) ? args[1] : detectFormat(f);
        if ("7z".equalsIgnoreCase(format)) {
            list7z(f);
        }
        else {
            listStream(f, args);
        }
    }
    
    private static void listStream(final File f, final String[] args) throws ArchiveException, IOException {
        try (final InputStream fis = new BufferedInputStream(Files.newInputStream(f.toPath(), new OpenOption[0]));
             final ArchiveInputStream ais = createArchiveInputStream(args, fis)) {
            System.out.println("Created " + ais.toString());
            ArchiveEntry ae;
            while ((ae = ais.getNextEntry()) != null) {
                System.out.println(ae.getName());
            }
        }
    }
    
    private static ArchiveInputStream createArchiveInputStream(final String[] args, final InputStream fis) throws ArchiveException {
        if (args.length > 1) {
            return Lister.factory.createArchiveInputStream(args[1], fis);
        }
        return Lister.factory.createArchiveInputStream(fis);
    }
    
    private static String detectFormat(final File f) throws ArchiveException, IOException {
        try (final InputStream fis = new BufferedInputStream(Files.newInputStream(f.toPath(), new OpenOption[0]))) {
            final ArchiveStreamFactory factory = Lister.factory;
            return ArchiveStreamFactory.detect(fis);
        }
    }
    
    private static void list7z(final File f) throws ArchiveException, IOException {
        try (final SevenZFile z = new SevenZFile(f)) {
            System.out.println("Created " + z.toString());
            ArchiveEntry ae;
            while ((ae = z.getNextEntry()) != null) {
                System.out.println(ae.getName());
            }
        }
    }
    
    private static void usage() {
        System.out.println("Parameters: archive-name [archive-type]");
    }
    
    static {
        factory = new ArchiveStreamFactory();
    }
}
