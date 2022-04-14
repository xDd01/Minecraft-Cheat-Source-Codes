package io.netty.handler.ssl;

import java.security.cert.*;
import io.netty.util.*;
import io.netty.buffer.*;
import io.netty.handler.codec.base64.*;
import java.util.*;
import java.util.regex.*;
import java.security.*;
import java.io.*;
import io.netty.util.internal.logging.*;

final class PemReader
{
    private static final InternalLogger logger;
    private static final Pattern CERT_PATTERN;
    private static final Pattern KEY_PATTERN;
    
    static ByteBuf[] readCertificates(final File file) throws CertificateException {
        String content;
        try {
            content = readContent(file);
        }
        catch (IOException e) {
            throw new CertificateException("failed to read a file: " + file, e);
        }
        final List<ByteBuf> certs = new ArrayList<ByteBuf>();
        final Matcher m = PemReader.CERT_PATTERN.matcher(content);
        for (int start = 0; m.find(start); start = m.end()) {
            final ByteBuf base64 = Unpooled.copiedBuffer(m.group(1), CharsetUtil.US_ASCII);
            final ByteBuf der = Base64.decode(base64);
            base64.release();
            certs.add(der);
        }
        if (certs.isEmpty()) {
            throw new CertificateException("found no certificates: " + file);
        }
        return certs.toArray(new ByteBuf[certs.size()]);
    }
    
    static ByteBuf readPrivateKey(final File file) throws KeyException {
        String content;
        try {
            content = readContent(file);
        }
        catch (IOException e) {
            throw new KeyException("failed to read a file: " + file, e);
        }
        final Matcher m = PemReader.KEY_PATTERN.matcher(content);
        if (!m.find()) {
            throw new KeyException("found no private key: " + file);
        }
        final ByteBuf base64 = Unpooled.copiedBuffer(m.group(1), CharsetUtil.US_ASCII);
        final ByteBuf der = Base64.decode(base64);
        base64.release();
        return der;
    }
    
    private static String readContent(final File file) throws IOException {
        final InputStream in = new FileInputStream(file);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            final byte[] buf = new byte[8192];
            while (true) {
                final int ret = in.read(buf);
                if (ret < 0) {
                    break;
                }
                out.write(buf, 0, ret);
            }
            return out.toString(CharsetUtil.US_ASCII.name());
        }
        finally {
            safeClose(in);
            safeClose(out);
        }
    }
    
    private static void safeClose(final InputStream in) {
        try {
            in.close();
        }
        catch (IOException e) {
            PemReader.logger.warn("Failed to close a stream.", e);
        }
    }
    
    private static void safeClose(final OutputStream out) {
        try {
            out.close();
        }
        catch (IOException e) {
            PemReader.logger.warn("Failed to close a stream.", e);
        }
    }
    
    private PemReader() {
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(PemReader.class);
        CERT_PATTERN = Pattern.compile("-+BEGIN\\s+.*CERTIFICATE[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*CERTIFICATE[^-]*-+", 2);
        KEY_PATTERN = Pattern.compile("-+BEGIN\\s+.*PRIVATE\\s+KEY[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*PRIVATE\\s+KEY[^-]*-+", 2);
    }
}
