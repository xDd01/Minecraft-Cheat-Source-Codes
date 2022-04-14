package io.netty.buffer;

import io.netty.util.*;

public interface ByteBufHolder extends ReferenceCounted
{
    ByteBuf content();
    
    ByteBufHolder copy();
    
    ByteBufHolder duplicate();
    
    ByteBufHolder retain();
    
    ByteBufHolder retain(final int p0);
}
