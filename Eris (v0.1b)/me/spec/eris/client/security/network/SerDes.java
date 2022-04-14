package me.spec.eris.client.security.network;

import java.util.List;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.io.DataInput;
import java.io.DataOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.NavigableMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * Serializes and deserializes objects.
 * <p>
 * This serializer is heavily tuned for
 * serialized data size.
 * <p>
 * The serializer cannot resolve circular
 * references and can only serialize types it
 * knows beforehand. Additionally, it doesn't
 * store any version information about the
 * serialized classes.
 *
 * @author Jonathan
 */
public class SerDes {

    @SuppressWarnings("rawtypes")
    private final MetadataWriter<SortedSet> SORTED_SET_METADATA_WRITER = (output, set) -> {
        this.writeObject(set.comparator(), output);
    };

    @SuppressWarnings({"rawtypes", "unchecked"})
    private final MetadataReader<TreeSet> SORTED_SET_METADATA_READER = input -> {
        try {
            Comparator comp = (Comparator) this.readObject(input);
            if (comp == null) {
                return new TreeSet();
            } else {
                return new TreeSet(comp);
            }
        } catch (ClassCastException e) {
            throw new IOException("Cannot deserialize SortedSet, not a comparator", e);
        }
    };

    @SuppressWarnings("rawtypes")
    private final MetadataWriter<SortedMap> SORTED_MAP_METADATA_WRITER = (output, map) -> {
        this.writeObject(map.comparator(), output);
    };

    @SuppressWarnings({"rawtypes", "unchecked"})
    private final MetadataReader<TreeMap> SORTED_MAP_METADATA_READER = input -> {
        try {
            Comparator comp = (Comparator) this.readObject(input);
            if (comp == null) {
                return new TreeMap();
            } else {
                return new TreeMap(comp);
            }
        } catch (ClassCastException e) {
            throw new IOException("Cannot deserialize SortedMap, not a comparator", e);
        }
    };

