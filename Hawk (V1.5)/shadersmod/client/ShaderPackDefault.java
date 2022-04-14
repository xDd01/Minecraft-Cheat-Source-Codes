package shadersmod.client;

import java.io.InputStream;

public class ShaderPackDefault implements IShaderPack {
   public String getName() {
      return Shaders.packNameDefault;
   }

   public boolean hasDirectory(String var1) {
      return false;
   }

   public InputStream getResourceAsStream(String var1) {
      return ShaderPackDefault.class.getResourceAsStream(var1);
   }

   public void close() {
   }
}
