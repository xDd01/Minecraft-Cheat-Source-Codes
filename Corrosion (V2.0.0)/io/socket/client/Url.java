/*
 * Decompiled with CFR 0.152.
 */
package io.socket.client;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Url {
    private static Pattern PATTERN_AUTHORITY = Pattern.compile("^(.*@)?([^:]+)(:\\d+)?$");

    private Url() {
    }

    public static ParsedURI parse(URI uri) {
        String path;
        int port;
        String protocol = uri.getScheme();
        if (protocol == null || !protocol.matches("^https?|wss?$")) {
            protocol = "https";
        }
        if ((port = uri.getPort()) == -1) {
            if ("http".equals(protocol) || "ws".equals(protocol)) {
                port = 80;
            } else if ("https".equals(protocol) || "wss".equals(protocol)) {
                port = 443;
            }
        }
        if ((path = uri.getRawPath()) == null || path.length() == 0) {
            path = "/";
        }
        String userInfo = uri.getRawUserInfo();
        String query = uri.getRawQuery();
        String fragment = uri.getRawFragment();
        String _host = uri.getHost();
        if (_host == null) {
            _host = Url.extractHostFromAuthorityPart(uri.getRawAuthority());
        }
        URI completeUri = URI.create(protocol + "://" + (userInfo != null ? userInfo + "@" : "") + _host + (port != -1 ? ":" + port : "") + path + (query != null ? "?" + query : "") + (fragment != null ? "#" + fragment : ""));
        String id2 = protocol + "://" + _host + ":" + port;
        return new ParsedURI(completeUri, id2);
    }

    private static String extractHostFromAuthorityPart(String authority) {
        if (authority == null) {
            throw new RuntimeException("unable to parse the host from the authority");
        }
        Matcher matcher = PATTERN_AUTHORITY.matcher(authority);
        if (!matcher.matches()) {
            throw new RuntimeException("unable to parse the host from the authority");
        }
        return matcher.group(2);
    }

    static class ParsedURI {
        public final URI uri;
        public final String id;

        public ParsedURI(URI uri, String id2) {
            this.uri = uri;
            this.id = id2;
        }
    }
}

