package org.apache.logging.log4j.core.jmx;

import java.util.*;
import org.apache.logging.log4j.status.*;

public interface StatusLoggerAdminMBean
{
    public static final String NAME = "org.apache.logging.log4j2:type=StatusLogger";
    public static final String NOTIF_TYPE_DATA = "com.apache.logging.log4j.core.jmx.statuslogger.data";
    public static final String NOTIF_TYPE_MESSAGE = "com.apache.logging.log4j.core.jmx.statuslogger.message";
    
    List<StatusData> getStatusData();
    
    String[] getStatusDataHistory();
    
    String getLevel();
    
    void setLevel(final String p0);
}
