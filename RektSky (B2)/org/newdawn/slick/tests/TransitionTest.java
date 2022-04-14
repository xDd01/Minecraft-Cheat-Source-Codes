package org.newdawn.slick.tests;

import org.newdawn.slick.state.transition.*;
import org.newdawn.slick.util.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.*;

public class TransitionTest extends StateBasedGame
{
    private Class[][] transitions;
    private int index;
    
    public TransitionTest() {
        super("Transition Test - Hit Space To Transition");
        this.transitions = new Class[][] { { null, VerticalSplitTransition.class }, { FadeOutTransition.class, FadeInTransition.class }, { null, RotateTransition.class }, { null, HorizontalSplitTransition.class }, { null, BlobbyTransition.class }, { null, SelectTransition.class } };
    }
    
    @Override
    public void initStatesList(final GameContainer container) throws SlickException {
        this.addState(new ImageState(0, "testdata/wallpaper/paper1.png", 1));
        this.addState(new ImageState(1, "testdata/wallpaper/paper2.png", 2));
        this.addState(new ImageState(2, "testdata/bigimage.tga", 0));
    }
    
    public Transition[] getNextTransitionPair() {
        final Transition[] pair = new Transition[2];
        try {
            if (this.transitions[this.index][0] != null) {
                pair[0] = this.transitions[this.index][0].newInstance();
            }
            if (this.transitions[this.index][1] != null) {
                pair[1] = this.transitions[this.index][1].newInstance();
            }
        }
        catch (Throwable e) {
            Log.error(e);
        }
        ++this.index;
        if (this.index >= this.transitions.length) {
            this.index = 0;
        }
        return pair;
    }
    
    public static void main(final String[] argv) {
        try {
            final AppGameContainer container = new AppGameContainer(new TransitionTest());
            container.setDisplayMode(800, 600, false);
            container.start();
        }
        catch (SlickException e) {
            e.printStackTrace();
        }
    }
    
    private class ImageState extends BasicGameState
    {
        private int id;
        private int next;
        private String ref;
        private Image image;
        
        public ImageState(final int id, final String ref, final int next) {
            this.ref = ref;
            this.id = id;
            this.next = next;
        }
        
        @Override
        public int getID() {
            return this.id;
        }
        
        @Override
        public void init(final GameContainer container, final StateBasedGame game) throws SlickException {
            this.image = new Image(this.ref);
        }
        
        @Override
        public void render(final GameContainer container, final StateBasedGame game, final Graphics g) throws SlickException {
            this.image.draw(0.0f, 0.0f, 800.0f, 600.0f);
            g.setColor(Color.red);
            g.fillRect(-50.0f, 200.0f, 50.0f, 50.0f);
        }
        
        @Override
        public void update(final GameContainer container, final StateBasedGame game, final int delta) throws SlickException {
            if (container.getInput().isKeyPressed(57)) {
                final Transition[] pair = TransitionTest.this.getNextTransitionPair();
                game.enterState(this.next, pair[0], pair[1]);
            }
        }
    }
}
