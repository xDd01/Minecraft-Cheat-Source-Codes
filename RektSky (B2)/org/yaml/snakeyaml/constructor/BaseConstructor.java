package org.yaml.snakeyaml.constructor;

import org.yaml.snakeyaml.composer.*;
import org.yaml.snakeyaml.introspector.*;
import java.lang.reflect.*;
import org.yaml.snakeyaml.error.*;
import java.util.*;
import org.yaml.snakeyaml.nodes.*;

public abstract class BaseConstructor
{
    protected final Map<NodeId, Construct> yamlClassConstructors;
    protected final Map<Tag, Construct> yamlConstructors;
    protected final Map<String, Construct> yamlMultiConstructors;
    private Composer composer;
    private final Map<Node, Object> constructedObjects;
    private final Set<Node> recursiveObjects;
    private final ArrayList<RecursiveTuple<Map<Object, Object>, RecursiveTuple<Object, Object>>> maps2fill;
    private final ArrayList<RecursiveTuple<Set<Object>, Object>> sets2fill;
    protected Tag rootTag;
    private PropertyUtils propertyUtils;
    private boolean explicitPropertyUtils;
    
    public BaseConstructor() {
        this.yamlClassConstructors = new EnumMap<NodeId, Construct>(NodeId.class);
        this.yamlConstructors = new HashMap<Tag, Construct>();
        this.yamlMultiConstructors = new HashMap<String, Construct>();
        this.constructedObjects = new HashMap<Node, Object>();
        this.recursiveObjects = new HashSet<Node>();
        this.maps2fill = new ArrayList<RecursiveTuple<Map<Object, Object>, RecursiveTuple<Object, Object>>>();
        this.sets2fill = new ArrayList<RecursiveTuple<Set<Object>, Object>>();
        this.rootTag = null;
        this.explicitPropertyUtils = false;
    }
    
    public void setComposer(final Composer composer) {
        this.composer = composer;
    }
    
    public boolean checkData() {
        return this.composer.checkNode();
    }
    
    public Object getData() {
        this.composer.checkNode();
        final Node node = this.composer.getNode();
        if (this.rootTag != null) {
            node.setTag(this.rootTag);
        }
        return this.constructDocument(node);
    }
    
    public Object getSingleData() {
        final Node node = this.composer.getSingleNode();
        if (node != null) {
            if (this.rootTag != null) {
                node.setTag(this.rootTag);
            }
            return this.constructDocument(node);
        }
        return null;
    }
    
    private Object constructDocument(final Node node) {
        final Object data = this.constructObject(node);
        this.fillRecursive();
        this.constructedObjects.clear();
        this.recursiveObjects.clear();
        return data;
    }
    
    private void fillRecursive() {
        if (!this.maps2fill.isEmpty()) {
            for (final RecursiveTuple<Map<Object, Object>, RecursiveTuple<Object, Object>> entry : this.maps2fill) {
                final RecursiveTuple<Object, Object> key_value = entry._2();
                entry._1().put(key_value._1(), key_value._2());
            }
            this.maps2fill.clear();
        }
        if (!this.sets2fill.isEmpty()) {
            for (final RecursiveTuple<Set<Object>, Object> value : this.sets2fill) {
                value._1().add(value._2());
            }
            this.sets2fill.clear();
        }
    }
    
    protected Object constructObject(final Node node) {
        if (this.constructedObjects.containsKey(node)) {
            return this.constructedObjects.get(node);
        }
        if (this.recursiveObjects.contains(node)) {
            throw new ConstructorException(null, null, "found unconstructable recursive node", node.getStartMark());
        }
        this.recursiveObjects.add(node);
        final Construct constructor = this.getConstructor(node);
        final Object data = constructor.construct(node);
        this.constructedObjects.put(node, data);
        this.recursiveObjects.remove(node);
        if (node.isTwoStepsConstruction()) {
            constructor.construct2ndStep(node, data);
        }
        return data;
    }
    
    protected Construct getConstructor(final Node node) {
        if (node.useClassConstructor()) {
            return this.yamlClassConstructors.get(node.getNodeId());
        }
        final Construct constructor = this.yamlConstructors.get(node.getTag());
        if (constructor == null) {
            for (final String prefix : this.yamlMultiConstructors.keySet()) {
                if (node.getTag().startsWith(prefix)) {
                    return this.yamlMultiConstructors.get(prefix);
                }
            }
            return this.yamlConstructors.get(null);
        }
        return constructor;
    }
    
    protected Object constructScalar(final ScalarNode node) {
        return node.getValue();
    }
    
    protected List<Object> createDefaultList(final int initSize) {
        return new ArrayList<Object>(initSize);
    }
    
    protected Set<Object> createDefaultSet(final int initSize) {
        return new LinkedHashSet<Object>(initSize);
    }
    
