package org.apache.commons.codec.cli;

import org.apache.commons.codec.binary.*;
import org.apache.commons.codec.digest.*;
import java.security.*;
import java.io.*;
import java.nio.charset.*;
import java.util.*;

public class Digest
{
    private final String algorithm;
    private final String[] args;
    private final String[] inputs;
    
    public static void main(final String[] args) throws IOException {
        new Digest(args).run();
    }
    
    private Digest(final String[] args) {
        if (args == null) {
            throw new IllegalArgumentException("args");
        }
        if (args.length == 0) {
            throw new IllegalArgumentException(String.format("Usage: java %s [algorithm] [FILE|DIRECTORY|string] ...", Digest.class.getName()));
        }
        this.args = args;
        this.algorithm = args[0];
        if (args.length <= 1) {
            this.inputs = null;
        }
        else {
            System.arraycopy(args, 1, this.inputs = new String[args.length - 1], 0, this.inputs.length);
        }
    }
    
    private void println(final String prefix, final byte[] digest) {
        this.println(prefix, digest, null);
    }
    
    private void println(final String prefix, final byte[] digest, final String fileName) {
        System.out.println(prefix + Hex.encodeHexString(digest) + ((fileName != null) ? ("  " + fileName) : ""));
    }
    
    private void run() throws IOException {
        if (this.algorithm.equalsIgnoreCase("ALL") || this.algorithm.equals("*")) {
            this.run(MessageDigestAlgorithms.values());
            return;
        }
        final MessageDigest messageDigest = DigestUtils.getDigest(this.algorithm, null);
        if (messageDigest != null) {
            this.run("", messageDigest);
        }
        else {
            this.run("", DigestUtils.getDigest(this.algorithm.toUpperCase(Locale.ROOT)));
        }
    }
    
    private void run(final String[] digestAlgorithms) throws IOException {
        for (final String messageDigestAlgorithm : digestAlgorithms) {
            if (DigestUtils.isAvailable(messageDigestAlgorithm)) {
                this.run(messageDigestAlgorithm + " ", messageDigestAlgorithm);
            }
        }
    }
    
    private void run(final String prefix, final MessageDigest messageDigest) throws IOException {
        if (this.inputs == null) {
            this.println(prefix, DigestUtils.digest(messageDigest, System.in));
            return;
        }
        for (final String source : this.inputs) {
            final File file = new File(source);
            if (file.isFile()) {
                this.println(prefix, DigestUtils.digest(messageDigest, file), source);
            }
            else if (file.isDirectory()) {
                final File[] listFiles = file.listFiles();
                if (listFiles != null) {
                    this.run(prefix, messageDigest, listFiles);
                }
            }
            else {
                final byte[] bytes = source.getBytes(Charset.defaultCharset());
                this.println(prefix, DigestUtils.digest(messageDigest, bytes));
            }
        }
    }
    
    private void run(final String prefix, final MessageDigest messageDigest, final File[] files) throws IOException {
        for (final File file : files) {
            if (file.isFile()) {
                this.println(prefix, DigestUtils.digest(messageDigest, file), file.getName());
            }
        }
    }
    
    private void run(final String prefix, final String messageDigestAlgorithm) throws IOException {
        this.run(prefix, DigestUtils.getDigest(messageDigestAlgorithm));
    }
    
    @Override
    public String toString() {
        return String.format("%s %s", super.toString(), Arrays.toString(this.args));
    }
}
