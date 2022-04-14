package shadersmod.client;

public class PropertyDefaultTrueFalse extends Property {
   public static final String[] PROPERTY_VALUES = new String[]{"default", "true", "false"};
   public static final String[] USER_VALUES = new String[]{"Default", "ON", "OFF"};

   public PropertyDefaultTrueFalse(String var1, String var2, int var3) {
      super(var1, PROPERTY_VALUES, var2, USER_VALUES, var3);
   }

   public boolean isTrue() {
      return this.getValue() == 1;
   }

   public boolean isDefault() {
      return this.getValue() == 0;
   }

   public boolean isFalse() {
      return this.getValue() == 2;
   }
}
