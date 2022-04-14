package org.newdawn.slick.tests.shader;

import org.newdawn.slick.opengl.shader.*;
import org.lwjgl.*;
import org.newdawn.slick.*;

public class ShaderLesson1 extends BasicGame
{
    private ShaderProgram program;
    private boolean shaderWorks;
    
    public static void main(final String[] args) throws SlickException {
        new AppGameContainer(new ShaderLesson1(), 800, 600, false).start();
    }
    
    public ShaderLesson1() {
        super("Shader Lesson 1");
        this.shaderWorks = false;
    }
    
    @Override
    public void init(final GameContainer container) throws SlickException {
        if (!ShaderProgram.isSupported()) {
            Sys.alert("Error", "Your graphics card doesn't support OpenGL shaders.");
            container.exit();
            return;
        }
        try {
            final String VERT = "testdata/shaders/pass.vert";
            final String FRAG = "testdata/shaders/lesson1.frag";
            this.program = ShaderProgram.loadProgram("testdata/shaders/pass.vert", "testdata/shaders/lesson1.frag");
            this.shaderWorks = true;
        }
        catch (SlickException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void render(final GameContainer container, final Graphics g) throws SlickException {
        if (this.shaderWorks) {
            this.program.bind();
        }
        g.fillRect(220.0f, 200.0f, 50.0f, 50.0f);
        if (this.shaderWorks) {
            this.program.unbind();
        }
        final String txt = this.shaderWorks ? "Shader works!" : "Shader did not compile, check log";
        g.drawString(txt, 10.0f, 25.0f);
    }
    
    @Override
    public void update(final GameContainer container, final int delta) throws SlickException {
    }
}
