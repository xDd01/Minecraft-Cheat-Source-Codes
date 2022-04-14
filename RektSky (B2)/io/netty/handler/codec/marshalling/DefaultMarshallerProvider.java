package io.netty.handler.codec.marshalling;

import io.netty.channel.*;
import org.jboss.marshalling.*;

public class DefaultMarshallerProvider implements MarshallerProvider
{
    private final MarshallerFactory factory;
    private final MarshallingConfiguration config;
    
    public DefaultMarshallerProvider(final MarshallerFactory factory, final MarshallingConfiguration config) {
        this.factory = factory;
        this.config = config;
    }
    
    @Override
    public Marshaller getMarshaller(final ChannelHandlerContext ctx) throws Exception {
        return this.factory.createMarshaller(this.config);
    }
}
