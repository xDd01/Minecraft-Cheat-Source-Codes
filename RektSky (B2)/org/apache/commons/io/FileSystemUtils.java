package org.apache.commons.io;

import java.nio.charset.*;
import java.util.*;
import java.io.*;

@Deprecated
public class FileSystemUtils
{
    private static final FileSystemUtils INSTANCE;
    private static final int INIT_PROBLEM = -1;
    private static final int OTHER = 0;
    private static final int WINDOWS = 1;
    private static final int UNIX = 2;
    private static final int POSIX_UNIX = 3;
    private static final int OS;
    private static final String DF;
    
    @Deprecated
    public static long freeSpace(final String path) throws IOException {
        return FileSystemUtils.INSTANCE.freeSpaceOS(path, FileSystemUtils.OS, false, -1L);
    }
    
    @Deprecated
    public static long freeSpaceKb(final String path) throws IOException {
        return freeSpaceKb(path, -1L);
    }
    
    @Deprecated
    public static long freeSpaceKb(final String path, final long timeout) throws IOException {
        return FileSystemUtils.INSTANCE.freeSpaceOS(path, FileSystemUtils.OS, true, timeout);
    }
    
    @Deprecated
    public static long freeSpaceKb() throws IOException {
        return freeSpaceKb(-1L);
    }
    
    @Deprecated
    public static long freeSpaceKb(final long timeout) throws IOException {
        return freeSpaceKb(new File(".").getAbsolutePath(), timeout);
    }
    
