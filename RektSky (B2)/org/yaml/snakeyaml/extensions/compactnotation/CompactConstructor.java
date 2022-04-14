package org.yaml.snakeyaml.extensions.compactnotation;

import org.yaml.snakeyaml.error.*;
import org.yaml.snakeyaml.introspector.*;
import java.util.regex.*;
import org.yaml.snakeyaml.nodes.*;
import java.util.*;
import java.beans.*;
import org.yaml.snakeyaml.constructor.*;

public class CompactConstructor extends Constructor
{
    private static final Pattern FIRST_PATTERN;
    private static final Pattern PROPERTY_NAME_PATTERN;
    
    @Override
    protected Object constructScalar(final ScalarNode node) {
        final CompactData data = this.getCompactData(node.getValue());
        if (data != null) {
            return this.constructCompactFormat(node, data);
        }
        return super.constructScalar(node);
    }
    
    protected Object constructCompactFormat(final ScalarNode node, final CompactData data) {
        try {
            final Object obj = this.createInstance(node, data);
            final Map<String, Object> properties = new HashMap<String, Object>(data.getProperties());
            this.setProperties(obj, properties);
            return obj;
        }
        catch (Exception e) {
            throw new YAMLException(e);
        }
    }
    
    protected Object createInstance(final ScalarNode node, final CompactData data) throws Exception {
        final Class<?> clazz = this.getClassForName(data.getPrefix());
        final Class<?>[] args = (Class<?>[])new Class[data.getArguments().size()];
        for (int i = 0; i < args.length; ++i) {
            args[i] = String.class;
        }
        final java.lang.reflect.Constructor<?> c = clazz.getDeclaredConstructor(args);
        c.setAccessible(true);
        return c.newInstance(data.getArguments().toArray());
    }
    
    protected void setProperties(final Object bean, final Map<String, Object> data) throws Exception {
        if (data == null) {
            throw new NullPointerException("Data for Compact Object Notation cannot be null.");
        }
        for (final String key : data.keySet()) {
            final Property property = this.getPropertyUtils().getProperty(bean.getClass(), key);
            try {
                property.set(bean, data.get(key));
            }
            catch (IllegalArgumentException e) {
                throw new YAMLException("Cannot set property='" + key + "' with value='" + data.get(key) + "' (" + data.get(key).getClass() + ") in " + bean);
            }
        }
    }
    
    public CompactData getCompactData(final String scalar) {
        if (!scalar.endsWith(")")) {
            return null;
        }
        if (scalar.indexOf(40) < 0) {
            return null;
        }
        final Matcher m = CompactConstructor.FIRST_PATTERN.matcher(scalar);
        if (!m.matches()) {
            return null;
        }
        final String tag = m.group(1).trim();
        final String content = m.group(3);
        final CompactData data = new CompactData(tag);
        if (content.length() == 0) {
            return data;
        }
        final String[] names = content.split("\\s*,\\s*");
        for (int i = 0; i < names.length; ++i) {
            final String section = names[i];
            if (section.indexOf(61) < 0) {
                data.getArguments().add(section);
            }
            else {
                final Matcher sm = CompactConstructor.PROPERTY_NAME_PATTERN.matcher(section);
                if (!sm.matches()) {
                    return null;
                }
                final String name = sm.group(1);
                final String value = sm.group(2).trim();
                data.getProperties().put(name, value);
            }
        }
        return data;
    }
    
    @Override
    protected Construct getConstructor(final Node node) {
        if (node instanceof MappingNode) {
            final MappingNode mnode = (MappingNode)node;
            final List<NodeTuple> list = mnode.getValue();
            if (list.size() == 1) {
                final NodeTuple tuple = list.get(0);
                final Node key = tuple.getKeyNode();
                if (key instanceof ScalarNode) {
                    final ScalarNode scalar = (ScalarNode)key;
                    final CompactData data = this.getCompactData(scalar.getValue());
                    if (data != null) {
                        return new ConstructCompactObject();
                    }
                }
            }
        }
        return super.getConstructor(node);
    }
    
    protected void applySequence(final Object bean, final List<?> value) {
        try {
            final Property property = this.getPropertyUtils().getProperty(bean.getClass(), this.getSequencePropertyName(bean.getClass()));
            property.set(bean, value);
        }
        catch (Exception e) {
            throw new YAMLException(e);
        }
    }
    
    protected String getSequencePropertyName(final Class<?> bean) throws IntrospectionException {
        final Set<Property> properties = this.getPropertyUtils().getProperties(bean);
        final Iterator<Property> iterator = properties.iterator();
        while (iterator.hasNext()) {
            final Property property = iterator.next();
            if (!List.class.isAssignableFrom(property.getType())) {
                iterator.remove();
            }
        }
        if (properties.size() == 0) {
            throw new YAMLException("No list property found in " + bean);
        }
        if (properties.size() > 1) {
            throw new YAMLException("Many list properties found in " + bean + "; Please override getSequencePropertyName() to specify which property to use.");
        }
        return properties.iterator().next().getName();
    }
    
    static {
        FIRST_PATTERN = Pattern.compile("(\\p{Alpha}.*)(\\s*)\\((.*?)\\)");
        PROPERTY_NAME_PATTERN = Pattern.compile("\\s*(\\p{Alpha}\\w*)\\s*=(.+)");
    }
    
    public class ConstructCompactObject extends AbstractConstruct
    {
        public Object construct(final Node node) {
            final Map<Object, Object> map = BaseConstructor.this.constructMapping((MappingNode)node);
            final Map.Entry<Object, Object> entry = map.entrySet().iterator().next();
            final Object result = entry.getKey();
            final Object value = entry.getValue();
            if (value instanceof Map) {
                final Map<String, Object> properties = (Map<String, Object>)value;
                try {
                    CompactConstructor.this.setProperties(result, properties);
                }
                catch (Exception e) {
                    throw new YAMLException(e);
                }
            }
            else {
                CompactConstructor.this.applySequence(result, (List<?>)value);
            }
            return result;
        }
    }
}
