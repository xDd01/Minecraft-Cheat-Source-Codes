package me.spec.eris.api.value.types;

import java.util.function.Supplier;

import me.spec.eris.api.module.Module;
import me.spec.eris.api.value.Value;

public class NumberValue<T> extends Value<T> {

    private T minimumValue, maximumValue;

    public NumberValue(String valueName, T defaultValueObject, T minimumValue, T maximumValue, Module parent, Supplier<?> supplier, String description) {
        super(valueName, defaultValueObject, parent, supplier, description);
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    public NumberValue(String valueName, T defaultValueObject, T minimumValue, T maximumValue, Module parent, String description) {
        super(valueName, defaultValueObject, parent, null, description);
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    public NumberValue(String valueName, T defaultValueObject, T minimumValue, T maximumValue, Module parent) {
        super(valueName, defaultValueObject, parent, null, "");
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    public NumberValue(String valueName, T defaultValueObject, T minimumValue, T maximumValue, Module parent, boolean valueSplitter, Supplier<?> supplier, String description) {
        super(valueName, defaultValueObject, parent, valueSplitter, supplier, description);
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    public NumberValue(String valueName, T defaultValueObject, T minimumValue, T maximumValue, Module parent, boolean valueSplitter, String description) {
        super(valueName, defaultValueObject, parent, valueSplitter, null, description);
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    public NumberValue(String valueName, T defaultValueObject, T minimumValue, T maximumValue, Module parent, boolean valueSplitter) {
        super(valueName, defaultValueObject, parent, valueSplitter, null, "");
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    public T getMinimumValue() {
        return minimumValue;
    }

    public void setMinimumValue(T minimumValue) {
        this.minimumValue = minimumValue;
    }

    public T getMaximumValue() {
        return maximumValue;
    }

    public void setMaximumValue(T maximumValue) {
        this.maximumValue = maximumValue;
    }

    public boolean isValueSplitter() {
        return this.getValueSplitter();
    }
}
