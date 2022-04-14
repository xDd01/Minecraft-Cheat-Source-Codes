/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.property.type;

import cafe.corrosion.module.Module;
import cafe.corrosion.property.Property;
import cafe.corrosion.util.json.JsonChain;
import com.google.gson.JsonObject;

public class NumberProperty
extends Property<Number> {
    private final Number min;
    private final Number max;
    private final Number increment;

    public NumberProperty(Module parent, String name, Number start, Number min, Number max, Number increment) {
        super(parent, name, start);
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    @Override
    public JsonObject serializeProperty() {
        return new JsonChain().addProperty("name", this.getName()).addProperty("type", "number").addProperty("value", (Number)this.getValue()).getJsonObject();
    }

    @Override
    public void applySerializedProperty(JsonObject serializedProperty) {
        this.setValue(serializedProperty.get("value").getAsNumber());
    }

    public Number getMin() {
        return this.min;
    }

    public Number getMax() {
        return this.max;
    }

    public Number getIncrement() {
        return this.increment;
    }
}

