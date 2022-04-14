package shadersmod.client;

import java.io.InputStream;

public class ShaderPackNone implements IShaderPack {
   public String getName() {
      return Shaders.packNameNone;
   }

   public boolean hasDirectory(String var1) {
      return false;
   }

   public InputStream getResourceAsStream(String var1) {
      return null;
   }

   public void close() {
   }
}
