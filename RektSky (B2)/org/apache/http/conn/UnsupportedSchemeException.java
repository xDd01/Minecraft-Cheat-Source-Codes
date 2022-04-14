package org.apache.http.conn;

import java.io.*;

public class UnsupportedSchemeException extends IOException
{
    private static final long serialVersionUID = 3597127619218687636L;
    
    public UnsupportedSchemeException(final String message) {
        super(message);
    }
}
