/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.property.type;

import cafe.corrosion.module.Module;
import cafe.corrosion.property.Property;
import cafe.corrosion.util.json.JsonChain;
import com.google.gson.JsonObject;
import java.awt.Color;

public class ColorProperty
extends Property<Color> {
    public ColorProperty(Module parent, String name, Color defaultValue) {
        super(parent, name, defaultValue);
    }

    @Override
    public JsonObject serializeProperty() {
        return new JsonChain().addProperty("color", ((Color)this.getValue()).getRGB()).getJsonObject();
    }

    @Override
    public void applySerializedProperty(JsonObject serializedProperty) {
        this.setValue(new Color(serializedProperty.get("color").getAsInt()));
    }
}

