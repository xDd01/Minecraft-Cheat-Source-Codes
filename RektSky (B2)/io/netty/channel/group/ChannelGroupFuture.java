package io.netty.channel.group;

import io.netty.channel.*;
import io.netty.util.concurrent.*;
import java.util.*;

public interface ChannelGroupFuture extends Future<Void>, Iterable<ChannelFuture>
{
    ChannelGroup group();
    
    ChannelFuture find(final Channel p0);
    
    boolean isSuccess();
    
    ChannelGroupException cause();
    
    boolean isPartialSuccess();
    
    boolean isPartialFailure();
    
    ChannelGroupFuture addListener(final GenericFutureListener<? extends Future<? super Void>> p0);
    
    ChannelGroupFuture addListeners(final GenericFutureListener<? extends Future<? super Void>>... p0);
    
    ChannelGroupFuture removeListener(final GenericFutureListener<? extends Future<? super Void>> p0);
    
    ChannelGroupFuture removeListeners(final GenericFutureListener<? extends Future<? super Void>>... p0);
    
    ChannelGroupFuture await() throws InterruptedException;
    
    ChannelGroupFuture awaitUninterruptibly();
    
    ChannelGroupFuture syncUninterruptibly();
    
    ChannelGroupFuture sync() throws InterruptedException;
    
    Iterator<ChannelFuture> iterator();
}
