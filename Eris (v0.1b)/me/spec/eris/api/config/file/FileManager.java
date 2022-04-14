package me.spec.eris.api.config.file;

import java.io.File;
import java.util.ArrayList;

import me.spec.eris.api.config.file.filetypes.AltsFile;
import me.spec.eris.api.config.file.filetypes.BindFile;
import me.spec.eris.api.config.file.filetypes.CustomHUDFile;
import me.spec.eris.api.config.file.filetypes.TimePlayedFile;
import net.minecraft.client.Minecraft;

public class FileManager {

    private ArrayList<DataFile> files = new ArrayList<DataFile>();
    public static File dir;
    public File configDir;
    public static File hitsDir;

    public FileManager() {
        dir = new File(Minecraft.getMinecraft().mcDataDir, "Eris");
        if (!dir.exists()) {
            dir.mkdir();
        }
        configDir = new File(dir, "configs");
        if (!configDir.exists()) {
            configDir.mkdir();
        }
        files.add(new TimePlayedFile());
        files.add(new AltsFile());
        files.add(new BindFile());
        files.add(new CustomHUDFile());
    }

    public void saveTimePlayedFile() {
        files.get(0).save();
    }

    public void saveAltsFile() {
        files.get(1).save();
    }

    public DataFile getBindsFile() {
        return files.get(2);
    }

    public DataFile getCustomHUDFile() {
        return files.get(3);
    }

    public DataFile getDataFile(String fileName) {
        for (int k = 0; k < files.size(); k++) {
            DataFile df = files.get(k);
            if (df.getName() != null && df.getName().equalsIgnoreCase(fileName) || (df.getName().contains(".") && df.getName().split(".")[0].equalsIgnoreCase(fileName))) {
                return df;
            }
        }
        return null;
    }

    public ArrayList<DataFile> files() {
        return this.files;
    }

    public ArrayList<File> getConfigs() {
        ArrayList<File> c = new ArrayList<File>();
        for (File f : configDir.listFiles()) {
            if (f.getName().toLowerCase().endsWith(".eriscnf")) {
                c.add(f);
            }
        }
        return c;
    }

    public void saveDataFile() {
    }
}
