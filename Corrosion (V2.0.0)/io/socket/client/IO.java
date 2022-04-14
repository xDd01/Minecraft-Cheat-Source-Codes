/*
 * Decompiled with CFR 0.152.
 */
package io.socket.client;

import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.client.SocketOptionBuilder;
import io.socket.client.Url;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.Call;
import okhttp3.WebSocket;

public class IO {
    private static final Logger logger = Logger.getLogger(IO.class.getName());
    private static final ConcurrentHashMap<String, Manager> managers = new ConcurrentHashMap();
    public static int protocol = 5;

    public static void setDefaultOkHttpWebSocketFactory(WebSocket.Factory factory) {
        Manager.defaultWebSocketFactory = factory;
    }

    public static void setDefaultOkHttpCallFactory(Call.Factory factory) {
        Manager.defaultCallFactory = factory;
    }

    private IO() {
    }

    public static Socket socket(String uri) throws URISyntaxException {
        return IO.socket(uri, null);
    }

    public static Socket socket(String uri, Options opts) throws URISyntaxException {
        return IO.socket(new URI(uri), opts);
    }

    public static Socket socket(URI uri) {
        return IO.socket(uri, null);
    }

    public static Socket socket(URI uri, Options opts) {
        Manager io2;
        if (opts == null) {
            opts = new Options();
        }
        Url.ParsedURI parsed = Url.parse(uri);
        URI source = parsed.uri;
        String id2 = parsed.id;
        boolean sameNamespace = managers.containsKey(id2) && IO.managers.get((Object)id2).nsps.containsKey(source.getPath());
        boolean newConnection = opts.forceNew || !opts.multiplex || sameNamespace;
        String query = source.getQuery();
        if (query != null && (opts.query == null || opts.query.isEmpty())) {
            opts.query = query;
        }
        if (newConnection) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(String.format("ignoring socket cache for %s", source));
            }
            io2 = new Manager(source, opts);
        } else {
            if (!managers.containsKey(id2)) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine(String.format("new io instance for %s", source));
                }
                managers.putIfAbsent(id2, new Manager(source, opts));
            }
            io2 = managers.get(id2);
        }
        return io2.socket(source.getPath(), opts);
    }

    public static class Options
    extends Manager.Options {
        public boolean forceNew;
        public boolean multiplex = true;

        public static SocketOptionBuilder builder() {
            return SocketOptionBuilder.builder();
        }
    }
}

