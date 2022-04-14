package io.netty.channel.group;

import io.netty.channel.*;

public interface ChannelMatcher
{
    boolean matches(final Channel p0);
}
