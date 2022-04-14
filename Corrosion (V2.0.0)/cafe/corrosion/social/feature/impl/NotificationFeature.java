/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.social.feature.impl;

import cafe.corrosion.event.impl.Event2DRender;
import cafe.corrosion.notification.Notification;
import cafe.corrosion.social.feature.Feature;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class NotificationFeature
extends Feature {
    private final List<Notification> notifications = new ArrayList<Notification>();

    public NotificationFeature() {
        this.registerEventHandler(Event2DRender.class, event -> {
            AtomicInteger counter = new AtomicInteger();
            this.notifications.removeIf(Notification::isCompleted);
            this.notifications.stream().filter(notification -> !notification.isActive() && !notification.isCompleted()).forEach(Notification::start);
            this.notifications.stream().filter(Notification::isActive).forEach(notification -> notification.draw(counter.incrementAndGet()));
        });
    }

    public void displayNotification(Notification notification) {
        this.notifications.add(notification);
    }
}

