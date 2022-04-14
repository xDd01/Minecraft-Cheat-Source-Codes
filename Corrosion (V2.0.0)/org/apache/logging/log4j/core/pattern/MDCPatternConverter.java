/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.pattern;

import java.util.Map;
import java.util.TreeSet;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

@Plugin(name="MDCPatternConverter", category="Converter")
@ConverterKeys(value={"X", "mdc", "MDC"})
public final class MDCPatternConverter
extends LogEventPatternConverter {
    private final String key;

    private MDCPatternConverter(String[] options) {
        super(options != null && options.length > 0 ? "MDC{" + options[0] + "}" : "MDC", "mdc");
        this.key = options != null && options.length > 0 ? options[0] : null;
    }

    public static MDCPatternConverter newInstance(String[] options) {
        return new MDCPatternConverter(options);
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        String val;
        Map<String, String> contextMap = event.getContextMap();
        if (this.key == null) {
            if (contextMap == null || contextMap.size() == 0) {
                toAppendTo.append("{}");
                return;
            }
            StringBuilder sb2 = new StringBuilder("{");
            TreeSet<String> keys = new TreeSet<String>(contextMap.keySet());
            for (String key : keys) {
                if (sb2.length() > 1) {
                    sb2.append(", ");
                }
                sb2.append(key).append("=").append(contextMap.get(key));
            }
            sb2.append("}");
            toAppendTo.append((CharSequence)sb2);
        } else if (contextMap != null && (val = contextMap.get(this.key)) != null) {
            toAppendTo.append((Object)val);
        }
    }
}

