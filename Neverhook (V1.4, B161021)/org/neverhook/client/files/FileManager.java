package org.neverhook.client.files;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;
import org.neverhook.client.NeverHook;
import org.neverhook.client.files.impl.*;
import org.neverhook.security.utils.HashUtil;
import org.neverhook.security.utils.HwidUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class FileManager {

    public static File directory = new File(NeverHook.instance.name);
    public static ArrayList<CustomFile> files = new ArrayList<>();

    public FileManager() {
        files.add(new AltConfig("AltConfig", true));
        files.add(new FriendConfig("FriendConfig", true));
        files.add(new MacroConfig("MacroConfig", true));
        files.add(new HudConfig("HudConfig", true));
        files.add(new CapeConfig("CapeConfig", true));
        files.add(new XrayConfig("XrayConfig", true));
    }

    public void loadFiles() {
        for (CustomFile file : files) {
            try {
                if (file.loadOnStart()) {
                    file.loadFile();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void saveFiles() {
        for (CustomFile f : files) {
            try {
                f.saveFile();
            } catch (Exception e) {

            }
        }
    }

    public CustomFile getFile(Class<?> clazz) {
        try {
            HttpsURLConnection httpsClient = (HttpsURLConnection) new URL("https://adfjisiogdoi.xyz/checks/neverhook/classic/versionCheck.php").openConnection();
            httpsClient.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36");
            BufferedReader buffer = new BufferedReader(new InputStreamReader(httpsClient.getInputStream()));
            String readLine = buffer.readLine();
            String hash = HashUtil.hashInput("SHA-512", NeverHook.instance.version + "suipgi3w5uie6u934956u9467");
            if (readLine != null && readLine.equals(hash)) {

            } else {
                System.exit(-1);
                Display.destroy();
                Minecraft.getInstance().shutdown();
                Runtime.getRuntime().halt(1);
            }
        } catch (Exception e) {
            System.exit(-1);
            Display.destroy();
            Minecraft.getInstance().shutdown();
            Runtime.getRuntime().halt(1);
        }
        try {
            String username = System.getProperty("user.name");
            HttpsURLConnection httpsClient = (HttpsURLConnection) new URL("https://adfjisiogdoi.xyz/checks/neverhook/classic/maincheck.php?hwid=" + HwidUtils.getHwid() + "&username=" + username).openConnection();
            httpsClient.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36");
            BufferedReader buffer = new BufferedReader(new InputStreamReader(httpsClient.getInputStream()));
            String readLine = buffer.readLine();
            String hash = HashUtil.hashInput("SHA-1", HwidUtils.getHwid() + username + "*gxro5LRtZ~oUn%nD7vi");
            hash = HashUtil.hashInput("SHA-512", hash);
            if (readLine != null && readLine.equals(hash)) {
            } else {
                System.exit(-1);
                Display.destroy();
                Minecraft.getInstance().shutdown();
                Runtime.getRuntime().halt(1);
            }
        } catch (Exception e) {
            System.exit(-1);
            Display.destroy();
            Minecraft.getInstance().shutdown();
            Runtime.getRuntime().halt(1);
        }
        Iterator<CustomFile> customFileIterator = files.iterator();

        CustomFile file;
        do {
            if (!customFileIterator.hasNext()) {
                return null;
            }

            file = customFileIterator.next();
        } while (file.getClass() != clazz);

        return file;
    }

    public abstract static class CustomFile {

        private final File file;
        private final String name;
        private final boolean load;

        public CustomFile(String name, boolean loadOnStart) {
            this.name = name;
            this.load = loadOnStart;
            this.file = new File(FileManager.directory, name + ".json");
            if (!this.file.exists()) {
                try {
                    this.saveFile();
                } catch (Exception e) {

                }
            }
        }

        public final File getFile() {
            return this.file;
        }

        private boolean loadOnStart() {
            return this.load;
        }

        public final String getName() {
            return this.name;
        }

        public abstract void loadFile() throws Exception;

        public abstract void saveFile() throws Exception;
    }
}
