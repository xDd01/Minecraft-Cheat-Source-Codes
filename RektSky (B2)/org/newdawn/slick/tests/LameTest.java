package org.newdawn.slick.tests;

import org.newdawn.slick.geom.*;
import org.newdawn.slick.*;

public class LameTest extends BasicGame
{
    private Polygon poly;
    private Image image;
    
    public LameTest() {
        super("Lame Test");
        this.poly = new Polygon();
    }
    
    @Override
    public void init(final GameContainer container) throws SlickException {
        this.poly.addPoint(100.0f, 100.0f);
        this.poly.addPoint(120.0f, 100.0f);
        this.poly.addPoint(120.0f, 120.0f);
        this.poly.addPoint(100.0f, 120.0f);
        this.image = new Image("testdata/rocks.png");
    }
    
    @Override
    public void update(final GameContainer container, final int delta) throws SlickException {
    }
    
    @Override
    public void render(final GameContainer container, final Graphics g) throws SlickException {
        g.setColor(Color.white);
        g.texture(this.poly, this.image);
    }
    
    public static void main(final String[] argv) {
        try {
            final AppGameContainer container = new AppGameContainer(new LameTest());
            container.setDisplayMode(800, 600, false);
            container.start();
        }
        catch (SlickException e) {
            e.printStackTrace();
        }
    }
}
