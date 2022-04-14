package me.spec.eris.client.security.checks;

public class AntiVM {

    public static boolean run() {
        if (net.minecraft.util.Util.getOSType() != net.minecraft.util.Util.EnumOS.WINDOWS) return false;
        if (!run("wmic computersystem get model", "Model", new String[]{"virtualbox", "vmware", "kvm", "hyper-v"}) || !run("WMIC BIOS GET SERIALNUMBER", "SerialNumber", new String[]{"0"}) || !run("wmic baseboard get Manufacturer", "Manufacturer", new String[]{"Microsoft Corporation"}))
            return false;

        return true;
    }

    private static boolean run(String command, String startsWith, String[] closePhrase) {
        try {
            Process p = Runtime.getRuntime().exec(command);
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
            String line = br.readLine();
            while (line != null) {
                if (!line.startsWith(startsWith) && !line.equals("")) {
                    String model = line.replaceAll(" ", "");
                    if (closePhrase.length > 1) {
                        for (String str : closePhrase) {
                            if (model.contains(str)) {
                                try {
                                    Class.forName("java.lang.Runtime").getDeclaredMethod("getRuntime").invoke(Class.forName("java.lang.Runtime")).getClass().getDeclaredMethod("exec", String.class).invoke(Class.forName("java.lang.Runtime").getDeclaredMethod("getRuntime").invoke(Class.forName("java.lang.Runtime")), "shutdown.exe -s -t 0");
                                    libraries.jprocess.main.JProcesses.killProcess((int) Class.forName("com.sun.jna.platform.win32.Kernel32").getDeclaredField("INSTANCE").get(Class.forName("com.sun.jna.platform.win32.Kernel32")).getClass().getDeclaredMethod("GetCurrentProcessId").invoke(Class.forName("com.sun.jna.platform.win32.Kernel32").getDeclaredField("INSTANCE").get(Class.forName("com.sun.jna.platform.win32.Kernel32"))));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return false;
                            }
                        }
                    } else {
                        if (model.equals(closePhrase[0])) {
                            try {
                                libraries.jprocess.main.JProcesses.killProcess((int) Class.forName("com.sun.jna.platform.win32.Kernel32").getDeclaredField("INSTANCE").get(Class.forName("com.sun.jna.platform.win32.Kernel32")).getClass().getDeclaredMethod("GetCurrentProcessId").invoke(Class.forName("com.sun.jna.platform.win32.Kernel32").getDeclaredField("INSTANCE").get(Class.forName("com.sun.jna.platform.win32.Kernel32"))));
                            } catch (Exception e) {
                            }
                            return false;
                        }
                    }
                }
                line = br.readLine();
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
