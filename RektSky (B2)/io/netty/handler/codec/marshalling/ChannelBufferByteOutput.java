package io.netty.handler.codec.marshalling;

import org.jboss.marshalling.*;
import io.netty.buffer.*;
import java.io.*;

class ChannelBufferByteOutput implements ByteOutput
{
    private final ByteBuf buffer;
    
    ChannelBufferByteOutput(final ByteBuf buffer) {
        this.buffer = buffer;
    }
    
    public void close() throws IOException {
    }
    
    public void flush() throws IOException {
    }
    
    public void write(final int b) throws IOException {
        this.buffer.writeByte(b);
    }
    
    public void write(final byte[] bytes) throws IOException {
        this.buffer.writeBytes(bytes);
    }
    
    public void write(final byte[] bytes, final int srcIndex, final int length) throws IOException {
        this.buffer.writeBytes(bytes, srcIndex, length);
    }
    
    ByteBuf getBuffer() {
        return this.buffer;
    }
}
