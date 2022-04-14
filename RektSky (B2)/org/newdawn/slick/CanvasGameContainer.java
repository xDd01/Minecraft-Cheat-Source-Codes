package org.newdawn.slick;

import java.awt.*;
import org.lwjgl.opengl.*;
import org.lwjgl.*;
import javax.swing.*;
import org.newdawn.slick.util.*;

public class CanvasGameContainer extends Canvas
{
    protected Container container;
    protected Game game;
    private boolean destroyed;
    
    public CanvasGameContainer(final Game game) throws SlickException {
        this(game, false);
    }
    
    public CanvasGameContainer(final Game game, final boolean shared) throws SlickException {
        this.destroyed = false;
        this.game = game;
        this.setIgnoreRepaint(true);
        this.requestFocus();
        this.setSize(500, 500);
        (this.container = new Container(game, shared)).setForceExit(false);
    }
    
    public void start() throws SlickException {
        this.destroyed = false;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Input.disableControllers();
                    try {
                        Display.setParent(CanvasGameContainer.this);
                    }
                    catch (LWJGLException e) {
                        throw new SlickException("Failed to setParent of canvas", e);
                    }
                    CanvasGameContainer.this.container.setup();
                    CanvasGameContainer.this.scheduleUpdate();
                }
                catch (SlickException e2) {
                    e2.printStackTrace();
                    System.exit(0);
                }
            }
        });
    }
    
    private void scheduleUpdate() {
        if (this.destroyed || !this.isVisible()) {
            return;
        }
        if (!this.container.running()) {
            this.container.destroy();
            this.destroyed = true;
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (CanvasGameContainer.this.destroyed) {
                    return;
                }
                try {
                    CanvasGameContainer.this.container.gameLoop();
                }
                catch (SlickException e) {
                    e.printStackTrace();
                }
                CanvasGameContainer.this.container.checkDimensions();
                CanvasGameContainer.this.scheduleUpdate();
            }
        });
    }
    
    public void dispose() {
    }
    
    public GameContainer getContainer() {
        return this.container;
    }
    
    private class Container extends AppGameContainer
    {
        public Container(final Game game, final boolean shared) throws SlickException {
            super(game, CanvasGameContainer.this.getWidth(), CanvasGameContainer.this.getHeight(), false);
            this.width = CanvasGameContainer.this.getWidth();
            this.height = CanvasGameContainer.this.getHeight();
            if (shared) {
                enableSharedContext();
            }
        }
        
        @Override
        protected void updateFPS() {
            super.updateFPS();
        }
        
        @Override
        public boolean running() {
            return super.running() && CanvasGameContainer.this.isDisplayable();
        }
        
        @Override
        public void exit() {
            super.exit();
            if (!CanvasGameContainer.this.destroyed) {
                this.destroy();
                CanvasGameContainer.this.destroyed = true;
            }
        }
        
        @Override
        public int getHeight() {
            return CanvasGameContainer.this.getHeight();
        }
        
        @Override
        public int getWidth() {
            return CanvasGameContainer.this.getWidth();
        }
        
        public void checkDimensions() {
            if (this.width == CanvasGameContainer.this.getWidth()) {
                if (this.height == CanvasGameContainer.this.getHeight()) {
                    return;
                }
            }
            try {
                this.setDisplayMode(CanvasGameContainer.this.getWidth(), CanvasGameContainer.this.getHeight(), false);
            }
            catch (SlickException e) {
                Log.error(e);
            }
        }
    }
}
