/*
 * Decompiled with CFR 0.152.
 */
package me.vaziak.cube.web.post;

public class PostData {
    private String data;
    private String type;
    private String encoding;

    public PostData(String data, String type) {
        this(data, type, null);
    }

    public PostData(String data, String type, String encoding) {
        this.data = data;
        this.type = type;
        this.encoding = encoding;
    }

    public PostData setData(String data) {
        this.data = data;
        return this;
    }

    public PostData setType(String type) {
        this.type = type;
        return this;
    }

    public PostData setEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public String getData() {
        return this.data;
    }

    public String getType() {
        return this.type;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public PostData() {
    }
}

