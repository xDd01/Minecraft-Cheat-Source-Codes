/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.pattern.FormattingInfo;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

public class PatternFormatter {
    private final LogEventPatternConverter converter;
    private final FormattingInfo field;

    public PatternFormatter(LogEventPatternConverter converter, FormattingInfo field) {
        this.converter = converter;
        this.field = field;
    }

    public void format(LogEvent event, StringBuilder buf) {
        int startField = buf.length();
        this.converter.format(event, buf);
        this.field.format(startField, buf);
    }

    public LogEventPatternConverter getConverter() {
        return this.converter;
    }

    public FormattingInfo getFormattingInfo() {
        return this.field;
    }

    public boolean handlesThrowable() {
        return this.converter.handlesThrowable();
    }

    public String toString() {
        StringBuilder sb2 = new StringBuilder();
        sb2.append(super.toString());
        sb2.append("[converter=");
        sb2.append(this.converter);
        sb2.append(", field=");
        sb2.append(this.field);
        sb2.append("]");
        return sb2.toString();
    }
}

