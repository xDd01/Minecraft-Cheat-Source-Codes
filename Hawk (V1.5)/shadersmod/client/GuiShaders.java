package shadersmod.client;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import optifine.Config;
import optifine.Lang;
import org.lwjgl.Sys;

public class GuiShaders extends GuiScreen {
   public static final int EnumOS_LINUX = 4;
   public static final int EnumOS_WINDOWS = 1;
   public static final int EnumOS_UNKNOWN = 0;
   public static final int EnumOS_OSX = 2;
   private static float[] HAND_DEPTH_VALUES = new float[]{0.0625F, 0.125F, 0.25F};
   private static String[] QUALITY_MULTIPLIER_NAMES = new String[]{"0.5x", "0.7x", "1x", "1.5x", "2x"};
   public static final int EnumOS_SOLARIS = 3;
   private int updateTimer = -1;
   private static float[] QUALITY_MULTIPLIERS = new float[]{0.5F, 0.70710677F, 1.0F, 1.4142135F, 2.0F};
   protected String screenTitle = "Shaders";
   private GuiSlotShaders shaderList;
   private static String[] HAND_DEPTH_NAMES = new String[]{"0.5x", "1x", "2x"};
   protected GuiScreen parentGui;

   public static String toStringValue(float var0, float[] var1, String[] var2) {
      int var3 = getValueIndex(var0, var1);
      return var2[var3];
   }

   public static int getOSType() {
      String var0 = System.getProperty("os.name").toLowerCase();
      return var0.contains("win") ? 1 : (var0.contains("mac") ? 2 : (var0.contains("solaris") ? 3 : (var0.contains("sunos") ? 3 : (var0.contains("linux") ? 4 : (var0.contains("unix") ? 4 : 0)))));
   }

