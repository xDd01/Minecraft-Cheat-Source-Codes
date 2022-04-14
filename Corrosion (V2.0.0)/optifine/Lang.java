/*
 * Decompiled with CFR 0.152.
 */
package optifine;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;
import optifine.Config;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class Lang {
    private static final Splitter splitter = Splitter.on('=').limit(2);
    private static final Pattern pattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");

    public static void resourcesReloaded() {
        Map map = I18n.getLocaleProperties();
        ArrayList<String> list = new ArrayList<String>();
        String s2 = "optifine/lang/";
        String s1 = "en_US";
        String s22 = ".lang";
        list.add(s2 + s1 + s22);
        if (!Config.getGameSettings().language.equals(s1)) {
            list.add(s2 + Config.getGameSettings().language + s22);
        }
        String[] astring = list.toArray(new String[list.size()]);
        Lang.loadResources(Config.getDefaultResourcePack(), astring, map);
        IResourcePack[] airesourcepack = Config.getResourcePacks();
        for (int i2 = 0; i2 < airesourcepack.length; ++i2) {
            IResourcePack iresourcepack = airesourcepack[i2];
            Lang.loadResources(iresourcepack, astring, map);
        }
    }

    private static void loadResources(IResourcePack p_loadResources_0_, String[] p_loadResources_1_, Map p_loadResources_2_) {
        try {
            for (int i2 = 0; i2 < p_loadResources_1_.length; ++i2) {
                InputStream inputstream;
                String s2 = p_loadResources_1_[i2];
                ResourceLocation resourcelocation = new ResourceLocation(s2);
                if (!p_loadResources_0_.resourceExists(resourcelocation) || (inputstream = p_loadResources_0_.getInputStream(resourcelocation)) == null) continue;
                Lang.loadLocaleData(inputstream, p_loadResources_2_);
            }
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    public static void loadLocaleData(InputStream p_loadLocaleData_0_, Map p_loadLocaleData_1_) throws IOException {
        for (String s2 : IOUtils.readLines(p_loadLocaleData_0_, Charsets.UTF_8)) {
            String[] astring;
            if (s2.isEmpty() || s2.charAt(0) == '#' || (astring = Iterables.toArray(splitter.split(s2), String.class)) == null || astring.length != 2) continue;
            String s1 = astring[0];
            String s22 = pattern.matcher(astring[1]).replaceAll("%$1s");
            p_loadLocaleData_1_.put(s1, s22);
        }
    }

    public static String get(String p_get_0_) {
        return I18n.format(p_get_0_, new Object[0]);
    }

    public static String get(String p_get_0_, String p_get_1_) {
        String s2 = I18n.format(p_get_0_, new Object[0]);
        return s2 != null && !s2.equals(p_get_0_) ? s2 : p_get_1_;
    }

    public static String getOn() {
        return I18n.format("options.on", new Object[0]);
    }

    public static String getOff() {
        return I18n.format("options.off", new Object[0]);
    }

    public static String getFast() {
        return I18n.format("options.graphics.fast", new Object[0]);
    }

    public static String getFancy() {
        return I18n.format("options.graphics.fancy", new Object[0]);
    }

    public static String getDefault() {
        return I18n.format("generator.default", new Object[0]);
    }
}

