package joptsimple;

import java.util.*;

public interface OptionDescriptor
{
    List<String> options();
    
    String description();
    
    List<?> defaultValues();
    
    boolean isRequired();
    
    boolean acceptsArguments();
    
    boolean requiresArgument();
    
    String argumentDescription();
    
    String argumentTypeIndicator();
    
    boolean representsNonOptions();
}
