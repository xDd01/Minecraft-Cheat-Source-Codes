/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Locale;
import javax.vecmath.Vector2f;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class TTFFontRenderer {
    private final boolean antiAlias;
    private final Font font;
    private final boolean fractionalMetrics;
    private final CharacterData[] regularData;
    private final CharacterData[] boldData;
    private final CharacterData[] italicsData;
    private final int[] colorCodes = new int[32];
    private static final int RANDOM_OFFSET = 1;

    public TTFFontRenderer(Font font) {
        this(font, 256);
    }

    public TTFFontRenderer(Font font, int characterCount) {
        this(font, characterCount, true);
    }

    public TTFFontRenderer(Font font, int characterCount, boolean antiAlias) {
        this.font = font;
        this.fractionalMetrics = true;
        this.antiAlias = antiAlias;
        this.regularData = this.setup(new CharacterData[characterCount], 0);
        this.boldData = this.setup(new CharacterData[characterCount], 1);
        this.italicsData = this.setup(new CharacterData[characterCount], 2);
    }

    public TTFFontRenderer(Font font, boolean antiAlias) {
        this(font, 256, antiAlias);
    }

    private CharacterData[] setup(CharacterData[] characterData, int type) {
        this.generateColors();
        Font font = this.font.deriveFont(type);
        BufferedImage utilityImage = new BufferedImage(1, 1, 2);
        Graphics2D utilityGraphics = (Graphics2D)utilityImage.getGraphics();
        utilityGraphics.setFont(font);
        FontMetrics fontMetrics = utilityGraphics.getFontMetrics();
        for (int index = 0; index < characterData.length; ++index) {
            char character = (char)index;
            Rectangle2D characterBounds = fontMetrics.getStringBounds(String.valueOf(character), utilityGraphics);
            float width = (float)characterBounds.getWidth() + 8.0f;
            float height = (float)characterBounds.getHeight();
            BufferedImage characterImage = new BufferedImage(MathHelper.ceiling_double_int(width), MathHelper.ceiling_double_int(height), 2);
            Graphics2D graphics = (Graphics2D)characterImage.getGraphics();
            graphics.setFont(font);
            graphics.setColor(new Color(255, 255, 255, 0));
            graphics.fillRect(0, 0, characterImage.getWidth(), characterImage.getHeight());
            graphics.setColor(Color.WHITE);
            if (this.antiAlias) {
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, this.fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
            }
            graphics.drawString(String.valueOf(character), 4, fontMetrics.getAscent());
            int textureId = GL11.glGenTextures();
            this.createTexture(textureId, characterImage);
            characterData[index] = new CharacterData(character, characterImage.getWidth(), characterImage.getHeight(), textureId);
        }
        return characterData;
    }

    private void createTexture(int textureId, BufferedImage image) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
        for (int y2 = 0; y2 < image.getHeight(); ++y2) {
            for (int x2 = 0; x2 < image.getWidth(); ++x2) {
                int pixel = pixels[y2 * image.getWidth() + x2];
                buffer.put((byte)(pixel >> 16 & 0xFF));
                buffer.put((byte)(pixel >> 8 & 0xFF));
                buffer.put((byte)(pixel & 0xFF));
                buffer.put((byte)(pixel >> 24 & 0xFF));
            }
        }
        buffer.flip();
        GlStateManager.bindTexture(textureId);
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        GL11.glTexImage2D(3553, 0, 6408, image.getWidth(), image.getHeight(), 0, 6408, 5121, buffer);
    }

    public void drawString(String text, float x2, float y2, int color) {
        this.renderString(text, x2, y2, color, false);
    }

    public void drawCenteredString(String text, float x2, float y2, int color) {
        float width = this.getWidth(text) / 2.0f;
        this.renderString(text, x2 - width, y2 - this.getHeight(text) / 2.0f, color, false);
    }

    public void drawCenteredStringWithShadow(String text, float x2, float y2, int color) {
        float width = this.getWidth(text) / 2.0f;
        this.drawStringWithShadow(text, x2 - width, y2 - this.getHeight(text) / 2.0f, color);
    }

    public void drawStringWithShadow(String text, float x2, float y2, int color) {
        GL11.glTranslated(0.7, 0.7, 0.0);
        this.renderString(text, x2, y2, color, true);
        GL11.glTranslated(-0.7, -0.7, 0.0);
        this.renderString(text, x2, y2, color, false);
    }

    private void renderString(String text, float x2, float y2, int color, boolean shadow) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
        if (text == null || text.length() == 0) {
            return;
        }
        GL11.glPushMatrix();
        GlStateManager.scale(0.5, 0.5, 1.0);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        x2 -= 2.0f;
        y2 -= 2.0f;
        x2 += 0.5f;
        y2 += 0.5f;
        x2 *= 2.0f;
        y2 *= 2.0f;
        CharacterData[] characterData = this.regularData;
        boolean underlined = false;
        boolean strikethrough = false;
        boolean obfuscated = false;
        int length = text.length();
        double multiplier = 255.0 * (double)(shadow ? 4 : 1);
        Color c2 = new Color(color);
        GL11.glColor4d((double)c2.getRed() / multiplier, (double)c2.getGreen() / multiplier, (double)c2.getBlue() / multiplier, (double)(color >> 24 & 0xFF) / 255.0);
        for (int i2 = 0; i2 < length; ++i2) {
            int previous;
            char character = text.charAt(i2);
            int n2 = previous = i2 > 0 ? (int)text.charAt(i2 - 1) : 46;
            if (previous == 167) continue;
            if (character == '\u00a7') {
                int index = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(i2 + 1));
                if (index < 16) {
                    obfuscated = false;
                    strikethrough = false;
                    underlined = false;
                    characterData = this.regularData;
                    if (index < 0) {
                        index = 15;
                    }
                    if (shadow) {
                        index += 16;
                    }
                    int textColor = this.colorCodes[index];
                    GL11.glColor4d((double)(textColor >> 16) / 255.0, (double)(textColor >> 8 & 0xFF) / 255.0, (double)(textColor & 0xFF) / 255.0, (double)(color >> 24 & 0xFF) / 255.0);
                    continue;
                }
                if (index == 16) {
                    obfuscated = true;
                    continue;
                }
                if (index == 17) {
                    characterData = this.boldData;
                    continue;
                }
                if (index == 18) {
                    strikethrough = true;
                    continue;
                }
                if (index == 19) {
                    underlined = true;
                    continue;
                }
                if (index == 20) {
                    characterData = this.italicsData;
                    continue;
                }
                obfuscated = false;
                strikethrough = false;
                underlined = false;
                characterData = this.regularData;
                GL11.glColor4d(shadow ? 0.25 : 1.0, shadow ? 0.25 : 1.0, shadow ? 0.25 : 1.0, (double)(color >> 24 & 0xFF) / 255.0);
                continue;
            }
            if (character > '\u00ff') continue;
            if (obfuscated) {
                character = (char)(character + '\u0001');
            }
            this.drawChar(character, characterData, x2, y2);
            CharacterData charData = characterData[character];
            if (strikethrough) {
                this.drawLine(new Vector2f(0.0f, charData.height / 2.0f), new Vector2f(charData.width, charData.height / 2.0f), 3.0f);
            }
            if (underlined) {
                this.drawLine(new Vector2f(0.0f, charData.height - 15.0f), new Vector2f(charData.width, charData.height - 15.0f), 3.0f);
            }
            x2 += charData.width - 8.0f;
        }
        GL11.glPopMatrix();
        GlStateManager.disableBlend();
        GlStateManager.bindTexture(0);
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
    }

    public float getWidth(String text) {
        if (text == null) {
            return 0.0f;
        }
        float width = 0.0f;
        CharacterData[] characterData = this.regularData;
        int length = text.length();
        for (int i2 = 0; i2 < length; ++i2) {
            int previous;
            char character = text.charAt(i2);
            int n2 = previous = i2 > 0 ? (int)text.charAt(i2 - 1) : 46;
            if (previous == 167) continue;
            if (character == '\u00a7') {
                int index = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(i2 + 1));
                if (index == 17) {
                    characterData = this.boldData;
                    continue;
                }
                if (index == 20) {
                    characterData = this.italicsData;
                    continue;
                }
                if (index != 21) continue;
                characterData = this.regularData;
                continue;
            }
            if (character > '\u00ff') continue;
            CharacterData charData = characterData[character];
            width += (charData.width - 8.0f) / 2.0f;
        }
        return width + 2.0f;
    }

    public float getHeight(String text) {
        if (text == null) {
            return 0.0f;
        }
        float height = 0.0f;
        CharacterData[] characterData = this.regularData;
        int length = text.length();
        for (int i2 = 0; i2 < length; ++i2) {
            int previous;
            char character = text.charAt(i2);
            int n2 = previous = i2 > 0 ? (int)text.charAt(i2 - 1) : 46;
            if (previous == 167) continue;
            if (character == '\u00a7') {
                int index = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(i2 + 1));
                if (index == 17) {
                    characterData = this.boldData;
                    continue;
                }
                if (index == 20) {
                    characterData = this.italicsData;
                    continue;
                }
                if (index != 21) continue;
                characterData = this.regularData;
                continue;
            }
            if (character > '\u00ff') continue;
            CharacterData charData = characterData[character];
            height = Math.max(height, charData.height);
        }
        return height / 2.0f - 2.0f;
    }

    private void drawChar(char character, CharacterData[] characterData, float x2, float y2) {
        CharacterData charData = characterData[character];
        charData.bind();
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2d(x2, y2);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2d(x2, y2 + charData.height);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex2d(x2 + charData.width, y2 + charData.height);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2d(x2 + charData.width, y2);
        GL11.glEnd();
    }

    private void drawLine(Vector2f start, Vector2f end, float width) {
        GL11.glDisable(3553);
        GL11.glLineWidth(width);
        GL11.glBegin(1);
        GL11.glVertex2f(start.x, start.y);
        GL11.glVertex2f(end.x, end.y);
        GL11.glEnd();
        GL11.glEnable(3553);
    }

    private void generateColors() {
        for (int i2 = 0; i2 < 32; ++i2) {
            int thingy = (i2 >> 3 & 1) * 85;
            int red = (i2 >> 2 & 1) * 170 + thingy;
            int green = (i2 >> 1 & 1) * 170 + thingy;
            int blue = (i2 & 1) * 170 + thingy;
            if (i2 == 6) {
                red += 85;
            }
            if (i2 >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }
            this.colorCodes[i2] = (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
        }
    }

    static class CharacterData {
        public char character;
        public float width;
        public float height;
        private final int textureId;

        public CharacterData(char character, float width, float height, int textureId) {
            this.character = character;
            this.width = width;
            this.height = height;
            this.textureId = textureId;
        }

        public void bind() {
            GL11.glBindTexture(3553, this.textureId);
        }
    }
}

