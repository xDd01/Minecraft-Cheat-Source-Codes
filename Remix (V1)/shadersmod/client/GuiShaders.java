package shadersmod.client;

import net.minecraft.client.settings.*;
import net.minecraft.client.resources.*;
import optifine.*;
import java.util.*;
import java.net.*;
import java.io.*;
import org.lwjgl.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.*;

public class GuiShaders extends GuiScreen
{
    public static final int EnumOS_UNKNOWN = 0;
    public static final int EnumOS_WINDOWS = 1;
    public static final int EnumOS_OSX = 2;
    public static final int EnumOS_SOLARIS = 3;
    public static final int EnumOS_LINUX = 4;
    private static float[] QUALITY_MULTIPLIERS;
    private static String[] QUALITY_MULTIPLIER_NAMES;
    private static float[] HAND_DEPTH_VALUES;
    private static String[] HAND_DEPTH_NAMES;
    protected GuiScreen parentGui;
    protected String screenTitle;
    private int updateTimer;
    private GuiSlotShaders shaderList;
    
    public GuiShaders(final GuiScreen par1GuiScreen, final GameSettings par2GameSettings) {
        this.screenTitle = "Shaders";
        this.updateTimer = -1;
        this.parentGui = par1GuiScreen;
    }
    
    public static String toStringOnOff(final boolean value) {
        final String on = Lang.getOn();
        final String off = Lang.getOff();
        return value ? on : off;
    }
    
    public static String toStringAa(final int value) {
        return (value == 2) ? "FXAA 2x" : ((value == 4) ? "FXAA 4x" : Lang.getOff());
    }
    
    public static String toStringValue(final float val, final float[] values, final String[] names) {
        final int index = getValueIndex(val, values);
        return names[index];
    }
    
    public static int getValueIndex(final float val, final float[] values) {
        for (int i = 0; i < values.length; ++i) {
            final float value = values[i];
            if (value >= val) {
                return i;
            }
        }
        return values.length - 1;
    }
    
    public static String toStringQuality(final float val) {
        return toStringValue(val, GuiShaders.QUALITY_MULTIPLIERS, GuiShaders.QUALITY_MULTIPLIER_NAMES);
    }
    
    public static String toStringHandDepth(final float val) {
        return toStringValue(val, GuiShaders.HAND_DEPTH_VALUES, GuiShaders.HAND_DEPTH_NAMES);
    }
    
    public static int getOSType() {
        final String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("win") ? 1 : (osName.contains("mac") ? 2 : (osName.contains("solaris") ? 3 : (osName.contains("sunos") ? 3 : (osName.contains("linux") ? 4 : (osName.contains("unix") ? 4 : 0)))));
    }
    
