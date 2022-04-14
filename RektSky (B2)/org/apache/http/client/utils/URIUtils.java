package org.apache.http.client.utils;

import java.net.*;
import org.apache.http.*;
import org.apache.http.util.*;
import org.apache.http.conn.routing.*;
import java.util.*;

public class URIUtils
{
    @Deprecated
    public static URI createURI(final String scheme, final String host, final int port, final String path, final String query, final String fragment) throws URISyntaxException {
        final StringBuilder buffer = new StringBuilder();
        if (host != null) {
            if (scheme != null) {
                buffer.append(scheme);
                buffer.append("://");
            }
            buffer.append(host);
            if (port > 0) {
                buffer.append(':');
                buffer.append(port);
            }
        }
        if (path == null || !path.startsWith("/")) {
            buffer.append('/');
        }
        if (path != null) {
            buffer.append(path);
        }
        if (query != null) {
            buffer.append('?');
            buffer.append(query);
        }
        if (fragment != null) {
            buffer.append('#');
            buffer.append(fragment);
        }
        return new URI(buffer.toString());
    }
    
    public static URI rewriteURI(final URI uri, final HttpHost target, final boolean dropFragment) throws URISyntaxException {
        Args.notNull(uri, "URI");
        if (uri.isOpaque()) {
            return uri;
        }
        final URIBuilder uribuilder = new URIBuilder(uri);
        if (target != null) {
            uribuilder.setScheme(target.getSchemeName());
            uribuilder.setHost(target.getHostName());
            uribuilder.setPort(target.getPort());
        }
        else {
            uribuilder.setScheme(null);
            uribuilder.setHost(null);
            uribuilder.setPort(-1);
        }
        if (dropFragment) {
            uribuilder.setFragment(null);
        }
        if (TextUtils.isEmpty(uribuilder.getPath())) {
            uribuilder.setPath("/");
        }
        return uribuilder.build();
    }
    
    public static URI rewriteURI(final URI uri, final HttpHost target) throws URISyntaxException {
        return rewriteURI(uri, target, false);
    }
    
    public static URI rewriteURI(final URI uri) throws URISyntaxException {
        Args.notNull(uri, "URI");
        if (uri.isOpaque()) {
            return uri;
        }
        final URIBuilder uribuilder = new URIBuilder(uri);
        if (uribuilder.getUserInfo() != null) {
            uribuilder.setUserInfo(null);
        }
        if (TextUtils.isEmpty(uribuilder.getPath())) {
            uribuilder.setPath("/");
        }
        if (uribuilder.getHost() != null) {
            uribuilder.setHost(uribuilder.getHost().toLowerCase(Locale.ROOT));
        }
        uribuilder.setFragment(null);
        return uribuilder.build();
    }
    
    public static URI rewriteURIForRoute(final URI uri, final RouteInfo route) throws URISyntaxException {
        if (uri == null) {
            return null;
        }
        if (route.getProxyHost() != null && !route.isTunnelled()) {
            if (!uri.isAbsolute()) {
                final HttpHost target = route.getTargetHost();
                return rewriteURI(uri, target, true);
            }
            return rewriteURI(uri);
        }
        else {
            if (uri.isAbsolute()) {
                return rewriteURI(uri, null, true);
            }
            return rewriteURI(uri);
        }
    }
    
    public static URI resolve(final URI baseURI, final String reference) {
        return resolve(baseURI, URI.create(reference));
    }
    
