/*
 * Decompiled with CFR 0.152.
 */
package optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import net.minecraft.util.ResourceLocation;
import optifine.Config;

public class FontUtils {
    public static Properties readFontProperties(ResourceLocation p_readFontProperties_0_) {
        String s2 = p_readFontProperties_0_.getResourcePath();
        Properties properties = new Properties();
        String s1 = ".png";
        if (!s2.endsWith(s1)) {
            return properties;
        }
        String s22 = s2.substring(0, s2.length() - s1.length()) + ".properties";
        try {
            ResourceLocation resourcelocation = new ResourceLocation(p_readFontProperties_0_.getResourceDomain(), s22);
            InputStream inputstream = Config.getResourceStream(Config.getResourceManager(), resourcelocation);
            if (inputstream == null) {
                return properties;
            }
            Config.log("Loading " + s22);
            properties.load(inputstream);
        }
        catch (FileNotFoundException resourcelocation) {
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
        return properties;
    }

    public static void readCustomCharWidths(Properties p_readCustomCharWidths_0_, float[] p_readCustomCharWidths_1_) {
        for (Object s0 : p_readCustomCharWidths_0_.keySet()) {
            String s3;
            float f2;
            String s2;
            int i2;
            String s1;
            String s4 = (String)s0;
            if (!s4.startsWith(s1 = "width.") || (i2 = Config.parseInt(s2 = s4.substring(s1.length()), -1)) < 0 || i2 >= p_readCustomCharWidths_1_.length || !((f2 = Config.parseFloat(s3 = p_readCustomCharWidths_0_.getProperty(s4), -1.0f)) >= 0.0f)) continue;
            p_readCustomCharWidths_1_[i2] = f2;
        }
    }

    public static float readFloat(Properties p_readFloat_0_, String p_readFloat_1_, float p_readFloat_2_) {
        String s2 = p_readFloat_0_.getProperty(p_readFloat_1_);
        if (s2 == null) {
            return p_readFloat_2_;
        }
        float f2 = Config.parseFloat(s2, Float.MIN_VALUE);
        if (f2 == Float.MIN_VALUE) {
            Config.warn("Invalid value for " + p_readFloat_1_ + ": " + s2);
            return p_readFloat_2_;
        }
        return f2;
    }

    public static ResourceLocation getHdFontLocation(ResourceLocation p_getHdFontLocation_0_) {
        if (!Config.isCustomFonts()) {
            return p_getHdFontLocation_0_;
        }
        if (p_getHdFontLocation_0_ == null) {
            return p_getHdFontLocation_0_;
        }
        String s2 = p_getHdFontLocation_0_.getResourcePath();
        String s1 = "textures/";
        String s22 = "mcpatcher/";
        if (!s2.startsWith(s1)) {
            return p_getHdFontLocation_0_;
        }
        s2 = s2.substring(s1.length());
        s2 = s22 + s2;
        ResourceLocation resourcelocation = new ResourceLocation(p_getHdFontLocation_0_.getResourceDomain(), s2);
        return Config.hasResource(Config.getResourceManager(), resourcelocation) ? resourcelocation : p_getHdFontLocation_0_;
    }
}

