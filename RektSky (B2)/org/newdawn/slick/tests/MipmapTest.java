package org.newdawn.slick.tests;

import org.newdawn.slick.util.*;
import java.io.*;
import java.nio.*;
import org.newdawn.slick.opengl.*;
import org.newdawn.slick.*;

public class MipmapTest extends BasicGame
{
    Image image;
    Image mippedImage;
    float scale;
    boolean supported;
    
    public static void main(final String[] args) throws SlickException {
        new AppGameContainer(new MipmapTest(), 800, 600, false).start();
    }
    
    public MipmapTest() {
        super("TestClass");
        this.scale = 1.0f;
        this.supported = false;
    }
    
    @Override
    public void init(final GameContainer c) throws SlickException {
        this.supported = InternalTextureLoader.isGenerateMipmapSupported();
        final String ref = "testdata/hiero.png";
        this.image = new Image(ref);
        this.mippedImage = (this.supported ? this.createMipmapImage(ref) : this.image);
    }
    
    private Image createMipmapImage(final String ref) throws SlickException {
        try {
            final InputStream in = ResourceLoader.getResourceAsStream(ref);
            final LoadableImageData imageData = ImageDataFactory.getImageDataFor(ref);
            final ByteBuffer buf = imageData.loadImage(new BufferedInputStream(in), false, null);
            final ImageData.Format fmt = imageData.getFormat();
            final int minFilter = 9987;
            final int magFilter = 9729;
            final Texture tex = InternalTextureLoader.get().createTexture(imageData, buf, ref, 3553, minFilter, magFilter, true, fmt);
            return new Image(tex);
        }
        catch (IOException e) {
            Log.error("error loading image", e);
            throw new SlickException("error loading image " + e.getMessage());
        }
    }
    
    @Override
    public void render(final GameContainer c, final Graphics g) throws SlickException {
        if (!this.supported) {
            g.drawString("Your OpenGL version does not support automatic mipmap generation", 10.0f, 25.0f);
        }
        else {
            g.drawString("Left = no mipmapping, right = automatically generated mipmaps", 10.0f, 25.0f);
        }
        this.image.draw(10.0f, 80.0f, this.scale);
        this.mippedImage.draw(this.image.getWidth() * this.scale + 25.0f, 80.0f, this.scale);
    }
    
    @Override
    public void update(final GameContainer c, final int delta) throws SlickException {
        final Input in = c.getInput();
        if (in.isKeyDown(200)) {
            this.scale = Math.min(2.0f, this.scale + 0.001f);
        }
        else if (in.isKeyDown(208)) {
            this.scale = Math.max(0.01f, this.scale - 0.001f);
        }
    }
}
