package org.apache.commons.io.input;

import java.nio.charset.*;
import org.apache.commons.io.*;
import java.io.*;

public class Tailer implements Runnable
{
    private static final int DEFAULT_DELAY_MILLIS = 1000;
    private static final String RAF_MODE = "r";
    private static final int DEFAULT_BUFSIZE = 4096;
    private static final Charset DEFAULT_CHARSET;
    private final byte[] inbuf;
    private final File file;
    private final Charset cset;
    private final long delayMillis;
    private final boolean end;
    private final TailerListener listener;
    private final boolean reOpen;
    private volatile boolean run;
    
    public Tailer(final File file, final TailerListener listener) {
        this(file, listener, 1000L);
    }
    
    public Tailer(final File file, final TailerListener listener, final long delayMillis) {
        this(file, listener, delayMillis, false);
    }
    
    public Tailer(final File file, final TailerListener listener, final long delayMillis, final boolean end) {
        this(file, listener, delayMillis, end, 4096);
    }
    
    public Tailer(final File file, final TailerListener listener, final long delayMillis, final boolean end, final boolean reOpen) {
        this(file, listener, delayMillis, end, reOpen, 4096);
    }
    
    public Tailer(final File file, final TailerListener listener, final long delayMillis, final boolean end, final int bufSize) {
        this(file, listener, delayMillis, end, false, bufSize);
    }
    
    public Tailer(final File file, final TailerListener listener, final long delayMillis, final boolean end, final boolean reOpen, final int bufSize) {
        this(file, Tailer.DEFAULT_CHARSET, listener, delayMillis, end, reOpen, bufSize);
    }
    
    public Tailer(final File file, final Charset cset, final TailerListener listener, final long delayMillis, final boolean end, final boolean reOpen, final int bufSize) {
        this.run = true;
        this.file = file;
        this.delayMillis = delayMillis;
        this.end = end;
        this.inbuf = new byte[bufSize];
        (this.listener = listener).init(this);
        this.reOpen = reOpen;
        this.cset = cset;
    }
    
    public static Tailer create(final File file, final TailerListener listener, final long delayMillis, final boolean end, final int bufSize) {
        return create(file, listener, delayMillis, end, false, bufSize);
    }
    
    public static Tailer create(final File file, final TailerListener listener, final long delayMillis, final boolean end, final boolean reOpen, final int bufSize) {
        return create(file, Tailer.DEFAULT_CHARSET, listener, delayMillis, end, reOpen, bufSize);
    }
    
    public static Tailer create(final File file, final Charset charset, final TailerListener listener, final long delayMillis, final boolean end, final boolean reOpen, final int bufSize) {
        final Tailer tailer = new Tailer(file, charset, listener, delayMillis, end, reOpen, bufSize);
        final Thread thread = new Thread(tailer);
        thread.setDaemon(true);
        thread.start();
        return tailer;
    }
    
    public static Tailer create(final File file, final TailerListener listener, final long delayMillis, final boolean end) {
        return create(file, listener, delayMillis, end, 4096);
    }
    
    public static Tailer create(final File file, final TailerListener listener, final long delayMillis, final boolean end, final boolean reOpen) {
        return create(file, listener, delayMillis, end, reOpen, 4096);
    }
    
    public static Tailer create(final File file, final TailerListener listener, final long delayMillis) {
        return create(file, listener, delayMillis, false);
    }
    
    public static Tailer create(final File file, final TailerListener listener) {
        return create(file, listener, 1000L, false);
    }
    
    public File getFile() {
        return this.file;
    }
    
    protected boolean getRun() {
        return this.run;
    }
    
    public long getDelay() {
        return this.delayMillis;
    }
    
    @Override
    public void run() {
        RandomAccessFile reader = null;
        try {
            long last = 0L;
            long position = 0L;
            while (this.getRun() && reader == null) {
                try {
                    reader = new RandomAccessFile(this.file, "r");
                }
                catch (FileNotFoundException e4) {
                    this.listener.fileNotFound();
                }
                if (reader == null) {
                    Thread.sleep(this.delayMillis);
                }
                else {
                    position = (this.end ? this.file.length() : 0L);
                    last = this.file.lastModified();
                    reader.seek(position);
                }
            }
            while (this.getRun()) {
                final boolean newer = FileUtils.isFileNewer(this.file, last);
                final long length = this.file.length();
                if (length < position) {
                    this.listener.fileRotated();
                    try (final RandomAccessFile save = reader) {
                        reader = new RandomAccessFile(this.file, "r");
                        try {
                            this.readLines(save);
                        }
                        catch (IOException ioe) {
                            this.listener.handle(ioe);
                        }
                        position = 0L;
                    }
                    catch (FileNotFoundException e5) {
                        this.listener.fileNotFound();
                        Thread.sleep(this.delayMillis);
                    }
                }
                else {
                    if (length > position) {
                        position = this.readLines(reader);
                        last = this.file.lastModified();
                    }
                    else if (newer) {
                        position = 0L;
                        reader.seek(position);
                        position = this.readLines(reader);
                        last = this.file.lastModified();
                    }
                    if (this.reOpen && reader != null) {
                        reader.close();
                    }
                    Thread.sleep(this.delayMillis);
                    if (!this.getRun() || !this.reOpen) {
                        continue;
                    }
                    reader = new RandomAccessFile(this.file, "r");
                    reader.seek(position);
                }
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            this.listener.handle(e);
        }
        catch (Exception e2) {
            this.listener.handle(e2);
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (IOException e3) {
                this.listener.handle(e3);
            }
            this.stop();
        }
    }
    
    public void stop() {
        this.run = false;
    }
    
    private long readLines(final RandomAccessFile reader) throws IOException {
        try (final ByteArrayOutputStream lineBuf = new ByteArrayOutputStream(64)) {
            long rePos;
            long pos = rePos = reader.getFilePointer();
            boolean seenCR = false;
            int num;
            while (this.getRun() && (num = reader.read(this.inbuf)) != -1) {
                for (int i = 0; i < num; ++i) {
                    final byte ch = this.inbuf[i];
                    switch (ch) {
                        case 10: {
                            seenCR = false;
                            this.listener.handle(new String(lineBuf.toByteArray(), this.cset));
                            lineBuf.reset();
                            rePos = pos + i + 1L;
                            break;
                        }
                        case 13: {
                            if (seenCR) {
                                lineBuf.write(13);
                            }
                            seenCR = true;
                            break;
                        }
                        default: {
                            if (seenCR) {
                                seenCR = false;
                                this.listener.handle(new String(lineBuf.toByteArray(), this.cset));
                                lineBuf.reset();
                                rePos = pos + i + 1L;
                            }
                            lineBuf.write(ch);
                            break;
                        }
                    }
                }
                pos = reader.getFilePointer();
            }
            reader.seek(rePos);
            if (this.listener instanceof TailerListenerAdapter) {
                ((TailerListenerAdapter)this.listener).endOfFileReached();
            }
            return rePos;
        }
    }
    
    static {
        DEFAULT_CHARSET = Charset.defaultCharset();
    }
}
