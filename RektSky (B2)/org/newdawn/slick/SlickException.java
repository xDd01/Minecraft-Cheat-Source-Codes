package org.newdawn.slick;

public class SlickException extends Exception
{
    public SlickException(final String message) {
        super(message);
    }
    
    public SlickException(final String message, final Throwable e) {
        super(message, e);
    }
}
