package org.yaml.snakeyaml.constructor;

import org.yaml.snakeyaml.*;
import java.lang.reflect.*;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.introspector.*;
import java.beans.*;
import org.yaml.snakeyaml.error.*;
import java.math.*;
import java.util.*;

public class Constructor extends SafeConstructor
{
    private final Map<Tag, Class<?>> typeTags;
    private final Map<Class<?>, TypeDescription> typeDefinitions;
    
    public Constructor() {
        this(Object.class);
    }
    
    public Constructor(final Class<?> theRoot) {
        if (theRoot == null) {
            throw new NullPointerException("Root type must be provided.");
        }
        this.yamlConstructors.put(null, new ConstructYamlObject());
        if (!Object.class.equals(theRoot)) {
            this.rootTag = new Tag(theRoot);
        }
        this.typeTags = new HashMap<Tag, Class<?>>();
        this.typeDefinitions = new HashMap<Class<?>, TypeDescription>();
        this.yamlClassConstructors.put(NodeId.scalar, new ConstructScalar());
        this.yamlClassConstructors.put(NodeId.mapping, new ConstructMapping());
        this.yamlClassConstructors.put(NodeId.sequence, new ConstructSequence());
    }
    
    public Constructor(final String theRoot) throws ClassNotFoundException {
        this(Class.forName(check(theRoot)));
    }
    
    private static final String check(final String s) {
        if (s == null) {
            throw new NullPointerException("Root type must be provided.");
        }
        if (s.trim().length() == 0) {
            throw new YAMLException("Root type must be provided.");
        }
        return s;
    }
    
    public TypeDescription addTypeDescription(final TypeDescription definition) {
        if (definition == null) {
            throw new NullPointerException("TypeDescription is required.");
        }
        if (this.rootTag == null && definition.isRoot()) {
            this.rootTag = new Tag(definition.getType());
        }
        final Tag tag = definition.getTag();
        this.typeTags.put(tag, definition.getType());
        return this.typeDefinitions.put(definition.getType(), definition);
    }
    
    protected Class<?> getClassForNode(final Node node) {
        final Class<?> classForTag = this.typeTags.get(node.getTag());
        if (classForTag == null) {
            final String name = node.getTag().getClassName();
            Class<?> cl;
            try {
                cl = this.getClassForName(name);
            }
            catch (ClassNotFoundException e) {
                throw new YAMLException("Class not found: " + name);
            }
            this.typeTags.put(node.getTag(), cl);
            return cl;
        }
        return classForTag;
    }
    
    protected Class<?> getClassForName(final String name) throws ClassNotFoundException {
        return Class.forName(name);
    }
    
    protected class ConstructMapping implements Construct
    {
        public Object construct(final Node node) {
            final MappingNode mnode = (MappingNode)node;
            if (Properties.class.isAssignableFrom(node.getType())) {
                final Properties properties = new Properties();
                if (!node.isTwoStepsConstruction()) {
                    Constructor.this.constructMapping2ndStep(mnode, properties);
                    return properties;
                }
                throw new YAMLException("Properties must not be recursive.");
            }
            else {
                if (SortedMap.class.isAssignableFrom(node.getType())) {
                    final SortedMap<Object, Object> map = new TreeMap<Object, Object>();
                    if (!node.isTwoStepsConstruction()) {
                        Constructor.this.constructMapping2ndStep(mnode, map);
                    }
                    return map;
                }
                if (Map.class.isAssignableFrom(node.getType())) {
                    if (node.isTwoStepsConstruction()) {
                        return Constructor.this.createDefaultMap();
                    }
                    return Constructor.this.constructMapping(mnode);
                }
                else {
                    if (SortedSet.class.isAssignableFrom(node.getType())) {
                        final SortedSet<Object> set = new TreeSet<Object>();
                        Constructor.this.constructSet2ndStep(mnode, set);
                        return set;
                    }
                    if (Collection.class.isAssignableFrom(node.getType())) {
                        if (node.isTwoStepsConstruction()) {
                            return Constructor.this.createDefaultSet();
                        }
                        return Constructor.this.constructSet(mnode);
                    }
                    else {
                        if (node.isTwoStepsConstruction()) {
                            return this.createEmptyJavaBean(mnode);
                        }
                        return this.constructJavaBean2ndStep(mnode, this.createEmptyJavaBean(mnode));
                    }
                }
            }
        }
        
