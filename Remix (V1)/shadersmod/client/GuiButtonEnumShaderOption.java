package shadersmod.client;

import net.minecraft.client.gui.*;
import net.minecraft.client.resources.*;

public class GuiButtonEnumShaderOption extends GuiButton
{
    private EnumShaderOption enumShaderOption;
    
    public GuiButtonEnumShaderOption(final EnumShaderOption enumShaderOption, final int x, final int y, final int widthIn, final int heightIn) {
        super(enumShaderOption.ordinal(), x, y, widthIn, heightIn, getButtonText(enumShaderOption));
        this.enumShaderOption = null;
        this.enumShaderOption = enumShaderOption;
    }
    
    private static String getButtonText(final EnumShaderOption eso) {
        final String nameText = I18n.format(eso.getResourceKey(), new Object[0]) + ": ";
        switch (NamelessClass895471824.$SwitchMap$shadersmod$client$EnumShaderOption[eso.ordinal()]) {
            case 1: {
                return nameText + GuiShaders.toStringAa(Shaders.configAntialiasingLevel);
            }
            case 2: {
                return nameText + GuiShaders.toStringOnOff(Shaders.configNormalMap);
            }
            case 3: {
                return nameText + GuiShaders.toStringOnOff(Shaders.configSpecularMap);
            }
            case 4: {
                return nameText + GuiShaders.toStringQuality(Shaders.configRenderResMul);
            }
            case 5: {
                return nameText + GuiShaders.toStringQuality(Shaders.configShadowResMul);
            }
            case 6: {
                return nameText + GuiShaders.toStringHandDepth(Shaders.configHandDepthMul);
            }
            case 7: {
                return nameText + GuiShaders.toStringOnOff(Shaders.configCloudShadow);
            }
            case 8: {
                return nameText + Shaders.configOldLighting.getUserValue();
            }
            case 9: {
                return nameText + GuiShaders.toStringOnOff(Shaders.configShadowClipFrustrum);
            }
            case 10: {
                return nameText + GuiShaders.toStringOnOff(Shaders.configTweakBlockDamage);
            }
            default: {
                return nameText + Shaders.getEnumShaderOption(eso);
            }
        }
    }
    
    public EnumShaderOption getEnumShaderOption() {
        return this.enumShaderOption;
    }
    
    public void updateButtonText() {
        this.displayString = getButtonText(this.enumShaderOption);
    }
    
    static class NamelessClass895471824
    {
        static final int[] $SwitchMap$shadersmod$client$EnumShaderOption;
        
        static {
            $SwitchMap$shadersmod$client$EnumShaderOption = new int[EnumShaderOption.values().length];
            try {
                NamelessClass895471824.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.ANTIALIASING.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {}
            try {
                NamelessClass895471824.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.NORMAL_MAP.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError2) {}
            try {
                NamelessClass895471824.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SPECULAR_MAP.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError3) {}
            try {
                NamelessClass895471824.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.RENDER_RES_MUL.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError4) {}
            try {
                NamelessClass895471824.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SHADOW_RES_MUL.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError5) {}
            try {
                NamelessClass895471824.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.HAND_DEPTH_MUL.ordinal()] = 6;
            }
            catch (NoSuchFieldError noSuchFieldError6) {}
            try {
                NamelessClass895471824.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.CLOUD_SHADOW.ordinal()] = 7;
            }
            catch (NoSuchFieldError noSuchFieldError7) {}
            try {
                NamelessClass895471824.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.OLD_LIGHTING.ordinal()] = 8;
            }
            catch (NoSuchFieldError noSuchFieldError8) {}
            try {
                NamelessClass895471824.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SHADOW_CLIP_FRUSTRUM.ordinal()] = 9;
            }
            catch (NoSuchFieldError noSuchFieldError9) {}
            try {
                NamelessClass895471824.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TWEAK_BLOCK_DAMAGE.ordinal()] = 10;
            }
            catch (NoSuchFieldError noSuchFieldError10) {}
        }
    }
}
