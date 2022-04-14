package shadersmod.client;

public class PropertyDefaultTrueFalse extends Property
{
    public static final String[] PROPERTY_VALUES;
    public static final String[] USER_VALUES;
    
    public PropertyDefaultTrueFalse(final String propertyName, final String userName, final int defaultValue) {
        super(propertyName, PropertyDefaultTrueFalse.PROPERTY_VALUES, userName, PropertyDefaultTrueFalse.USER_VALUES, defaultValue);
    }
    
    public boolean isDefault() {
        return this.getValue() == 0;
    }
    
    public boolean isTrue() {
        return this.getValue() == 1;
    }
    
    public boolean isFalse() {
        return this.getValue() == 2;
    }
    
    static {
        PROPERTY_VALUES = new String[] { "default", "true", "false" };
        USER_VALUES = new String[] { "Default", "ON", "OFF" };
    }
}
