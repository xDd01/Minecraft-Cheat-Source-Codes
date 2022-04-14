package org.yaml.snakeyaml;

import org.yaml.snakeyaml.resolver.*;
import org.yaml.snakeyaml.representer.*;
import org.yaml.snakeyaml.constructor.*;
import java.util.*;
import org.yaml.snakeyaml.emitter.*;
import org.yaml.snakeyaml.serializer.*;
import org.yaml.snakeyaml.error.*;
import org.yaml.snakeyaml.reader.*;
import org.yaml.snakeyaml.composer.*;
import org.yaml.snakeyaml.parser.*;
import java.io.*;
import java.util.regex.*;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.events.*;
import org.yaml.snakeyaml.introspector.*;

public class Yaml
{
    protected final Resolver resolver;
    private String name;
    protected BaseConstructor constructor;
    protected Representer representer;
    protected DumperOptions dumperOptions;
    protected LoaderOptions loaderOptions;
    
    public Yaml() {
        this(new Constructor(), new LoaderOptions(), new Representer(), new DumperOptions(), new Resolver());
    }
    
    public Yaml(final LoaderOptions loaderOptions) {
        this(new Constructor(), loaderOptions, new Representer(), new DumperOptions(), new Resolver());
    }
    
    public Yaml(final DumperOptions dumperOptions) {
        this(new Constructor(), new Representer(), dumperOptions);
    }
    
    public Yaml(final Representer representer) {
        this(new Constructor(), representer);
    }
    
    public Yaml(final BaseConstructor constructor) {
        this(constructor, new Representer());
    }
    
    public Yaml(final BaseConstructor constructor, final Representer representer) {
        this(constructor, representer, new DumperOptions());
    }
    
    public Yaml(final Representer representer, final DumperOptions dumperOptions) {
        this(new Constructor(), representer, dumperOptions, new Resolver());
    }
    
    public Yaml(final BaseConstructor constructor, final Representer representer, final DumperOptions dumperOptions) {
        this(constructor, representer, dumperOptions, new Resolver());
    }
    
    public Yaml(final BaseConstructor constructor, final Representer representer, final DumperOptions dumperOptions, final Resolver resolver) {
        this(constructor, new LoaderOptions(), representer, dumperOptions, resolver);
    }
    
    public Yaml(final BaseConstructor constructor, final LoaderOptions loaderOptions, final Representer representer, final DumperOptions dumperOptions, final Resolver resolver) {
        if (!constructor.isExplicitPropertyUtils()) {
            constructor.setPropertyUtils(representer.getPropertyUtils());
        }
        else if (!representer.isExplicitPropertyUtils()) {
            representer.setPropertyUtils(constructor.getPropertyUtils());
        }
        this.constructor = constructor;
        this.loaderOptions = loaderOptions;
        representer.setDefaultFlowStyle(dumperOptions.getDefaultFlowStyle());
        representer.setDefaultScalarStyle(dumperOptions.getDefaultScalarStyle());
        representer.getPropertyUtils().setAllowReadOnlyProperties(dumperOptions.isAllowReadOnlyProperties());
        this.representer = representer;
        this.dumperOptions = dumperOptions;
        this.resolver = resolver;
        this.name = "Yaml:" + System.identityHashCode(this);
    }
    
    public String dump(final Object data) {
        final List<Object> list = new ArrayList<Object>(1);
        list.add(data);
        return this.dumpAll(list.iterator());
    }
    
    public String dumpAll(final Iterator<?> data) {
        final StringWriter buffer = new StringWriter();
        this.dumpAll(data, buffer);
        return buffer.toString();
    }
    
    public void dump(final Object data, final Writer output) {
        final List<Object> list = new ArrayList<Object>(1);
        list.add(data);
        this.dumpAll(list.iterator(), output);
    }
    
    public void dumpAll(final Iterator<?> data, final Writer output) {
        final Serializer s = new Serializer(new Emitter(output, this.dumperOptions), this.resolver, this.dumperOptions);
        try {
            s.open();
            while (data.hasNext()) {
                this.representer.represent(s, data.next());
            }
            s.close();
        }
        catch (IOException e) {
            throw new YAMLException(e);
        }
    }
    
    public Object load(final String yaml) {
        return this.loadFromReader(new StreamReader(yaml));
    }
    
