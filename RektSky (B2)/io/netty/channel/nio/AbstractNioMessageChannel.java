package io.netty.channel.nio;

import java.io.*;
import java.nio.channels.*;
import java.util.*;
import io.netty.channel.*;

public abstract class AbstractNioMessageChannel extends AbstractNioChannel
{
    protected AbstractNioMessageChannel(final Channel parent, final SelectableChannel ch, final int readInterestOp) {
        super(parent, ch, readInterestOp);
    }
    
    @Override
    protected AbstractNioUnsafe newUnsafe() {
        return new NioMessageUnsafe();
    }
    
    @Override
    protected void doWrite(final ChannelOutboundBuffer in) throws Exception {
        final SelectionKey key = this.selectionKey();
        final int interestOps = key.interestOps();
        while (true) {
            final Object msg = in.current();
            if (msg == null) {
                if ((interestOps & 0x4) != 0x0) {
                    key.interestOps(interestOps & 0xFFFFFFFB);
                    break;
                }
                break;
            }
            else {
                try {
                    boolean done = false;
                    for (int i = this.config().getWriteSpinCount() - 1; i >= 0; --i) {
                        if (this.doWriteMessage(msg, in)) {
                            done = true;
                            break;
                        }
                    }
                    if (!done) {
                        if ((interestOps & 0x4) == 0x0) {
                            key.interestOps(interestOps | 0x4);
                        }
                        break;
                    }
                    in.remove();
                }
                catch (IOException e) {
                    if (!this.continueOnWriteError()) {
                        throw e;
                    }
                    in.remove(e);
                }
            }
        }
    }
    
    protected boolean continueOnWriteError() {
        return false;
    }
    
    protected abstract int doReadMessages(final List<Object> p0) throws Exception;
    
    protected abstract boolean doWriteMessage(final Object p0, final ChannelOutboundBuffer p1) throws Exception;
    
    private final class NioMessageUnsafe extends AbstractNioUnsafe
    {
        private final List<Object> readBuf;
        
        private NioMessageUnsafe() {
            this.readBuf = new ArrayList<Object>();
        }
        
        @Override
        public void read() {
            assert AbstractNioMessageChannel.this.eventLoop().inEventLoop();
            final ChannelConfig config = AbstractNioMessageChannel.this.config();
            if (!config.isAutoRead() && !AbstractNioMessageChannel.this.isReadPending()) {
                this.removeReadOp();
                return;
            }
            final int maxMessagesPerRead = config.getMaxMessagesPerRead();
            final ChannelPipeline pipeline = AbstractNioMessageChannel.this.pipeline();
            boolean closed = false;
            Throwable exception = null;
            try {
                try {
                    do {
                        final int localRead = AbstractNioMessageChannel.this.doReadMessages(this.readBuf);
                        if (localRead == 0) {
                            break;
                        }
                        if (localRead < 0) {
                            closed = true;
                            break;
                        }
                        if (!config.isAutoRead()) {
                            break;
                        }
                    } while (this.readBuf.size() < maxMessagesPerRead);
                }
                catch (Throwable t) {
                    exception = t;
                }
                AbstractNioMessageChannel.this.setReadPending(false);
                for (int size = this.readBuf.size(), i = 0; i < size; ++i) {
                    pipeline.fireChannelRead(this.readBuf.get(i));
                }
                this.readBuf.clear();
                pipeline.fireChannelReadComplete();
                if (exception != null) {
                    if (exception instanceof IOException) {
                        closed = !(AbstractNioMessageChannel.this instanceof ServerChannel);
                    }
                    pipeline.fireExceptionCaught(exception);
                }
                if (closed && AbstractNioMessageChannel.this.isOpen()) {
                    this.close(this.voidPromise());
                }
            }
            finally {
                if (!config.isAutoRead() && !AbstractNioMessageChannel.this.isReadPending()) {
                    this.removeReadOp();
                }
            }
        }
    }
}
