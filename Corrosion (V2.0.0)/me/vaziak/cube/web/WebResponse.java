/*
 * Decompiled with CFR 0.152.
 */
package me.vaziak.cube.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import me.vaziak.cube.web.WebRequest;

public class WebResponse {
    private final WebRequest request;
    private final byte[] responseData;
    private final int responseCode;

    public WebResponse(WebRequest request) throws IOException {
        int length;
        this.request = request;
        InputStream inputStream = request.getHttpURLConnection().getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        this.responseData = byteArrayOutputStream.toByteArray();
        this.responseCode = request.getHttpURLConnection().getResponseCode();
    }

    public WebRequest getRequest() {
        return this.request;
    }

    public byte[] getResponseData() {
        return this.responseData;
    }

    public int getResponseCode() {
        return this.responseCode;
    }
}