        public void construct2ndStep(final Node node, final Object object) {
            if (Map.class.isAssignableFrom(node.getType())) {
                Constructor.this.constructMapping2ndStep((MappingNode)node, (Map<Object, Object>)object);
            }
            else if (Set.class.isAssignableFrom(node.getType())) {
                Constructor.this.constructSet2ndStep((MappingNode)node, (Set<Object>)object);
            }
            else {
                this.constructJavaBean2ndStep((MappingNode)node, object);
            }
        }
        
        protected Object createEmptyJavaBean(final MappingNode node) {
            try {
                final Class<?> type = node.getType();
                if (Modifier.isAbstract(type.getModifiers())) {
                    node.setType(Constructor.this.getClassForNode(node));
                }
                final java.lang.reflect.Constructor<?> c = node.getType().getDeclaredConstructor((Class<?>[])new Class[0]);
                c.setAccessible(true);
                return c.newInstance(new Object[0]);
            }
            catch (Exception e) {
                throw new YAMLException(e);
            }
        }
        
        protected Object constructJavaBean2ndStep(final MappingNode node, final Object object) {
            Constructor.this.flattenMapping(node);
            final Class<?> beanType = node.getType();
            final List<NodeTuple> nodeValue = node.getValue();
            for (final NodeTuple tuple : nodeValue) {
                if (!(tuple.getKeyNode() instanceof ScalarNode)) {
                    throw new YAMLException("Keys must be scalars but found: " + tuple.getKeyNode());
                }
                final ScalarNode keyNode = (ScalarNode)tuple.getKeyNode();
                final Node valueNode = tuple.getValueNode();
                keyNode.setType(String.class);
                final String key = (String)Constructor.this.constructObject(keyNode);
                try {
                    final Property property = this.getProperty(beanType, key);
                    valueNode.setType(property.getType());
                    final TypeDescription memberDescription = Constructor.this.typeDefinitions.get(beanType);
                    boolean typeDetected = false;
                    if (memberDescription != null) {
                        switch (valueNode.getNodeId()) {
                            case sequence: {
                                final SequenceNode snode = (SequenceNode)valueNode;
                                final Class<?> memberType = memberDescription.getListPropertyType(key);
                                if (memberType != null) {
                                    snode.setListType(memberType);
                                    typeDetected = true;
                                    break;
                                }
                                if (property.getType().isArray()) {
                                    snode.setListType(property.getType().getComponentType());
                                    typeDetected = true;
                                    break;
                                }
                                break;
                            }
                            case mapping: {
                                final MappingNode mnode = (MappingNode)valueNode;
                                final Class<?> keyType = memberDescription.getMapKeyType(key);
                                if (keyType != null) {
                                    mnode.setTypes(keyType, memberDescription.getMapValueType(key));
                                    typeDetected = true;
                                    break;
                                }
                                break;
                            }
                        }
                    }
                    if (!typeDetected && valueNode.getNodeId() != NodeId.scalar) {
                        final Class[] arguments = property.getActualTypeArguments();
                        if (arguments != null) {
                            if (valueNode.getNodeId() == NodeId.sequence) {
                                final Class t = arguments[0];
                                final SequenceNode snode2 = (SequenceNode)valueNode;
                                snode2.setListType(t);
                            }
                            else if (valueNode.getTag().equals(Tag.SET)) {
                                final Class t = arguments[0];
                                final MappingNode mnode = (MappingNode)valueNode;
                                mnode.setOnlyKeyType(t);
                                mnode.setUseClassConstructor(true);
                            }
                            else if (valueNode.getNodeId() == NodeId.mapping) {
                                final Class ketType = arguments[0];
                                final Class valueType = arguments[1];
                                final MappingNode mnode2 = (MappingNode)valueNode;
                                mnode2.setTypes(ketType, valueType);
                                mnode2.setUseClassConstructor(true);
                            }
                        }
                    }
                    final Object value = Constructor.this.constructObject(valueNode);
                    property.set(object, value);
                }
                catch (Exception e) {
                    throw new YAMLException("Cannot create property=" + key + " for JavaBean=" + object + "; " + e.getMessage(), e);
                }
            }
            return object;
        }
        
