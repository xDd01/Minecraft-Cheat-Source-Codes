/*
 * Decompiled with CFR 0.152.
 */
package io.socket.engineio.client.transports;

import io.socket.engineio.client.Transport;
import io.socket.engineio.client.transports.PollingXHR;
import io.socket.engineio.parser.Packet;
import io.socket.engineio.parser.Parser;
import io.socket.parseqs.ParseQS;
import io.socket.thread.EventThread;
import io.socket.yeast.Yeast;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocket
extends Transport {
    public static final String NAME = "websocket";
    private static final Logger logger = Logger.getLogger(PollingXHR.class.getName());
    private okhttp3.WebSocket ws;

    public WebSocket(Transport.Options opts) {
        super(opts);
        this.name = NAME;
    }

    @Override
    protected void doOpen() {
        TreeMap headers = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        if (this.extraHeaders != null) {
            headers.putAll(this.extraHeaders);
        }
        this.emit("requestHeaders", headers);
        final WebSocket self = this;
        WebSocket.Factory factory = this.webSocketFactory != null ? this.webSocketFactory : new OkHttpClient();
        Request.Builder builder = new Request.Builder().url(this.uri());
        for (Map.Entry entry : headers.entrySet()) {
            for (String v2 : (List)entry.getValue()) {
                builder.addHeader((String)entry.getKey(), v2);
            }
        }
        Request request = builder.build();
        this.ws = factory.newWebSocket(request, new WebSocketListener(){

            @Override
            public void onOpen(okhttp3.WebSocket webSocket, Response response) {
                final Map<String, List<String>> headers = response.headers().toMultimap();
                EventThread.exec(new Runnable(){

                    @Override
                    public void run() {
                        self.emit("responseHeaders", headers);
                        self.onOpen();
                    }
                });
            }

            @Override
            public void onMessage(okhttp3.WebSocket webSocket, final String text) {
                if (text == null) {
                    return;
                }
                EventThread.exec(new Runnable(){

                    @Override
                    public void run() {
                        self.onData(text);
                    }
                });
            }

            @Override
            public void onMessage(okhttp3.WebSocket webSocket, final ByteString bytes) {
                if (bytes == null) {
                    return;
                }
                EventThread.exec(new Runnable(){

                    @Override
                    public void run() {
                        self.onData(bytes.toByteArray());
                    }
                });
            }

            @Override
            public void onClosed(okhttp3.WebSocket webSocket, int code, String reason) {
                EventThread.exec(new Runnable(){

                    @Override
                    public void run() {
                        self.onClose();
                    }
                });
            }

            @Override
            public void onFailure(okhttp3.WebSocket webSocket, final Throwable t2, Response response) {
                if (!(t2 instanceof Exception)) {
                    return;
                }
                EventThread.exec(new Runnable(){

                    @Override
                    public void run() {
                        self.onError("websocket error", (Exception)t2);
                    }
                });
            }
        });
    }

    @Override
    protected void write(Packet[] packets) {
        final WebSocket self = this;
        this.writable = false;
        final Runnable done = new Runnable(){

            @Override
            public void run() {
                EventThread.nextTick(new Runnable(){

                    @Override
                    public void run() {
                        self.writable = true;
                        self.emit("drain", new Object[0]);
                    }
                });
            }
        };
        final int[] total = new int[]{packets.length};
        for (Packet packet : packets) {
            if (this.readyState != Transport.ReadyState.OPENING && this.readyState != Transport.ReadyState.OPEN) break;
            Parser.encodePacket(packet, new Parser.EncodeCallback(){

                public void call(Object packet) {
                    try {
                        if (packet instanceof String) {
                            self.ws.send((String)packet);
                        } else if (packet instanceof byte[]) {
                            self.ws.send(ByteString.of((byte[])packet));
                        }
                    }
                    catch (IllegalStateException e2) {
                        logger.fine("websocket closed before we could write");
                    }
                    total[0] = total[0] - 1;
                    if (0 == total[0]) {
                        done.run();
                    }
                }
            });
        }
    }

    @Override
    protected void doClose() {
        if (this.ws != null) {
            this.ws.close(1000, "");
            this.ws = null;
        }
    }

    protected String uri() {
        String derivedQuery;
        HashMap<String, String> query = this.query;
        if (query == null) {
            query = new HashMap<String, String>();
        }
        String schema = this.secure ? "wss" : "ws";
        String port = "";
        if (this.port > 0 && ("wss".equals(schema) && this.port != 443 || "ws".equals(schema) && this.port != 80)) {
            port = ":" + this.port;
        }
        if (this.timestampRequests) {
            query.put(this.timestampParam, Yeast.yeast());
        }
        if ((derivedQuery = ParseQS.encode(query)).length() > 0) {
            derivedQuery = "?" + derivedQuery;
        }
        boolean ipv6 = this.hostname.contains(":");
        return schema + "://" + (ipv6 ? "[" + this.hostname + "]" : this.hostname) + port + this.path + derivedQuery;
    }
}

