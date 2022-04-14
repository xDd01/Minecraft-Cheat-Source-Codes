package io.netty.channel.sctp;

import io.netty.channel.*;
import com.sun.nio.sctp.*;
import java.net.*;

public class SctpChannelOption<T> extends ChannelOption<T>
{
    public static final SctpChannelOption<Boolean> SCTP_DISABLE_FRAGMENTS;
    public static final SctpChannelOption<Boolean> SCTP_EXPLICIT_COMPLETE;
    public static final SctpChannelOption<Integer> SCTP_FRAGMENT_INTERLEAVE;
    public static final SctpChannelOption<SctpStandardSocketOptions.InitMaxStreams> SCTP_INIT_MAXSTREAMS;
    public static final SctpChannelOption<Boolean> SCTP_NODELAY;
    public static final SctpChannelOption<SocketAddress> SCTP_PRIMARY_ADDR;
    public static final SctpChannelOption<SocketAddress> SCTP_SET_PEER_PRIMARY_ADDR;
    
    @Deprecated
    protected SctpChannelOption(final String name) {
        super(name);
    }
    
    static {
        SCTP_DISABLE_FRAGMENTS = new SctpChannelOption<Boolean>("SCTP_DISABLE_FRAGMENTS");
        SCTP_EXPLICIT_COMPLETE = new SctpChannelOption<Boolean>("SCTP_EXPLICIT_COMPLETE");
        SCTP_FRAGMENT_INTERLEAVE = new SctpChannelOption<Integer>("SCTP_FRAGMENT_INTERLEAVE");
        SCTP_INIT_MAXSTREAMS = new SctpChannelOption<SctpStandardSocketOptions.InitMaxStreams>("SCTP_INIT_MAXSTREAMS");
        SCTP_NODELAY = new SctpChannelOption<Boolean>("SCTP_NODELAY");
        SCTP_PRIMARY_ADDR = new SctpChannelOption<SocketAddress>("SCTP_PRIMARY_ADDR");
        SCTP_SET_PEER_PRIMARY_ADDR = new SctpChannelOption<SocketAddress>("SCTP_SET_PEER_PRIMARY_ADDR");
    }
}
