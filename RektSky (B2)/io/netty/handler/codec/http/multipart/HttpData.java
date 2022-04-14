package io.netty.handler.codec.http.multipart;

import io.netty.buffer.*;
import java.io.*;
import java.nio.charset.*;

public interface HttpData extends InterfaceHttpData, ByteBufHolder
{
    void setContent(final ByteBuf p0) throws IOException;
    
    void addContent(final ByteBuf p0, final boolean p1) throws IOException;
    
    void setContent(final File p0) throws IOException;
    
    void setContent(final InputStream p0) throws IOException;
    
    boolean isCompleted();
    
    long length();
    
    void delete();
    
    byte[] get() throws IOException;
    
    ByteBuf getByteBuf() throws IOException;
    
    ByteBuf getChunk(final int p0) throws IOException;
    
    String getString() throws IOException;
    
    String getString(final Charset p0) throws IOException;
    
    void setCharset(final Charset p0);
    
    Charset getCharset();
    
    boolean renameTo(final File p0) throws IOException;
    
    boolean isInMemory();
    
    File getFile() throws IOException;
    
    HttpData copy();
    
    HttpData duplicate();
    
    HttpData retain();
    
    HttpData retain(final int p0);
}
