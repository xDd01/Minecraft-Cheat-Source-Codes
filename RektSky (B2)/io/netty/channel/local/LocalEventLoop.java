package io.netty.channel.local;

import java.util.concurrent.*;
import io.netty.channel.*;

final class LocalEventLoop extends SingleThreadEventLoop
{
    LocalEventLoop(final LocalEventLoopGroup parent, final ThreadFactory threadFactory) {
        super(parent, threadFactory, true);
    }
    
    @Override
    protected void run() {
        do {
            final Runnable task = this.takeTask();
            if (task != null) {
                task.run();
                this.updateLastExecutionTime();
            }
        } while (!this.confirmShutdown());
    }
}
