/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.text;

import com.ibm.icu.text.CharsetMatch;
import com.ibm.icu.text.CharsetRecog_2022;
import com.ibm.icu.text.CharsetRecog_UTF8;
import com.ibm.icu.text.CharsetRecog_Unicode;
import com.ibm.icu.text.CharsetRecog_mbcs;
import com.ibm.icu.text.CharsetRecog_sbcs;
import com.ibm.icu.text.CharsetRecognizer;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CharsetDetector {
    private static final int kBufSize = 8000;
    byte[] fInputBytes = new byte[8000];
    int fInputLen;
    short[] fByteStats = new short[256];
    boolean fC1Bytes = false;
    String fDeclaredEncoding;
    byte[] fRawInput;
    int fRawLength;
    InputStream fInputStream;
    boolean fStripTags = false;
    private static ArrayList<CharsetRecognizer> fCSRecognizers = CharsetDetector.createRecognizers();
    private static String[] fCharsetNames;

    public CharsetDetector setDeclaredEncoding(String encoding) {
        this.fDeclaredEncoding = encoding;
        return this;
    }

    public CharsetDetector setText(byte[] in2) {
        this.fRawInput = in2;
        this.fRawLength = in2.length;
        return this;
    }

    public CharsetDetector setText(InputStream in2) throws IOException {
        int bytesRead;
        this.fInputStream = in2;
        this.fInputStream.mark(8000);
        this.fRawInput = new byte[8000];
        this.fRawLength = 0;
        for (int remainingLength = 8000; remainingLength > 0 && (bytesRead = this.fInputStream.read(this.fRawInput, this.fRawLength, remainingLength)) > 0; remainingLength -= bytesRead) {
            this.fRawLength += bytesRead;
        }
        this.fInputStream.reset();
        return this;
    }

    public CharsetMatch detect() {
        CharsetMatch[] matches = this.detectAll();
        if (matches == null || matches.length == 0) {
            return null;
        }
        return matches[0];
    }

    public CharsetMatch[] detectAll() {
        ArrayList<CharsetMatch> matches = new ArrayList<CharsetMatch>();
        this.MungeInput();
        for (CharsetRecognizer csr : fCSRecognizers) {
            CharsetMatch m2 = csr.match(this);
            if (m2 == null) continue;
            matches.add(m2);
        }
        Collections.sort(matches);
        Collections.reverse(matches);
        CharsetMatch[] resultArray = new CharsetMatch[matches.size()];
        resultArray = matches.toArray(resultArray);
        return resultArray;
    }

    public Reader getReader(InputStream in2, String declaredEncoding) {
        this.fDeclaredEncoding = declaredEncoding;
        try {
            this.setText(in2);
            CharsetMatch match = this.detect();
            if (match == null) {
                return null;
            }
            return match.getReader();
        }
        catch (IOException e2) {
            return null;
        }
    }

    public String getString(byte[] in2, String declaredEncoding) {
        this.fDeclaredEncoding = declaredEncoding;
        try {
            this.setText(in2);
            CharsetMatch match = this.detect();
            if (match == null) {
                return null;
            }
            return match.getString(-1);
        }
        catch (IOException e2) {
            return null;
        }
    }

    public static String[] getAllDetectableCharsets() {
        return fCharsetNames;
    }

    public boolean inputFilterEnabled() {
        return this.fStripTags;
    }

    public boolean enableInputFilter(boolean filter) {
        boolean previous = this.fStripTags;
        this.fStripTags = filter;
        return previous;
    }

    private void MungeInput() {
        int srci = 0;
        int dsti = 0;
        boolean inMarkup = false;
        int openTags = 0;
        int badTags = 0;
        if (this.fStripTags) {
            for (srci = 0; srci < this.fRawLength && dsti < this.fInputBytes.length; ++srci) {
                byte b2 = this.fRawInput[srci];
                if (b2 == 60) {
                    if (inMarkup) {
                        ++badTags;
                    }
                    inMarkup = true;
                    ++openTags;
                }
                if (!inMarkup) {
                    this.fInputBytes[dsti++] = b2;
                }
                if (b2 != 62) continue;
                inMarkup = false;
            }
            this.fInputLen = dsti;
        }
        if (openTags < 5 || openTags / 5 < badTags || this.fInputLen < 100 && this.fRawLength > 600) {
            int limit = this.fRawLength;
            if (limit > 8000) {
                limit = 8000;
            }
            for (srci = 0; srci < limit; ++srci) {
                this.fInputBytes[srci] = this.fRawInput[srci];
            }
            this.fInputLen = srci;
        }
        Arrays.fill(this.fByteStats, (short)0);
        for (srci = 0; srci < this.fInputLen; ++srci) {
            int val;
            int n2 = val = this.fInputBytes[srci] & 0xFF;
            this.fByteStats[n2] = (short)(this.fByteStats[n2] + 1);
        }
        this.fC1Bytes = false;
        for (int i2 = 128; i2 <= 159; ++i2) {
            if (this.fByteStats[i2] == 0) continue;
            this.fC1Bytes = true;
            break;
        }
    }

    private static ArrayList<CharsetRecognizer> createRecognizers() {
        ArrayList<CharsetRecognizer> recognizers = new ArrayList<CharsetRecognizer>();
        recognizers.add(new CharsetRecog_UTF8());
        recognizers.add(new CharsetRecog_Unicode.CharsetRecog_UTF_16_BE());
        recognizers.add(new CharsetRecog_Unicode.CharsetRecog_UTF_16_LE());
        recognizers.add(new CharsetRecog_Unicode.CharsetRecog_UTF_32_BE());
        recognizers.add(new CharsetRecog_Unicode.CharsetRecog_UTF_32_LE());
        recognizers.add(new CharsetRecog_mbcs.CharsetRecog_sjis());
        recognizers.add(new CharsetRecog_2022.CharsetRecog_2022JP());
        recognizers.add(new CharsetRecog_2022.CharsetRecog_2022CN());
        recognizers.add(new CharsetRecog_2022.CharsetRecog_2022KR());
        recognizers.add(new CharsetRecog_mbcs.CharsetRecog_gb_18030());
        recognizers.add(new CharsetRecog_mbcs.CharsetRecog_euc.CharsetRecog_euc_jp());
        recognizers.add(new CharsetRecog_mbcs.CharsetRecog_euc.CharsetRecog_euc_kr());
        recognizers.add(new CharsetRecog_mbcs.CharsetRecog_big5());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_1());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_2());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_5_ru());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_6_ar());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_7_el());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_8_I_he());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_8_he());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_windows_1251());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_windows_1256());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_KOI8_R());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_9_tr());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_IBM424_he_rtl());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_IBM424_he_ltr());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_IBM420_ar_rtl());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_IBM420_ar_ltr());
        String[] charsetNames = new String[recognizers.size()];
        int out = 0;
        for (int i2 = 0; i2 < recognizers.size(); ++i2) {
            String name = ((CharsetRecognizer)recognizers.get(i2)).getName();
            if (out != 0 && name.equals(charsetNames[out - 1])) continue;
            charsetNames[out++] = name;
        }
        fCharsetNames = new String[out];
        System.arraycopy(charsetNames, 0, fCharsetNames, 0, out);
        return recognizers;
    }
}

