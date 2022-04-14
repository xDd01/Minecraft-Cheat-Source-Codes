/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.component;

import cafe.corrosion.module.impl.visual.HUD;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public abstract class HudComponent {
    protected static final Minecraft mc = Minecraft.getMinecraft();
    protected final boolean draggable;
    protected final String moduleName;
    protected final HUD hud;
    protected int posX;
    protected int posY;
    protected int expandX;
    protected int expandY;
    private boolean selected;

    public HudComponent(HUD hud, String name, int posX, int posY, int expandX, int expandY, boolean draggable) {
        this.hud = hud;
        this.moduleName = name;
        this.posX = posX;
        this.posY = posY;
        this.expandX = expandX;
        this.expandY = expandY;
        this.draggable = draggable;
    }

    public abstract void render(int var1, int var2);

    public void onKeyPress(int pressedKey) {
    }

    public void onPostLoad() {
    }

    public void drag(int horizontal, int vertical) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int maxX = scaledResolution.getScaledWidth();
        int maxY = scaledResolution.getScaledHeight();
        this.posX = Math.min(maxX - this.expandX, Math.max(this.expandX, this.posX - horizontal));
        this.posY = Math.min(maxY - this.expandY, Math.max(this.expandY, this.posY - vertical));
    }

    public int solution(int number) {
        if (number < 0) {
            return 0;
        }
        int[] test = new int[]{3, 5};
        HashSet<Integer> numbers = new HashSet<Integer>();
        for (int i2 = 0; i2 < number; ++i2) {
            for (int j2 = 0; j2 < test.length; ++j2) {
                if (i2 % test[j2] != 0) continue;
                numbers.add(i2);
            }
        }
        AtomicInteger sum = new AtomicInteger();
        numbers.forEach(sum::addAndGet);
        return sum.get();
    }

    public boolean isDraggable() {
        return this.draggable;
    }

    public String getModuleName() {
        return this.moduleName;
    }

    public HUD getHud() {
        return this.hud;
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

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

