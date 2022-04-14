package joptsimple;

import java.util.*;
import joptsimple.internal.*;

public class BuiltinHelpFormatter implements HelpFormatter
{
    private final Rows nonOptionRows;
    private final Rows optionRows;
    
    BuiltinHelpFormatter() {
        this(80, 2);
    }
    
    public BuiltinHelpFormatter(final int desiredOverallWidth, final int desiredColumnSeparatorWidth) {
        this.nonOptionRows = new Rows(desiredOverallWidth * 2, 0);
        this.optionRows = new Rows(desiredOverallWidth, desiredColumnSeparatorWidth);
    }
    
    public String format(final Map<String, ? extends OptionDescriptor> options) {
        final Comparator<OptionDescriptor> comparator = new Comparator<OptionDescriptor>() {
            public int compare(final OptionDescriptor first, final OptionDescriptor second) {
                return first.options().iterator().next().compareTo((String)second.options().iterator().next());
            }
        };
        final Set<OptionDescriptor> sorted = new TreeSet<OptionDescriptor>(comparator);
        sorted.addAll(options.values());
        this.addRows(sorted);
        return this.formattedHelpOutput();
    }
    
    protected void addOptionRow(final String single) {
        this.addOptionRow(single, "");
    }
    
    protected void addOptionRow(final String left, final String right) {
        this.optionRows.add(left, right);
    }
    
    protected void addNonOptionRow(final String single) {
        this.nonOptionRows.add(single, "");
    }
    
    protected void fitRowsToWidth() {
        this.nonOptionRows.fitToWidth();
        this.optionRows.fitToWidth();
    }
    
    protected String nonOptionOutput() {
        return this.nonOptionRows.render();
    }
    
    protected String optionOutput() {
        return this.optionRows.render();
    }
    
    protected String formattedHelpOutput() {
        final StringBuilder formatted = new StringBuilder();
        final String nonOptionDisplay = this.nonOptionOutput();
        if (!Strings.isNullOrEmpty(nonOptionDisplay)) {
            formatted.append(nonOptionDisplay).append(Strings.LINE_SEPARATOR);
        }
        formatted.append(this.optionOutput());
        return formatted.toString();
    }
    
    protected void addRows(final Collection<? extends OptionDescriptor> options) {
        this.addNonOptionsDescription(options);
        if (options.isEmpty()) {
            this.addOptionRow(this.message("no.options.specified", new Object[0]));
        }
        else {
            this.addHeaders(options);
            this.addOptions(options);
        }
        this.fitRowsToWidth();
    }
    
    protected void addNonOptionsDescription(final Collection<? extends OptionDescriptor> options) {
        final OptionDescriptor nonOptions = this.findAndRemoveNonOptionsSpec(options);
        if (this.shouldShowNonOptionArgumentDisplay(nonOptions)) {
            this.addNonOptionRow(this.message("non.option.arguments.header", new Object[0]));
            this.addNonOptionRow(this.createNonOptionArgumentsDisplay(nonOptions));
        }
    }
    
    protected boolean shouldShowNonOptionArgumentDisplay(final OptionDescriptor nonOptionDescriptor) {
        return !Strings.isNullOrEmpty(nonOptionDescriptor.description()) || !Strings.isNullOrEmpty(nonOptionDescriptor.argumentTypeIndicator()) || !Strings.isNullOrEmpty(nonOptionDescriptor.argumentDescription());
    }
    
    protected String createNonOptionArgumentsDisplay(final OptionDescriptor nonOptionDescriptor) {
        final StringBuilder buffer = new StringBuilder();
        this.maybeAppendOptionInfo(buffer, nonOptionDescriptor);
        this.maybeAppendNonOptionsDescription(buffer, nonOptionDescriptor);
        return buffer.toString();
    }
    
    protected void maybeAppendNonOptionsDescription(final StringBuilder buffer, final OptionDescriptor nonOptions) {
        buffer.append((buffer.length() > 0 && !Strings.isNullOrEmpty(nonOptions.description())) ? " -- " : "").append(nonOptions.description());
    }
    
    protected OptionDescriptor findAndRemoveNonOptionsSpec(final Collection<? extends OptionDescriptor> options) {
        final Iterator<? extends OptionDescriptor> it = options.iterator();
        while (it.hasNext()) {
            final OptionDescriptor next = (OptionDescriptor)it.next();
            if (next.representsNonOptions()) {
                it.remove();
                return next;
            }
        }
        throw new AssertionError((Object)"no non-options argument spec");
    }
    
