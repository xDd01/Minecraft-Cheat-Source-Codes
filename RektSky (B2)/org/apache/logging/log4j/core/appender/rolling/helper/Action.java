package org.apache.logging.log4j.core.appender.rolling.helper;

import java.io.*;

public interface Action extends Runnable
{
    boolean execute() throws IOException;
    
    void close();
    
    boolean isComplete();
}
