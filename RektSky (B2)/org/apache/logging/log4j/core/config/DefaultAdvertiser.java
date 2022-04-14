package org.apache.logging.log4j.core.config;

import org.apache.logging.log4j.core.net.*;
import org.apache.logging.log4j.core.config.plugins.*;
import java.util.*;

@Plugin(name = "default", category = "Core", elementType = "advertiser", printObject = false)
public class DefaultAdvertiser implements Advertiser
{
    @Override
    public Object advertise(final Map<String, String> properties) {
        return null;
    }
    
    @Override
    public void unadvertise(final Object advertisedObject) {
    }
}