    @SuppressWarnings("rawtypes")
    private final List<TypeReaderWriter<?>> BUILTIN_READER_WRITERS = Arrays.asList(
            new TypeReaderWriter<Void>() {
                @Override
                public void write(Void obj, DataOutput output) throws IOException {
                    //Nothing to write
                }

                @Override
                public Void read(DataInput input) throws IOException {
                    return null;
                }

                @Override
                public Class<Void> getType() {
                    return Void.class;
                }

                @Override
                public Class<?> getPrimitiveType() {
                    return void.class;
                }
            },
            new TypeReaderWriter<Byte>() {
                @Override
                public void write(Byte obj, DataOutput output) throws IOException {
                    output.writeByte(obj);
                }

                @Override
                public Byte read(DataInput input) throws IOException {
                    return input.readByte();
                }

                @Override
                public Class<Byte> getType() {
                    return Byte.class;
                }

                @Override
                public Class<?> getPrimitiveType() {
                    return byte.class;
                }
            },
            new TypeReaderWriter<Short>() {
                @Override
                public void write(Short obj, DataOutput output) throws IOException {
                    output.writeShort(obj);
                }

                @Override
                public Short read(DataInput input) throws IOException {
                    return input.readShort();
                }

                @Override
                public Class<Short> getType() {
                    return Short.class;
                }

                @Override
                public Class<?> getPrimitiveType() {
                    return short.class;
                }
            },
            new TypeReaderWriter<Character>() {
                @Override
                public void write(Character obj, DataOutput output) throws IOException {
                    output.writeChar(obj);
                }

                @Override
                public Character read(DataInput input) throws IOException {
                    return input.readChar();
                }

                @Override
                public Class<Character> getType() {
                    return Character.class;
                }

                @Override
                public Class<?> getPrimitiveType() {
                    return char.class;
                }
            },
            new TypeReaderWriter<Integer>() {
                @Override
                public void write(Integer obj, DataOutput output) throws IOException {
                    output.writeInt(obj);
                }

                @Override
                public Integer read(DataInput input) throws IOException {
                    return input.readInt();
                }

                @Override
                public Class<Integer> getType() {
                    return Integer.class;
                }

                @Override
                public Class<?> getPrimitiveType() {
                    return int.class;
                }
            },
            new TypeReaderWriter<Long>() {
                @Override
                public void write(Long obj, DataOutput output) throws IOException {
                    output.writeLong(obj);
                }

                @Override
                public Long read(DataInput input) throws IOException {
                    return input.readLong();
                }

                @Override
                public Class<Long> getType() {
                    return Long.class;
                }

                @Override
                public Class<?> getPrimitiveType() {
                    return long.class;
                }
            },
            new TypeReaderWriter<Float>() {
                @Override
                public void write(Float obj, DataOutput output) throws IOException {
                    output.writeFloat(obj);
                }

                @Override
                public Float read(DataInput input) throws IOException {
                    return input.readFloat();
                }

                @Override
                public Class<Float> getType() {
                    return Float.class;
                }

                @Override
                public Class<?> getPrimitiveType() {
                    return float.class;
                }
            },
            new TypeReaderWriter<Double>() {
                @Override
                public void write(Double obj, DataOutput output) throws IOException {
                    output.writeDouble(obj);
                }

                @Override
                public Double read(DataInput input) throws IOException {
                    return input.readDouble();
                }

                @Override
                public Class<Double> getType() {
                    return Double.class;
                }

                @Override
                public Class<?> getPrimitiveType() {
                    return double.class;
                }
            },
            new TypeReaderWriter<Boolean>() {
                @Override
                public void write(Boolean obj, DataOutput output) throws IOException {
                    output.writeBoolean(obj);
                }

                @Override
                public Boolean read(DataInput input) throws IOException {
                    return input.readBoolean();
                }

                @Override
                public Class<Boolean> getType() {
                    return Boolean.class;
                }

                @Override
                public Class<?> getPrimitiveType() {
                    return boolean.class;
                }
            },
            new TypeReaderWriter<String>() {
                @Override
                public void write(String obj, DataOutput output) throws IOException {
                    output.writeUTF(obj);
                }

                @Override
                public String read(DataInput input) throws IOException {
                    return input.readUTF();
                }

                @Override
                public Class<String> getType() {
                    return String.class;
                }
            },
            new TypeReaderWriter<Class>() {
                @Override
                public void write(Class obj, DataOutput output) throws IOException {
                    output.writeUTF(obj.getName());
                }

                @Override
                public Class read(DataInput input) throws IOException {
                    try {
                        return Class.forName(input.readUTF());
                    } catch (ClassNotFoundException e) {
                        throw new IOException("Tried to deserialize unknown class object", e);
                    }
                }

                @Override
                public Class<Class> getType() {
                    return Class.class;
                }
            },
            this.createCollectionReaderWriter(List.class, null, input -> new ArrayList()),
            this.createCollectionReaderWriter(Set.class, null, input -> new HashSet()),
            this.createCollectionReaderWriter(SortedSet.class, SORTED_SET_METADATA_WRITER, SORTED_SET_METADATA_READER),
            this.createCollectionReaderWriter(NavigableSet.class, SORTED_SET_METADATA_WRITER, SORTED_SET_METADATA_READER),
            this.createMapReaderWriter(Map.class, null, input -> new HashMap()),
            this.createMapReaderWriter(SortedMap.class, SORTED_MAP_METADATA_WRITER, SORTED_MAP_METADATA_READER),
            this.createMapReaderWriter(NavigableMap.class, SORTED_MAP_METADATA_WRITER, SORTED_MAP_METADATA_READER)
    );

