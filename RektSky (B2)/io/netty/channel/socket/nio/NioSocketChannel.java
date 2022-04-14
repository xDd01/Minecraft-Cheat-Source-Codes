package io.netty.channel.socket.nio;

import io.netty.channel.nio.*;
import java.nio.channels.spi.*;
import java.io.*;
import io.netty.util.internal.*;
import io.netty.buffer.*;
import java.nio.channels.*;
import java.nio.*;
import io.netty.channel.*;
import io.netty.channel.socket.*;
import java.net.*;

public class NioSocketChannel extends AbstractNioByteChannel implements SocketChannel
{
    private static final ChannelMetadata METADATA;
    private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER;
    private final SocketChannelConfig config;
    
    private static java.nio.channels.SocketChannel newSocket(final SelectorProvider provider) {
        try {
            return provider.openSocketChannel();
        }
        catch (IOException e) {
            throw new ChannelException("Failed to open a socket.", e);
        }
    }
    
    public NioSocketChannel() {
        this(newSocket(NioSocketChannel.DEFAULT_SELECTOR_PROVIDER));
    }
    
    public NioSocketChannel(final SelectorProvider provider) {
        this(newSocket(provider));
    }
    
    public NioSocketChannel(final java.nio.channels.SocketChannel socket) {
        this(null, socket);
    }
    
    public NioSocketChannel(final Channel parent, final java.nio.channels.SocketChannel socket) {
        super(parent, socket);
        this.config = new NioSocketChannelConfig(this, socket.socket());
    }
    
    @Override
    public ServerSocketChannel parent() {
        return (ServerSocketChannel)super.parent();
    }
    
    @Override
    public ChannelMetadata metadata() {
        return NioSocketChannel.METADATA;
    }
    
    @Override
    public SocketChannelConfig config() {
        return this.config;
    }
    
    @Override
    protected java.nio.channels.SocketChannel javaChannel() {
        return (java.nio.channels.SocketChannel)super.javaChannel();
    }
    
    @Override
    public boolean isActive() {
        final java.nio.channels.SocketChannel ch = this.javaChannel();
        return ch.isOpen() && ch.isConnected();
    }
    
    @Override
    public boolean isInputShutdown() {
        return super.isInputShutdown();
    }
    
    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }
    
    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress)super.remoteAddress();
    }
    
    @Override
    public boolean isOutputShutdown() {
        return this.javaChannel().socket().isOutputShutdown() || !this.isActive();
    }
    
    @Override
    public ChannelFuture shutdownOutput() {
        return this.shutdownOutput(this.newPromise());
    }
    
    @Override
    public ChannelFuture shutdownOutput(final ChannelPromise promise) {
        final EventLoop loop = this.eventLoop();
        if (loop.inEventLoop()) {
            try {
                this.javaChannel().socket().shutdownOutput();
                promise.setSuccess();
            }
            catch (Throwable t) {
                promise.setFailure(t);
            }
        }
        else {
            loop.execute(new OneTimeTask() {
                @Override
                public void run() {
                    NioSocketChannel.this.shutdownOutput(promise);
                }
            });
        }
        return promise;
    }
    
    @Override
    protected SocketAddress localAddress0() {
        return this.javaChannel().socket().getLocalSocketAddress();
    }
    
    @Override
    protected SocketAddress remoteAddress0() {
        return this.javaChannel().socket().getRemoteSocketAddress();
    }
    
    @Override
    protected void doBind(final SocketAddress localAddress) throws Exception {
        this.javaChannel().socket().bind(localAddress);
    }
    
    @Override
    protected boolean doConnect(final SocketAddress remoteAddress, final SocketAddress localAddress) throws Exception {
        if (localAddress != null) {
            this.javaChannel().socket().bind(localAddress);
        }
        boolean success = false;
        try {
            final boolean connected = this.javaChannel().connect(remoteAddress);
            if (!connected) {
                this.selectionKey().interestOps(8);
            }
            success = true;
            return connected;
        }
        finally {
            if (!success) {
                this.doClose();
            }
        }
    }
    
    @Override
    protected void doFinishConnect() throws Exception {
        if (!this.javaChannel().finishConnect()) {
            throw new Error();
        }
    }
    
    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }
    
    @Override
    protected void doClose() throws Exception {
        this.javaChannel().close();
    }
    
    @Override
    protected int doReadBytes(final ByteBuf byteBuf) throws Exception {
        return byteBuf.writeBytes(this.javaChannel(), byteBuf.writableBytes());
    }
    
    @Override
    protected int doWriteBytes(final ByteBuf buf) throws Exception {
        final int expectedWrittenBytes = buf.readableBytes();
        return buf.readBytes(this.javaChannel(), expectedWrittenBytes);
    }
    
    @Override
    protected long doWriteFileRegion(final FileRegion region) throws Exception {
        final long position = region.transfered();
        return region.transferTo(this.javaChannel(), position);
    }
    
    @Override
    protected void doWrite(final ChannelOutboundBuffer in) throws Exception {
        while (true) {
            final int size = in.size();
            if (size == 0) {
                this.clearOpWrite();
                break;
            }
            long writtenBytes = 0L;
            boolean done = false;
            boolean setOpWrite = false;
            final ByteBuffer[] nioBuffers = in.nioBuffers();
            final int nioBufferCnt = in.nioBufferCount();
            long expectedWrittenBytes = in.nioBufferSize();
            final java.nio.channels.SocketChannel ch = this.javaChannel();
            switch (nioBufferCnt) {
                case 0: {
                    super.doWrite(in);
                    return;
                }
                case 1: {
                    final ByteBuffer nioBuffer = nioBuffers[0];
                    for (int i = this.config().getWriteSpinCount() - 1; i >= 0; --i) {
                        final int localWrittenBytes = ch.write(nioBuffer);
                        if (localWrittenBytes == 0) {
                            setOpWrite = true;
                            break;
                        }
                        expectedWrittenBytes -= localWrittenBytes;
                        writtenBytes += localWrittenBytes;
                        if (expectedWrittenBytes == 0L) {
                            done = true;
                            break;
                        }
                    }
                    break;
                }
                default: {
                    for (int i = this.config().getWriteSpinCount() - 1; i >= 0; --i) {
                        final long localWrittenBytes2 = ch.write(nioBuffers, 0, nioBufferCnt);
                        if (localWrittenBytes2 == 0L) {
                            setOpWrite = true;
                            break;
                        }
                        expectedWrittenBytes -= localWrittenBytes2;
                        writtenBytes += localWrittenBytes2;
                        if (expectedWrittenBytes == 0L) {
                            done = true;
                            break;
                        }
                    }
                    break;
                }
            }
            in.removeBytes(writtenBytes);
            if (!done) {
                this.incompleteWrite(setOpWrite);
                break;
            }
        }
    }
    
    static {
        METADATA = new ChannelMetadata(false);
        DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();
    }
    
    private final class NioSocketChannelConfig extends DefaultSocketChannelConfig
    {
        private NioSocketChannelConfig(final NioSocketChannel channel, final Socket javaSocket) {
            super(channel, javaSocket);
        }
        
        @Override
        protected void autoReadCleared() {
            AbstractNioChannel.this.setReadPending(false);
        }
    }
}
