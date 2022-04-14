/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.property.type;

import cafe.corrosion.module.Module;
import cafe.corrosion.property.Property;
import cafe.corrosion.util.json.JsonChain;
import com.google.gson.JsonObject;

public class BooleanProperty
extends Property<Boolean> {
    public BooleanProperty(Module parent, String name) {
        super(parent, name, false);
    }

    public BooleanProperty(Module parent, String name, Boolean defaultValue) {
        super(parent, name, defaultValue);
    }

    @Override
    public JsonObject serializeProperty() {
        return new JsonChain().addProperty("name", this.getName()).addProperty("type", "boolean").addProperty("value", (Boolean)this.getValue()).getJsonObject();
    }

    @Override
    public void applySerializedProperty(JsonObject serializedProperty) {
        this.setValue(serializedProperty.get("value").getAsBoolean());
    }
}

