package org.apache.http.impl;

import org.apache.http.annotation.*;
import org.apache.http.util.*;
import org.apache.http.protocol.*;
import org.apache.http.message.*;
import java.util.*;
import org.apache.http.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultHttpResponseFactory implements HttpResponseFactory
{
    public static final DefaultHttpResponseFactory INSTANCE;
    protected final ReasonPhraseCatalog reasonCatalog;
    
    public DefaultHttpResponseFactory(final ReasonPhraseCatalog catalog) {
        this.reasonCatalog = Args.notNull(catalog, "Reason phrase catalog");
    }
    
    public DefaultHttpResponseFactory() {
        this(EnglishReasonPhraseCatalog.INSTANCE);
    }
    
    @Override
    public HttpResponse newHttpResponse(final ProtocolVersion ver, final int status, final HttpContext context) {
        Args.notNull(ver, "HTTP version");
        final Locale loc = this.determineLocale(context);
        final String reason = this.reasonCatalog.getReason(status, loc);
        final StatusLine statusline = new BasicStatusLine(ver, status, reason);
        return new BasicHttpResponse(statusline, this.reasonCatalog, loc);
    }
    
    @Override
    public HttpResponse newHttpResponse(final StatusLine statusline, final HttpContext context) {
        Args.notNull(statusline, "Status line");
        return new BasicHttpResponse(statusline, this.reasonCatalog, this.determineLocale(context));
    }
    
    protected Locale determineLocale(final HttpContext context) {
        return Locale.getDefault();
    }
    
    static {
        INSTANCE = new DefaultHttpResponseFactory();
    }
}
