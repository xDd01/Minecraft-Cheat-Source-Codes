package io.netty.channel.sctp;

import com.sun.nio.sctp.*;

public final class SctpNotificationHandler extends AbstractNotificationHandler<Object>
{
    private final SctpChannel sctpChannel;
    
    public SctpNotificationHandler(final SctpChannel sctpChannel) {
        if (sctpChannel == null) {
            throw new NullPointerException("sctpChannel");
        }
        this.sctpChannel = sctpChannel;
    }
    
    @Override
    public HandlerResult handleNotification(final AssociationChangeNotification notification, final Object o) {
        this.fireEvent(notification);
        return HandlerResult.CONTINUE;
    }
    
    @Override
    public HandlerResult handleNotification(final PeerAddressChangeNotification notification, final Object o) {
        this.fireEvent(notification);
        return HandlerResult.CONTINUE;
    }
    
    @Override
    public HandlerResult handleNotification(final SendFailedNotification notification, final Object o) {
        this.fireEvent(notification);
        return HandlerResult.CONTINUE;
    }
    
    @Override
    public HandlerResult handleNotification(final ShutdownNotification notification, final Object o) {
        this.fireEvent(notification);
        this.sctpChannel.close();
        return HandlerResult.RETURN;
    }
    
    private void fireEvent(final Notification notification) {
        this.sctpChannel.pipeline().fireUserEventTriggered(notification);
    }
}
