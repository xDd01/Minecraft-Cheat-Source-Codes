package org.newdawn.slick.tests;

import org.newdawn.slick.opengl.*;
import org.newdawn.slick.util.*;
import org.newdawn.slick.*;

public class ImageMemTest extends BasicGame
{
    public ImageMemTest() {
        super("Image Memory Test");
    }
    
    @Override
    public void init(final GameContainer container) throws SlickException {
        try {
            Image img = Image.createOffscreenImage(2400, 2400);
            img.getGraphics();
            img.destroy();
            img = Image.createOffscreenImage(2400, 2400);
            img.getGraphics();
            img.destroy();
            final StringBuilder append = new StringBuilder().append("Total active textures: ");
            InternalTextureLoader.get();
            Log.info(append.append(InternalTextureLoader.getTextureCount()).toString());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void render(final GameContainer container, final Graphics g) {
    }
    
    @Override
    public void update(final GameContainer container, final int delta) {
    }
    
    public static void main(final String[] argv) {
        try {
            final AppGameContainer container = new AppGameContainer(new ImageMemTest());
            container.setDisplayMode(800, 600, false);
            container.start();
        }
        catch (SlickException e) {
            e.printStackTrace();
        }
    }
}