    protected <T> T[] createArray(final Class<T> type, final int size) {
        return (T[])Array.newInstance(type.getComponentType(), size);
    }
    
    protected List<?> constructSequence(final SequenceNode node) {
        List<Object> result = null;
        Label_0061: {
            if (List.class.isAssignableFrom(node.getType()) && !node.getType().isInterface()) {
                try {
                    result = (List<Object>)node.getType().newInstance();
                    break Label_0061;
                }
                catch (Exception e) {
                    throw new YAMLException(e);
                }
            }
            result = this.createDefaultList(node.getValue().size());
        }
        this.constructSequenceStep2(node, result);
        return result;
    }
    
    protected Set<?> constructSet(final SequenceNode node) {
        Set<Object> result = null;
        Label_0048: {
            if (!node.getType().isInterface()) {
                try {
                    result = (Set<Object>)node.getType().newInstance();
                    break Label_0048;
                }
                catch (Exception e) {
                    throw new YAMLException(e);
                }
            }
            result = this.createDefaultSet(node.getValue().size());
        }
        this.constructSequenceStep2(node, result);
        return result;
    }
    
    protected Object constructArray(final SequenceNode node) {
        return this.constructArrayStep2(node, this.createArray(node.getType(), node.getValue().size()));
    }
    
    protected void constructSequenceStep2(final SequenceNode node, final Collection<Object> collection) {
        for (final Node child : node.getValue()) {
            collection.add(this.constructObject(child));
        }
    }
    
    protected Object constructArrayStep2(final SequenceNode node, final Object array) {
        int index = 0;
        for (final Node child : node.getValue()) {
            Array.set(array, index++, this.constructObject(child));
        }
        return array;
    }
    
    protected Map<Object, Object> createDefaultMap() {
        return new LinkedHashMap<Object, Object>();
    }
    
    protected Set<Object> createDefaultSet() {
        return new LinkedHashSet<Object>();
    }
    
    protected Set<Object> constructSet(final MappingNode node) {
        final Set<Object> set = this.createDefaultSet();
        this.constructSet2ndStep(node, set);
        return set;
    }
    
    protected Map<Object, Object> constructMapping(final MappingNode node) {
        final Map<Object, Object> mapping = this.createDefaultMap();
        this.constructMapping2ndStep(node, mapping);
        return mapping;
    }
    
    protected void constructMapping2ndStep(final MappingNode node, final Map<Object, Object> mapping) {
        final List<NodeTuple> nodeValue = node.getValue();
        for (final NodeTuple tuple : nodeValue) {
            final Node keyNode = tuple.getKeyNode();
            final Node valueNode = tuple.getValueNode();
            final Object key = this.constructObject(keyNode);
            if (key != null) {
                try {
                    key.hashCode();
                }
                catch (Exception e) {
                    throw new ConstructorException("while constructing a mapping", node.getStartMark(), "found unacceptable key " + key, tuple.getKeyNode().getStartMark(), e);
                }
            }
            final Object value = this.constructObject(valueNode);
            if (keyNode.isTwoStepsConstruction()) {
                this.maps2fill.add(0, new RecursiveTuple<Map<Object, Object>, RecursiveTuple<Object, Object>>(mapping, new RecursiveTuple<Object, Object>(key, value)));
            }
            else {
                mapping.put(key, value);
            }
        }
    }
    
    protected void constructSet2ndStep(final MappingNode node, final Set<Object> set) {
        final List<NodeTuple> nodeValue = node.getValue();
        for (final NodeTuple tuple : nodeValue) {
            final Node keyNode = tuple.getKeyNode();
            final Object key = this.constructObject(keyNode);
            if (key != null) {
                try {
                    key.hashCode();
                }
                catch (Exception e) {
                    throw new ConstructorException("while constructing a Set", node.getStartMark(), "found unacceptable key " + key, tuple.getKeyNode().getStartMark(), e);
                }
            }
            if (keyNode.isTwoStepsConstruction()) {
                this.sets2fill.add(0, new RecursiveTuple<Set<Object>, Object>(set, key));
            }
            else {
                set.add(key);
            }
        }
    }
    
    public void setPropertyUtils(final PropertyUtils propertyUtils) {
        this.propertyUtils = propertyUtils;
        this.explicitPropertyUtils = true;
    }
    
    public final PropertyUtils getPropertyUtils() {
        if (this.propertyUtils == null) {
            this.propertyUtils = new PropertyUtils();
        }
        return this.propertyUtils;
    }
    
    public final boolean isExplicitPropertyUtils() {
        return this.explicitPropertyUtils;
    }
    
    private static class RecursiveTuple<T, K>
    {
        private final T _1;
        private final K _2;
        
        public RecursiveTuple(final T _1, final K _2) {
            this._1 = _1;
            this._2 = _2;
        }
        
        public K _2() {
            return this._2;
        }
        
        public T _1() {
            return this._1;
        }
    }
}