    /**
     * Creates a reader/writer which can handle
     * a specific collection interface and its implementations.
     *
     * @param clazz          The collection type to handle
     * @param metadataWriter Writes any metadata about the collection to the output. May be null.
     * @param metadataReader Reads any metadata from the input and creates an appropriate collection.
     * @return The reader/writer
     */
    @SuppressWarnings("rawtypes")
    public <T extends Collection> TypeReaderWriter<T> createCollectionReaderWriter(
            Class<T> clazz,
            MetadataWriter<? super T> metadataWriter,
            MetadataReader<? extends T> metadataReader) {
        final boolean isSetType = Set.class.isAssignableFrom(clazz);

        return new TypeReaderWriter<T>() {
            @Override
            public void write(T collection, DataOutput output) throws IOException {
                if (metadataWriter != null) {
                    metadataWriter.writeMetadata(output, collection);
                }

                TaggedReaderWriter<?> trw = null;
                boolean addNull = false;

                if (SerDes.this.optimizeCollectionStorage) {
                    Class<?> commonClass = null;

                    //Determine the class that every object in the
                    //collection is an instance of, if any
                    for (Object o : collection) {
                        if (o == null) {
                            if (!isSetType) {
                                //Null in a non-set type means tags are needed
                                //(null can be contained multiple times and at any position)
                                commonClass = null;
                                break;
                            } else {
                                //Null in a set means we can just add null up front
                                addNull = true;
                            }
                        } else {
                            //All non-null objects must have the same class
                            Class<?> objClass = o.getClass();
                            if (commonClass == null) {
                                //If we're looking at the first object, its class is trivially
                                //equal to the common class
                                commonClass = objClass;
                            }

                            //If there is an object of a different class, there is no
                            //common class and optimization is impossible
                            if (!commonClass.equals(objClass)) {
                                commonClass = null;
                                break;
                            }
                        }
                    }

                    if (commonClass != null) {
                        //If all objects have the same class, perform the optimization
                        trw = SerDes.this.getReaderWriterByClass(commonClass);
                    } else {
                        //addNull may have already been set, so reset it to avoid
                        //confusing further code if we're not actually optimizing
                        addNull = false;
                    }
                }

                //If there is a common TaggedReaderWriter, that means we're optimizing
                boolean optimizing = trw != null;
                output.writeBoolean(optimizing);
                if (optimizing) {
                    output.writeShort(trw.getTag());
                    output.writeBoolean(addNull);
                }

                //If we have to add null, it was contained exactly once
                //in the collection, and the size decreases by one
                output.writeInt(collection.size() - (addNull ? 1 : 0));
                for (Object o : collection) {
                    if (addNull && (o == null)) {
                        //Do not attempt to write null if null has to be omitted
                        continue;
                    }

                    if (trw != null) {
                        //If there is a common TaggedReaderWriter, we're optimizing
                        //this collection, and have to omit the tag
                        SerDes.this.writeObjectRawWithoutTag(o, output, trw);
                    } else {
                        //Otherwise, just write with tags
                        SerDes.this.writeObject(o, output);
                    }
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            public T read(DataInput input) throws IOException {
                T collection = metadataReader.readMetadata(input);

                //First see if the collection was optimized
                boolean optimizeCollection = input.readBoolean();
                TaggedReaderWriter<?> trw = null;

                if (optimizeCollection) {
                    //If it was optimized, there is one tag common to all
                    //objects in the collection, and thus one reader/writer
                    //to read them all
                    short optimizedTag = input.readShort();
                    trw = SerDes.this.getReaderWriterByTag(optimizedTag);

                    //The writer may have omitted a null in a set to enable
                    //optimization, add it back if needed
                    boolean addNull = input.readBoolean();
                    if (addNull) {
                        collection.add(null);
                    }
                }

                int nItems = input.readInt();
                for (int i = 0; i < nItems; i++) {
                    Object obj;
                    if (trw != null) {
                        //If there's a common reader/writer, that means the
                        //collection has been optimized, and tags have been
                        //omitted
                        obj = trw.getReaderWriter().read(input);
                    } else {
                        //Otherwise there are just normal objects with tags
                        obj = SerDes.this.readObject(input);
                    }
                    collection.add(obj);
                }
                return collection;
            }

            @Override
            public Class<T> getType() {
                return clazz;
            }

            @Override
            public boolean canHandleSubclasses() {
                return true;
            }
        };
    }

    /**
     * Creates a reader/writer which can handle
     * a specific map interface and its implementations.
     *
     * @param clazz          The map type to handle
     * @param metadataWriter Writes any metadata about the map to the output. May be null.
     * @param metadataReader Reads any metadata from the input and creates an appropriate map.
     * @return The reader/writer
     */
    @SuppressWarnings("rawtypes")
    public <T extends Map> TypeReaderWriter<T> createMapReaderWriter(
            Class<T> clazz,
            MetadataWriter<? super T> metadataWriter,
            MetadataReader<? extends T> metadataReader) {
        return new TypeReaderWriter<T>() {

            @Override
            public void write(T map, DataOutput output) throws IOException {
                if (metadataWriter != null) {
                    metadataWriter.writeMetadata(output, map);
                }

                Set keySet = map.keySet();

                //Store the mapping for null separately
                boolean hasNullMapping = false;
                Object nullMapping = null;

                //Store keys and values in two lists to benefit from
                //the tag optimizations of the list serializer
                List<Object> keys = new ArrayList<>();
                List<Object> values = new ArrayList<>();

                //Put keys and values into the lists (indices in the two
                //lists correspond to a key/value pair)
                for (Object key : keySet) {
                    Object value = map.get(key);
                    if (key == null) {
                        hasNullMapping = true;
                        nullMapping = value;
                    } else {
                        keys.add(key);
                        values.add(value);
                    }
                }

                //Store null mapping
                output.writeBoolean(hasNullMapping);
                if (hasNullMapping) {
                    SerDes.this.writeObject(nullMapping, output);
                }

                //Store keys and values
                TaggedReaderWriter<List> listWriter = SerDes.this.getReaderWriterByClass(List.class);
                SerDes.this.writeObjectRawWithoutTag(keys, output, listWriter);
                SerDes.this.writeObjectRawWithoutTag(values, output, listWriter);
            }

            @SuppressWarnings("unchecked")
            @Override
            public T read(DataInput input) throws IOException {
                T map = metadataReader.readMetadata(input);

                //Read null mapping, if any
                boolean hasNullMapping = input.readBoolean();
                if (hasNullMapping) {
                    Object nullMapping = SerDes.this.readObject(input);
                    map.put(null, nullMapping);
                }

                //Read key and value lists
                TaggedReaderWriter<List> listRw = SerDes.this.getReaderWriterByClass(List.class);
                List<Object> keys = listRw.getReaderWriter().read(input);
                List<Object> values = listRw.getReaderWriter().read(input);

                if (keys.size() != values.size()) {
                    throw new IOException("Malformed data: key/value lists have different size");
                }

                //Populate the map
                for (int i = 0; i < keys.size(); i++) {
                    map.put(keys.get(i), values.get(i));
                }

                return map;
            }

            @Override
            public Class<T> getType() {
                return clazz;
            }

            @Override
            public boolean canHandleSubclasses() {
                return true;
            }
        };
    }

    /**
     * Maps each serializable class to its reader/writer.
     */
    private final Map<Class<?>, TaggedReaderWriter<?>> readerWriterMap = new HashMap<>();

    /**
     * Maps each class tag to its reader/writer.
     */
    private final List<TaggedReaderWriter<?>> readerWriterByTag = new ArrayList<>();

    /**
     * True if the SerDes should attempt to omit class tags in collections if possible.
     */
    private boolean optimizeCollectionStorage = true;

    /**
     * Constructs a new SerDes which can only serialize
     * and deserialize types for which readers/writers
     * are available, and primitive types including
     * strings, lists, sets and maps.
     * <p>
     * Note that the order of the readers/writers in the
     * specified list has to be equal for every SerDes
     * that will operate on the same data.
     *
     * @param readerWriters The readers/writers this SerDes uses for serializing and deserializing objects.
     */
    public SerDes(List<TypeReaderWriter<?>> readerWriters) {
        this.init(readerWriters);
    }

    private void init(List<TypeReaderWriter<?>> readerWriters) {
        List<TypeReaderWriter<?>> rwlist = new ArrayList<>(BUILTIN_READER_WRITERS);
        rwlist.addAll(readerWriters);

        for (TypeReaderWriter<?> rw : rwlist) {
            this.addTypeReaderWriter(rw);
        }
    }

    /**
     * Adds a reader/writer for a specific type to this
     * SerDes, enabling it to read and write objects
     * of this type.
     * <p>
     * BE CAREFUL WHEN USING THIS METHOD. If you want
     * multiple instances of SerDes to produce compatible
     * serialized data, each of them must have the same
     * set of readers/writers, and the readers/writers
     * of each SerDes must have to be added in exactly
     * the same order.
     *
     * @param rw
     */
    public void addTypeReaderWriter(TypeReaderWriter<?> rw) {
        TaggedReaderWriter<?> trw = this.taggedReaderWriterInstantiateHelper((short) this.readerWriterByTag.size(), rw);
        this.readerWriterByTag.add(trw);
        this.readerWriterMap.put(rw.getType(), trw);
        Class<?> primitive = rw.getPrimitiveType();
        if (primitive != null) {
            this.readerWriterMap.put(primitive, trw);
        }
    }

    private <T> TaggedReaderWriter<T> taggedReaderWriterInstantiateHelper(short tag, TypeReaderWriter<T> rw) {
        return new TaggedReaderWriter<>(tag, rw);
    }

    /**
     * Constructs a new SerDes which can only serialize
     * and deserialize types for which readers/writers
     * are available. Automatically generates readers/
     * writers for the specified classes.
     * <p>
     * Readers/writers for primitive types including
     * strings, lists, sets and maps are available by
     * default and do not have to be specified explicitly
     * as serializable classes.
     * <p>
     * Note that the order of the classes and readers/
     * writers in the specified lists has to be equal
     * for every SerDes that will operate on the same
     * data.
     *
     * @param readerWriters  The readers/writers to use. If no explicit class readers/writers are desired, pass null.
     * @param autoGenerateRw The classes to generate readers/writers for automatically.
     * @throws NoSuchMethodException If there was no public/protected no-arg constructor for one of the classes.
     * @throws SecurityException     If a serialized field or a constructor was inaccessible.
     */
    public SerDes(List<TypeReaderWriter<?>> readerWriters, List<Class<? extends Serializable>> autoGenerateRw) throws NoSuchMethodException, SecurityException {
        if (readerWriters == null) {
            readerWriters = new ArrayList<>();
        }

        List<TypeReaderWriter<?>> rwlist = new ArrayList<>(readerWriters);
        for (Class<? extends Serializable> clazz : autoGenerateRw) {
            rwlist.add(this.makeClassReaderWriter(clazz));
        }
        this.init(rwlist);
    }


    /**
     * Creates a reader/writer for a class which
     * serializes and deserializes each serializable
     * field of the class.
     * <p>
     * The class has to have a public or protected
     * no-arg constructor.
     *
     * @param clazz The class to create the reader/writer for.
     * @return The reader/writer for the class.
     * @throws NoSuchMethodException If there is no appropriate constructor.
     * @throws SecurityException     If the constructor or one of the fields cannot be accessed.
     */
    private <T extends Serializable> TypeReaderWriter<T> makeClassReaderWriter(final Class<T> clazz) throws NoSuchMethodException, SecurityException {
        //Serialize superclass?
        final Class<? super T> superclass = clazz.getSuperclass();
        final boolean serializeSuperclass = (superclass != null) && Serializable.class.isAssignableFrom(superclass);

        //Get fields to serialize
        Field[] fields = clazz.getDeclaredFields();
        final List<FieldInfo> persistentFields = new ArrayList<>();
        for (Field f : fields) {
            int modifiers = f.getModifiers();

            //v1.1: Allow serialization of final fields.
            //Setting final fields via reflection may give weird results if the
            //field is a compile-time constant, but if it is, it will never have
            //a different value anyway, so this is not an issue.
            if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)) {

                //If the field's compile-time type is primitive, it is possible
                //to omit the runtime object type since the runtime type will
                //always be equal to the compile-time type.
                //Omitting the type for non-primitive fields with final type would
                //be possible as well, but since there would have to be a separate
                //marker for null and non-primitive types tend to be large anyway,
                //this is not worth the effort.
                boolean canOmitType = f.getType().isPrimitive();
                f.setAccessible(true);
                persistentFields.add(new FieldInfo(f, canOmitType));
            }
        }

        //Ensure consistent ordering on every JVM
        Collections.sort(persistentFields, (f1, f2) -> f1.getField().getName().compareTo(f2.getField().getName()));

        //Get public/protected no-arg constructor
        final Constructor<T> ctor = clazz.getDeclaredConstructor();
        ctor.setAccessible(true);

        //Maybe this anonymous class is a bit large, but whatever.
        return new TypeReaderWriter<T>() {

            /**
             * @return The reader/writer for the superclass.
             * @throws IOException
             */
            private TaggedReaderWriter<? super T> getSuperRw() throws IOException {
                TaggedReaderWriter<? super T> superRw = SerDes.this.getReaderWriterByClass(superclass);
                if (superRw == null) {
                    throw new IOException("Cannot serialize class " + clazz.getName() + ", no reader/writer for superclass known");
                }
                return superRw;
            }

            @Override
            public void write(T obj, DataOutput output) throws IOException {
                try {
                    //First serialize the superclass with its reader/writer, if needed.
                    if (serializeSuperclass) {
                        SerDes.this.writeObjectRawWithoutTag(obj, output, this.getSuperRw());
                    }

                    //Then serialize every field
                    for (FieldInfo f : persistentFields) {
                        TaggedReaderWriter<?> fieldRw = SerDes.this.getReaderWriterByClass(f.getField().getType());
                        Object child = f.getField().get(obj);

                        if ((fieldRw != null) && fieldRw.getReaderWriter().canHandleSubclasses() && (child != null)) {
                            //If there is a reader/writer for the declared field type and it
                            //says it can handle any subclass of the field type, use that instead
                            //(i.e. a common reader/writer for List)
                            SerDes.this.writeObjectRawWithTag(child, output, fieldRw);
                        } else if ((fieldRw != null) && f.canOmitTypeInfo()) {
                            //Omit the tag if possible
                            SerDes.this.writeObjectRawWithoutTag(child, output, fieldRw);
                        } else {
                            //Otherwise just use normal serialization based on the runtime type
                            SerDes.this.writeObject(child, output);
                        }
                    }
                } catch (IllegalArgumentException e) {
                    //Only thrown when field.get didn't work, which means a bug
                    throw new RuntimeException("Bug in SerDes: Passed wrong object type to auto-generated TypeReaderWriter", e);
                } catch (IllegalAccessException e) {
                    //Should never be thrown since we called field.setAccessible(true), but who knows
                    throw new RuntimeException("Bug in SerDes: Cannot access field (get)", e);
                }
            }

            @Override
            public T read(DataInput input) throws IOException {
                T result;
                try {
                    result = ctor.newInstance();
                } catch (InstantiationException | InvocationTargetException e) {
                    throw new IOException("Could not instantiate object", e);
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    //We've got a no-arg constructor and setAccessible(true), so this should
                    //never be thrown, but who knows
                    throw new RuntimeException("Bug in SerDes: Cannot run constructor", e);
                }

                try {
                    this.readFields(result, input);
                } catch (IllegalArgumentException e) {
                    //Indicates that there was a class serialized into the field that doesn't
                    //belong there
                    throw new IOException("Wrong value type for field", e);
                } catch (IllegalAccessException e) {
                    //Should never be thrown since we called field.setAccessible(true), but who knows
                    throw new RuntimeException("Bug in SerDes: Cannot access field (set)", e);
                }

                return result;
            }

            @Override
            public void readFields(T target, DataInput input) throws IOException, IllegalArgumentException, IllegalAccessException {
                if (serializeSuperclass) {
                    try {
                        //If the superclass was serialized, first read its fields
                        //into the already-created object.
                        this.getSuperRw().getReaderWriter().readFields(target, input);
                    } catch (UnsupportedOperationException e) {
                        throw new IOException("Cannot restore fields of superclass, superclass reader doesn't support readFields", e);
                    }
                }

                //Read every field.
                for (FieldInfo f : persistentFields) {

                    //If the field's runtime type can be determined at
                    //compile-time, the tag has been omitted to save space.
                    if (f.canOmitTypeInfo()) {
                        TaggedReaderWriter<?> fieldRw = SerDes.this.getReaderWriterByClass(f.getField().getType());
                        if (fieldRw != null) {
                            f.getField().set(target, fieldRw.getReaderWriter().read(input));
                            continue;
                        }
                    }

                    //Read via the default mechanism.
                    f.getField().set(target, SerDes.this.readObject(input));
                }
            }

            @Override
            public Class<T> getType() {
                return clazz;
            }
        };
    }

