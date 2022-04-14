package org.apache.http.protocol;

import org.apache.http.*;
import java.util.*;

@Deprecated
public interface HttpResponseInterceptorList
{
    void addResponseInterceptor(final HttpResponseInterceptor p0);
    
    void addResponseInterceptor(final HttpResponseInterceptor p0, final int p1);
    
    int getResponseInterceptorCount();
    
    HttpResponseInterceptor getResponseInterceptor(final int p0);
    
    void clearResponseInterceptors();
    
    void removeResponseInterceptorByClass(final Class<? extends HttpResponseInterceptor> p0);
    
    void setInterceptors(final List<?> p0);
}
