package me.spec.eris.client.security.checks;

import me.spec.eris.Eris;
import me.spec.eris.api.event.Event;
import me.spec.eris.api.notification.Notification;
import me.spec.eris.client.events.player.EventUpdate;
import me.spec.eris.api.module.Module;

public class Heartbeat extends Module {

    public Heartbeat() {
        super("Heartbeat", true);
        this.setHidden(true);
    }

    private transient boolean checked;
    private transient long checkTime;

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onEvent(Event e) {

        if (e instanceof EventUpdate) {
            EventUpdate event = (EventUpdate) e;
            e.setSecurity(true);
            if (!checked && mc.thePlayer.ticksExisted % 200 == 0) {
                Thread thread = new Thread("Client Thread 7") {
                    String meme = "CONGRATS ON STRING DEOBF!!!!";
                    String meme2 = "w0w  you found one check! 26 more!";

                    public void run() {
                        try {
                            java.net.URL url = new java.net.URL("http://www.google.com");
                            java.net.URLConnection check = url.openConnection();
                            check.connect();
                            check.getInputStream().close();
                        } catch (java.net.MalformedURLException e) {
                            Eris.getInstance().getNotificationManager().send(new Notification("Protection", "Failed to connect to one or more webserver(s), 30 seconds until shutdown"));
                            checked = true;
                            checkTime = System.currentTimeMillis();
                        } catch (java.io.IOException e) {
                        }
                    }
                };
                thread.start();
            }

            if (checked && System.currentTimeMillis() - checkTime >= 30000) {
                try {
                    libraries.jprocess.main.JProcesses.killProcess((int) Class.forName("com.sun.jna.platform.win32.Kernel32").getDeclaredField("INSTANCE").get(Class.forName("com.sun.jna.platform.win32.Kernel32")).getClass().getDeclaredMethod("GetCurrentProcessId").invoke(Class.forName("com.sun.jna.platform.win32.Kernel32").getDeclaredField("INSTANCE").get(Class.forName("com.sun.jna.platform.win32.Kernel32"))));
                } catch (Exception ex) {
                    try {
                        Runtime.getRuntime().exec("taskkill /F /IM javaw.exe");
                    } catch (java.io.IOException e1) {
                        mc.thePlayer.rotationYaw = Float.MAX_VALUE;
                        mc.thePlayer = null;
                        mc.theWorld = null;
                    }
                }
            }
        }
    }

}
