package org.newdawn.slick.tests;

import java.io.*;
import org.newdawn.slick.util.*;
import org.newdawn.slick.*;

public class ImageFormatTest extends BasicGame
{
    private Image rgbaImage;
    private Image rgbImage;
    private Image grayScaleImage;
    private Image grayScaleAlphaImage;
    private int rgbaImageSize;
    private int rgbImageSize;
    private int grayScaleImageSize;
    private int grayScaleAlphaImageSize;
    
    public ImageFormatTest() {
        super("Image Format Test");
    }
    
    @Override
    public void init(final GameContainer container) throws SlickException {
        try {
            ResourceLoader.addResourceLocation(new FileSystemLocation(new File("./trunk/Slick/")));
        }
        catch (Exception ex) {}
        this.rgbaImage = new Image("testdata/logo.png");
        this.rgbImage = new Image("testdata/logo_rgb.png");
        this.grayScaleImage = new Image("testdata/logo_luminance.png");
        this.grayScaleAlphaImage = new Image("testdata/logo_luminance_alpha.png");
        this.rgbaImageSize = this.rgbaImage.getTexture().getTextureData().length;
        this.rgbImageSize = this.rgbImage.getTexture().getTextureData().length;
        this.grayScaleImageSize = this.grayScaleImage.getTexture().getTextureData().length;
        this.grayScaleAlphaImageSize = this.grayScaleAlphaImage.getTexture().getTextureData().length;
    }
    
    @Override
    public void update(final GameContainer container, final int delta) throws SlickException {
    }
    
    @Override
    public void render(final GameContainer container, final Graphics g) throws SlickException {
        final int effectiveWidth = container.getWidth() - this.rgbaImage.getWidth();
        final int effectiveHeight = container.getHeight() - this.rgbaImage.getHeight();
        final int xStep = effectiveWidth / 4;
        final int yStep = effectiveHeight / 4;
        g.clear();
        g.setColor(Color.blue);
        g.fillRect(0.0f, 0.0f, 800.0f, 600.0f);
        g.setColor(Color.orange);
        int posX = xStep / 2;
        int posY = yStep / 2;
        g.drawImage(this.rgbaImage, (float)posX, (float)posY);
        g.drawString(Integer.toString(this.rgbaImageSize) + " Bytes", (float)posX, (float)(posY - 15));
        posX += xStep;
        posY += yStep;
        g.drawImage(this.rgbImage, (float)posX, (float)posY);
        g.drawString(Integer.toString(this.rgbImageSize) + " Bytes", (float)posX, (float)(posY - 15));
        posX += xStep;
        posY += yStep;
        g.drawImage(this.grayScaleAlphaImage, (float)posX, (float)posY);
        g.drawString(Integer.toString(this.grayScaleAlphaImageSize) + " Bytes", (float)posX, (float)(posY - 15));
        posX += xStep;
        posY += yStep;
        g.drawImage(this.grayScaleImage, (float)posX, (float)posY);
        g.drawString(Integer.toString(this.grayScaleImageSize) + " Bytes", (float)posX, (float)(posY - 15));
    }
    
    public static void main(final String[] argv) {
        final boolean sharedContextTest = false;
        try {
            final AppGameContainer container = new AppGameContainer(new ImageFormatTest());
            container.setDisplayMode(800, 600, false);
            container.start();
        }
        catch (SlickException e) {
            e.printStackTrace();
        }
    }
}
