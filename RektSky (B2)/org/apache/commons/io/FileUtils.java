package org.apache.commons.io;

import java.math.*;
import org.apache.commons.io.filefilter.*;
import java.nio.*;
import java.nio.charset.*;
import java.nio.channels.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import org.apache.commons.io.output.*;
import java.nio.file.*;

public class FileUtils
{
    public static final long ONE_KB = 1024L;
    public static final BigInteger ONE_KB_BI;
    public static final long ONE_MB = 1048576L;
    public static final BigInteger ONE_MB_BI;
    private static final long FILE_COPY_BUFFER_SIZE = 31457280L;
    public static final long ONE_GB = 1073741824L;
    public static final BigInteger ONE_GB_BI;
    public static final long ONE_TB = 1099511627776L;
    public static final BigInteger ONE_TB_BI;
    public static final long ONE_PB = 1125899906842624L;
    public static final BigInteger ONE_PB_BI;
    public static final long ONE_EB = 1152921504606846976L;
    public static final BigInteger ONE_EB_BI;
    public static final BigInteger ONE_ZB;
    public static final BigInteger ONE_YB;
    public static final File[] EMPTY_FILE_ARRAY;
    
    public static File getFile(final File directory, final String... names) {
        if (directory == null) {
            throw new NullPointerException("directory must not be null");
        }
        if (names == null) {
            throw new NullPointerException("names must not be null");
        }
        File file = directory;
        for (final String name : names) {
            file = new File(file, name);
        }
        return file;
    }
    
    public static File getFile(final String... names) {
        if (names == null) {
            throw new NullPointerException("names must not be null");
        }
        File file = null;
        for (final String name : names) {
            if (file == null) {
                file = new File(name);
            }
            else {
                file = new File(file, name);
            }
        }
        return file;
    }
    
    public static String getTempDirectoryPath() {
        return System.getProperty("java.io.tmpdir");
    }
    
    public static File getTempDirectory() {
        return new File(getTempDirectoryPath());
    }
    
    public static String getUserDirectoryPath() {
        return System.getProperty("user.home");
    }
    
    public static File getUserDirectory() {
        return new File(getUserDirectoryPath());
    }
    
    public static FileInputStream openInputStream(final File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
        if (file.isDirectory()) {
            throw new IOException("File '" + file + "' exists but is a directory");
        }
        if (!file.canRead()) {
            throw new IOException("File '" + file + "' cannot be read");
        }
        return new FileInputStream(file);
    }
    
    public static FileOutputStream openOutputStream(final File file) throws IOException {
        return openOutputStream(file, false);
    }
    
