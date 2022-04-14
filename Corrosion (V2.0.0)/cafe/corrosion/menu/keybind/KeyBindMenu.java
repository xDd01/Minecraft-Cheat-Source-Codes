/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.menu.keybind;

import cafe.corrosion.Corrosion;
import cafe.corrosion.font.FontManager;
import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.keybind.KeyStorage;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.util.font.type.FontType;
import cafe.corrosion.util.render.ColorUtil;
import cafe.corrosion.util.render.GuiUtils;
import cafe.corrosion.util.render.RenderUtil;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class KeyBindMenu
extends GuiScreen {
    private static final List<Button> BUTTONS = new ArrayList<Button>();
    private final Module module;
    private final GuiScreen guiScreen;

    public KeyBindMenu(Module module, GuiScreen guiScreen) {
        this.module = module;
        this.guiScreen = guiScreen;
    }

    @Override
    public void drawScreen(int posX, int posY, float partialTicks) {
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        int screenWidth = scaledResolution.getScaledWidth();
        int screenHeight = scaledResolution.getScaledHeight();
        int backgroundColor = new Color(36, 36, 36).getRGB();
        int buttonColor = new Color(64, 64, 64).getRGB();
        int minX = screenWidth / 2 - 250;
        int maxX = minX + 500;
        int minY = screenHeight / 2 - 100;
        int maxY = minY + 190;
        RenderUtil.drawRoundedRect(minX, minY, maxX, maxY, backgroundColor, backgroundColor);
        TTFFontRenderer renderer = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.ROBOTO, 19.0f);
        BUTTONS.forEach(button -> {
            int bx2 = minX + button.getPosX();
            int by = minY + button.getPosY();
            RenderUtil.drawRoundedRect(bx2, by, bx2 + button.getExpandX(), by + button.getExpandY(), buttonColor, buttonColor);
            int width = (int)renderer.getWidth(button.getName());
            int height = (int)renderer.getHeight(button.getName());
            int buttonWidth = button.getExpandX();
            renderer.drawString(button.getName(), (float)bx2 + (float)buttonWidth / 2.0f - (float)width / 2.0f, (float)by + (float)height / 2.0f + 6.0f, ColorUtil.astolfoColors(-6000, by));
        });
        BUTTONS.stream().filter(Button::isDisplaying).forEach(button -> {
            int bx2 = minX + button.getPosX();
            int by = minY + button.getPosY();
            button.displayMenu(bx2, by, screenWidth / 2, screenHeight / 2);
        });
        super.drawScreen(posX, posY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        int screenWidth = scaledResolution.getScaledWidth();
        int screenHeight = scaledResolution.getScaledHeight();
        int minX = screenWidth / 2 - 250;
        int minY = screenHeight / 2 - 100;
        Button displayingButton = BUTTONS.stream().filter(Button::isDisplaying).findFirst().orElse(null);
        if (displayingButton != null) {
            boolean hovered = GuiUtils.isHoveringPos(mouseX, mouseY, displayingButton.getBoxMinX(), displayingButton.getBoxMaxY(), displayingButton.getBoxMaxX(), displayingButton.getBoxMinY());
            boolean hoveringButton = GuiUtils.isHoveringPos(mouseX, mouseY, displayingButton.getBoxMinX() + 10, displayingButton.getBaseY() - 30, displayingButton.getBoxMaxX() - 10, displayingButton.getBoxMinY() - 5);
            if (hoveringButton) {
                this.module.setKeyCode(displayingButton.getKeyCode());
                this.mc.displayGuiScreen(this.guiScreen);
            } else if (hovered) {
                Module[] boundModules = Corrosion.INSTANCE.getModuleManager().getIf(module -> module.getKeyCode() == displayingButton.getKeyCode()).toArray(new Module[0]);
                for (int i2 = 0; i2 < boundModules.length; ++i2) {
                    Module module2 = boundModules[i2];
                    int textY = displayingButton.getBoxMaxY() + 17 + i2 * 17;
                    if (!GuiUtils.isHoveringPos(mouseX, mouseY, displayingButton.getBoxMaxX() - 40, textY, displayingButton.getBoxMaxX(), textY + 10)) continue;
                    module2.setKeyCode(0);
                    return;
                }
            }
            if (hovered) {
                return;
            }
        }
        BUTTONS.stream().filter(button -> {
            button.setDisplaying(false);
            return GuiUtils.isHoveringPos(mouseX, mouseY, minX + button.getPosX(), minY + button.getPosY(), minX + button.getPosX() + button.getExpandX(), minY + button.getPosY() + button.getExpandY());
        }).forEach(button -> button.setDisplaying(true));
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        BUTTONS.forEach(button -> button.setDisplaying(false));
        KeyStorage.write(Corrosion.INSTANCE.getKeybindPath());
    }

    static {
        BUTTONS.add(new Button("~", 10, 10, 30, 30, 41));
        BUTTONS.add(new Button("1", 45, 10, 30, 30, 2));
        BUTTONS.add(new Button("2", 80, 10, 30, 30, 3));
        BUTTONS.add(new Button("3", 115, 10, 30, 30, 4));
        BUTTONS.add(new Button("4", 150, 10, 30, 30, 5));
        BUTTONS.add(new Button("5", 185, 10, 30, 30, 6));
        BUTTONS.add(new Button("6", 220, 10, 30, 30, 7));
        BUTTONS.add(new Button("7", 255, 10, 30, 30, 8));
        BUTTONS.add(new Button("8", 290, 10, 30, 30, 9));
        BUTTONS.add(new Button("9", 325, 10, 30, 30, 10));
        BUTTONS.add(new Button("0", 360, 10, 30, 30, 11));
        BUTTONS.add(new Button("<---------", 395, 10, 95, 30, 14));
        BUTTONS.add(new Button("Tab", 10, 45, 45, 30, 15));
        BUTTONS.add(new Button("Q", 60, 45, 30, 30, 16));
        BUTTONS.add(new Button("W", 95, 45, 30, 30, 17));
        BUTTONS.add(new Button("E", 130, 45, 30, 30, 18));
        BUTTONS.add(new Button("R", 165, 45, 30, 30, 19));
        BUTTONS.add(new Button("T", 200, 45, 30, 30, 20));
        BUTTONS.add(new Button("Y", 235, 45, 30, 30, 21));
        BUTTONS.add(new Button("U", 270, 45, 30, 30, 22));
        BUTTONS.add(new Button("I", 305, 45, 30, 30, 23));
        BUTTONS.add(new Button("O", 340, 45, 30, 30, 24));
        BUTTONS.add(new Button("P", 375, 45, 30, 30, 25));
        BUTTONS.add(new Button("[", 410, 45, 25, 30, 26));
        BUTTONS.add(new Button("]", 440, 45, 25, 30, 27));
        BUTTONS.add(new Button("\\", 470, 45, 20, 30, 43));
        BUTTONS.add(new Button("Caps Lock", 10, 80, 60, 30, 58));
        BUTTONS.add(new Button("A", 75, 80, 30, 30, 30));
        BUTTONS.add(new Button("S", 110, 80, 30, 30, 31));
        BUTTONS.add(new Button("D", 145, 80, 30, 30, 32));
        BUTTONS.add(new Button("F", 180, 80, 30, 30, 33));
        BUTTONS.add(new Button("G", 215, 80, 30, 30, 34));
        BUTTONS.add(new Button("H", 250, 80, 30, 30, 35));
        BUTTONS.add(new Button("J", 285, 80, 30, 30, 36));
        BUTTONS.add(new Button("K", 320, 80, 30, 30, 37));
        BUTTONS.add(new Button("L", 355, 80, 30, 30, 38));
        BUTTONS.add(new Button(";", 390, 80, 20, 30, 39));
        BUTTONS.add(new Button("'", 415, 80, 20, 30, 40));
        BUTTONS.add(new Button("Enter", 440, 80, 50, 30, 28));
        BUTTONS.add(new Button("Left Shift", 10, 115, 70, 30, 42));
        BUTTONS.add(new Button("Z", 85, 115, 30, 30, 44));
        BUTTONS.add(new Button("X", 120, 115, 30, 30, 45));
        BUTTONS.add(new Button("C", 155, 115, 30, 30, 46));
        BUTTONS.add(new Button("V", 190, 115, 30, 30, 47));
        BUTTONS.add(new Button("B", 225, 115, 30, 30, 48));
        BUTTONS.add(new Button("N", 260, 115, 30, 30, 49));
        BUTTONS.add(new Button("M", 295, 115, 30, 30, 50));
        BUTTONS.add(new Button(",", 330, 115, 25, 30, 51));
        BUTTONS.add(new Button(".", 360, 115, 25, 30, 52));
        BUTTONS.add(new Button("/", 390, 115, 25, 30, 53));
        BUTTONS.add(new Button("Right Shift", 420, 115, 70, 30, 54));
        BUTTONS.add(new Button("Ctrl", 10, 150, 50, 30, 29));
        BUTTONS.add(new Button("Alt", 65, 150, 50, 30, 56));
        BUTTONS.add(new Button("Space", 120, 150, 205, 30, 57));
        BUTTONS.add(new Button("Alt", 330, 150, 50, 30, 184));
        BUTTONS.add(new Button("Function", 385, 150, 50, 30, 196));
        BUTTONS.add(new Button("Ctrl", 440, 150, 50, 30, 157));
    }

    private static class Button {
        private final String name;
        private final int posX;
        private final int posY;
        private final int expandX;
        private final int expandY;
        private final int keyCode;
        private boolean isDisplaying;
        private int boxMinX;
        private int boxMinY;
        private int boxMaxX;
        private int boxMaxY;
        private int baseY;

        public void displayMenu(int baseX, int baseY, int centerX, int centerY) {
            Module[] boundModules = Corrosion.INSTANCE.getModuleManager().getIf(module -> module.getKeyCode() == this.keyCode).toArray(new Module[0]);
            int backgroundColor = new Color(36, 36, 36).getRGB();
            int boxMinX = baseX + this.expandX / 2 - 55;
            int boxMinY = baseY + 5;
            int boxMaxX = baseX + this.expandX / 2 + 55;
            int boxMaxY = baseY - 50 - 17 * boundModules.length;
            RenderUtil.drawRoundedRect(boxMinX, boxMinY, boxMaxX, boxMaxY, backgroundColor, backgroundColor);
            RenderUtil.drawTriangle((float)baseX + (float)this.expandX / 2.0f, (float)baseY + 10.0f, 10.0f, 180.0f, 10.0f, backgroundColor);
            FontManager fontManager = Corrosion.INSTANCE.getFontManager();
            TTFFontRenderer largeRenderer = fontManager.getFontRenderer(FontType.ROBOTO, 20.0f);
            int textColor = ColorUtil.astolfoColors(-6000, boxMaxY);
            int buttonColor = new Color(28, 28, 28).getRGB();
            RenderUtil.drawRoundedRect(boxMinX + 10, baseY - 5, boxMaxX - 10, baseY - 30, buttonColor, buttonColor);
            largeRenderer.drawString("Modules - " + this.name, boxMinX + 5, boxMaxY + 3, textColor);
            largeRenderer.drawString("Add Module", boxMinX + 27, baseY - 22, textColor);
            for (int i2 = 0; i2 < boundModules.length; ++i2) {
                Module module2 = boundModules[i2];
                ModuleAttributes attributes = module2.getAttributes();
                int textY = boxMaxY + 17 + i2 * 17;
                largeRenderer.drawString(attributes.name(), boxMinX + 6, textY, Color.WHITE.getRGB());
                largeRenderer.drawString("Remove", boxMaxX - 40, textY, Color.RED.getRGB());
            }
            this.boxMinX = boxMinX;
            this.boxMinY = boxMinY;
            this.boxMaxX = boxMaxX;
            this.boxMaxY = boxMaxY;
            this.baseY = baseY;
        }

        public String getName() {
            return this.name;
        }

        public int getPosX() {
            return this.posX;
        }

        public int getPosY() {
            return this.posY;
        }

        public int getExpandX() {
            return this.expandX;
        }

        public int getExpandY() {
            return this.expandY;
        }

        public int getKeyCode() {
            return this.keyCode;
        }

        public boolean isDisplaying() {
            return this.isDisplaying;
        }

        public int getBoxMinX() {
            return this.boxMinX;
        }

        public int getBoxMinY() {
            return this.boxMinY;
        }

        public int getBoxMaxX() {
            return this.boxMaxX;
        }

        public int getBoxMaxY() {
            return this.boxMaxY;
        }

        public int getBaseY() {
            return this.baseY;
        }

        public Button(String name, int posX, int posY, int expandX, int expandY, int keyCode) {
            this.name = name;
            this.posX = posX;
            this.posY = posY;
            this.expandX = expandX;
            this.expandY = expandY;
            this.keyCode = keyCode;
        }

        public void setDisplaying(boolean isDisplaying) {
            this.isDisplaying = isDisplaying;
        }
    }
}

