package org.apache.logging.log4j.core.appender.db.jpa.converter;

import javax.persistence.*;
import org.apache.logging.log4j.core.helpers.*;
import org.apache.logging.log4j.*;

@Converter(autoApply = false)
public class MarkerAttributeConverter implements AttributeConverter<Marker, String>
{
    public String convertToDatabaseColumn(final Marker marker) {
        if (marker == null) {
            return null;
        }
        final StringBuilder builder = new StringBuilder(marker.getName());
        Marker parent = marker.getParent();
        int levels = 0;
        boolean hasParent = false;
        while (parent != null) {
            ++levels;
            hasParent = true;
            builder.append("[ ").append(parent.getName());
            parent = parent.getParent();
        }
        for (int i = 0; i < levels; ++i) {
            builder.append(" ]");
        }
        if (hasParent) {
            builder.append(" ]");
        }
        return builder.toString();
    }
    
    public Marker convertToEntityAttribute(final String s) {
        if (Strings.isEmpty(s)) {
            return null;
        }
        final int bracket = s.indexOf("[");
        return (bracket < 1) ? MarkerManager.getMarker(s) : MarkerManager.getMarker(s.substring(0, bracket));
    }
}
