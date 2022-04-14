/*
 * Decompiled with CFR 0.152.
 */
package io.socket.client;

import io.socket.backo.Backoff;
import io.socket.client.On;
import io.socket.client.Socket;
import io.socket.client.SocketIOException;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Socket;
import io.socket.parser.DecodingException;
import io.socket.parser.IOParser;
import io.socket.parser.Packet;
import io.socket.parser.Parser;
import io.socket.thread.EventThread;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.Call;
import okhttp3.WebSocket;

public class Manager
extends Emitter {
    private static final Logger logger = Logger.getLogger(Manager.class.getName());
    public static final String EVENT_OPEN = "open";
    public static final String EVENT_CLOSE = "close";
    public static final String EVENT_PACKET = "packet";
    public static final String EVENT_ERROR = "error";
    public static final String EVENT_RECONNECT = "reconnect";
    public static final String EVENT_RECONNECT_ERROR = "reconnect_error";
    public static final String EVENT_RECONNECT_FAILED = "reconnect_failed";
    public static final String EVENT_RECONNECT_ATTEMPT = "reconnect_attempt";
    public static final String EVENT_TRANSPORT = "transport";
    static WebSocket.Factory defaultWebSocketFactory;
    static Call.Factory defaultCallFactory;
    ReadyState readyState;
    private boolean _reconnection;
    private boolean skipReconnect;
    private boolean reconnecting;
    private boolean encoding;
    private int _reconnectionAttempts;
    private long _reconnectionDelay;
    private long _reconnectionDelayMax;
    private double _randomizationFactor;
    private Backoff backoff;
    private long _timeout;
    private URI uri;
    private List<Packet> packetBuffer;
    private Queue<On.Handle> subs;
    private Options opts;
    io.socket.engineio.client.Socket engine;
    private Parser.Encoder encoder;
    private Parser.Decoder decoder;
    ConcurrentHashMap<String, Socket> nsps;

    public Manager() {
        this(null, null);
    }

    public Manager(URI uri) {
        this(uri, null);
    }

    public Manager(Options opts) {
        this(null, opts);
    }

    public Manager(URI uri, Options opts) {
        if (opts == null) {
            opts = new Options();
        }
        if (opts.path == null) {
            opts.path = "/socket.io";
        }
        if (opts.webSocketFactory == null) {
            opts.webSocketFactory = defaultWebSocketFactory;
        }
        if (opts.callFactory == null) {
            opts.callFactory = defaultCallFactory;
        }
        this.opts = opts;
        this.nsps = new ConcurrentHashMap();
        this.subs = new LinkedList<On.Handle>();
        this.reconnection(opts.reconnection);
        this.reconnectionAttempts(opts.reconnectionAttempts != 0 ? opts.reconnectionAttempts : Integer.MAX_VALUE);
        this.reconnectionDelay(opts.reconnectionDelay != 0L ? opts.reconnectionDelay : 1000L);
        this.reconnectionDelayMax(opts.reconnectionDelayMax != 0L ? opts.reconnectionDelayMax : 5000L);
        this.randomizationFactor(opts.randomizationFactor != 0.0 ? opts.randomizationFactor : 0.5);
        this.backoff = new Backoff().setMin(this.reconnectionDelay()).setMax(this.reconnectionDelayMax()).setJitter(this.randomizationFactor());
        this.timeout(opts.timeout);
        this.readyState = ReadyState.CLOSED;
        this.uri = uri;
        this.encoding = false;
        this.packetBuffer = new ArrayList<Packet>();
        this.encoder = opts.encoder != null ? opts.encoder : new IOParser.Encoder();
        this.decoder = opts.decoder != null ? opts.decoder : new IOParser.Decoder();
    }

    public boolean reconnection() {
        return this._reconnection;
    }

    public Manager reconnection(boolean v2) {
        this._reconnection = v2;
        return this;
    }

    public boolean isReconnecting() {
        return this.reconnecting;
    }

    public int reconnectionAttempts() {
        return this._reconnectionAttempts;
    }

    public Manager reconnectionAttempts(int v2) {
        this._reconnectionAttempts = v2;
        return this;
    }

    public final long reconnectionDelay() {
        return this._reconnectionDelay;
    }

    public Manager reconnectionDelay(long v2) {
        this._reconnectionDelay = v2;
        if (this.backoff != null) {
            this.backoff.setMin(v2);
        }
        return this;
    }

    public final double randomizationFactor() {
        return this._randomizationFactor;
    }

    public Manager randomizationFactor(double v2) {
        this._randomizationFactor = v2;
        if (this.backoff != null) {
            this.backoff.setJitter(v2);
        }
        return this;
    }

    public final long reconnectionDelayMax() {
        return this._reconnectionDelayMax;
    }

    public Manager reconnectionDelayMax(long v2) {
        this._reconnectionDelayMax = v2;
        if (this.backoff != null) {
            this.backoff.setMax(v2);
        }
        return this;
    }

    public long timeout() {
        return this._timeout;
    }

    public Manager timeout(long v2) {
        this._timeout = v2;
        return this;
    }

    private void maybeReconnectOnOpen() {
        if (!this.reconnecting && this._reconnection && this.backoff.getAttempts() == 0) {
            this.reconnect();
        }
    }

    public Manager open() {
        return this.open(null);
    }

    public Manager open(final OpenCallback fn2) {
        EventThread.exec(new Runnable(){

            @Override
            public void run() {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine(String.format("readyState %s", new Object[]{Manager.this.readyState}));
                }
                if (Manager.this.readyState == ReadyState.OPEN || Manager.this.readyState == ReadyState.OPENING) {
                    return;
                }
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine(String.format("opening %s", Manager.this.uri));
                }
                final io.socket.engineio.client.Socket socket = Manager.this.engine = new Engine(Manager.this.uri, (Socket.Options)Manager.this.opts);
                final Manager self = Manager.this;
                Manager.this.readyState = ReadyState.OPENING;
                Manager.this.skipReconnect = false;
                socket.on(Manager.EVENT_TRANSPORT, new Emitter.Listener(){

                    @Override
                    public void call(Object ... args) {
                        self.emit(Manager.EVENT_TRANSPORT, args);
                    }
                });
                final On.Handle openSub = On.on(socket, Manager.EVENT_OPEN, new Emitter.Listener(){

                    @Override
                    public void call(Object ... objects) {
                        self.onopen();
                        if (fn2 != null) {
                            fn2.call(null);
                        }
                    }
                });
                On.Handle errorSub = On.on(socket, Manager.EVENT_ERROR, new Emitter.Listener(){

                    @Override
                    public void call(Object ... objects) {
                        Object data = objects.length > 0 ? objects[0] : null;
                        logger.fine("connect_error");
                        self.cleanup();
                        self.readyState = ReadyState.CLOSED;
                        self.emit(Manager.EVENT_ERROR, data);
                        if (fn2 != null) {
                            SocketIOException err = new SocketIOException("Connection error", data instanceof Exception ? (Exception)data : null);
                            fn2.call(err);
                        } else {
                            self.maybeReconnectOnOpen();
                        }
                    }
                });
                final long timeout = Manager.this._timeout;
                final Runnable onTimeout = new Runnable(){

                    @Override
                    public void run() {
                        logger.fine(String.format("connect attempt timed out after %d", timeout));
                        openSub.destroy();
                        socket.close();
                        socket.emit(Manager.EVENT_ERROR, new SocketIOException("timeout"));
                    }
                };
                if (timeout == 0L) {
                    EventThread.exec(onTimeout);
                    return;
                }
                if (Manager.this._timeout > 0L) {
                    logger.fine(String.format("connection attempt will timeout after %d", timeout));
                    final Timer timer = new Timer();
                    timer.schedule(new TimerTask(){

                        @Override
                        public void run() {
                            EventThread.exec(onTimeout);
                        }
                    }, timeout);
                    Manager.this.subs.add(new On.Handle(){

                        @Override
                        public void destroy() {
                            timer.cancel();
                        }
                    });
                }
                Manager.this.subs.add(openSub);
                Manager.this.subs.add(errorSub);
                Manager.this.engine.open();
            }
        });
        return this;
    }

    private void onopen() {
        logger.fine(EVENT_OPEN);
        this.cleanup();
        this.readyState = ReadyState.OPEN;
        this.emit(EVENT_OPEN, new Object[0]);
        io.socket.engineio.client.Socket socket = this.engine;
        this.subs.add(On.on(socket, "data", new Emitter.Listener(){

            @Override
            public void call(Object ... objects) {
                Object data = objects[0];
                try {
                    if (data instanceof String) {
                        Manager.this.decoder.add((String)data);
                    } else if (data instanceof byte[]) {
                        Manager.this.decoder.add((byte[])data);
                    }
                }
                catch (DecodingException e2) {
                    logger.fine("error while decoding the packet: " + e2.getMessage());
                }
            }
        }));
        this.subs.add(On.on(socket, EVENT_ERROR, new Emitter.Listener(){

            @Override
            public void call(Object ... objects) {
                Manager.this.onerror((Exception)objects[0]);
            }
        }));
        this.subs.add(On.on(socket, EVENT_CLOSE, new Emitter.Listener(){

            @Override
            public void call(Object ... objects) {
                Manager.this.onclose((String)objects[0]);
            }
        }));
        this.decoder.onDecoded(new Parser.Decoder.Callback(){

            @Override
            public void call(Packet packet) {
                Manager.this.ondecoded(packet);
            }
        });
    }

    private void ondecoded(Packet packet) {
        this.emit(EVENT_PACKET, packet);
    }

    private void onerror(Exception err) {
        logger.log(Level.FINE, EVENT_ERROR, err);
        this.emit(EVENT_ERROR, err);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Socket socket(String nsp, Options opts) {
        ConcurrentHashMap<String, Socket> concurrentHashMap = this.nsps;
        synchronized (concurrentHashMap) {
            Socket socket = this.nsps.get(nsp);
            if (socket == null) {
                socket = new Socket(this, nsp, opts);
                this.nsps.put(nsp, socket);
            }
            return socket;
        }
    }

    public Socket socket(String nsp) {
        return this.socket(nsp, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void destroy() {
        ConcurrentHashMap<String, Socket> concurrentHashMap = this.nsps;
        synchronized (concurrentHashMap) {
            for (Socket socket : this.nsps.values()) {
                if (!socket.isActive()) continue;
                logger.fine("socket is still active, skipping close");
                return;
            }
            this.close();
        }
    }

    void packet(Packet packet) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("writing packet %s", packet));
        }
        final Manager self = this;
        if (!self.encoding) {
            self.encoding = true;
            this.encoder.encode(packet, new Parser.Encoder.Callback(){

                @Override
                public void call(Object[] encodedPackets) {
                    for (Object packet : encodedPackets) {
                        if (packet instanceof String) {
                            self.engine.write((String)packet);
                            continue;
                        }
                        if (!(packet instanceof byte[])) continue;
                        self.engine.write((byte[])packet);
                    }
                    self.encoding = false;
                    self.processPacketQueue();
                }
            });
        } else {
            self.packetBuffer.add(packet);
        }
    }

    private void processPacketQueue() {
        if (!this.packetBuffer.isEmpty() && !this.encoding) {
            Packet pack = this.packetBuffer.remove(0);
            this.packet(pack);
        }
    }

    private void cleanup() {
        On.Handle sub;
        logger.fine("cleanup");
        while ((sub = this.subs.poll()) != null) {
            sub.destroy();
        }
        this.decoder.onDecoded(null);
        this.packetBuffer.clear();
        this.encoding = false;
        this.decoder.destroy();
    }

    void close() {
        logger.fine("disconnect");
        this.skipReconnect = true;
        this.reconnecting = false;
        if (this.readyState != ReadyState.OPEN) {
            this.cleanup();
        }
        this.backoff.reset();
        this.readyState = ReadyState.CLOSED;
        if (this.engine != null) {
            this.engine.close();
        }
    }

    private void onclose(String reason) {
        logger.fine("onclose");
        this.cleanup();
        this.backoff.reset();
        this.readyState = ReadyState.CLOSED;
        this.emit(EVENT_CLOSE, reason);
        if (this._reconnection && !this.skipReconnect) {
            this.reconnect();
        }
    }

    private void reconnect() {
        if (this.reconnecting || this.skipReconnect) {
            return;
        }
        final Manager self = this;
        if (this.backoff.getAttempts() >= this._reconnectionAttempts) {
            logger.fine("reconnect failed");
            this.backoff.reset();
            this.emit(EVENT_RECONNECT_FAILED, new Object[0]);
            this.reconnecting = false;
        } else {
            long delay = this.backoff.duration();
            logger.fine(String.format("will wait %dms before reconnect attempt", delay));
            this.reconnecting = true;
            final Timer timer = new Timer();
            timer.schedule(new TimerTask(){

                @Override
                public void run() {
                    EventThread.exec(new Runnable(){

                        @Override
                        public void run() {
                            if (self.skipReconnect) {
                                return;
                            }
                            logger.fine("attempting reconnect");
                            int attempts = self.backoff.getAttempts();
                            self.emit(Manager.EVENT_RECONNECT_ATTEMPT, attempts);
                            if (self.skipReconnect) {
                                return;
                            }
                            self.open(new OpenCallback(){

                                @Override
                                public void call(Exception err) {
                                    if (err != null) {
                                        logger.fine("reconnect attempt error");
                                        self.reconnecting = false;
                                        self.reconnect();
                                        self.emit(Manager.EVENT_RECONNECT_ERROR, err);
                                    } else {
                                        logger.fine("reconnect success");
                                        self.onreconnect();
                                    }
                                }
                            });
                        }
                    });
                }
            }, delay);
            this.subs.add(new On.Handle(){

                @Override
                public void destroy() {
                    timer.cancel();
                }
            });
        }
    }

    private void onreconnect() {
        int attempts = this.backoff.getAttempts();
        this.reconnecting = false;
        this.backoff.reset();
        this.emit(EVENT_RECONNECT, attempts);
    }

    public static class Options
    extends Socket.Options {
        public boolean reconnection = true;
        public int reconnectionAttempts;
        public long reconnectionDelay;
        public long reconnectionDelayMax;
        public double randomizationFactor;
        public Parser.Encoder encoder;
        public Parser.Decoder decoder;
        public Map<String, String> auth;
        public long timeout = 20000L;
    }

    private static class Engine
    extends io.socket.engineio.client.Socket {
        Engine(URI uri, Socket.Options opts) {
            super(uri, opts);
        }
    }

    public static interface OpenCallback {
        public void call(Exception var1);
    }

    static enum ReadyState {
        CLOSED,
        OPENING,
        OPEN;

    }
}

