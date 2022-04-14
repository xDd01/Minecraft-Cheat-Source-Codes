package io.netty.handler.codec.marshalling;

import org.jboss.marshalling.*;
import io.netty.channel.*;
import io.netty.util.*;

public class ContextBoundUnmarshallerProvider extends DefaultUnmarshallerProvider
{
    private static final AttributeKey<Unmarshaller> UNMARSHALLER;
    
    public ContextBoundUnmarshallerProvider(final MarshallerFactory factory, final MarshallingConfiguration config) {
        super(factory, config);
    }
    
    @Override
    public Unmarshaller getUnmarshaller(final ChannelHandlerContext ctx) throws Exception {
        final Attribute<Unmarshaller> attr = ctx.attr(ContextBoundUnmarshallerProvider.UNMARSHALLER);
        Unmarshaller unmarshaller = attr.get();
        if (unmarshaller == null) {
            unmarshaller = super.getUnmarshaller(ctx);
            attr.set(unmarshaller);
        }
        return unmarshaller;
    }
    
    static {
        UNMARSHALLER = AttributeKey.valueOf(ContextBoundUnmarshallerProvider.class.getName() + ".UNMARSHALLER");
    }
}
