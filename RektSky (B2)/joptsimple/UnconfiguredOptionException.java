package joptsimple;

import java.util.*;

class UnconfiguredOptionException extends OptionException
{
    private static final long serialVersionUID = -1L;
    
    UnconfiguredOptionException(final String option) {
        this(Collections.singletonList(option));
    }
    
    UnconfiguredOptionException(final List<String> options) {
        super(options);
    }
    
    @Override
    Object[] messageArguments() {
        return new Object[] { this.multipleOptionString() };
    }
}
