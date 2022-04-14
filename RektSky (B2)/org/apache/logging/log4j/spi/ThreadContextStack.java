package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.*;
import java.util.*;

public interface ThreadContextStack extends ThreadContext.ContextStack, Collection<String>
{
}
