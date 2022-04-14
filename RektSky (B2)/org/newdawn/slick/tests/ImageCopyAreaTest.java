package org.newdawn.slick.tests;

import org.newdawn.slick.*;

public class ImageCopyAreaTest extends BasicGame
{
    private Image logo;
    private Image background;
    private Graphics renderGraphics;
    private Image renderImage;
    private Image copiedImage;
    private Image postCopy;
    
    public ImageCopyAreaTest() {
        super("ImageCopyAreaTest");
    }
    
    @Override
    public void init(final GameContainer container) throws SlickException {
        this.logo = new Image("testdata/logo.png");
        this.background = new Image("testdata/sky.jpg");
        this.renderImage = Image.createOffscreenImage(256, 356);
        (this.renderGraphics = this.renderImage.getGraphics()).setColor(Color.pink);
        this.renderGraphics.fillRoundRect(0.0f, 0.0f, 256.0f, 256.0f, 15);
        this.renderGraphics.drawImage(this.logo, 0.0f, 0.0f);
        this.copiedImage = new Image(256, 256);
        this.postCopy = new Image(256, 256);
        this.renderGraphics.copyArea(this.copiedImage, 50, 50, 0, 0, 50, 50);
        this.renderGraphics.copyArea(this.copiedImage, 0, 0, 50, 0, 50, 50);
        this.renderGraphics.flush();
    }
    
    @Override
    public void render(final GameContainer container, final Graphics g) throws SlickException {
        this.background.draw(0.0f, 0.0f, (float)container.getWidth(), (float)container.getHeight());
        g.drawImage(this.renderImage, 100.0f, 172.0f);
        g.drawImage(this.copiedImage, 444.0f, 172.0f);
        g.copyArea(this.postCopy, 100, 172);
        this.postCopy.draw(444.0f, 350.0f);
    }
    
    public static void main(final String[] argv) {
        try {
            final AppGameContainer container = new AppGameContainer(new ImageCopyAreaTest());
            container.setDisplayMode(800, 600, false);
            container.start();
        }
        catch (SlickException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void update(final GameContainer container, final int delta) throws SlickException {
    }
}
