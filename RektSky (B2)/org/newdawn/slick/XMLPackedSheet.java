package org.newdawn.slick;

import java.util.*;
import org.newdawn.slick.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class XMLPackedSheet
{
    private Image image;
    private HashMap sprites;
    
    public XMLPackedSheet(final String imageRef, final String xmlRef) throws SlickException {
        this.sprites = new HashMap();
        this.image = new Image(imageRef, false, 9728);
        try {
            final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            final Document doc = builder.parse(ResourceLoader.getResourceAsStream(xmlRef));
            final NodeList list = doc.getElementsByTagName("sprite");
            for (int i = 0; i < list.getLength(); ++i) {
                final Element element = (Element)list.item(i);
                final String name = element.getAttribute("name");
                final int x = Integer.parseInt(element.getAttribute("x"));
                final int y = Integer.parseInt(element.getAttribute("y"));
                final int width = Integer.parseInt(element.getAttribute("width"));
                final int height = Integer.parseInt(element.getAttribute("height"));
                this.sprites.put(name, this.image.getSubImage(x, y, width, height));
            }
        }
        catch (Exception e) {
            throw new SlickException("Failed to parse sprite sheet XML", e);
        }
    }
    
    public Image getSprite(final String name) {
        return this.sprites.get(name);
    }
}
