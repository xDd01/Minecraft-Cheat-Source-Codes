package org.apache.commons.compress.archivers.jar;

import org.apache.commons.compress.archivers.zip.*;
import java.io.*;
import org.apache.commons.compress.archivers.*;

public class JarArchiveInputStream extends ZipArchiveInputStream
{
    public JarArchiveInputStream(final InputStream inputStream) {
        super(inputStream);
    }
    
    public JarArchiveInputStream(final InputStream inputStream, final String encoding) {
        super(inputStream, encoding);
    }
    
    public JarArchiveEntry getNextJarEntry() throws IOException {
        final ZipArchiveEntry entry = this.getNextZipEntry();
        return (entry == null) ? null : new JarArchiveEntry(entry);
    }
    
    @Override
    public ArchiveEntry getNextEntry() throws IOException {
        return this.getNextJarEntry();
    }
    
    public static boolean matches(final byte[] signature, final int length) {
        return ZipArchiveInputStream.matches(signature, length);
    }
}
