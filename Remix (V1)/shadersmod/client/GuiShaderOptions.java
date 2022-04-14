package shadersmod.client;

import net.minecraft.client.settings.*;
import net.minecraft.client.resources.*;
import java.util.*;
import optifine.*;
import net.minecraft.client.gui.*;

public class GuiShaderOptions extends GuiScreen
{
    public static final String OPTION_PROFILE = "<profile>";
    public static final String OPTION_EMPTY = "<empty>";
    public static final String OPTION_REST = "*";
    protected String title;
    private GuiScreen prevScreen;
    private GameSettings settings;
    private int lastMouseX;
    private int lastMouseY;
    private long mouseStillTime;
    private String screenName;
    private String screenText;
    private boolean changed;
    
    public GuiShaderOptions(final GuiScreen guiscreen, final GameSettings gamesettings) {
        this.lastMouseX = 0;
        this.lastMouseY = 0;
        this.mouseStillTime = 0L;
        this.screenName = null;
        this.screenText = null;
        this.changed = false;
        this.title = "Shader Options";
        this.prevScreen = guiscreen;
        this.settings = gamesettings;
    }
    
    public GuiShaderOptions(final GuiScreen guiscreen, final GameSettings gamesettings, final String screenName) {
        this(guiscreen, gamesettings);
        this.screenName = screenName;
        if (screenName != null) {
            this.screenText = Shaders.translate("screen." + screenName, screenName);
        }
    }
    
    @Override
    public void initGui() {
        this.title = I18n.format("of.options.shaderOptionsTitle", new Object[0]);
        final byte baseId = 100;
        final boolean baseX = false;
        final byte baseY = 30;
        final byte stepY = 20;
        final int btnX = GuiShaderOptions.width - 130;
        final byte btnWidth = 120;
        final byte btnHeight = 20;
        int columns = 2;
        final ShaderOption[] ops = Shaders.getShaderPackOptions(this.screenName);
        if (ops != null) {
            if (ops.length > 18) {
                columns = ops.length / 9 + 1;
            }
            for (int i = 0; i < ops.length; ++i) {
                final ShaderOption so = ops[i];
                if (so != null && so.isVisible()) {
                    final int col = i % columns;
                    final int row = i / columns;
                    final int colWidth = Math.min(GuiShaderOptions.width / columns, 200);
                    final int var21 = (GuiShaderOptions.width - colWidth * columns) / 2;
                    final int x = col * colWidth + 5 + var21;
                    final int y = baseY + row * stepY;
                    final int w = colWidth - 10;
                    final String text = this.getButtonText(so, w);
                    final GuiButtonShaderOption btn = new GuiButtonShaderOption(baseId + i, x, y, w, btnHeight, so, text);
                    btn.enabled = so.isEnabled();
                    this.buttonList.add(btn);
                }
            }
        }
        this.buttonList.add(new GuiButton(201, GuiShaderOptions.width / 2 - btnWidth - 20, GuiShaderOptions.height / 6 + 168 + 11, btnWidth, btnHeight, I18n.format("controls.reset", new Object[0])));
        this.buttonList.add(new GuiButton(200, GuiShaderOptions.width / 2 + 20, GuiShaderOptions.height / 6 + 168 + 11, btnWidth, btnHeight, I18n.format("gui.done", new Object[0])));
    }
    
    private String getButtonText(final ShaderOption so, final int btnWidth) {
        String labelName = so.getNameText();
        if (so instanceof ShaderOptionScreen) {
            final ShaderOptionScreen fr1 = (ShaderOptionScreen)so;
            return labelName + "...";
        }
        final FontRenderer fr2 = Config.getMinecraft().fontRendererObj;
        for (int lenSuffix = fr2.getStringWidth(": " + Lang.getOff()) + 5; fr2.getStringWidth(labelName) + lenSuffix >= btnWidth && labelName.length() > 0; labelName = labelName.substring(0, labelName.length() - 1)) {}
        final String col = so.isChanged() ? so.getValueColor(so.getValue()) : "";
        final String labelValue = so.getValueText(so.getValue());
        return labelName + ": " + col + labelValue;
    }
    
