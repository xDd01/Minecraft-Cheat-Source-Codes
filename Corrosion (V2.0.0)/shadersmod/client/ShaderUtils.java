/*
 * Decompiled with CFR 0.152.
 */
package shadersmod.client;

import optifine.Config;
import shadersmod.client.ShaderOption;
import shadersmod.client.ShaderProfile;

public class ShaderUtils {
    public static ShaderOption getShaderOption(String name, ShaderOption[] opts) {
        if (opts == null) {
            return null;
        }
        for (int i2 = 0; i2 < opts.length; ++i2) {
            ShaderOption shaderoption = opts[i2];
            if (!shaderoption.getName().equals(name)) continue;
            return shaderoption;
        }
        return null;
    }

    public static ShaderProfile detectProfile(ShaderProfile[] profs, ShaderOption[] opts, boolean def) {
        if (profs == null) {
            return null;
        }
        for (int i2 = 0; i2 < profs.length; ++i2) {
            ShaderProfile shaderprofile = profs[i2];
            if (!ShaderUtils.matchProfile(shaderprofile, opts, def)) continue;
            return shaderprofile;
        }
        return null;
    }

    public static boolean matchProfile(ShaderProfile prof, ShaderOption[] opts, boolean def) {
        if (prof == null) {
            return false;
        }
        if (opts == null) {
            return false;
        }
        String[] astring = prof.getOptions();
        for (int i2 = 0; i2 < astring.length; ++i2) {
            String s2;
            String s1;
            String s3 = astring[i2];
            ShaderOption shaderoption = ShaderUtils.getShaderOption(s3, opts);
            if (shaderoption == null || Config.equals(s1 = def ? shaderoption.getValueDefault() : shaderoption.getValue(), s2 = prof.getValue(s3))) continue;
            return false;
        }
        return true;
    }
}

