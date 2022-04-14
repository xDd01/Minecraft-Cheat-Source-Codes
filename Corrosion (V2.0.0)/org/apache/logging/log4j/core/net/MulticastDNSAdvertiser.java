/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.net;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.helpers.Integers;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(name="multicastdns", category="Core", elementType="advertiser", printObject=false)
public class MulticastDNSAdvertiser
implements Advertiser {
    protected static final Logger LOGGER = StatusLogger.getLogger();
    private static Object jmDNS = MulticastDNSAdvertiser.initializeJMDNS();
    private static Class<?> jmDNSClass;
    private static Class<?> serviceInfoClass;

    @Override
    public Object advertise(Map<String, String> properties) {
        HashMap<String, String> truncatedProperties = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (entry.getKey().length() > 255 || entry.getValue().length() > 255) continue;
            truncatedProperties.put(entry.getKey(), entry.getValue());
        }
        String protocol = (String)truncatedProperties.get("protocol");
        String zone = "._log4j._" + (protocol != null ? protocol : "tcp") + ".local.";
        String portString = (String)truncatedProperties.get("port");
        int port = Integers.parseInt(portString, 4555);
        String name = (String)truncatedProperties.get("name");
        if (jmDNS != null) {
            boolean isVersion3 = false;
            try {
                jmDNSClass.getMethod("create", null);
                isVersion3 = true;
            }
            catch (NoSuchMethodException e2) {
                // empty catch block
            }
            Object serviceInfo = isVersion3 ? this.buildServiceInfoVersion3(zone, port, name, truncatedProperties) : this.buildServiceInfoVersion1(zone, port, name, truncatedProperties);
            try {
                Method method = jmDNSClass.getMethod("registerService", serviceInfoClass);
                method.invoke(jmDNS, serviceInfo);
            }
            catch (IllegalAccessException e3) {
                LOGGER.warn("Unable to invoke registerService method", (Throwable)e3);
            }
            catch (NoSuchMethodException e4) {
                LOGGER.warn("No registerService method", (Throwable)e4);
            }
            catch (InvocationTargetException e5) {
                LOGGER.warn("Unable to invoke registerService method", (Throwable)e5);
            }
            return serviceInfo;
        }
        LOGGER.warn("JMDNS not available - will not advertise ZeroConf support");
        return null;
    }

    @Override
    public void unadvertise(Object serviceInfo) {
        if (jmDNS != null) {
            try {
                Method method = jmDNSClass.getMethod("unregisterService", serviceInfoClass);
                method.invoke(jmDNS, serviceInfo);
            }
            catch (IllegalAccessException e2) {
                LOGGER.warn("Unable to invoke unregisterService method", (Throwable)e2);
            }
            catch (NoSuchMethodException e3) {
                LOGGER.warn("No unregisterService method", (Throwable)e3);
            }
            catch (InvocationTargetException e4) {
                LOGGER.warn("Unable to invoke unregisterService method", (Throwable)e4);
            }
        }
    }

    private static Object createJmDNSVersion1() {
        try {
            return jmDNSClass.newInstance();
        }
        catch (InstantiationException e2) {
            LOGGER.warn("Unable to instantiate JMDNS", (Throwable)e2);
        }
        catch (IllegalAccessException e3) {
            LOGGER.warn("Unable to instantiate JMDNS", (Throwable)e3);
        }
        return null;
    }

    private static Object createJmDNSVersion3() {
        try {
            Method jmDNSCreateMethod = jmDNSClass.getMethod("create", null);
            return jmDNSCreateMethod.invoke(null, null);
        }
        catch (IllegalAccessException e2) {
            LOGGER.warn("Unable to instantiate jmdns class", (Throwable)e2);
        }
        catch (NoSuchMethodException e3) {
            LOGGER.warn("Unable to access constructor", (Throwable)e3);
        }
        catch (InvocationTargetException e4) {
            LOGGER.warn("Unable to call constructor", (Throwable)e4);
        }
        return null;
    }

    private Object buildServiceInfoVersion1(String zone, int port, String name, Map<String, String> properties) {
        Hashtable<String, String> hashtableProperties = new Hashtable<String, String>(properties);
        try {
            Class[] args = new Class[]{String.class, String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Hashtable.class};
            Constructor<?> constructor = serviceInfoClass.getConstructor(args);
            Object[] values = new Object[]{zone, name, port, 0, 0, hashtableProperties};
            return constructor.newInstance(values);
        }
        catch (IllegalAccessException e2) {
            LOGGER.warn("Unable to construct ServiceInfo instance", (Throwable)e2);
        }
        catch (NoSuchMethodException e3) {
            LOGGER.warn("Unable to get ServiceInfo constructor", (Throwable)e3);
        }
        catch (InstantiationException e4) {
            LOGGER.warn("Unable to construct ServiceInfo instance", (Throwable)e4);
        }
        catch (InvocationTargetException e5) {
            LOGGER.warn("Unable to construct ServiceInfo instance", (Throwable)e5);
        }
        return null;
    }

    private Object buildServiceInfoVersion3(String zone, int port, String name, Map<String, String> properties) {
        try {
            Class[] args = new Class[]{String.class, String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Map.class};
            Method serviceInfoCreateMethod = serviceInfoClass.getMethod("create", args);
            Object[] values = new Object[]{zone, name, port, 0, 0, properties};
            return serviceInfoCreateMethod.invoke(null, values);
        }
        catch (IllegalAccessException e2) {
            LOGGER.warn("Unable to invoke create method", (Throwable)e2);
        }
        catch (NoSuchMethodException e3) {
            LOGGER.warn("Unable to find create method", (Throwable)e3);
        }
        catch (InvocationTargetException e4) {
            LOGGER.warn("Unable to invoke create method", (Throwable)e4);
        }
        return null;
    }

    private static Object initializeJMDNS() {
        try {
            jmDNSClass = Class.forName("javax.jmdns.JmDNS");
            serviceInfoClass = Class.forName("javax.jmdns.ServiceInfo");
            boolean isVersion3 = false;
            try {
                jmDNSClass.getMethod("create", null);
                isVersion3 = true;
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
            if (isVersion3) {
                return MulticastDNSAdvertiser.createJmDNSVersion3();
            }
            return MulticastDNSAdvertiser.createJmDNSVersion1();
        }
        catch (ClassNotFoundException e2) {
            LOGGER.warn("JmDNS or serviceInfo class not found", (Throwable)e2);
        }
        catch (ExceptionInInitializerError e2) {
            LOGGER.warn("JmDNS or serviceInfo class not found", (Throwable)e2);
        }
        return null;
    }
}

