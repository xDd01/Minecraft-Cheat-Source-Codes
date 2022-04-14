package org.apache.commons.lang3.reflect;

import java.lang.reflect.*;
import org.apache.commons.lang3.*;

abstract class MemberUtils
{
    private static final int ACCESS_TEST = 7;
    private static final Class<?>[] ORDERED_PRIMITIVE_TYPES;
    
    static boolean setAccessibleWorkaround(final AccessibleObject o) {
        if (o == null || o.isAccessible()) {
            return false;
        }
        final Member m = (Member)o;
        if (!o.isAccessible() && Modifier.isPublic(m.getModifiers()) && isPackageAccess(m.getDeclaringClass().getModifiers())) {
            try {
                o.setAccessible(true);
                return true;
            }
            catch (SecurityException ex) {}
        }
        return false;
    }
    
    static boolean isPackageAccess(final int modifiers) {
        return (modifiers & 0x7) == 0x0;
    }
    
    static boolean isAccessible(final Member m) {
        return m != null && Modifier.isPublic(m.getModifiers()) && !m.isSynthetic();
    }
    
    static int compareConstructorFit(final Constructor<?> left, final Constructor<?> right, final Class<?>[] actual) {
        return compareParameterTypes(of(left), of(right), actual);
    }
    
    static int compareMethodFit(final Method left, final Method right, final Class<?>[] actual) {
        return compareParameterTypes(of(left), of(right), actual);
    }
    
    private static int compareParameterTypes(final Executable left, final Executable right, final Class<?>[] actual) {
        final float leftCost = getTotalTransformationCost(actual, left);
        final float rightCost = getTotalTransformationCost(actual, right);
        return (leftCost < rightCost) ? -1 : ((rightCost < leftCost) ? 1 : 0);
    }
    
    private static float getTotalTransformationCost(final Class<?>[] srcArgs, final Executable executable) {
        final Class<?>[] destArgs = executable.getParameterTypes();
        final boolean isVarArgs = executable.isVarArgs();
        float totalCost = 0.0f;
        final long normalArgsLen = isVarArgs ? (destArgs.length - 1) : ((long)destArgs.length);
        if (srcArgs.length < normalArgsLen) {
            return Float.MAX_VALUE;
        }
        for (int i = 0; i < normalArgsLen; ++i) {
            totalCost += getObjectTransformationCost(srcArgs[i], destArgs[i]);
        }
        if (isVarArgs) {
            final boolean noVarArgsPassed = srcArgs.length < destArgs.length;
            final boolean explicitArrayForVarags = srcArgs.length == destArgs.length && srcArgs[srcArgs.length - 1].isArray();
            final float varArgsCost = 0.001f;
            final Class<?> destClass = destArgs[destArgs.length - 1].getComponentType();
            if (noVarArgsPassed) {
                totalCost += getObjectTransformationCost(destClass, Object.class) + 0.001f;
            }
            else if (explicitArrayForVarags) {
                final Class<?> sourceClass = srcArgs[srcArgs.length - 1].getComponentType();
                totalCost += getObjectTransformationCost(sourceClass, destClass) + 0.001f;
            }
            else {
                for (int j = destArgs.length - 1; j < srcArgs.length; ++j) {
                    final Class<?> srcClass = srcArgs[j];
                    totalCost += getObjectTransformationCost(srcClass, destClass) + 0.001f;
                }
            }
        }
        return totalCost;
    }
    
    private static float getObjectTransformationCost(Class<?> srcClass, final Class<?> destClass) {
        if (destClass.isPrimitive()) {
            return getPrimitivePromotionCost(srcClass, destClass);
        }
        float cost = 0.0f;
        while (srcClass != null && !destClass.equals(srcClass)) {
            if (destClass.isInterface() && ClassUtils.isAssignable(srcClass, destClass)) {
                cost += 0.25f;
                break;
            }
            ++cost;
            srcClass = srcClass.getSuperclass();
        }
        if (srcClass == null) {
            cost += 1.5f;
        }
        return cost;
    }
    
    private static float getPrimitivePromotionCost(final Class<?> srcClass, final Class<?> destClass) {
        float cost = 0.0f;
        Class<?> cls = srcClass;
        if (!cls.isPrimitive()) {
            cost += 0.1f;
            cls = ClassUtils.wrapperToPrimitive(cls);
        }
        for (int i = 0; cls != destClass && i < MemberUtils.ORDERED_PRIMITIVE_TYPES.length; ++i) {
            if (cls == MemberUtils.ORDERED_PRIMITIVE_TYPES[i]) {
                cost += 0.1f;
                if (i < MemberUtils.ORDERED_PRIMITIVE_TYPES.length - 1) {
                    cls = MemberUtils.ORDERED_PRIMITIVE_TYPES[i + 1];
                }
            }
        }
        return cost;
    }
    
    static boolean isMatchingMethod(final Method method, final Class<?>[] parameterTypes) {
        return isMatchingExecutable(of(method), parameterTypes);
    }
    
    static boolean isMatchingConstructor(final Constructor<?> method, final Class<?>[] parameterTypes) {
        return isMatchingExecutable(of(method), parameterTypes);
    }
    
    private static boolean isMatchingExecutable(final Executable method, final Class<?>[] parameterTypes) {
        final Class<?>[] methodParameterTypes = method.getParameterTypes();
        if (ClassUtils.isAssignable(parameterTypes, methodParameterTypes, true)) {
            return true;
        }
        if (method.isVarArgs()) {
            int i;
            for (i = 0; i < methodParameterTypes.length - 1 && i < parameterTypes.length; ++i) {
                if (!ClassUtils.isAssignable(parameterTypes[i], methodParameterTypes[i], true)) {
                    return false;
                }
            }
            final Class<?> varArgParameterType = methodParameterTypes[methodParameterTypes.length - 1].getComponentType();
            while (i < parameterTypes.length) {
                if (!ClassUtils.isAssignable(parameterTypes[i], varArgParameterType, true)) {
                    return false;
                }
                ++i;
            }
            return true;
        }
        return false;
    }
    
    static {
        ORDERED_PRIMITIVE_TYPES = new Class[] { Byte.TYPE, Short.TYPE, Character.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE };
    }
    
    private static final class Executable
    {
        private final Class<?>[] parameterTypes;
        private final boolean isVarArgs;
        
        private static Executable of(final Method method) {
            return new Executable(method);
        }
        
        private static Executable of(final Constructor<?> constructor) {
            return new Executable(constructor);
        }
        
        private Executable(final Method method) {
            this.parameterTypes = method.getParameterTypes();
            this.isVarArgs = method.isVarArgs();
        }
        
        private Executable(final Constructor<?> constructor) {
            this.parameterTypes = constructor.getParameterTypes();
            this.isVarArgs = constructor.isVarArgs();
        }
        
        public Class<?>[] getParameterTypes() {
            return this.parameterTypes;
        }
        
        public boolean isVarArgs() {
            return this.isVarArgs;
        }
    }
}
