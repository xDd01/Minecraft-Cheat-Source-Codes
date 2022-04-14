package io.netty.handler.codec.marshalling;

import io.netty.util.concurrent.*;
import org.jboss.marshalling.*;
import io.netty.channel.*;

public class ThreadLocalMarshallerProvider implements MarshallerProvider
{
    private final FastThreadLocal<Marshaller> marshallers;
    private final MarshallerFactory factory;
    private final MarshallingConfiguration config;
    
    public ThreadLocalMarshallerProvider(final MarshallerFactory factory, final MarshallingConfiguration config) {
        this.marshallers = new FastThreadLocal<Marshaller>();
        this.factory = factory;
        this.config = config;
    }
    
    @Override
    public Marshaller getMarshaller(final ChannelHandlerContext ctx) throws Exception {
        Marshaller marshaller = this.marshallers.get();
        if (marshaller == null) {
            marshaller = this.factory.createMarshaller(this.config);
            this.marshallers.set(marshaller);
        }
        return marshaller;
    }
}
