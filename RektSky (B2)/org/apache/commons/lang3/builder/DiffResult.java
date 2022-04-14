package org.apache.commons.lang3.builder;

import org.apache.commons.lang3.*;
import java.util.*;

public class DiffResult implements Iterable<Diff<?>>
{
    public static final String OBJECTS_SAME_STRING = "";
    private static final String DIFFERS_STRING = "differs from";
    private final List<Diff<?>> diffs;
    private final Object lhs;
    private final Object rhs;
    private final ToStringStyle style;
    
    DiffResult(final Object lhs, final Object rhs, final List<Diff<?>> diffs, final ToStringStyle style) {
        Validate.isTrue(lhs != null, "Left hand object cannot be null", new Object[0]);
        Validate.isTrue(rhs != null, "Right hand object cannot be null", new Object[0]);
        Validate.isTrue(diffs != null, "List of differences cannot be null", new Object[0]);
        this.diffs = diffs;
        this.lhs = lhs;
        this.rhs = rhs;
        if (style == null) {
            this.style = ToStringStyle.DEFAULT_STYLE;
        }
        else {
            this.style = style;
        }
    }
    
    public List<Diff<?>> getDiffs() {
        return Collections.unmodifiableList((List<? extends Diff<?>>)this.diffs);
    }
    
    public int getNumberOfDiffs() {
        return this.diffs.size();
    }
    
    public ToStringStyle getToStringStyle() {
        return this.style;
    }
    
    @Override
    public String toString() {
        return this.toString(this.style);
    }
    
    public String toString(final ToStringStyle style) {
        if (this.diffs.isEmpty()) {
            return "";
        }
        final ToStringBuilder lhsBuilder = new ToStringBuilder(this.lhs, style);
        final ToStringBuilder rhsBuilder = new ToStringBuilder(this.rhs, style);
        for (final Diff<?> diff : this.diffs) {
            lhsBuilder.append(diff.getFieldName(), diff.getLeft());
            rhsBuilder.append(diff.getFieldName(), diff.getRight());
        }
        return String.format("%s %s %s", lhsBuilder.build(), "differs from", rhsBuilder.build());
    }
    
    @Override
    public Iterator<Diff<?>> iterator() {
        return this.diffs.iterator();
    }
}
