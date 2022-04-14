package shadersmod.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import optifine.Config;
import optifine.StrUtils;

public class ShaderPackParser {
   private static final Pattern PATTERN_INCLUDE = Pattern.compile("^\\s*#include\\s+\"([A-Za-z0-9_/\\.]+)\".*$");
   private static final Set<String> setConstNames = makeSetConstNames();

   public static BufferedReader resolveIncludes(BufferedReader var0, String var1, IShaderPack var2, int var3) throws IOException {
      String var4 = "/";
      int var5 = var1.lastIndexOf("/");
      if (var5 >= 0) {
         var4 = var1.substring(0, var5);
      }

      CharArrayWriter var6 = new CharArrayWriter();

      while(true) {
         String var7 = var0.readLine();
         if (var7 == null) {
            CharArrayReader var12 = new CharArrayReader(var6.toCharArray());
            return new BufferedReader(var12);
         }

         Matcher var8 = PATTERN_INCLUDE.matcher(var7);
         if (var8.matches()) {
            String var9 = var8.group(1);
            boolean var10 = var9.startsWith("/");
            String var11 = var10 ? String.valueOf((new StringBuilder("/shaders")).append(var9)) : String.valueOf((new StringBuilder(String.valueOf(var4))).append("/").append(var9));
            var7 = loadFile(var11, var2, var3);
            if (var7 == null) {
               throw new IOException(String.valueOf((new StringBuilder("Included file not found: ")).append(var1)));
            }
         }

         var6.write(var7);
         var6.write("\n");
      }
   }

   private static ShaderOption getShaderOption(String var0, String var1) {
      ShaderOption var2 = null;
      if (var2 == null) {
         var2 = ShaderOptionSwitch.parseOption(var0, var1);
      }

      if (var2 == null) {
         var2 = ShaderOptionVariable.parseOption(var0, var1);
      }

      if (var2 != null) {
         return var2;
      } else {
         if (var2 == null) {
            var2 = ShaderOptionSwitchConst.parseOption(var0, var1);
         }

         if (var2 == null) {
            var2 = ShaderOptionVariableConst.parseOption(var0, var1);
         }

         return var2 != null && setConstNames.contains(var2.getName()) ? var2 : null;
      }
   }

   private static boolean isOptionUsed(ShaderOption var0, String[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         String var3 = var1[var2];
         if (var0.isUsedInLine(var3)) {
            return true;
         }
      }

      return false;
   }

   private static boolean parseGuiScreen(String var0, Properties var1, Map<String, ShaderOption[]> var2, ShaderProfile[] var3, ShaderOption[] var4) {
      String var5 = var1.getProperty(var0);
      if (var5 == null) {
         return false;
      } else {
         ArrayList var6 = new ArrayList();
         HashSet var7 = new HashSet();
         String[] var8 = Config.tokenize(var5, " ");

         for(int var9 = 0; var9 < var8.length; ++var9) {
            String var10 = var8[var9];
            if (var10.equals("<empty>")) {
               var6.add((Object)null);
            } else if (var7.contains(var10)) {
               Config.warn(String.valueOf((new StringBuilder("[Shaders] Duplicate option: ")).append(var10).append(", key: ").append(var0)));
            } else {
               var7.add(var10);
               if (var10.equals("<profile>")) {
                  if (var3 == null) {
                     Config.warn(String.valueOf((new StringBuilder("[Shaders] Option profile can not be used, no profiles defined: ")).append(var10).append(", key: ").append(var0)));
                  } else {
                     ShaderOptionProfile var16 = new ShaderOptionProfile(var3, var4);
                     var6.add(var16);
                  }
               } else if (var10.equals("*")) {
                  ShaderOptionRest var15 = new ShaderOptionRest("<rest>");
                  var6.add(var15);
               } else if (var10.startsWith("[") && var10.endsWith("]")) {
                  String var14 = StrUtils.removePrefixSuffix(var10, "[", "]");
                  if (!var14.matches("^[a-zA-Z0-9_]+$")) {
                     Config.warn(String.valueOf((new StringBuilder("[Shaders] Invalid screen: ")).append(var10).append(", key: ").append(var0)));
                  } else if (!parseGuiScreen(String.valueOf((new StringBuilder("screen.")).append(var14)), var1, var2, var3, var4)) {
                     Config.warn(String.valueOf((new StringBuilder("[Shaders] Invalid screen: ")).append(var10).append(", key: ").append(var0)));
                  } else {
                     ShaderOptionScreen var12 = new ShaderOptionScreen(var14);
                     var6.add(var12);
                  }
               } else {
                  ShaderOption var11 = ShaderUtils.getShaderOption(var10, var4);
                  if (var11 == null) {
                     Config.warn(String.valueOf((new StringBuilder("[Shaders] Invalid option: ")).append(var10).append(", key: ").append(var0)));
                     var6.add((Object)null);
                  } else {
                     var11.setVisible(true);
                     var6.add(var11);
                  }
               }
            }
         }

         ShaderOption[] var13 = (ShaderOption[])var6.toArray(new ShaderOption[var6.size()]);
         var2.put(var0, var13);
         return true;
      }
   }

