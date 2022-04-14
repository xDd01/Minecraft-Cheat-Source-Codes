/*
 * Decompiled with CFR 0.152.
 */
package me.vaziak.cube.web;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.function.Consumer;
import me.vaziak.cube.web.WebResponse;
import me.vaziak.cube.web.post.PostData;

public class WebRequest {
    private URL url;
    private Proxy proxy;
    private HttpURLConnection httpURLConnection;
    private PostData postData;
    private String method;
    private String userAgent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0";

    public WebRequest() {
        this(null, null);
    }

    public WebRequest(URL url) {
        this(url, null);
    }

    public WebRequest(String url) {
        this.url = new URL(url);
    }

    public WebRequest(URL url, Proxy proxy) {
        this.url = url;
        this.proxy = proxy;
    }

    public WebRequest setUrl(URL url) {
        this.url = url;
        return this;
    }

    public WebRequest setUrl(String url) throws MalformedURLException {
        return this.setUrl(new URL(url));
    }

    public WebRequest setProxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    public WebRequest setMethod(String method) {
        this.method = method.toUpperCase();
        return this;
    }

    public WebRequest setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public WebRequest setPostData(PostData postData) {
        this.postData = postData;
        return this;
    }

    public void invoke(Consumer<WebResponse> webCallback) {
        Objects.requireNonNull(this.url, "A URL was not supplied to the WebRequest");
        this.httpURLConnection = this.proxy != null ? (HttpURLConnection)this.url.openConnection(this.proxy) : (HttpURLConnection)this.url.openConnection();
        this.httpURLConnection.addRequestProperty("User-Agent", this.userAgent);
        this.httpURLConnection.setDoOutput(true);
        this.httpURLConnection.setDoInput(true);
        if (Objects.isNull(this.method) || this.method.isEmpty()) {
            this.method = Objects.isNull(this.postData) ? "GET" : "POST";
        }
        this.httpURLConnection.setRequestMethod(this.method);
        if (Objects.nonNull(this.postData)) {
            String encodedData = Objects.nonNull(this.postData.getEncoding()) ? URLEncoder.encode(this.postData.getData(), this.postData.getEncoding()) : this.postData.getData();
            this.httpURLConnection.setRequestProperty("Content-Type", this.postData.getType());
            this.httpURLConnection.setRequestProperty("Content-Length", String.valueOf(encodedData.length()));
            DataOutputStream output = new DataOutputStream(this.httpURLConnection.getOutputStream());
            output.writeBytes(encodedData);
            output.close();
        }
        webCallback.accept(new WebResponse(this));
    }

    public URL getUrl() {
        return this.url;
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    public HttpURLConnection getHttpURLConnection() {
        return this.httpURLConnection;
    }

    public PostData getPostData() {
        return this.postData;
    }

    public String getMethod() {
        return this.method;
    }

    public String getUserAgent() {
        return this.userAgent;
    }
}

