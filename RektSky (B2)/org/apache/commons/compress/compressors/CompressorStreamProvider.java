package org.apache.commons.compress.compressors;

import java.io.*;
import java.util.*;

public interface CompressorStreamProvider
{
    CompressorInputStream createCompressorInputStream(final String p0, final InputStream p1, final boolean p2) throws CompressorException;
    
    CompressorOutputStream createCompressorOutputStream(final String p0, final OutputStream p1) throws CompressorException;
    
    Set<String> getInputStreamCompressorNames();
    
    Set<String> getOutputStreamCompressorNames();
}
