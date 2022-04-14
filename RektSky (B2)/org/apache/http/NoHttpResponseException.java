package org.apache.http;

import java.io.*;

public class NoHttpResponseException extends IOException
{
    private static final long serialVersionUID = -7658940387386078766L;
    
    public NoHttpResponseException(final String message) {
        super(HttpException.clean(message));
    }
}
