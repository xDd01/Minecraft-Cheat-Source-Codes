package io.netty.channel;

import java.lang.annotation.*;

public interface ChannelHandler
{
    void handlerAdded(final ChannelHandlerContext p0) throws Exception;
    
    void handlerRemoved(final ChannelHandlerContext p0) throws Exception;
    
    void exceptionCaught(final ChannelHandlerContext p0, final Throwable p1) throws Exception;
    
    @Inherited
    @Documented
    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Sharable {
    }
}
