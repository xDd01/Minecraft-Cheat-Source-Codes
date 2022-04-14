package org.apache.commons.lang3.reflect;

import org.apache.commons.lang3.*;
import java.lang.reflect.*;

public class ConstructorUtils
{
    public static <T> T invokeConstructor(final Class<T> cls, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        args = ArrayUtils.nullToEmpty(args);
        final Class<?>[] parameterTypes = ClassUtils.toClass(args);
        return invokeConstructor(cls, args, parameterTypes);
    }
    
    public static <T> T invokeConstructor(final Class<T> cls, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        args = ArrayUtils.nullToEmpty(args);
        parameterTypes = ArrayUtils.nullToEmpty(parameterTypes);
        final Constructor<T> ctor = getMatchingAccessibleConstructor(cls, parameterTypes);
        if (ctor == null) {
            throw new NoSuchMethodException("No such accessible constructor on object: " + cls.getName());
        }
        if (ctor.isVarArgs()) {
            final Class<?>[] methodParameterTypes = ctor.getParameterTypes();
            args = MethodUtils.getVarArgs(args, methodParameterTypes);
        }
        return ctor.newInstance(args);
    }
    
    public static <T> T invokeExactConstructor(final Class<T> cls, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        args = ArrayUtils.nullToEmpty(args);
        final Class<?>[] parameterTypes = ClassUtils.toClass(args);
        return invokeExactConstructor(cls, args, parameterTypes);
    }
    
    public static <T> T invokeExactConstructor(final Class<T> cls, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        args = ArrayUtils.nullToEmpty(args);
        parameterTypes = ArrayUtils.nullToEmpty(parameterTypes);
        final Constructor<T> ctor = getAccessibleConstructor(cls, parameterTypes);
        if (ctor == null) {
            throw new NoSuchMethodException("No such accessible constructor on object: " + cls.getName());
        }
        return ctor.newInstance(args);
    }
    
    public static <T> Constructor<T> getAccessibleConstructor(final Class<T> cls, final Class<?>... parameterTypes) {
        Validate.notNull(cls, "class cannot be null", new Object[0]);
        try {
            return getAccessibleConstructor(cls.getConstructor(parameterTypes));
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }
    
    public static <T> Constructor<T> getAccessibleConstructor(final Constructor<T> ctor) {
        Validate.notNull(ctor, "constructor cannot be null", new Object[0]);
        return (MemberUtils.isAccessible(ctor) && isAccessible(ctor.getDeclaringClass())) ? ctor : null;
    }
    
    public static <T> Constructor<T> getMatchingAccessibleConstructor(final Class<T> cls, final Class<?>... parameterTypes) {
        Validate.notNull(cls, "class cannot be null", new Object[0]);
        try {
            final Constructor<T> ctor = cls.getConstructor(parameterTypes);
            MemberUtils.setAccessibleWorkaround(ctor);
            return ctor;
        }
        catch (NoSuchMethodException ex) {
            Constructor<T> result = null;
            final Constructor<?>[] constructors;
            final Constructor<?>[] ctors = constructors = cls.getConstructors();
            for (Constructor<?> ctor2 : constructors) {
                if (MemberUtils.isMatchingConstructor(ctor2, parameterTypes)) {
                    ctor2 = getAccessibleConstructor(ctor2);
                    if (ctor2 != null) {
                        MemberUtils.setAccessibleWorkaround(ctor2);
                        if (result == null || MemberUtils.compareConstructorFit(ctor2, result, parameterTypes) < 0) {
                            final Constructor<T> constructor = result = (Constructor<T>)ctor2;
                        }
                    }
                }
            }
            return result;
        }
    }
    
    private static boolean isAccessible(final Class<?> type) {
        for (Class<?> cls = type; cls != null; cls = cls.getEnclosingClass()) {
            if (!Modifier.isPublic(cls.getModifiers())) {
                return false;
            }
        }
        return true;
    }
}
