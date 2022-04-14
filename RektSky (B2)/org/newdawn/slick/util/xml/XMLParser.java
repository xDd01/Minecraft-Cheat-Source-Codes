package org.newdawn.slick.util.xml;

import org.newdawn.slick.util.*;
import org.newdawn.slick.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class XMLParser
{
    private static DocumentBuilderFactory factory;
    
    public XMLElement parse(final String ref) throws SlickException {
        return this.parse(ref, ResourceLoader.getResourceAsStream(ref));
    }
    
    public XMLElement parse(final String name, final InputStream in) throws SlickXMLException {
        try {
            if (XMLParser.factory == null) {
                XMLParser.factory = DocumentBuilderFactory.newInstance();
            }
            final DocumentBuilder builder = XMLParser.factory.newDocumentBuilder();
            final Document doc = builder.parse(in);
            return new XMLElement(doc.getDocumentElement());
        }
        catch (Exception e) {
            throw new SlickXMLException("Failed to parse document: " + name, e);
        }
    }
}
