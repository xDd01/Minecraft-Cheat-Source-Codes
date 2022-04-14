package org.newdawn.slick;

public interface Font
{
    int getWidth(final CharSequence p0);
    
    int getHeight(final CharSequence p0);
    
    int getLineHeight();
    
    void drawString(final float p0, final float p1, final CharSequence p2);
    
    void drawString(final float p0, final float p1, final CharSequence p2, final Color p3);
    
    void drawString(final float p0, final float p1, final CharSequence p2, final Color p3, final int p4, final int p5);
}
