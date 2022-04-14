package org.newdawn.slick.tests.shader;

import org.newdawn.slick.opengl.shader.*;
import org.newdawn.slick.util.*;
import org.newdawn.slick.*;

public class ShaderTestAdvanced extends BasicGame
{
    private Image logo;
    private ShaderProgram blurHoriz;
    private ShaderProgram blurVert;
    private String log;
    private boolean shaderWorks;
    private boolean useBlur;
    private boolean supported;
    private float rot;
    private float radius;
    private GameContainer container;
    private Image postImageA;
    private Image postImageB;
    private Graphics postGraphicsA;
    private Graphics postGraphicsB;
    
    public static void main(final String[] args) throws SlickException {
        new AppGameContainer(new ShaderTestAdvanced(), 800, 600, false).start();
    }
    
    public ShaderTestAdvanced() {
        super("Advanced Shader Test");
        this.useBlur = true;
        this.supported = false;
        this.radius = 1.2f;
    }
    
    @Override
    public void init(final GameContainer container) throws SlickException {
        this.container = container;
        this.logo = new Image("testdata/logo.png");
        container.setClearEachFrame(false);
        this.supported = ShaderProgram.isSupported();
        if (this.supported) {
            try {
                this.postImageA = Image.createOffscreenImage(container.getWidth(), container.getHeight());
                this.postGraphicsA = this.postImageA.getGraphics();
                this.postImageB = Image.createOffscreenImage(container.getWidth(), container.getHeight());
                this.postGraphicsB = this.postImageB.getGraphics();
                final String h = "testdata/shaders/hblur.frag";
                final String v = "testdata/shaders/vblur.frag";
                final String vert = "testdata/shaders/blur.vert";
                this.blurHoriz = ShaderProgram.loadProgram(vert, h);
                this.blurVert = ShaderProgram.loadProgram(vert, v);
                this.shaderWorks = true;
                this.log = this.blurHoriz.getLog() + "\n" + this.blurVert.getLog();
                this.blurHoriz.bind();
                this.blurHoriz.setUniform1i("tex0", 0);
                this.blurHoriz.setUniform1f("resolution", (float)container.getWidth());
                this.blurHoriz.setUniform1f("radius", this.radius);
                this.blurVert.bind();
                this.blurVert.setUniform1i("tex0", 0);
                this.blurVert.setUniform1f("resolution", (float)container.getHeight());
                this.blurVert.setUniform1f("radius", this.radius);
                ShaderProgram.unbindAll();
            }
            catch (SlickException e) {
                Log.error(this.log = e.getMessage());
                this.shaderWorks = false;
            }
        }
    }
    
    public void renderScene(final GameContainer container, final Graphics g) throws SlickException {
        this.logo.setRotation(0.0f);
        g.drawImage(this.logo, 100.0f, 300.0f);
        this.logo.setRotation(this.rot);
        g.drawImage(this.logo, 400.0f, 200.0f);
        g.setColor(Color.white);
        g.fillRect(450.0f, 350.0f, 100.0f, 100.0f);
    }
    
    @Override
    public void render(final GameContainer container, final Graphics screenGraphics) throws SlickException {
        screenGraphics.clear();
        if (this.shaderWorks && this.useBlur) {
            Graphics.setCurrent(this.postGraphicsA);
            this.postGraphicsA.clear();
            this.renderScene(container, this.postGraphicsA);
            this.postGraphicsA.flush();
            this.blurHoriz.bind();
            this.blurHoriz.setUniform1f("radius", this.radius);
            Graphics.setCurrent(this.postGraphicsB);
            this.postGraphicsB.clear();
            this.postGraphicsB.fillRect(0.0f, 0.0f, 800.0f, 600.0f);
            this.postGraphicsB.drawImage(this.postImageA, 0.0f, 0.0f);
            this.postGraphicsB.flush();
            this.blurHoriz.unbind();
            this.blurVert.bind();
            this.blurVert.setUniform1f("radius", this.radius);
            Graphics.setCurrent(screenGraphics);
            screenGraphics.drawImage(this.postImageB, 0.0f, 0.0f);
            ShaderProgram.unbindAll();
        }
        else {
            this.renderScene(container, screenGraphics);
        }
        screenGraphics.setColor(Color.white);
        if (this.shaderWorks) {
            screenGraphics.drawString("B to toggle blur" + (this.useBlur ? " (enabled)" : "") + "\nUP/DOWN to change radius: " + this.radius, 10.0f, 25.0f);
        }
        else if (!this.supported) {
            screenGraphics.drawString("Your drivers do not support OpenGL Shaders, sorry!", 10.0f, 25.0f);
        }
        else {
            screenGraphics.drawString("Oops, shader didn't load!", 10.0f, 25.0f);
        }
        if (this.log != null && this.log.trim().length() != 0) {
            screenGraphics.drawString("Shader Log:\n" + this.log, 10.0f, 75.0f);
        }
    }
    
    @Override
    public void update(final GameContainer container, final int delta) throws SlickException {
        if (container.getInput().isKeyPressed(48)) {
            this.useBlur = !this.useBlur;
        }
        if (container.getInput().isKeyDown(208)) {
            this.radius = Math.max(0.0f, this.radius - 3.0E-4f * delta);
        }
        else if (container.getInput().isKeyDown(200)) {
            this.radius = Math.min(5.0f, this.radius + 3.0E-4f * delta);
        }
        this.rot += 0.03f * delta;
    }
}
