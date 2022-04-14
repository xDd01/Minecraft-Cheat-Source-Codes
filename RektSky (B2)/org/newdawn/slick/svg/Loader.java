package org.newdawn.slick.svg;

import org.w3c.dom.*;
import org.newdawn.slick.geom.*;

public interface Loader
{
    void loadChildren(final Element p0, final Transform p1) throws ParsingException;
}
