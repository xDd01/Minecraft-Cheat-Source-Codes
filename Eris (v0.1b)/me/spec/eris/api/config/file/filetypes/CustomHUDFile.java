package me.spec.eris.api.config.file.filetypes;

import java.util.ArrayList;
import java.util.Objects;

import me.spec.eris.client.modules.render.HUD;
import me.spec.eris.client.ui.hud.element.Element;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import me.spec.eris.Eris;
import me.spec.eris.api.config.file.DataFile;
import me.spec.eris.api.module.Module;
import me.spec.eris.utils.file.FileUtils;

public class CustomHUDFile extends DataFile {

    public CustomHUDFile() {
        super("CustomUI.eriscnf");
    }

    @Override
    public void save() {
        ArrayList<String> toWrite = new ArrayList<String>();
        if(!Eris.getInstance().customHUDManager.getManagerArraylist().isEmpty() && Eris.getInstance().customHUDManager.getManagerArraylist() != null) {
            for(Element element : Eris.getInstance().customHUDManager.getManagerArraylist()) {
                toWrite.add(Eris.getInstance().customHUDManager.getManagerArraylist().indexOf(element) + ":" + element.x + ":" + element.y);
            }
        } else {
            System.out.println("HELL NAW");
        }
        FileUtils.writeToFile(this.file, toWrite);
    }

    @Override
    public void load() {
        if(Minecraft.getMinecraft().theWorld != null) {
            if(!Eris.getInstance().getModuleManager().getModuleByClass(HUD.class).isToggled()) {
                Eris.getInstance().getModuleManager().getModuleByClass(HUD.class).toggle(true);
            }
        }
        ArrayList<String> lines = FileUtils.getLines(this.file);
        for (int k = 0; k < lines.size(); k++) {
            if (lines.get(k).contains(":")) {
                String[] args = lines.get(k).split(":");

                int index = Integer.parseInt(args[0]);
                int x = Integer.parseInt(args[1]);
                int y = Integer.parseInt(args[2]);

                if(Objects.nonNull(Eris.getInstance().customHUDManager.getManagerArraylist().get(index))) {
                    if(x != 0 && y != 0) {
                        Eris.getInstance().customHUDManager.getManagerArraylist().get(index).x = x;
                        Eris.getInstance().customHUDManager.getManagerArraylist().get(index).y = y;
                    } else {
                        Eris.getInstance().customHUDManager.getManagerArraylist().get(index).x = 50;
                        Eris.getInstance().customHUDManager.getManagerArraylist().get(index).y = 50;
                    }
                }
            }
        }
    }
}