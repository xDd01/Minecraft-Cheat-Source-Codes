/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.task;

import cafe.corrosion.Corrosion;
import cafe.corrosion.module.Module;
import cafe.corrosion.property.Property;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.gardeningtool.helper.annotation.Native;
import java.util.List;

@Native
public class LoadModulesTask
implements Runnable {
    private static final Gson GSON = new Gson();
    private final List<JsonObject> objects;

    @Override
    public void run() {
        this.objects.forEach(object -> {
            String moduleName = object.get("module").getAsString();
            Object module = Corrosion.INSTANCE.getModuleManager().getModule(moduleName);
            List<Property<?>> properties = Corrosion.INSTANCE.getPropertyRegistry().getProperties((Module)module);
            properties.forEach(property -> {
                if (!object.has(property.getName() + "-property")) {
                    return;
                }
                JsonObject deserializedData = GSON.fromJson(object.get(property.getName() + "-property").getAsString(), JsonObject.class);
                property.applySerializedProperty(deserializedData);
            });
            if (module != null && ((Module)module).isEnabled() != object.get("enabled").getAsBoolean()) {
                ((Module)module).toggle();
            }
        });
    }

    public LoadModulesTask(List<JsonObject> objects) {
        this.objects = objects;
    }
}

