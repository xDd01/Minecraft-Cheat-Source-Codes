package org.newdawn.slick.muffin;

import java.util.*;
import java.io.*;

public interface Muffin
{
    void saveFile(final HashMap p0, final String p1) throws IOException;
    
    HashMap loadFile(final String p0) throws IOException;
}
