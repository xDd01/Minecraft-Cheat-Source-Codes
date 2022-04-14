package io.netty.handler.codec.http;

import io.netty.buffer.*;

public interface HttpContent extends HttpObject, ByteBufHolder
{
    HttpContent copy();
    
    HttpContent duplicate();
    
    HttpContent retain();
    
    HttpContent retain(final int p0);
}
