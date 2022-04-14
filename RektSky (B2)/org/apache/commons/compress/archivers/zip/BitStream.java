package org.apache.commons.compress.archivers.zip;

import org.apache.commons.compress.utils.*;
import java.nio.*;
import java.io.*;

class BitStream extends BitInputStream
{
    BitStream(final InputStream in) {
        super(in, ByteOrder.LITTLE_ENDIAN);
    }
    
    int nextBit() throws IOException {
        return (int)this.readBits(1);
    }
    
    long nextBits(final int n) throws IOException {
        return this.readBits(n);
    }
    
    int nextByte() throws IOException {
        return (int)this.readBits(8);
    }
}
