/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.task;

import cafe.corrosion.Corrosion;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.manager.ModuleManager;
import cafe.corrosion.property.registry.PropertyRegistry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.gardeningtool.helper.annotation.Native;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

@Native
public class ExportModulesTask
implements Runnable {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    private final Path exportLocation;
    private final JsonObject baseObject;

    @Override
    public void run() {
        JsonArray array = new JsonArray();
        ModuleManager moduleManager = Corrosion.INSTANCE.getModuleManager();
        PropertyRegistry propertyRegistry = Corrosion.INSTANCE.getPropertyRegistry();
        if (this.baseObject != null) {
            array.add(this.baseObject);
        }
        moduleManager.getObjects().forEach(module -> {
            JsonObject object = new JsonObject();
            propertyRegistry.getProperties((Module)module).forEach(property -> {
                String serializedProperty = GSON.toJson(property.serializeProperty());
                object.addProperty(property.getName() + "-property", serializedProperty);
            });
            object.addProperty("module", module.getAttributes().name());
            object.addProperty("enabled", module.isEnabled());
            array.add(object);
        });
        Files.write(this.exportLocation, GSON.toJson(array).getBytes(), new OpenOption[0]);
    }

    public ExportModulesTask(Path exportLocation, JsonObject baseObject) {
        this.exportLocation = exportLocation;
        this.baseObject = baseObject;
    }
}

