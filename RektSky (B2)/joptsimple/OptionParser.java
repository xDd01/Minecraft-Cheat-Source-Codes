package joptsimple;

import joptsimple.internal.*;
import java.io.*;
import joptsimple.util.*;
import java.util.*;

public class OptionParser implements OptionDeclarer
{
    private final AbbreviationMap<AbstractOptionSpec<?>> recognizedOptions;
    private final ArrayList<AbstractOptionSpec<?>> trainingOrder;
    private final Map<List<String>, Set<OptionSpec<?>>> requiredIf;
    private final Map<List<String>, Set<OptionSpec<?>>> requiredUnless;
    private OptionParserState state;
    private boolean posixlyCorrect;
    private boolean allowsUnrecognizedOptions;
    private HelpFormatter helpFormatter;
    
    public OptionParser() {
        this.helpFormatter = new BuiltinHelpFormatter();
        this.recognizedOptions = new AbbreviationMap<AbstractOptionSpec<?>>();
        this.trainingOrder = new ArrayList<AbstractOptionSpec<?>>();
        this.requiredIf = new HashMap<List<String>, Set<OptionSpec<?>>>();
        this.requiredUnless = new HashMap<List<String>, Set<OptionSpec<?>>>();
        this.state = OptionParserState.moreOptions(false);
        this.recognize(new NonOptionArgumentSpec<Object>());
    }
    
    public OptionParser(final String optionSpecification) {
        this();
        new OptionSpecTokenizer(optionSpecification).configure(this);
    }
    
    public OptionSpecBuilder accepts(final String option) {
        return this.acceptsAll(Collections.singletonList(option));
    }
    
    public OptionSpecBuilder accepts(final String option, final String description) {
        return this.acceptsAll(Collections.singletonList(option), description);
    }
    
    public OptionSpecBuilder acceptsAll(final List<String> options) {
        return this.acceptsAll(options, "");
    }
    
    public OptionSpecBuilder acceptsAll(final List<String> options, final String description) {
        if (options.isEmpty()) {
            throw new IllegalArgumentException("need at least one option");
        }
        ParserRules.ensureLegalOptions(options);
        return new OptionSpecBuilder(this, options, description);
    }
    
    public NonOptionArgumentSpec<String> nonOptions() {
        final NonOptionArgumentSpec<String> spec = new NonOptionArgumentSpec<String>();
        this.recognize(spec);
        return spec;
    }
    
    public NonOptionArgumentSpec<String> nonOptions(final String description) {
        final NonOptionArgumentSpec<String> spec = new NonOptionArgumentSpec<String>(description);
        this.recognize(spec);
        return spec;
    }
    
    public void posixlyCorrect(final boolean setting) {
        this.posixlyCorrect = setting;
        this.state = OptionParserState.moreOptions(setting);
    }
    
    boolean posixlyCorrect() {
        return this.posixlyCorrect;
    }
    
    public void allowsUnrecognizedOptions() {
        this.allowsUnrecognizedOptions = true;
    }
    
    boolean doesAllowsUnrecognizedOptions() {
        return this.allowsUnrecognizedOptions;
    }
    
    public void recognizeAlternativeLongOptions(final boolean recognize) {
        if (recognize) {
            this.recognize(new AlternativeLongOptionSpec());
        }
        else {
            this.recognizedOptions.remove(String.valueOf("W"));
        }
    }
    
    void recognize(final AbstractOptionSpec<?> spec) {
        this.recognizedOptions.putAll(spec.options(), spec);
        this.trainingOrder.add(spec);
    }
    
    public void printHelpOn(final OutputStream sink) throws IOException {
        this.printHelpOn(new OutputStreamWriter(sink));
    }
    
    public void printHelpOn(final Writer sink) throws IOException {
        sink.write(this.helpFormatter.format(this._recognizedOptions()));
        sink.flush();
    }
    
    public void formatHelpWith(final HelpFormatter formatter) {
        if (formatter == null) {
            throw new NullPointerException();
        }
        this.helpFormatter = formatter;
    }
    
