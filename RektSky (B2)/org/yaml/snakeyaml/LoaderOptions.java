package org.yaml.snakeyaml;

public class LoaderOptions
{
    private TypeDescription rootTypeDescription;
    
    public LoaderOptions() {
        this(new TypeDescription(Object.class));
    }
    
    public LoaderOptions(final TypeDescription rootTypeDescription) {
        this.rootTypeDescription = rootTypeDescription;
    }
    
    public TypeDescription getRootTypeDescription() {
        return this.rootTypeDescription;
    }
    
    public void setRootTypeDescription(final TypeDescription rootTypeDescription) {
        this.rootTypeDescription = rootTypeDescription;
    }
}