    protected void addHeaders(final Collection<? extends OptionDescriptor> options) {
        if (this.hasRequiredOption(options)) {
            this.addOptionRow(this.message("option.header.with.required.indicator", new Object[0]), this.message("description.header", new Object[0]));
            this.addOptionRow(this.message("option.divider.with.required.indicator", new Object[0]), this.message("description.divider", new Object[0]));
        }
        else {
            this.addOptionRow(this.message("option.header", new Object[0]), this.message("description.header", new Object[0]));
            this.addOptionRow(this.message("option.divider", new Object[0]), this.message("description.divider", new Object[0]));
        }
    }
    
    protected final boolean hasRequiredOption(final Collection<? extends OptionDescriptor> options) {
        for (final OptionDescriptor each : options) {
            if (each.isRequired()) {
                return true;
            }
        }
        return false;
    }
    
    protected void addOptions(final Collection<? extends OptionDescriptor> options) {
        for (final OptionDescriptor each : options) {
            if (!each.representsNonOptions()) {
                this.addOptionRow(this.createOptionDisplay(each), this.createDescriptionDisplay(each));
            }
        }
    }
    
    protected String createOptionDisplay(final OptionDescriptor descriptor) {
        final StringBuilder buffer = new StringBuilder(descriptor.isRequired() ? "* " : "");
        final Iterator<String> i = descriptor.options().iterator();
        while (i.hasNext()) {
            final String option = i.next();
            buffer.append(this.optionLeader(option));
            buffer.append(option);
            if (i.hasNext()) {
                buffer.append(", ");
            }
        }
        this.maybeAppendOptionInfo(buffer, descriptor);
        return buffer.toString();
    }
    
    protected String optionLeader(final String option) {
        return (option.length() > 1) ? "--" : ParserRules.HYPHEN;
    }
    
    protected void maybeAppendOptionInfo(final StringBuilder buffer, final OptionDescriptor descriptor) {
        final String indicator = this.extractTypeIndicator(descriptor);
        final String description = descriptor.argumentDescription();
        if (indicator != null || !Strings.isNullOrEmpty(description)) {
            this.appendOptionHelp(buffer, indicator, description, descriptor.requiresArgument());
        }
    }
    
    protected String extractTypeIndicator(final OptionDescriptor descriptor) {
        final String indicator = descriptor.argumentTypeIndicator();
        if (!Strings.isNullOrEmpty(indicator) && !String.class.getName().equals(indicator)) {
            return Classes.shortNameOf(indicator);
        }
        return null;
    }
    
    protected void appendOptionHelp(final StringBuilder buffer, final String typeIndicator, final String description, final boolean required) {
        if (required) {
            this.appendTypeIndicator(buffer, typeIndicator, description, '<', '>');
        }
        else {
            this.appendTypeIndicator(buffer, typeIndicator, description, '[', ']');
        }
    }
    
    protected void appendTypeIndicator(final StringBuilder buffer, final String typeIndicator, final String description, final char start, final char end) {
        buffer.append(' ').append(start);
        if (typeIndicator != null) {
            buffer.append(typeIndicator);
        }
        if (!Strings.isNullOrEmpty(description)) {
            if (typeIndicator != null) {
                buffer.append(": ");
            }
            buffer.append(description);
        }
        buffer.append(end);
    }
    
    protected String createDescriptionDisplay(final OptionDescriptor descriptor) {
        final List<?> defaultValues = descriptor.defaultValues();
        if (defaultValues.isEmpty()) {
            return descriptor.description();
        }
        final String defaultValuesDisplay = this.createDefaultValuesDisplay(defaultValues);
        return (descriptor.description() + ' ' + Strings.surround(this.message("default.value.header", new Object[0]) + ' ' + defaultValuesDisplay, '(', ')')).trim();
    }
    
    protected String createDefaultValuesDisplay(final List<?> defaultValues) {
        return (defaultValues.size() == 1) ? defaultValues.get(0).toString() : defaultValues.toString();
    }
    
    protected String message(final String keySuffix, final Object... args) {
        return Messages.message(Locale.getDefault(), "joptsimple.HelpFormatterMessages", BuiltinHelpFormatter.class, keySuffix, args);
    }
}
