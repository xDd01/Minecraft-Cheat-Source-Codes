package io.netty.handler.codec.compression;

import io.netty.handler.codec.*;

public abstract class ZlibDecoder extends ByteToMessageDecoder
{
    public abstract boolean isClosed();
}
