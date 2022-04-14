package org.newdawn.slick.tests.states;

import org.newdawn.slick.state.*;
import org.newdawn.slick.*;
import org.newdawn.slick.state.transition.*;

public class TestState1 extends BasicGameState
{
    public static final int ID = 1;
    private Font font;
    private StateBasedGame game;
    
    @Override
    public int getID() {
        return 1;
    }
    
    @Override
    public void init(final GameContainer container, final StateBasedGame game) throws SlickException {
        this.game = game;
        this.font = new AngelCodeFont("testdata/demo2.fnt", "testdata/demo2.png");
        System.out.println("blah init");
    }
    
    @Override
    public void render(final GameContainer container, final StateBasedGame game, final Graphics g) {
        g.setFont(this.font);
        g.setColor(Color.white);
        g.drawString("State Based Game Test", 100.0f, 100.0f);
        g.drawString("Numbers 1-3 will switch between states.", 150.0f, 300.0f);
        g.setColor(Color.red);
        g.drawString("This is State 1", 200.0f, 50.0f);
    }
    
    @Override
    public void update(final GameContainer container, final StateBasedGame game, final int delta) {
    }
    
    @Override
    public void keyReleased(final int key, final char c) {
        if (key == 3) {
            this.game.enterState(2, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
        }
        if (key == 4) {
            this.game.enterState(3, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
        }
    }
}