    @Override
    protected void actionPerformed(final GuiButton guibutton) {
        if (guibutton.enabled) {
            if (guibutton.id < 200 && guibutton instanceof GuiButtonShaderOption) {
                final GuiButtonShaderOption opts = (GuiButtonShaderOption)guibutton;
                final ShaderOption i = opts.getShaderOption();
                if (i instanceof ShaderOptionScreen) {
                    final String var8 = i.getName();
                    final GuiShaderOptions scr = new GuiShaderOptions(this, this.settings, var8);
                    GuiShaderOptions.mc.displayGuiScreen(scr);
                    return;
                }
                i.nextValue();
                this.updateAllButtons();
                this.changed = true;
            }
            if (guibutton.id == 201) {
                final ShaderOption[] var9 = Shaders.getChangedOptions(Shaders.getShaderPackOptions());
                for (int var10 = 0; var10 < var9.length; ++var10) {
                    final ShaderOption opt = var9[var10];
                    opt.resetValue();
                    this.changed = true;
                }
                this.updateAllButtons();
            }
            if (guibutton.id == 200) {
                if (this.changed) {
                    Shaders.saveShaderPackOptions();
                    Shaders.uninit();
                }
                GuiShaderOptions.mc.displayGuiScreen(this.prevScreen);
            }
        }
    }
    
    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 1) {
            final GuiButton btn = this.getSelectedButton(mouseX, mouseY);
            if (btn instanceof GuiButtonShaderOption) {
                final GuiButtonShaderOption btnSo = (GuiButtonShaderOption)btn;
                final ShaderOption so = btnSo.getShaderOption();
                if (so.isChanged()) {
                    btnSo.playPressSound(GuiShaderOptions.mc.getSoundHandler());
                    so.resetValue();
                    this.changed = true;
                    this.updateAllButtons();
                }
            }
        }
    }
    
    private void updateAllButtons() {
        for (final GuiButton btn : this.buttonList) {
            if (btn instanceof GuiButtonShaderOption) {
                final GuiButtonShaderOption gbso = (GuiButtonShaderOption)btn;
                final ShaderOption opt = gbso.getShaderOption();
                if (opt instanceof ShaderOptionProfile) {
                    final ShaderOptionProfile optProf = (ShaderOptionProfile)opt;
                    optProf.updateProfile();
                }
                gbso.displayString = this.getButtonText(opt, gbso.getButtonWidth());
            }
        }
    }
    
    @Override
    public void drawScreen(final int x, final int y, final float f) {
        this.drawDefaultBackground();
        if (this.screenText != null) {
            Gui.drawCenteredString(this.fontRendererObj, this.screenText, GuiShaderOptions.width / 2, 15, 16777215);
        }
        else {
            Gui.drawCenteredString(this.fontRendererObj, this.title, GuiShaderOptions.width / 2, 15, 16777215);
        }
        super.drawScreen(x, y, f);
        if (Math.abs(x - this.lastMouseX) <= 5 && Math.abs(y - this.lastMouseY) <= 5) {
            this.drawTooltips(x, y, this.buttonList);
        }
        else {
            this.lastMouseX = x;
            this.lastMouseY = y;
            this.mouseStillTime = System.currentTimeMillis();
        }
    }
    
    private void drawTooltips(final int x, final int y, final List buttonList) {
        final short activateDelay = 700;
        if (System.currentTimeMillis() >= this.mouseStillTime + activateDelay) {
            final int x2 = GuiShaderOptions.width / 2 - 150;
            int y2 = GuiShaderOptions.height / 6 - 7;
            if (y <= y2 + 98) {
                y2 += 105;
            }
            final int x3 = x2 + 150 + 150;
            final int y3 = y2 + 84 + 10;
            final GuiButton btn = this.getSelectedButton(x, y);
            if (btn instanceof GuiButtonShaderOption) {
                final GuiButtonShaderOption btnSo = (GuiButtonShaderOption)btn;
                final ShaderOption so = btnSo.getShaderOption();
                final String[] lines = this.makeTooltipLines(so, x3 - x2);
                if (lines == null) {
                    return;
                }
                this.drawGradientRect(x2, y2, x3, y3, -536870912, -536870912);
                for (int i = 0; i < lines.length; ++i) {
                    final String line = lines[i];
                    int col = 14540253;
                    if (line.endsWith("!")) {
                        col = 16719904;
                    }
                    this.fontRendererObj.func_175063_a(line, (float)(x2 + 5), (float)(y2 + 5 + i * 11), col);
                }
            }
        }
    }
    
    private String[] makeTooltipLines(final ShaderOption so, final int width) {
        if (so instanceof ShaderOptionProfile) {
            return null;
        }
        final String name = so.getNameText();
        final String desc = Config.normalize(so.getDescriptionText()).trim();
        final String[] descs = this.splitDescription(desc);
        String id = null;
        if (!name.equals(so.getName())) {
            id = Lang.get("of.general.id") + ": " + so.getName();
        }
        String source = null;
        if (so.getPaths() != null) {
            source = Lang.get("of.general.from") + ": " + Config.arrayToString(so.getPaths());
        }
        String def = null;
        if (so.getValueDefault() != null) {
            final String list = so.isEnabled() ? so.getValueText(so.getValueDefault()) : Lang.get("of.general.ambiguous");
            def = Lang.getDefault() + ": " + list;
        }
        final ArrayList list2 = new ArrayList();
        list2.add(name);
        list2.addAll(Arrays.asList(descs));
        if (id != null) {
            list2.add(id);
        }
        if (source != null) {
            list2.add(source);
        }
        if (def != null) {
            list2.add(def);
        }
        final String[] lines = this.makeTooltipLines(width, list2);
        return lines;
    }
    
    private String[] splitDescription(String desc) {
        if (desc.length() <= 0) {
            return new String[0];
        }
        desc = StrUtils.removePrefix(desc, "//");
        final String[] descs = desc.split("\\. ");
        for (int i = 0; i < descs.length; ++i) {
            descs[i] = "- " + descs[i].trim();
            descs[i] = StrUtils.removeSuffix(descs[i], ".");
        }
        return descs;
    }
    
    private String[] makeTooltipLines(final int width, final List<String> args) {
        final FontRenderer fr = Config.getMinecraft().fontRendererObj;
        final ArrayList list = new ArrayList();
        for (int lines = 0; lines < args.size(); ++lines) {
            final String arg = args.get(lines);
            if (arg != null && arg.length() > 0) {
                final List parts = fr.listFormattedStringToWidth(arg, width);
                for (final String part : parts) {
                    list.add(part);
                }
            }
        }
        final String[] var10 = list.toArray(new String[list.size()]);
        return var10;
    }
    
    private GuiButton getSelectedButton(final int x, final int y) {
        for (int i = 0; i < this.buttonList.size(); ++i) {
            final GuiButton btn = this.buttonList.get(i);
            final int btnWidth = GuiVideoSettings.getButtonWidth(btn);
            final int btnHeight = GuiVideoSettings.getButtonHeight(btn);
            if (x >= btn.xPosition && y >= btn.yPosition && x < btn.xPosition + btnWidth && y < btn.yPosition + btnHeight) {
                return btn;
            }
        }
        return null;
    }
}