        protected Property getProperty(final Class<?> type, final String name) throws IntrospectionException {
            return Constructor.this.getPropertyUtils().getProperty(type, name);
        }
    }
    
    protected class ConstructYamlObject implements Construct
    {
        private Construct getConstructor(final Node node) {
            final Class cl = Constructor.this.getClassForNode(node);
            node.setType(cl);
            final Construct constructor = Constructor.this.yamlClassConstructors.get(node.getNodeId());
            return constructor;
        }
        
        public Object construct(final Node node) {
            Object result = null;
            try {
                result = this.getConstructor(node).construct(node);
            }
            catch (Exception e) {
                throw new ConstructorException(null, null, "Can't construct a java object for " + node.getTag() + "; exception=" + e.getMessage(), node.getStartMark(), e);
            }
            return result;
        }
        
        public void construct2ndStep(final Node node, final Object object) {
            try {
                this.getConstructor(node).construct2ndStep(node, object);
            }
            catch (Exception e) {
                throw new ConstructorException(null, null, "Can't construct a second step for a java object for " + node.getTag() + "; exception=" + e.getMessage(), node.getStartMark(), e);
            }
        }
    }
    
    protected class ConstructScalar extends AbstractConstruct
    {
        public Object construct(final Node nnode) {
            final ScalarNode node = (ScalarNode)nnode;
            final Class type = node.getType();
            Object result;
            if (type.isPrimitive() || type == String.class || Number.class.isAssignableFrom(type) || type == Boolean.class || Date.class.isAssignableFrom(type) || type == Character.class || type == BigInteger.class || type == BigDecimal.class || Enum.class.isAssignableFrom(type) || Tag.BINARY.equals(node.getTag()) || Calendar.class.isAssignableFrom(type)) {
                result = this.constructStandardJavaInstance(type, node);
            }
            else {
                final java.lang.reflect.Constructor[] javaConstructors = type.getConstructors();
                int oneArgCount = 0;
                java.lang.reflect.Constructor javaConstructor = null;
                for (final java.lang.reflect.Constructor c : javaConstructors) {
                    if (c.getParameterTypes().length == 1) {
                        ++oneArgCount;
                        javaConstructor = c;
                    }
                }
                if (javaConstructor == null) {
                    throw new YAMLException("No single argument constructor found for " + type);
                }
                Object argument;
                if (oneArgCount == 1) {
                    argument = this.constructStandardJavaInstance(javaConstructor.getParameterTypes()[0], node);
                }
                else {
                    argument = Constructor.this.constructScalar(node);
                    try {
                        javaConstructor = type.getConstructor(String.class);
                    }
                    catch (Exception e) {
                        throw new ConstructorException(null, null, "Can't construct a java object for scalar " + node.getTag() + "; No String constructor found. Exception=" + e.getMessage(), node.getStartMark(), e);
                    }
                }
                try {
                    result = javaConstructor.newInstance(argument);
                }
                catch (Exception e) {
                    throw new ConstructorException(null, null, "Can't construct a java object for scalar " + node.getTag() + "; exception=" + e.getMessage(), node.getStartMark(), e);
                }
            }
            return result;
        }
        
