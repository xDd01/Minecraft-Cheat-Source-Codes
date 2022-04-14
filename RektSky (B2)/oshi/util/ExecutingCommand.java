package oshi.util;

import java.util.*;
import java.io.*;

public class ExecutingCommand
{
    public static ArrayList<String> runNative(final String cmdToRun) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(cmdToRun);
            p.waitFor();
        }
        catch (IOException e) {
            return null;
        }
        catch (InterruptedException e2) {
            return null;
        }
        final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = "";
        final ArrayList<String> sa = new ArrayList<String>();
        try {
            while ((line = reader.readLine()) != null) {
                sa.add(line);
            }
        }
        catch (IOException e3) {
            return null;
        }
        return sa;
    }
    
    public static String getFirstAnswer(final String cmd2launch) {
        final ArrayList<String> sa = runNative(cmd2launch);
        if (sa != null) {
            return sa.get(0);
        }
        return null;
    }
}
