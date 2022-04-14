package org.newdawn.slick.tests;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import org.newdawn.slick.*;

public class CanvasContainerTest extends BasicGame
{
    private Image tga;
    private Image scaleMe;
    private Image scaled;
    private Image gif;
    private Image image;
    private Image subImage;
    private float rot;
    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;
    
    public CanvasContainerTest() {
        super("Canvas Container Test");
    }
    
    @Override
    public void init(final GameContainer container) throws SlickException {
        container.getGraphics().setBackground(Color.darkGray);
        final Image image = new Image("testdata/logo.tga");
        this.tga = image;
        this.image = image;
        this.scaleMe = new Image("testdata/logo.tga", true, 9728);
        this.gif = new Image("testdata/logo.gif");
        this.scaled = this.gif.getScaledCopy(120, 120);
        this.subImage = this.image.getSubImage(200, 0, 70, 260);
        this.rot = 0.0f;
    }
    
    @Override
    public void render(final GameContainer container, final Graphics g) {
        this.image.draw((float)(800 - this.image.getWidth()), 0.0f);
        this.image.draw(0.0f, 0.0f);
        this.scaleMe.draw(500.0f, 100.0f, 200.0f, 100.0f);
        this.scaled.draw(400.0f, 500.0f);
        final Image flipped = this.scaled.getFlippedCopy(true, false);
        flipped.draw(520.0f, 500.0f);
        final Image flipped2 = flipped.getFlippedCopy(false, true);
        flipped2.draw(520.0f, 380.0f);
        final Image flipped3 = flipped2.getFlippedCopy(true, false);
        flipped3.draw(400.0f, 380.0f);
        for (int i = 0; i < 3; ++i) {
            this.subImage.draw((float)(200 + i * 30), 300.0f);
        }
        g.translate(500.0f, 200.0f);
        g.rotate(50.0f, 50.0f, this.rot);
        g.scale(0.3f, 0.3f);
        this.image.draw();
        g.resetTransform();
    }
    
    @Override
    public void update(final GameContainer container, final int delta) {
        this.rot += delta * 0.1f;
        if (this.rot > 360.0f) {
            this.rot -= 360.0f;
        }
    }
    
    public static void main(final String[] argv) {
        try {
            final Game game = new CanvasContainerTest();
            final CanvasGameContainer canvasPanel = new CanvasGameContainer(game);
            final JFrame frame = new JFrame(game.getTitle());
            frame.setDefaultCloseOperation(2);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(final WindowEvent we) {
                    frame.setVisible(false);
                    canvasPanel.getContainer().exit();
                }
            });
            canvasPanel.addKeyListener(new java.awt.event.KeyListener() {
                @Override
                public void keyTyped(final KeyEvent arg0) {
                }
                
                @Override
                public void keyReleased(final KeyEvent arg0) {
                }
                
                @Override
                public void keyPressed(final KeyEvent e) {
                    if (e.getKeyCode() == 10) {
                        final GameContainer container = canvasPanel.getContainer();
                        if (container.running()) {
                            container.exit();
                        }
                        else {
                            try {
                                canvasPanel.start();
                                System.out.println("starting");
                            }
                            catch (SlickException e2) {
                                container.exit();
                                e2.printStackTrace();
                            }
                        }
                    }
                }
            });
            frame.getContentPane().setBackground(java.awt.Color.black);
            final Dimension size = new Dimension(800, 600);
            canvasPanel.setPreferredSize(size);
            canvasPanel.setMinimumSize(size);
            canvasPanel.setMaximumSize(size);
            final GridBagConstraints c = new GridBagConstraints();
            c.fill = 10;
            frame.getContentPane().setLayout(new GridBagLayout());
            frame.getContentPane().add(canvasPanel, c);
            frame.pack();
            frame.setResizable(true);
            frame.setLocationRelativeTo(null);
            canvasPanel.requestFocusInWindow();
            frame.setVisible(true);
            canvasPanel.start();
        }
        catch (SlickException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void keyPressed(final int key, final char c) {
        if (key == 57) {
            if (this.image == this.gif) {
                this.image = this.tga;
            }
            else {
                this.image = this.gif;
            }
        }
    }
}
