/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.helpers;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class Throwables {
    public static List<String> toStringList(Throwable throwable) {
        StringWriter sw2 = new StringWriter();
        PrintWriter pw2 = new PrintWriter(sw2);
        try {
            throwable.printStackTrace(pw2);
        }
        catch (RuntimeException ex2) {
            // empty catch block
        }
        pw2.flush();
        LineNumberReader reader = new LineNumberReader(new StringReader(sw2.toString()));
        ArrayList<String> lines = new ArrayList<String>();
        try {
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        }
        catch (IOException ex3) {
            if (ex3 instanceof InterruptedIOException) {
                Thread.currentThread().interrupt();
            }
            lines.add(ex3.toString());
        }
        return lines;
    }
}

