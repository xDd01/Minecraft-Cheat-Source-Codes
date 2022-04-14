package joptsimple;

import java.util.*;

class UnrecognizedOptionException extends OptionException
{
    private static final long serialVersionUID = -1L;
    
    UnrecognizedOptionException(final String option) {
        super(Collections.singletonList(option));
    }
    
    @Override
    Object[] messageArguments() {
        return new Object[] { this.singleOptionString() };
    }
}
