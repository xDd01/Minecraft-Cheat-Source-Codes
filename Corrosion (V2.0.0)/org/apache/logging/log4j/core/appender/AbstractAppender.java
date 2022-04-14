/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.appender;

import java.io.Serializable;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.ErrorHandler;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.DefaultErrorHandler;
import org.apache.logging.log4j.core.filter.AbstractFilterable;
import org.apache.logging.log4j.core.helpers.Integers;

public abstract class AbstractAppender
extends AbstractFilterable
implements Appender {
    private final boolean ignoreExceptions;
    private ErrorHandler handler = new DefaultErrorHandler(this);
    private final Layout<? extends Serializable> layout;
    private final String name;
    private boolean started = false;

    public static int parseInt(String s2, int defaultValue) {
        try {
            return Integers.parseInt(s2, defaultValue);
        }
        catch (NumberFormatException e2) {
            LOGGER.error("Could not parse \"{}\" as an integer,  using default value {}: {}", s2, defaultValue, e2);
            return defaultValue;
        }
    }

    protected AbstractAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
        this(name, filter, layout, true);
    }

    protected AbstractAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(filter);
        this.name = name;
        this.layout = layout;
        this.ignoreExceptions = ignoreExceptions;
    }

    public void error(String msg) {
        this.handler.error(msg);
    }

    public void error(String msg, LogEvent event, Throwable t2) {
        this.handler.error(msg, event, t2);
    }

    public void error(String msg, Throwable t2) {
        this.handler.error(msg, t2);
    }

    @Override
    public ErrorHandler getHandler() {
        return this.handler;
    }

    @Override
    public Layout<? extends Serializable> getLayout() {
        return this.layout;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean ignoreExceptions() {
        return this.ignoreExceptions;
    }

    @Override
    public boolean isStarted() {
        return this.started;
    }

    @Override
    public void setHandler(ErrorHandler handler) {
        if (handler == null) {
            LOGGER.error("The handler cannot be set to null");
        }
        if (this.isStarted()) {
            LOGGER.error("The handler cannot be changed once the appender is started");
            return;
        }
        this.handler = handler;
    }

    @Override
    public void start() {
        this.startFilter();
        this.started = true;
    }

    @Override
    public void stop() {
        this.started = false;
        this.stopFilter();
    }

    public String toString() {
        return this.name;
    }
}

