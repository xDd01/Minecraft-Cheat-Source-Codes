package io.github.nevalackin.client.api.notification;

import io.github.nevalackin.client.impl.event.render.overlay.RenderGameOverlayEvent;
import io.github.nevalackin.client.impl.notification.Notification;
import net.minecraft.client.gui.ScaledResolution;

public interface NotificationManager {

    void add(final NotificationType type, final String title, final String body, final long duration);

    void onDraw(final ScaledResolution scaledResolution);

}
