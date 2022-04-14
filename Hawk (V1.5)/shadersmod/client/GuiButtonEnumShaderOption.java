package shadersmod.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class GuiButtonEnumShaderOption extends GuiButton {
   private EnumShaderOption enumShaderOption = null;

   public EnumShaderOption getEnumShaderOption() {
      return this.enumShaderOption;
   }

   public void updateButtonText() {
      this.displayString = getButtonText(this.enumShaderOption);
   }

   public GuiButtonEnumShaderOption(EnumShaderOption var1, int var2, int var3, int var4, int var5) {
      super(var1.ordinal(), var2, var3, var4, var5, getButtonText(var1));
      this.enumShaderOption = var1;
   }

   private static String getButtonText(EnumShaderOption var0) {
      String var1 = String.valueOf((new StringBuilder(String.valueOf(I18n.format(var0.getResourceKey())))).append(": "));
      switch(var0) {
      case ANTIALIASING:
         return String.valueOf((new StringBuilder(String.valueOf(var1))).append(GuiShaders.toStringAa(Shaders.configAntialiasingLevel)));
      case NORMAL_MAP:
         return String.valueOf((new StringBuilder(String.valueOf(var1))).append(GuiShaders.toStringOnOff(Shaders.configNormalMap)));
      case SPECULAR_MAP:
         return String.valueOf((new StringBuilder(String.valueOf(var1))).append(GuiShaders.toStringOnOff(Shaders.configSpecularMap)));
      case RENDER_RES_MUL:
         return String.valueOf((new StringBuilder(String.valueOf(var1))).append(GuiShaders.toStringQuality(Shaders.configRenderResMul)));
      case SHADOW_RES_MUL:
         return String.valueOf((new StringBuilder(String.valueOf(var1))).append(GuiShaders.toStringQuality(Shaders.configShadowResMul)));
      case HAND_DEPTH_MUL:
         return String.valueOf((new StringBuilder(String.valueOf(var1))).append(GuiShaders.toStringHandDepth(Shaders.configHandDepthMul)));
      case CLOUD_SHADOW:
         return String.valueOf((new StringBuilder(String.valueOf(var1))).append(GuiShaders.toStringOnOff(Shaders.configCloudShadow)));
      case OLD_LIGHTING:
         return String.valueOf((new StringBuilder(String.valueOf(var1))).append(Shaders.configOldLighting.getUserValue()));
      case SHADOW_CLIP_FRUSTRUM:
         return String.valueOf((new StringBuilder(String.valueOf(var1))).append(GuiShaders.toStringOnOff(Shaders.configShadowClipFrustrum)));
      case TWEAK_BLOCK_DAMAGE:
         return String.valueOf((new StringBuilder(String.valueOf(var1))).append(GuiShaders.toStringOnOff(Shaders.configTweakBlockDamage)));
      default:
         return String.valueOf((new StringBuilder(String.valueOf(var1))).append(Shaders.getEnumShaderOption(var0)));
      }
   }

   static class NamelessClass895471824 {
      static final int[] $SwitchMap$shadersmod$client$EnumShaderOption = new int[EnumShaderOption.values().length];

      static {
         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.ANTIALIASING.ordinal()] = 1;
         } catch (NoSuchFieldError var10) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.NORMAL_MAP.ordinal()] = 2;
         } catch (NoSuchFieldError var9) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SPECULAR_MAP.ordinal()] = 3;
         } catch (NoSuchFieldError var8) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.RENDER_RES_MUL.ordinal()] = 4;
         } catch (NoSuchFieldError var7) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SHADOW_RES_MUL.ordinal()] = 5;
         } catch (NoSuchFieldError var6) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.HAND_DEPTH_MUL.ordinal()] = 6;
         } catch (NoSuchFieldError var5) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.CLOUD_SHADOW.ordinal()] = 7;
         } catch (NoSuchFieldError var4) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.OLD_LIGHTING.ordinal()] = 8;
         } catch (NoSuchFieldError var3) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SHADOW_CLIP_FRUSTRUM.ordinal()] = 9;
         } catch (NoSuchFieldError var2) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TWEAK_BLOCK_DAMAGE.ordinal()] = 10;
         } catch (NoSuchFieldError var1) {
         }

      }
   }
}
