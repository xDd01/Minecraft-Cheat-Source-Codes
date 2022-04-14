/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core;

public interface LifeCycle {
    public void start();

    public void stop();

    public boolean isStarted();
}

