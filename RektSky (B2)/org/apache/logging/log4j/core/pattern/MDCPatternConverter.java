package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.*;
import java.util.*;

@Plugin(name = "MDCPatternConverter", category = "Converter")
@ConverterKeys({ "X", "mdc", "MDC" })
public final class MDCPatternConverter extends LogEventPatternConverter
{
    private final String key;
    
    private MDCPatternConverter(final String[] options) {
        super((options != null && options.length > 0) ? ("MDC{" + options[0] + "}") : "MDC", "mdc");
        this.key = ((options != null && options.length > 0) ? options[0] : null);
    }
    
    public static MDCPatternConverter newInstance(final String[] options) {
        return new MDCPatternConverter(options);
    }
    
    @Override
    public void format(final LogEvent event, final StringBuilder toAppendTo) {
        final Map<String, String> contextMap = event.getContextMap();
        if (this.key == null) {
            if (contextMap == null || contextMap.size() == 0) {
                toAppendTo.append("{}");
                return;
            }
            final StringBuilder sb = new StringBuilder("{");
            final Set<String> keys = new TreeSet<String>(contextMap.keySet());
            for (final String key : keys) {
                if (sb.length() > 1) {
                    sb.append(", ");
                }
                sb.append(key).append("=").append(contextMap.get(key));
            }
            sb.append("}");
            toAppendTo.append((CharSequence)sb);
        }
        else if (contextMap != null) {
            final Object val = contextMap.get(this.key);
            if (val != null) {
                toAppendTo.append(val);
            }
        }
    }
}
