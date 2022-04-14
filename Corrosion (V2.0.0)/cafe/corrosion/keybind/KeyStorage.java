/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.keybind;

import cafe.corrosion.util.json.JsonChain;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class KeyStorage {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    private static final Map<String, Integer> KEY_MAP = new HashMap<String, Integer>();

    public static int getKey(String module) {
        return KEY_MAP.getOrDefault(module, 0);
    }

    public static void setKey(String module, int key) {
        KEY_MAP.put(module, key);
    }

    public static void load(Path path) {
        if (!path.toFile().exists()) {
            path.toFile().createNewFile();
            return;
        }
        byte[] bytes = Files.readAllBytes(path);
        JsonArray array = GSON.fromJson(new String(bytes), JsonArray.class);
        if (array == null) {
            return;
        }
        array.forEach(element -> {
            JsonObject object = element.getAsJsonObject();
            KEY_MAP.put(object.get("module").getAsString(), object.get("key").getAsInt());
        });
    }

    public static void write(Path path) {
        JsonArray elements = new JsonArray();
        KEY_MAP.forEach((key, value) -> elements.add(new JsonChain().addProperty("module", (String)key).addProperty("key", (Number)value).getJsonObject()));
        Files.write(path, GSON.toJson(elements).getBytes(), new OpenOption[0]);
    }
}

