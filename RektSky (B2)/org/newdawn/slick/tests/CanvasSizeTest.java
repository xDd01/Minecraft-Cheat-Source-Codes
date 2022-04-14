package org.newdawn.slick.tests;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import org.newdawn.slick.*;

public class CanvasSizeTest extends BasicGame
{
    private Image image;
    
    public CanvasSizeTest() {
        super("Test");
    }
    
    @Override
    public void init(final GameContainer container) throws SlickException {
        System.out.println(container.getWidth() + ", " + container.getHeight());
        container.getGraphics().setBackground(Color.gray);
        this.image = new Image("testdata/logo.png");
    }
    
    @Override
    public void render(final GameContainer container, final Graphics g) throws SlickException {
        this.image.draw(container.getWidth() / 2.0f - this.image.getWidth() / 2.0f, container.getHeight() / 2.0f - this.image.getHeight() / 2.0f);
    }
    
    @Override
    public void update(final GameContainer container, final int delta) throws SlickException {
    }
    
    public static void main(final String[] argv) {
        try {
            final Game game = new CanvasSizeTest();
            final CanvasGameContainer container = new CanvasGameContainer(game);
            final JFrame frame = new JFrame(game.getTitle());
            frame.setDefaultCloseOperation(3);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(final WindowEvent we) {
                    frame.setVisible(false);
                    container.getContainer().exit();
                }
            });
            frame.getContentPane().setBackground(java.awt.Color.black);
            container.setSize(800, 600);
            frame.getContentPane().add(container);
            frame.pack();
            frame.setResizable(true);
            frame.setLocationRelativeTo(null);
            container.requestFocusInWindow();
            frame.setVisible(true);
            container.start();
        }
        catch (SlickException ex) {
            ex.printStackTrace();
        }
    }
}
