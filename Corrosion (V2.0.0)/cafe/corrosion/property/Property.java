/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.property;

import cafe.corrosion.Corrosion;
import cafe.corrosion.module.Module;
import com.google.gson.JsonObject;
import java.util.function.BooleanSupplier;

public abstract class Property<T> {
    private final Module parent;
    private final String name;
    private T value;
    private BooleanSupplier hidden = () -> false;

    public Property(Module parent, String name, T defaultValue) {
        this.parent = parent;
        this.name = name;
        this.value = defaultValue;
        Corrosion.INSTANCE.getPropertyRegistry().register(parent, this);
    }

    public Property<T> setValue(T value) {
        this.value = value;
        return this;
    }

    public Property<T> setHidden(BooleanSupplier supplier) {
        this.hidden = supplier;
        return this;
    }

    public abstract JsonObject serializeProperty();

    public abstract void applySerializedProperty(JsonObject var1);

    public Module getParent() {
        return this.parent;
    }

    public String getName() {
        return this.name;
    }

    public T getValue() {
        return this.value;
    }

    public BooleanSupplier getHidden() {
        return this.hidden;
    }
}

