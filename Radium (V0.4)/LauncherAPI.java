import net.minecraft.client.main.Main;
import net.minecraft.util.Util;

import java.io.File;

public final class LauncherAPI {

    public static void launch(int width, int height, boolean fullscreen) {
        final String userHome = System.getProperty("user.home", ".");
        File workingDirectory = null;
        switch (Util.getOSType()) {
            case LINUX:
                workingDirectory = new File(userHome, ".minecraft/");
                break;
            case WINDOWS:
                final String applicationData = System.getenv("APPDATA");
                final String folder = (applicationData != null) ? applicationData : userHome;
                workingDirectory = new File(folder, ".minecraft/");
                break;
            case OSX:
                workingDirectory = new File(userHome, "Library/Application Support/minecraft");
                break;
            default:
                workingDirectory = new File(userHome, "minecraft/");
                break;
        }
        Main.main(new String[]{
                "--version", "1.8.9",
                "--accessToken", "0",
                "--assetIndex", "1.8",
                "--userProperties", "{}",
                // TODO: UNCOSMMENT ON RELEASE
//                "--gameDir", new File(workingDirectory, ".").getAbsolutePath(),
                "--assetsDir", new File(workingDirectory, "assets/").getAbsolutePath()});
    }
}
