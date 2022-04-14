package io.netty.channel.epoll;

import java.util.concurrent.*;
import io.netty.util.concurrent.*;
import java.util.*;
import io.netty.channel.*;

public final class EpollEventLoopGroup extends MultithreadEventLoopGroup
{
    public EpollEventLoopGroup() {
        this(0);
    }
    
    public EpollEventLoopGroup(final int nThreads) {
        this(nThreads, null);
    }
    
    public EpollEventLoopGroup(final int nThreads, final ThreadFactory threadFactory) {
        this(nThreads, threadFactory, 128);
    }
    
    public EpollEventLoopGroup(final int nThreads, final ThreadFactory threadFactory, final int maxEventsAtOnce) {
        super(nThreads, threadFactory, new Object[] { maxEventsAtOnce });
    }
    
    public void setIoRatio(final int ioRatio) {
        for (final EventExecutor e : this.children()) {
            ((EpollEventLoop)e).setIoRatio(ioRatio);
        }
    }
    
    @Override
    protected EventExecutor newChild(final ThreadFactory threadFactory, final Object... args) throws Exception {
        return new EpollEventLoop(this, threadFactory, (int)args[0]);
    }
}
