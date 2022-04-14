/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.Type;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Method {
    private final String name;
    private final String descriptor;
    private static final Map<String, String> PRIMITIVE_TYPE_DESCRIPTORS;

    public Method(String name, String descriptor) {
        this.name = name;
        this.descriptor = descriptor;
    }

    public Method(String name, Type returnType, Type[] argumentTypes) {
        this(name, Type.getMethodDescriptor(returnType, argumentTypes));
    }

    public static Method getMethod(java.lang.reflect.Method method) {
        return new Method(method.getName(), Type.getMethodDescriptor(method));
    }

    public static Method getMethod(Constructor<?> constructor) {
        return new Method("<init>", Type.getConstructorDescriptor(constructor));
    }

    public static Method getMethod(String method) {
        return Method.getMethod(method, false);
    }

    public static Method getMethod(String method, boolean defaultPackage) {
        int currentArgumentEndIndex;
        int spaceIndex = method.indexOf(32);
        int currentArgumentStartIndex = method.indexOf(40, spaceIndex) + 1;
        int endIndex = method.indexOf(41, currentArgumentStartIndex);
        if (spaceIndex == -1 || currentArgumentStartIndex == 0 || endIndex == -1) {
            throw new IllegalArgumentException();
        }
        String returnType = method.substring(0, spaceIndex);
        String methodName = method.substring(spaceIndex + 1, currentArgumentStartIndex - 1).trim();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');
        do {
            String argumentDescriptor;
            if ((currentArgumentEndIndex = method.indexOf(44, currentArgumentStartIndex)) == -1) {
                argumentDescriptor = Method.getDescriptorInternal(method.substring(currentArgumentStartIndex, endIndex).trim(), defaultPackage);
            } else {
                argumentDescriptor = Method.getDescriptorInternal(method.substring(currentArgumentStartIndex, currentArgumentEndIndex).trim(), defaultPackage);
                currentArgumentStartIndex = currentArgumentEndIndex + 1;
            }
            stringBuilder.append(argumentDescriptor);
        } while (currentArgumentEndIndex != -1);
        stringBuilder.append(')').append(Method.getDescriptorInternal(returnType, defaultPackage));
        return new Method(methodName, stringBuilder.toString());
    }

    private static String getDescriptorInternal(String type, boolean defaultPackage) {
        if ("".equals(type)) {
            return type;
        }
        StringBuilder stringBuilder = new StringBuilder();
        int arrayBracketsIndex = 0;
        while ((arrayBracketsIndex = type.indexOf("[]", arrayBracketsIndex) + 1) > 0) {
            stringBuilder.append('[');
        }
        String elementType = type.substring(0, type.length() - stringBuilder.length() * 2);
        String descriptor = PRIMITIVE_TYPE_DESCRIPTORS.get(elementType);
        if (descriptor != null) {
            stringBuilder.append(descriptor);
        } else {
            stringBuilder.append('L');
            if (elementType.indexOf(46) < 0) {
                if (!defaultPackage) {
                    stringBuilder.append("java/lang/");
                }
                stringBuilder.append(elementType);
            } else {
                stringBuilder.append(elementType.replace('.', '/'));
            }
            stringBuilder.append(';');
        }
        return stringBuilder.toString();
    }

    public String getName() {
        return this.name;
    }

    public String getDescriptor() {
        return this.descriptor;
    }

    public Type getReturnType() {
        return Type.getReturnType(this.descriptor);
    }

    public Type[] getArgumentTypes() {
        return Type.getArgumentTypes(this.descriptor);
    }

    public String toString() {
        return this.name + this.descriptor;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Method)) {
            return false;
        }
        Method otherMethod = (Method)other;
        return this.name.equals(otherMethod.name) && this.descriptor.equals(otherMethod.descriptor);
    }

    public int hashCode() {
        return this.name.hashCode() ^ this.descriptor.hashCode();
    }

    static {
        HashMap<String, String> descriptors = new HashMap<String, String>();
        descriptors.put("void", "V");
        descriptors.put("byte", "B");
        descriptors.put("char", "C");
        descriptors.put("double", "D");
        descriptors.put("float", "F");
        descriptors.put("int", "I");
        descriptors.put("long", "J");
        descriptors.put("short", "S");
        descriptors.put("boolean", "Z");
        PRIMITIVE_TYPE_DESCRIPTORS = descriptors;
    }
}

