/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.property.type;

import cafe.corrosion.module.Module;
import cafe.corrosion.property.Property;
import cafe.corrosion.util.json.JsonChain;
import cafe.corrosion.util.nameable.INameable;
import com.google.gson.JsonObject;
import java.util.Arrays;

public class EnumProperty<E extends INameable>
extends Property<E> {
    private final E[] values;

    public EnumProperty(Module parent, String name, E defaultValue, E[] values) {
        super(parent, name, defaultValue);
        this.values = values;
    }

    public EnumProperty(Module parent, String name, E[] values) {
        super(parent, name, values[0]);
        this.values = values;
    }

    @Override
    public JsonObject serializeProperty() {
        return new JsonChain().addProperty("name", this.getName()).addProperty("type", "enum").addProperty("value", ((INameable)this.getValue()).getName()).getJsonObject();
    }

    @Override
    public void applySerializedProperty(JsonObject serializedProperty) {
        String name = serializedProperty.get("value").getAsString();
        INameable value = Arrays.stream(this.values).filter(element -> element.getName().equalsIgnoreCase(name)).findFirst().orElse((INameable)this.values[0]);
        this.setValue(value);
    }

    public E[] getValues() {
        return this.values;
    }
}

