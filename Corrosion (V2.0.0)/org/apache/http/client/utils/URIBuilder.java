/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.client.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.message.BasicNameValuePair;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@NotThreadSafe
public class URIBuilder {
    private String scheme;
    private String encodedSchemeSpecificPart;
    private String encodedAuthority;
    private String userInfo;
    private String encodedUserInfo;
    private String host;
    private int port;
    private String path;
    private String encodedPath;
    private String encodedQuery;
    private List<NameValuePair> queryParams;
    private String query;
    private String fragment;
    private String encodedFragment;

    public URIBuilder() {
        this.port = -1;
    }

    public URIBuilder(String string) throws URISyntaxException {
        this.digestURI(new URI(string));
    }

    public URIBuilder(URI uri) {
        this.digestURI(uri);
    }

    private List<NameValuePair> parseQuery(String query, Charset charset) {
        if (query != null && query.length() > 0) {
            return URLEncodedUtils.parse(query, charset);
        }
        return null;
    }

    public URI build() throws URISyntaxException {
        return new URI(this.buildString());
    }

    private String buildString() {
        StringBuilder sb2 = new StringBuilder();
        if (this.scheme != null) {
            sb2.append(this.scheme).append(':');
        }
        if (this.encodedSchemeSpecificPart != null) {
            sb2.append(this.encodedSchemeSpecificPart);
        } else {
            if (this.encodedAuthority != null) {
                sb2.append("//").append(this.encodedAuthority);
            } else if (this.host != null) {
                sb2.append("//");
                if (this.encodedUserInfo != null) {
                    sb2.append(this.encodedUserInfo).append("@");
                } else if (this.userInfo != null) {
                    sb2.append(this.encodeUserInfo(this.userInfo)).append("@");
                }
                if (InetAddressUtils.isIPv6Address(this.host)) {
                    sb2.append("[").append(this.host).append("]");
                } else {
                    sb2.append(this.host);
                }
                if (this.port >= 0) {
                    sb2.append(":").append(this.port);
                }
            }
            if (this.encodedPath != null) {
                sb2.append(URIBuilder.normalizePath(this.encodedPath));
            } else if (this.path != null) {
                sb2.append(this.encodePath(URIBuilder.normalizePath(this.path)));
            }
            if (this.encodedQuery != null) {
                sb2.append("?").append(this.encodedQuery);
            } else if (this.queryParams != null) {
                sb2.append("?").append(this.encodeUrlForm(this.queryParams));
            } else if (this.query != null) {
                sb2.append("?").append(this.encodeUric(this.query));
            }
        }
        if (this.encodedFragment != null) {
            sb2.append("#").append(this.encodedFragment);
        } else if (this.fragment != null) {
            sb2.append("#").append(this.encodeUric(this.fragment));
        }
        return sb2.toString();
    }

    private void digestURI(URI uri) {
        this.scheme = uri.getScheme();
        this.encodedSchemeSpecificPart = uri.getRawSchemeSpecificPart();
        this.encodedAuthority = uri.getRawAuthority();
        this.host = uri.getHost();
        this.port = uri.getPort();
        this.encodedUserInfo = uri.getRawUserInfo();
        this.userInfo = uri.getUserInfo();
        this.encodedPath = uri.getRawPath();
        this.path = uri.getPath();
        this.encodedQuery = uri.getRawQuery();
        this.queryParams = this.parseQuery(uri.getRawQuery(), Consts.UTF_8);
        this.encodedFragment = uri.getRawFragment();
        this.fragment = uri.getFragment();
    }

    private String encodeUserInfo(String userInfo) {
        return URLEncodedUtils.encUserInfo(userInfo, Consts.UTF_8);
    }

    private String encodePath(String path) {
        return URLEncodedUtils.encPath(path, Consts.UTF_8);
    }

    private String encodeUrlForm(List<NameValuePair> params) {
        return URLEncodedUtils.format(params, Consts.UTF_8);
    }

    private String encodeUric(String fragment) {
        return URLEncodedUtils.encUric(fragment, Consts.UTF_8);
    }

