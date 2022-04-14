package me.spec.eris.api.value.types;

import java.util.function.Supplier;

import me.spec.eris.api.module.Module;
import me.spec.eris.api.value.Value;

public class ModeValue<T extends Enum<T>> extends Value<T> {

    private T[] modes;

    public ModeValue(String valueName, T defaultValueObject, Module parent, Supplier<?> supplier, String description) {
        super(valueName, defaultValueObject, parent, supplier, description);
        modes = readModes(getValue());
    }



    public ModeValue(String valueName, T defaultValueObject, Module parent) {
        super(valueName, defaultValueObject, parent);
        modes = readModes(getValue());
    }

    public ModeValue(String valueName, T defaultValueObject, Module parent, boolean valueSplitter) {
        super(valueName, defaultValueObject, parent, valueSplitter);
        modes = readModes(getValue());
    }

    public ModeValue(String valueName, T defaultValueObject, Module parent, boolean valueSplitter, Supplier<?> supplier, String description) {
        super(valueName, defaultValueObject, parent, valueSplitter, supplier, description);
        modes = readModes(getValue());
    }


    public String getFixedValue() {
        return getValue().toString();
    }

    public T[] getModes() {
        return modes;
    }

    public T[] readModes(T value) {
        return value.getDeclaringClass().getEnumConstants();
    }

    public void increment() {
        T currentValue = getValue();

        for (T constant : getModes()) {
            if (constant != currentValue) {
                continue;
            }

            T newValue;

            int ordinal = constant.ordinal();
            if (ordinal == getModes().length - 1) {
                newValue = getModes()[0];
            } else {
                newValue = getModes()[ordinal + 1];
            }

            setValueObject(newValue);
            return;
        }
    }

    public void decrement() {
        T currentValue = getValue();

        for (T constant : getModes()) {
            if (constant != currentValue) {
                continue;
            }

            T newValue;

            int ordinal = constant.ordinal();
            if (ordinal == 0) {
                newValue = getModes()[getModes().length - 1];
            } else {
                newValue = getModes()[ordinal - 1];
            }

            setValueObject(newValue);
            return;
        }
    }

    public boolean isValueSplitter() {
        return this.getValueSplitter();
    }
}

