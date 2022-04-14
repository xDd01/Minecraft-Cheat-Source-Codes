/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.event.bus;

import cafe.corrosion.event.Event;
import cafe.corrosion.event.handler.IHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventBus {
    private static final Map<IHandler, Map<Class<? extends Event>, List<Consumer<? extends Event>>>> EVENT_HANDLERS = new HashMap<IHandler, Map<Class<? extends Event>, List<Consumer<? extends Event>>>>();

    public <T extends Event> void register(IHandler handler, Class<T> clazz, Consumer<T> consumer) {
        Map map = EVENT_HANDLERS.getOrDefault(handler, new HashMap());
        List consumers = map.getOrDefault(clazz, new ArrayList());
        consumers.add(consumer);
        map.put(clazz, consumers);
        EVENT_HANDLERS.put(handler, map);
    }

    public <T extends Event> void handle(T event) {
        try {
            Class<?> clazz = event.getClass();
            EVENT_HANDLERS.forEach((handler, map) -> {
                if (!handler.isEnabled()) {
                    return;
                }
                map.forEach((eventClass, list) -> {
                    if (!eventClass.equals(clazz)) {
                        return;
                    }
                    list.forEach(consumer -> {
                        Consumer castedConsumer = consumer;
                        castedConsumer.accept(event);
                    });
                });
            });
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}