   private static ShaderProfile parseProfile(String var0, Properties var1, Set<String> var2, ShaderOption[] var3) {
      String var4 = "profile.";
      String var5 = String.valueOf((new StringBuilder(String.valueOf(var4))).append(var0));
      if (var2.contains(var5)) {
         Config.warn(String.valueOf((new StringBuilder("[Shaders] Profile already parsed: ")).append(var0)));
         return null;
      } else {
         var2.add(var0);
         ShaderProfile var6 = new ShaderProfile(var0);
         String var7 = var1.getProperty(var5);
         String[] var8 = Config.tokenize(var7, " ");

         for(int var9 = 0; var9 < var8.length; ++var9) {
            String var10 = var8[var9];
            if (var10.startsWith(var4)) {
               String var16 = var10.substring(var4.length());
               ShaderProfile var18 = parseProfile(var16, var1, var2, var3);
               if (var6 != null) {
                  var6.addOptionValues(var18);
                  var6.addDisabledPrograms(var18.getDisabledPrograms());
               }
            } else {
               String[] var11 = Config.tokenize(var10, ":=");
               String var12;
               if (var11.length == 1) {
                  var12 = var11[0];
                  boolean var17 = true;
                  if (var12.startsWith("!")) {
                     var17 = false;
                     var12 = var12.substring(1);
                  }

                  String var19 = "program.";
                  if (!var17 && var12.startsWith("program.")) {
                     String var20 = var12.substring(var19.length());
                     if (!Shaders.isProgramPath(var20)) {
                        Config.warn(String.valueOf((new StringBuilder("Invalid program: ")).append(var20).append(" in profile: ").append(var6.getName())));
                     } else {
                        var6.addDisabledProgram(var20);
                     }
                  } else {
                     ShaderOption var15 = ShaderUtils.getShaderOption(var12, var3);
                     if (!(var15 instanceof ShaderOptionSwitch)) {
                        Config.warn(String.valueOf((new StringBuilder("[Shaders] Invalid option: ")).append(var12)));
                     } else {
                        var6.addOptionValue(var12, String.valueOf(var17));
                        var15.setVisible(true);
                     }
                  }
               } else if (var11.length != 2) {
                  Config.warn(String.valueOf((new StringBuilder("[Shaders] Invalid option value: ")).append(var10)));
               } else {
                  var12 = var11[0];
                  String var13 = var11[1];
                  ShaderOption var14 = ShaderUtils.getShaderOption(var12, var3);
                  if (var14 == null) {
                     Config.warn(String.valueOf((new StringBuilder("[Shaders] Invalid option: ")).append(var10)));
                  } else if (!var14.isValidValue(var13)) {
                     Config.warn(String.valueOf((new StringBuilder("[Shaders] Invalid value: ")).append(var10)));
                  } else {
                     var14.setVisible(true);
                     var6.addOptionValue(var12, var13);
                  }
               }
            }
         }

         return var6;
      }
   }

   private static String[] getLines(IShaderPack var0, String var1) {
      try {
         String var2 = loadFile(var1, var0, 0);
         if (var2 == null) {
            return new String[0];
         } else {
            ByteArrayInputStream var3 = new ByteArrayInputStream(var2.getBytes());
            String[] var4 = Config.readLines((InputStream)var3);
            return var4;
         }
      } catch (IOException var5) {
         Config.dbg(String.valueOf((new StringBuilder(String.valueOf(var5.getClass().getName()))).append(": ").append(var5.getMessage())));
         return new String[0];
      }
   }

   private static void collectShaderOptions(IShaderPack var0, String var1, String[] var2, Map<String, ShaderOption> var3) {
      for(int var4 = 0; var4 < var2.length; ++var4) {
         String var5 = var2[var4];
         if (!var5.equals("")) {
            String var6 = String.valueOf((new StringBuilder(String.valueOf(var1))).append("/").append(var5).append(".vsh"));
            String var7 = String.valueOf((new StringBuilder(String.valueOf(var1))).append("/").append(var5).append(".fsh"));
            collectShaderOptions(var0, var6, var3);
            collectShaderOptions(var0, var7, var3);
         }
      }

   }

   private static String loadFile(String var0, IShaderPack var1, int var2) throws IOException {
      if (var2 >= 10) {
         throw new IOException(String.valueOf((new StringBuilder("#include depth exceeded: ")).append(var2).append(", file: ").append(var0)));
      } else {
         ++var2;
         InputStream var3 = var1.getResourceAsStream(var0);
         if (var3 == null) {
            return null;
         } else {
            InputStreamReader var4 = new InputStreamReader(var3, "ASCII");
            BufferedReader var5 = new BufferedReader(var4);
            var5 = resolveIncludes(var5, var0, var1, var2);
            CharArrayWriter var6 = new CharArrayWriter();

            while(true) {
               String var7 = var5.readLine();
               if (var7 == null) {
                  return var6.toString();
               }

               var6.write(var7);
               var6.write("\n");
            }
         }
      }
   }

