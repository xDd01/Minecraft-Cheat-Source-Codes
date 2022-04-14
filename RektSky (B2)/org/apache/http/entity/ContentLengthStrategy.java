package org.apache.http.entity;

import org.apache.http.*;

public interface ContentLengthStrategy
{
    public static final int IDENTITY = -1;
    public static final int CHUNKED = -2;
    
    long determineLength(final HttpMessage p0) throws HttpException;
}
