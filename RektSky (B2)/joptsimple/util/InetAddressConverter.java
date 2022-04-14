package joptsimple.util;

import joptsimple.*;
import java.net.*;
import java.util.*;
import joptsimple.internal.*;

public class InetAddressConverter implements ValueConverter<InetAddress>
{
    public InetAddress convert(final String value) {
        try {
            return InetAddress.getByName(value);
        }
        catch (UnknownHostException e) {
            throw new ValueConversionException(this.message(value));
        }
    }
    
    public Class<InetAddress> valueType() {
        return InetAddress.class;
    }
    
    public String valuePattern() {
        return null;
    }
    
    private String message(final String value) {
        return Messages.message(Locale.getDefault(), "joptsimple.ExceptionMessages", InetAddressConverter.class, "message", value);
    }
}
