package shadersmod.client;

import optifine.*;
import java.util.zip.*;
import java.io.*;

public class ShaderPackZip implements IShaderPack
{
    protected File packFile;
    protected ZipFile packZipFile;
    
    public ShaderPackZip(final String name, final File file) {
        this.packFile = file;
        this.packZipFile = null;
    }
    
    @Override
    public void close() {
        if (this.packZipFile != null) {
            try {
                this.packZipFile.close();
            }
            catch (Exception ex) {}
            this.packZipFile = null;
        }
    }
    
    @Override
    public InputStream getResourceAsStream(final String resName) {
        try {
            if (this.packZipFile == null) {
                this.packZipFile = new ZipFile(this.packFile);
            }
            final String excp = StrUtils.removePrefix(resName, "/");
            final ZipEntry entry = this.packZipFile.getEntry(excp);
            return (entry == null) ? null : this.packZipFile.getInputStream(entry);
        }
        catch (Exception var4) {
            return null;
        }
    }
    
    @Override
    public boolean hasDirectory(final String resName) {
        try {
            if (this.packZipFile == null) {
                this.packZipFile = new ZipFile(this.packFile);
            }
            final String e = StrUtils.removePrefix(resName, "/");
            final ZipEntry entry = this.packZipFile.getEntry(e);
            return entry != null;
        }
        catch (IOException var4) {
            return false;
        }
    }
    
    @Override
    public String getName() {
        return this.packFile.getName();
    }
}
