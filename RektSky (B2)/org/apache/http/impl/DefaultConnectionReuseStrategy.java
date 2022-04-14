package org.apache.http.impl;

import org.apache.http.annotation.*;
import org.apache.http.protocol.*;
import org.apache.http.util.*;
import org.apache.http.message.*;
import org.apache.http.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class DefaultConnectionReuseStrategy implements ConnectionReuseStrategy
{
    public static final DefaultConnectionReuseStrategy INSTANCE;
    
    @Override
    public boolean keepAlive(final HttpResponse response, final HttpContext context) {
        Args.notNull(response, "HTTP response");
        Args.notNull(context, "HTTP context");
        if (response.getStatusLine().getStatusCode() == 204) {
            final Header clh = response.getFirstHeader("Content-Length");
            if (clh != null) {
                try {
                    final int contentLen = Integer.parseInt(clh.getValue());
                    if (contentLen > 0) {
                        return false;
                    }
                }
                catch (NumberFormatException ex2) {}
            }
            final Header teh = response.getFirstHeader("Transfer-Encoding");
            if (teh != null) {
                return false;
            }
        }
        final HttpRequest request = (HttpRequest)context.getAttribute("http.request");
        if (request != null) {
            try {
                final TokenIterator ti = new BasicTokenIterator(request.headerIterator("Connection"));
                while (ti.hasNext()) {
                    final String token = ti.nextToken();
                    if ("Close".equalsIgnoreCase(token)) {
                        return false;
                    }
                }
            }
            catch (ParseException px) {
                return false;
            }
        }
        final ProtocolVersion ver = response.getStatusLine().getProtocolVersion();
        final Header teh2 = response.getFirstHeader("Transfer-Encoding");
        if (teh2 != null) {
            if (!"chunked".equalsIgnoreCase(teh2.getValue())) {
                return false;
            }
        }
        else if (this.canResponseHaveBody(request, response)) {
            final Header[] clhs = response.getHeaders("Content-Length");
            if (clhs.length != 1) {
                return false;
            }
            final Header clh2 = clhs[0];
            try {
                final int contentLen2 = Integer.parseInt(clh2.getValue());
                if (contentLen2 < 0) {
                    return false;
                }
            }
            catch (NumberFormatException ex) {
                return false;
            }
        }
        HeaderIterator headerIterator = response.headerIterator("Connection");
        if (!headerIterator.hasNext()) {
            headerIterator = response.headerIterator("Proxy-Connection");
        }
        if (headerIterator.hasNext()) {
            try {
                final TokenIterator ti2 = new BasicTokenIterator(headerIterator);
                boolean keepalive = false;
                while (ti2.hasNext()) {
                    final String token2 = ti2.nextToken();
                    if ("Close".equalsIgnoreCase(token2)) {
                        return false;
                    }
                    if (!"Keep-Alive".equalsIgnoreCase(token2)) {
                        continue;
                    }
                    keepalive = true;
                }
                if (keepalive) {
                    return true;
                }
            }
            catch (ParseException px2) {
                return false;
            }
        }
        return !ver.lessEquals(HttpVersion.HTTP_1_0);
    }
    
    protected TokenIterator createTokenIterator(final HeaderIterator hit) {
        return new BasicTokenIterator(hit);
    }
    
    private boolean canResponseHaveBody(final HttpRequest request, final HttpResponse response) {
        if (request != null && request.getRequestLine().getMethod().equalsIgnoreCase("HEAD")) {
            return false;
        }
        final int status = response.getStatusLine().getStatusCode();
        return status >= 200 && status != 204 && status != 304 && status != 205;
    }
    
    static {
        INSTANCE = new DefaultConnectionReuseStrategy();
    }
}
