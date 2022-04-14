package io.netty.util;

import java.util.concurrent.*;
import java.util.*;

public interface Timer
{
    Timeout newTimeout(final TimerTask p0, final long p1, final TimeUnit p2);
    
    Set<Timeout> stop();
}
