package org.apache.http.protocol;

@Deprecated
public interface HttpRequestHandlerResolver
{
    HttpRequestHandler lookup(final String p0);
}
