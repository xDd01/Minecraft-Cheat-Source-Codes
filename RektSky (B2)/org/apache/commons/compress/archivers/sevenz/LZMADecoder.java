package org.apache.commons.compress.archivers.sevenz;

import java.io.*;
import org.tukaani.xz.*;
import org.apache.commons.compress.utils.*;

class LZMADecoder extends CoderBase
{
    LZMADecoder() {
        super((Class<?>[])new Class[] { LZMA2Options.class, Number.class });
    }
    
    @Override
    InputStream decode(final String archiveName, final InputStream in, final long uncompressedLength, final Coder coder, final byte[] password) throws IOException {
        final byte propsByte = coder.properties[0];
        final int dictSize = this.getDictionarySize(coder);
        if (dictSize > 2147483632) {
            throw new IOException("Dictionary larger than 4GiB maximum size used in " + archiveName);
        }
        return (InputStream)new LZMAInputStream(in, uncompressedLength, propsByte, dictSize);
    }
    
    @Override
    OutputStream encode(final OutputStream out, final Object opts) throws IOException {
        return new FlushShieldFilterOutputStream((OutputStream)new LZMAOutputStream(out, this.getOptions(opts), false));
    }
    
    @Override
    byte[] getOptionsAsProperties(final Object opts) throws IOException {
        final LZMA2Options options = this.getOptions(opts);
        final byte props = (byte)((options.getPb() * 5 + options.getLp()) * 9 + options.getLc());
        final int dictSize = options.getDictSize();
        final byte[] o = new byte[5];
        o[0] = props;
        ByteUtils.toLittleEndian(o, dictSize, 1, 4);
        return o;
    }
    
    @Override
    Object getOptionsFromCoder(final Coder coder, final InputStream in) throws IOException {
        final byte propsByte = coder.properties[0];
        int props = propsByte & 0xFF;
        final int pb = props / 45;
        props -= pb * 9 * 5;
        final int lp = props / 9;
        final int lc = props - lp * 9;
        final LZMA2Options opts = new LZMA2Options();
        opts.setPb(pb);
        opts.setLcLp(lc, lp);
        opts.setDictSize(this.getDictionarySize(coder));
        return opts;
    }
    
    private int getDictionarySize(final Coder coder) throws IllegalArgumentException {
        return (int)ByteUtils.fromLittleEndian(coder.properties, 1, 4);
    }
    
    private LZMA2Options getOptions(final Object opts) throws IOException {
        if (opts instanceof LZMA2Options) {
            return (LZMA2Options)opts;
        }
        final LZMA2Options options = new LZMA2Options();
        options.setDictSize(this.numberOptionOrDefault(opts));
        return options;
    }
    
    private int numberOptionOrDefault(final Object opts) {
        return CoderBase.numberOptionOrDefault(opts, 8388608);
    }
}
