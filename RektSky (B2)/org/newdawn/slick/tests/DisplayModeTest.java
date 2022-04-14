package org.newdawn.slick.tests;

import org.newdawn.slick.*;

public class DisplayModeTest extends BasicGame
{
    private Image tex;
    private AppGameContainer appContainer;
    
    public DisplayModeTest() {
        super("Display Mode Alpha Test");
    }
    
    @Override
    public void init(final GameContainer container) throws SlickException {
        this.appContainer = (AppGameContainer)container;
        this.tex = new Image("testdata/grass.png");
        container.getGraphics().setBackground(Color.lightGray);
    }
    
    @Override
    public void update(final GameContainer container, final int delta) throws SlickException {
        final Input input = container.getInput();
        if (input.isKeyPressed(2)) {
            this.appContainer.setDisplayMode(800, 600, false);
        }
        else if (input.isKeyPressed(3)) {
            this.appContainer.setDisplayMode(1024, 768, false);
        }
        else if (input.isKeyPressed(4)) {
            this.appContainer.setDisplayMode(1280, 600, false);
        }
    }
    
    @Override
    public void render(final GameContainer container, final Graphics g) throws SlickException {
        this.tex.draw(50.0f, 50.0f);
        this.tex.draw((float)(container.getWidth() - this.tex.getWidth()), 300.0f);
        g.drawString("input: " + container.getInput().getMouseX() + " , " + container.getInput().getMouseY(), 10.0f, 20.0f);
    }
    
    public static void main(final String[] argv) {
        try {
            final AppGameContainer container = new AppGameContainer(new DisplayModeTest());
            container.setDisplayMode(800, 600, false);
            container.start();
        }
        catch (SlickException e) {
            e.printStackTrace();
        }
    }
}
