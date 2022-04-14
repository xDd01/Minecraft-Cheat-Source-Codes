package org.apache.commons.lang3;

import java.io.*;
import java.util.*;

public class SerializationUtils
{
    public static <T extends Serializable> T clone(final T object) {
        if (object == null) {
            return null;
        }
        final byte[] objectData = serialize(object);
        final ByteArrayInputStream bais = new ByteArrayInputStream(objectData);
        try (final ClassLoaderAwareObjectInputStream in = new ClassLoaderAwareObjectInputStream(bais, object.getClass().getClassLoader())) {
            final T readObject = (T)in.readObject();
            return readObject;
        }
        catch (ClassNotFoundException ex) {
            throw new SerializationException("ClassNotFoundException while reading cloned object data", ex);
        }
        catch (IOException ex2) {
            throw new SerializationException("IOException while reading or closing cloned object data", ex2);
        }
    }
    
    public static <T extends Serializable> T roundtrip(final T msg) {
        return deserialize(serialize(msg));
    }
    
    public static void serialize(final Serializable obj, final OutputStream outputStream) {
        Validate.isTrue(outputStream != null, "The OutputStream must not be null", new Object[0]);
        try (final ObjectOutputStream out = new ObjectOutputStream(outputStream)) {
            out.writeObject(obj);
        }
        catch (IOException ex) {
            throw new SerializationException(ex);
        }
    }
    
    public static byte[] serialize(final Serializable obj) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        serialize(obj, baos);
        return baos.toByteArray();
    }
    
    public static <T> T deserialize(final InputStream inputStream) {
        Validate.isTrue(inputStream != null, "The InputStream must not be null", new Object[0]);
        try (final ObjectInputStream in = new ObjectInputStream(inputStream)) {
            final T obj = (T)in.readObject();
            return obj;
        }
        catch (ClassNotFoundException | IOException ex3) {
            final Exception ex2;
            final Exception ex = ex2;
            throw new SerializationException(ex);
        }
    }
    
    public static <T> T deserialize(final byte[] objectData) {
        Validate.isTrue(objectData != null, "The byte[] must not be null", new Object[0]);
        return deserialize(new ByteArrayInputStream(objectData));
    }
    
    static class ClassLoaderAwareObjectInputStream extends ObjectInputStream
    {
        private static final Map<String, Class<?>> primitiveTypes;
        private final ClassLoader classLoader;
        
        ClassLoaderAwareObjectInputStream(final InputStream in, final ClassLoader classLoader) throws IOException {
            super(in);
            this.classLoader = classLoader;
        }
        
        @Override
        protected Class<?> resolveClass(final ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            final String name = desc.getName();
            try {
                return Class.forName(name, false, this.classLoader);
            }
            catch (ClassNotFoundException ex) {
                try {
                    return Class.forName(name, false, Thread.currentThread().getContextClassLoader());
                }
                catch (ClassNotFoundException cnfe) {
                    final Class<?> cls = ClassLoaderAwareObjectInputStream.primitiveTypes.get(name);
                    if (cls != null) {
                        return cls;
                    }
                    throw cnfe;
                }
            }
        }
        
        static {
            (primitiveTypes = new HashMap<String, Class<?>>()).put("byte", Byte.TYPE);
            ClassLoaderAwareObjectInputStream.primitiveTypes.put("short", Short.TYPE);
            ClassLoaderAwareObjectInputStream.primitiveTypes.put("int", Integer.TYPE);
            ClassLoaderAwareObjectInputStream.primitiveTypes.put("long", Long.TYPE);
            ClassLoaderAwareObjectInputStream.primitiveTypes.put("float", Float.TYPE);
            ClassLoaderAwareObjectInputStream.primitiveTypes.put("double", Double.TYPE);
            ClassLoaderAwareObjectInputStream.primitiveTypes.put("boolean", Boolean.TYPE);
            ClassLoaderAwareObjectInputStream.primitiveTypes.put("char", Character.TYPE);
            ClassLoaderAwareObjectInputStream.primitiveTypes.put("void", Void.TYPE);
        }
    }
}
