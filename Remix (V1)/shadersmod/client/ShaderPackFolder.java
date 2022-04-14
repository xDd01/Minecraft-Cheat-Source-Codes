package shadersmod.client;

import optifine.*;
import java.io.*;

public class ShaderPackFolder implements IShaderPack
{
    protected File packFile;
    
    public ShaderPackFolder(final String name, final File file) {
        this.packFile = file;
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public InputStream getResourceAsStream(final String resName) {
        try {
            final String excp = StrUtils.removePrefixSuffix(resName, "/", "/");
            final File resFile = new File(this.packFile, excp);
            InputStream inputStream;
            if (!resFile.exists()) {
                inputStream = null;
            }
            else {
                final FileInputStream fileInputStream;
                inputStream = new BufferedInputStream(fileInputStream);
                fileInputStream = new FileInputStream(resFile);
            }
            return inputStream;
        }
        catch (Exception var4) {
            return null;
        }
    }
    
    @Override
    public boolean hasDirectory(final String name) {
        final File resFile = new File(this.packFile, name.substring(1));
        return resFile.exists() && resFile.isDirectory();
    }
    
    @Override
    public String getName() {
        return this.packFile.getName();
    }
}
