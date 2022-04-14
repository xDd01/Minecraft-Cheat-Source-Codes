package joptsimple;

public interface ValueConverter<V>
{
    V convert(final String p0);
    
    Class<? extends V> valueType();
    
    String valuePattern();
}