        private Object constructStandardJavaInstance(final Class type, final ScalarNode node) {
            Object result;
            if (type == String.class) {
                final Construct stringConstructor = Constructor.this.yamlConstructors.get(Tag.STR);
                result = stringConstructor.construct(node);
            }
            else if (type == Boolean.class || type == Boolean.TYPE) {
                final Construct boolConstructor = Constructor.this.yamlConstructors.get(Tag.BOOL);
                result = boolConstructor.construct(node);
            }
            else if (type == Character.class || type == Character.TYPE) {
                final Construct charConstructor = Constructor.this.yamlConstructors.get(Tag.STR);
                final String ch = (String)charConstructor.construct(node);
                if (ch.length() == 0) {
                    result = null;
                }
                else {
                    if (ch.length() != 1) {
                        throw new YAMLException("Invalid node Character: '" + ch + "'; length: " + ch.length());
                    }
                    result = new Character(ch.charAt(0));
                }
            }
            else if (Date.class.isAssignableFrom(type)) {
                final Construct dateConstructor = Constructor.this.yamlConstructors.get(Tag.TIMESTAMP);
                final Date date = (Date)dateConstructor.construct(node);
                if (type == Date.class) {
                    result = date;
                }
                else {
                    try {
                        final java.lang.reflect.Constructor<?> constr = type.getConstructor(Long.TYPE);
                        result = constr.newInstance(date.getTime());
                    }
                    catch (Exception e) {
                        throw new YAMLException("Cannot construct: '" + type + "'");
                    }
                }
            }
            else if (type == Float.class || type == Double.class || type == Float.TYPE || type == Double.TYPE || type == BigDecimal.class) {
                if (type == BigDecimal.class) {
                    result = new BigDecimal(node.getValue());
                }
                else {
                    final Construct doubleConstructor = Constructor.this.yamlConstructors.get(Tag.FLOAT);
                    result = doubleConstructor.construct(node);
                    if (type == Float.class || type == Float.TYPE) {
                        result = new Float((double)result);
                    }
                }
            }
            else if (type == Byte.class || type == Short.class || type == Integer.class || type == Long.class || type == BigInteger.class || type == Byte.TYPE || type == Short.TYPE || type == Integer.TYPE || type == Long.TYPE) {
                final Construct intConstructor = Constructor.this.yamlConstructors.get(Tag.INT);
                result = intConstructor.construct(node);
                if (type == Byte.class || type == Byte.TYPE) {
                    result = new Byte(result.toString());
                }
                else if (type == Short.class || type == Short.TYPE) {
                    result = new Short(result.toString());
                }
                else if (type == Integer.class || type == Integer.TYPE) {
                    result = new Integer(result.toString());
                }
                else if (type == Long.class || type == Long.TYPE) {
                    result = new Long(result.toString());
                }
                else {
                    result = new BigInteger(result.toString());
                }
            }
            else if (Enum.class.isAssignableFrom(type)) {
                final String enumValueName = node.getValue();
                try {
                    result = Enum.valueOf((Class<Object>)type, enumValueName);
                }
                catch (Exception ex) {
                    throw new YAMLException("Unable to find enum value '" + enumValueName + "' for enum class: " + type.getName());
                }
            }
            else {
                if (!Calendar.class.isAssignableFrom(type)) {
                    throw new YAMLException("Unsupported class: " + type);
                }
                final ConstructYamlTimestamp contr = new ConstructYamlTimestamp();
                contr.construct(node);
                result = contr.getCalendar();
            }
            return result;
        }
    }
    
