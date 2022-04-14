/*
 * Decompiled with CFR 0.152.
 */
package io.socket.client;

import io.socket.client.Ack;
import io.socket.client.Manager;
import io.socket.client.On;
import io.socket.client.SocketIOException;
import io.socket.emitter.Emitter;
import io.socket.parser.Packet;
import io.socket.thread.EventThread;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Socket
extends Emitter {
    private static final Logger logger = Logger.getLogger(Socket.class.getName());
    public static final String EVENT_CONNECT = "connect";
    public static final String EVENT_DISCONNECT = "disconnect";
    public static final String EVENT_CONNECT_ERROR = "connect_error";
    static final String EVENT_MESSAGE = "message";
    protected static Map<String, Integer> RESERVED_EVENTS = new HashMap<String, Integer>(){
        {
            this.put(Socket.EVENT_CONNECT, 1);
            this.put(Socket.EVENT_CONNECT_ERROR, 1);
            this.put(Socket.EVENT_DISCONNECT, 1);
            this.put("disconnecting", 1);
            this.put("newListener", 1);
            this.put("removeListener", 1);
        }
    };
    String id;
    private volatile boolean connected;
    private int ids;
    private String nsp;
    private Manager io;
    private Map<String, String> auth;
    private Map<Integer, Ack> acks = new HashMap<Integer, Ack>();
    private Queue<On.Handle> subs;
    private final Queue<List<Object>> receiveBuffer = new LinkedList<List<Object>>();
    private final Queue<Packet<JSONArray>> sendBuffer = new LinkedList<Packet<JSONArray>>();

    public Socket(Manager io2, String nsp, Manager.Options opts) {
        this.io = io2;
        this.nsp = nsp;
        if (opts != null) {
            this.auth = opts.auth;
        }
    }

    private void subEvents() {
        if (this.subs != null) {
            return;
        }
        final Manager io2 = this.io;
        this.subs = new LinkedList<On.Handle>(){
            {
                this.add(On.on(io2, "open", new Emitter.Listener(){

                    @Override
                    public void call(Object ... args) {
                        Socket.this.onopen();
                    }
                }));
                this.add(On.on(io2, "packet", new Emitter.Listener(){

                    @Override
                    public void call(Object ... args) {
                        Socket.this.onpacket((Packet)args[0]);
                    }
                }));
                this.add(On.on(io2, "error", new Emitter.Listener(){

                    @Override
                    public void call(Object ... args) {
                        if (!Socket.this.connected) {
                            Socket.super.emit(Socket.EVENT_CONNECT_ERROR, new Object[]{args[0]});
                        }
                    }
                }));
                this.add(On.on(io2, "close", new Emitter.Listener(){

                    @Override
                    public void call(Object ... args) {
                        Socket.this.onclose(args.length > 0 ? (String)args[0] : null);
                    }
                }));
            }
        };
    }

    public boolean isActive() {
        return this.subs != null;
    }

    public Socket open() {
        EventThread.exec(new Runnable(){

            @Override
            public void run() {
                if (Socket.this.connected || Socket.this.io.isReconnecting()) {
                    return;
                }
                Socket.this.subEvents();
                Socket.this.io.open();
                if (Manager.ReadyState.OPEN == ((Socket)Socket.this).io.readyState) {
                    Socket.this.onopen();
                }
            }
        });
        return this;
    }

    public Socket connect() {
        return this.open();
    }

    public Socket send(final Object ... args) {
        EventThread.exec(new Runnable(){

            @Override
            public void run() {
                Socket.this.emit(Socket.EVENT_MESSAGE, args);
            }
        });
        return this;
    }

    @Override
    public Emitter emit(final String event, final Object ... args) {
        if (RESERVED_EVENTS.containsKey(event)) {
            throw new RuntimeException("'" + event + "' is a reserved event name");
        }
        EventThread.exec(new Runnable(){

            @Override
            public void run() {
                Ack ack2;
                Object[] _args;
                int lastIndex = args.length - 1;
                if (args.length > 0 && args[lastIndex] instanceof Ack) {
                    _args = new Object[lastIndex];
                    for (int i2 = 0; i2 < lastIndex; ++i2) {
                        _args[i2] = args[i2];
                    }
                    ack2 = (Ack)args[lastIndex];
                } else {
                    _args = args;
                    ack2 = null;
                }
                Socket.this.emit(event, _args, ack2);
            }
        });
        return this;
    }

    public Emitter emit(final String event, final Object[] args, final Ack ack2) {
        EventThread.exec(new Runnable(){

            @Override
            public void run() {
                JSONArray jsonArgs = new JSONArray();
                jsonArgs.put(event);
                if (args != null) {
                    for (Object arg2 : args) {
                        jsonArgs.put(arg2);
                    }
                }
                Packet<JSONArray> packet = new Packet<JSONArray>(2, jsonArgs);
                if (ack2 != null) {
                    logger.fine(String.format("emitting packet with ack id %d", Socket.this.ids));
                    Socket.this.acks.put(Socket.this.ids, ack2);
                    packet.id = Socket.this.ids++;
                }
                if (Socket.this.connected) {
                    Socket.this.packet(packet);
                } else {
                    Socket.this.sendBuffer.add(packet);
                }
            }
        });
        return this;
    }

    private void packet(Packet packet) {
        packet.nsp = this.nsp;
        this.io.packet(packet);
    }

    private void onopen() {
        logger.fine("transport is open - connecting");
        if (this.auth != null) {
            this.packet(new Packet<JSONObject>(0, new JSONObject(this.auth)));
        } else {
            this.packet(new Packet(0));
        }
    }

    private void onclose(String reason) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("close (%s)", reason));
        }
        this.connected = false;
        this.id = null;
        super.emit(EVENT_DISCONNECT, reason);
    }

    private void onpacket(Packet<?> packet) {
        if (!this.nsp.equals(packet.nsp)) {
            return;
        }
        switch (packet.type) {
            case 0: {
                if (packet.data instanceof JSONObject && ((JSONObject)packet.data).has("sid")) {
                    try {
                        this.onconnect(((JSONObject)packet.data).getString("sid"));
                        return;
                    }
                    catch (JSONException jSONException) {
                        break;
                    }
                }
                super.emit(EVENT_CONNECT_ERROR, new SocketIOException("It seems you are trying to reach a Socket.IO server in v2.x with a v3.x client, which is not possible"));
                break;
            }
            case 2: {
                Packet<JSONArray> p2 = packet;
                this.onevent(p2);
                break;
            }
            case 5: {
                Packet<JSONArray> p3 = packet;
                this.onevent(p3);
                break;
            }
            case 3: {
                Packet<JSONArray> p4 = packet;
                this.onack(p4);
                break;
            }
            case 6: {
                Packet<JSONArray> p5 = packet;
                this.onack(p5);
                break;
            }
            case 1: {
                this.ondisconnect();
                break;
            }
            case 4: {
                super.emit(EVENT_CONNECT_ERROR, packet.data);
            }
        }
    }

    private void onevent(Packet<JSONArray> packet) {
        ArrayList<Object> args = new ArrayList<Object>(Arrays.asList(Socket.toArray((JSONArray)packet.data)));
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("emitting event %s", args));
        }
        if (packet.id >= 0) {
            logger.fine("attaching ack callback to event");
            args.add(this.ack(packet.id));
        }
        if (this.connected) {
            if (args.isEmpty()) {
                return;
            }
            String event = args.remove(0).toString();
            super.emit(event, args.toArray());
        } else {
            this.receiveBuffer.add(args);
        }
    }

    private Ack ack(final int id2) {
        final Socket self = this;
        final boolean[] sent = new boolean[]{false};
        return new Ack(){

            @Override
            public void call(final Object ... args) {
                EventThread.exec(new Runnable(){

                    @Override
                    public void run() {
                        if (sent[0]) {
                            return;
                        }
                        sent[0] = true;
                        if (logger.isLoggable(Level.FINE)) {
                            logger.fine(String.format("sending ack %s", args.length != 0 ? args : null));
                        }
                        JSONArray jsonArgs = new JSONArray();
                        for (Object arg2 : args) {
                            jsonArgs.put(arg2);
                        }
                        Packet<JSONArray> packet = new Packet<JSONArray>(3, jsonArgs);
                        packet.id = id2;
                        self.packet(packet);
                    }
                });
            }
        };
    }

    private void onack(Packet<JSONArray> packet) {
        Ack fn2 = this.acks.remove(packet.id);
        if (fn2 != null) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(String.format("calling ack %s with %s", packet.id, packet.data));
            }
            fn2.call(Socket.toArray((JSONArray)packet.data));
        } else if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("bad ack %s", packet.id));
        }
    }

    private void onconnect(String id2) {
        this.connected = true;
        this.id = id2;
        this.emitBuffered();
        super.emit(EVENT_CONNECT, new Object[0]);
    }

    private void emitBuffered() {
        Packet<JSONArray> packet;
        List<Object> data;
        while ((data = this.receiveBuffer.poll()) != null) {
            String event = (String)data.get(0);
            super.emit(event, data.toArray());
        }
        this.receiveBuffer.clear();
        while ((packet = this.sendBuffer.poll()) != null) {
            this.packet(packet);
        }
        this.sendBuffer.clear();
    }

    private void ondisconnect() {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("server disconnect (%s)", this.nsp));
        }
        this.destroy();
        this.onclose("io server disconnect");
    }

    private void destroy() {
        if (this.subs != null) {
            for (On.Handle sub : this.subs) {
                sub.destroy();
            }
            this.subs = null;
        }
        this.io.destroy();
    }

    public Socket close() {
        EventThread.exec(new Runnable(){

            @Override
            public void run() {
                if (Socket.this.connected) {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine(String.format("performing disconnect (%s)", Socket.this.nsp));
                    }
                    Socket.this.packet(new Packet(1));
                }
                Socket.this.destroy();
                if (Socket.this.connected) {
                    Socket.this.onclose("io client disconnect");
                }
            }
        });
        return this;
    }

    public Socket disconnect() {
        return this.close();
    }

    public Manager io() {
        return this.io;
    }

    public boolean connected() {
        return this.connected;
    }

    public String id() {
        return this.id;
    }

    private static Object[] toArray(JSONArray array) {
        int length = array.length();
        Object[] data = new Object[length];
        for (int i2 = 0; i2 < length; ++i2) {
            Object v2;
            try {
                v2 = array.get(i2);
            }
            catch (JSONException e2) {
                logger.log(Level.WARNING, "An error occured while retrieving data from JSONArray", e2);
                v2 = null;
            }
            data[i2] = JSONObject.NULL.equals(v2) ? null : v2;
        }
        return data;
    }
}

