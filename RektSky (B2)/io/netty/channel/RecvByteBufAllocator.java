package io.netty.channel;

import io.netty.buffer.*;

public interface RecvByteBufAllocator
{
    Handle newHandle();
    
    public interface Handle
    {
        ByteBuf allocate(final ByteBufAllocator p0);
        
        int guess();
        
        void record(final int p0);
    }
}
