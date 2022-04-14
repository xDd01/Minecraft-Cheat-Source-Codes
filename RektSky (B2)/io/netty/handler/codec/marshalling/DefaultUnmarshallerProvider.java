package io.netty.handler.codec.marshalling;

import io.netty.channel.*;
import org.jboss.marshalling.*;

public class DefaultUnmarshallerProvider implements UnmarshallerProvider
{
    private final MarshallerFactory factory;
    private final MarshallingConfiguration config;
    
    public DefaultUnmarshallerProvider(final MarshallerFactory factory, final MarshallingConfiguration config) {
        this.factory = factory;
        this.config = config;
    }
    
    @Override
    public Unmarshaller getUnmarshaller(final ChannelHandlerContext ctx) throws Exception {
        return this.factory.createUnmarshaller(this.config);
    }
}
