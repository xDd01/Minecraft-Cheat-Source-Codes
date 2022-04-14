package me.spec.eris.api.value.types;

import java.util.function.Supplier;

import me.spec.eris.api.module.Module;
import me.spec.eris.api.value.Value;

public class BooleanValue<T> extends Value<T> {

    public BooleanValue(String valueName, T defaultValueObject, Module parent, Supplier<?> supplier, String description) {
        super(valueName, defaultValueObject, parent, supplier, description);
    }

    public BooleanValue(String valueName, T defaultValueObject, Module parent, String description) {
        super(valueName, defaultValueObject, parent, null, description);
    }

    public BooleanValue(String valueName, T defaultValueObject, Module parent) {
        super(valueName, defaultValueObject, parent, null, "");
    }

    public BooleanValue(String valueName, T defaultValueObject, Module parent, boolean valueSplitter, Supplier<?> supplier, String description) {
        super(valueName, defaultValueObject, parent, valueSplitter, supplier, description);
    }

    public BooleanValue(String valueName, T defaultValueObject, Module parent, boolean valueSplitter, String description) {
        super(valueName, defaultValueObject, parent, valueSplitter, null, description);
    }

    public BooleanValue(String valueName, T defaultValueObject, Module parent, boolean valueSplitter) {
        super(valueName, defaultValueObject, parent, valueSplitter, null, "");
    }
}
