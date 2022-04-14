package me.spec.eris.api.config.file.filetypes;

import java.util.ArrayList;

import me.spec.eris.Eris;
import me.spec.eris.api.config.file.DataFile;
import me.spec.eris.client.ui.alts.Alt;
import me.spec.eris.client.ui.alts.AltManager;
import me.spec.eris.utils.file.FileUtils;

public class AltsFile extends DataFile {

    public AltsFile() {
        super("Alts.eriscnf");
        this.load();
    }

    @Override
    public void save() {
        ArrayList<String> toWrite = new ArrayList<String>();
        for (int k = 0; k <  Eris.getInstance().altManager.getManagerArraylist().size(); k++) {
            toWrite.add( Eris.getInstance().altManager.getManagerArraylist().get(k).getUser() + ":" +  Eris.getInstance().altManager.getManagerArraylist().get(k).getPass());
        }

        if (!toWrite.isEmpty()) {
            FileUtils.writeToFile(this.file, toWrite);
        }
    }

    @Override
    public void load() {
        ArrayList<String> lines = FileUtils.getLines(this.file);
        for (int k = 0; k < lines.size(); k++) {
            if (lines.get(k).contains(":")) {
                String username = "";
                if (lines.get(k).split(":").length > 0) {
                    username = lines.get(k).split(":")[0];
                }
                String pass = "";
                if (lines.get(k).split(":").length > 1) {
                    pass = lines.get(k).split(":")[1];
                }
                Eris.getInstance().altManager.getManagerArraylist().add(new Alt(username == null ? "" : username, pass == null ? "" : pass));
            }
        }
    }
}