    long freeSpaceOS(final String path, final int os, final boolean kb, final long timeout) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Path must not be null");
        }
        switch (os) {
            case 1: {
                return kb ? (this.freeSpaceWindows(path, timeout) / 1024L) : this.freeSpaceWindows(path, timeout);
            }
            case 2: {
                return this.freeSpaceUnix(path, kb, false, timeout);
            }
            case 3: {
                return this.freeSpaceUnix(path, kb, true, timeout);
            }
            case 0: {
                throw new IllegalStateException("Unsupported operating system");
            }
            default: {
                throw new IllegalStateException("Exception caught when determining operating system");
            }
        }
    }
    
    long freeSpaceWindows(final String path, final long timeout) throws IOException {
        String normPath = FilenameUtils.normalize(path, false);
        if (normPath == null) {
            throw new IllegalArgumentException(path);
        }
        if (normPath.length() > 0 && normPath.charAt(0) != '\"') {
            normPath = "\"" + normPath + "\"";
        }
        final String[] cmdAttribs = { "cmd.exe", "/C", "dir /a /-c " + normPath };
        final List<String> lines = this.performCommand(cmdAttribs, Integer.MAX_VALUE, timeout);
        for (int i = lines.size() - 1; i >= 0; --i) {
            final String line = lines.get(i);
            if (line.length() > 0) {
                return this.parseDir(line, normPath);
            }
        }
        throw new IOException("Command line 'dir /-c' did not return any info for path '" + normPath + "'");
    }
    
    long parseDir(final String line, final String path) throws IOException {
        int bytesStart = 0;
        int bytesEnd = 0;
        int j;
        for (j = line.length() - 1; j >= 0; --j) {
            final char c = line.charAt(j);
            if (Character.isDigit(c)) {
                bytesEnd = j + 1;
                break;
            }
        }
        while (j >= 0) {
            final char c = line.charAt(j);
            if (!Character.isDigit(c) && c != ',' && c != '.') {
                bytesStart = j + 1;
                break;
            }
            --j;
        }
        if (j < 0) {
            throw new IOException("Command line 'dir /-c' did not return valid info for path '" + path + "'");
        }
        final StringBuilder buf = new StringBuilder(line.substring(bytesStart, bytesEnd));
        for (int k = 0; k < buf.length(); ++k) {
            if (buf.charAt(k) == ',' || buf.charAt(k) == '.') {
                buf.deleteCharAt(k--);
            }
        }
        return this.parseBytes(buf.toString(), path);
    }
    
    long freeSpaceUnix(final String path, final boolean kb, final boolean posix, final long timeout) throws IOException {
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Path must not be empty");
        }
        String flags = "-";
        if (kb) {
            flags += "k";
        }
        if (posix) {
            flags += "P";
        }
        final String[] cmdAttribs = (flags.length() > 1) ? new String[] { FileSystemUtils.DF, flags, path } : new String[] { FileSystemUtils.DF, path };
        final List<String> lines = this.performCommand(cmdAttribs, 3, timeout);
        if (lines.size() < 2) {
            throw new IOException("Command line '" + FileSystemUtils.DF + "' did not return info as expected for path '" + path + "'- response was " + lines);
        }
        final String line2 = lines.get(1);
        StringTokenizer tok = new StringTokenizer(line2, " ");
        if (tok.countTokens() < 4) {
            if (tok.countTokens() != 1 || lines.size() < 3) {
                throw new IOException("Command line '" + FileSystemUtils.DF + "' did not return data as expected for path '" + path + "'- check path is valid");
            }
            final String line3 = lines.get(2);
            tok = new StringTokenizer(line3, " ");
        }
        else {
            tok.nextToken();
        }
        tok.nextToken();
        tok.nextToken();
        final String freeSpace = tok.nextToken();
        return this.parseBytes(freeSpace, path);
    }
    
    long parseBytes(final String freeSpace, final String path) throws IOException {
        try {
            final long bytes = Long.parseLong(freeSpace);
            if (bytes < 0L) {
                throw new IOException("Command line '" + FileSystemUtils.DF + "' did not find free space in response for path '" + path + "'- check path is valid");
            }
            return bytes;
        }
        catch (NumberFormatException ex) {
            throw new IOException("Command line '" + FileSystemUtils.DF + "' did not return numeric data as expected for path '" + path + "'- check path is valid", ex);
        }
    }
    
    List<String> performCommand(final String[] cmdAttribs, final int max, final long timeout) throws IOException {
        final List<String> lines = new ArrayList<String>(20);
        Process proc = null;
        InputStream in = null;
        OutputStream out = null;
        InputStream err = null;
        BufferedReader inr = null;
        try {
            final Thread monitor = ThreadMonitor.start(timeout);
            proc = this.openProcess(cmdAttribs);
            in = proc.getInputStream();
            out = proc.getOutputStream();
            err = proc.getErrorStream();
            inr = new BufferedReader(new InputStreamReader(in, Charset.defaultCharset()));
            for (String line = inr.readLine(); line != null && lines.size() < max; line = inr.readLine()) {
                line = line.toLowerCase(Locale.ENGLISH).trim();
                lines.add(line);
            }
            proc.waitFor();
            ThreadMonitor.stop(monitor);
            if (proc.exitValue() != 0) {
                throw new IOException("Command line returned OS error code '" + proc.exitValue() + "' for command " + Arrays.asList(cmdAttribs));
            }
            if (lines.isEmpty()) {
                throw new IOException("Command line did not return any info for command " + Arrays.asList(cmdAttribs));
            }
            inr.close();
            inr = null;
            in.close();
            in = null;
            if (out != null) {
                out.close();
                out = null;
            }
            if (err != null) {
                err.close();
                err = null;
            }
            return lines;
        }
        catch (InterruptedException ex) {
            throw new IOException("Command line threw an InterruptedException for command " + Arrays.asList(cmdAttribs) + " timeout=" + timeout, ex);
        }
        finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(err);
            IOUtils.closeQuietly(inr);
            if (proc != null) {
                proc.destroy();
            }
        }
    }
    
    Process openProcess(final String[] cmdAttribs) throws IOException {
        return Runtime.getRuntime().exec(cmdAttribs);
    }
    
    static {
        INSTANCE = new FileSystemUtils();
        int os = 0;
        String dfPath = "df";
        try {
            String osName = System.getProperty("os.name");
            if (osName == null) {
                throw new IOException("os.name not found");
            }
            osName = osName.toLowerCase(Locale.ENGLISH);
            if (osName.contains("windows")) {
                os = 1;
            }
            else if (osName.contains("linux") || osName.contains("mpe/ix") || osName.contains("freebsd") || osName.contains("openbsd") || osName.contains("irix") || osName.contains("digital unix") || osName.contains("unix") || osName.contains("mac os x")) {
                os = 2;
            }
            else if (osName.contains("sun os") || osName.contains("sunos") || osName.contains("solaris")) {
                os = 3;
                dfPath = "/usr/xpg4/bin/df";
            }
            else if (osName.contains("hp-ux") || osName.contains("aix")) {
                os = 3;
            }
            else {
                os = 0;
            }
        }
        catch (Exception ex) {
            os = -1;
        }
        OS = os;
        DF = dfPath;
    }
}
