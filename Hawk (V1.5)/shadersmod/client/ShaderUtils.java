package shadersmod.client;

import optifine.Config;

public class ShaderUtils {
   public static ShaderOption getShaderOption(String var0, ShaderOption[] var1) {
      if (var1 == null) {
         return null;
      } else {
         for(int var2 = 0; var2 < var1.length; ++var2) {
            ShaderOption var3 = var1[var2];
            if (var3.getName().equals(var0)) {
               return var3;
            }
         }

         return null;
      }
   }

   public static ShaderProfile detectProfile(ShaderProfile[] var0, ShaderOption[] var1, boolean var2) {
      if (var0 == null) {
         return null;
      } else {
         for(int var3 = 0; var3 < var0.length; ++var3) {
            ShaderProfile var4 = var0[var3];
            if (matchProfile(var4, var1, var2)) {
               return var4;
            }
         }

         return null;
      }
   }

   public static boolean matchProfile(ShaderProfile var0, ShaderOption[] var1, boolean var2) {
      if (var0 == null) {
         return false;
      } else if (var1 == null) {
         return false;
      } else {
         String[] var3 = var0.getOptions();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            String var5 = var3[var4];
            ShaderOption var6 = getShaderOption(var5, var1);
            if (var6 != null) {
               String var7 = var2 ? var6.getValueDefault() : var6.getValue();
               String var8 = var0.getValue(var5);
               if (!Config.equals(var7, var8)) {
                  return false;
               }
            }
         }

         return true;
      }
   }
}