    public Map<String, OptionSpec<?>> recognizedOptions() {
        return new LinkedHashMap<String, OptionSpec<?>>(this._recognizedOptions());
    }
    
    private Map<String, AbstractOptionSpec<?>> _recognizedOptions() {
        final Map<String, AbstractOptionSpec<?>> options = new LinkedHashMap<String, AbstractOptionSpec<?>>();
        for (final AbstractOptionSpec<?> spec : this.trainingOrder) {
            for (final String option : spec.options()) {
                options.put(option, spec);
            }
        }
        return options;
    }
    
    public OptionSet parse(final String... arguments) {
        final ArgumentList argumentList = new ArgumentList(arguments);
        final OptionSet detected = new OptionSet(this.recognizedOptions.toJavaUtilMap());
        detected.add(this.recognizedOptions.get("[arguments]"));
        while (argumentList.hasMore()) {
            this.state.handleArgument(this, argumentList, detected);
        }
        this.reset();
        this.ensureRequiredOptions(detected);
        return detected;
    }
    
    private void ensureRequiredOptions(final OptionSet options) {
        final List<AbstractOptionSpec<?>> missingRequiredOptions = this.missingRequiredOptions(options);
        final boolean helpOptionPresent = this.isHelpOptionPresent(options);
        if (!missingRequiredOptions.isEmpty() && !helpOptionPresent) {
            throw new MissingRequiredOptionsException(missingRequiredOptions);
        }
    }
    
    private List<AbstractOptionSpec<?>> missingRequiredOptions(final OptionSet options) {
        final List<AbstractOptionSpec<?>> missingRequiredOptions = new ArrayList<AbstractOptionSpec<?>>();
        for (final AbstractOptionSpec<?> each : this.recognizedOptions.toJavaUtilMap().values()) {
            if (each.isRequired() && !options.has(each)) {
                missingRequiredOptions.add(each);
            }
        }
        for (final Map.Entry<List<String>, Set<OptionSpec<?>>> eachEntry : this.requiredIf.entrySet()) {
            final AbstractOptionSpec<?> required = this.specFor(eachEntry.getKey().iterator().next());
            if (this.optionsHasAnyOf(options, eachEntry.getValue()) && !options.has(required)) {
                missingRequiredOptions.add(required);
            }
        }
        for (final Map.Entry<List<String>, Set<OptionSpec<?>>> eachEntry : this.requiredUnless.entrySet()) {
            final AbstractOptionSpec<?> required = this.specFor(eachEntry.getKey().iterator().next());
            if (!this.optionsHasAnyOf(options, eachEntry.getValue()) && !options.has(required)) {
                missingRequiredOptions.add(required);
            }
        }
        return missingRequiredOptions;
    }
    
