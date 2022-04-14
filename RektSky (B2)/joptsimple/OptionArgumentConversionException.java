package joptsimple;

import java.util.*;

class OptionArgumentConversionException extends OptionException
{
    private static final long serialVersionUID = -1L;
    private final String argument;
    
    OptionArgumentConversionException(final OptionSpec<?> options, final String argument, final Throwable cause) {
        super(Collections.singleton(options), cause);
        this.argument = argument;
    }
    
    @Override
    Object[] messageArguments() {
        return new Object[] { this.argument, this.singleOptionString() };
    }
}
