package org.newdawn.slick.tests;

import org.newdawn.slick.util.*;
import org.newdawn.slick.*;

public class FontTest extends BasicGame
{
    private AngelCodeFont font;
    private AngelCodeFont font2;
    private Image image;
    private float widthMult;
    private float width;
    private static AppGameContainer container;
    
    public FontTest() {
        super("Font Test");
        this.widthMult = 1.0f;
        this.width = 0.0f;
    }
    
    @Override
    public void init(final GameContainer container) throws SlickException {
        this.font = new AngelCodeFont("testdata/demo.fnt", "testdata/demo.png");
        this.font2 = new AngelCodeFont("testdata/hiero.fnt", "testdata/hiero.png");
        this.image = this.font.getImage();
    }
    
    @Override
    public void render(final GameContainer container, final Graphics g) {
        if (g.getFont() instanceof AngelCodeFont) {
            final AngelCodeFont f = (AngelCodeFont)g.getFont();
            final String t = "testing } baseline";
            final float w = (float)f.getWidth(t);
            g.drawString(t, 400.0f, 500.0f);
            g.setColor(Color.red);
            final int ascent = f.getAscent();
            g.drawLine(400.0f, (float)(500 + ascent), 400.0f + w, (float)(500 + ascent));
        }
        this.font.drawString(80.0f, 5.0f, "A Font Example", Color.green);
        this.font.drawString(100.0f, 32.0f, "We - AV - Here is a more complete line that hopefully");
        this.font.drawString(100.0f, (float)(36 + this.font.getHeight("We Here is a more complete line that hopefully")), "will show some kerning.");
        this.font2.drawString(80.0f, 85.0f, "A Font Example", Color.green);
        this.font2.drawString(100.0f, 132.0f, "We - AV - Here is a more complete line that hopefully");
        this.font2.drawString(100.0f, (float)(136 + this.font2.getHeight("We - Here is a more complete line that hopefully")), "will show some kerning.");
        this.image.draw(100.0f, 400.0f);
        final String testStr = "Testing Font";
        this.font2.drawString(100.0f, 300.0f, testStr);
        g.setColor(Color.white);
        g.drawRect(100.0f, (float)(300 + this.font2.getYOffset(testStr)), (float)this.font2.getWidth(testStr), (float)(this.font2.getHeight(testStr) - this.font2.getYOffset(testStr)));
        this.font.drawString(500.0f, 300.0f, testStr);
        g.setColor(Color.white);
        g.drawRect(500.0f, (float)(300 + this.font.getYOffset(testStr)), (float)this.font.getWidth(testStr), (float)(this.font.getHeight(testStr) - this.font.getYOffset(testStr)));
        g.setColor(Color.white);
        this.drawTextBox(this.font, "custom font render", 500.0f, 350.0f, this.width);
        g.drawRect(500.0f, 350.0f, this.width, (float)this.font.getLineHeight());
    }
    
    private void drawTextBox(final AngelCodeFont font, final CharSequence text, float x, final float y, final float maxWidth) {
        AngelCodeFont.Glyph lastDef = null;
        font.getImage().startUse();
        final float startX = x;
        x = 0.0f;
        for (int i = 0; i < text.length(); ++i) {
            final char c = text.charAt(i);
            final AngelCodeFont.Glyph def = font.getGlyph(c);
            if (def != null) {
                if (lastDef != null) {
                    x += lastDef.getKerning(c);
                }
                else {
                    x -= def.xoffset;
                }
                lastDef = def;
                if (def.xoffset + def.width + x > maxWidth) {
                    break;
                }
                final Image subImage = def.image;
                subImage.drawEmbedded(startX + x + def.xoffset, y + def.yoffset, def.width, def.height);
                x += def.xadvance;
            }
        }
        font.getImage().endUse();
    }
    
    @Override
    public void update(final GameContainer container, final int delta) throws SlickException {
        this.width += delta * 0.1f * this.widthMult;
        if (this.width > 270.0f || this.width < 0.0f) {
            this.widthMult = ((this.widthMult == 1.0f) ? -1.0f : 1.0f);
        }
    }
    
    @Override
    public void keyPressed(final int key, final char c) {
        if (key == 1) {
            System.exit(0);
        }
        if (key == 57) {
            try {
                FontTest.container.setDisplayMode(640, 480, false);
            }
            catch (SlickException e) {
                Log.error(e);
            }
        }
    }
    
    public static void main(final String[] argv) {
        try {
            (FontTest.container = new AppGameContainer(new FontTest())).setDisplayMode(800, 600, false);
            FontTest.container.start();
        }
        catch (SlickException e) {
            e.printStackTrace();
        }
    }
}
