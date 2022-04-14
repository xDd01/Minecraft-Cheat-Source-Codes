package org.apache.logging.log4j.core.appender.db.nosql;

import java.io.*;

public interface NoSQLConnection<W, T extends NoSQLObject<W>> extends Closeable
{
    T createObject();
    
    T[] createList(final int p0);
    
    void insertObject(final NoSQLObject<W> p0);
    
    void close();
    
    boolean isClosed();
}
