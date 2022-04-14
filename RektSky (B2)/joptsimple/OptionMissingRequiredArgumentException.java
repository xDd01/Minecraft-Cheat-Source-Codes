package joptsimple;

import java.util.*;

class OptionMissingRequiredArgumentException extends OptionException
{
    private static final long serialVersionUID = -1L;
    
    OptionMissingRequiredArgumentException(final OptionSpec<?> option) {
        super((Collection<? extends OptionSpec<?>>)Arrays.asList(option));
    }
    
    @Override
    Object[] messageArguments() {
        return new Object[] { this.singleOptionString() };
    }
}
