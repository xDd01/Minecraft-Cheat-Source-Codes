package org.newdawn.slick.tests;

import java.nio.*;
import org.newdawn.slick.*;
import java.net.*;
import org.newdawn.slick.opengl.*;
import java.io.*;

public class URLImageTest extends BasicGame
{
    Texture tex;
    int texWidth;
    int texHeight;
    Image img;
    ByteBuffer buffer;
    
    public static void main(final String[] args) throws SlickException {
        new AppGameContainer(new URLImageTest(), 800, 600, false).start();
    }
    
    public URLImageTest() {
        super("TestClass");
    }
    
    @Override
    public void init(final GameContainer c) throws SlickException {
    }
    
    @Override
    public void render(final GameContainer c, final Graphics g) throws SlickException {
        if (this.img != null) {
            this.img.draw(50.0f, 50.0f);
        }
        g.drawString("Press SPACE to load image from URL", 10.0f, 25.0f);
    }
    
    @Override
    public void update(final GameContainer c, final int delta) throws SlickException {
        if (c.getInput().isKeyPressed(57)) {
            try {
                final String ref = "http://upload.wikimedia.org/wikipedia/commons/6/63/Wikipedia-logo.png";
                final URL u = new URL(ref);
                final InputStream is = u.openStream();
                final BufferedInputStream in = new BufferedInputStream(is);
                in.mark(is.available());
                if (this.img != null) {
                    this.img.destroy();
                }
                this.tex = InternalTextureLoader.get().getTexture(is, ".png", false, 9728);
                is.close();
                this.img = new Image(this.tex);
            }
            catch (Exception e) {
                if (this.img != null) {
                    this.img.destroy();
                }
                this.img = null;
                e.printStackTrace();
            }
        }
    }
}