    /**
     * Writes the given object to the data output.
     *
     * @param obj    The object to write.
     * @param output The output to write the object to.
     * @throws IOException If the object could not be serialized or the stream threw an exception.
     */
    public void writeObject(Object obj, DataOutput output) throws IOException {
        TaggedReaderWriter<?> trw;
        if (obj == null) {
            //Treat nulls as "instances" of the void type.
            trw = this.getReaderWriterByClass(Void.class);
        } else {
            trw = this.getReaderWriterByClass(obj.getClass());
        }
        if (trw == null) {
            throw new IOException("Cannot serialize unknown object " + obj.toString());
        }
        this.writeObjectRawWithTag(obj, output, trw);
    }

    private <T> void writeObjectRawWithTag(Object obj, DataOutput output, TaggedReaderWriter<T> trw) throws IOException {
        output.writeShort(trw.getTag());
        this.writeObjectRawWithoutTag(obj, output, trw);
    }

    @SuppressWarnings("unchecked")
    private <T> void writeObjectRawWithoutTag(Object obj, DataOutput output, TaggedReaderWriter<T> trw) throws IOException {
        trw.getReaderWriter().write((T) obj, output);
    }

    @SuppressWarnings("unchecked")
    private <T> TaggedReaderWriter<T> getReaderWriterByClass(Class<T> clazz) {
        return (TaggedReaderWriter<T>) this.readerWriterMap.get(clazz);
    }

    /**
     * Reads an object from the data input.
     *
     * @param input The input to read from.
     * @return The read object.
     * @throws IOException If the object could not be deserialized or the stream threw an exception.
     */
    public Object readObject(DataInput input) throws IOException {
        short tag = input.readShort();
        return this.getReaderWriterByTag(tag).getReaderWriter().read(input);
    }

    private TaggedReaderWriter<?> getReaderWriterByTag(short tag) throws IOException {
        if ((tag < 0) || (tag >= this.readerWriterByTag.size())) {
            throw new IOException("Unknown tag: " + tag);
        }
        return this.readerWriterByTag.get(tag);
    }

    /**
     * Enables or disables optimization of collections which
     * only contain objects of the same class. Disabling
     * the optimization will result in faster serialization,
     * but the serialized data will be bigger in most cases,
     * especially in collections of primitive types.
     * <p>
     * The optimization is enabled by default.
     *
     * @param optimize True if the SerDes should attempt to omit class tags in collections if possible.
     */
    public void setOptimizeCollectionStorage(boolean optimize) {
        this.optimizeCollectionStorage = optimize;
    }

}
