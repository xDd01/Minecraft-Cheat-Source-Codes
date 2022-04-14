package org.apache.commons.compress.archivers.zip;

import java.nio.*;
import java.io.*;

public interface ZipEncoding
{
    boolean canEncode(final String p0);
    
    ByteBuffer encode(final String p0) throws IOException;
    
    String decode(final byte[] p0) throws IOException;
}
