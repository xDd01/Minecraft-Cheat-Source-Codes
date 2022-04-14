/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.tukaani.xz.DeltaOptions
 *  org.tukaani.xz.FinishableOutputStream
 *  org.tukaani.xz.FinishableWrapperOutputStream
 *  org.tukaani.xz.UnsupportedOptionsException
 */
package org.apache.commons.compress.archivers.sevenz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.compress.archivers.sevenz.Coder;
import org.apache.commons.compress.archivers.sevenz.CoderBase;
import org.tukaani.xz.DeltaOptions;
import org.tukaani.xz.FinishableOutputStream;
import org.tukaani.xz.FinishableWrapperOutputStream;
import org.tukaani.xz.UnsupportedOptionsException;

class DeltaDecoder
extends CoderBase {
    DeltaDecoder() {
        super(Number.class);
    }

    InputStream decode(InputStream in2, Coder coder, byte[] password) throws IOException {
        return new DeltaOptions(this.getOptionsFromCoder(coder)).getInputStream(in2);
    }

    OutputStream encode(OutputStream out, Object options) throws IOException {
        int distance = DeltaDecoder.numberOptionOrDefault(options, 1);
        try {
            return new DeltaOptions(distance).getOutputStream((FinishableOutputStream)new FinishableWrapperOutputStream(out));
        }
        catch (UnsupportedOptionsException ex2) {
            throw new IOException(ex2.getMessage());
        }
    }

    byte[] getOptionsAsProperties(Object options) {
        return new byte[]{(byte)(DeltaDecoder.numberOptionOrDefault(options, 1) - 1)};
    }

    Object getOptionsFromCoder(Coder coder, InputStream in2) {
        return this.getOptionsFromCoder(coder);
    }

    private int getOptionsFromCoder(Coder coder) {
        if (coder.properties == null || coder.properties.length == 0) {
            return 1;
        }
        return (0xFF & coder.properties[0]) + 1;
    }
}