   private static void collectShaderOptions(IShaderPack var0, String var1, Map<String, ShaderOption> var2) {
      String[] var3 = getLines(var0, var1);

      for(int var4 = 0; var4 < var3.length; ++var4) {
         String var5 = var3[var4];
         ShaderOption var6 = getShaderOption(var5, var1);
         if (var6 != null && (!var6.checkUsed() || isOptionUsed(var6, var3))) {
            String var7 = var6.getName();
            ShaderOption var8 = (ShaderOption)var2.get(var7);
            if (var8 != null) {
               if (!Config.equals(var8.getValueDefault(), var6.getValueDefault())) {
                  Config.warn(String.valueOf((new StringBuilder("Ambiguous shader option: ")).append(var6.getName())));
                  Config.warn(String.valueOf((new StringBuilder(" - in ")).append(Config.arrayToString((Object[])var8.getPaths())).append(": ").append(var8.getValueDefault())));
                  Config.warn(String.valueOf((new StringBuilder(" - in ")).append(Config.arrayToString((Object[])var6.getPaths())).append(": ").append(var6.getValueDefault())));
                  var8.setEnabled(false);
               }

               if (var8.getDescription() == null || var8.getDescription().length() <= 0) {
                  var8.setDescription(var6.getDescription());
               }

               var8.addPaths(var6.getPaths());
            } else {
               var2.put(var7, var6);
            }
         }
      }

   }

   public static ShaderOption[] parseShaderPackOptions(IShaderPack var0, String[] var1, List<Integer> var2) {
      if (var0 == null) {
         return new ShaderOption[0];
      } else {
         HashMap var3 = new HashMap();
         collectShaderOptions(var0, "/shaders", var1, var3);
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            int var5 = (Integer)var4.next();
            String var6 = String.valueOf((new StringBuilder("/shaders/world")).append(var5));
            collectShaderOptions(var0, var6, var1, var3);
         }

         Collection var8 = var3.values();
         ShaderOption[] var9 = (ShaderOption[])var8.toArray(new ShaderOption[var8.size()]);
         Comparator var7 = new Comparator() {
            public int compare(Object var1, Object var2) {
               return this.compare((ShaderOption)var1, (ShaderOption)var2);
            }

            public int compare(ShaderOption var1, ShaderOption var2) {
               return var1.getName().compareToIgnoreCase(var2.getName());
            }
         };
         Arrays.sort(var9, var7);
         return var9;
      }
   }

   private static Set<String> makeSetConstNames() {
      HashSet var0 = new HashSet();
      var0.add("shadowMapResolution");
      var0.add("shadowDistance");
      var0.add("shadowIntervalSize");
      var0.add("generateShadowMipmap");
      var0.add("generateShadowColorMipmap");
      var0.add("shadowHardwareFiltering");
      var0.add("shadowHardwareFiltering0");
      var0.add("shadowHardwareFiltering1");
      var0.add("shadowtex0Mipmap");
      var0.add("shadowtexMipmap");
      var0.add("shadowtex1Mipmap");
      var0.add("shadowcolor0Mipmap");
      var0.add("shadowColor0Mipmap");
      var0.add("shadowcolor1Mipmap");
      var0.add("shadowColor1Mipmap");
      var0.add("shadowtex0Nearest");
      var0.add("shadowtexNearest");
      var0.add("shadow0MinMagNearest");
      var0.add("shadowtex1Nearest");
      var0.add("shadow1MinMagNearest");
      var0.add("shadowcolor0Nearest");
      var0.add("shadowColor0Nearest");
      var0.add("shadowColor0MinMagNearest");
      var0.add("shadowcolor1Nearest");
      var0.add("shadowColor1Nearest");
      var0.add("shadowColor1MinMagNearest");
      var0.add("wetnessHalflife");
      var0.add("drynessHalflife");
      var0.add("eyeBrightnessHalflife");
      var0.add("centerDepthHalflife");
      var0.add("sunPathRotation");
      var0.add("ambientOcclusionLevel");
      var0.add("superSamplingLevel");
      var0.add("noiseTextureResolution");
      return var0;
   }

   public static Map<String, ShaderOption[]> parseGuiScreens(Properties var0, ShaderProfile[] var1, ShaderOption[] var2) {
      HashMap var3 = new HashMap();
      parseGuiScreen("screen", var0, var3, var1, var2);
      return var3.isEmpty() ? null : var3;
   }

   public static ShaderProfile[] parseProfiles(Properties var0, ShaderOption[] var1) {
      String var2 = "profile.";
      ArrayList var3 = new ArrayList();
      Set var4 = var0.keySet();
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         String var6 = (String)var5.next();
         if (var6.startsWith(var2)) {
            String var7 = var6.substring(var2.length());
            var0.getProperty(var6);
            HashSet var8 = new HashSet();
            ShaderProfile var9 = parseProfile(var7, var0, var8, var1);
            if (var9 != null) {
               var3.add(var9);
            }
         }
      }

      if (var3.size() <= 0) {
         return null;
      } else {
         ShaderProfile[] var10 = (ShaderProfile[])var3.toArray(new ShaderProfile[var3.size()]);
         return var10;
      }
   }
}
