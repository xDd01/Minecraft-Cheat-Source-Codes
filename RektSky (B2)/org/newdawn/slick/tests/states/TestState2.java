package org.newdawn.slick.tests.states;

import org.newdawn.slick.state.*;
import org.newdawn.slick.gui.*;
import org.newdawn.slick.*;
import org.newdawn.slick.state.transition.*;

public class TestState2 extends BasicGameState
{
    public static final int ID = 2;
    private Font font;
    private Image image;
    private float ang;
    private StateBasedGame game;
    private TextField username;
    
    @Override
    public int getID() {
        return 2;
    }
    
    @Override
    public void init(final GameContainer container, final StateBasedGame game) throws SlickException {
        this.game = game;
        this.font = new AngelCodeFont("testdata/demo2.fnt", "testdata/demo2.png");
        this.image = new Image("testdata/logo.tga");
        (this.username = new TextField(container, container.getDefaultFont(), 500, 500, 200, 20)).setBorderColor(Color.black);
        this.username.setBackgroundColor(Color.white);
        this.username.setTextColor(Color.black);
    }
    
    @Override
    public void render(final GameContainer container, final StateBasedGame game, final Graphics g) {
        g.setFont(this.font);
        g.setColor(Color.green);
        g.drawString("This is State 2", 200.0f, 50.0f);
        this.username.render(container, g);
        g.rotate(400.0f, 300.0f, this.ang);
        g.drawImage(this.image, (float)(400 - this.image.getWidth() / 2), (float)(300 - this.image.getHeight() / 2));
    }
    
    @Override
    public void update(final GameContainer container, final StateBasedGame game, final int delta) {
        this.ang += delta * 0.1f;
    }
    
    @Override
    public void keyReleased(final int key, final char c) {
        if (key == 2) {
            this.game.enterState(1, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
        }
        if (key == 4) {
            this.game.enterState(3, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
        }
    }
}
