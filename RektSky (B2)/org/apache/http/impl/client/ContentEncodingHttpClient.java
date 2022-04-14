package org.apache.http.impl.client;

import org.apache.http.annotation.*;
import org.apache.http.conn.*;
import org.apache.http.params.*;
import org.apache.http.protocol.*;
import org.apache.http.client.protocol.*;
import org.apache.http.*;

@Deprecated
@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public class ContentEncodingHttpClient extends DefaultHttpClient
{
    public ContentEncodingHttpClient(final ClientConnectionManager conman, final HttpParams params) {
        super(conman, params);
    }
    
    public ContentEncodingHttpClient(final HttpParams params) {
        this(null, params);
    }
    
    public ContentEncodingHttpClient() {
        this((HttpParams)null);
    }
    
    @Override
    protected BasicHttpProcessor createHttpProcessor() {
        final BasicHttpProcessor result = super.createHttpProcessor();
        result.addRequestInterceptor(new RequestAcceptEncoding());
        result.addResponseInterceptor(new ResponseContentEncoding());
        return result;
    }
}
