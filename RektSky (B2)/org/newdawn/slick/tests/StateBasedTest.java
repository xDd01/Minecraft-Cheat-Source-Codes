package org.newdawn.slick.tests;

import org.newdawn.slick.state.*;
import org.newdawn.slick.tests.states.*;
import org.newdawn.slick.*;

public class StateBasedTest extends StateBasedGame
{
    public StateBasedTest() {
        super("State Based Test");
        this.addState(new TestState1());
        this.addState(new TestState2());
        this.addState(new TestState3());
        this.enterState(1);
    }
    
    @Override
    public void initStatesList(final GameContainer container) {
    }
    
    public static void main(final String[] argv) {
        try {
            final AppGameContainer container = new AppGameContainer(new StateBasedTest());
            container.setDisplayMode(800, 600, false);
            container.start();
        }
        catch (SlickException e) {
            e.printStackTrace();
        }
    }
}
