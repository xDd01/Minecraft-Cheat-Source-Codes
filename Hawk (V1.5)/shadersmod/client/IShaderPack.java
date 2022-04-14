package shadersmod.client;

import java.io.InputStream;

public interface IShaderPack {
   boolean hasDirectory(String var1);

   void close();

   String getName();

   InputStream getResourceAsStream(String var1);
}
