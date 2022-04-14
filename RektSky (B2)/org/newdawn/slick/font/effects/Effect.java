package org.newdawn.slick.font.effects;

import java.awt.image.*;
import java.awt.*;
import org.newdawn.slick.*;
import org.newdawn.slick.font.*;

public interface Effect
{
    void draw(final BufferedImage p0, final Graphics2D p1, final UnicodeFont p2, final Glyph p3);
}
