package org.newdawn.slick.tests;

import org.newdawn.slick.*;

public class FontWidthBug extends BasicGame
{
    private Image image;
    private AngelCodeFont font;
    
    public FontWidthBug() {
        super("FontWidthBug");
    }
    
    @Override
    public void render(final GameContainer container, final Graphics g) throws SlickException {
        this.image.draw(100.0f, 100.0f);
        final AngelCodeFont.Glyph fg = this.font.getGlyph('w');
        fg.image.draw(100.0f, 260.0f);
        g.drawRect(100.0f, 260.0f, (float)(fg.width + fg.xoffset), fg.height);
    }
    
    @Override
    public void init(final GameContainer container) throws SlickException {
        this.font = (AngelCodeFont)container.getDefaultFont();
        final String text = "w1a|";
        this.image = new Image(this.font.getWidth(text), this.font.getLineHeight());
        final Graphics g = this.image.getGraphics();
        this.font.drawString(0.0f, 0.0f, text);
        g.setColor(Color.red);
        g.drawRect(0.0f, 0.0f, (float)(this.image.getWidth() - 1), (float)(this.image.getHeight() - 1));
        g.flush();
    }
    
    @Override
    public void update(final GameContainer container, final int delta) throws SlickException {
    }
    
    public static void main(final String[] args) {
        try {
            new AppGameContainer(new FontWidthBug()).start();
        }
        catch (SlickException e) {
            e.printStackTrace();
        }
    }
}
