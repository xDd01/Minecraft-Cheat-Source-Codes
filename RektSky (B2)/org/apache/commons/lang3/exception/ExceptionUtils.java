package org.apache.commons.lang3.exception;

import java.io.*;
import java.util.*;
import org.apache.commons.lang3.*;
import java.lang.reflect.*;

public class ExceptionUtils
{
    static final String WRAPPED_MARKER = " [wrapped] ";
    private static final String[] CAUSE_METHOD_NAMES;
    
    @Deprecated
    public static String[] getDefaultCauseMethodNames() {
        return ArrayUtils.clone(ExceptionUtils.CAUSE_METHOD_NAMES);
    }
    
    @Deprecated
    public static Throwable getCause(final Throwable throwable) {
        return getCause(throwable, null);
    }
    
    @Deprecated
    public static Throwable getCause(final Throwable throwable, String[] methodNames) {
        if (throwable == null) {
            return null;
        }
        if (methodNames == null) {
            final Throwable cause = throwable.getCause();
            if (cause != null) {
                return cause;
            }
            methodNames = ExceptionUtils.CAUSE_METHOD_NAMES;
        }
        for (final String methodName : methodNames) {
            if (methodName != null) {
                final Throwable legacyCause = getCauseUsingMethodName(throwable, methodName);
                if (legacyCause != null) {
                    return legacyCause;
                }
            }
        }
        return null;
    }
    
    public static Throwable getRootCause(final Throwable throwable) {
        final List<Throwable> list = getThrowableList(throwable);
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }
    
    private static Throwable getCauseUsingMethodName(final Throwable throwable, final String methodName) {
        Method method = null;
        try {
            method = throwable.getClass().getMethod(methodName, (Class<?>[])new Class[0]);
        }
        catch (NoSuchMethodException ex) {}
        catch (SecurityException ex2) {}
        if (method != null && Throwable.class.isAssignableFrom(method.getReturnType())) {
            try {
                return (Throwable)method.invoke(throwable, new Object[0]);
            }
            catch (IllegalAccessException ex3) {}
            catch (IllegalArgumentException ex4) {}
            catch (InvocationTargetException ex5) {}
        }
        return null;
    }
    
    public static int getThrowableCount(final Throwable throwable) {
        return getThrowableList(throwable).size();
    }
    
    public static Throwable[] getThrowables(final Throwable throwable) {
        final List<Throwable> list = getThrowableList(throwable);
        return list.toArray(new Throwable[list.size()]);
    }
    
    public static List<Throwable> getThrowableList(Throwable throwable) {
        List<Throwable> list;
        for (list = new ArrayList<Throwable>(); throwable != null && !list.contains(throwable); throwable = throwable.getCause()) {
            list.add(throwable);
        }
        return list;
    }
    
    public static int indexOfThrowable(final Throwable throwable, final Class<?> clazz) {
        return indexOf(throwable, clazz, 0, false);
    }
    
    public static int indexOfThrowable(final Throwable throwable, final Class<?> clazz, final int fromIndex) {
        return indexOf(throwable, clazz, fromIndex, false);
    }
    
    public static int indexOfType(final Throwable throwable, final Class<?> type) {
        return indexOf(throwable, type, 0, true);
    }
    
    public static int indexOfType(final Throwable throwable, final Class<?> type, final int fromIndex) {
        return indexOf(throwable, type, fromIndex, true);
    }
    