    private boolean optionsHasAnyOf(final OptionSet options, final Collection<OptionSpec<?>> specs) {
        for (final OptionSpec<?> each : specs) {
            if (options.has(each)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isHelpOptionPresent(final OptionSet options) {
        boolean helpOptionPresent = false;
        for (final AbstractOptionSpec<?> each : this.recognizedOptions.toJavaUtilMap().values()) {
            if (each.isForHelp() && options.has(each)) {
                helpOptionPresent = true;
                break;
            }
        }
        return helpOptionPresent;
    }
    
    void handleLongOptionToken(final String candidate, final ArgumentList arguments, final OptionSet detected) {
        final KeyValuePair optionAndArgument = parseLongOptionWithArgument(candidate);
        if (!this.isRecognized(optionAndArgument.key)) {
            throw OptionException.unrecognizedOption(optionAndArgument.key);
        }
        final AbstractOptionSpec<?> optionSpec = this.specFor(optionAndArgument.key);
        optionSpec.handleOption(this, arguments, detected, optionAndArgument.value);
    }
    
    void handleShortOptionToken(final String candidate, final ArgumentList arguments, final OptionSet detected) {
        final KeyValuePair optionAndArgument = parseShortOptionWithArgument(candidate);
        if (this.isRecognized(optionAndArgument.key)) {
            this.specFor(optionAndArgument.key).handleOption(this, arguments, detected, optionAndArgument.value);
        }
        else {
            this.handleShortOptionCluster(candidate, arguments, detected);
        }
    }
    
    private void handleShortOptionCluster(final String candidate, final ArgumentList arguments, final OptionSet detected) {
        final char[] options = extractShortOptionsFrom(candidate);
        this.validateOptionCharacters(options);
        for (int i = 0; i < options.length; ++i) {
            final AbstractOptionSpec<?> optionSpec = this.specFor(options[i]);
            if (optionSpec.acceptsArguments() && options.length > i + 1) {
                final String detectedArgument = String.valueOf(options, i + 1, options.length - 1 - i);
                optionSpec.handleOption(this, arguments, detected, detectedArgument);
                break;
            }
            optionSpec.handleOption(this, arguments, detected, null);
        }
    }
    
    void handleNonOptionArgument(final String candidate, final ArgumentList arguments, final OptionSet detectedOptions) {
        this.specFor("[arguments]").handleOption(this, arguments, detectedOptions, candidate);
    }
    
    void noMoreOptions() {
        this.state = OptionParserState.noMoreOptions();
    }
    
    boolean looksLikeAnOption(final String argument) {
        return ParserRules.isShortOptionToken(argument) || ParserRules.isLongOptionToken(argument);
    }
    
    boolean isRecognized(final String option) {
        return this.recognizedOptions.contains(option);
    }
    
    void requiredIf(final List<String> precedentSynonyms, final String required) {
        this.requiredIf(precedentSynonyms, this.specFor(required));
    }
    
    void requiredIf(final List<String> precedentSynonyms, final OptionSpec<?> required) {
        this.putRequiredOption(precedentSynonyms, required, this.requiredIf);
    }
    
    void requiredUnless(final List<String> precedentSynonyms, final String required) {
        this.requiredUnless(precedentSynonyms, this.specFor(required));
    }
    
    void requiredUnless(final List<String> precedentSynonyms, final OptionSpec<?> required) {
        this.putRequiredOption(precedentSynonyms, required, this.requiredUnless);
    }
    
    private void putRequiredOption(final List<String> precedentSynonyms, final OptionSpec<?> required, final Map<List<String>, Set<OptionSpec<?>>> target) {
        for (final String each : precedentSynonyms) {
            final AbstractOptionSpec<?> spec = this.specFor(each);
            if (spec == null) {
                throw new UnconfiguredOptionException(precedentSynonyms);
            }
        }
        Set<OptionSpec<?>> associated = target.get(precedentSynonyms);
        if (associated == null) {
            associated = new HashSet<OptionSpec<?>>();
            target.put(precedentSynonyms, associated);
        }
        associated.add(required);
    }
    
    private AbstractOptionSpec<?> specFor(final char option) {
        return this.specFor(String.valueOf(option));
    }
    
    private AbstractOptionSpec<?> specFor(final String option) {
        return this.recognizedOptions.get(option);
    }
    
    private void reset() {
        this.state = OptionParserState.moreOptions(this.posixlyCorrect);
    }
    
    private static char[] extractShortOptionsFrom(final String argument) {
        final char[] options = new char[argument.length() - 1];
        argument.getChars(1, argument.length(), options, 0);
        return options;
    }
    
    private void validateOptionCharacters(final char[] options) {
        for (final char each : options) {
            final String option = String.valueOf(each);
            if (!this.isRecognized(option)) {
                throw OptionException.unrecognizedOption(option);
            }
            if (this.specFor(option).acceptsArguments()) {
                return;
            }
        }
    }
    
    private static KeyValuePair parseLongOptionWithArgument(final String argument) {
        return KeyValuePair.valueOf(argument.substring(2));
    }
    
    private static KeyValuePair parseShortOptionWithArgument(final String argument) {
        return KeyValuePair.valueOf(argument.substring(1));
    }
}
