package org.apache.http.client;

import org.apache.http.params.*;
import org.apache.http.conn.*;
import org.apache.http.client.methods.*;
import java.io.*;
import org.apache.http.protocol.*;
import org.apache.http.*;

public interface HttpClient
{
    @Deprecated
    HttpParams getParams();
    
    @Deprecated
    ClientConnectionManager getConnectionManager();
    
    HttpResponse execute(final HttpUriRequest p0) throws IOException, ClientProtocolException;
    
    HttpResponse execute(final HttpUriRequest p0, final HttpContext p1) throws IOException, ClientProtocolException;
    
    HttpResponse execute(final HttpHost p0, final HttpRequest p1) throws IOException, ClientProtocolException;
    
    HttpResponse execute(final HttpHost p0, final HttpRequest p1, final HttpContext p2) throws IOException, ClientProtocolException;
    
     <T> T execute(final HttpUriRequest p0, final ResponseHandler<? extends T> p1) throws IOException, ClientProtocolException;
    
     <T> T execute(final HttpUriRequest p0, final ResponseHandler<? extends T> p1, final HttpContext p2) throws IOException, ClientProtocolException;
    
     <T> T execute(final HttpHost p0, final HttpRequest p1, final ResponseHandler<? extends T> p2) throws IOException, ClientProtocolException;
    
     <T> T execute(final HttpHost p0, final HttpRequest p1, final ResponseHandler<? extends T> p2, final HttpContext p3) throws IOException, ClientProtocolException;
}
