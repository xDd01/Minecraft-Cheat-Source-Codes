/*
 * Decompiled with CFR 0.152.
 */
package io.socket.engineio.client;

import io.socket.emitter.Emitter;
import io.socket.engineio.client.EngineIOException;
import io.socket.engineio.client.Socket;
import io.socket.engineio.parser.Packet;
import io.socket.engineio.parser.Parser;
import io.socket.thread.EventThread;
import java.util.List;
import java.util.Map;
import okhttp3.Call;
import okhttp3.WebSocket;

public abstract class Transport
extends Emitter {
    public static final String EVENT_OPEN = "open";
    public static final String EVENT_CLOSE = "close";
    public static final String EVENT_PACKET = "packet";
    public static final String EVENT_DRAIN = "drain";
    public static final String EVENT_ERROR = "error";
    public static final String EVENT_REQUEST_HEADERS = "requestHeaders";
    public static final String EVENT_RESPONSE_HEADERS = "responseHeaders";
    public boolean writable;
    public String name;
    public Map<String, String> query;
    protected boolean secure;
    protected boolean timestampRequests;
    protected int port;
    protected String path;
    protected String hostname;
    protected String timestampParam;
    protected Socket socket;
    protected ReadyState readyState;
    protected WebSocket.Factory webSocketFactory;
    protected Call.Factory callFactory;
    protected Map<String, List<String>> extraHeaders;

    public Transport(Options opts) {
        this.path = opts.path;
        this.hostname = opts.hostname;
        this.port = opts.port;
        this.secure = opts.secure;
        this.query = opts.query;
        this.timestampParam = opts.timestampParam;
        this.timestampRequests = opts.timestampRequests;
        this.socket = opts.socket;
        this.webSocketFactory = opts.webSocketFactory;
        this.callFactory = opts.callFactory;
        this.extraHeaders = opts.extraHeaders;
    }

    protected Transport onError(String msg, Exception desc) {
        EngineIOException err = new EngineIOException(msg, desc);
        this.emit(EVENT_ERROR, err);
        return this;
    }

    public Transport open() {
        EventThread.exec(new Runnable(){

            @Override
            public void run() {
                if (Transport.this.readyState == ReadyState.CLOSED || Transport.this.readyState == null) {
                    Transport.this.readyState = ReadyState.OPENING;
                    Transport.this.doOpen();
                }
            }
        });
        return this;
    }

    public Transport close() {
        EventThread.exec(new Runnable(){

            @Override
            public void run() {
                if (Transport.this.readyState == ReadyState.OPENING || Transport.this.readyState == ReadyState.OPEN) {
                    Transport.this.doClose();
                    Transport.this.onClose();
                }
            }
        });
        return this;
    }

    public void send(final Packet[] packets) {
        EventThread.exec(new Runnable(){

            @Override
            public void run() {
                if (Transport.this.readyState != ReadyState.OPEN) {
                    throw new RuntimeException("Transport not open");
                }
                Transport.this.write(packets);
            }
        });
    }

    protected void onOpen() {
        this.readyState = ReadyState.OPEN;
        this.writable = true;
        this.emit(EVENT_OPEN, new Object[0]);
    }

    protected void onData(String data) {
        this.onPacket(Parser.decodePacket(data));
    }

    protected void onData(byte[] data) {
        this.onPacket(Parser.decodePacket(data));
    }

    protected void onPacket(Packet packet) {
        this.emit(EVENT_PACKET, packet);
    }

    protected void onClose() {
        this.readyState = ReadyState.CLOSED;
        this.emit(EVENT_CLOSE, new Object[0]);
    }

    protected abstract void write(Packet[] var1);

    protected abstract void doOpen();

    protected abstract void doClose();

    public static class Options {
        public String hostname;
        public String path;
        public String timestampParam;
        public boolean secure;
        public boolean timestampRequests;
        public int port = -1;
        public int policyPort = -1;
        public Map<String, String> query;
        protected Socket socket;
        public WebSocket.Factory webSocketFactory;
        public Call.Factory callFactory;
        public Map<String, List<String>> extraHeaders;
    }

    protected static enum ReadyState {
        OPENING,
        OPEN,
        CLOSED,
        PAUSED;


        public String toString() {
            return super.toString().toLowerCase();
        }
    }
}

