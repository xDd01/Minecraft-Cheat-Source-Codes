package org.apache.commons.io;

import java.nio.charset.*;
import java.net.*;
import org.apache.commons.io.output.*;
import java.util.*;
import java.nio.channels.*;
import java.nio.*;
import java.io.*;

public class IOUtils
{
    public static final int EOF = -1;
    public static final char DIR_SEPARATOR_UNIX = '/';
    public static final char DIR_SEPARATOR_WINDOWS = '\\';
    public static final char DIR_SEPARATOR;
    public static final String LINE_SEPARATOR_UNIX = "\n";
    public static final String LINE_SEPARATOR_WINDOWS = "\r\n";
    public static final String LINE_SEPARATOR;
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private static final int SKIP_BUFFER_SIZE = 2048;
    private static char[] SKIP_CHAR_BUFFER;
    private static byte[] SKIP_BYTE_BUFFER;
    
    public static void close(final URLConnection conn) {
        if (conn instanceof HttpURLConnection) {
            ((HttpURLConnection)conn).disconnect();
        }
    }
    
    @Deprecated
    public static void closeQuietly(final Reader input) {
        closeQuietly((Closeable)input);
    }
    
    @Deprecated
    public static void closeQuietly(final Writer output) {
        closeQuietly((Closeable)output);
    }
    
    @Deprecated
    public static void closeQuietly(final InputStream input) {
        closeQuietly((Closeable)input);
    }
    
    @Deprecated
    public static void closeQuietly(final OutputStream output) {
        closeQuietly((Closeable)output);
    }
    
