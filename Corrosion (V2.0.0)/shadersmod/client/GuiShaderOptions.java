/*
 * Decompiled with CFR 0.152.
 */
package shadersmod.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import optifine.Config;
import optifine.GuiScreenOF;
import optifine.Lang;
import optifine.StrUtils;
import shadersmod.client.GuiButtonShaderOption;
import shadersmod.client.ShaderOption;
import shadersmod.client.ShaderOptionProfile;
import shadersmod.client.ShaderOptionScreen;
import shadersmod.client.Shaders;

public class GuiShaderOptions
extends GuiScreenOF {
    private GuiScreen prevScreen;
    protected String title = "Shader Options";
    private GameSettings settings;
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private long mouseStillTime = 0L;
    private String screenName = null;
    private String screenText = null;
    private boolean changed = false;
    public static final String OPTION_PROFILE = "<profile>";
    public static final String OPTION_EMPTY = "<empty>";
    public static final String OPTION_REST = "*";

    public GuiShaderOptions(GuiScreen guiscreen, GameSettings gamesettings) {
        this.prevScreen = guiscreen;
        this.settings = gamesettings;
    }

    public GuiShaderOptions(GuiScreen guiscreen, GameSettings gamesettings, String screenName) {
        this(guiscreen, gamesettings);
        this.screenName = screenName;
        if (screenName != null) {
            this.screenText = Shaders.translate("screen." + screenName, screenName);
        }
    }

    @Override
    public void initGui() {
        this.title = I18n.format("of.options.shaderOptionsTitle", new Object[0]);
        int i2 = 100;
        int j2 = 0;
        int k2 = 30;
        int l2 = 20;
        int i1 = this.width - 130;
        int j1 = 120;
        int k1 = 20;
        int l1 = 2;
        ShaderOption[] ashaderoption = Shaders.getShaderPackOptions(this.screenName);
        if (ashaderoption != null) {
            if (ashaderoption.length > 18) {
                l1 = ashaderoption.length / 9 + 1;
            }
            for (int i22 = 0; i22 < ashaderoption.length; ++i22) {
                ShaderOption shaderoption = ashaderoption[i22];
                if (shaderoption == null || !shaderoption.isVisible()) continue;
                int j22 = i22 % l1;
                int k22 = i22 / l1;
                int l22 = Math.min(this.width / l1, 200);
                j2 = (this.width - l22 * l1) / 2;
                int i3 = j22 * l22 + 5 + j2;
                int j3 = k2 + k22 * l2;
                int k3 = l22 - 10;
                String s2 = this.getButtonText(shaderoption, k3);
                GuiButtonShaderOption guibuttonshaderoption = new GuiButtonShaderOption(i2 + i22, i3, j3, k3, k1, shaderoption, s2);
                guibuttonshaderoption.enabled = shaderoption.isEnabled();
                this.buttonList.add(guibuttonshaderoption);
            }
        }
        this.buttonList.add(new GuiButton(201, this.width / 2 - j1 - 20, this.height / 6 + 168 + 11, j1, k1, I18n.format("controls.reset", new Object[0])));
        this.buttonList.add(new GuiButton(200, this.width / 2 + 20, this.height / 6 + 168 + 11, j1, k1, I18n.format("gui.done", new Object[0])));
    }

    private String getButtonText(ShaderOption so2, int btnWidth) {
        String s2 = so2.getNameText();
        if (so2 instanceof ShaderOptionScreen) {
            ShaderOptionScreen shaderoptionscreen = (ShaderOptionScreen)so2;
            return s2 + "...";
        }
        FontRenderer fontrenderer = Config.getMinecraft().fontRendererObj;
        int i2 = fontrenderer.getStringWidth(": " + Lang.getOff()) + 5;
        while (fontrenderer.getStringWidth(s2) + i2 >= btnWidth && s2.length() > 0) {
            s2 = s2.substring(0, s2.length() - 1);
        }
        String s1 = so2.isChanged() ? so2.getValueColor(so2.getValue()) : "";
        String s22 = so2.getValueText(so2.getValue());
        return s2 + ": " + s1 + s22;
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.enabled) {
            if (guibutton.id < 200 && guibutton instanceof GuiButtonShaderOption) {
                GuiButtonShaderOption guibuttonshaderoption = (GuiButtonShaderOption)guibutton;
                ShaderOption shaderoption = guibuttonshaderoption.getShaderOption();
                if (shaderoption instanceof ShaderOptionScreen) {
                    String s2 = shaderoption.getName();
                    GuiShaderOptions guishaderoptions = new GuiShaderOptions(this, this.settings, s2);
                    this.mc.displayGuiScreen(guishaderoptions);
                    return;
                }
                if (GuiShaderOptions.isShiftKeyDown()) {
                    shaderoption.resetValue();
                } else {
                    shaderoption.nextValue();
                }
                this.updateAllButtons();
                this.changed = true;
            }
            if (guibutton.id == 201) {
                ShaderOption[] ashaderoption = Shaders.getChangedOptions(Shaders.getShaderPackOptions());
                for (int i2 = 0; i2 < ashaderoption.length; ++i2) {
                    ShaderOption shaderoption1 = ashaderoption[i2];
                    shaderoption1.resetValue();
                    this.changed = true;
                }
                this.updateAllButtons();
            }
            if (guibutton.id == 200) {
                if (this.changed) {
                    Shaders.saveShaderPackOptions();
                    this.changed = false;
                    Shaders.uninit();
                }
                this.mc.displayGuiScreen(this.prevScreen);
            }
        }
    }

    @Override
    protected void actionPerformedRightClick(GuiButton btn) {
        if (btn instanceof GuiButtonShaderOption) {
            GuiButtonShaderOption guibuttonshaderoption = (GuiButtonShaderOption)btn;
            ShaderOption shaderoption = guibuttonshaderoption.getShaderOption();
            if (GuiShaderOptions.isShiftKeyDown()) {
                shaderoption.resetValue();
            } else {
                shaderoption.prevValue();
            }
            this.updateAllButtons();
            this.changed = true;
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        if (this.changed) {
            Shaders.saveShaderPackOptions();
            this.changed = false;
            Shaders.uninit();
        }
    }

    private void updateAllButtons() {
        for (GuiButton guibutton : this.buttonList) {
            if (!(guibutton instanceof GuiButtonShaderOption)) continue;
            GuiButtonShaderOption guibuttonshaderoption = (GuiButtonShaderOption)guibutton;
            ShaderOption shaderoption = guibuttonshaderoption.getShaderOption();
            if (shaderoption instanceof ShaderOptionProfile) {
                ShaderOptionProfile shaderoptionprofile = (ShaderOptionProfile)shaderoption;
                shaderoptionprofile.updateProfile();
            }
            guibuttonshaderoption.displayString = this.getButtonText(shaderoption, guibuttonshaderoption.getButtonWidth());
        }
    }

    @Override
    public void drawScreen(int x2, int y2, float f2) {
        this.drawDefaultBackground();
        if (this.screenText != null) {
            this.drawCenteredString(this.fontRendererObj, this.screenText, this.width / 2, 15, 0xFFFFFF);
        } else {
            this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 15, 0xFFFFFF);
        }
        super.drawScreen(x2, y2, f2);
        if (Math.abs(x2 - this.lastMouseX) <= 5 && Math.abs(y2 - this.lastMouseY) <= 5) {
            this.drawTooltips(x2, y2, this.buttonList);
        } else {
            this.lastMouseX = x2;
            this.lastMouseY = y2;
            this.mouseStillTime = System.currentTimeMillis();
        }
    }

    private void drawTooltips(int x2, int y2, List buttons) {
        int i2 = 700;
        if (System.currentTimeMillis() >= this.mouseStillTime + (long)i2) {
            int j2 = this.width / 2 - 150;
            int k2 = this.height / 6 - 7;
            if (y2 <= k2 + 98) {
                k2 += 105;
            }
            int l2 = j2 + 150 + 150;
            int i1 = k2 + 84 + 10;
            GuiButton guibutton = GuiShaderOptions.getSelectedButton(buttons, x2, y2);
            if (guibutton instanceof GuiButtonShaderOption) {
                GuiButtonShaderOption guibuttonshaderoption = (GuiButtonShaderOption)guibutton;
                ShaderOption shaderoption = guibuttonshaderoption.getShaderOption();
                String[] astring = this.makeTooltipLines(shaderoption, l2 - j2);
                if (astring == null) {
                    return;
                }
                this.drawGradientRect(j2, k2, l2, i1, -536870912, -536870912);
                for (int j1 = 0; j1 < astring.length; ++j1) {
                    String s2 = astring[j1];
                    int k1 = 0xDDDDDD;
                    if (s2.endsWith("!")) {
                        k1 = 0xFF2020;
                    }
                    this.fontRendererObj.drawStringWithShadow(s2, j2 + 5, k2 + 5 + j1 * 11, k1);
                }
            }
        }
    }

    private String[] makeTooltipLines(ShaderOption so2, int width) {
        if (so2 instanceof ShaderOptionProfile) {
            return null;
        }
        String s2 = so2.getNameText();
        String s1 = Config.normalize(so2.getDescriptionText()).trim();
        String[] astring = this.splitDescription(s1);
        String s22 = null;
        if (!s2.equals(so2.getName()) && this.settings.advancedItemTooltips) {
            s22 = "\u00a78" + Lang.get("of.general.id") + ": " + so2.getName();
        }
        String s3 = null;
        if (so2.getPaths() != null && this.settings.advancedItemTooltips) {
            s3 = "\u00a78" + Lang.get("of.general.from") + ": " + Config.arrayToString(so2.getPaths());
        }
        String s4 = null;
        if (so2.getValueDefault() != null && this.settings.advancedItemTooltips) {
            String s5 = so2.isEnabled() ? so2.getValueText(so2.getValueDefault()) : Lang.get("of.general.ambiguous");
            s4 = "\u00a78" + Lang.getDefault() + ": " + s5;
        }
        ArrayList<String> list = new ArrayList<String>();
        list.add(s2);
        list.addAll(Arrays.asList(astring));
        if (s22 != null) {
            list.add(s22);
        }
        if (s3 != null) {
            list.add(s3);
        }
        if (s4 != null) {
            list.add(s4);
        }
        String[] astring1 = this.makeTooltipLines(width, list);
        return astring1;
    }

    private String[] splitDescription(String desc) {
        if (desc.length() <= 0) {
            return new String[0];
        }
        desc = StrUtils.removePrefix(desc, "//");
        String[] astring = desc.split("\\. ");
        for (int i2 = 0; i2 < astring.length; ++i2) {
            astring[i2] = "- " + astring[i2].trim();
            astring[i2] = StrUtils.removeSuffix(astring[i2], ".");
        }
        return astring;
    }

    private String[] makeTooltipLines(int width, List<String> args) {
        FontRenderer fontrenderer = Config.getMinecraft().fontRendererObj;
        ArrayList<String> list = new ArrayList<String>();
        for (int i2 = 0; i2 < args.size(); ++i2) {
            String s2 = args.get(i2);
            if (s2 == null || s2.length() <= 0) continue;
            for (Object s1 : fontrenderer.listFormattedStringToWidth(s2, width)) {
                list.add((String)s1);
            }
        }
        String[] astring = list.toArray(new String[list.size()]);
        return astring;
    }
}

