package io.netty.util.concurrent;

import java.util.*;

public interface GenericFutureListener<F extends Future<?>> extends EventListener
{
    void operationComplete(final F p0) throws Exception;
}
