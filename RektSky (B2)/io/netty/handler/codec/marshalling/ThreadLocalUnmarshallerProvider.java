package io.netty.handler.codec.marshalling;

import io.netty.util.concurrent.*;
import org.jboss.marshalling.*;
import io.netty.channel.*;

public class ThreadLocalUnmarshallerProvider implements UnmarshallerProvider
{
    private final FastThreadLocal<Unmarshaller> unmarshallers;
    private final MarshallerFactory factory;
    private final MarshallingConfiguration config;
    
    public ThreadLocalUnmarshallerProvider(final MarshallerFactory factory, final MarshallingConfiguration config) {
        this.unmarshallers = new FastThreadLocal<Unmarshaller>();
        this.factory = factory;
        this.config = config;
    }
    
    @Override
    public Unmarshaller getUnmarshaller(final ChannelHandlerContext ctx) throws Exception {
        Unmarshaller unmarshaller = this.unmarshallers.get();
        if (unmarshaller == null) {
            unmarshaller = this.factory.createUnmarshaller(this.config);
            this.unmarshallers.set(unmarshaller);
        }
        return unmarshaller;
    }
}
