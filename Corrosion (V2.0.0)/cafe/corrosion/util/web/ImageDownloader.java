/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import me.vaziak.cube.web.WebRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

public class ImageDownloader {
    private static final Map<String, ResourceLocation> RESOURCE_LOCATION_CACHE = new HashMap<String, ResourceLocation>();
    private static final Map<String, BufferedImage> IMAGE_MAP = new HashMap<String, BufferedImage>();
    private static final String[] PATHS = new String[]{"head", "full"};
    private static final ExecutorService ASYNC_EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void download(String urlString, String name) throws Exception {
        BufferedImage image = ImageIO.read(new URL(urlString));
        IMAGE_MAP.put(name, image);
    }

    public static ResourceLocation getTextureAsLocation(String texture) {
        if (!RESOURCE_LOCATION_CACHE.containsKey(texture)) {
            DynamicTexture dynamicTexture = new DynamicTexture(IMAGE_MAP.get(texture));
            ResourceLocation location = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation(texture, dynamicTexture);
            RESOURCE_LOCATION_CACHE.put(texture, location);
            return location;
        }
        return RESOURCE_LOCATION_CACHE.get(texture);
    }

    public static void downloadImage(String name) {
        ASYNC_EXECUTOR.execute(() -> {
            WebRequest request = new WebRequest("https://api.ashcon.app/mojang/v2/user/" + name).setMethod("GET");
            request.invoke(response -> {
                JsonObject responseJson = GSON.fromJson(new String(response.getResponseData()), JsonObject.class);
                String uuid = responseJson.get("uuid").getAsString();
                for (String path : PATHS) {
                    String url = "https://visage.surgeplay.com/" + path + "/" + uuid;
                    try {
                        ImageDownloader.download(url, name + "-" + path);
                    }
                    catch (Exception exc) {
                        exc.printStackTrace();
                    }
                }
            });
        });
    }
}

