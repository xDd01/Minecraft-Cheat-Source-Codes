package org.newdawn.slick.tests;

import org.newdawn.slick.state.*;
import org.newdawn.slick.tests.states.*;
import org.newdawn.slick.*;

public class StateBasedPauseTest extends StateBasedGame
{
    public StateBasedPauseTest() {
        super("State Based Test");
    }
    
    @Override
    public void initStatesList(final GameContainer container) {
        this.addState(new TestState1());
        this.addState(new TestState2());
        this.addState(new TestState3());
    }
    
    @Override
    protected void preUpdateState(final GameContainer container, final int delta) throws SlickException {
        if (container.getInput().isKeyPressed(30)) {
            this.setUpdatePaused(!this.isUpdatePaused());
        }
        if (container.getInput().isKeyPressed(31)) {
            this.setRenderPaused(!this.isRenderPaused());
        }
        if (container.getInput().isKeyPressed(32)) {
            this.getState(2).setUpdatePaused(!this.getState(2).isUpdatePaused());
        }
        if (container.getInput().isKeyPressed(33)) {
            this.getState(3).setRenderPaused(!this.getState(3).isRenderPaused());
        }
    }
    
    @Override
    protected void postRenderState(final GameContainer container, final Graphics g) throws SlickException {
        g.resetTransform();
        g.resetFont();
        g.setColor(Color.white);
        g.drawString("Current State:" + this.getCurrentStateID(), 10.0f, 25.0f);
        g.drawString("Press A to pause/unpause update calls on the current state. Paused: " + this.isUpdatePaused(), 10.0f, (float)(container.getHeight() - 100));
        g.drawString("Press S to pause/unpause render calls on the current state. Paused: " + this.isRenderPaused(), 10.0f, (float)(container.getHeight() - 85));
        g.drawString("Press D to pause/unpause update on the state #2. Paused: " + this.getState(2).isUpdatePaused(), 10.0f, (float)(container.getHeight() - 70));
        g.drawString("Press F to pause/unpause render on the state #3. Paused: " + this.getState(3).isRenderPaused(), 10.0f, (float)(container.getHeight() - 55));
    }
    
    public static void main(final String[] argv) {
        try {
            final AppGameContainer container = new AppGameContainer(new StateBasedPauseTest());
            container.setDisplayMode(800, 600, false);
            container.start();
        }
        catch (SlickException e) {
            e.printStackTrace();
        }
    }
}