    public static URI resolve(final URI baseURI, final URI reference) {
        Args.notNull(baseURI, "Base URI");
        Args.notNull(reference, "Reference URI");
        final String s = reference.toASCIIString();
        if (s.startsWith("?")) {
            String baseUri = baseURI.toASCIIString();
            final int i = baseUri.indexOf(63);
            baseUri = ((i > -1) ? baseUri.substring(0, i) : baseUri);
            return URI.create(baseUri + s);
        }
        final boolean emptyReference = s.isEmpty();
        URI resolved;
        if (emptyReference) {
            resolved = baseURI.resolve(URI.create("#"));
            final String resolvedString = resolved.toASCIIString();
            resolved = URI.create(resolvedString.substring(0, resolvedString.indexOf(35)));
        }
        else {
            resolved = baseURI.resolve(reference);
        }
        try {
            return normalizeSyntax(resolved);
        }
        catch (URISyntaxException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    static URI normalizeSyntax(final URI uri) throws URISyntaxException {
        if (uri.isOpaque() || uri.getAuthority() == null) {
            return uri;
        }
        Args.check(uri.isAbsolute(), "Base URI must be absolute");
        final URIBuilder builder = new URIBuilder(uri);
        final String path = builder.getPath();
        if (path != null && !path.equals("/")) {
            final String[] inputSegments = path.split("/");
            final Stack<String> outputSegments = new Stack<String>();
            for (final String inputSegment : inputSegments) {
                if (!inputSegment.isEmpty()) {
                    if (!".".equals(inputSegment)) {
                        if ("..".equals(inputSegment)) {
                            if (!outputSegments.isEmpty()) {
                                outputSegments.pop();
                            }
                        }
                        else {
                            outputSegments.push(inputSegment);
                        }
                    }
                }
            }
            final StringBuilder outputBuffer = new StringBuilder();
            for (final String outputSegment : outputSegments) {
                outputBuffer.append('/').append(outputSegment);
            }
            if (path.lastIndexOf(47) == path.length() - 1) {
                outputBuffer.append('/');
            }
            builder.setPath(outputBuffer.toString());
        }
        if (builder.getScheme() != null) {
            builder.setScheme(builder.getScheme().toLowerCase(Locale.ROOT));
        }
        if (builder.getHost() != null) {
            builder.setHost(builder.getHost().toLowerCase(Locale.ROOT));
        }
        return builder.build();
    }
    
    public static HttpHost extractHost(final URI uri) {
        if (uri == null) {
            return null;
        }
        HttpHost target = null;
        if (uri.isAbsolute()) {
            int port = uri.getPort();
            String host = uri.getHost();
            if (host == null) {
                host = uri.getAuthority();
                if (host != null) {
                    final int at = host.indexOf(64);
                    if (at >= 0) {
                        if (host.length() > at + 1) {
                            host = host.substring(at + 1);
                        }
                        else {
                            host = null;
                        }
                    }
                    if (host != null) {
                        final int colon = host.indexOf(58);
                        if (colon >= 0) {
                            final int pos = colon + 1;
                            int len = 0;
                            for (int i = pos; i < host.length() && Character.isDigit(host.charAt(i)); ++i) {
                                ++len;
                            }
                            if (len > 0) {
                                try {
                                    port = Integer.parseInt(host.substring(pos, pos + len));
                                }
                                catch (NumberFormatException ex) {}
                            }
                            host = host.substring(0, colon);
                        }
                    }
                }
            }
            final String scheme = uri.getScheme();
            if (!TextUtils.isBlank(host)) {
                try {
                    target = new HttpHost(host, port, scheme);
                }
                catch (IllegalArgumentException ex2) {}
            }
        }
        return target;
    }
    
    public static URI resolve(final URI originalURI, final HttpHost target, final List<URI> redirects) throws URISyntaxException {
        Args.notNull(originalURI, "Request URI");
        URIBuilder uribuilder;
        if (redirects == null || redirects.isEmpty()) {
            uribuilder = new URIBuilder(originalURI);
        }
        else {
            uribuilder = new URIBuilder(redirects.get(redirects.size() - 1));
            String frag = uribuilder.getFragment();
            for (int i = redirects.size() - 1; frag == null && i >= 0; frag = redirects.get(i).getFragment(), --i) {}
            uribuilder.setFragment(frag);
        }
        if (uribuilder.getFragment() == null) {
            uribuilder.setFragment(originalURI.getFragment());
        }
        if (target != null && !uribuilder.isAbsolute()) {
            uribuilder.setScheme(target.getSchemeName());
            uribuilder.setHost(target.getHostName());
            uribuilder.setPort(target.getPort());
        }
        return uribuilder.build();
    }
    
    private URIUtils() {
    }
}