    public static FileOutputStream openOutputStream(final File file, final boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canWrite()) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        }
        else {
            final File parent = file.getParentFile();
            if (parent != null && !parent.mkdirs() && !parent.isDirectory()) {
                throw new IOException("Directory '" + parent + "' could not be created");
            }
        }
        return new FileOutputStream(file, append);
    }
    
    public static String byteCountToDisplaySize(final BigInteger size) {
        String displaySize;
        if (size.divide(FileUtils.ONE_EB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(FileUtils.ONE_EB_BI)) + " EB";
        }
        else if (size.divide(FileUtils.ONE_PB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(FileUtils.ONE_PB_BI)) + " PB";
        }
        else if (size.divide(FileUtils.ONE_TB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(FileUtils.ONE_TB_BI)) + " TB";
        }
        else if (size.divide(FileUtils.ONE_GB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(FileUtils.ONE_GB_BI)) + " GB";
        }
        else if (size.divide(FileUtils.ONE_MB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(FileUtils.ONE_MB_BI)) + " MB";
        }
        else if (size.divide(FileUtils.ONE_KB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(FileUtils.ONE_KB_BI)) + " KB";
        }
        else {
            displaySize = String.valueOf(size) + " bytes";
        }
        return displaySize;
    }
    
    public static String byteCountToDisplaySize(final long size) {
        return byteCountToDisplaySize(BigInteger.valueOf(size));
    }
    
    public static void touch(final File file) throws IOException {
        if (!file.exists()) {
            openOutputStream(file).close();
        }
        final boolean success = file.setLastModified(System.currentTimeMillis());
        if (!success) {
            throw new IOException("Unable to set the last modification time for " + file);
        }
    }
    
    public static File[] convertFileCollectionToFileArray(final Collection<File> files) {
        return files.toArray(new File[files.size()]);
    }
    
    private static void innerListFiles(final Collection<File> files, final File directory, final IOFileFilter filter, final boolean includeSubDirectories) {
        final File[] found = directory.listFiles((FileFilter)filter);
        if (found != null) {
            for (final File file : found) {
                if (file.isDirectory()) {
                    if (includeSubDirectories) {
                        files.add(file);
                    }
                    innerListFiles(files, file, filter, includeSubDirectories);
                }
                else {
                    files.add(file);
                }
            }
        }
    }
    
    public static Collection<File> listFiles(final File directory, final IOFileFilter fileFilter, final IOFileFilter dirFilter) {
        validateListFilesParameters(directory, fileFilter);
        final IOFileFilter effFileFilter = setUpEffectiveFileFilter(fileFilter);
        final IOFileFilter effDirFilter = setUpEffectiveDirFilter(dirFilter);
        final Collection<File> files = new LinkedList<File>();
        innerListFiles(files, directory, FileFilterUtils.or(effFileFilter, effDirFilter), false);
        return files;
    }
    
    private static void validateListFilesParameters(final File directory, final IOFileFilter fileFilter) {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Parameter 'directory' is not a directory: " + directory);
        }
        if (fileFilter == null) {
            throw new NullPointerException("Parameter 'fileFilter' is null");
        }
    }
    
    private static IOFileFilter setUpEffectiveFileFilter(final IOFileFilter fileFilter) {
        return FileFilterUtils.and(fileFilter, FileFilterUtils.notFileFilter(DirectoryFileFilter.INSTANCE));
    }
    
    private static IOFileFilter setUpEffectiveDirFilter(final IOFileFilter dirFilter) {
        return (dirFilter == null) ? FalseFileFilter.INSTANCE : FileFilterUtils.and(dirFilter, DirectoryFileFilter.INSTANCE);
    }
    
    public static Collection<File> listFilesAndDirs(final File directory, final IOFileFilter fileFilter, final IOFileFilter dirFilter) {
        validateListFilesParameters(directory, fileFilter);
        final IOFileFilter effFileFilter = setUpEffectiveFileFilter(fileFilter);
        final IOFileFilter effDirFilter = setUpEffectiveDirFilter(dirFilter);
        final Collection<File> files = new LinkedList<File>();
        if (directory.isDirectory()) {
            files.add(directory);
        }
        innerListFiles(files, directory, FileFilterUtils.or(effFileFilter, effDirFilter), true);
        return files;
    }
    
    public static Iterator<File> iterateFiles(final File directory, final IOFileFilter fileFilter, final IOFileFilter dirFilter) {
        return listFiles(directory, fileFilter, dirFilter).iterator();
    }
    
    public static Iterator<File> iterateFilesAndDirs(final File directory, final IOFileFilter fileFilter, final IOFileFilter dirFilter) {
        return listFilesAndDirs(directory, fileFilter, dirFilter).iterator();
    }
    
    private static String[] toSuffixes(final String[] extensions) {
        final String[] suffixes = new String[extensions.length];
        for (int i = 0; i < extensions.length; ++i) {
            suffixes[i] = "." + extensions[i];
        }
        return suffixes;
    }
    
    public static Collection<File> listFiles(final File directory, final String[] extensions, final boolean recursive) {
        IOFileFilter filter;
        if (extensions == null) {
            filter = TrueFileFilter.INSTANCE;
        }
        else {
            final String[] suffixes = toSuffixes(extensions);
            filter = new SuffixFileFilter(suffixes);
        }
        return listFiles(directory, filter, recursive ? TrueFileFilter.INSTANCE : FalseFileFilter.INSTANCE);
    }
    
    public static Iterator<File> iterateFiles(final File directory, final String[] extensions, final boolean recursive) {
        return listFiles(directory, extensions, recursive).iterator();
    }
    
    public static boolean contentEquals(final File file1, final File file2) throws IOException {
        final boolean file1Exists = file1.exists();
        if (file1Exists != file2.exists()) {
            return false;
        }
        if (!file1Exists) {
            return true;
        }
        if (file1.isDirectory() || file2.isDirectory()) {
            throw new IOException("Can't compare directories, only files");
        }
        if (file1.length() != file2.length()) {
            return false;
        }
        if (file1.getCanonicalFile().equals(file2.getCanonicalFile())) {
            return true;
        }
        try (final InputStream input1 = new FileInputStream(file1);
             final InputStream input2 = new FileInputStream(file2)) {
            return IOUtils.contentEquals(input1, input2);
        }
    }
    
    public static boolean contentEqualsIgnoreEOL(final File file1, final File file2, final String charsetName) throws IOException {
        final boolean file1Exists = file1.exists();
        if (file1Exists != file2.exists()) {
            return false;
        }
        if (!file1Exists) {
            return true;
        }
        if (file1.isDirectory() || file2.isDirectory()) {
            throw new IOException("Can't compare directories, only files");
        }
        if (file1.getCanonicalFile().equals(file2.getCanonicalFile())) {
            return true;
        }
        try (final Reader input1 = (charsetName == null) ? new InputStreamReader(new FileInputStream(file1), Charset.defaultCharset()) : new InputStreamReader(new FileInputStream(file1), charsetName)) {
            if (charsetName == null) {
                final FileInputStream fileInputStream;
                final Reader reader = new InputStreamReader(fileInputStream, Charset.defaultCharset());
                fileInputStream = new FileInputStream(file2);
            }
            else {
                final FileInputStream fileInputStream2;
                final Reader reader = new InputStreamReader(fileInputStream2, charsetName);
                fileInputStream2 = new FileInputStream(file2);
            }
            Reader reader;
            try (final Reader input2 = reader) {
                return IOUtils.contentEqualsIgnoreEOL(input1, input2);
            }
        }
    }
    
    public static File toFile(final URL url) {
        if (url == null || !"file".equalsIgnoreCase(url.getProtocol())) {
            return null;
        }
        String filename = url.getFile().replace('/', File.separatorChar);
        filename = decodeUrl(filename);
        return new File(filename);
    }
    
    static String decodeUrl(final String url) {
        String decoded = url;
        if (url != null && url.indexOf(37) >= 0) {
            final int n = url.length();
            final StringBuilder buffer = new StringBuilder();
            final ByteBuffer bytes = ByteBuffer.allocate(n);
            int i = 0;
            while (i < n) {
                if (url.charAt(i) == '%') {
                    try {
                        do {
                            final byte octet = (byte)Integer.parseInt(url.substring(i + 1, i + 3), 16);
                            bytes.put(octet);
                            i += 3;
                        } while (i < n && url.charAt(i) == '%');
                        continue;
                    }
                    catch (RuntimeException ex) {}
                    finally {
                        if (bytes.position() > 0) {
                            bytes.flip();
                            buffer.append(StandardCharsets.UTF_8.decode(bytes).toString());
                            bytes.clear();
                        }
                    }
                }
                buffer.append(url.charAt(i++));
            }
            decoded = buffer.toString();
        }
        return decoded;
    }
    
    public static File[] toFiles(final URL[] urls) {
        if (urls == null || urls.length == 0) {
            return FileUtils.EMPTY_FILE_ARRAY;
        }
        final File[] files = new File[urls.length];
        for (int i = 0; i < urls.length; ++i) {
            final URL url = urls[i];
            if (url != null) {
                if (!url.getProtocol().equals("file")) {
                    throw new IllegalArgumentException("URL could not be converted to a File: " + url);
                }
                files[i] = toFile(url);
            }
        }
        return files;
    }
    
    public static URL[] toURLs(final File[] files) throws IOException {
        final URL[] urls = new URL[files.length];
        for (int i = 0; i < urls.length; ++i) {
            urls[i] = files[i].toURI().toURL();
        }
        return urls;
    }
    
    public static void copyFileToDirectory(final File srcFile, final File destDir) throws IOException {
        copyFileToDirectory(srcFile, destDir, true);
    }
    
    public static void copyFileToDirectory(final File srcFile, final File destDir, final boolean preserveFileDate) throws IOException {
        if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (destDir.exists() && !destDir.isDirectory()) {
            throw new IllegalArgumentException("Destination '" + destDir + "' is not a directory");
        }
        final File destFile = new File(destDir, srcFile.getName());
        copyFile(srcFile, destFile, preserveFileDate);
    }
    
    public static void copyFile(final File srcFile, final File destFile) throws IOException {
        copyFile(srcFile, destFile, true);
    }
    
    public static void copyFile(final File srcFile, final File destFile, final boolean preserveFileDate) throws IOException {
        checkFileRequirements(srcFile, destFile);
        if (srcFile.isDirectory()) {
            throw new IOException("Source '" + srcFile + "' exists but is a directory");
        }
        if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
            throw new IOException("Source '" + srcFile + "' and destination '" + destFile + "' are the same");
        }
        final File parentFile = destFile.getParentFile();
        if (parentFile != null && !parentFile.mkdirs() && !parentFile.isDirectory()) {
            throw new IOException("Destination '" + parentFile + "' directory cannot be created");
        }
        if (destFile.exists() && !destFile.canWrite()) {
            throw new IOException("Destination '" + destFile + "' exists but is read-only");
        }
        doCopyFile(srcFile, destFile, preserveFileDate);
    }
    
    public static long copyFile(final File input, final OutputStream output) throws IOException {
        try (final FileInputStream fis = new FileInputStream(input)) {
            return IOUtils.copyLarge(fis, output);
        }
    }
    
    private static void doCopyFile(final File srcFile, final File destFile, final boolean preserveFileDate) throws IOException {
        if (destFile.exists() && destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
        }
        try (final FileInputStream fis = new FileInputStream(srcFile);
             final FileChannel input = fis.getChannel();
             final FileOutputStream fos = new FileOutputStream(destFile);
             final FileChannel output = fos.getChannel()) {
            final long size = input.size();
            long pos = 0L;
            long count = 0L;
            while (pos < size) {
                final long remain = size - pos;
                count = ((remain > 31457280L) ? 31457280L : remain);
                final long bytesCopied = output.transferFrom(input, pos, count);
                if (bytesCopied == 0L) {
                    break;
                }
                pos += bytesCopied;
            }
        }
        final long srcLen = srcFile.length();
        final long dstLen = destFile.length();
        if (srcLen != dstLen) {
            throw new IOException("Failed to copy full contents from '" + srcFile + "' to '" + destFile + "' Expected length: " + srcLen + " Actual: " + dstLen);
        }
        if (preserveFileDate) {
            destFile.setLastModified(srcFile.lastModified());
        }
    }
    
    public static void copyDirectoryToDirectory(final File srcDir, final File destDir) throws IOException {
        if (srcDir == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (srcDir.exists() && !srcDir.isDirectory()) {
            throw new IllegalArgumentException("Source '" + destDir + "' is not a directory");
        }
        if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (destDir.exists() && !destDir.isDirectory()) {
            throw new IllegalArgumentException("Destination '" + destDir + "' is not a directory");
        }
        copyDirectory(srcDir, new File(destDir, srcDir.getName()), true);
    }
    
    public static void copyDirectory(final File srcDir, final File destDir) throws IOException {
        copyDirectory(srcDir, destDir, true);
    }
    
    public static void copyDirectory(final File srcDir, final File destDir, final boolean preserveFileDate) throws IOException {
        copyDirectory(srcDir, destDir, null, preserveFileDate);
    }
    
    public static void copyDirectory(final File srcDir, final File destDir, final FileFilter filter) throws IOException {
        copyDirectory(srcDir, destDir, filter, true);
    }
    
    public static void copyDirectory(final File srcDir, final File destDir, final FileFilter filter, final boolean preserveFileDate) throws IOException {
        checkFileRequirements(srcDir, destDir);
        if (!srcDir.isDirectory()) {
            throw new IOException("Source '" + srcDir + "' exists but is not a directory");
        }
        if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
            throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are the same");
        }
        List<String> exclusionList = null;
        if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
            final File[] srcFiles = (filter == null) ? srcDir.listFiles() : srcDir.listFiles(filter);
            if (srcFiles != null && srcFiles.length > 0) {
                exclusionList = new ArrayList<String>(srcFiles.length);
                for (final File srcFile : srcFiles) {
                    final File copiedFile = new File(destDir, srcFile.getName());
                    exclusionList.add(copiedFile.getCanonicalPath());
                }
            }
        }
        doCopyDirectory(srcDir, destDir, filter, preserveFileDate, exclusionList);
    }
    
    private static void checkFileRequirements(final File src, final File dest) throws FileNotFoundException {
        if (src == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (dest == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (!src.exists()) {
            throw new FileNotFoundException("Source '" + src + "' does not exist");
        }
    }
    
    private static void doCopyDirectory(final File srcDir, final File destDir, final FileFilter filter, final boolean preserveFileDate, final List<String> exclusionList) throws IOException {
        final File[] srcFiles = (filter == null) ? srcDir.listFiles() : srcDir.listFiles(filter);
        if (srcFiles == null) {
            throw new IOException("Failed to list contents of " + srcDir);
        }
        if (destDir.exists()) {
            if (!destDir.isDirectory()) {
                throw new IOException("Destination '" + destDir + "' exists but is not a directory");
            }
        }
        else if (!destDir.mkdirs() && !destDir.isDirectory()) {
            throw new IOException("Destination '" + destDir + "' directory cannot be created");
        }
        if (!destDir.canWrite()) {
            throw new IOException("Destination '" + destDir + "' cannot be written to");
        }
        for (final File srcFile : srcFiles) {
            final File dstFile = new File(destDir, srcFile.getName());
            if (exclusionList == null || !exclusionList.contains(srcFile.getCanonicalPath())) {
                if (srcFile.isDirectory()) {
                    doCopyDirectory(srcFile, dstFile, filter, preserveFileDate, exclusionList);
                }
                else {
                    doCopyFile(srcFile, dstFile, preserveFileDate);
                }
            }
        }
        if (preserveFileDate) {
            destDir.setLastModified(srcDir.lastModified());
        }
    }
    
    public static void copyURLToFile(final URL source, final File destination) throws IOException {
        copyInputStreamToFile(source.openStream(), destination);
    }
    
    public static void copyURLToFile(final URL source, final File destination, final int connectionTimeout, final int readTimeout) throws IOException {
        final URLConnection connection = source.openConnection();
        connection.setConnectTimeout(connectionTimeout);
        connection.setReadTimeout(readTimeout);
        copyInputStreamToFile(connection.getInputStream(), destination);
    }
    
    public static void copyInputStreamToFile(final InputStream source, final File destination) throws IOException {
        try (final InputStream in = source) {
            copyToFile(in, destination);
        }
    }
    
    public static void copyToFile(final InputStream source, final File destination) throws IOException {
        try (final InputStream in = source;
             final OutputStream out = openOutputStream(destination)) {
            IOUtils.copy(in, out);
        }
    }
    
    public static void copyToDirectory(final File src, final File destDir) throws IOException {
        if (src == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (src.isFile()) {
            copyFileToDirectory(src, destDir);
        }
        else {
            if (!src.isDirectory()) {
                throw new IOException("The source " + src + " does not exist");
            }
            copyDirectoryToDirectory(src, destDir);
        }
    }
    
    public static void copyToDirectory(final Iterable<File> srcs, final File destDir) throws IOException {
        if (srcs == null) {
            throw new NullPointerException("Sources must not be null");
        }
        for (final File src : srcs) {
            copyFileToDirectory(src, destDir);
        }
    }
    
    public static void deleteDirectory(final File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }
        if (!isSymlink(directory)) {
            cleanDirectory(directory);
        }
        if (!directory.delete()) {
            final String message = "Unable to delete directory " + directory + ".";
            throw new IOException(message);
        }
    }
    
    public static boolean deleteQuietly(final File file) {
        if (file == null) {
            return false;
        }
        try {
            if (file.isDirectory()) {
                cleanDirectory(file);
            }
        }
        catch (Exception ex) {}
        try {
            return file.delete();
        }
        catch (Exception ignored) {
            return false;
        }
    }
    
    public static boolean directoryContains(final File directory, final File child) throws IOException {
        if (directory == null) {
            throw new IllegalArgumentException("Directory must not be null");
        }
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + directory);
        }
        if (child == null) {
            return false;
        }
        if (!directory.exists() || !child.exists()) {
            return false;
        }
        final String canonicalParent = directory.getCanonicalPath();
        final String canonicalChild = child.getCanonicalPath();
        return FilenameUtils.directoryContains(canonicalParent, canonicalChild);
    }
    
    public static void cleanDirectory(final File directory) throws IOException {
        final File[] files = verifiedListFiles(directory);
        IOException exception = null;
        for (final File file : files) {
            try {
                forceDelete(file);
            }
            catch (IOException ioe) {
                exception = ioe;
            }
        }
        if (null != exception) {
            throw exception;
        }
    }
    
    private static File[] verifiedListFiles(final File directory) throws IOException {
        if (!directory.exists()) {
            final String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }
        if (!directory.isDirectory()) {
            final String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }
        final File[] files = directory.listFiles();
        if (files == null) {
            throw new IOException("Failed to list contents of " + directory);
        }
        return files;
    }
    
    public static boolean waitFor(final File file, final int seconds) {
        final long finishAt = System.currentTimeMillis() + seconds * 1000L;
        boolean wasInterrupted = false;
        try {
            while (!file.exists()) {
                final long remaining = finishAt - System.currentTimeMillis();
                if (remaining < 0L) {
                    return false;
                }
                try {
                    Thread.sleep(Math.min(100L, remaining));
                }
                catch (InterruptedException ignore) {
                    wasInterrupted = true;
                }
                catch (Exception ex) {
                    break;
                }
            }
        }
        finally {
            if (wasInterrupted) {
                Thread.currentThread().interrupt();
            }
        }
        return true;
    }
    
    public static String readFileToString(final File file, final Charset encoding) throws IOException {
        try (final InputStream in = openInputStream(file)) {
            return IOUtils.toString(in, Charsets.toCharset(encoding));
        }
    }
    
    public static String readFileToString(final File file, final String encoding) throws IOException {
        return readFileToString(file, Charsets.toCharset(encoding));
    }
    
    @Deprecated
    public static String readFileToString(final File file) throws IOException {
        return readFileToString(file, Charset.defaultCharset());
    }
    
    public static byte[] readFileToByteArray(final File file) throws IOException {
        try (final InputStream in = openInputStream(file)) {
            final long fileLength = file.length();
            return (fileLength > 0L) ? IOUtils.toByteArray(in, fileLength) : IOUtils.toByteArray(in);
        }
    }
    
    public static List<String> readLines(final File file, final Charset encoding) throws IOException {
        try (final InputStream in = openInputStream(file)) {
            return IOUtils.readLines(in, Charsets.toCharset(encoding));
        }
    }
    
    public static List<String> readLines(final File file, final String encoding) throws IOException {
        return readLines(file, Charsets.toCharset(encoding));
    }
    
    @Deprecated
    public static List<String> readLines(final File file) throws IOException {
        return readLines(file, Charset.defaultCharset());
    }
    
    public static LineIterator lineIterator(final File file, final String encoding) throws IOException {
        InputStream in = null;
        try {
            in = openInputStream(file);
            return IOUtils.lineIterator(in, encoding);
        }
        catch (IOException | RuntimeException ex3) {
            final Exception ex2;
            final Exception ex = ex2;
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException e) {
                ex.addSuppressed(e);
            }
            throw ex;
        }
    }
    
    public static LineIterator lineIterator(final File file) throws IOException {
        return lineIterator(file, null);
    }
    
    public static void writeStringToFile(final File file, final String data, final Charset encoding) throws IOException {
        writeStringToFile(file, data, encoding, false);
    }
    
    public static void writeStringToFile(final File file, final String data, final String encoding) throws IOException {
        writeStringToFile(file, data, encoding, false);
    }
    
    public static void writeStringToFile(final File file, final String data, final Charset encoding, final boolean append) throws IOException {
        try (final OutputStream out = openOutputStream(file, append)) {
            IOUtils.write(data, out, encoding);
        }
    }
    
    public static void writeStringToFile(final File file, final String data, final String encoding, final boolean append) throws IOException {
        writeStringToFile(file, data, Charsets.toCharset(encoding), append);
    }
    
    @Deprecated
    public static void writeStringToFile(final File file, final String data) throws IOException {
        writeStringToFile(file, data, Charset.defaultCharset(), false);
    }
    
    @Deprecated
    public static void writeStringToFile(final File file, final String data, final boolean append) throws IOException {
        writeStringToFile(file, data, Charset.defaultCharset(), append);
    }
    
    @Deprecated
    public static void write(final File file, final CharSequence data) throws IOException {
        write(file, data, Charset.defaultCharset(), false);
    }
    
    @Deprecated
    public static void write(final File file, final CharSequence data, final boolean append) throws IOException {
        write(file, data, Charset.defaultCharset(), append);
    }
    
    public static void write(final File file, final CharSequence data, final Charset encoding) throws IOException {
        write(file, data, encoding, false);
    }
    
    public static void write(final File file, final CharSequence data, final String encoding) throws IOException {
        write(file, data, encoding, false);
    }
    
    public static void write(final File file, final CharSequence data, final Charset encoding, final boolean append) throws IOException {
        final String str = (data == null) ? null : data.toString();
        writeStringToFile(file, str, encoding, append);
    }
    
    public static void write(final File file, final CharSequence data, final String encoding, final boolean append) throws IOException {
        write(file, data, Charsets.toCharset(encoding), append);
    }
    
    public static void writeByteArrayToFile(final File file, final byte[] data) throws IOException {
        writeByteArrayToFile(file, data, false);
    }
    
    public static void writeByteArrayToFile(final File file, final byte[] data, final boolean append) throws IOException {
        writeByteArrayToFile(file, data, 0, data.length, append);
    }
    
    public static void writeByteArrayToFile(final File file, final byte[] data, final int off, final int len) throws IOException {
        writeByteArrayToFile(file, data, off, len, false);
    }
    
    public static void writeByteArrayToFile(final File file, final byte[] data, final int off, final int len, final boolean append) throws IOException {
        try (final OutputStream out = openOutputStream(file, append)) {
            out.write(data, off, len);
        }
    }
    
    public static void writeLines(final File file, final String encoding, final Collection<?> lines) throws IOException {
        writeLines(file, encoding, lines, null, false);
    }
    
    public static void writeLines(final File file, final String encoding, final Collection<?> lines, final boolean append) throws IOException {
        writeLines(file, encoding, lines, null, append);
    }
    
    public static void writeLines(final File file, final Collection<?> lines) throws IOException {
        writeLines(file, null, lines, null, false);
    }
    
    public static void writeLines(final File file, final Collection<?> lines, final boolean append) throws IOException {
        writeLines(file, null, lines, null, append);
    }
    
    public static void writeLines(final File file, final String encoding, final Collection<?> lines, final String lineEnding) throws IOException {
        writeLines(file, encoding, lines, lineEnding, false);
    }
    
    public static void writeLines(final File file, final String encoding, final Collection<?> lines, final String lineEnding, final boolean append) throws IOException {
        try (final OutputStream out = new BufferedOutputStream(openOutputStream(file, append))) {
            IOUtils.writeLines(lines, lineEnding, out, encoding);
        }
    }
    
    public static void writeLines(final File file, final Collection<?> lines, final String lineEnding) throws IOException {
        writeLines(file, null, lines, lineEnding, false);
    }
    
    public static void writeLines(final File file, final Collection<?> lines, final String lineEnding, final boolean append) throws IOException {
        writeLines(file, null, lines, lineEnding, append);
    }
    
    public static void forceDelete(final File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        }
        else {
            final boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent) {
                    throw new FileNotFoundException("File does not exist: " + file);
                }
                final String message = "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }
    
    public static void forceDeleteOnExit(final File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectoryOnExit(file);
        }
        else {
            file.deleteOnExit();
        }
    }
    
    private static void deleteDirectoryOnExit(final File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }
        directory.deleteOnExit();
        if (!isSymlink(directory)) {
            cleanDirectoryOnExit(directory);
        }
    }
    
    private static void cleanDirectoryOnExit(final File directory) throws IOException {
        final File[] files = verifiedListFiles(directory);
        IOException exception = null;
        for (final File file : files) {
            try {
                forceDeleteOnExit(file);
            }
            catch (IOException ioe) {
                exception = ioe;
            }
        }
        if (null != exception) {
            throw exception;
        }
    }
    
    public static void forceMkdir(final File directory) throws IOException {
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                final String message = "File " + directory + " exists and is not a directory. Unable to create directory.";
                throw new IOException(message);
            }
        }
        else if (!directory.mkdirs() && !directory.isDirectory()) {
            final String message = "Unable to create directory " + directory;
            throw new IOException(message);
        }
    }
    
    public static void forceMkdirParent(final File file) throws IOException {
        final File parent = file.getParentFile();
        if (parent == null) {
            return;
        }
        forceMkdir(parent);
    }
    
    public static long sizeOf(final File file) {
        if (!file.exists()) {
            final String message = file + " does not exist";
            throw new IllegalArgumentException(message);
        }
        if (file.isDirectory()) {
            return sizeOfDirectory0(file);
        }
        return file.length();
    }
    
    public static BigInteger sizeOfAsBigInteger(final File file) {
        if (!file.exists()) {
            final String message = file + " does not exist";
            throw new IllegalArgumentException(message);
        }
        if (file.isDirectory()) {
            return sizeOfDirectoryBig0(file);
        }
        return BigInteger.valueOf(file.length());
    }
    
    public static long sizeOfDirectory(final File directory) {
        checkDirectory(directory);
        return sizeOfDirectory0(directory);
    }
    
    private static long sizeOfDirectory0(final File directory) {
        final File[] files = directory.listFiles();
        if (files == null) {
            return 0L;
        }
        long size = 0L;
        for (final File file : files) {
            try {
                if (!isSymlink(file)) {
                    size += sizeOf0(file);
                    if (size < 0L) {
                        break;
                    }
                }
            }
            catch (IOException ex) {}
        }
        return size;
    }
    
    private static long sizeOf0(final File file) {
        if (file.isDirectory()) {
            return sizeOfDirectory0(file);
        }
        return file.length();
    }
    
    public static BigInteger sizeOfDirectoryAsBigInteger(final File directory) {
        checkDirectory(directory);
        return sizeOfDirectoryBig0(directory);
    }
    
    private static BigInteger sizeOfDirectoryBig0(final File directory) {
        final File[] files = directory.listFiles();
        if (files == null) {
            return BigInteger.ZERO;
        }
        BigInteger size = BigInteger.ZERO;
        for (final File file : files) {
            try {
                if (!isSymlink(file)) {
                    size = size.add(sizeOfBig0(file));
                }
            }
            catch (IOException ex) {}
        }
        return size;
    }
    
    private static BigInteger sizeOfBig0(final File fileOrDir) {
        if (fileOrDir.isDirectory()) {
            return sizeOfDirectoryBig0(fileOrDir);
        }
        return BigInteger.valueOf(fileOrDir.length());
    }
    
    private static void checkDirectory(final File directory) {
        if (!directory.exists()) {
            throw new IllegalArgumentException(directory + " does not exist");
        }
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(directory + " is not a directory");
        }
    }
    
    public static boolean isFileNewer(final File file, final File reference) {
        if (reference == null) {
            throw new IllegalArgumentException("No specified reference file");
        }
        if (!reference.exists()) {
            throw new IllegalArgumentException("The reference file '" + reference + "' doesn't exist");
        }
        return isFileNewer(file, reference.lastModified());
    }
    
    public static boolean isFileNewer(final File file, final Date date) {
        if (date == null) {
            throw new IllegalArgumentException("No specified date");
        }
        return isFileNewer(file, date.getTime());
    }
    
    public static boolean isFileNewer(final File file, final long timeMillis) {
        if (file == null) {
            throw new IllegalArgumentException("No specified file");
        }
        return file.exists() && file.lastModified() > timeMillis;
    }
    
    public static boolean isFileOlder(final File file, final File reference) {
        if (reference == null) {
            throw new IllegalArgumentException("No specified reference file");
        }
        if (!reference.exists()) {
            throw new IllegalArgumentException("The reference file '" + reference + "' doesn't exist");
        }
        return isFileOlder(file, reference.lastModified());
    }
    
    public static boolean isFileOlder(final File file, final Date date) {
        if (date == null) {
            throw new IllegalArgumentException("No specified date");
        }
        return isFileOlder(file, date.getTime());
    }
    
    public static boolean isFileOlder(final File file, final long timeMillis) {
        if (file == null) {
            throw new IllegalArgumentException("No specified file");
        }
        return file.exists() && file.lastModified() < timeMillis;
    }
    
    public static long checksumCRC32(final File file) throws IOException {
        final CRC32 crc = new CRC32();
        checksum(file, crc);
        return crc.getValue();
    }
    
    public static Checksum checksum(final File file, final Checksum checksum) throws IOException {
        if (file.isDirectory()) {
            throw new IllegalArgumentException("Checksums can't be computed on directories");
        }
        try (final InputStream in = new CheckedInputStream(new FileInputStream(file), checksum)) {
            IOUtils.copy(in, new NullOutputStream());
        }
        return checksum;
    }
    
    public static void moveDirectory(final File srcDir, final File destDir) throws IOException {
        if (srcDir == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (!srcDir.exists()) {
            throw new FileNotFoundException("Source '" + srcDir + "' does not exist");
        }
        if (!srcDir.isDirectory()) {
            throw new IOException("Source '" + srcDir + "' is not a directory");
        }
        if (destDir.exists()) {
            throw new FileExistsException("Destination '" + destDir + "' already exists");
        }
        final boolean rename = srcDir.renameTo(destDir);
        if (!rename) {
            if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath() + File.separator)) {
                throw new IOException("Cannot move directory: " + srcDir + " to a subdirectory of itself: " + destDir);
            }
            copyDirectory(srcDir, destDir);
            deleteDirectory(srcDir);
            if (srcDir.exists()) {
                throw new IOException("Failed to delete original directory '" + srcDir + "' after copy to '" + destDir + "'");
            }
        }
    }
    
    public static void moveDirectoryToDirectory(final File src, final File destDir, final boolean createDestDir) throws IOException {
        if (src == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destDir == null) {
            throw new NullPointerException("Destination directory must not be null");
        }
        if (!destDir.exists() && createDestDir) {
            destDir.mkdirs();
        }
        if (!destDir.exists()) {
            throw new FileNotFoundException("Destination directory '" + destDir + "' does not exist [createDestDir=" + createDestDir + "]");
        }
        if (!destDir.isDirectory()) {
            throw new IOException("Destination '" + destDir + "' is not a directory");
        }
        moveDirectory(src, new File(destDir, src.getName()));
    }
    
    public static void moveFile(final File srcFile, final File destFile) throws IOException {
        if (srcFile == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destFile == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (!srcFile.exists()) {
            throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
        }
        if (srcFile.isDirectory()) {
            throw new IOException("Source '" + srcFile + "' is a directory");
        }
        if (destFile.exists()) {
            throw new FileExistsException("Destination '" + destFile + "' already exists");
        }
        if (destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' is a directory");
        }
        final boolean rename = srcFile.renameTo(destFile);
        if (!rename) {
            copyFile(srcFile, destFile);
            if (!srcFile.delete()) {
                deleteQuietly(destFile);
                throw new IOException("Failed to delete original file '" + srcFile + "' after copy to '" + destFile + "'");
            }
        }
    }
    
    public static void moveFileToDirectory(final File srcFile, final File destDir, final boolean createDestDir) throws IOException {
        if (srcFile == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destDir == null) {
            throw new NullPointerException("Destination directory must not be null");
        }
        if (!destDir.exists() && createDestDir) {
            destDir.mkdirs();
        }
        if (!destDir.exists()) {
            throw new FileNotFoundException("Destination directory '" + destDir + "' does not exist [createDestDir=" + createDestDir + "]");
        }
        if (!destDir.isDirectory()) {
            throw new IOException("Destination '" + destDir + "' is not a directory");
        }
        moveFile(srcFile, new File(destDir, srcFile.getName()));
    }
    
    public static void moveToDirectory(final File src, final File destDir, final boolean createDestDir) throws IOException {
        if (src == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (!src.exists()) {
            throw new FileNotFoundException("Source '" + src + "' does not exist");
        }
        if (src.isDirectory()) {
            moveDirectoryToDirectory(src, destDir, createDestDir);
        }
        else {
            moveFileToDirectory(src, destDir, createDestDir);
        }
    }
    
    public static boolean isSymlink(final File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("File must not be null");
        }
        return Files.isSymbolicLink(file.toPath());
    }
    
    static {
        ONE_KB_BI = BigInteger.valueOf(1024L);
        ONE_MB_BI = FileUtils.ONE_KB_BI.multiply(FileUtils.ONE_KB_BI);
        ONE_GB_BI = FileUtils.ONE_KB_BI.multiply(FileUtils.ONE_MB_BI);
        ONE_TB_BI = FileUtils.ONE_KB_BI.multiply(FileUtils.ONE_GB_BI);
        ONE_PB_BI = FileUtils.ONE_KB_BI.multiply(FileUtils.ONE_TB_BI);
        ONE_EB_BI = FileUtils.ONE_KB_BI.multiply(FileUtils.ONE_PB_BI);
        ONE_ZB = BigInteger.valueOf(1024L).multiply(BigInteger.valueOf(1152921504606846976L));
        ONE_YB = FileUtils.ONE_KB_BI.multiply(FileUtils.ONE_ZB);
        EMPTY_FILE_ARRAY = new File[0];
    }
}
