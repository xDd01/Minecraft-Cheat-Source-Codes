package io.netty.channel.group;

import io.netty.channel.*;
import java.util.*;

public class ChannelGroupException extends ChannelException implements Iterable<Map.Entry<Channel, Throwable>>
{
    private static final long serialVersionUID = -4093064295562629453L;
    private final Collection<Map.Entry<Channel, Throwable>> failed;
    
    public ChannelGroupException(final Collection<Map.Entry<Channel, Throwable>> causes) {
        if (causes == null) {
            throw new NullPointerException("causes");
        }
        if (causes.isEmpty()) {
            throw new IllegalArgumentException("causes must be non empty");
        }
        this.failed = Collections.unmodifiableCollection((Collection<? extends Map.Entry<Channel, Throwable>>)causes);
    }
    
    @Override
    public Iterator<Map.Entry<Channel, Throwable>> iterator() {
        return this.failed.iterator();
    }
}
