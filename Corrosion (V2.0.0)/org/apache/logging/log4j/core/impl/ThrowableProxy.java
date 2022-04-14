/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.impl;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.impl.ReflectiveCallerClassUtility;
import org.apache.logging.log4j.core.impl.StackTracePackageElement;
import org.apache.logging.log4j.status.StatusLogger;

public class ThrowableProxy
implements Serializable {
    private static final long serialVersionUID = -2752771578252251910L;
    private static final Logger LOGGER;
    private static final PrivateSecurityManager SECURITY_MANAGER;
    private static final Method GET_SUPPRESSED;
    private static final Method ADD_SUPPRESSED;
    private final ThrowableProxy proxyCause;
    private final Throwable throwable;
    private final String name;
    private final StackTracePackageElement[] callerPackageData;
    private int commonElementCount;

    public ThrowableProxy(Throwable throwable) {
        this.throwable = throwable;
        this.name = throwable.getClass().getName();
        HashMap<String, CacheEntry> map = new HashMap<String, CacheEntry>();
        Stack<Class<?>> stack = this.getCurrentStack();
        this.callerPackageData = this.resolvePackageData(stack, map, null, throwable.getStackTrace());
        this.proxyCause = throwable.getCause() == null ? null : new ThrowableProxy(throwable, stack, map, throwable.getCause());
        this.setSuppressed(throwable);
    }

    private ThrowableProxy(Throwable parent, Stack<Class<?>> stack, Map<String, CacheEntry> map, Throwable cause) {
        this.throwable = cause;
        this.name = cause.getClass().getName();
        this.callerPackageData = this.resolvePackageData(stack, map, parent.getStackTrace(), cause.getStackTrace());
        this.proxyCause = cause.getCause() == null ? null : new ThrowableProxy(parent, stack, map, cause.getCause());
        this.setSuppressed(cause);
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    public ThrowableProxy getCause() {
        return this.proxyCause;
    }

    public String getName() {
        return this.name;
    }

    public int getCommonElementCount() {
        return this.commonElementCount;
    }

    public StackTracePackageElement[] getPackageData() {
        return this.callerPackageData;
    }

    public String toString() {
        String msg = this.throwable.getMessage();
        return msg != null ? this.name + ": " + msg : this.name;
    }

    public String getRootCauseStackTrace() {
        return this.getRootCauseStackTrace(null);
    }

    public String getRootCauseStackTrace(List<String> packages) {
        StringBuilder sb2 = new StringBuilder();
        if (this.proxyCause != null) {
            this.formatWrapper(sb2, this.proxyCause);
            sb2.append("Wrapped by: ");
        }
        sb2.append(this.toString());
        sb2.append("\n");
        this.formatElements(sb2, 0, this.throwable.getStackTrace(), this.callerPackageData, packages);
        return sb2.toString();
    }

    public void formatWrapper(StringBuilder sb2, ThrowableProxy cause) {
        this.formatWrapper(sb2, cause, null);
    }

    public void formatWrapper(StringBuilder sb2, ThrowableProxy cause, List<String> packages) {
        Throwable caused;
        Throwable throwable = caused = cause.getCause() != null ? cause.getCause().getThrowable() : null;
        if (caused != null) {
            this.formatWrapper(sb2, cause.proxyCause);
            sb2.append("Wrapped by: ");
        }
        sb2.append(cause).append("\n");
        this.formatElements(sb2, cause.commonElementCount, cause.getThrowable().getStackTrace(), cause.callerPackageData, packages);
    }

    public String getExtendedStackTrace() {
        return this.getExtendedStackTrace(null);
    }

    public String getExtendedStackTrace(List<String> packages) {
        StringBuilder sb2 = new StringBuilder(this.name);
        String msg = this.throwable.getMessage();
        if (msg != null) {
            sb2.append(": ").append(this.throwable.getMessage());
        }
        sb2.append("\n");
        this.formatElements(sb2, 0, this.throwable.getStackTrace(), this.callerPackageData, packages);
        if (this.proxyCause != null) {
            this.formatCause(sb2, this.proxyCause, packages);
        }
        return sb2.toString();
    }

    public String getSuppressedStackTrace() {
        ThrowableProxy[] suppressed = this.getSuppressed();
        if (suppressed == null || suppressed.length == 0) {
            return "";
        }
        StringBuilder sb2 = new StringBuilder("Suppressed Stack Trace Elements:\n");
        for (ThrowableProxy proxy : suppressed) {
            sb2.append(proxy.getExtendedStackTrace());
        }
        return sb2.toString();
    }

    private void formatCause(StringBuilder sb2, ThrowableProxy cause, List<String> packages) {
        sb2.append("Caused by: ").append(cause).append("\n");
        this.formatElements(sb2, cause.commonElementCount, cause.getThrowable().getStackTrace(), cause.callerPackageData, packages);
        if (cause.getCause() != null) {
            this.formatCause(sb2, cause.proxyCause, packages);
        }
    }

    private void formatElements(StringBuilder sb2, int commonCount, StackTraceElement[] causedTrace, StackTracePackageElement[] packageData, List<String> packages) {
        if (packages == null || packages.size() == 0) {
            for (int i2 = 0; i2 < packageData.length; ++i2) {
                this.formatEntry(causedTrace[i2], packageData[i2], sb2);
            }
        } else {
            int count = 0;
            for (int i3 = 0; i3 < packageData.length; ++i3) {
                if (!this.isSuppressed(causedTrace[i3], packages)) {
                    if (count > 0) {
                        if (count == 1) {
                            sb2.append("\t....\n");
                        } else {
                            sb2.append("\t... suppressed ").append(count).append(" lines\n");
                        }
                        count = 0;
                    }
                    this.formatEntry(causedTrace[i3], packageData[i3], sb2);
                    continue;
                }
                ++count;
            }
            if (count > 0) {
                if (count == 1) {
                    sb2.append("\t...\n");
                } else {
                    sb2.append("\t... suppressed ").append(count).append(" lines\n");
                }
            }
        }
        if (commonCount != 0) {
            sb2.append("\t... ").append(commonCount).append(" more").append("\n");
        }
    }

    private void formatEntry(StackTraceElement element, StackTracePackageElement packageData, StringBuilder sb2) {
        sb2.append("\tat ");
        sb2.append(element);
        sb2.append(" ");
        sb2.append(packageData);
        sb2.append("\n");
    }

    private boolean isSuppressed(StackTraceElement element, List<String> packages) {
        String className = element.getClassName();
        for (String pkg : packages) {
            if (!className.startsWith(pkg)) continue;
            return true;
        }
        return false;
    }

    private Stack<Class<?>> getCurrentStack() {
        if (ReflectiveCallerClassUtility.isSupported()) {
            Stack classes = new Stack();
            int index = 1;
            Class<?> clazz = ReflectiveCallerClassUtility.getCaller(index);
            while (clazz != null) {
                classes.push(clazz);
                clazz = ReflectiveCallerClassUtility.getCaller(++index);
            }
            return classes;
        }
        if (SECURITY_MANAGER != null) {
            Class<?>[] array = SECURITY_MANAGER.getClasses();
            Stack classes = new Stack();
            for (Class<?> clazz : array) {
                classes.push(clazz);
            }
            return classes;
        }
        return new Stack();
    }

    StackTracePackageElement[] resolvePackageData(Stack<Class<?>> stack, Map<String, CacheEntry> map, StackTraceElement[] rootTrace, StackTraceElement[] stackTrace) {
        int stackLength;
        if (rootTrace != null) {
            int stackIndex;
            int rootIndex = rootTrace.length - 1;
            for (stackIndex = stackTrace.length - 1; rootIndex >= 0 && stackIndex >= 0 && rootTrace[rootIndex].equals(stackTrace[stackIndex]); --rootIndex, --stackIndex) {
            }
            this.commonElementCount = stackTrace.length - 1 - stackIndex;
            stackLength = stackIndex + 1;
        } else {
            this.commonElementCount = 0;
            stackLength = stackTrace.length;
        }
        StackTracePackageElement[] packageArray = new StackTracePackageElement[stackLength];
        Class<?> clazz = stack.isEmpty() ? null : stack.peek();
        ClassLoader lastLoader = null;
        for (int i2 = stackLength - 1; i2 >= 0; --i2) {
            CacheEntry entry;
            String className = stackTrace[i2].getClassName();
            if (clazz != null && className.equals(clazz.getName())) {
                entry = this.resolvePackageElement(clazz, true);
                packageArray[i2] = entry.element;
                lastLoader = entry.loader;
                stack.pop();
                clazz = stack.isEmpty() ? null : stack.peek();
                continue;
            }
            if (map.containsKey(className)) {
                entry = map.get(className);
                packageArray[i2] = entry.element;
                if (entry.loader == null) continue;
                lastLoader = entry.loader;
                continue;
            }
            entry = this.resolvePackageElement(this.loadClass(lastLoader, className), false);
            packageArray[i2] = entry.element;
            map.put(className, entry);
            if (entry.loader == null) continue;
            lastLoader = entry.loader;
        }
        return packageArray;
    }

    private CacheEntry resolvePackageElement(Class<?> callerClass, boolean exact) {
        String location = "?";
        String version = "?";
        ClassLoader lastLoader = null;
        if (callerClass != null) {
            String ver;
            try {
                URL locationURL;
                CodeSource source = callerClass.getProtectionDomain().getCodeSource();
                if (source != null && (locationURL = source.getLocation()) != null) {
                    String str = locationURL.toString().replace('\\', '/');
                    int index = str.lastIndexOf("/");
                    if (index >= 0 && index == str.length() - 1) {
                        index = str.lastIndexOf("/", index - 1);
                        location = str.substring(index + 1);
                    } else {
                        location = str.substring(index + 1);
                    }
                }
            }
            catch (Exception ex2) {
                // empty catch block
            }
            Package pkg = callerClass.getPackage();
            if (pkg != null && (ver = pkg.getImplementationVersion()) != null) {
                version = ver;
            }
            lastLoader = callerClass.getClassLoader();
        }
        return new CacheEntry(new StackTracePackageElement(location, version, exact), lastLoader);
    }

    private Class<?> loadClass(ClassLoader lastLoader, String className) {
        Class<?> clazz;
        if (lastLoader != null) {
            try {
                clazz = lastLoader.loadClass(className);
                if (clazz != null) {
                    return clazz;
                }
            }
            catch (Exception ex2) {
                // empty catch block
            }
        }
        try {
            clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
        }
        catch (ClassNotFoundException e2) {
            try {
                clazz = Class.forName(className);
            }
            catch (ClassNotFoundException e1) {
                try {
                    clazz = this.getClass().getClassLoader().loadClass(className);
                }
                catch (ClassNotFoundException e22) {
                    return null;
                }
            }
        }
        return clazz;
    }

    public ThrowableProxy[] getSuppressed() {
        if (GET_SUPPRESSED != null) {
            try {
                return (ThrowableProxy[])GET_SUPPRESSED.invoke(this.throwable, new Object[0]);
            }
            catch (Exception ignore) {
                return null;
            }
        }
        return null;
    }

    private void setSuppressed(Throwable throwable) {
        if (GET_SUPPRESSED != null && ADD_SUPPRESSED != null) {
            try {
                Throwable[] array;
                for (Throwable t2 : array = (Throwable[])GET_SUPPRESSED.invoke(throwable, new Object[0])) {
                    ADD_SUPPRESSED.invoke(this, new ThrowableProxy(t2));
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    static {
        Method[] methods;
        LOGGER = StatusLogger.getLogger();
        if (ReflectiveCallerClassUtility.isSupported()) {
            SECURITY_MANAGER = null;
        } else {
            PrivateSecurityManager securityManager;
            try {
                securityManager = new PrivateSecurityManager();
                if (securityManager.getClasses() == null) {
                    securityManager = null;
                    LOGGER.error("Unable to obtain call stack from security manager.");
                }
            }
            catch (Exception e2) {
                securityManager = null;
                LOGGER.debug("Unable to install security manager.", (Throwable)e2);
            }
            SECURITY_MANAGER = securityManager;
        }
        Method getSuppressed = null;
        Method addSuppressed = null;
        for (Method method : methods = Throwable.class.getMethods()) {
            if (method.getName().equals("getSuppressed")) {
                getSuppressed = method;
                continue;
            }
            if (!method.getName().equals("addSuppressed")) continue;
            addSuppressed = method;
        }
        GET_SUPPRESSED = getSuppressed;
        ADD_SUPPRESSED = addSuppressed;
    }

    private static class PrivateSecurityManager
    extends SecurityManager {
        private PrivateSecurityManager() {
        }

        public Class<?>[] getClasses() {
            return this.getClassContext();
        }
    }

    class CacheEntry {
        private final StackTracePackageElement element;
        private final ClassLoader loader;

        public CacheEntry(StackTracePackageElement element, ClassLoader loader) {
            this.element = element;
            this.loader = loader;
        }
    }
}

