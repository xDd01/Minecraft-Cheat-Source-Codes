/*
 * Decompiled with CFR 0.152.
 */
package io.socket.engineio.client.transports;

import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;
import io.socket.engineio.parser.Packet;
import io.socket.engineio.parser.Parser;
import io.socket.parseqs.ParseQS;
import io.socket.thread.EventThread;
import io.socket.yeast.Yeast;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Polling
extends Transport {
    private static final Logger logger = Logger.getLogger(Polling.class.getName());
    public static final String NAME = "polling";
    public static final String EVENT_POLL = "poll";
    public static final String EVENT_POLL_COMPLETE = "pollComplete";
    private boolean polling;

    public Polling(Transport.Options opts) {
        super(opts);
        this.name = NAME;
    }

    @Override
    protected void doOpen() {
        this.poll();
    }

    public void pause(final Runnable onPause) {
        EventThread.exec(new Runnable(){

            @Override
            public void run() {
                final Polling self = Polling.this;
                Polling.this.readyState = Transport.ReadyState.PAUSED;
                final Runnable pause = new Runnable(){

                    @Override
                    public void run() {
                        logger.fine("paused");
                        self.readyState = Transport.ReadyState.PAUSED;
                        onPause.run();
                    }
                };
                if (Polling.this.polling || !Polling.this.writable) {
                    final int[] total = new int[]{0};
                    if (Polling.this.polling) {
                        logger.fine("we are currently polling - waiting to pause");
                        total[0] = total[0] + 1;
                        Polling.this.once(Polling.EVENT_POLL_COMPLETE, new Emitter.Listener(){

                            @Override
                            public void call(Object ... args) {
                                logger.fine("pre-pause polling complete");
                                total[0] = total[0] - 1;
                                if (total[0] == 0) {
                                    pause.run();
                                }
                            }
                        });
                    }
                    if (!Polling.this.writable) {
                        logger.fine("we are currently writing - waiting to pause");
                        total[0] = total[0] + 1;
                        Polling.this.once("drain", new Emitter.Listener(){

                            @Override
                            public void call(Object ... args) {
                                logger.fine("pre-pause writing complete");
                                total[0] = total[0] - 1;
                                if (total[0] == 0) {
                                    pause.run();
                                }
                            }
                        });
                    }
                } else {
                    pause.run();
                }
            }
        });
    }

    private void poll() {
        logger.fine(NAME);
        this.polling = true;
        this.doPoll();
        this.emit(EVENT_POLL, new Object[0]);
    }

    @Override
    protected void onData(String data) {
        this._onData(data);
    }

    @Override
    protected void onData(byte[] data) {
        this._onData(data);
    }

    private void _onData(Object data) {
        final Polling self = this;
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("polling got data %s", data));
        }
        Parser.DecodePayloadCallback callback = new Parser.DecodePayloadCallback(){

            public boolean call(Packet packet, int index, int total) {
                if (self.readyState == Transport.ReadyState.OPENING && "open".equals(packet.type)) {
                    self.onOpen();
                }
                if ("close".equals(packet.type)) {
                    self.onClose();
                    return false;
                }
                self.onPacket(packet);
                return true;
            }
        };
        Parser.decodePayload((String)data, callback);
        if (this.readyState != Transport.ReadyState.CLOSED) {
            this.polling = false;
            this.emit(EVENT_POLL_COMPLETE, new Object[0]);
            if (this.readyState == Transport.ReadyState.OPEN) {
                this.poll();
            } else if (logger.isLoggable(Level.FINE)) {
                logger.fine(String.format("ignoring poll - transport state '%s'", new Object[]{this.readyState}));
            }
        }
    }

    @Override
    protected void doClose() {
        final Polling self = this;
        Emitter.Listener close = new Emitter.Listener(){

            @Override
            public void call(Object ... args) {
                logger.fine("writing close packet");
                self.write(new Packet[]{new Packet("close")});
            }
        };
        if (this.readyState == Transport.ReadyState.OPEN) {
            logger.fine("transport open - closing");
            close.call(new Object[0]);
        } else {
            logger.fine("transport not open - deferring close");
            this.once("open", close);
        }
    }

    @Override
    protected void write(Packet[] packets) {
        final Polling self = this;
        this.writable = false;
        final Runnable callbackfn = new Runnable(){

            @Override
            public void run() {
                self.writable = true;
                self.emit("drain", new Object[0]);
            }
        };
        Parser.encodePayload(packets, new Parser.EncodeCallback<String>(){

            @Override
            public void call(String data) {
                self.doWrite(data, callbackfn);
            }
        });
    }

    protected String uri() {
        HashMap<String, String> query = this.query;
        if (query == null) {
            query = new HashMap<String, String>();
        }
        String schema = this.secure ? "https" : "http";
        String port = "";
        if (this.timestampRequests) {
            query.put(this.timestampParam, Yeast.yeast());
        }
        String derivedQuery = ParseQS.encode(query);
        if (this.port > 0 && ("https".equals(schema) && this.port != 443 || "http".equals(schema) && this.port != 80)) {
            port = ":" + this.port;
        }
        if (derivedQuery.length() > 0) {
            derivedQuery = "?" + derivedQuery;
        }
        boolean ipv6 = this.hostname.contains(":");
        return schema + "://" + (ipv6 ? "[" + this.hostname + "]" : this.hostname) + port + this.path + derivedQuery;
    }

    protected abstract void doWrite(String var1, Runnable var2);

    protected abstract void doPoll();
}