    public URIBuilder setScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public URIBuilder setUserInfo(String userInfo) {
        this.userInfo = userInfo;
        this.encodedSchemeSpecificPart = null;
        this.encodedAuthority = null;
        this.encodedUserInfo = null;
        return this;
    }

    public URIBuilder setUserInfo(String username, String password) {
        return this.setUserInfo(username + ':' + password);
    }

    public URIBuilder setHost(String host) {
        this.host = host;
        this.encodedSchemeSpecificPart = null;
        this.encodedAuthority = null;
        return this;
    }

    public URIBuilder setPort(int port) {
        this.port = port < 0 ? -1 : port;
        this.encodedSchemeSpecificPart = null;
        this.encodedAuthority = null;
        return this;
    }

    public URIBuilder setPath(String path) {
        this.path = path;
        this.encodedSchemeSpecificPart = null;
        this.encodedPath = null;
        return this;
    }

    public URIBuilder removeQuery() {
        this.queryParams = null;
        this.query = null;
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        return this;
    }

    @Deprecated
    public URIBuilder setQuery(String query) {
        this.queryParams = this.parseQuery(query, Consts.UTF_8);
        this.query = null;
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        return this;
    }

    public URIBuilder setParameters(List<NameValuePair> nvps) {
        if (this.queryParams == null) {
            this.queryParams = new ArrayList<NameValuePair>();
        } else {
            this.queryParams.clear();
        }
        this.queryParams.addAll(nvps);
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.query = null;
        return this;
    }

    public URIBuilder addParameters(List<NameValuePair> nvps) {
        if (this.queryParams == null) {
            this.queryParams = new ArrayList<NameValuePair>();
        }
        this.queryParams.addAll(nvps);
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.query = null;
        return this;
    }

    public URIBuilder setParameters(NameValuePair ... nvps) {
        if (this.queryParams == null) {
            this.queryParams = new ArrayList<NameValuePair>();
        } else {
            this.queryParams.clear();
        }
        for (NameValuePair nvp : nvps) {
            this.queryParams.add(nvp);
        }
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.query = null;
        return this;
    }

    public URIBuilder addParameter(String param, String value) {
        if (this.queryParams == null) {
            this.queryParams = new ArrayList<NameValuePair>();
        }
        this.queryParams.add(new BasicNameValuePair(param, value));
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.query = null;
        return this;
    }

    public URIBuilder setParameter(String param, String value) {
        if (this.queryParams == null) {
            this.queryParams = new ArrayList<NameValuePair>();
        }
        if (!this.queryParams.isEmpty()) {
            Iterator<NameValuePair> it2 = this.queryParams.iterator();
            while (it2.hasNext()) {
                NameValuePair nvp = it2.next();
                if (!nvp.getName().equals(param)) continue;
                it2.remove();
            }
        }
        this.queryParams.add(new BasicNameValuePair(param, value));
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.query = null;
        return this;
    }

    public URIBuilder clearParameters() {
        this.queryParams = null;
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        return this;
    }

    public URIBuilder setCustomQuery(String query) {
        this.query = query;
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.queryParams = null;
        return this;
    }

    public URIBuilder setFragment(String fragment) {
        this.fragment = fragment;
        this.encodedFragment = null;
        return this;
    }

    public boolean isAbsolute() {
        return this.scheme != null;
    }

    public boolean isOpaque() {
        return this.path == null;
    }

    public String getScheme() {
        return this.scheme;
    }

    public String getUserInfo() {
        return this.userInfo;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getPath() {
        return this.path;
    }

    public List<NameValuePair> getQueryParams() {
        if (this.queryParams != null) {
            return new ArrayList<NameValuePair>(this.queryParams);
        }
        return new ArrayList<NameValuePair>();
    }

    public String getFragment() {
        return this.fragment;
    }

    public String toString() {
        return this.buildString();
    }

    private static String normalizePath(String path) {
        int n2;
        String s2 = path;
        if (s2 == null) {
            return null;
        }
        for (n2 = 0; n2 < s2.length() && s2.charAt(n2) == '/'; ++n2) {
        }
        if (n2 > 1) {
            s2 = s2.substring(n2 - 1);
        }
        return s2;
    }
}

