package org.apache.commons.compress.archivers;

import java.io.*;
import java.util.*;

public interface ArchiveStreamProvider
{
    ArchiveInputStream createArchiveInputStream(final String p0, final InputStream p1, final String p2) throws ArchiveException;
    
    ArchiveOutputStream createArchiveOutputStream(final String p0, final OutputStream p1, final String p2) throws ArchiveException;
    
    Set<String> getInputStreamArchiveNames();
    
    Set<String> getOutputStreamArchiveNames();
}