    private static int indexOf(final Throwable throwable, final Class<?> type, int fromIndex, final boolean subclass) {
        if (throwable == null || type == null) {
            return -1;
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        final Throwable[] throwables = getThrowables(throwable);
        if (fromIndex >= throwables.length) {
            return -1;
        }
        if (subclass) {
            for (int i = fromIndex; i < throwables.length; ++i) {
                if (type.isAssignableFrom(throwables[i].getClass())) {
                    return i;
                }
            }
        }
        else {
            for (int i = fromIndex; i < throwables.length; ++i) {
                if (type.equals(throwables[i].getClass())) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public static void printRootCauseStackTrace(final Throwable throwable) {
        printRootCauseStackTrace(throwable, System.err);
    }
    
    public static void printRootCauseStackTrace(final Throwable throwable, final PrintStream stream) {
        if (throwable == null) {
            return;
        }
        Validate.isTrue(stream != null, "The PrintStream must not be null", new Object[0]);
        final String[] rootCauseStackTrace;
        final String[] trace = rootCauseStackTrace = getRootCauseStackTrace(throwable);
        for (final String element : rootCauseStackTrace) {
            stream.println(element);
        }
        stream.flush();
    }
    
    public static void printRootCauseStackTrace(final Throwable throwable, final PrintWriter writer) {
        if (throwable == null) {
            return;
        }
        Validate.isTrue(writer != null, "The PrintWriter must not be null", new Object[0]);
        final String[] rootCauseStackTrace;
        final String[] trace = rootCauseStackTrace = getRootCauseStackTrace(throwable);
        for (final String element : rootCauseStackTrace) {
            writer.println(element);
        }
        writer.flush();
    }
    
    public static String[] getRootCauseStackTrace(final Throwable throwable) {
        if (throwable == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        final Throwable[] throwables = getThrowables(throwable);
        final int count = throwables.length;
        final List<String> frames = new ArrayList<String>();
        List<String> nextTrace = getStackFrameList(throwables[count - 1]);
        int i = count;
        while (--i >= 0) {
            final List<String> trace = nextTrace;
            if (i != 0) {
                nextTrace = getStackFrameList(throwables[i - 1]);
                removeCommonFrames(trace, nextTrace);
            }
            if (i == count - 1) {
                frames.add(throwables[i].toString());
            }
            else {
                frames.add(" [wrapped] " + throwables[i].toString());
            }
            frames.addAll(trace);
        }
        return frames.toArray(new String[frames.size()]);
    }
    
    public static void removeCommonFrames(final List<String> causeFrames, final List<String> wrapperFrames) {
        if (causeFrames == null || wrapperFrames == null) {
            throw new IllegalArgumentException("The List must not be null");
        }
        for (int causeFrameIndex = causeFrames.size() - 1, wrapperFrameIndex = wrapperFrames.size() - 1; causeFrameIndex >= 0 && wrapperFrameIndex >= 0; --causeFrameIndex, --wrapperFrameIndex) {
            final String causeFrame = causeFrames.get(causeFrameIndex);
            final String wrapperFrame = wrapperFrames.get(wrapperFrameIndex);
            if (causeFrame.equals(wrapperFrame)) {
                causeFrames.remove(causeFrameIndex);
            }
        }
    }
    
    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
    
    public static String[] getStackFrames(final Throwable throwable) {
        if (throwable == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return getStackFrames(getStackTrace(throwable));
    }
    
    static String[] getStackFrames(final String stackTrace) {
        final String linebreak = System.lineSeparator();
        final StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
        final List<String> list = new ArrayList<String>();
        while (frames.hasMoreTokens()) {
            list.add(frames.nextToken());
        }
        return list.toArray(new String[list.size()]);
    }
    
    static List<String> getStackFrameList(final Throwable t) {
        final String stackTrace = getStackTrace(t);
        final String linebreak = System.lineSeparator();
        final StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
        final List<String> list = new ArrayList<String>();
        boolean traceStarted = false;
        while (frames.hasMoreTokens()) {
            final String token = frames.nextToken();
            final int at = token.indexOf("at");
            if (at != -1 && token.substring(0, at).trim().isEmpty()) {
                traceStarted = true;
                list.add(token);
            }
            else {
                if (traceStarted) {
                    break;
                }
                continue;
            }
        }
        return list;
    }
    
    public static String getMessage(final Throwable th) {
        if (th == null) {
            return "";
        }
        final String clsName = ClassUtils.getShortClassName(th, null);
        final String msg = th.getMessage();
        return clsName + ": " + StringUtils.defaultString(msg);
    }
    
    public static String getRootCauseMessage(final Throwable th) {
        Throwable root = getRootCause(th);
        root = ((root == null) ? th : root);
        return getMessage(root);
    }
    
    public static <R> R rethrow(final Throwable throwable) {
        return (R)typeErasure(throwable);
    }
    
    private static <R, T extends Throwable> R typeErasure(final Throwable throwable) throws T, Throwable {
        throw throwable;
    }
    
    public static <R> R wrapAndThrow(final Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException)throwable;
        }
        if (throwable instanceof Error) {
            throw (Error)throwable;
        }
        throw new UndeclaredThrowableException(throwable);
    }
    
    public static boolean hasCause(Throwable chain, final Class<? extends Throwable> type) {
        if (chain instanceof UndeclaredThrowableException) {
            chain = chain.getCause();
        }
        return type.isInstance(chain);
    }
    
    static {
        CAUSE_METHOD_NAMES = new String[] { "getCause", "getNextException", "getTargetException", "getException", "getSourceException", "getRootCause", "getCausedByException", "getNested", "getLinkedException", "getNestedException", "getLinkedCause", "getThrowable" };
    }
}
