/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson.internal;

import com.google.gson.internal.$Gson$Preconditions;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class $Gson$Types {
    static final Type[] EMPTY_TYPE_ARRAY = new Type[0];

    private $Gson$Types() {
    }

    public static ParameterizedType newParameterizedTypeWithOwner(Type ownerType, Type rawType, Type ... typeArguments) {
        return new ParameterizedTypeImpl(ownerType, rawType, typeArguments);
    }

    public static GenericArrayType arrayOf(Type componentType) {
        return new GenericArrayTypeImpl(componentType);
    }

    public static WildcardType subtypeOf(Type bound) {
        return new WildcardTypeImpl(new Type[]{bound}, EMPTY_TYPE_ARRAY);
    }

    public static WildcardType supertypeOf(Type bound) {
        return new WildcardTypeImpl(new Type[]{Object.class}, new Type[]{bound});
    }

    public static Type canonicalize(Type type) {
        if (type instanceof Class) {
            Class c2 = (Class)type;
            return c2.isArray() ? new GenericArrayTypeImpl($Gson$Types.canonicalize(c2.getComponentType())) : c2;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType p2 = (ParameterizedType)type;
            return new ParameterizedTypeImpl(p2.getOwnerType(), p2.getRawType(), p2.getActualTypeArguments());
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType g2 = (GenericArrayType)type;
            return new GenericArrayTypeImpl(g2.getGenericComponentType());
        }
        if (type instanceof WildcardType) {
            WildcardType w2 = (WildcardType)type;
            return new WildcardTypeImpl(w2.getUpperBounds(), w2.getLowerBounds());
        }
        return type;
    }

    public static Class<?> getRawType(Type type) {
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            Type rawType = parameterizedType.getRawType();
            $Gson$Preconditions.checkArgument(rawType instanceof Class);
            return (Class)rawType;
        }
        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType)type).getGenericComponentType();
            return Array.newInstance($Gson$Types.getRawType(componentType), 0).getClass();
        }
        if (type instanceof TypeVariable) {
            return Object.class;
        }
        if (type instanceof WildcardType) {
            return $Gson$Types.getRawType(((WildcardType)type).getUpperBounds()[0]);
        }
        String className = type == null ? "null" : type.getClass().getName();
        throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + type + "> is of type " + className);
    }

    static boolean equal(Object a2, Object b2) {
        return a2 == b2 || a2 != null && a2.equals(b2);
    }

    public static boolean equals(Type a2, Type b2) {
        if (a2 == b2) {
            return true;
        }
        if (a2 instanceof Class) {
            return a2.equals(b2);
        }
        if (a2 instanceof ParameterizedType) {
            if (!(b2 instanceof ParameterizedType)) {
                return false;
            }
            ParameterizedType pa2 = (ParameterizedType)a2;
            ParameterizedType pb2 = (ParameterizedType)b2;
            return $Gson$Types.equal(pa2.getOwnerType(), pb2.getOwnerType()) && pa2.getRawType().equals(pb2.getRawType()) && Arrays.equals(pa2.getActualTypeArguments(), pb2.getActualTypeArguments());
        }
        if (a2 instanceof GenericArrayType) {
            if (!(b2 instanceof GenericArrayType)) {
                return false;
            }
            GenericArrayType ga2 = (GenericArrayType)a2;
            GenericArrayType gb2 = (GenericArrayType)b2;
            return $Gson$Types.equals(ga2.getGenericComponentType(), gb2.getGenericComponentType());
        }
        if (a2 instanceof WildcardType) {
            if (!(b2 instanceof WildcardType)) {
                return false;
            }
            WildcardType wa2 = (WildcardType)a2;
            WildcardType wb2 = (WildcardType)b2;
            return Arrays.equals(wa2.getUpperBounds(), wb2.getUpperBounds()) && Arrays.equals(wa2.getLowerBounds(), wb2.getLowerBounds());
        }
        if (a2 instanceof TypeVariable) {
            if (!(b2 instanceof TypeVariable)) {
                return false;
            }
            TypeVariable va2 = (TypeVariable)a2;
            TypeVariable vb2 = (TypeVariable)b2;
            return va2.getGenericDeclaration() == vb2.getGenericDeclaration() && va2.getName().equals(vb2.getName());
        }
        return false;
    }

    private static int hashCodeOrZero(Object o2) {
        return o2 != null ? o2.hashCode() : 0;
    }

    public static String typeToString(Type type) {
        return type instanceof Class ? ((Class)type).getName() : type.toString();
    }

    static Type getGenericSupertype(Type context, Class<?> rawType, Class<?> toResolve) {
        if (toResolve == rawType) {
            return context;
        }
        if (toResolve.isInterface()) {
            Class<?>[] interfaces = rawType.getInterfaces();
            int length = interfaces.length;
            for (int i2 = 0; i2 < length; ++i2) {
                if (interfaces[i2] == toResolve) {
                    return rawType.getGenericInterfaces()[i2];
                }
                if (!toResolve.isAssignableFrom(interfaces[i2])) continue;
                return $Gson$Types.getGenericSupertype(rawType.getGenericInterfaces()[i2], interfaces[i2], toResolve);
            }
        }
        if (!rawType.isInterface()) {
            while (rawType != Object.class) {
                Class<?> rawSupertype = rawType.getSuperclass();
                if (rawSupertype == toResolve) {
                    return rawType.getGenericSuperclass();
                }
                if (toResolve.isAssignableFrom(rawSupertype)) {
                    return $Gson$Types.getGenericSupertype(rawType.getGenericSuperclass(), rawSupertype, toResolve);
                }
                rawType = rawSupertype;
            }
        }
        return toResolve;
    }

    static Type getSupertype(Type context, Class<?> contextRawType, Class<?> supertype) {
        $Gson$Preconditions.checkArgument(supertype.isAssignableFrom(contextRawType));
        return $Gson$Types.resolve(context, contextRawType, $Gson$Types.getGenericSupertype(context, contextRawType, supertype));
    }

    public static Type getArrayComponentType(Type array) {
        return array instanceof GenericArrayType ? ((GenericArrayType)array).getGenericComponentType() : ((Class)array).getComponentType();
    }

    public static Type getCollectionElementType(Type context, Class<?> contextRawType) {
        Type collectionType = $Gson$Types.getSupertype(context, contextRawType, Collection.class);
        if (collectionType instanceof WildcardType) {
            collectionType = ((WildcardType)collectionType).getUpperBounds()[0];
        }
        if (collectionType instanceof ParameterizedType) {
            return ((ParameterizedType)collectionType).getActualTypeArguments()[0];
        }
        return Object.class;
    }

    public static Type[] getMapKeyAndValueTypes(Type context, Class<?> contextRawType) {
        if (context == Properties.class) {
            return new Type[]{String.class, String.class};
        }
        Type mapType = $Gson$Types.getSupertype(context, contextRawType, Map.class);
        if (mapType instanceof ParameterizedType) {
            ParameterizedType mapParameterizedType = (ParameterizedType)mapType;
            return mapParameterizedType.getActualTypeArguments();
        }
        return new Type[]{Object.class, Object.class};
    }

    public static Type resolve(Type context, Class<?> contextRawType, Type toResolve) {
        Type original;
        while (toResolve instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable)toResolve;
            if ((toResolve = $Gson$Types.resolveTypeVariable(context, contextRawType, typeVariable)) != typeVariable) continue;
            return toResolve;
        }
        if (toResolve instanceof Class && ((Class)toResolve).isArray()) {
            Type newComponentType;
            original = (Class)toResolve;
            Class<?> componentType = ((Class)original).getComponentType();
            return componentType == (newComponentType = $Gson$Types.resolve(context, contextRawType, componentType)) ? original : $Gson$Types.arrayOf(newComponentType);
        }
        if (toResolve instanceof GenericArrayType) {
            Type newComponentType;
            original = (GenericArrayType)toResolve;
            Type componentType = original.getGenericComponentType();
            return componentType == (newComponentType = $Gson$Types.resolve(context, contextRawType, componentType)) ? original : $Gson$Types.arrayOf(newComponentType);
        }
        if (toResolve instanceof ParameterizedType) {
            original = (ParameterizedType)toResolve;
            Type ownerType = original.getOwnerType();
            Type newOwnerType = $Gson$Types.resolve(context, contextRawType, ownerType);
            boolean changed = newOwnerType != ownerType;
            Type[] args = original.getActualTypeArguments();
            int length = args.length;
            for (int t2 = 0; t2 < length; ++t2) {
                Type resolvedTypeArgument = $Gson$Types.resolve(context, contextRawType, args[t2]);
                if (resolvedTypeArgument == args[t2]) continue;
                if (!changed) {
                    args = (Type[])args.clone();
                    changed = true;
                }
                args[t2] = resolvedTypeArgument;
            }
            return changed ? $Gson$Types.newParameterizedTypeWithOwner(newOwnerType, original.getRawType(), args) : original;
        }
        if (toResolve instanceof WildcardType) {
            Type upperBound;
            original = (WildcardType)toResolve;
            Type[] originalLowerBound = original.getLowerBounds();
            Type[] originalUpperBound = original.getUpperBounds();
            if (originalLowerBound.length == 1) {
                Type lowerBound = $Gson$Types.resolve(context, contextRawType, originalLowerBound[0]);
                if (lowerBound != originalLowerBound[0]) {
                    return $Gson$Types.supertypeOf(lowerBound);
                }
            } else if (originalUpperBound.length == 1 && (upperBound = $Gson$Types.resolve(context, contextRawType, originalUpperBound[0])) != originalUpperBound[0]) {
                return $Gson$Types.subtypeOf(upperBound);
            }
            return original;
        }
        return toResolve;
    }

    static Type resolveTypeVariable(Type context, Class<?> contextRawType, TypeVariable<?> unknown) {
        Class<?> declaredByRaw = $Gson$Types.declaringClassOf(unknown);
        if (declaredByRaw == null) {
            return unknown;
        }
        Type declaredBy = $Gson$Types.getGenericSupertype(context, contextRawType, declaredByRaw);
        if (declaredBy instanceof ParameterizedType) {
            int index = $Gson$Types.indexOf(declaredByRaw.getTypeParameters(), unknown);
            return ((ParameterizedType)declaredBy).getActualTypeArguments()[index];
        }
        return unknown;
    }

    private static int indexOf(Object[] array, Object toFind) {
        for (int i2 = 0; i2 < array.length; ++i2) {
            if (!toFind.equals(array[i2])) continue;
            return i2;
        }
        throw new NoSuchElementException();
    }

    private static Class<?> declaringClassOf(TypeVariable<?> typeVariable) {
        Object genericDeclaration = typeVariable.getGenericDeclaration();
        return genericDeclaration instanceof Class ? (Class)genericDeclaration : null;
    }

    private static void checkNotPrimitive(Type type) {
        $Gson$Preconditions.checkArgument(!(type instanceof Class) || !((Class)type).isPrimitive());
    }

    private static final class WildcardTypeImpl
    implements WildcardType,
    Serializable {
        private final Type upperBound;
        private final Type lowerBound;
        private static final long serialVersionUID = 0L;

        public WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
            $Gson$Preconditions.checkArgument(lowerBounds.length <= 1);
            $Gson$Preconditions.checkArgument(upperBounds.length == 1);
            if (lowerBounds.length == 1) {
                $Gson$Preconditions.checkNotNull(lowerBounds[0]);
                $Gson$Types.checkNotPrimitive(lowerBounds[0]);
                $Gson$Preconditions.checkArgument(upperBounds[0] == Object.class);
                this.lowerBound = $Gson$Types.canonicalize(lowerBounds[0]);
                this.upperBound = Object.class;
            } else {
                $Gson$Preconditions.checkNotNull(upperBounds[0]);
                $Gson$Types.checkNotPrimitive(upperBounds[0]);
                this.lowerBound = null;
                this.upperBound = $Gson$Types.canonicalize(upperBounds[0]);
            }
        }

        public Type[] getUpperBounds() {
            return new Type[]{this.upperBound};
        }

        public Type[] getLowerBounds() {
            Type[] typeArray;
            if (this.lowerBound != null) {
                Type[] typeArray2 = new Type[1];
                typeArray = typeArray2;
                typeArray2[0] = this.lowerBound;
            } else {
                typeArray = EMPTY_TYPE_ARRAY;
            }
            return typeArray;
        }

        public boolean equals(Object other) {
            return other instanceof WildcardType && $Gson$Types.equals(this, (WildcardType)other);
        }

        public int hashCode() {
            return (this.lowerBound != null ? 31 + this.lowerBound.hashCode() : 1) ^ 31 + this.upperBound.hashCode();
        }

        public String toString() {
            if (this.lowerBound != null) {
                return "? super " + $Gson$Types.typeToString(this.lowerBound);
            }
            if (this.upperBound == Object.class) {
                return "?";
            }
            return "? extends " + $Gson$Types.typeToString(this.upperBound);
        }
    }

    private static final class GenericArrayTypeImpl
    implements GenericArrayType,
    Serializable {
        private final Type componentType;
        private static final long serialVersionUID = 0L;

        public GenericArrayTypeImpl(Type componentType) {
            this.componentType = $Gson$Types.canonicalize(componentType);
        }

        public Type getGenericComponentType() {
            return this.componentType;
        }

        public boolean equals(Object o2) {
            return o2 instanceof GenericArrayType && $Gson$Types.equals(this, (GenericArrayType)o2);
        }

        public int hashCode() {
            return this.componentType.hashCode();
        }

        public String toString() {
            return $Gson$Types.typeToString(this.componentType) + "[]";
        }
    }

    private static final class ParameterizedTypeImpl
    implements ParameterizedType,
    Serializable {
        private final Type ownerType;
        private final Type rawType;
        private final Type[] typeArguments;
        private static final long serialVersionUID = 0L;

        public ParameterizedTypeImpl(Type ownerType, Type rawType, Type ... typeArguments) {
            if (rawType instanceof Class) {
                Class rawTypeAsClass = (Class)rawType;
                $Gson$Preconditions.checkArgument(ownerType != null || rawTypeAsClass.getEnclosingClass() == null);
                $Gson$Preconditions.checkArgument(ownerType == null || rawTypeAsClass.getEnclosingClass() != null);
            }
            this.ownerType = ownerType == null ? null : $Gson$Types.canonicalize(ownerType);
            this.rawType = $Gson$Types.canonicalize(rawType);
            this.typeArguments = (Type[])typeArguments.clone();
            for (int t2 = 0; t2 < this.typeArguments.length; ++t2) {
                $Gson$Preconditions.checkNotNull(this.typeArguments[t2]);
                $Gson$Types.checkNotPrimitive(this.typeArguments[t2]);
                this.typeArguments[t2] = $Gson$Types.canonicalize(this.typeArguments[t2]);
            }
        }

        public Type[] getActualTypeArguments() {
            return (Type[])this.typeArguments.clone();
        }

        public Type getRawType() {
            return this.rawType;
        }

        public Type getOwnerType() {
            return this.ownerType;
        }

        public boolean equals(Object other) {
            return other instanceof ParameterizedType && $Gson$Types.equals(this, (ParameterizedType)other);
        }

        public int hashCode() {
            return Arrays.hashCode(this.typeArguments) ^ this.rawType.hashCode() ^ $Gson$Types.hashCodeOrZero(this.ownerType);
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder(30 * (this.typeArguments.length + 1));
            stringBuilder.append($Gson$Types.typeToString(this.rawType));
            if (this.typeArguments.length == 0) {
                return stringBuilder.toString();
            }
            stringBuilder.append("<").append($Gson$Types.typeToString(this.typeArguments[0]));
            for (int i2 = 1; i2 < this.typeArguments.length; ++i2) {
                stringBuilder.append(", ").append($Gson$Types.typeToString(this.typeArguments[i2]));
            }
            return stringBuilder.append(">").toString();
        }
    }
}