    protected class ConstructSequence implements Construct
    {
        public Object construct(final Node node) {
            final SequenceNode snode = (SequenceNode)node;
            if (Set.class.isAssignableFrom(node.getType())) {
                if (node.isTwoStepsConstruction()) {
                    throw new YAMLException("Set cannot be recursive.");
                }
                return Constructor.this.constructSet(snode);
            }
            else if (Collection.class.isAssignableFrom(node.getType())) {
                if (node.isTwoStepsConstruction()) {
                    return Constructor.this.createDefaultList(snode.getValue().size());
                }
                return Constructor.this.constructSequence(snode);
            }
            else {
                if (!node.getType().isArray()) {
                    final List<java.lang.reflect.Constructor> possibleConstructors = new ArrayList<java.lang.reflect.Constructor>(snode.getValue().size());
                    for (final java.lang.reflect.Constructor constructor : node.getType().getConstructors()) {
                        if (snode.getValue().size() == constructor.getParameterTypes().length) {
                            possibleConstructors.add(constructor);
                        }
                    }
                    if (!possibleConstructors.isEmpty()) {
                        if (possibleConstructors.size() == 1) {
                            final Object[] argumentList = new Object[snode.getValue().size()];
                            final java.lang.reflect.Constructor c = possibleConstructors.get(0);
                            int index = 0;
                            for (final Node argumentNode : snode.getValue()) {
                                final Class type = c.getParameterTypes()[index];
                                argumentNode.setType(type);
                                argumentList[index++] = Constructor.this.constructObject(argumentNode);
                            }
                            try {
                                return c.newInstance(argumentList);
                            }
                            catch (Exception e) {
                                throw new YAMLException(e);
                            }
                        }
                        final List<Object> argumentList2 = (List<Object>)Constructor.this.constructSequence(snode);
                        final Class[] parameterTypes = new Class[argumentList2.size()];
                        int index = 0;
                        for (final Object parameter : argumentList2) {
                            parameterTypes[index] = parameter.getClass();
                            ++index;
                        }
                        for (final java.lang.reflect.Constructor c2 : possibleConstructors) {
                            final Class[] argTypes = c2.getParameterTypes();
                            boolean foundConstructor = true;
                            for (int i = 0; i < argTypes.length; ++i) {
                                if (!this.wrapIfPrimitive(argTypes[i]).isAssignableFrom(parameterTypes[i])) {
                                    foundConstructor = false;
                                    break;
                                }
                            }
                            if (foundConstructor) {
                                try {
                                    return c2.newInstance(argumentList2.toArray());
                                }
                                catch (Exception e2) {
                                    throw new YAMLException(e2);
                                }
                            }
                        }
                    }
                    throw new YAMLException("No suitable constructor with " + String.valueOf(snode.getValue().size()) + " arguments found for " + node.getType());
                }
                if (node.isTwoStepsConstruction()) {
                    return Constructor.this.createArray(node.getType(), snode.getValue().size());
                }
                return Constructor.this.constructArray(snode);
            }
        }
        
        private final Class<?> wrapIfPrimitive(final Class<?> clazz) {
            if (!clazz.isPrimitive()) {
                return clazz;
            }
            if (clazz == Integer.TYPE) {
                return Integer.class;
            }
            if (clazz == Float.TYPE) {
                return Float.class;
            }
            if (clazz == Double.TYPE) {
                return Double.class;
            }
            if (clazz == Boolean.TYPE) {
                return Boolean.class;
            }
            if (clazz == Long.TYPE) {
                return Long.class;
            }
            if (clazz == Character.TYPE) {
                return Character.class;
            }
            if (clazz == Short.TYPE) {
                return Short.class;
            }
            if (clazz == Byte.TYPE) {
                return Byte.class;
            }
            throw new YAMLException("Unexpected primitive " + clazz);
        }
        
        public void construct2ndStep(final Node node, final Object object) {
            final SequenceNode snode = (SequenceNode)node;
            if (List.class.isAssignableFrom(node.getType())) {
                final List<Object> list = (List<Object>)object;
                Constructor.this.constructSequenceStep2(snode, list);
            }
            else {
                if (!node.getType().isArray()) {
                    throw new YAMLException("Immutable objects cannot be recursive.");
                }
                Constructor.this.constructArrayStep2(snode, object);
            }
        }
    }
}
