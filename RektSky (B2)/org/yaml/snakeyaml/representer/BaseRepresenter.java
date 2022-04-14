package org.yaml.snakeyaml.representer;

import org.yaml.snakeyaml.introspector.*;
import org.yaml.snakeyaml.serializer.*;
import java.io.*;
import org.yaml.snakeyaml.error.*;
import java.util.*;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.*;

public abstract class BaseRepresenter
{
    protected final Map<Class, Represent> representers;
    protected Represent nullRepresenter;
    protected final Map<Class, Represent> multiRepresenters;
    private Character defaultStyle;
    protected Boolean defaultFlowStyle;
    protected final Map<Object, Node> representedObjects;
    protected final Map<Node, ?> withCheckedTag;
    protected Object objectToRepresent;
    private PropertyUtils propertyUtils;
    private boolean explicitPropertyUtils;
    
    public BaseRepresenter() {
        this.representers = new HashMap<Class, Represent>();
        this.multiRepresenters = new LinkedHashMap<Class, Represent>();
        this.representedObjects = new IdentityHashMap<Object, Node>();
        this.withCheckedTag = new IdentityHashMap<Node, Object>();
        this.explicitPropertyUtils = false;
    }
    
    public void represent(final Serializer serializer, final Object data) throws IOException {
        final Node node = this.representData(data);
        serializer.serialize(node);
        this.representedObjects.clear();
        this.withCheckedTag.clear();
        this.objectToRepresent = null;
    }
    
    protected Node representData(final Object data) {
        this.objectToRepresent = data;
        if (this.representedObjects.containsKey(this.objectToRepresent)) {
            final Node node = this.representedObjects.get(this.objectToRepresent);
            return node;
        }
        if (data == null) {
            final Node node = this.nullRepresenter.representData(data);
            return node;
        }
        final Class clazz = data.getClass();
        Node node;
        if (this.representers.containsKey(clazz)) {
            final Represent representer = this.representers.get(clazz);
            node = representer.representData(data);
        }
        else {
            for (final Class repr : this.multiRepresenters.keySet()) {
                if (repr.isInstance(data)) {
                    final Represent representer2 = this.multiRepresenters.get(repr);
                    node = representer2.representData(data);
                    return node;
                }
            }
            if (clazz.isArray()) {
                throw new YAMLException("Arrays of primitives are not fully supported.");
            }
            if (this.multiRepresenters.containsKey(null)) {
                final Represent representer = this.multiRepresenters.get(null);
                node = representer.representData(data);
            }
            else {
                final Represent representer = this.representers.get(null);
                node = representer.representData(data);
            }
        }
        return node;
    }
    
    protected Node representScalar(final Tag tag, final String value, Character style) {
        if (style == null) {
            style = this.defaultStyle;
        }
        final Node node = new ScalarNode(tag, value, null, null, style);
        return node;
    }
    
    protected Node representScalar(final Tag tag, final String value) {
        return this.representScalar(tag, value, null);
    }
    
    protected Node representSequence(final Tag tag, final Iterable<?> sequence, final Boolean flowStyle) {
        int size = 10;
        if (sequence instanceof List) {
            size = ((List)sequence).size();
        }
        final List<Node> value = new ArrayList<Node>(size);
        final SequenceNode node = new SequenceNode(tag, value, flowStyle);
        this.representedObjects.put(this.objectToRepresent, node);
        boolean bestStyle = true;
        for (final Object item : sequence) {
            final Node nodeItem = this.representData(item);
            if (!(nodeItem instanceof ScalarNode) || ((ScalarNode)nodeItem).getStyle() != null) {
                bestStyle = false;
            }
            value.add(nodeItem);
        }
        if (flowStyle == null) {
            if (this.defaultFlowStyle != null) {
                node.setFlowStyle(this.defaultFlowStyle);
            }
            else {
                node.setFlowStyle(bestStyle);
            }
        }
        return node;
    }
    
    protected Node representMapping(final Tag tag, final Map<?, Object> mapping, final Boolean flowStyle) {
        final List<NodeTuple> value = new ArrayList<NodeTuple>(mapping.size());
        final MappingNode node = new MappingNode(tag, value, flowStyle);
        this.representedObjects.put(this.objectToRepresent, node);
        boolean bestStyle = true;
        for (final Object itemKey : mapping.keySet()) {
            final Object itemValue = mapping.get(itemKey);
            final Node nodeKey = this.representData(itemKey);
            final Node nodeValue = this.representData(itemValue);
            if (!(nodeKey instanceof ScalarNode) || ((ScalarNode)nodeKey).getStyle() != null) {
                bestStyle = false;
            }
            if (!(nodeValue instanceof ScalarNode) || ((ScalarNode)nodeValue).getStyle() != null) {
                bestStyle = false;
            }
            value.add(new NodeTuple(nodeKey, nodeValue));
        }
        if (flowStyle == null) {
            if (this.defaultFlowStyle != null) {
                node.setFlowStyle(this.defaultFlowStyle);
            }
            else {
                node.setFlowStyle(bestStyle);
            }
        }
        return node;
    }
    
    public void setDefaultScalarStyle(final DumperOptions.ScalarStyle defaultStyle) {
        this.defaultStyle = defaultStyle.getChar();
    }
    
    public void setDefaultFlowStyle(final DumperOptions.FlowStyle defaultFlowStyle) {
        this.defaultFlowStyle = defaultFlowStyle.getStyleBoolean();
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
}
