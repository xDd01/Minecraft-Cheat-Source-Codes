package io.netty.channel.socket.nio;

import io.netty.channel.nio.*;
import java.nio.channels.spi.*;
import java.io.*;
import java.util.*;
import java.nio.channels.*;
import io.netty.channel.*;
import io.netty.util.internal.logging.*;
import io.netty.channel.socket.*;
import java.net.*;

public class NioServerSocketChannel extends AbstractNioMessageChannel implements ServerSocketChannel
{
    private static final ChannelMetadata METADATA;
    private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER;
    private static final InternalLogger logger;
    private final ServerSocketChannelConfig config;
    
    private static java.nio.channels.ServerSocketChannel newSocket(final SelectorProvider provider) {
        try {
            return provider.openServerSocketChannel();
        }
        catch (IOException e) {
            throw new ChannelException("Failed to open a server socket.", e);
        }
    }
    
    public NioServerSocketChannel() {
        this(newSocket(NioServerSocketChannel.DEFAULT_SELECTOR_PROVIDER));
    }
    
    public NioServerSocketChannel(final SelectorProvider provider) {
        this(newSocket(provider));
    }
    
    public NioServerSocketChannel(final java.nio.channels.ServerSocketChannel channel) {
        super(null, channel, 16);
        this.config = new NioServerSocketChannelConfig(this, this.javaChannel().socket());
    }
    
    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }
    
    @Override
    public ChannelMetadata metadata() {
        return NioServerSocketChannel.METADATA;
    }
    
    @Override
    public ServerSocketChannelConfig config() {
        return this.config;
    }
    
    @Override
    public boolean isActive() {
        return this.javaChannel().socket().isBound();
    }
    
    @Override
    public InetSocketAddress remoteAddress() {
        return null;
    }
    
    @Override
    protected java.nio.channels.ServerSocketChannel javaChannel() {
        return (java.nio.channels.ServerSocketChannel)super.javaChannel();
    }
    
    @Override
    protected SocketAddress localAddress0() {
        return this.javaChannel().socket().getLocalSocketAddress();
    }
    
    @Override
    protected void doBind(final SocketAddress localAddress) throws Exception {
        this.javaChannel().socket().bind(localAddress, this.config.getBacklog());
    }
    
    @Override
    protected void doClose() throws Exception {
        this.javaChannel().close();
    }
    
    @Override
    protected int doReadMessages(final List<Object> buf) throws Exception {
        final SocketChannel ch = this.javaChannel().accept();
        try {
            if (ch != null) {
                buf.add(new NioSocketChannel(this, ch));
                return 1;
            }
        }
        catch (Throwable t) {
            NioServerSocketChannel.logger.warn("Failed to create a new channel from an accepted socket.", t);
            try {
                ch.close();
            }
            catch (Throwable t2) {
                NioServerSocketChannel.logger.warn("Failed to close a socket.", t2);
            }
        }
        return 0;
    }
    
    @Override
    protected boolean doConnect(final SocketAddress remoteAddress, final SocketAddress localAddress) throws Exception {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected void doFinishConnect() throws Exception {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected SocketAddress remoteAddress0() {
        return null;
    }
    
    @Override
    protected void doDisconnect() throws Exception {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected boolean doWriteMessage(final Object msg, final ChannelOutboundBuffer in) throws Exception {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected final Object filterOutboundMessage(final Object msg) throws Exception {
        throw new UnsupportedOperationException();
    }
    
    static {
        METADATA = new ChannelMetadata(false);
        DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();
        logger = InternalLoggerFactory.getInstance(NioServerSocketChannel.class);
    }
    
    private final class NioServerSocketChannelConfig extends DefaultServerSocketChannelConfig
    {
        private NioServerSocketChannelConfig(final NioServerSocketChannel channel, final ServerSocket javaSocket) {
            super(channel, javaSocket);
        }
        
        @Override
        protected void autoReadCleared() {
            AbstractNioChannel.this.setReadPending(false);
        }
    }
}
