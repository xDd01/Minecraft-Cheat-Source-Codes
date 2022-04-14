package io.netty.channel.group;

import java.util.*;
import io.netty.channel.*;

public interface ChannelGroup extends Set<Channel>, Comparable<ChannelGroup>
{
    String name();
    
    ChannelGroupFuture write(final Object p0);
    
    ChannelGroupFuture write(final Object p0, final ChannelMatcher p1);
    
    ChannelGroup flush();
    
    ChannelGroup flush(final ChannelMatcher p0);
    
    ChannelGroupFuture writeAndFlush(final Object p0);
    
    @Deprecated
    ChannelGroupFuture flushAndWrite(final Object p0);
    
    ChannelGroupFuture writeAndFlush(final Object p0, final ChannelMatcher p1);
    
    @Deprecated
    ChannelGroupFuture flushAndWrite(final Object p0, final ChannelMatcher p1);
    
    ChannelGroupFuture disconnect();
    
    ChannelGroupFuture disconnect(final ChannelMatcher p0);
    
    ChannelGroupFuture close();
    
    ChannelGroupFuture close(final ChannelMatcher p0);
    
    @Deprecated
    ChannelGroupFuture deregister();
    
    @Deprecated
    ChannelGroupFuture deregister(final ChannelMatcher p0);
}
