/*
 * Decompiled with CFR 0.152.
 */
package optifine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;
import optifine.Config;
import optifine.StrUtils;

public class ResUtils {
    public static String[] collectFiles(String p_collectFiles_0_, String p_collectFiles_1_) {
        return ResUtils.collectFiles(new String[]{p_collectFiles_0_}, new String[]{p_collectFiles_1_});
    }

    public static String[] collectFiles(String[] p_collectFiles_0_, String[] p_collectFiles_1_) {
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        IResourcePack[] airesourcepack = Config.getResourcePacks();
        for (int i2 = 0; i2 < airesourcepack.length; ++i2) {
            IResourcePack iresourcepack = airesourcepack[i2];
            String[] astring = ResUtils.collectFiles(iresourcepack, p_collectFiles_0_, p_collectFiles_1_, (String[])null);
            set.addAll(Arrays.asList(astring));
        }
        String[] astring1 = set.toArray(new String[set.size()]);
        return astring1;
    }

    public static String[] collectFiles(IResourcePack p_collectFiles_0_, String p_collectFiles_1_, String p_collectFiles_2_, String[] p_collectFiles_3_) {
        return ResUtils.collectFiles(p_collectFiles_0_, new String[]{p_collectFiles_1_}, new String[]{p_collectFiles_2_}, p_collectFiles_3_);
    }

    public static String[] collectFiles(IResourcePack p_collectFiles_0_, String[] p_collectFiles_1_, String[] p_collectFiles_2_) {
        return ResUtils.collectFiles(p_collectFiles_0_, p_collectFiles_1_, p_collectFiles_2_, (String[])null);
    }

    public static String[] collectFiles(IResourcePack p_collectFiles_0_, String[] p_collectFiles_1_, String[] p_collectFiles_2_, String[] p_collectFiles_3_) {
        if (p_collectFiles_0_ instanceof DefaultResourcePack) {
            return ResUtils.collectFilesFixed(p_collectFiles_0_, p_collectFiles_3_);
        }
        if (!(p_collectFiles_0_ instanceof AbstractResourcePack)) {
            return new String[0];
        }
        AbstractResourcePack abstractresourcepack = (AbstractResourcePack)p_collectFiles_0_;
        File file1 = abstractresourcepack.resourcePackFile;
        return file1 == null ? new String[]{} : (file1.isDirectory() ? ResUtils.collectFilesFolder(file1, "", p_collectFiles_1_, p_collectFiles_2_) : (file1.isFile() ? ResUtils.collectFilesZIP(file1, p_collectFiles_1_, p_collectFiles_2_) : new String[]{}));
    }

    private static String[] collectFilesFixed(IResourcePack p_collectFilesFixed_0_, String[] p_collectFilesFixed_1_) {
        if (p_collectFilesFixed_1_ == null) {
            return new String[0];
        }
        ArrayList<String> list = new ArrayList<String>();
        for (int i2 = 0; i2 < p_collectFilesFixed_1_.length; ++i2) {
            String s2 = p_collectFilesFixed_1_[i2];
            ResourceLocation resourcelocation = new ResourceLocation(s2);
            if (!p_collectFilesFixed_0_.resourceExists(resourcelocation)) continue;
            list.add(s2);
        }
        String[] astring = list.toArray(new String[list.size()]);
        return astring;
    }

    private static String[] collectFilesFolder(File p_collectFilesFolder_0_, String p_collectFilesFolder_1_, String[] p_collectFilesFolder_2_, String[] p_collectFilesFolder_3_) {
        ArrayList<String> list = new ArrayList<String>();
        String s2 = "assets/minecraft/";
        File[] afile = p_collectFilesFolder_0_.listFiles();
        if (afile == null) {
            return new String[0];
        }
        for (int i2 = 0; i2 < afile.length; ++i2) {
            File file1 = afile[i2];
            if (file1.isFile()) {
                String s3 = p_collectFilesFolder_1_ + file1.getName();
                if (!s3.startsWith(s2) || !StrUtils.startsWith(s3 = s3.substring(s2.length()), p_collectFilesFolder_2_) || !StrUtils.endsWith(s3, p_collectFilesFolder_3_)) continue;
                list.add(s3);
                continue;
            }
            if (!file1.isDirectory()) continue;
            String s1 = p_collectFilesFolder_1_ + file1.getName() + "/";
            String[] astring = ResUtils.collectFilesFolder(file1, s1, p_collectFilesFolder_2_, p_collectFilesFolder_3_);
            for (int j2 = 0; j2 < astring.length; ++j2) {
                String s22 = astring[j2];
                list.add(s22);
            }
        }
        String[] astring1 = list.toArray(new String[list.size()]);
        return astring1;
    }

    private static String[] collectFilesZIP(File p_collectFilesZIP_0_, String[] p_collectFilesZIP_1_, String[] p_collectFilesZIP_2_) {
        ArrayList<String> list = new ArrayList<String>();
        String s2 = "assets/minecraft/";
        try {
            ZipFile zipfile = new ZipFile(p_collectFilesZIP_0_);
            Enumeration<? extends ZipEntry> enumeration = zipfile.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry zipentry = enumeration.nextElement();
                String s1 = zipentry.getName();
                if (!s1.startsWith(s2) || !StrUtils.startsWith(s1 = s1.substring(s2.length()), p_collectFilesZIP_1_) || !StrUtils.endsWith(s1, p_collectFilesZIP_2_)) continue;
                list.add(s1);
            }
            zipfile.close();
            String[] astring = list.toArray(new String[list.size()]);
            return astring;
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
            return new String[0];
        }
    }
}

