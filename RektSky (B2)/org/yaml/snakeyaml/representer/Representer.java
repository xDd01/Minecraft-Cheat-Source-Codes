package org.yaml.snakeyaml.representer;

import org.yaml.snakeyaml.introspector.*;
import org.yaml.snakeyaml.nodes.*;
import java.util.*;
import java.beans.*;
import org.yaml.snakeyaml.error.*;

public class Representer extends SafeRepresenter
{
    public Representer() {
        this.representers.put(null, new RepresentJavaBean());
    }
    
    protected MappingNode representJavaBean(final Set<Property> properties, final Object javaBean) {
        final List<NodeTuple> value = new ArrayList<NodeTuple>(properties.size());
        final Tag customTag = this.classTags.get(javaBean.getClass());
        final Tag tag = (customTag != null) ? customTag : new Tag(javaBean.getClass());
        final MappingNode node = new MappingNode(tag, value, null);
        this.representedObjects.put(javaBean, node);
        boolean bestStyle = true;
        for (final Property property : properties) {
            final Object memberValue = property.get(javaBean);
            final Tag customPropertyTag = (memberValue == null) ? null : this.classTags.get(memberValue.getClass());
            final NodeTuple tuple = this.representJavaBeanProperty(javaBean, property, memberValue, customPropertyTag);
            if (tuple == null) {
                continue;
            }
            if (((ScalarNode)tuple.getKeyNode()).getStyle() != null) {
                bestStyle = false;
            }
            final Node nodeValue = tuple.getValueNode();
            if (!(nodeValue instanceof ScalarNode) || ((ScalarNode)nodeValue).getStyle() != null) {
                bestStyle = false;
            }
            value.add(tuple);
        }
        if (this.defaultFlowStyle != null) {
            node.setFlowStyle(this.defaultFlowStyle);
        }
        else {
            node.setFlowStyle(bestStyle);
        }
        return node;
    }
    
    protected NodeTuple representJavaBeanProperty(final Object javaBean, final Property property, final Object propertyValue, final Tag customTag) {
        final ScalarNode nodeKey = (ScalarNode)this.representData(property.getName());
        final boolean hasAlias = this.representedObjects.containsKey(propertyValue);
        final Node nodeValue = this.representData(propertyValue);
        if (propertyValue != null && !hasAlias) {
            final NodeId nodeId = nodeValue.getNodeId();
            if (customTag == null) {
                if (nodeId == NodeId.scalar) {
                    if (propertyValue instanceof Enum) {
                        nodeValue.setTag(Tag.STR);
                    }
                }
                else {
                    if (nodeId == NodeId.mapping) {
                        if (property.getType() == propertyValue.getClass() && !(propertyValue instanceof Map) && !nodeValue.getTag().equals(Tag.SET)) {
                            nodeValue.setTag(Tag.MAP);
                        }
                        this.withCheckedTag.put(nodeValue, null);
                    }
                    this.checkGlobalTag(property, nodeValue, propertyValue);
                }
            }
            else if (nodeId == NodeId.mapping) {
                this.withCheckedTag.put(nodeValue, null);
            }
        }
        return new NodeTuple(nodeKey, nodeValue);
    }
    
    protected void checkGlobalTag(final Property property, final Node node, final Object object) {
        final Class<?>[] arguments = property.getActualTypeArguments();
        if (arguments != null) {
            if (node.getNodeId() == NodeId.sequence) {
                final Class<?> t = arguments[0];
                final SequenceNode snode = (SequenceNode)node;
                Iterable<Object> memberList;
                if (object.getClass().isArray()) {
                    memberList = Arrays.asList((Object[])object);
                }
                else {
                    memberList = (Iterable<Object>)object;
                }
                final Iterator<Object> iter = memberList.iterator();
                for (final Node childNode : snode.getValue()) {
                    final Object member = iter.next();
                    if (member != null) {
                        if (t.equals(member.getClass()) && childNode.getNodeId() == NodeId.mapping && !this.withCheckedTag.containsKey(childNode)) {
                            childNode.setTag(Tag.MAP);
                        }
                        this.withCheckedTag.put(childNode, null);
                    }
                }
            }
            else if (object instanceof Set) {
                final Class<?> t = arguments[0];
                final MappingNode mnode = (MappingNode)node;
                final Iterator<NodeTuple> iter2 = mnode.getValue().iterator();
                final Set<?> set = (Set<?>)object;
                for (final Object member2 : set) {
                    final NodeTuple tuple = iter2.next();
                    final Node keyNode = tuple.getKeyNode();
                    if (t.equals(member2.getClass()) && keyNode.getNodeId() == NodeId.mapping && !this.withCheckedTag.containsKey(keyNode)) {
                        keyNode.setTag(Tag.MAP);
                    }
                    this.withCheckedTag.put(keyNode, null);
                }
            }
            else {
                final Class<?> keyType = arguments[0];
                final Class<?> valueType = arguments[1];
                final MappingNode mnode2 = (MappingNode)node;
                for (final NodeTuple tuple2 : mnode2.getValue()) {
                    this.resetTag(keyType, tuple2.getKeyNode());
                    this.resetTag(valueType, tuple2.getValueNode());
                }
            }
        }
    }
    
    private void resetTag(final Class<?> type, final Node node) {
        final Tag tag = node.getTag();
        if (tag.matches(type)) {
            if (Enum.class.isAssignableFrom(type)) {
                node.setTag(Tag.STR);
            }
            else if (!this.withCheckedTag.containsKey(node)) {
                node.setTag(Tag.MAP);
                this.withCheckedTag.put(node, null);
            }
        }
    }
    
    protected Set<Property> getProperties(final Class<?> type) throws IntrospectionException {
        return this.getPropertyUtils().getProperties(type);
    }
    
    protected class RepresentJavaBean implements Represent
    {
        public Node representData(final Object data) {
            try {
                return Representer.this.representJavaBean(Representer.this.getProperties(data.getClass()), data);
            }
            catch (IntrospectionException e) {
                throw new YAMLException(e);
            }
        }
    }
}
