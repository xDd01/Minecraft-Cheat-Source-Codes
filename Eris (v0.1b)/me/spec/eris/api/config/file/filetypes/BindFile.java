package me.spec.eris.api.config.file.filetypes;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import me.spec.eris.Eris;
import me.spec.eris.api.config.file.DataFile;
import me.spec.eris.api.module.Module;
import me.spec.eris.utils.file.FileUtils;

public class BindFile extends DataFile {

    public BindFile() {
        super("Binds.eriscnf");
        this.load();
    }

    @Override
    public void save() {
        ArrayList<String> toWrite = new ArrayList<String>();
        for (Module m : Eris.INSTANCE.moduleManager.getModules()) {
            if (m.getKey() != Keyboard.KEY_NONE && !toWrite.contains(m.getName() + ":" + m.getKey())) {
                toWrite.add(m.getName() + ":" + m.getKey());
            }
        }
        FileUtils.writeToFile(this.file, toWrite);
    }

    @Override
    public void load() {
        ArrayList<String> lines = FileUtils.getLines(this.file);
        for (int k = 0; k < lines.size(); k++) {
            if (lines.get(k).contains(":")) {
                String[] args = lines.get(k).split(":");
                if (args[0] != null && args != null) {
                    if (Eris.INSTANCE.moduleManager.getModuleByName(args[0]) != null) {
                        Module m = Eris.INSTANCE.moduleManager.getModuleByName(args[0]);
                        if (m != null && args.length > 1) {
                            try {
                                m.setKey(Integer.parseInt(args[1]), false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
}
