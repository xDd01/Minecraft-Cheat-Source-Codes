/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree.analysis;

import java.util.List;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.BasicVerifier;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SimpleVerifier
extends BasicVerifier {
    private final Type currentClass;
    private final Type currentSuperClass;
    private final List<Type> currentClassInterfaces;
    private final boolean isInterface;
    private ClassLoader loader = this.getClass().getClassLoader();

    public SimpleVerifier() {
        this(null, null, false);
    }

    public SimpleVerifier(Type currentClass, Type currentSuperClass, boolean isInterface) {
        this(currentClass, currentSuperClass, null, isInterface);
    }

    public SimpleVerifier(Type currentClass, Type currentSuperClass, List<Type> currentClassInterfaces, boolean isInterface) {
        this(589824, currentClass, currentSuperClass, currentClassInterfaces, isInterface);
        if (this.getClass() != SimpleVerifier.class) {
            throw new IllegalStateException();
        }
    }

    protected SimpleVerifier(int api2, Type currentClass, Type currentSuperClass, List<Type> currentClassInterfaces, boolean isInterface) {
        super(api2);
        this.currentClass = currentClass;
        this.currentSuperClass = currentSuperClass;
        this.currentClassInterfaces = currentClassInterfaces;
        this.isInterface = isInterface;
    }

    public void setClassLoader(ClassLoader loader) {
        this.loader = loader;
    }

    @Override
    public BasicValue newValue(Type type) {
        BasicValue value;
        boolean isArray;
        if (type == null) {
            return BasicValue.UNINITIALIZED_VALUE;
        }
        boolean bl2 = isArray = type.getSort() == 9;
        if (isArray) {
            switch (type.getElementType().getSort()) {
                case 1: 
                case 2: 
                case 3: 
                case 4: {
                    return new BasicValue(type);
                }
            }
        }
        if (BasicValue.REFERENCE_VALUE.equals(value = super.newValue(type))) {
            if (isArray) {
                value = this.newValue(type.getElementType());
                StringBuilder descriptor = new StringBuilder();
                for (int i2 = 0; i2 < type.getDimensions(); ++i2) {
                    descriptor.append('[');
                }
                descriptor.append(value.getType().getDescriptor());
                value = new BasicValue(Type.getType(descriptor.toString()));
            } else {
                value = new BasicValue(type);
            }
        }
        return value;
    }

    @Override
    protected boolean isArrayValue(BasicValue value) {
        Type type = value.getType();
        return type != null && (type.getSort() == 9 || type.equals(NULL_TYPE));
    }

    @Override
    protected BasicValue getElementValue(BasicValue objectArrayValue) throws AnalyzerException {
        Type arrayType = objectArrayValue.getType();
        if (arrayType != null) {
            if (arrayType.getSort() == 9) {
                return this.newValue(Type.getType(arrayType.getDescriptor().substring(1)));
            }
            if (arrayType.equals(NULL_TYPE)) {
                return objectArrayValue;
            }
        }
        throw new AssertionError();
    }

    @Override
    protected boolean isSubTypeOf(BasicValue value, BasicValue expected) {
        Type expectedType = expected.getType();
        Type type = value.getType();
        switch (expectedType.getSort()) {
            case 5: 
            case 6: 
            case 7: 
            case 8: {
                return type.equals(expectedType);
            }
            case 9: 
            case 10: {
                if (type.equals(NULL_TYPE)) {
                    return true;
                }
                if (type.getSort() == 10 || type.getSort() == 9) {
                    if (this.isAssignableFrom(expectedType, type)) {
                        return true;
                    }
                    if (this.getClass(expectedType).isInterface()) {
                        return Object.class.isAssignableFrom(this.getClass(type));
                    }
                    return false;
                }
                return false;
            }
        }
        throw new AssertionError();
    }

    @Override
    public BasicValue merge(BasicValue value1, BasicValue value2) {
        if (!value1.equals(value2)) {
            Type type1 = value1.getType();
            Type type2 = value2.getType();
            if (!(type1 == null || type1.getSort() != 10 && type1.getSort() != 9 || type2 == null || type2.getSort() != 10 && type2.getSort() != 9)) {
                if (type1.equals(NULL_TYPE)) {
                    return value2;
                }
                if (type2.equals(NULL_TYPE)) {
                    return value1;
                }
                if (this.isAssignableFrom(type1, type2)) {
                    return value1;
                }
                if (this.isAssignableFrom(type2, type1)) {
                    return value2;
                }
                int numDimensions = 0;
                if (type1.getSort() == 9 && type2.getSort() == 9 && type1.getDimensions() == type2.getDimensions() && type1.getElementType().getSort() == 10 && type2.getElementType().getSort() == 10) {
                    numDimensions = type1.getDimensions();
                    type1 = type1.getElementType();
                    type2 = type2.getElementType();
                }
                do {
                    if (type1 != null && !this.isInterface(type1)) continue;
                    return this.newArrayValue(Type.getObjectType("java/lang/Object"), numDimensions);
                } while (!this.isAssignableFrom(type1 = this.getSuperClass(type1), type2));
                return this.newArrayValue(type1, numDimensions);
            }
            return BasicValue.UNINITIALIZED_VALUE;
        }
        return value1;
    }

    private BasicValue newArrayValue(Type type, int dimensions) {
        if (dimensions == 0) {
            return this.newValue(type);
        }
        StringBuilder descriptor = new StringBuilder();
        for (int i2 = 0; i2 < dimensions; ++i2) {
            descriptor.append('[');
        }
        descriptor.append(type.getDescriptor());
        return this.newValue(Type.getType(descriptor.toString()));
    }

    protected boolean isInterface(Type type) {
        if (this.currentClass != null && this.currentClass.equals(type)) {
            return this.isInterface;
        }
        return this.getClass(type).isInterface();
    }

    protected Type getSuperClass(Type type) {
        if (this.currentClass != null && this.currentClass.equals(type)) {
            return this.currentSuperClass;
        }
        Class<?> superClass = this.getClass(type).getSuperclass();
        return superClass == null ? null : Type.getType(superClass);
    }

    protected boolean isAssignableFrom(Type type1, Type type2) {
        if (type1.equals(type2)) {
            return true;
        }
        if (this.currentClass != null && this.currentClass.equals(type1)) {
            if (this.getSuperClass(type2) == null) {
                return false;
            }
            if (this.isInterface) {
                return type2.getSort() == 10 || type2.getSort() == 9;
            }
            return this.isAssignableFrom(type1, this.getSuperClass(type2));
        }
        if (this.currentClass != null && this.currentClass.equals(type2)) {
            if (this.isAssignableFrom(type1, this.currentSuperClass)) {
                return true;
            }
            if (this.currentClassInterfaces != null) {
                for (Type currentClassInterface : this.currentClassInterfaces) {
                    if (!this.isAssignableFrom(type1, currentClassInterface)) continue;
                    return true;
                }
            }
            return false;
        }
        return this.getClass(type1).isAssignableFrom(this.getClass(type2));
    }

    protected Class<?> getClass(Type type) {
        try {
            if (type.getSort() == 9) {
                return Class.forName(type.getDescriptor().replace('/', '.'), false, this.loader);
            }
            return Class.forName(type.getClassName(), false, this.loader);
        }
        catch (ClassNotFoundException e2) {
            throw new TypeNotPresentException(e2.toString(), e2);
        }
    }
}

