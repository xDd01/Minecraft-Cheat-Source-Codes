package io.netty.channel.nio;

import io.netty.channel.*;
import java.util.concurrent.*;
import java.nio.channels.spi.*;
import io.netty.util.concurrent.*;
import java.util.*;

public class NioEventLoopGroup extends MultithreadEventLoopGroup
{
    public NioEventLoopGroup() {
        this(0);
    }
    
    public NioEventLoopGroup(final int nThreads) {
        this(nThreads, null);
    }
    
    public NioEventLoopGroup(final int nThreads, final ThreadFactory threadFactory) {
        this(nThreads, threadFactory, SelectorProvider.provider());
    }
    
    public NioEventLoopGroup(final int nThreads, final ThreadFactory threadFactory, final SelectorProvider selectorProvider) {
        super(nThreads, threadFactory, new Object[] { selectorProvider });
    }
    
    public void setIoRatio(final int ioRatio) {
        for (final EventExecutor e : this.children()) {
            ((NioEventLoop)e).setIoRatio(ioRatio);
        }
    }
    
    public void rebuildSelectors() {
        for (final EventExecutor e : this.children()) {
            ((NioEventLoop)e).rebuildSelector();
        }
    }
    
    @Override
    protected EventExecutor newChild(final ThreadFactory threadFactory, final Object... args) throws Exception {
        return new NioEventLoop(this, threadFactory, (SelectorProvider)args[0]);
    }
}
