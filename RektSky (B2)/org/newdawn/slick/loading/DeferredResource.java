package org.newdawn.slick.loading;

import java.io.*;

public interface DeferredResource
{
    void load() throws IOException;
    
    String getDescription();
}
