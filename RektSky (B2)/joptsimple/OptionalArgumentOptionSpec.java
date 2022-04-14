package joptsimple;

import java.util.*;

class OptionalArgumentOptionSpec<V> extends ArgumentAcceptingOptionSpec<V>
{
    OptionalArgumentOptionSpec(final String option) {
        super(option, false);
    }
    
    OptionalArgumentOptionSpec(final List<String> options, final String description) {
        super(options, false, description);
    }
    
    @Override
    protected void detectOptionArgument(final OptionParser parser, final ArgumentList arguments, final OptionSet detectedOptions) {
        if (arguments.hasMore()) {
            final String nextArgument = arguments.peek();
            if (!parser.looksLikeAnOption(nextArgument) && this.canConvertArgument(nextArgument)) {
                this.handleOptionArgument(parser, detectedOptions, arguments);
            }
            else if (this.isArgumentOfNumberType() && this.canConvertArgument(nextArgument)) {
                this.addArguments(detectedOptions, arguments.next());
            }
            else {
                detectedOptions.add(this);
            }
        }
        else {
            detectedOptions.add(this);
        }
    }
    
    private void handleOptionArgument(final OptionParser parser, final OptionSet detectedOptions, final ArgumentList arguments) {
        if (parser.posixlyCorrect()) {
            detectedOptions.add(this);
            parser.noMoreOptions();
        }
        else {
            this.addArguments(detectedOptions, arguments.next());
        }
    }
}
