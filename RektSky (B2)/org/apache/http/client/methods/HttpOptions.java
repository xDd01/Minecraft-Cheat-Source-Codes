package org.apache.http.client.methods;

import java.net.*;
import org.apache.http.util.*;
import java.util.*;
import org.apache.http.*;

public class HttpOptions extends HttpRequestBase
{
    public static final String METHOD_NAME = "OPTIONS";
    
    public HttpOptions() {
    }
    
    public HttpOptions(final URI uri) {
        this.setURI(uri);
    }
    
    public HttpOptions(final String uri) {
        this.setURI(URI.create(uri));
    }
    
    @Override
    public String getMethod() {
        return "OPTIONS";
    }
    
    public Set<String> getAllowedMethods(final HttpResponse response) {
        Args.notNull(response, "HTTP response");
        final HeaderIterator it = response.headerIterator("Allow");
        final Set<String> methods = new HashSet<String>();
        while (it.hasNext()) {
            final Header header = it.nextHeader();
            final HeaderElement[] arr$;
            final HeaderElement[] elements = arr$ = header.getElements();
            for (final HeaderElement element : arr$) {
                methods.add(element.getName());
            }
        }
        return methods;
    }
}
