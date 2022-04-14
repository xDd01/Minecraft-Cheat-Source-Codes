package io.netty.handler.codec.marshalling;

import io.netty.channel.*;
import org.jboss.marshalling.*;

public interface MarshallerProvider
{
    Marshaller getMarshaller(final ChannelHandlerContext p0) throws Exception;
}