   protected void actionPerformed(GuiButton var1) {
      if (var1.enabled) {
         if (var1 instanceof GuiButtonEnumShaderOption) {
            GuiButtonEnumShaderOption var2 = (GuiButtonEnumShaderOption)var1;
            String[] var3;
            int var4;
            float var5;
            float[] var6;
            switch(var2.getEnumShaderOption()) {
            case ANTIALIASING:
               Shaders.nextAntialiasingLevel();
               Shaders.uninit();
               break;
            case NORMAL_MAP:
               Shaders.configNormalMap = !Shaders.configNormalMap;
               this.mc.func_175603_A();
               break;
            case SPECULAR_MAP:
               Shaders.configSpecularMap = !Shaders.configSpecularMap;
               this.mc.func_175603_A();
               break;
            case RENDER_RES_MUL:
               var5 = Shaders.configRenderResMul;
               var6 = QUALITY_MULTIPLIERS;
               var3 = QUALITY_MULTIPLIER_NAMES;
               var4 = getValueIndex(var5, var6);
               if (isShiftKeyDown()) {
                  --var4;
                  if (var4 < 0) {
                     var4 = var6.length - 1;
                  }
               } else {
                  ++var4;
                  if (var4 >= var6.length) {
                     var4 = 0;
                  }
               }

               Shaders.configRenderResMul = var6[var4];
               Shaders.scheduleResize();
               break;
            case SHADOW_RES_MUL:
               var5 = Shaders.configShadowResMul;
               var6 = QUALITY_MULTIPLIERS;
               var3 = QUALITY_MULTIPLIER_NAMES;
               var4 = getValueIndex(var5, var6);
               if (isShiftKeyDown()) {
                  --var4;
                  if (var4 < 0) {
                     var4 = var6.length - 1;
                  }
               } else {
                  ++var4;
                  if (var4 >= var6.length) {
                     var4 = 0;
                  }
               }

               Shaders.configShadowResMul = var6[var4];
               Shaders.scheduleResizeShadow();
               break;
            case HAND_DEPTH_MUL:
               var5 = Shaders.configHandDepthMul;
               var6 = HAND_DEPTH_VALUES;
               var3 = HAND_DEPTH_NAMES;
               var4 = getValueIndex(var5, var6);
               if (isShiftKeyDown()) {
                  --var4;
                  if (var4 < 0) {
                     var4 = var6.length - 1;
                  }
               } else {
                  ++var4;
                  if (var4 >= var6.length) {
                     var4 = 0;
                  }
               }

               Shaders.configHandDepthMul = var6[var4];
               break;
            case CLOUD_SHADOW:
               Shaders.configCloudShadow = !Shaders.configCloudShadow;
               break;
            case OLD_LIGHTING:
               Shaders.configOldLighting.nextValue();
               Shaders.updateBlockLightLevel();
               this.mc.func_175603_A();
               break;
            case TWEAK_BLOCK_DAMAGE:
               Shaders.configTweakBlockDamage = !Shaders.configTweakBlockDamage;
               break;
            case TEX_MIN_FIL_B:
               Shaders.configTexMinFilB = (Shaders.configTexMinFilB + 1) % 3;
               Shaders.configTexMinFilN = Shaders.configTexMinFilS = Shaders.configTexMinFilB;
               var1.displayString = String.valueOf((new StringBuilder("Tex Min: ")).append(Shaders.texMinFilDesc[Shaders.configTexMinFilB]));
               ShadersTex.updateTextureMinMagFilter();
               break;
            case TEX_MAG_FIL_N:
               Shaders.configTexMagFilN = (Shaders.configTexMagFilN + 1) % 2;
               var1.displayString = String.valueOf((new StringBuilder("Tex_n Mag: ")).append(Shaders.texMagFilDesc[Shaders.configTexMagFilN]));
               ShadersTex.updateTextureMinMagFilter();
               break;
            case TEX_MAG_FIL_S:
               Shaders.configTexMagFilS = (Shaders.configTexMagFilS + 1) % 2;
               var1.displayString = String.valueOf((new StringBuilder("Tex_s Mag: ")).append(Shaders.texMagFilDesc[Shaders.configTexMagFilS]));
               ShadersTex.updateTextureMinMagFilter();
               break;
            case SHADOW_CLIP_FRUSTRUM:
               Shaders.configShadowClipFrustrum = !Shaders.configShadowClipFrustrum;
               var1.displayString = String.valueOf((new StringBuilder("ShadowClipFrustrum: ")).append(toStringOnOff(Shaders.configShadowClipFrustrum)));
               ShadersTex.updateTextureMinMagFilter();
            }

            var2.updateButtonText();
         } else {
            switch(var1.id) {
            case 201:
               switch(getOSType()) {
               case 1:
                  String var11 = String.format("cmd.exe /C start \"Open file\" \"%s\"", Shaders.shaderpacksdir.getAbsolutePath());

                  try {
                     Runtime.getRuntime().exec(var11);
                     return;
                  } catch (IOException var10) {
                     var10.printStackTrace();
                     break;
                  }
               case 2:
                  try {
                     Runtime.getRuntime().exec(new String[]{"/usr/bin/open", Shaders.shaderpacksdir.getAbsolutePath()});
                     return;
                  } catch (IOException var9) {
                     var9.printStackTrace();
                  }
               }

               boolean var12 = false;

               try {
                  Class var14 = Class.forName("java.awt.Desktop");
                  Object var15 = var14.getMethod("getDesktop").invoke((Object)null);
                  var14.getMethod("browse", URI.class).invoke(var15, (new File(this.mc.mcDataDir, Shaders.shaderpacksdirname)).toURI());
               } catch (Throwable var8) {
                  var8.printStackTrace();
                  var12 = true;
               }

               if (var12) {
                  Config.dbg("Opening via system class!");
                  Sys.openURL(String.valueOf((new StringBuilder("file://")).append(Shaders.shaderpacksdir.getAbsolutePath())));
               }
               break;
            case 202:
               new File(Shaders.shadersdir, "current.cfg");

               try {
                  Shaders.storeConfig();
               } catch (Exception var7) {
               }

               this.mc.displayGuiScreen(this.parentGui);
               break;
            case 203:
               GuiShaderOptions var13 = new GuiShaderOptions(this, Config.getGameSettings());
               Config.getMinecraft().displayGuiScreen(var13);
               break;
            default:
               this.shaderList.actionPerformed(var1);
            }
         }
      }

   }

   public static int getValueIndex(float var0, float[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         float var3 = var1[var2];
         if (var3 >= var0) {
            return var2;
         }
      }

      return var1.length - 1;
   }

   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.shaderList.drawScreen(var1, var2, var3);
      if (this.updateTimer <= 0) {
         this.shaderList.updateList();
         this.updateTimer += 20;
      }

      this.drawCenteredString(this.fontRendererObj, String.valueOf((new StringBuilder(String.valueOf(this.screenTitle))).append(" ")), this.width / 2, 15, 16777215);
      String var4 = String.valueOf((new StringBuilder("OpenGL: ")).append(Shaders.glVersionString).append(", ").append(Shaders.glVendorString).append(", ").append(Shaders.glRendererString));
      int var5 = this.fontRendererObj.getStringWidth(var4);
      if (var5 < this.width - 5) {
         this.drawCenteredString(this.fontRendererObj, var4, this.width / 2, this.height - 40, 8421504);
      } else {
         this.drawString(this.fontRendererObj, var4, 5, this.height - 40, 8421504);
      }

