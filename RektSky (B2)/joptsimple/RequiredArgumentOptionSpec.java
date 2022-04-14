package joptsimple;

import java.util.*;

class RequiredArgumentOptionSpec<V> extends ArgumentAcceptingOptionSpec<V>
{
    RequiredArgumentOptionSpec(final String option) {
        super(option, true);
    }
    
    RequiredArgumentOptionSpec(final List<String> options, final String description) {
        super(options, true, description);
    }
    
    @Override
    protected void detectOptionArgument(final OptionParser parser, final ArgumentList arguments, final OptionSet detectedOptions) {
        if (!arguments.hasMore()) {
            throw new OptionMissingRequiredArgumentException(this);
        }
        this.addArguments(detectedOptions, arguments.next());
    }
}
