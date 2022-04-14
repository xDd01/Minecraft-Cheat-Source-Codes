package org.yaml.snakeyaml;

import org.yaml.snakeyaml.introspector.*;
import org.yaml.snakeyaml.representer.*;
import org.yaml.snakeyaml.resolver.*;
import org.yaml.snakeyaml.constructor.*;
import java.io.*;
import org.yaml.snakeyaml.reader.*;

public class JavaBeanLoader<T>
{
    private Yaml loader;
    
    public JavaBeanLoader(final TypeDescription typeDescription) {
        this(typeDescription, BeanAccess.DEFAULT);
    }
    
    public JavaBeanLoader(final TypeDescription typeDescription, final BeanAccess beanAccess) {
        this(new LoaderOptions(typeDescription), beanAccess);
    }
    
    public JavaBeanLoader(final LoaderOptions options, final BeanAccess beanAccess) {
        if (options == null) {
            throw new NullPointerException("LoaderOptions must be provided.");
        }
        if (options.getRootTypeDescription() == null) {
            throw new NullPointerException("TypeDescription must be provided.");
        }
        final Constructor constructor = new Constructor(options.getRootTypeDescription().getType());
        options.getRootTypeDescription().setRoot(true);
        constructor.addTypeDescription(options.getRootTypeDescription());
        (this.loader = new Yaml(constructor, options, new Representer(), new DumperOptions(), new Resolver())).setBeanAccess(beanAccess);
    }
    
    public <S extends T> JavaBeanLoader(final Class<S> clazz, final BeanAccess beanAccess) {
        this(new TypeDescription(clazz), beanAccess);
    }
    
    public <S extends T> JavaBeanLoader(final Class<S> clazz) {
        this(clazz, BeanAccess.DEFAULT);
    }
    
    public T load(final String yaml) {
        return (T)this.loader.load(new StringReader(yaml));
    }
    
    public T load(final InputStream io) {
        return (T)this.loader.load(new UnicodeReader(io));
    }
    
    public T load(final Reader io) {
        return (T)this.loader.load(io);
    }
}
