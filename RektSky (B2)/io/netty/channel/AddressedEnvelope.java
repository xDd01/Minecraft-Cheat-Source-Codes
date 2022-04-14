package io.netty.channel;

import java.net.*;
import io.netty.util.*;

public interface AddressedEnvelope<M, A extends SocketAddress> extends ReferenceCounted
{
    M content();
    
    A sender();
    
    A recipient();
}
