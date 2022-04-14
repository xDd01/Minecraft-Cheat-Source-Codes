/*
 * Decompiled with CFR 0.152.
 */
package io.socket.engineio.client.transports;

import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;
import io.socket.engineio.client.transports.Polling;
import io.socket.thread.EventThread;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PollingXHR
extends Polling {
    private static final Logger logger = Logger.getLogger(PollingXHR.class.getName());
    private static boolean LOGGABLE_FINE = logger.isLoggable(Level.FINE);

    public PollingXHR(Transport.Options opts) {
        super(opts);
    }

    protected Request request() {
        return this.request(null);
    }

    protected Request request(Request.Options opts) {
        if (opts == null) {
            opts = new Request.Options();
        }
        opts.uri = this.uri();
        opts.callFactory = this.callFactory;
        opts.extraHeaders = this.extraHeaders;
        Request req = new Request(opts);
        final PollingXHR self = this;
        req.on("requestHeaders", new Emitter.Listener(){

            @Override
            public void call(Object ... args) {
                self.emit("requestHeaders", args[0]);
            }
        }).on("responseHeaders", new Emitter.Listener(){

            @Override
            public void call(final Object ... args) {
                EventThread.exec(new Runnable(){

                    @Override
                    public void run() {
                        self.emit("responseHeaders", args[0]);
                    }
                });
            }
        });
        return req;
    }

    @Override
    protected void doWrite(String data, final Runnable fn2) {
        Request.Options opts = new Request.Options();
        opts.method = "POST";
        opts.data = data;
        opts.extraHeaders = this.extraHeaders;
        Request req = this.request(opts);
        final PollingXHR self = this;
        req.on("success", new Emitter.Listener(){

            @Override
            public void call(Object ... args) {
                EventThread.exec(new Runnable(){

                    @Override
                    public void run() {
                        fn2.run();
                    }
                });
            }
        });
        req.on("error", new Emitter.Listener(){

            @Override
            public void call(final Object ... args) {
                EventThread.exec(new Runnable(){

                    @Override
                    public void run() {
                        Exception err = args.length > 0 && args[0] instanceof Exception ? (Exception)args[0] : null;
                        self.onError("xhr post error", err);
                    }
                });
            }
        });
        req.create();
    }

    @Override
    protected void doPoll() {
        logger.fine("xhr poll");
        Request req = this.request();
        final PollingXHR self = this;
        req.on("data", new Emitter.Listener(){

            @Override
            public void call(final Object ... args) {
                EventThread.exec(new Runnable(){

                    @Override
                    public void run() {
                        Object arg2 = args.length > 0 ? args[0] : null;
                        self.onData((String)arg2);
                    }
                });
            }
        });
        req.on("error", new Emitter.Listener(){

            @Override
            public void call(final Object ... args) {
                EventThread.exec(new Runnable(){

                    @Override
                    public void run() {
                        Exception err = args.length > 0 && args[0] instanceof Exception ? (Exception)args[0] : null;
                        self.onError("xhr poll error", err);
                    }
                });
            }
        });
        req.create();
    }

    public static class Request
    extends Emitter {
        public static final String EVENT_SUCCESS = "success";
        public static final String EVENT_DATA = "data";
        public static final String EVENT_ERROR = "error";
        public static final String EVENT_REQUEST_HEADERS = "requestHeaders";
        public static final String EVENT_RESPONSE_HEADERS = "responseHeaders";
        private static final String TEXT_CONTENT_TYPE = "text/plain;charset=UTF-8";
        private static final MediaType TEXT_MEDIA_TYPE = MediaType.parse("text/plain;charset=UTF-8");
        private String method;
        private String uri;
        private String data;
        private Call.Factory callFactory;
        private Map<String, List<String>> extraHeaders;
        private Response response;
        private Call requestCall;

        public Request(Options opts) {
            this.method = opts.method != null ? opts.method : "GET";
            this.uri = opts.uri;
            this.data = opts.data;
            this.callFactory = opts.callFactory != null ? opts.callFactory : new OkHttpClient();
            this.extraHeaders = opts.extraHeaders;
        }

        public void create() {
            final Request self = this;
            if (LOGGABLE_FINE) {
                logger.fine(String.format("xhr open %s: %s", this.method, this.uri));
            }
            TreeMap<String, List<String>> headers = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
            if (this.extraHeaders != null) {
                headers.putAll(this.extraHeaders);
            }
            if ("POST".equals(this.method)) {
                headers.put("Content-type", new LinkedList<String>(Collections.singletonList(TEXT_CONTENT_TYPE)));
            }
            headers.put("Accept", new LinkedList<String>(Collections.singletonList("*/*")));
            this.onRequestHeaders(headers);
            if (LOGGABLE_FINE) {
                logger.fine(String.format("sending xhr with url %s | data %s", this.uri, this.data));
            }
            Request.Builder requestBuilder = new Request.Builder();
            for (Map.Entry header : headers.entrySet()) {
                for (String v2 : (List)header.getValue()) {
                    requestBuilder.addHeader((String)header.getKey(), v2);
                }
            }
            RequestBody body = null;
            if (this.data != null) {
                body = RequestBody.create(TEXT_MEDIA_TYPE, this.data);
            }
            okhttp3.Request request = requestBuilder.url(HttpUrl.parse(self.uri)).method(self.method, body).build();
            this.requestCall = this.callFactory.newCall(request);
            this.requestCall.enqueue(new Callback(){

                @Override
                public void onFailure(Call call, IOException e2) {
                    self.onError(e2);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    self.response = response;
                    self.onResponseHeaders(response.headers().toMultimap());
                    try {
                        if (response.isSuccessful()) {
                            self.onLoad();
                        } else {
                            self.onError(new IOException(Integer.toString(response.code())));
                        }
                    }
                    finally {
                        response.close();
                    }
                }
            });
        }

        private void onSuccess() {
            this.emit(EVENT_SUCCESS, new Object[0]);
        }

        private void onData(String data) {
            this.emit(EVENT_DATA, data);
            this.onSuccess();
        }

        private void onError(Exception err) {
            this.emit(EVENT_ERROR, err);
        }

        private void onRequestHeaders(Map<String, List<String>> headers) {
            this.emit(EVENT_REQUEST_HEADERS, headers);
        }

        private void onResponseHeaders(Map<String, List<String>> headers) {
            this.emit(EVENT_RESPONSE_HEADERS, headers);
        }

        private void onLoad() {
            ResponseBody body = this.response.body();
            try {
                this.onData(body.string());
            }
            catch (IOException e2) {
                this.onError(e2);
            }
        }

        public static class Options {
            public String uri;
            public String method;
            public String data;
            public Call.Factory callFactory;
            public Map<String, List<String>> extraHeaders;
        }
    }
}

