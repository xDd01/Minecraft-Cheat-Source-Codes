package org.newdawn.slick.util;

import java.io.*;
import java.net.*;

public interface ResourceLocation
{
    InputStream getResourceAsStream(final String p0);
    
    URL getResource(final String p0);
}
