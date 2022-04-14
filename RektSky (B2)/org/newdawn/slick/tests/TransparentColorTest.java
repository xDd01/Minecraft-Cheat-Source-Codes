package org.newdawn.slick.tests;

import org.newdawn.slick.*;

public class TransparentColorTest extends BasicGame
{
    private Image image;
    private Image timage;
    private Image gifImage;
    private Image gifTImage;
    private Image tgaImage;
    private Image tgaTImage;
    
    public TransparentColorTest() {
        super("Transparent Color Test");
    }
    
    @Override
    public void init(final GameContainer container) throws SlickException {
        this.image = new Image("testdata/transtest.png");
        this.timage = new Image("testdata/transtest.png", new Color(94, 66, 41, 255));
        this.gifImage = new Image("testdata/logo.gif");
        this.gifTImage = new Image("testdata/logo.gif", new Color(254, 255, 252));
    }
    
    @Override
    public void render(final GameContainer container, final Graphics g) {
        g.setBackground(Color.lightGray);
        this.image.draw();
        this.timage.draw((float)this.image.getWidth(), 0.0f);
    }
    
    @Override
    public void update(final GameContainer container, final int delta) {
        final int mx = container.getInput().getMouseX();
        final int my = container.getInput().getMouseY();
        try {
            final Color c = this.image.getColor(mx, my);
            System.out.println(c.getRed() + " " + c.getBlue() + " " + c.getGreen());
        }
        catch (Exception ex) {}
    }
    
    public static void main(final String[] argv) {
        try {
            final AppGameContainer container = new AppGameContainer(new TransparentColorTest());
            container.setDisplayMode(800, 600, false);
            container.start();
        }
        catch (SlickException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void keyPressed(final int key, final char c) {
    }
}
