package io.netty.bootstrap;

import io.netty.channel.*;

public interface ChannelFactory<T extends Channel>
{
    T newChannel();
}
