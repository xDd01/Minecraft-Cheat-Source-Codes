/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.jmx;

import java.util.List;
import javax.management.ObjectName;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.helpers.Assert;
import org.apache.logging.log4j.core.jmx.LoggerConfigAdminMBean;
import org.apache.logging.log4j.core.jmx.Server;

public class LoggerConfigAdmin
implements LoggerConfigAdminMBean {
    private final String contextName;
    private final LoggerConfig loggerConfig;
    private final ObjectName objectName;

    public LoggerConfigAdmin(String contextName, LoggerConfig loggerConfig) {
        this.contextName = Assert.isNotNull(contextName, "contextName");
        this.loggerConfig = Assert.isNotNull(loggerConfig, "loggerConfig");
        try {
            String ctxName = Server.escape(this.contextName);
            String configName = Server.escape(loggerConfig.getName());
            String name = String.format("org.apache.logging.log4j2:type=LoggerContext,ctx=%s,sub=LoggerConfig,name=%s", ctxName, configName);
            this.objectName = new ObjectName(name);
        }
        catch (Exception e2) {
            throw new IllegalStateException(e2);
        }
    }

    public ObjectName getObjectName() {
        return this.objectName;
    }

    @Override
    public String getName() {
        return this.loggerConfig.getName();
    }

    @Override
    public String getLevel() {
        return this.loggerConfig.getLevel().name();
    }

    @Override
    public void setLevel(String level) {
        this.loggerConfig.setLevel(Level.valueOf(level));
    }

    @Override
    public boolean isAdditive() {
        return this.loggerConfig.isAdditive();
    }

    @Override
    public void setAdditive(boolean additive) {
        this.loggerConfig.setAdditive(additive);
    }

    @Override
    public boolean isIncludeLocation() {
        return this.loggerConfig.isIncludeLocation();
    }

    @Override
    public String getFilter() {
        return String.valueOf(this.loggerConfig.getFilter());
    }

    @Override
    public String[] getAppenderRefs() {
        List<AppenderRef> refs = this.loggerConfig.getAppenderRefs();
        String[] result = new String[refs.size()];
        for (int i2 = 0; i2 < result.length; ++i2) {
            result[i2] = refs.get(i2).getRef();
        }
        return result;
    }
}