    @Deprecated
    public static void closeQuietly(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        }
        catch (IOException ex) {}
    }
    
    @Deprecated
    public static void closeQuietly(final Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (final Closeable closeable : closeables) {
            closeQuietly(closeable);
        }
    }
    
    @Deprecated
    public static void closeQuietly(final Socket sock) {
        if (sock != null) {
            try {
                sock.close();
            }
            catch (IOException ex) {}
        }
    }
    
    @Deprecated
    public static void closeQuietly(final Selector selector) {
        if (selector != null) {
            try {
                selector.close();
            }
            catch (IOException ex) {}
        }
    }
    
    @Deprecated
    public static void closeQuietly(final ServerSocket sock) {
        if (sock != null) {
            try {
                sock.close();
            }
            catch (IOException ex) {}
        }
    }
    
    public static InputStream toBufferedInputStream(final InputStream input) throws IOException {
        return ByteArrayOutputStream.toBufferedInputStream(input);
    }
    
    public static InputStream toBufferedInputStream(final InputStream input, final int size) throws IOException {
        return ByteArrayOutputStream.toBufferedInputStream(input, size);
    }
    
    public static BufferedReader toBufferedReader(final Reader reader) {
        return (BufferedReader)((reader instanceof BufferedReader) ? reader : new BufferedReader(reader));
    }
    
    public static BufferedReader toBufferedReader(final Reader reader, final int size) {
        return (BufferedReader)((reader instanceof BufferedReader) ? reader : new BufferedReader(reader, size));
    }
    
    public static BufferedReader buffer(final Reader reader) {
        return (BufferedReader)((reader instanceof BufferedReader) ? reader : new BufferedReader(reader));
    }
    
    public static BufferedReader buffer(final Reader reader, final int size) {
        return (BufferedReader)((reader instanceof BufferedReader) ? reader : new BufferedReader(reader, size));
    }
    
    public static BufferedWriter buffer(final Writer writer) {
        return (BufferedWriter)((writer instanceof BufferedWriter) ? writer : new BufferedWriter(writer));
    }
    
    public static BufferedWriter buffer(final Writer writer, final int size) {
        return (BufferedWriter)((writer instanceof BufferedWriter) ? writer : new BufferedWriter(writer, size));
    }
    
    public static BufferedOutputStream buffer(final OutputStream outputStream) {
        if (outputStream == null) {
            throw new NullPointerException();
        }
        return (BufferedOutputStream)((outputStream instanceof BufferedOutputStream) ? outputStream : new BufferedOutputStream(outputStream));
    }
    
    public static BufferedOutputStream buffer(final OutputStream outputStream, final int size) {
        if (outputStream == null) {
            throw new NullPointerException();
        }
        return (BufferedOutputStream)((outputStream instanceof BufferedOutputStream) ? outputStream : new BufferedOutputStream(outputStream, size));
    }
    
    public static BufferedInputStream buffer(final InputStream inputStream) {
        if (inputStream == null) {
            throw new NullPointerException();
        }
        return (BufferedInputStream)((inputStream instanceof BufferedInputStream) ? inputStream : new BufferedInputStream(inputStream));
    }
    
    public static BufferedInputStream buffer(final InputStream inputStream, final int size) {
        if (inputStream == null) {
            throw new NullPointerException();
        }
        return (BufferedInputStream)((inputStream instanceof BufferedInputStream) ? inputStream : new BufferedInputStream(inputStream, size));
    }
    
    public static byte[] toByteArray(final InputStream input) throws IOException {
        try (final ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            copy(input, output);
            return output.toByteArray();
        }
    }
    
    public static byte[] toByteArray(final InputStream input, final long size) throws IOException {
        if (size > 2147483647L) {
            throw new IllegalArgumentException("Size cannot be greater than Integer max value: " + size);
        }
        return toByteArray(input, (int)size);
    }
    
    public static byte[] toByteArray(final InputStream input, final int size) throws IOException {
        if (size < 0) {
            throw new IllegalArgumentException("Size must be equal or greater than zero: " + size);
        }
        if (size == 0) {
            return new byte[0];
        }
        byte[] data;
        int offset;
        int read;
        for (data = new byte[size], offset = 0; offset < size && (read = input.read(data, offset, size - offset)) != -1; offset += read) {}
        if (offset != size) {
            throw new IOException("Unexpected read size. current: " + offset + ", expected: " + size);
        }
        return data;
    }
    
    @Deprecated
    public static byte[] toByteArray(final Reader input) throws IOException {
        return toByteArray(input, Charset.defaultCharset());
    }
    
    public static byte[] toByteArray(final Reader input, final Charset encoding) throws IOException {
        try (final ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            copy(input, output, encoding);
            return output.toByteArray();
        }
    }
    
    public static byte[] toByteArray(final Reader input, final String encoding) throws IOException {
        return toByteArray(input, Charsets.toCharset(encoding));
    }
    
    @Deprecated
    public static byte[] toByteArray(final String input) throws IOException {
        return input.getBytes(Charset.defaultCharset());
    }
    
    public static byte[] toByteArray(final URI uri) throws IOException {
        return toByteArray(uri.toURL());
    }
    
    public static byte[] toByteArray(final URL url) throws IOException {
        final URLConnection conn = url.openConnection();
        try {
            return toByteArray(conn);
        }
        finally {
            close(conn);
        }
    }
    
    public static byte[] toByteArray(final URLConnection urlConn) throws IOException {
        try (final InputStream inputStream = urlConn.getInputStream()) {
            return toByteArray(inputStream);
        }
    }
    
    @Deprecated
    public static char[] toCharArray(final InputStream is) throws IOException {
        return toCharArray(is, Charset.defaultCharset());
    }
    
    public static char[] toCharArray(final InputStream is, final Charset encoding) throws IOException {
        final CharArrayWriter output = new CharArrayWriter();
        copy(is, output, encoding);
        return output.toCharArray();
    }
    
    public static char[] toCharArray(final InputStream is, final String encoding) throws IOException {
        return toCharArray(is, Charsets.toCharset(encoding));
    }
    
    public static char[] toCharArray(final Reader input) throws IOException {
        final CharArrayWriter sw = new CharArrayWriter();
        copy(input, sw);
        return sw.toCharArray();
    }
    
    @Deprecated
    public static String toString(final InputStream input) throws IOException {
        return toString(input, Charset.defaultCharset());
    }
    
    public static String toString(final InputStream input, final Charset encoding) throws IOException {
        try (final StringBuilderWriter sw = new StringBuilderWriter()) {
            copy(input, sw, encoding);
            return sw.toString();
        }
    }
    
    public static String toString(final InputStream input, final String encoding) throws IOException {
        return toString(input, Charsets.toCharset(encoding));
    }
    
    public static String toString(final Reader input) throws IOException {
        try (final StringBuilderWriter sw = new StringBuilderWriter()) {
            copy(input, sw);
            return sw.toString();
        }
    }
    
    @Deprecated
    public static String toString(final URI uri) throws IOException {
        return toString(uri, Charset.defaultCharset());
    }
    
    public static String toString(final URI uri, final Charset encoding) throws IOException {
        return toString(uri.toURL(), Charsets.toCharset(encoding));
    }
    
    public static String toString(final URI uri, final String encoding) throws IOException {
        return toString(uri, Charsets.toCharset(encoding));
    }
    
    @Deprecated
    public static String toString(final URL url) throws IOException {
        return toString(url, Charset.defaultCharset());
    }
    
    public static String toString(final URL url, final Charset encoding) throws IOException {
        try (final InputStream inputStream = url.openStream()) {
            return toString(inputStream, encoding);
        }
    }
    
    public static String toString(final URL url, final String encoding) throws IOException {
        return toString(url, Charsets.toCharset(encoding));
    }
    
    @Deprecated
    public static String toString(final byte[] input) throws IOException {
        return new String(input, Charset.defaultCharset());
    }
    
    public static String toString(final byte[] input, final String encoding) throws IOException {
        return new String(input, Charsets.toCharset(encoding));
    }
    
    public static String resourceToString(final String name, final Charset encoding) throws IOException {
        return resourceToString(name, encoding, null);
    }
    
    public static String resourceToString(final String name, final Charset encoding, final ClassLoader classLoader) throws IOException {
        return toString(resourceToURL(name, classLoader), encoding);
    }
    
    public static byte[] resourceToByteArray(final String name) throws IOException {
        return resourceToByteArray(name, null);
    }
    
    public static byte[] resourceToByteArray(final String name, final ClassLoader classLoader) throws IOException {
        return toByteArray(resourceToURL(name, classLoader));
    }
    
    public static URL resourceToURL(final String name) throws IOException {
        return resourceToURL(name, null);
    }
    
    public static URL resourceToURL(final String name, final ClassLoader classLoader) throws IOException {
        final URL resource = (classLoader == null) ? IOUtils.class.getResource(name) : classLoader.getResource(name);
        if (resource == null) {
            throw new IOException("Resource not found: " + name);
        }
        return resource;
    }
    
    @Deprecated
    public static List<String> readLines(final InputStream input) throws IOException {
        return readLines(input, Charset.defaultCharset());
    }
    
    public static List<String> readLines(final InputStream input, final Charset encoding) throws IOException {
        final InputStreamReader reader = new InputStreamReader(input, Charsets.toCharset(encoding));
        return readLines(reader);
    }
    
    public static List<String> readLines(final InputStream input, final String encoding) throws IOException {
        return readLines(input, Charsets.toCharset(encoding));
    }
    
    public static List<String> readLines(final Reader input) throws IOException {
        final BufferedReader reader = toBufferedReader(input);
        final List<String> list = new ArrayList<String>();
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            list.add(line);
        }
        return list;
    }
    
    public static LineIterator lineIterator(final Reader reader) {
        return new LineIterator(reader);
    }
    
    public static LineIterator lineIterator(final InputStream input, final Charset encoding) throws IOException {
        return new LineIterator(new InputStreamReader(input, Charsets.toCharset(encoding)));
    }
    
    public static LineIterator lineIterator(final InputStream input, final String encoding) throws IOException {
        return lineIterator(input, Charsets.toCharset(encoding));
    }
    
    @Deprecated
    public static InputStream toInputStream(final CharSequence input) {
        return toInputStream(input, Charset.defaultCharset());
    }
    
    public static InputStream toInputStream(final CharSequence input, final Charset encoding) {
        return toInputStream(input.toString(), encoding);
    }
    
    public static InputStream toInputStream(final CharSequence input, final String encoding) throws IOException {
        return toInputStream(input, Charsets.toCharset(encoding));
    }
    
    @Deprecated
    public static InputStream toInputStream(final String input) {
        return toInputStream(input, Charset.defaultCharset());
    }
    
    public static InputStream toInputStream(final String input, final Charset encoding) {
        return new ByteArrayInputStream(input.getBytes(Charsets.toCharset(encoding)));
    }
    
    public static InputStream toInputStream(final String input, final String encoding) throws IOException {
        final byte[] bytes = input.getBytes(Charsets.toCharset(encoding));
        return new ByteArrayInputStream(bytes);
    }
    
    public static void write(final byte[] data, final OutputStream output) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }
    
    public static void writeChunked(final byte[] data, final OutputStream output) throws IOException {
        if (data != null) {
            int chunk;
            for (int bytes = data.length, offset = 0; bytes > 0; bytes -= chunk, offset += chunk) {
                chunk = Math.min(bytes, 4096);
                output.write(data, offset, chunk);
            }
        }
    }
    
    @Deprecated
    public static void write(final byte[] data, final Writer output) throws IOException {
        write(data, output, Charset.defaultCharset());
    }
    
    public static void write(final byte[] data, final Writer output, final Charset encoding) throws IOException {
        if (data != null) {
            output.write(new String(data, Charsets.toCharset(encoding)));
        }
    }
    
    public static void write(final byte[] data, final Writer output, final String encoding) throws IOException {
        write(data, output, Charsets.toCharset(encoding));
    }
    
    public static void write(final char[] data, final Writer output) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }
    
    public static void writeChunked(final char[] data, final Writer output) throws IOException {
        if (data != null) {
            int chunk;
            for (int bytes = data.length, offset = 0; bytes > 0; bytes -= chunk, offset += chunk) {
                chunk = Math.min(bytes, 4096);
                output.write(data, offset, chunk);
            }
        }
    }
    
    @Deprecated
    public static void write(final char[] data, final OutputStream output) throws IOException {
        write(data, output, Charset.defaultCharset());
    }
    
    public static void write(final char[] data, final OutputStream output, final Charset encoding) throws IOException {
        if (data != null) {
            output.write(new String(data).getBytes(Charsets.toCharset(encoding)));
        }
    }
    
    public static void write(final char[] data, final OutputStream output, final String encoding) throws IOException {
        write(data, output, Charsets.toCharset(encoding));
    }
    
    public static void write(final CharSequence data, final Writer output) throws IOException {
        if (data != null) {
            write(data.toString(), output);
        }
    }
    
    @Deprecated
    public static void write(final CharSequence data, final OutputStream output) throws IOException {
        write(data, output, Charset.defaultCharset());
    }
    
    public static void write(final CharSequence data, final OutputStream output, final Charset encoding) throws IOException {
        if (data != null) {
            write(data.toString(), output, encoding);
        }
    }
    
    public static void write(final CharSequence data, final OutputStream output, final String encoding) throws IOException {
        write(data, output, Charsets.toCharset(encoding));
    }
    
    public static void write(final String data, final Writer output) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }
    
    @Deprecated
    public static void write(final String data, final OutputStream output) throws IOException {
        write(data, output, Charset.defaultCharset());
    }
    
    public static void write(final String data, final OutputStream output, final Charset encoding) throws IOException {
        if (data != null) {
            output.write(data.getBytes(Charsets.toCharset(encoding)));
        }
    }
    
    public static void write(final String data, final OutputStream output, final String encoding) throws IOException {
        write(data, output, Charsets.toCharset(encoding));
    }
    
    @Deprecated
    public static void write(final StringBuffer data, final Writer output) throws IOException {
        if (data != null) {
            output.write(data.toString());
        }
    }
    
    @Deprecated
    public static void write(final StringBuffer data, final OutputStream output) throws IOException {
        write(data, output, null);
    }
    
    @Deprecated
    public static void write(final StringBuffer data, final OutputStream output, final String encoding) throws IOException {
        if (data != null) {
            output.write(data.toString().getBytes(Charsets.toCharset(encoding)));
        }
    }
    
    @Deprecated
    public static void writeLines(final Collection<?> lines, final String lineEnding, final OutputStream output) throws IOException {
        writeLines(lines, lineEnding, output, Charset.defaultCharset());
    }
    
    public static void writeLines(final Collection<?> lines, String lineEnding, final OutputStream output, final Charset encoding) throws IOException {
        if (lines == null) {
            return;
        }
        if (lineEnding == null) {
            lineEnding = IOUtils.LINE_SEPARATOR;
        }
        final Charset cs = Charsets.toCharset(encoding);
        for (final Object line : lines) {
            if (line != null) {
                output.write(line.toString().getBytes(cs));
            }
            output.write(lineEnding.getBytes(cs));
        }
    }
    
    public static void writeLines(final Collection<?> lines, final String lineEnding, final OutputStream output, final String encoding) throws IOException {
        writeLines(lines, lineEnding, output, Charsets.toCharset(encoding));
    }
    
    public static void writeLines(final Collection<?> lines, String lineEnding, final Writer writer) throws IOException {
        if (lines == null) {
            return;
        }
        if (lineEnding == null) {
            lineEnding = IOUtils.LINE_SEPARATOR;
        }
        for (final Object line : lines) {
            if (line != null) {
                writer.write(line.toString());
            }
            writer.write(lineEnding);
        }
    }
    
    public static int copy(final InputStream input, final OutputStream output) throws IOException {
        final long count = copyLarge(input, output);
        if (count > 2147483647L) {
            return -1;
        }
        return (int)count;
    }
    
    public static long copy(final InputStream input, final OutputStream output, final int bufferSize) throws IOException {
        return copyLarge(input, output, new byte[bufferSize]);
    }
    
    public static long copyLarge(final InputStream input, final OutputStream output) throws IOException {
        return copy(input, output, 4096);
    }
    
    public static long copyLarge(final InputStream input, final OutputStream output, final byte[] buffer) throws IOException {
        long count = 0L;
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
    
    public static long copyLarge(final InputStream input, final OutputStream output, final long inputOffset, final long length) throws IOException {
        return copyLarge(input, output, inputOffset, length, new byte[4096]);
    }
    
    public static long copyLarge(final InputStream input, final OutputStream output, final long inputOffset, final long length, final byte[] buffer) throws IOException {
        if (inputOffset > 0L) {
            skipFully(input, inputOffset);
        }
        if (length == 0L) {
            return 0L;
        }
        int bytesToRead;
        final int bufferLength = bytesToRead = buffer.length;
        if (length > 0L && length < bufferLength) {
            bytesToRead = (int)length;
        }
        long totalRead;
        int read;
        for (totalRead = 0L; bytesToRead > 0 && -1 != (read = input.read(buffer, 0, bytesToRead)); bytesToRead = (int)Math.min(length - totalRead, bufferLength)) {
            output.write(buffer, 0, read);
            totalRead += read;
            if (length > 0L) {}
        }
        return totalRead;
    }
    
    @Deprecated
    public static void copy(final InputStream input, final Writer output) throws IOException {
        copy(input, output, Charset.defaultCharset());
    }
    
    public static void copy(final InputStream input, final Writer output, final Charset inputEncoding) throws IOException {
        final InputStreamReader in = new InputStreamReader(input, Charsets.toCharset(inputEncoding));
        copy(in, output);
    }
    
    public static void copy(final InputStream input, final Writer output, final String inputEncoding) throws IOException {
        copy(input, output, Charsets.toCharset(inputEncoding));
    }
    
    public static int copy(final Reader input, final Writer output) throws IOException {
        final long count = copyLarge(input, output);
        if (count > 2147483647L) {
            return -1;
        }
        return (int)count;
    }
    
    public static long copyLarge(final Reader input, final Writer output) throws IOException {
        return copyLarge(input, output, new char[4096]);
    }
    
    public static long copyLarge(final Reader input, final Writer output, final char[] buffer) throws IOException {
        long count = 0L;
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
    
    public static long copyLarge(final Reader input, final Writer output, final long inputOffset, final long length) throws IOException {
        return copyLarge(input, output, inputOffset, length, new char[4096]);
    }
    
    public static long copyLarge(final Reader input, final Writer output, final long inputOffset, final long length, final char[] buffer) throws IOException {
        if (inputOffset > 0L) {
            skipFully(input, inputOffset);
        }
        if (length == 0L) {
            return 0L;
        }
        int bytesToRead = buffer.length;
        if (length > 0L && length < buffer.length) {
            bytesToRead = (int)length;
        }
        long totalRead;
        int read;
        for (totalRead = 0L; bytesToRead > 0 && -1 != (read = input.read(buffer, 0, bytesToRead)); bytesToRead = (int)Math.min(length - totalRead, buffer.length)) {
            output.write(buffer, 0, read);
            totalRead += read;
            if (length > 0L) {}
        }
        return totalRead;
    }
    
    @Deprecated
    public static void copy(final Reader input, final OutputStream output) throws IOException {
        copy(input, output, Charset.defaultCharset());
    }
    
    public static void copy(final Reader input, final OutputStream output, final Charset outputEncoding) throws IOException {
        final OutputStreamWriter out = new OutputStreamWriter(output, Charsets.toCharset(outputEncoding));
        copy(input, out);
        out.flush();
    }
    
    public static void copy(final Reader input, final OutputStream output, final String outputEncoding) throws IOException {
        copy(input, output, Charsets.toCharset(outputEncoding));
    }
    
    public static boolean contentEquals(InputStream input1, InputStream input2) throws IOException {
        if (input1 == input2) {
            return true;
        }
        if (!(input1 instanceof BufferedInputStream)) {
            input1 = new BufferedInputStream(input1);
        }
        if (!(input2 instanceof BufferedInputStream)) {
            input2 = new BufferedInputStream(input2);
        }
        for (int ch = input1.read(); -1 != ch; ch = input1.read()) {
            final int ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
        }
        final int ch2 = input2.read();
        return ch2 == -1;
    }
    
    public static boolean contentEquals(Reader input1, Reader input2) throws IOException {
        if (input1 == input2) {
            return true;
        }
        input1 = toBufferedReader(input1);
        input2 = toBufferedReader(input2);
        for (int ch = input1.read(); -1 != ch; ch = input1.read()) {
            final int ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
        }
        final int ch2 = input2.read();
        return ch2 == -1;
    }
    
    public static boolean contentEqualsIgnoreEOL(final Reader input1, final Reader input2) throws IOException {
        if (input1 == input2) {
            return true;
        }
        BufferedReader br1;
        BufferedReader br2;
        String line1;
        String line2;
        for (br1 = toBufferedReader(input1), br2 = toBufferedReader(input2), line1 = br1.readLine(), line2 = br2.readLine(); line1 != null && line2 != null && line1.equals(line2); line1 = br1.readLine(), line2 = br2.readLine()) {}
        return (line1 == null) ? (line2 == null) : line1.equals(line2);
    }
    
    public static long skip(final InputStream input, final long toSkip) throws IOException {
        if (toSkip < 0L) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }
        if (IOUtils.SKIP_BYTE_BUFFER == null) {
            IOUtils.SKIP_BYTE_BUFFER = new byte[2048];
        }
        long remain;
        long n;
        for (remain = toSkip; remain > 0L; remain -= n) {
            n = input.read(IOUtils.SKIP_BYTE_BUFFER, 0, (int)Math.min(remain, 2048L));
            if (n < 0L) {
                break;
            }
        }
        return toSkip - remain;
    }
    
    public static long skip(final ReadableByteChannel input, final long toSkip) throws IOException {
        if (toSkip < 0L) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }
        final ByteBuffer skipByteBuffer = ByteBuffer.allocate((int)Math.min(toSkip, 2048L));
        long remain;
        int n;
        for (remain = toSkip; remain > 0L; remain -= n) {
            skipByteBuffer.position(0);
            skipByteBuffer.limit((int)Math.min(remain, 2048L));
            n = input.read(skipByteBuffer);
            if (n == -1) {
                break;
            }
        }
        return toSkip - remain;
    }
    
    public static long skip(final Reader input, final long toSkip) throws IOException {
        if (toSkip < 0L) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }
        if (IOUtils.SKIP_CHAR_BUFFER == null) {
            IOUtils.SKIP_CHAR_BUFFER = new char[2048];
        }
        long remain;
        long n;
        for (remain = toSkip; remain > 0L; remain -= n) {
            n = input.read(IOUtils.SKIP_CHAR_BUFFER, 0, (int)Math.min(remain, 2048L));
            if (n < 0L) {
                break;
            }
        }
        return toSkip - remain;
    }
    
    public static void skipFully(final InputStream input, final long toSkip) throws IOException {
        if (toSkip < 0L) {
            throw new IllegalArgumentException("Bytes to skip must not be negative: " + toSkip);
        }
        final long skipped = skip(input, toSkip);
        if (skipped != toSkip) {
            throw new EOFException("Bytes to skip: " + toSkip + " actual: " + skipped);
        }
    }
    
    public static void skipFully(final ReadableByteChannel input, final long toSkip) throws IOException {
        if (toSkip < 0L) {
            throw new IllegalArgumentException("Bytes to skip must not be negative: " + toSkip);
        }
        final long skipped = skip(input, toSkip);
        if (skipped != toSkip) {
            throw new EOFException("Bytes to skip: " + toSkip + " actual: " + skipped);
        }
    }
    
    public static void skipFully(final Reader input, final long toSkip) throws IOException {
        final long skipped = skip(input, toSkip);
        if (skipped != toSkip) {
            throw new EOFException("Chars to skip: " + toSkip + " actual: " + skipped);
        }
    }
    
    public static int read(final Reader input, final char[] buffer, final int offset, final int length) throws IOException {
        if (length < 0) {
            throw new IllegalArgumentException("Length must not be negative: " + length);
        }
        int remaining;
        int count;
        for (remaining = length; remaining > 0; remaining -= count) {
            final int location = length - remaining;
            count = input.read(buffer, offset + location, remaining);
            if (-1 == count) {
                break;
            }
        }
        return length - remaining;
    }
    
    public static int read(final Reader input, final char[] buffer) throws IOException {
        return read(input, buffer, 0, buffer.length);
    }
    
    public static int read(final InputStream input, final byte[] buffer, final int offset, final int length) throws IOException {
        if (length < 0) {
            throw new IllegalArgumentException("Length must not be negative: " + length);
        }
        int remaining;
        int count;
        for (remaining = length; remaining > 0; remaining -= count) {
            final int location = length - remaining;
            count = input.read(buffer, offset + location, remaining);
            if (-1 == count) {
                break;
            }
        }
        return length - remaining;
    }
    
    public static int read(final InputStream input, final byte[] buffer) throws IOException {
        return read(input, buffer, 0, buffer.length);
    }
    
    public static int read(final ReadableByteChannel input, final ByteBuffer buffer) throws IOException {
        final int length = buffer.remaining();
        while (buffer.remaining() > 0) {
            final int count = input.read(buffer);
            if (-1 == count) {
                break;
            }
        }
        return length - buffer.remaining();
    }
    
    public static void readFully(final Reader input, final char[] buffer, final int offset, final int length) throws IOException {
        final int actual = read(input, buffer, offset, length);
        if (actual != length) {
            throw new EOFException("Length to read: " + length + " actual: " + actual);
        }
    }
    
    public static void readFully(final Reader input, final char[] buffer) throws IOException {
        readFully(input, buffer, 0, buffer.length);
    }
    
    public static void readFully(final InputStream input, final byte[] buffer, final int offset, final int length) throws IOException {
        final int actual = read(input, buffer, offset, length);
        if (actual != length) {
            throw new EOFException("Length to read: " + length + " actual: " + actual);
        }
    }
    
    public static void readFully(final InputStream input, final byte[] buffer) throws IOException {
        readFully(input, buffer, 0, buffer.length);
    }
    
    public static byte[] readFully(final InputStream input, final int length) throws IOException {
        final byte[] buffer = new byte[length];
        readFully(input, buffer, 0, buffer.length);
        return buffer;
    }
    
    public static void readFully(final ReadableByteChannel input, final ByteBuffer buffer) throws IOException {
        final int expected = buffer.remaining();
        final int actual = read(input, buffer);
        if (actual != expected) {
            throw new EOFException("Length to read: " + expected + " actual: " + actual);
        }
    }
    
    static {
        DIR_SEPARATOR = File.separatorChar;
        try (final StringBuilderWriter buf = new StringBuilderWriter(4);
             final PrintWriter out = new PrintWriter(buf)) {
            out.println();
            LINE_SEPARATOR = buf.toString();
        }
    }
}