    public Object load(final InputStream io) {
        return this.loadFromReader(new StreamReader(new UnicodeReader(io)));
    }
    
    public Object load(final Reader io) {
        return this.loadFromReader(new StreamReader(io));
    }
    
    private Object loadFromReader(final StreamReader sreader) {
        final Composer composer = new Composer(new ParserImpl(sreader), this.resolver);
        this.constructor.setComposer(composer);
        return this.constructor.getSingleData();
    }
    
    public Iterable<Object> loadAll(final Reader yaml) {
        final Composer composer = new Composer(new ParserImpl(new StreamReader(yaml)), this.resolver);
        this.constructor.setComposer(composer);
        final Iterator<Object> result = new Iterator<Object>() {
            public boolean hasNext() {
                return Yaml.this.constructor.checkData();
            }
            
            public Object next() {
                return Yaml.this.constructor.getData();
            }
            
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return new YamlIterable(result);
    }
    
    public Iterable<Object> loadAll(final String yaml) {
        return this.loadAll(new StringReader(yaml));
    }
    
    public Iterable<Object> loadAll(final InputStream yaml) {
        return this.loadAll(new UnicodeReader(yaml));
    }
    
    public Node compose(final Reader yaml) {
        final Composer composer = new Composer(new ParserImpl(new StreamReader(yaml)), this.resolver);
        this.constructor.setComposer(composer);
        return composer.getSingleNode();
    }
    
    public Iterable<Node> composeAll(final Reader yaml) {
        final Composer composer = new Composer(new ParserImpl(new StreamReader(yaml)), this.resolver);
        this.constructor.setComposer(composer);
        final Iterator<Node> result = new Iterator<Node>() {
            public boolean hasNext() {
                return composer.checkNode();
            }
            
            public Node next() {
                return composer.getNode();
            }
            
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return new NodeIterable(result);
    }
    
    @Deprecated
    public void addImplicitResolver(final String tag, final Pattern regexp, final String first) {
        this.addImplicitResolver(new Tag(tag), regexp, first);
    }
    
    public void addImplicitResolver(final Tag tag, final Pattern regexp, final String first) {
        this.resolver.addImplicitResolver(tag, regexp, first);
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Iterable<Event> parse(final Reader yaml) {
        final Parser parser = new ParserImpl(new StreamReader(yaml));
        final Iterator<Event> result = new Iterator<Event>() {
            public boolean hasNext() {
                return parser.peekEvent() != null;
            }
            
            public Event next() {
                return parser.getEvent();
            }
            
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return new EventIterable(result);
    }
    
    public void setBeanAccess(final BeanAccess beanAccess) {
        this.constructor.getPropertyUtils().setBeanAccess(beanAccess);
        this.representer.getPropertyUtils().setBeanAccess(beanAccess);
    }
    
    @Deprecated
    public Yaml(final Loader loader) {
        this(loader, new Dumper(new DumperOptions()));
    }
    
    @Deprecated
    public Yaml(final Loader loader, final Dumper dumper) {
        this(loader, dumper, new Resolver());
    }
    
    @Deprecated
    public Yaml(final Loader loader, final Dumper dumper, final Resolver resolver) {
        this(loader.constructor, dumper.representer, dumper.options, resolver);
    }
    
    public Yaml(final Dumper dumper) {
        this(new Constructor(), dumper.representer, dumper.options);
    }
    
    private class YamlIterable implements Iterable<Object>
    {
        private Iterator<Object> iterator;
        
        public YamlIterable(final Iterator<Object> iterator) {
            this.iterator = iterator;
        }
        
        public Iterator<Object> iterator() {
            return this.iterator;
        }
    }
    
    private class NodeIterable implements Iterable<Node>
    {
        private Iterator<Node> iterator;
        
        public NodeIterable(final Iterator<Node> iterator) {
            this.iterator = iterator;
        }
        
        public Iterator<Node> iterator() {
            return this.iterator;
        }
    }
    
    private class EventIterable implements Iterable<Event>
    {
        private Iterator<Event> iterator;
        
        public EventIterable(final Iterator<Event> iterator) {
            this.iterator = iterator;
        }
        
        public Iterator<Event> iterator() {
            return this.iterator;
        }
    }
}
