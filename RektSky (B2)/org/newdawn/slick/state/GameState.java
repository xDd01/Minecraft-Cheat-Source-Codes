package org.newdawn.slick.state;

import org.newdawn.slick.util.*;
import org.newdawn.slick.*;

public interface GameState extends InputListener, Pauseable
{
    int getID();
    
    void init(final GameContainer p0, final StateBasedGame p1) throws SlickException;
    
    void render(final GameContainer p0, final StateBasedGame p1, final Graphics p2) throws SlickException;
    
    void update(final GameContainer p0, final StateBasedGame p1, final int p2) throws SlickException;
    
    void enter(final GameContainer p0, final StateBasedGame p1) throws SlickException;
    
    void leave(final GameContainer p0, final StateBasedGame p1) throws SlickException;
}
