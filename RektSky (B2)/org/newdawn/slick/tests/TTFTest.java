package org.newdawn.slick.tests;

import java.awt.*;
import org.newdawn.slick.font.effects.*;
import org.newdawn.slick.*;

public class TTFTest extends BasicGame
{
    private UnicodeFont ufont;
    private Font font;
    
    public TTFTest() {
        super("Font Performance Test");
    }
    
    @Override
    public void init(final GameContainer container) throws SlickException {
        final java.awt.Font f = new java.awt.Font("Papyrus", 0, 16);
        final long t = System.currentTimeMillis();
        this.ufont = new UnicodeFont(f);
        this.ufont.getEffects().add(new ColorEffect(Color.white));
        this.ufont.addGlyphs(32, 127);
        this.ufont.loadGlyphs();
        System.out.println("Time Taken: " + (System.currentTimeMillis() - t) + " ms");
    }
    
    @Override
    public void render(final GameContainer container, final Graphics g) {
        this.ufont.drawString(50.0f, 50.0f, "hello");
    }
    
    @Override
    public void update(final GameContainer container, final int delta) throws SlickException {
    }
    
    @Override
    public void keyPressed(final int key, final char c) {
        if (key == 1) {
            System.exit(0);
        }
    }
    
    public static void main(final String[] argv) {
        try {
            final AppGameContainer container = new AppGameContainer(new TTFTest());
            container.setDisplayMode(800, 600, false);
            container.start();
        }
        catch (SlickException e) {
            e.printStackTrace();
        }
    }
}
