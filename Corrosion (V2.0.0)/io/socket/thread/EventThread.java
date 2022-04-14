/*
 * Decompiled with CFR 0.152.
 */
package io.socket.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventThread
extends Thread {
    private static final Logger logger = Logger.getLogger(EventThread.class.getName());
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory(){

        @Override
        public Thread newThread(Runnable runnable) {
            thread = new EventThread(runnable);
            thread.setName("EventThread");
            thread.setDaemon(Thread.currentThread().isDaemon());
            return thread;
        }
    };
    private static EventThread thread;
    private static ExecutorService service;
    private static int counter;

    private EventThread(Runnable runnable) {
        super(runnable);
    }

    public static boolean isCurrent() {
        return EventThread.currentThread() == thread;
    }

    public static void exec(Runnable task) {
        if (EventThread.isCurrent()) {
            task.run();
        } else {
            EventThread.nextTick(task);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void nextTick(final Runnable task) {
        Class<EventThread> clazz = EventThread.class;
        synchronized (EventThread.class) {
            ++counter;
            if (service == null) {
                service = Executors.newSingleThreadExecutor(THREAD_FACTORY);
            }
            ExecutorService executor = service;
            // ** MonitorExit[var2_1] (shouldn't be in output)
            executor.execute(new Runnable(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void run() {
                    try {
                        task.run();
                    }
                    catch (Throwable t2) {
                        logger.log(Level.SEVERE, "Task threw exception", t2);
                        throw t2;
                    }
                    finally {
                        Class<EventThread> clazz = EventThread.class;
                        synchronized (EventThread.class) {
                            counter--;
                            if (counter == 0) {
                                service.shutdown();
                                service = null;
                                thread = null;
                            }
                            // ** MonitorExit[var1_1] (shouldn't be in output)
                        }
                    }
                }
            });
            return;
        }
    }

    static {
        counter = 0;
    }
}

