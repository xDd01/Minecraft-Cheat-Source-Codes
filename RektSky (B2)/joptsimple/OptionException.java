package joptsimple;

import java.util.*;
import joptsimple.internal.*;

public abstract class OptionException extends RuntimeException
{
    private static final long serialVersionUID = -1L;
    private final List<String> options;
    
    protected OptionException(final List<String> options) {
        (this.options = new ArrayList<String>()).addAll(options);
    }
    
    protected OptionException(final Collection<? extends OptionSpec<?>> options) {
        (this.options = new ArrayList<String>()).addAll(this.specsToStrings(options));
    }
    
    protected OptionException(final Collection<? extends OptionSpec<?>> options, final Throwable cause) {
        super(cause);
        (this.options = new ArrayList<String>()).addAll(this.specsToStrings(options));
    }
    
    private List<String> specsToStrings(final Collection<? extends OptionSpec<?>> options) {
        final List<String> strings = new ArrayList<String>();
        for (final OptionSpec<?> each : options) {
            strings.add(this.specToString(each));
        }
        return strings;
    }
    
    private String specToString(final OptionSpec<?> option) {
        return Strings.join(new ArrayList<String>(option.options()), "/");
    }
    
    public List<String> options() {
        return Collections.unmodifiableList((List<? extends String>)this.options);
    }
    
    protected final String singleOptionString() {
        return this.singleOptionString(this.options.get(0));
    }
    
    protected final String singleOptionString(final String option) {
        return option;
    }
    
    protected final String multipleOptionString() {
        final StringBuilder buffer = new StringBuilder("[");
        final Set<String> asSet = new LinkedHashSet<String>(this.options);
        final Iterator<String> iter = asSet.iterator();
        while (iter.hasNext()) {
            buffer.append(this.singleOptionString(iter.next()));
            if (iter.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append(']');
        return buffer.toString();
    }
    
    static OptionException unrecognizedOption(final String option) {
        return new UnrecognizedOptionException(option);
    }
    
    @Override
    public final String getMessage() {
        return this.localizedMessage(Locale.getDefault());
    }
    
    final String localizedMessage(final Locale locale) {
        return this.formattedMessage(locale);
    }
    
    private String formattedMessage(final Locale locale) {
        return Messages.message(locale, "joptsimple.ExceptionMessages", this.getClass(), "message", this.messageArguments());
    }
    
    abstract Object[] messageArguments();
}
