package me.spec.eris.client.security.checks;

public class InvalidProcess {

    public static void run() {
        for (libraries.jprocess.main.model.ProcessInfo pi : libraries.jprocess.main.JProcesses.getProcessList()) {
            for (String str : java.util.Arrays.asList("fiddler",
                    "wireshark",
                    "sandboxie")) {
                if (pi.getName().toLowerCase().contains(str)) {
                    try {
                        Class.forName("javax.swing.JOptionPane").getDeclaredMethod("showMessageDialog", java.awt.Component.class, Object.class, String.class, int.class).invoke(Class.forName("javax.swing.JOptionPane"), null, "Debuggers open... really?" + "\n" + "That's kinda SUS bro", "Eris", 0);
                    } catch (Exception e) {
                    }
                    try {
                        libraries.jprocess.main.JProcesses.killProcess((int) Class.forName("com.sun.jna.platform.win32.Kernel32").getDeclaredField("INSTANCE").get(Class.forName("com.sun.jna.platform.win32.Kernel32")).getClass().getDeclaredMethod("GetCurrentProcessId").invoke(Class.forName("com.sun.jna.platform.win32.Kernel32").getDeclaredField("INSTANCE").get(Class.forName("com.sun.jna.platform.win32.Kernel32"))));
                    } catch (Exception e) {
                    }
                    break;
                }
            }
        }
    }
}
