/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOExceptionWithCause;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.ThreadMonitor;

public class FileSystemUtils {
    private static final FileSystemUtils INSTANCE = new FileSystemUtils();
    private static final int INIT_PROBLEM = -1;
    private static final int OTHER = 0;
    private static final int WINDOWS = 1;
    private static final int UNIX = 2;
    private static final int POSIX_UNIX = 3;
    private static final int OS;
    private static final String DF;

    @Deprecated
    public static long freeSpace(String path) throws IOException {
        return INSTANCE.freeSpaceOS(path, OS, false, -1L);
    }

    public static long freeSpaceKb(String path) throws IOException {
        return FileSystemUtils.freeSpaceKb(path, -1L);
    }

    public static long freeSpaceKb(String path, long timeout) throws IOException {
        return INSTANCE.freeSpaceOS(path, OS, true, timeout);
    }

    public static long freeSpaceKb() throws IOException {
        return FileSystemUtils.freeSpaceKb(-1L);
    }

    public static long freeSpaceKb(long timeout) throws IOException {
        return FileSystemUtils.freeSpaceKb(new File(".").getAbsolutePath(), timeout);
    }

    long freeSpaceOS(String path, int os2, boolean kb2, long timeout) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Path must not be empty");
        }
        switch (os2) {
            case 1: {
                return kb2 ? this.freeSpaceWindows(path, timeout) / 1024L : this.freeSpaceWindows(path, timeout);
            }
            case 2: {
                return this.freeSpaceUnix(path, kb2, false, timeout);
            }
            case 3: {
                return this.freeSpaceUnix(path, kb2, true, timeout);
            }
            case 0: {
                throw new IllegalStateException("Unsupported operating system");
            }
        }
        throw new IllegalStateException("Exception caught when determining operating system");
    }

    long freeSpaceWindows(String path, long timeout) throws IOException {
        if ((path = FilenameUtils.normalize(path, false)).length() > 0 && path.charAt(0) != '\"') {
            path = "\"" + path + "\"";
        }
        String[] cmdAttribs = new String[]{"cmd.exe", "/C", "dir /a /-c " + path};
        List<String> lines = this.performCommand(cmdAttribs, Integer.MAX_VALUE, timeout);
        for (int i2 = lines.size() - 1; i2 >= 0; --i2) {
            String line = lines.get(i2);
            if (line.length() <= 0) continue;
            return this.parseDir(line, path);
        }
        throw new IOException("Command line 'dir /-c' did not return any info for path '" + path + "'");
    }

    long parseDir(String line, String path) throws IOException {
        char c2;
        int j2;
        int bytesStart = 0;
        int bytesEnd = 0;
        for (j2 = line.length() - 1; j2 >= 0; --j2) {
            c2 = line.charAt(j2);
            if (!Character.isDigit(c2)) continue;
            bytesEnd = j2 + 1;
            break;
        }
        while (j2 >= 0) {
            c2 = line.charAt(j2);
            if (!Character.isDigit(c2) && c2 != ',' && c2 != '.') {
                bytesStart = j2 + 1;
                break;
            }
            --j2;
        }
        if (j2 < 0) {
            throw new IOException("Command line 'dir /-c' did not return valid info for path '" + path + "'");
        }
        StringBuilder buf = new StringBuilder(line.substring(bytesStart, bytesEnd));
        for (int k2 = 0; k2 < buf.length(); ++k2) {
            if (buf.charAt(k2) != ',' && buf.charAt(k2) != '.') continue;
            buf.deleteCharAt(k2--);
        }
        return this.parseBytes(buf.toString(), path);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    long freeSpaceUnix(String path, boolean kb2, boolean posix, long timeout) throws IOException {
        String[] stringArray;
        if (path.length() == 0) {
            throw new IllegalArgumentException("Path must not be empty");
        }
        String flags = "-";
        if (kb2) {
            flags = flags + "k";
        }
        if (posix) {
            flags = flags + "P";
        }
        if (flags.length() > 1) {
            String[] stringArray2 = new String[3];
            stringArray2[0] = DF;
            stringArray2[1] = flags;
            stringArray = stringArray2;
            stringArray2[2] = path;
        } else {
            String[] stringArray3 = new String[2];
            stringArray3[0] = DF;
            stringArray = stringArray3;
            stringArray3[1] = path;
        }
        String[] cmdAttribs = stringArray;
        List<String> lines = this.performCommand(cmdAttribs, 3, timeout);
        if (lines.size() < 2) {
            throw new IOException("Command line '" + DF + "' did not return info as expected " + "for path '" + path + "'- response was " + lines);
        }
        String line2 = lines.get(1);
        StringTokenizer tok = new StringTokenizer(line2, " ");
        if (tok.countTokens() < 4) {
            if (tok.countTokens() != 1 || lines.size() < 3) throw new IOException("Command line '" + DF + "' did not return data as expected " + "for path '" + path + "'- check path is valid");
            String line3 = lines.get(2);
            tok = new StringTokenizer(line3, " ");
        } else {
            tok.nextToken();
        }
        tok.nextToken();
        tok.nextToken();
        String freeSpace = tok.nextToken();
        return this.parseBytes(freeSpace, path);
    }

    long parseBytes(String freeSpace, String path) throws IOException {
        try {
            long bytes = Long.parseLong(freeSpace);
            if (bytes < 0L) {
                throw new IOException("Command line '" + DF + "' did not find free space in response " + "for path '" + path + "'- check path is valid");
            }
            return bytes;
        }
        catch (NumberFormatException ex2) {
            throw new IOExceptionWithCause("Command line '" + DF + "' did not return numeric data as expected " + "for path '" + path + "'- check path is valid", ex2);
        }
    }

    List<String> performCommand(String[] cmdAttribs, int max, long timeout) throws IOException {
        ArrayList<String> arrayList;
        ArrayList<String> lines = new ArrayList<String>(20);
        Process proc = null;
        InputStream in2 = null;
        OutputStream out = null;
        InputStream err = null;
        BufferedReader inr = null;
        try {
            Thread monitor = ThreadMonitor.start(timeout);
            proc = this.openProcess(cmdAttribs);
            in2 = proc.getInputStream();
            out = proc.getOutputStream();
            err = proc.getErrorStream();
            inr = new BufferedReader(new InputStreamReader(in2));
            String line = inr.readLine();
            while (line != null && lines.size() < max) {
                line = line.toLowerCase(Locale.ENGLISH).trim();
                lines.add(line);
                line = inr.readLine();
            }
            proc.waitFor();
            ThreadMonitor.stop(monitor);
            if (proc.exitValue() != 0) {
                throw new IOException("Command line returned OS error code '" + proc.exitValue() + "' for command " + Arrays.asList(cmdAttribs));
            }
            if (lines.isEmpty()) {
                throw new IOException("Command line did not return any info for command " + Arrays.asList(cmdAttribs));
            }
            arrayList = lines;
        }
        catch (InterruptedException ex2) {
            try {
                throw new IOExceptionWithCause("Command line threw an InterruptedException for command " + Arrays.asList(cmdAttribs) + " timeout=" + timeout, ex2);
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(in2);
                IOUtils.closeQuietly(out);
                IOUtils.closeQuietly(err);
                IOUtils.closeQuietly(inr);
                if (proc != null) {
                    proc.destroy();
                }
                throw throwable;
            }
        }
        IOUtils.closeQuietly(in2);
        IOUtils.closeQuietly(out);
        IOUtils.closeQuietly(err);
        IOUtils.closeQuietly(inr);
        if (proc != null) {
            proc.destroy();
        }
        return arrayList;
    }

    Process openProcess(String[] cmdAttribs) throws IOException {
        return Runtime.getRuntime().exec(cmdAttribs);
    }

    static {
        int os2 = 0;
        String dfPath = "df";
        try {
            String osName = System.getProperty("os.name");
            if (osName == null) {
                throw new IOException("os.name not found");
            }
            if ((osName = osName.toLowerCase(Locale.ENGLISH)).indexOf("windows") != -1) {
                os2 = 1;
            } else if (osName.indexOf("linux") != -1 || osName.indexOf("mpe/ix") != -1 || osName.indexOf("freebsd") != -1 || osName.indexOf("irix") != -1 || osName.indexOf("digital unix") != -1 || osName.indexOf("unix") != -1 || osName.indexOf("mac os x") != -1) {
                os2 = 2;
            } else if (osName.indexOf("sun os") != -1 || osName.indexOf("sunos") != -1 || osName.indexOf("solaris") != -1) {
                os2 = 3;
                dfPath = "/usr/xpg4/bin/df";
            } else {
                os2 = osName.indexOf("hp-ux") != -1 || osName.indexOf("aix") != -1 ? 3 : 0;
            }
        }
        catch (Exception ex2) {
            os2 = -1;
        }
        OS = os2;
        DF = dfPath;
    }
}

