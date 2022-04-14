package org.newdawn.slick.svg.inkscape;

import org.w3c.dom.*;
import org.newdawn.slick.geom.*;
import org.newdawn.slick.svg.*;

public class GroupProcessor implements ElementProcessor
{
    @Override
    public boolean handles(final Element element) {
        return element.getNodeName().equals("g");
    }
    
    @Override
    public void process(final Loader loader, final Element element, final Diagram diagram, final Transform t) throws ParsingException {
        Transform transform = Util.getTransform(element);
        transform = new Transform(t, transform);
        loader.loadChildren(element, transform);
    }
}