      super.drawScreen(var1, var2, var3);
   }

   public Minecraft getMc() {
      return this.mc;
   }

   public void drawCenteredString(String var1, int var2, int var3, int var4) {
      this.drawCenteredString(this.fontRendererObj, var1, var2, var3, var4);
   }

   public void updateScreen() {
      super.updateScreen();
      --this.updateTimer;
   }

   public GuiShaders(GuiScreen var1, GameSettings var2) {
      this.parentGui = var1;
   }

   public static String toStringAa(int var0) {
      return var0 == 2 ? "FXAA 2x" : (var0 == 4 ? "FXAA 4x" : Lang.getOff());
   }

   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.shaderList.func_178039_p();
   }

   public void initGui() {
      this.screenTitle = I18n.format("of.options.shadersTitle");
      if (Shaders.shadersConfig == null) {
         Shaders.loadConfig();
      }

      byte var1 = 120;
      byte var2 = 20;
      int var3 = this.width - var1 - 10;
      byte var4 = 30;
      byte var5 = 20;
      int var6 = this.width - var1 - 20;
      this.shaderList = new GuiSlotShaders(this, var6, this.height, var4, this.height - 50, 16);
      this.shaderList.registerScrollButtons(7, 8);
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.ANTIALIASING, var3, 0 * var5 + var4, var1, var2));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.NORMAL_MAP, var3, 1 * var5 + var4, var1, var2));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.SPECULAR_MAP, var3, 2 * var5 + var4, var1, var2));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.RENDER_RES_MUL, var3, 3 * var5 + var4, var1, var2));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.SHADOW_RES_MUL, var3, 4 * var5 + var4, var1, var2));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.HAND_DEPTH_MUL, var3, 5 * var5 + var4, var1, var2));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.OLD_LIGHTING, var3, 6 * var5 + var4, var1, var2));
      int var7 = Math.min(150, var6 / 2 - 10);
      this.buttonList.add(new GuiButton(201, var6 / 4 - var7 / 2, this.height - 25, var7, var2, Lang.get("of.options.shaders.shadersFolder")));
      this.buttonList.add(new GuiButton(202, var6 / 4 * 3 - var7 / 2, this.height - 25, var7, var2, I18n.format("gui.done")));
      this.buttonList.add(new GuiButton(203, var3, this.height - 25, var1, var2, Lang.get("of.options.shaders.shaderOptions")));
      this.updateButtons();
   }

   public static String toStringQuality(float var0) {
      return toStringValue(var0, QUALITY_MULTIPLIERS, QUALITY_MULTIPLIER_NAMES);
   }

   public static String toStringOnOff(boolean var0) {
      String var1 = Lang.getOn();
      String var2 = Lang.getOff();
      return var0 ? var1 : var2;
   }

   public static String toStringHandDepth(float var0) {
      return toStringValue(var0, HAND_DEPTH_VALUES, HAND_DEPTH_NAMES);
   }

   public void updateButtons() {
      boolean var1 = Config.isShaders();
      Iterator var2 = this.buttonList.iterator();

      while(var2.hasNext()) {
         GuiButton var3 = (GuiButton)var2.next();
         if (var3.id != 201 && var3.id != 202 && var3.id != EnumShaderOption.ANTIALIASING.ordinal()) {
            var3.enabled = var1;
         }
      }

   }

   static class NamelessClass1647571870 {
      static final int[] $SwitchMap$shadersmod$client$EnumShaderOption = new int[EnumShaderOption.values().length];

      static {
         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.ANTIALIASING.ordinal()] = 1;
         } catch (NoSuchFieldError var13) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.NORMAL_MAP.ordinal()] = 2;
         } catch (NoSuchFieldError var12) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SPECULAR_MAP.ordinal()] = 3;
         } catch (NoSuchFieldError var11) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.RENDER_RES_MUL.ordinal()] = 4;
         } catch (NoSuchFieldError var10) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SHADOW_RES_MUL.ordinal()] = 5;
         } catch (NoSuchFieldError var9) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.HAND_DEPTH_MUL.ordinal()] = 6;
         } catch (NoSuchFieldError var8) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.CLOUD_SHADOW.ordinal()] = 7;
         } catch (NoSuchFieldError var7) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.OLD_LIGHTING.ordinal()] = 8;
         } catch (NoSuchFieldError var6) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TWEAK_BLOCK_DAMAGE.ordinal()] = 9;
         } catch (NoSuchFieldError var5) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MIN_FIL_B.ordinal()] = 10;
         } catch (NoSuchFieldError var4) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MAG_FIL_N.ordinal()] = 11;
         } catch (NoSuchFieldError var3) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MAG_FIL_S.ordinal()] = 12;
         } catch (NoSuchFieldError var2) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SHADOW_CLIP_FRUSTRUM.ordinal()] = 13;
         } catch (NoSuchFieldError var1) {
         }

      }
   }
}
