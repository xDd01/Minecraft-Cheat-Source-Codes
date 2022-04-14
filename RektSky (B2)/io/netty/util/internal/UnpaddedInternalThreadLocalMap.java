package io.netty.util.internal;

import java.util.concurrent.atomic.*;
import java.util.*;
import java.nio.charset.*;

class UnpaddedInternalThreadLocalMap
{
    static ThreadLocal<InternalThreadLocalMap> slowThreadLocalMap;
    static final AtomicInteger nextIndex;
    Object[] indexedVariables;
    int futureListenerStackDepth;
    int localChannelReaderStackDepth;
    Map<Class<?>, Boolean> handlerSharableCache;
    IntegerHolder counterHashCode;
    ThreadLocalRandom random;
    Map<Class<?>, TypeParameterMatcher> typeParameterMatcherGetCache;
    Map<Class<?>, Map<String, TypeParameterMatcher>> typeParameterMatcherFindCache;
    StringBuilder stringBuilder;
    Map<Charset, CharsetEncoder> charsetEncoderCache;
    Map<Charset, CharsetDecoder> charsetDecoderCache;
    
    UnpaddedInternalThreadLocalMap(final Object[] indexedVariables) {
        this.indexedVariables = indexedVariables;
    }
    
    static {
        nextIndex = new AtomicInteger();
    }
}
