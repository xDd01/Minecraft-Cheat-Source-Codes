package io.netty.channel.oio;

import io.netty.channel.*;
import java.util.concurrent.*;

public class OioEventLoopGroup extends ThreadPerChannelEventLoopGroup
{
    public OioEventLoopGroup() {
        this(0);
    }
    
    public OioEventLoopGroup(final int maxChannels) {
        this(maxChannels, Executors.defaultThreadFactory());
    }
    
    public OioEventLoopGroup(final int maxChannels, final ThreadFactory threadFactory) {
        super(maxChannels, threadFactory, new Object[0]);
    }
}
