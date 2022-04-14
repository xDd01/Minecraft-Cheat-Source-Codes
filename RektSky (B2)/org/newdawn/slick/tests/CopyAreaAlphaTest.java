package org.newdawn.slick.tests;

import org.newdawn.slick.*;

public class CopyAreaAlphaTest extends BasicGame
{
    private Image textureMap;
    private Image copy;
    
    public CopyAreaAlphaTest() {
        super("CopyArea Alpha Test");
    }
    
    @Override
    public void init(final GameContainer container) throws SlickException {
        this.textureMap = new Image("testdata/grass.png");
        container.getGraphics().setBackground(Color.lightGray);
        this.copy = new Image(300, 200);
    }
    
    @Override
    public void update(final GameContainer container, final int delta) throws SlickException {
    }
    
    @Override
    public void render(final GameContainer container, final Graphics g) throws SlickException {
        g.clearAlphaMap();
        g.setDrawMode(Graphics.MODE_NORMAL);
        g.setColor(Color.white);
        g.fillOval(100.0f, 100.0f, 150.0f, 150.0f);
        this.textureMap.draw(10.0f, 50.0f);
        g.copyArea(this.copy, 10, 50);
        g.setColor(Color.red);
        g.fillRect(300.0f, 100.0f, 200.0f, 200.0f);
        this.copy.draw(350.0f, 150.0f);
    }
    
    @Override
    public void keyPressed(final int key, final char c) {
    }
    
    public static void main(final String[] argv) {
        try {
            final AppGameContainer container = new AppGameContainer(new CopyAreaAlphaTest());
            container.setDisplayMode(800, 600, false);
            container.start();
        }
        catch (SlickException e) {
            e.printStackTrace();
        }
    }
}
