/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.social.socket;

import cafe.corrosion.Corrosion;
import cafe.corrosion.event.impl.social.EventSocialPacketReceive;
import cafe.corrosion.event.impl.social.EventSocialPacketSend;
import cafe.corrosion.social.packet.SocialPacketIn;
import cafe.corrosion.social.packet.SocialPacketOut;
import cafe.corrosion.social.packet.adapter.PacketAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.socket.client.IO;
import io.socket.client.Socket;
import java.net.URI;

public class WebSocket {
    private final Gson gson = new Gson();
    private final Socket websocket;

    public WebSocket() {
        IO.Options options = IO.Options.builder().setReconnection(true).setReconnectionAttempts(Integer.MAX_VALUE).setReconnectionDelay(1000L).setReconnectionDelayMax(5000L).setRandomizationFactor(0.5).setTimeout(30000L).build();
        URI uri = URI.create("https://socket.corrosion.lol/");
        Socket websocket = IO.socket(uri, options);
        websocket.on("packet", objects -> {
            String object = objects[0].toString();
            JsonObject jsonObject = this.gson.fromJson(object, JsonObject.class);
            Object packet = PacketAdapter.adaptInbound(jsonObject);
            Corrosion.INSTANCE.getEventBus().handle(new EventSocialPacketReceive((SocialPacketIn)packet));
        });
        this.websocket = websocket;
    }

    public void write(SocialPacketOut packet) {
        JsonObject serialized = packet.serialize();
        EventSocialPacketSend event = new EventSocialPacketSend(packet);
        Corrosion.INSTANCE.getEventBus().handle(event);
        if (event.isCancelled()) {
            return;
        }
        serialized.addProperty("packet-type", PacketAdapter.getType(packet));
        this.websocket.emit("packet", serialized.getAsString());
    }
}

