package org.newdawn.slick.tests.shader;

import org.newdawn.slick.opengl.shader.*;
import org.newdawn.slick.util.*;
import org.newdawn.slick.*;

public class ShaderTest extends BasicGame
{
    private Image logo;
    private ShaderProgram program;
    private String log;
    private boolean shaderWorks;
    private boolean useShader;
    private boolean supported;
    private float elapsed;
    private GameContainer container;
    
    public static void main(final String[] args) throws SlickException {
        new AppGameContainer(new ShaderTest(), 800, 600, false).start();
    }
    
    public ShaderTest() {
        super("Simple Shader Test");
        this.useShader = true;
        this.supported = false;
    }
    
    @Override
    public void init(final GameContainer container) throws SlickException {
        this.container = container;
        this.logo = new Image("testdata/logo.png");
        container.getGraphics().setBackground(Color.darkGray);
        this.supported = ShaderProgram.isSupported();
        if (this.supported) {
            ShaderProgram.setStrictMode(false);
            this.reload();
        }
    }
    
    private void reload() {
        if (!this.supported) {
            return;
        }
        if (this.program != null) {
            this.program.release();
        }
        try {
            this.program = ShaderProgram.loadProgram("testdata/shaders/invert.vert", "testdata/shaders/invert.frag");
            this.shaderWorks = true;
            this.log = this.program.getLog();
            if (this.log != null && this.log.length() != 0) {
                Log.warn(this.log);
            }
            this.program.bind();
            this.program.setUniform1i("tex0", 0);
            this.program.unbind();
        }
        catch (Exception e) {
            Log.error(this.log = e.getMessage());
            this.shaderWorks = false;
        }
    }
    
    @Override
    public void render(final GameContainer container, final Graphics g) throws SlickException {
        if (this.shaderWorks && this.useShader) {
            this.program.bind();
        }
        g.drawImage(this.logo, 100.0f, 300.0f);
        if (this.shaderWorks && this.useShader) {
            this.program.unbind();
        }
        if (this.shaderWorks) {
            g.drawString("Space to toggle shader\nPress R to reload shaders", 10.0f, 25.0f);
        }
        else if (!this.supported) {
            g.drawString("Your drivers do not support OpenGL Shaders, sorry!", 10.0f, 25.0f);
        }
        else {
            g.drawString("Oops, shader didn't load!", 10.0f, 25.0f);
        }
        if (this.log != null && this.log.length() != 0) {
            g.drawString(this.log, 10.0f, 75.0f);
        }
    }
    
    @Override
    public void update(final GameContainer container, final int delta) throws SlickException {
        if (container.getInput().isKeyPressed(57)) {
            this.useShader = !this.useShader;
        }
        if (container.getInput().isKeyPressed(19)) {
            this.reload();
        }
    }
}
