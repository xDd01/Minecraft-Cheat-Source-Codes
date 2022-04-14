package org.newdawn.slick.tests;

import org.newdawn.slick.particles.effects.*;
import org.newdawn.slick.particles.*;
import org.newdawn.slick.*;

public class ParticleTest extends BasicGame
{
    private ParticleSystem system;
    private int mode;
    
    public ParticleTest() {
        super("Particle Test");
        this.mode = 2;
    }
    
    @Override
    public void init(final GameContainer container) throws SlickException {
        final Image image = new Image("testdata/particle.tga", true);
        (this.system = new ParticleSystem(image)).addEmitter(new FireEmitter(400, 300, 45.0f));
        this.system.addEmitter(new FireEmitter(200, 300, 60.0f));
        this.system.addEmitter(new FireEmitter(600, 300, 30.0f));
    }
    
    @Override
    public void render(final GameContainer container, final Graphics g) {
        for (int i = 0; i < 100; ++i) {
            g.translate(1.0f, 1.0f);
            this.system.render();
        }
        g.resetTransform();
        g.drawString("Press space to toggle blending mode", 200.0f, 500.0f);
        g.drawString("Particle Count: " + this.system.getParticleCount() * 100, 200.0f, 520.0f);
    }
    
    @Override
    public void update(final GameContainer container, final int delta) {
        this.system.update(delta);
    }
    
    @Override
    public void keyPressed(final int key, final char c) {
        if (key == 1) {
            System.exit(0);
        }
        if (key == 57) {
            this.mode = ((1 == this.mode) ? 2 : 1);
            this.system.setBlendingMode(this.mode);
        }
    }
    
    public static void main(final String[] argv) {
        try {
            final AppGameContainer container = new AppGameContainer(new ParticleTest());
            container.setDisplayMode(800, 600, false);
            container.start();
        }
        catch (SlickException e) {
            e.printStackTrace();
        }
    }
}
