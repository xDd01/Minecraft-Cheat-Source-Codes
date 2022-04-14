/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.appender.rolling.helper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import org.apache.logging.log4j.core.appender.rolling.helper.AbstractAction;

public final class GZCompressAction
extends AbstractAction {
    private static final int BUF_SIZE = 8102;
    private final File source;
    private final File destination;
    private final boolean deleteSource;

    public GZCompressAction(File source, File destination, boolean deleteSource) {
        if (source == null) {
            throw new NullPointerException("source");
        }
        if (destination == null) {
            throw new NullPointerException("destination");
        }
        this.source = source;
        this.destination = destination;
        this.deleteSource = deleteSource;
    }

    @Override
    public boolean execute() throws IOException {
        return GZCompressAction.execute(this.source, this.destination, this.deleteSource);
    }

    public static boolean execute(File source, File destination, boolean deleteSource) throws IOException {
        if (source.exists()) {
            int n2;
            FileInputStream fis = new FileInputStream(source);
            FileOutputStream fos = new FileOutputStream(destination);
            GZIPOutputStream gzos = new GZIPOutputStream(fos);
            BufferedOutputStream os2 = new BufferedOutputStream(gzos);
            byte[] inbuf = new byte[8102];
            while ((n2 = fis.read(inbuf)) != -1) {
                os2.write(inbuf, 0, n2);
            }
            os2.close();
            fis.close();
            if (deleteSource && !source.delete()) {
                LOGGER.warn("Unable to delete " + source.toString() + '.');
            }
            return true;
        }
        return false;
    }

    @Override
    protected void reportException(Exception ex2) {
        LOGGER.warn("Exception during compression of '" + this.source.toString() + "'.", (Throwable)ex2);
    }
}