    @Override
    public void initGui() {
        this.screenTitle = I18n.format("of.options.shadersTitle", new Object[0]);
        if (Shaders.shadersConfig == null) {
            Shaders.loadConfig();
        }
        final byte btnWidth = 120;
        final byte btnHeight = 20;
        final int btnX = GuiShaders.width - btnWidth - 10;
        final byte baseY = 30;
        final byte stepY = 20;
        final int shaderListWidth = GuiShaders.width - btnWidth - 20;
        (this.shaderList = new GuiSlotShaders(this, shaderListWidth, GuiShaders.height, baseY, GuiShaders.height - 50, 16)).registerScrollButtons(7, 8);
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.ANTIALIASING, btnX, 0 * stepY + baseY, btnWidth, btnHeight));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.NORMAL_MAP, btnX, 1 * stepY + baseY, btnWidth, btnHeight));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.SPECULAR_MAP, btnX, 2 * stepY + baseY, btnWidth, btnHeight));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.RENDER_RES_MUL, btnX, 3 * stepY + baseY, btnWidth, btnHeight));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.SHADOW_RES_MUL, btnX, 4 * stepY + baseY, btnWidth, btnHeight));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.HAND_DEPTH_MUL, btnX, 5 * stepY + baseY, btnWidth, btnHeight));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.OLD_LIGHTING, btnX, 6 * stepY + baseY, btnWidth, btnHeight));
        final int btnFolderWidth = Math.min(150, shaderListWidth / 2 - 10);
        this.buttonList.add(new GuiButton(201, shaderListWidth / 4 - btnFolderWidth / 2, GuiShaders.height - 25, btnFolderWidth, btnHeight, Lang.get("of.options.shaders.shadersFolder")));
        this.buttonList.add(new GuiButton(202, shaderListWidth / 4 * 3 - btnFolderWidth / 2, GuiShaders.height - 25, btnFolderWidth, btnHeight, I18n.format("gui.done", new Object[0])));
        this.buttonList.add(new GuiButton(203, btnX, GuiShaders.height - 25, btnWidth, btnHeight, Lang.get("of.options.shaders.shaderOptions")));
        this.updateButtons();
    }
    
    public void updateButtons() {
        final boolean shaderActive = Config.isShaders();
        for (final GuiButton button : this.buttonList) {
            if (button.id != 201 && button.id != 202 && button.id != EnumShaderOption.ANTIALIASING.ordinal()) {
                button.enabled = shaderActive;
            }
        }
    }
    
    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        this.shaderList.func_178039_p();
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) {
        if (button.enabled) {
            if (button instanceof GuiButtonEnumShaderOption) {
                final GuiButtonEnumShaderOption var12 = (GuiButtonEnumShaderOption)button;
                switch (NamelessClass1647571870.$SwitchMap$shadersmod$client$EnumShaderOption[var12.getEnumShaderOption().ordinal()]) {
                    case 1: {
                        Shaders.nextAntialiasingLevel();
                        Shaders.uninit();
                        break;
                    }
                    case 2: {
                        Shaders.configNormalMap = !Shaders.configNormalMap;
                        GuiShaders.mc.func_175603_A();
                        break;
                    }
                    case 3: {
                        Shaders.configSpecularMap = !Shaders.configSpecularMap;
                        GuiShaders.mc.func_175603_A();
                        break;
                    }
                    case 4: {
                        final float var13 = Shaders.configRenderResMul;
                        final float[] var14 = GuiShaders.QUALITY_MULTIPLIERS;
                        final String[] names = GuiShaders.QUALITY_MULTIPLIER_NAMES;
                        int index = getValueIndex(var13, var14);
                        if (isShiftKeyDown()) {
                            if (--index < 0) {
                                index = var14.length - 1;
                            }
                        }
                        else if (++index >= var14.length) {
                            index = 0;
                        }
                        Shaders.configRenderResMul = var14[index];
                        Shaders.scheduleResize();
                        break;
                    }
                    case 5: {
                        final float var13 = Shaders.configShadowResMul;
                        final float[] var14 = GuiShaders.QUALITY_MULTIPLIERS;
                        final String[] names = GuiShaders.QUALITY_MULTIPLIER_NAMES;
                        int index = getValueIndex(var13, var14);
                        if (isShiftKeyDown()) {
                            if (--index < 0) {
                                index = var14.length - 1;
                            }
                        }
                        else if (++index >= var14.length) {
                            index = 0;
                        }
                        Shaders.configShadowResMul = var14[index];
                        Shaders.scheduleResizeShadow();
                        break;
                    }
                    case 6: {
                        final float var13 = Shaders.configHandDepthMul;
                        final float[] var14 = GuiShaders.HAND_DEPTH_VALUES;
                        final String[] names = GuiShaders.HAND_DEPTH_NAMES;
                        int index = getValueIndex(var13, var14);
                        if (isShiftKeyDown()) {
                            if (--index < 0) {
                                index = var14.length - 1;
                            }
                        }
                        else if (++index >= var14.length) {
                            index = 0;
                        }
                        Shaders.configHandDepthMul = var14[index];
                        break;
                    }
                    case 7: {
                        Shaders.configCloudShadow = !Shaders.configCloudShadow;
                        break;
                    }
                    case 8: {
                        Shaders.configOldLighting.nextValue();
                        Shaders.updateBlockLightLevel();
                        GuiShaders.mc.func_175603_A();
                        break;
                    }
                    case 9: {
                        Shaders.configTweakBlockDamage = !Shaders.configTweakBlockDamage;
                        break;
                    }
                    case 10: {
                        Shaders.configTexMinFilB = (Shaders.configTexMinFilB + 1) % 3;
                        Shaders.configTexMinFilN = (Shaders.configTexMinFilS = Shaders.configTexMinFilB);
                        button.displayString = "Tex Min: " + Shaders.texMinFilDesc[Shaders.configTexMinFilB];
                        ShadersTex.updateTextureMinMagFilter();
                        break;
                    }
                    case 11: {
                        Shaders.configTexMagFilN = (Shaders.configTexMagFilN + 1) % 2;
                        button.displayString = "Tex_n Mag: " + Shaders.texMagFilDesc[Shaders.configTexMagFilN];
                        ShadersTex.updateTextureMinMagFilter();
                        break;
                    }
                    case 12: {
                        Shaders.configTexMagFilS = (Shaders.configTexMagFilS + 1) % 2;
                        button.displayString = "Tex_s Mag: " + Shaders.texMagFilDesc[Shaders.configTexMagFilS];
                        ShadersTex.updateTextureMinMagFilter();
                        break;
                    }
                    case 13: {
                        Shaders.configShadowClipFrustrum = !Shaders.configShadowClipFrustrum;
                        button.displayString = "ShadowClipFrustrum: " + toStringOnOff(Shaders.configShadowClipFrustrum);
                        ShadersTex.updateTextureMinMagFilter();
                        break;
                    }
                }
                var12.updateButtonText();
            }
            else {
                switch (button.id) {
                    case 201: {
                        switch (getOSType()) {
                            case 1: {
                                final String gbeso = String.format("cmd.exe /C start \"Open file\" \"%s\"", Shaders.shaderpacksdir.getAbsolutePath());
                                try {
                                    Runtime.getRuntime().exec(gbeso);
                                    return;
                                }
                                catch (IOException var15) {
                                    var15.printStackTrace();
                                    break;
                                }
                            }
                            case 2: {
                                try {
                                    Runtime.getRuntime().exec(new String[] { "/usr/bin/open", Shaders.shaderpacksdir.getAbsolutePath() });
                                    return;
                                }
                                catch (IOException var16) {
                                    var16.printStackTrace();
                                }
                                break;
                            }
                        }
                        boolean var17 = false;
                        try {
                            final Class val = Class.forName("java.awt.Desktop");
                            final Object var18 = val.getMethod("getDesktop", (Class[])new Class[0]).invoke(null, new Object[0]);
                            val.getMethod("browse", URI.class).invoke(var18, new File(GuiShaders.mc.mcDataDir, Shaders.shaderpacksdirname).toURI());
                        }
                        catch (Throwable var19) {
                            var19.printStackTrace();
                            var17 = true;
                        }
                        if (var17) {
                            Config.dbg("Opening via system class!");
                            Sys.openURL("file://" + Shaders.shaderpacksdir.getAbsolutePath());
                            break;
                        }
                        break;
                    }
                    case 202: {
                        new File(Shaders.shadersdir, "current.cfg");
                        try {
                            Shaders.storeConfig();
                        }
                        catch (Exception ex) {}
                        GuiShaders.mc.displayGuiScreen(this.parentGui);
                        break;
                    }
                    case 203: {
                        final GuiShaderOptions values = new GuiShaderOptions(this, Config.getGameSettings());
                        Config.getMinecraft().displayGuiScreen(values);
                        break;
                    }
                    default: {
                        this.shaderList.actionPerformed(button);
                        break;
                    }
                }
            }
        }
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        this.shaderList.drawScreen(mouseX, mouseY, partialTicks);
        if (this.updateTimer <= 0) {
            this.shaderList.updateList();
            this.updateTimer += 20;
        }
        Gui.drawCenteredString(this.fontRendererObj, this.screenTitle + " ", GuiShaders.width / 2, 15, 16777215);
        final String info = "OpenGL: " + Shaders.glVersionString + ", " + Shaders.glVendorString + ", " + Shaders.glRendererString;
        final int infoWidth = this.fontRendererObj.getStringWidth(info);
        if (infoWidth < GuiShaders.width - 5) {
            Gui.drawCenteredString(this.fontRendererObj, info, GuiShaders.width / 2, GuiShaders.height - 40, 8421504);
        }
        else {
            this.drawString(this.fontRendererObj, info, 5, GuiShaders.height - 40, 8421504);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    public void updateScreen() {
        super.updateScreen();
        --this.updateTimer;
    }
    
    public Minecraft getMc() {
        return GuiShaders.mc;
    }
    
    public void drawCenteredString(final String text, final int x, final int y, final int color) {
        Gui.drawCenteredString(this.fontRendererObj, text, x, y, color);
    }
    
    static {
        GuiShaders.QUALITY_MULTIPLIERS = new float[] { 0.5f, 0.70710677f, 1.0f, 1.4142135f, 2.0f };
        GuiShaders.QUALITY_MULTIPLIER_NAMES = new String[] { "0.5x", "0.7x", "1x", "1.5x", "2x" };
        GuiShaders.HAND_DEPTH_VALUES = new float[] { 0.0625f, 0.125f, 0.25f };
        GuiShaders.HAND_DEPTH_NAMES = new String[] { "0.5x", "1x", "2x" };
    }
    
    static class NamelessClass1647571870
    {
        static final int[] $SwitchMap$shadersmod$client$EnumShaderOption;
        
        static {
            $SwitchMap$shadersmod$client$EnumShaderOption = new int[EnumShaderOption.values().length];
            try {
                NamelessClass1647571870.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.ANTIALIASING.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {}
            try {
                NamelessClass1647571870.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.NORMAL_MAP.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError2) {}
            try {
                NamelessClass1647571870.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SPECULAR_MAP.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError3) {}
            try {
                NamelessClass1647571870.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.RENDER_RES_MUL.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError4) {}
            try {
                NamelessClass1647571870.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SHADOW_RES_MUL.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError5) {}
            try {
                NamelessClass1647571870.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.HAND_DEPTH_MUL.ordinal()] = 6;
            }
            catch (NoSuchFieldError noSuchFieldError6) {}
            try {
                NamelessClass1647571870.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.CLOUD_SHADOW.ordinal()] = 7;
            }
            catch (NoSuchFieldError noSuchFieldError7) {}
            try {
                NamelessClass1647571870.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.OLD_LIGHTING.ordinal()] = 8;
            }
            catch (NoSuchFieldError noSuchFieldError8) {}
            try {
                NamelessClass1647571870.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TWEAK_BLOCK_DAMAGE.ordinal()] = 9;
            }
            catch (NoSuchFieldError noSuchFieldError9) {}
            try {
                NamelessClass1647571870.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MIN_FIL_B.ordinal()] = 10;
            }
            catch (NoSuchFieldError noSuchFieldError10) {}
            try {
                NamelessClass1647571870.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MAG_FIL_N.ordinal()] = 11;
            }
            catch (NoSuchFieldError noSuchFieldError11) {}
            try {
                NamelessClass1647571870.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MAG_FIL_S.ordinal()] = 12;
            }
            catch (NoSuchFieldError noSuchFieldError12) {}
            try {
                NamelessClass1647571870.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SHADOW_CLIP_FRUSTRUM.ordinal()] = 13;
            }
            catch (NoSuchFieldError noSuchFieldError13) {}
        }
    }
}
