package org.newdawn.slick.tests;

import org.newdawn.slick.tiled.*;
import org.newdawn.slick.util.*;
import org.newdawn.slick.*;

public class IsoTiledTest extends BasicGame
{
    private TiledMap tilemap;
    
    public IsoTiledTest() {
        super("Isometric Tiled Map Test");
    }
    
    @Override
    public void init(final GameContainer container) throws SlickException {
        this.tilemap = new TiledMap("testdata/isoexample.tmx", "testdata/");
    }
    
    @Override
    public void update(final GameContainer container, final int delta) throws SlickException {
    }
    
    @Override
    public void render(final GameContainer container, final Graphics g) throws SlickException {
        this.tilemap.render(350, 150);
    }
    
    public static void main(final String[] argv) {
        Bootstrap.runAsApplication(new IsoTiledTest(), 800, 600, false);
    }
}
