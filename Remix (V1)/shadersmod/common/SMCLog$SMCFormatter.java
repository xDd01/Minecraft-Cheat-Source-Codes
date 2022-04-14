package shadersmod.common;

import java.util.*;
import java.util.logging.*;

private static class SMCFormatter extends Formatter
{
    int tzOffset;
    
    private SMCFormatter() {
        this.tzOffset = Calendar.getInstance().getTimeZone().getRawOffset();
    }
    
    @Override
    public String format(final LogRecord record) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("Shaders").append("]");
        if (record.getLevel() != SMCLog.SMCINFO) {
            sb.append("[").append(record.getLevel()).append("]");
        }
        sb.append(" ");
        sb.append(record.getMessage()).append("\n");
        return sb.toString();
    }
}
