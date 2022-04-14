package org.reflections;

import java.util.*;
import org.reflections.scanners.*;
import java.net.*;
import org.reflections.adapters.*;
import java.util.function.*;
import java.util.concurrent.*;
import org.reflections.serializers.*;

public interface Configuration
{
    Set<Scanner> getScanners();
    
    Set<URL> getUrls();
    
    MetadataAdapter getMetadataAdapter();
    
    Predicate<String> getInputsFilter();
    
    ExecutorService getExecutorService();
    
    Serializer getSerializer();
    
    ClassLoader[] getClassLoaders();
    
    boolean shouldExpandSuperTypes();
}
