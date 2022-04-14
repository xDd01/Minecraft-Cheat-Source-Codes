package org.apache.commons.compress.archivers.jar;

import org.apache.commons.compress.archivers.zip.*;
import java.security.cert.*;
import java.util.zip.*;
import java.util.jar.*;

public class JarArchiveEntry extends ZipArchiveEntry
{
    private final Attributes manifestAttributes;
    private final Certificate[] certificates;
    
    public JarArchiveEntry(final ZipEntry entry) throws ZipException {
        super(entry);
        this.manifestAttributes = null;
        this.certificates = null;
    }
    
    public JarArchiveEntry(final String name) {
        super(name);
        this.manifestAttributes = null;
        this.certificates = null;
    }
    
    public JarArchiveEntry(final ZipArchiveEntry entry) throws ZipException {
        super(entry);
        this.manifestAttributes = null;
        this.certificates = null;
    }
    
    public JarArchiveEntry(final JarEntry entry) throws ZipException {
        super(entry);
        this.manifestAttributes = null;
        this.certificates = null;
    }
    
    @Deprecated
    public Attributes getManifestAttributes() {
        return this.manifestAttributes;
    }
    
    @Deprecated
    public Certificate[] getCertificates() {
        if (this.certificates != null) {
            final Certificate[] certs = new Certificate[this.certificates.length];
            System.arraycopy(this.certificates, 0, certs, 0, certs.length);
            return certs;
        }
        return null;
    }
}
