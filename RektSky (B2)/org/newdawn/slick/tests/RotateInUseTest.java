package org.newdawn.slick.tests;

import org.newdawn.slick.*;

public class RotateInUseTest extends BasicGame
{
    private Image sheet2;
    private Image subImage;
    private SpriteSheet sheet1;
    private float rot1;
    private float rot2;
    
    public static void main(final String[] args) throws SlickException {
        new AppGameContainer(new RotateInUseTest(), 800, 600, false).start();
    }
    
    public RotateInUseTest() {
        super("Rotate In Use");
    }
    
    @Override
    public void init(final GameContainer container) throws SlickException {
        this.sheet1 = new SpriteSheet("testdata/dungeontiles.gif", 32, 32);
        this.sheet1.getSubImage(1, 2).setCenterOfRotation(0.0f, 0.0f);
        this.sheet1.getSubImage(3, 2).setCenterOfRotation(16.0f, 0.0f);
        this.sheet2 = new Image("testdata/logo.tga");
        this.subImage = this.sheet2.getSubImage(40, 40, 50, 50);
    }
    
    @Override
    public void render(final GameContainer container, final Graphics g) throws SlickException {
        this.sheet1.startUse();
        this.sheet1.renderInUse(50, 50, 0, 0);
        this.sheet1.renderInUse(100, 50, 1, 2);
        this.sheet1.renderInUse(150, 50, 64, 64, 1, 2);
        this.sheet1.renderInUse(250, 50, 64, 64, this.rot1, 1, 2);
        this.sheet1.renderInUse(350, 50, 64, 64, this.rot2, 3, 2);
        this.sheet1.renderInUse(450, 50, this.rot1, 4, 4);
        this.sheet1.renderInUse(250, 50, 64, 64, this.rot1, 1, 2);
        this.sheet1.endUse();
        this.sheet2.startUse();
        this.subImage.drawEmbedded(100.0f, 200.0f, (float)this.subImage.getWidth(), (float)this.subImage.getHeight(), this.rot1);
        this.subImage.drawEmbedded(300.0f, 200.0f, this.subImage.getWidth() * 4.0f, this.subImage.getHeight() * 2.0f, this.rot2);
        this.sheet2.endUse();
    }
    
    @Override
    public void update(final GameContainer container, final int delta) throws SlickException {
        this.rot1 += delta * 0.03f;
        this.rot2 += delta * 0.08f;
    }
}
