package org.apache.commons.compress.archivers.jar;

import org.apache.commons.compress.archivers.*;
import org.apache.commons.compress.archivers.zip.*;
import java.io.*;

public class JarArchiveOutputStream extends ZipArchiveOutputStream
{
    private boolean jarMarkerAdded;
    
    public JarArchiveOutputStream(final OutputStream out) {
        super(out);
        this.jarMarkerAdded = false;
    }
    
    public JarArchiveOutputStream(final OutputStream out, final String encoding) {
        super(out);
        this.jarMarkerAdded = false;
        this.setEncoding(encoding);
    }
    
    @Override
    public void putArchiveEntry(final ArchiveEntry ze) throws IOException {
        if (!this.jarMarkerAdded) {
            ((ZipArchiveEntry)ze).addAsFirstExtraField(JarMarker.getInstance());
            this.jarMarkerAdded = true;
        }
        super.putArchiveEntry(ze);
    }
}
