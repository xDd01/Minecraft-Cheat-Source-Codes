/*
 * Decompiled with CFR 0.152.
 */
package io.socket.engineio.client;

import io.socket.emitter.Emitter;
import io.socket.engineio.client.EngineIOException;
import io.socket.engineio.client.HandshakeData;
import io.socket.engineio.client.Transport;
import io.socket.engineio.client.transports.Polling;
import io.socket.engineio.client.transports.PollingXHR;
import io.socket.engineio.client.transports.WebSocket;
import io.socket.engineio.parser.Packet;
import io.socket.parseqs.ParseQS;
import io.socket.thread.EventThread;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import org.json.JSONException;

public class Socket
extends Emitter {
    private static final Logger logger = Logger.getLogger(Socket.class.getName());
    private static final String PROBE_ERROR = "probe error";
    public static final String EVENT_OPEN = "open";
    public static final String EVENT_CLOSE = "close";
    public static final String EVENT_MESSAGE = "message";
    public static final String EVENT_ERROR = "error";
    public static final String EVENT_UPGRADE_ERROR = "upgradeError";
    public static final String EVENT_FLUSH = "flush";
    public static final String EVENT_DRAIN = "drain";
    public static final String EVENT_HANDSHAKE = "handshake";
    public static final String EVENT_UPGRADING = "upgrading";
    public static final String EVENT_UPGRADE = "upgrade";
    public static final String EVENT_PACKET = "packet";
    public static final String EVENT_PACKET_CREATE = "packetCreate";
    public static final String EVENT_HEARTBEAT = "heartbeat";
    public static final String EVENT_DATA = "data";
    public static final String EVENT_PING = "ping";
    public static final String EVENT_PONG = "pong";
    public static final String EVENT_TRANSPORT = "transport";
    public static final int PROTOCOL = 4;
    private static boolean priorWebsocketSuccess = false;
    private static WebSocket.Factory defaultWebSocketFactory;
    private static Call.Factory defaultCallFactory;
    private static OkHttpClient defaultOkHttpClient;
    private boolean secure;
    private boolean upgrade;
    private boolean timestampRequests;
    private boolean upgrading;
    private boolean rememberUpgrade;
    int port;
    private int policyPort;
    private int prevBufferLen;
    private long pingInterval;
    private long pingTimeout;
    private String id;
    String hostname;
    private String path;
    private String timestampParam;
    private List<String> transports;
    private Map<String, Transport.Options> transportOptions;
    private List<String> upgrades;
    private Map<String, String> query;
    LinkedList<Packet> writeBuffer = new LinkedList();
    Transport transport;
    private Future pingTimeoutTimer;
    private WebSocket.Factory webSocketFactory;
    private Call.Factory callFactory;
    private final Map<String, List<String>> extraHeaders;
    private ReadyState readyState;
    private ScheduledExecutorService heartbeatScheduler;
    private final Emitter.Listener onHeartbeatAsListener = new Emitter.Listener(){

        @Override
        public void call(Object ... args) {
            Socket.this.onHeartbeat();
        }
    };

    public Socket() {
        this(new Options());
    }

    public Socket(String uri) throws URISyntaxException {
        this(uri, null);
    }

    public Socket(URI uri) {
        this(uri, null);
    }

    public Socket(String uri, Options opts) throws URISyntaxException {
        this(uri == null ? null : new URI(uri), opts);
    }

    public Socket(URI uri, Options opts) {
        this(uri == null ? opts : Options.fromURI(uri, opts));
    }

    public Socket(Options opts) {
        String[] stringArray;
        if (opts.host != null) {
            boolean ipv6;
            String hostname = opts.host;
            boolean bl2 = ipv6 = hostname.split(":").length > 2;
            if (ipv6) {
                int end;
                int start = hostname.indexOf(91);
                if (start != -1) {
                    hostname = hostname.substring(start + 1);
                }
                if ((end = hostname.lastIndexOf(93)) != -1) {
                    hostname = hostname.substring(0, end);
                }
            }
            opts.hostname = hostname;
        }
        this.secure = opts.secure;
        if (opts.port == -1) {
            opts.port = this.secure ? 443 : 80;
        }
        this.hostname = opts.hostname != null ? opts.hostname : "localhost";
        this.port = opts.port;
        this.query = opts.query != null ? ParseQS.decode(opts.query) : new HashMap();
        this.upgrade = opts.upgrade;
        this.path = (opts.path != null ? opts.path : "/engine.io").replaceAll("/$", "") + "/";
        this.timestampParam = opts.timestampParam != null ? opts.timestampParam : "t";
        this.timestampRequests = opts.timestampRequests;
        if (opts.transports != null) {
            stringArray = opts.transports;
        } else {
            String[] stringArray2 = new String[2];
            stringArray2[0] = "polling";
            stringArray = stringArray2;
            stringArray2[1] = "websocket";
        }
        this.transports = new ArrayList<String>(Arrays.asList(stringArray));
        this.transportOptions = opts.transportOptions != null ? opts.transportOptions : new HashMap<String, Transport.Options>();
        this.policyPort = opts.policyPort != 0 ? opts.policyPort : 843;
        this.rememberUpgrade = opts.rememberUpgrade;
        this.callFactory = opts.callFactory != null ? opts.callFactory : defaultCallFactory;
        WebSocket.Factory factory = this.webSocketFactory = opts.webSocketFactory != null ? opts.webSocketFactory : defaultWebSocketFactory;
        if (this.callFactory == null) {
            if (defaultOkHttpClient == null) {
                defaultOkHttpClient = new OkHttpClient();
            }
            this.callFactory = defaultOkHttpClient;
        }
        if (this.webSocketFactory == null) {
            if (defaultOkHttpClient == null) {
                defaultOkHttpClient = new OkHttpClient();
            }
            this.webSocketFactory = defaultOkHttpClient;
        }
        this.extraHeaders = opts.extraHeaders;
    }

    public static void setDefaultOkHttpWebSocketFactory(WebSocket.Factory factory) {
        defaultWebSocketFactory = factory;
    }

    public static void setDefaultOkHttpCallFactory(Call.Factory factory) {
        defaultCallFactory = factory;
    }

    public Socket open() {
        EventThread.exec(new Runnable(){

            @Override
            public void run() {
                String transportName;
                if (Socket.this.rememberUpgrade && priorWebsocketSuccess && Socket.this.transports.contains("websocket")) {
                    transportName = "websocket";
                } else {
                    if (0 == Socket.this.transports.size()) {
                        final Socket self = Socket.this;
                        EventThread.nextTick(new Runnable(){

                            @Override
                            public void run() {
                                self.emit(Socket.EVENT_ERROR, new EngineIOException("No transports available"));
                            }
                        });
                        return;
                    }
                    transportName = (String)Socket.this.transports.get(0);
                }
                Socket.this.readyState = ReadyState.OPENING;
                Transport transport = Socket.this.createTransport(transportName);
                Socket.this.setTransport(transport);
                transport.open();
            }
        });
        return this;
    }

    private Transport createTransport(String name) {
        Transport transport;
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("creating transport '%s'", name));
        }
        HashMap<String, String> query = new HashMap<String, String>(this.query);
        query.put("EIO", String.valueOf(4));
        query.put(EVENT_TRANSPORT, name);
        if (this.id != null) {
            query.put("sid", this.id);
        }
        Transport.Options options = this.transportOptions.get(name);
        Transport.Options opts = new Transport.Options();
        opts.query = query;
        opts.socket = this;
        opts.hostname = options != null ? options.hostname : this.hostname;
        opts.port = options != null ? options.port : this.port;
        opts.secure = options != null ? options.secure : this.secure;
        opts.path = options != null ? options.path : this.path;
        opts.timestampRequests = options != null ? options.timestampRequests : this.timestampRequests;
        opts.timestampParam = options != null ? options.timestampParam : this.timestampParam;
        opts.policyPort = options != null ? options.policyPort : this.policyPort;
        opts.callFactory = options != null ? options.callFactory : this.callFactory;
        opts.webSocketFactory = options != null ? options.webSocketFactory : this.webSocketFactory;
        opts.extraHeaders = this.extraHeaders;
        if ("websocket".equals(name)) {
            transport = new WebSocket(opts);
        } else if ("polling".equals(name)) {
            transport = new PollingXHR(opts);
        } else {
            throw new RuntimeException();
        }
        this.emit(EVENT_TRANSPORT, transport);
        return transport;
    }

    private void setTransport(Transport transport) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("setting transport %s", transport.name));
        }
        final Socket self = this;
        if (this.transport != null) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(String.format("clearing existing transport %s", this.transport.name));
            }
            this.transport.off();
        }
        this.transport = transport;
        transport.on(EVENT_DRAIN, new Emitter.Listener(){

            @Override
            public void call(Object ... args) {
                self.onDrain();
            }
        }).on(EVENT_PACKET, new Emitter.Listener(){

            @Override
            public void call(Object ... args) {
                self.onPacket(args.length > 0 ? (Packet)args[0] : null);
            }
        }).on(EVENT_ERROR, new Emitter.Listener(){

            @Override
            public void call(Object ... args) {
                self.onError(args.length > 0 ? (Exception)args[0] : null);
            }
        }).on(EVENT_CLOSE, new Emitter.Listener(){

            @Override
            public void call(Object ... args) {
                self.onClose("transport close");
            }
        });
    }

    private void probe(final String name) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("probing transport '%s'", name));
        }
        final Transport[] transport = new Transport[]{this.createTransport(name)};
        final boolean[] failed = new boolean[]{false};
        final Socket self = this;
        priorWebsocketSuccess = false;
        final Runnable[] cleanup = new Runnable[1];
        final Emitter.Listener onTransportOpen = new Emitter.Listener(){

            @Override
            public void call(Object ... args) {
                if (failed[0]) {
                    return;
                }
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine(String.format("probe transport '%s' opened", name));
                }
                Packet<String> packet = new Packet<String>(Socket.EVENT_PING, "probe");
                transport[0].send(new Packet[]{packet});
                transport[0].once(Socket.EVENT_PACKET, new Emitter.Listener(){

                    @Override
                    public void call(Object ... args) {
                        if (failed[0]) {
                            return;
                        }
                        Packet msg = (Packet)args[0];
                        if (Socket.EVENT_PONG.equals(msg.type) && "probe".equals(msg.data)) {
                            if (logger.isLoggable(Level.FINE)) {
                                logger.fine(String.format("probe transport '%s' pong", name));
                            }
                            self.upgrading = true;
                            self.emit(Socket.EVENT_UPGRADING, transport[0]);
                            if (null == transport[0]) {
                                return;
                            }
                            priorWebsocketSuccess = "websocket".equals(transport[0].name);
                            if (logger.isLoggable(Level.FINE)) {
                                logger.fine(String.format("pausing current transport '%s'", self.transport.name));
                            }
                            ((Polling)self.transport).pause(new Runnable(){

                                @Override
                                public void run() {
                                    if (failed[0]) {
                                        return;
                                    }
                                    if (ReadyState.CLOSED == self.readyState) {
                                        return;
                                    }
                                    logger.fine("changing transport and sending upgrade packet");
                                    cleanup[0].run();
                                    self.setTransport(transport[0]);
                                    Packet packet = new Packet(Socket.EVENT_UPGRADE);
                                    transport[0].send(new Packet[]{packet});
                                    self.emit(Socket.EVENT_UPGRADE, transport[0]);
                                    transport[0] = null;
                                    self.upgrading = false;
                                    self.flush();
                                }
                            });
                        } else {
                            if (logger.isLoggable(Level.FINE)) {
                                logger.fine(String.format("probe transport '%s' failed", name));
                            }
                            EngineIOException err = new EngineIOException(Socket.PROBE_ERROR);
                            err.transport = transport[0].name;
                            self.emit(Socket.EVENT_UPGRADE_ERROR, err);
                        }
                    }
                });
            }
        };
        final Emitter.Listener freezeTransport = new Emitter.Listener(){

            @Override
            public void call(Object ... args) {
                if (failed[0]) {
                    return;
                }
                failed[0] = true;
                cleanup[0].run();
                transport[0].close();
                transport[0] = null;
            }
        };
        final Emitter.Listener onerror = new Emitter.Listener(){

            @Override
            public void call(Object ... args) {
                Object err = args[0];
                EngineIOException error = err instanceof Exception ? new EngineIOException(Socket.PROBE_ERROR, (Exception)err) : (err instanceof String ? new EngineIOException("probe error: " + (String)err) : new EngineIOException(Socket.PROBE_ERROR));
                error.transport = transport[0].name;
                freezeTransport.call(new Object[0]);
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine(String.format("probe transport \"%s\" failed because of error: %s", name, err));
                }
                self.emit(Socket.EVENT_UPGRADE_ERROR, error);
            }
        };
        final Emitter.Listener onTransportClose = new Emitter.Listener(){

            @Override
            public void call(Object ... args) {
                onerror.call("transport closed");
            }
        };
        final Emitter.Listener onclose = new Emitter.Listener(){

            @Override
            public void call(Object ... args) {
                onerror.call("socket closed");
            }
        };
        final Emitter.Listener onupgrade = new Emitter.Listener(){

            @Override
            public void call(Object ... args) {
                Transport to2 = (Transport)args[0];
                if (transport[0] != null && !to2.name.equals(transport[0].name)) {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine(String.format("'%s' works - aborting '%s'", to2.name, transport[0].name));
                    }
                    freezeTransport.call(new Object[0]);
                }
            }
        };
        cleanup[0] = new Runnable(){

            @Override
            public void run() {
                transport[0].off(Socket.EVENT_OPEN, onTransportOpen);
                transport[0].off(Socket.EVENT_ERROR, onerror);
                transport[0].off(Socket.EVENT_CLOSE, onTransportClose);
                self.off(Socket.EVENT_CLOSE, onclose);
                self.off(Socket.EVENT_UPGRADING, onupgrade);
            }
        };
        transport[0].once(EVENT_OPEN, onTransportOpen);
        transport[0].once(EVENT_ERROR, onerror);
        transport[0].once(EVENT_CLOSE, onTransportClose);
        this.once(EVENT_CLOSE, onclose);
        this.once(EVENT_UPGRADING, onupgrade);
        transport[0].open();
    }

    private void onOpen() {
        logger.fine("socket open");
        this.readyState = ReadyState.OPEN;
        priorWebsocketSuccess = "websocket".equals(this.transport.name);
        this.emit(EVENT_OPEN, new Object[0]);
        this.flush();
        if (this.readyState == ReadyState.OPEN && this.upgrade && this.transport instanceof Polling) {
            logger.fine("starting upgrade probes");
            for (String upgrade : this.upgrades) {
                this.probe(upgrade);
            }
        }
    }

    private void onPacket(Packet packet) {
        if (this.readyState == ReadyState.OPENING || this.readyState == ReadyState.OPEN || this.readyState == ReadyState.CLOSING) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(String.format("socket received: type '%s', data '%s'", packet.type, packet.data));
            }
            this.emit(EVENT_PACKET, packet);
            this.emit(EVENT_HEARTBEAT, new Object[0]);
            if (EVENT_OPEN.equals(packet.type)) {
                try {
                    this.onHandshake(new HandshakeData((String)packet.data));
                }
                catch (JSONException e2) {
                    this.emit(EVENT_ERROR, new EngineIOException(e2));
                }
            } else if (EVENT_PING.equals(packet.type)) {
                this.emit(EVENT_PING, new Object[0]);
                EventThread.exec(new Runnable(){

                    @Override
                    public void run() {
                        Socket.this.sendPacket(Socket.EVENT_PONG, null);
                    }
                });
            } else if (EVENT_ERROR.equals(packet.type)) {
                EngineIOException err = new EngineIOException("server error");
                err.code = packet.data;
                this.onError(err);
            } else if (EVENT_MESSAGE.equals(packet.type)) {
                this.emit(EVENT_DATA, packet.data);
                this.emit(EVENT_MESSAGE, packet.data);
            }
        } else if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("packet received with socket readyState '%s'", new Object[]{this.readyState}));
        }
    }

    private void onHandshake(HandshakeData data) {
        this.emit(EVENT_HANDSHAKE, data);
        this.id = data.sid;
        this.transport.query.put("sid", data.sid);
        this.upgrades = this.filterUpgrades(Arrays.asList(data.upgrades));
        this.pingInterval = data.pingInterval;
        this.pingTimeout = data.pingTimeout;
        this.onOpen();
        if (ReadyState.CLOSED == this.readyState) {
            return;
        }
        this.onHeartbeat();
        this.off(EVENT_HEARTBEAT, this.onHeartbeatAsListener);
        this.on(EVENT_HEARTBEAT, this.onHeartbeatAsListener);
    }

    private void onHeartbeat() {
        if (this.pingTimeoutTimer != null) {
            this.pingTimeoutTimer.cancel(false);
        }
        long timeout = this.pingInterval + this.pingTimeout;
        final Socket self = this;
        this.pingTimeoutTimer = this.getHeartbeatScheduler().schedule(new Runnable(){

            @Override
            public void run() {
                EventThread.exec(new Runnable(){

                    @Override
                    public void run() {
                        if (self.readyState == ReadyState.CLOSED) {
                            return;
                        }
                        self.onClose("ping timeout");
                    }
                });
            }
        }, timeout, TimeUnit.MILLISECONDS);
    }

    private void onDrain() {
        for (int i2 = 0; i2 < this.prevBufferLen; ++i2) {
            this.writeBuffer.poll();
        }
        this.prevBufferLen = 0;
        if (0 == this.writeBuffer.size()) {
            this.emit(EVENT_DRAIN, new Object[0]);
        } else {
            this.flush();
        }
    }

    private void flush() {
        if (this.readyState != ReadyState.CLOSED && this.transport.writable && !this.upgrading && this.writeBuffer.size() != 0) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(String.format("flushing %d packets in socket", this.writeBuffer.size()));
            }
            this.prevBufferLen = this.writeBuffer.size();
            this.transport.send(this.writeBuffer.toArray(new Packet[this.writeBuffer.size()]));
            this.emit(EVENT_FLUSH, new Object[0]);
        }
    }

    public void write(String msg) {
        this.write(msg, null);
    }

    public void write(String msg, Runnable fn2) {
        this.send(msg, fn2);
    }

    public void write(byte[] msg) {
        this.write(msg, null);
    }

    public void write(byte[] msg, Runnable fn2) {
        this.send(msg, fn2);
    }

    public void send(String msg) {
        this.send(msg, null);
    }

    public void send(byte[] msg) {
        this.send(msg, null);
    }

    public void send(final String msg, final Runnable fn2) {
        EventThread.exec(new Runnable(){

            @Override
            public void run() {
                Socket.this.sendPacket(Socket.EVENT_MESSAGE, msg, fn2);
            }
        });
    }

    public void send(final byte[] msg, final Runnable fn2) {
        EventThread.exec(new Runnable(){

            @Override
            public void run() {
                Socket.this.sendPacket(Socket.EVENT_MESSAGE, msg, fn2);
            }
        });
    }

    private void sendPacket(String type, Runnable fn2) {
        this.sendPacket(new Packet(type), fn2);
    }

    private void sendPacket(String type, String data, Runnable fn2) {
        Packet<String> packet = new Packet<String>(type, data);
        this.sendPacket(packet, fn2);
    }

    private void sendPacket(String type, byte[] data, Runnable fn2) {
        Packet<byte[]> packet = new Packet<byte[]>(type, data);
        this.sendPacket(packet, fn2);
    }

    private void sendPacket(Packet packet, final Runnable fn2) {
        if (ReadyState.CLOSING == this.readyState || ReadyState.CLOSED == this.readyState) {
            return;
        }
        this.emit(EVENT_PACKET_CREATE, packet);
        this.writeBuffer.offer(packet);
        if (null != fn2) {
            this.once(EVENT_FLUSH, new Emitter.Listener(){

                @Override
                public void call(Object ... args) {
                    fn2.run();
                }
            });
        }
        this.flush();
    }

    public Socket close() {
        EventThread.exec(new Runnable(){

            @Override
            public void run() {
                if (Socket.this.readyState == ReadyState.OPENING || Socket.this.readyState == ReadyState.OPEN) {
                    Emitter.Listener[] cleanupAndClose;
                    Socket.this.readyState = ReadyState.CLOSING;
                    final Socket self = Socket.this;
                    final Runnable close = new Runnable(){

                        @Override
                        public void run() {
                            self.onClose("forced close");
                            logger.fine("socket closing - telling transport to close");
                            self.transport.close();
                        }
                    };
                    cleanupAndClose = new Emitter.Listener[]{new Emitter.Listener(){

                        @Override
                        public void call(Object ... args) {
                            self.off(Socket.EVENT_UPGRADE, cleanupAndClose[0]);
                            self.off(Socket.EVENT_UPGRADE_ERROR, cleanupAndClose[0]);
                            close.run();
                        }
                    }};
                    final Runnable waitForUpgrade = new Runnable(){

                        @Override
                        public void run() {
                            self.once(Socket.EVENT_UPGRADE, cleanupAndClose[0]);
                            self.once(Socket.EVENT_UPGRADE_ERROR, cleanupAndClose[0]);
                        }
                    };
                    if (Socket.this.writeBuffer.size() > 0) {
                        Socket.this.once(Socket.EVENT_DRAIN, new Emitter.Listener(){

                            @Override
                            public void call(Object ... args) {
                                if (Socket.this.upgrading) {
                                    waitForUpgrade.run();
                                } else {
                                    close.run();
                                }
                            }
                        });
                    } else if (Socket.this.upgrading) {
                        waitForUpgrade.run();
                    } else {
                        close.run();
                    }
                }
            }
        });
        return this;
    }

    private void onError(Exception err) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("socket error %s", err));
        }
        priorWebsocketSuccess = false;
        this.emit(EVENT_ERROR, err);
        this.onClose("transport error", err);
    }

    private void onClose(String reason) {
        this.onClose(reason, null);
    }

    private void onClose(String reason, Exception desc) {
        if (ReadyState.OPENING == this.readyState || ReadyState.OPEN == this.readyState || ReadyState.CLOSING == this.readyState) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(String.format("socket close with reason: %s", reason));
            }
            Socket self = this;
            if (this.pingTimeoutTimer != null) {
                this.pingTimeoutTimer.cancel(false);
            }
            if (this.heartbeatScheduler != null) {
                this.heartbeatScheduler.shutdown();
            }
            this.transport.off(EVENT_CLOSE);
            this.transport.close();
            this.transport.off();
            this.readyState = ReadyState.CLOSED;
            this.id = null;
            this.emit(EVENT_CLOSE, reason, desc);
            self.writeBuffer.clear();
            self.prevBufferLen = 0;
        }
    }

    List<String> filterUpgrades(List<String> upgrades) {
        ArrayList<String> filteredUpgrades = new ArrayList<String>();
        for (String upgrade : upgrades) {
            if (!this.transports.contains(upgrade)) continue;
            filteredUpgrades.add(upgrade);
        }
        return filteredUpgrades;
    }

    public String id() {
        return this.id;
    }

    private ScheduledExecutorService getHeartbeatScheduler() {
        if (this.heartbeatScheduler == null || this.heartbeatScheduler.isShutdown()) {
            this.heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();
        }
        return this.heartbeatScheduler;
    }

    public static class Options
    extends Transport.Options {
        public String[] transports;
        public boolean upgrade = true;
        public boolean rememberUpgrade;
        public String host;
        public String query;
        public Map<String, Transport.Options> transportOptions;

        private static Options fromURI(URI uri, Options opts) {
            if (opts == null) {
                opts = new Options();
            }
            opts.host = uri.getHost();
            opts.secure = "https".equals(uri.getScheme()) || "wss".equals(uri.getScheme());
            opts.port = uri.getPort();
            String query = uri.getRawQuery();
            if (query != null) {
                opts.query = query;
            }
            return opts;
        }
    }

    private static enum ReadyState {
        OPENING,
        OPEN,
        CLOSING,
        CLOSED;


        public String toString() {
            return super.toString().toLowerCase();
        }
    }
}

