/*
 * Decompiled with CFR 0.152.
 */
package io.socket.engineio.parser;

import java.io.UnsupportedEncodingException;

public class Base64 {
    public static final int DEFAULT = 0;
    public static final int NO_PADDING = 1;
    public static final int NO_WRAP = 2;
    public static final int CRLF = 4;
    public static final int URL_SAFE = 8;
    public static final int NO_CLOSE = 16;

    public static byte[] decode(String str, int flags) {
        return Base64.decode(str.getBytes(), flags);
    }

    public static byte[] decode(byte[] input, int flags) {
        return Base64.decode(input, 0, input.length, flags);
    }

    public static byte[] decode(byte[] input, int offset, int len, int flags) {
        Decoder decoder = new Decoder(flags, new byte[len * 3 / 4]);
        if (!decoder.process(input, offset, len, true)) {
            throw new IllegalArgumentException("bad base-64");
        }
        if (decoder.op == decoder.output.length) {
            return decoder.output;
        }
        byte[] temp = new byte[decoder.op];
        System.arraycopy(decoder.output, 0, temp, 0, decoder.op);
        return temp;
    }

    public static String encodeToString(byte[] input, int flags) {
        try {
            return new String(Base64.encode(input, flags), "US-ASCII");
        }
        catch (UnsupportedEncodingException e2) {
            throw new AssertionError((Object)e2);
        }
    }

    public static String encodeToString(byte[] input, int offset, int len, int flags) {
        try {
            return new String(Base64.encode(input, offset, len, flags), "US-ASCII");
        }
        catch (UnsupportedEncodingException e2) {
            throw new AssertionError((Object)e2);
        }
    }

    public static byte[] encode(byte[] input, int flags) {
        return Base64.encode(input, 0, input.length, flags);
    }

    public static byte[] encode(byte[] input, int offset, int len, int flags) {
        Encoder encoder = new Encoder(flags, null);
        int output_len = len / 3 * 4;
        if (encoder.do_padding) {
            if (len % 3 > 0) {
                output_len += 4;
            }
        } else {
            switch (len % 3) {
                case 0: {
                    break;
                }
                case 1: {
                    output_len += 2;
                    break;
                }
                case 2: {
                    output_len += 3;
                }
            }
        }
        if (encoder.do_newline && len > 0) {
            output_len += ((len - 1) / 57 + 1) * (encoder.do_cr ? 2 : 1);
        }
        encoder.output = new byte[output_len];
        encoder.process(input, offset, len, true);
        assert (encoder.op == output_len);
        return encoder.output;
    }

    private Base64() {
    }

    static class Encoder
    extends Coder {
        public static final int LINE_GROUPS = 19;
        private static final byte[] ENCODE = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
        private static final byte[] ENCODE_WEBSAFE = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95};
        private final byte[] tail;
        int tailLen;
        private int count;
        public final boolean do_padding;
        public final boolean do_newline;
        public final boolean do_cr;
        private final byte[] alphabet;

        public Encoder(int flags, byte[] output) {
            this.output = output;
            this.do_padding = (flags & 1) == 0;
            this.do_newline = (flags & 2) == 0;
            this.do_cr = (flags & 4) != 0;
            this.alphabet = (flags & 8) == 0 ? ENCODE : ENCODE_WEBSAFE;
            this.tail = new byte[2];
            this.tailLen = 0;
            this.count = this.do_newline ? 19 : -1;
        }

        @Override
        public int maxOutputSize(int len) {
            return len * 8 / 5 + 10;
        }

        @Override
        public boolean process(byte[] input, int offset, int len, boolean finish) {
            byte[] alphabet = this.alphabet;
            byte[] output = this.output;
            int op2 = 0;
            int count = this.count;
            int p2 = offset;
            len += offset;
            int v2 = -1;
            switch (this.tailLen) {
                case 0: {
                    break;
                }
                case 1: {
                    if (p2 + 2 > len) break;
                    v2 = (this.tail[0] & 0xFF) << 16 | (input[p2++] & 0xFF) << 8 | input[p2++] & 0xFF;
                    this.tailLen = 0;
                    break;
                }
                case 2: {
                    if (p2 + 1 > len) break;
                    v2 = (this.tail[0] & 0xFF) << 16 | (this.tail[1] & 0xFF) << 8 | input[p2++] & 0xFF;
                    this.tailLen = 0;
                }
            }
            if (v2 != -1) {
                output[op2++] = alphabet[v2 >> 18 & 0x3F];
                output[op2++] = alphabet[v2 >> 12 & 0x3F];
                output[op2++] = alphabet[v2 >> 6 & 0x3F];
                output[op2++] = alphabet[v2 & 0x3F];
                if (--count == 0) {
                    if (this.do_cr) {
                        output[op2++] = 13;
                    }
                    output[op2++] = 10;
                    count = 19;
                }
            }
            while (p2 + 3 <= len) {
                v2 = (input[p2] & 0xFF) << 16 | (input[p2 + 1] & 0xFF) << 8 | input[p2 + 2] & 0xFF;
                output[op2] = alphabet[v2 >> 18 & 0x3F];
                output[op2 + 1] = alphabet[v2 >> 12 & 0x3F];
                output[op2 + 2] = alphabet[v2 >> 6 & 0x3F];
                output[op2 + 3] = alphabet[v2 & 0x3F];
                p2 += 3;
                op2 += 4;
                if (--count != 0) continue;
                if (this.do_cr) {
                    output[op2++] = 13;
                }
                output[op2++] = 10;
                count = 19;
            }
            if (finish) {
                if (p2 - this.tailLen == len - 1) {
                    int t2 = 0;
                    v2 = ((this.tailLen > 0 ? this.tail[t2++] : input[p2++]) & 0xFF) << 4;
                    this.tailLen -= t2;
                    output[op2++] = alphabet[v2 >> 6 & 0x3F];
                    output[op2++] = alphabet[v2 & 0x3F];
                    if (this.do_padding) {
                        output[op2++] = 61;
                        output[op2++] = 61;
                    }
                    if (this.do_newline) {
                        if (this.do_cr) {
                            output[op2++] = 13;
                        }
                        output[op2++] = 10;
                    }
                } else if (p2 - this.tailLen == len - 2) {
                    int t3 = 0;
                    v2 = ((this.tailLen > 1 ? this.tail[t3++] : input[p2++]) & 0xFF) << 10 | ((this.tailLen > 0 ? this.tail[t3++] : input[p2++]) & 0xFF) << 2;
                    this.tailLen -= t3;
                    output[op2++] = alphabet[v2 >> 12 & 0x3F];
                    output[op2++] = alphabet[v2 >> 6 & 0x3F];
                    output[op2++] = alphabet[v2 & 0x3F];
                    if (this.do_padding) {
                        output[op2++] = 61;
                    }
                    if (this.do_newline) {
                        if (this.do_cr) {
                            output[op2++] = 13;
                        }
                        output[op2++] = 10;
                    }
                } else if (this.do_newline && op2 > 0 && count != 19) {
                    if (this.do_cr) {
                        output[op2++] = 13;
                    }
                    output[op2++] = 10;
                }
                assert (this.tailLen == 0);
                assert (p2 == len);
            } else if (p2 == len - 1) {
                this.tail[this.tailLen++] = input[p2];
            } else if (p2 == len - 2) {
                this.tail[this.tailLen++] = input[p2];
                this.tail[this.tailLen++] = input[p2 + 1];
            }
            this.op = op2;
            this.count = count;
            return true;
        }
    }

    static class Decoder
    extends Coder {
        private static final int[] DECODE = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        private static final int[] DECODE_WEBSAFE = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        private static final int SKIP = -1;
        private static final int EQUALS = -2;
        private int state;
        private int value;
        private final int[] alphabet;

        public Decoder(int flags, byte[] output) {
            this.output = output;
            this.alphabet = (flags & 8) == 0 ? DECODE : DECODE_WEBSAFE;
            this.state = 0;
            this.value = 0;
        }

        @Override
        public int maxOutputSize(int len) {
            return len * 3 / 4 + 10;
        }

        @Override
        public boolean process(byte[] input, int offset, int len, boolean finish) {
            if (this.state == 6) {
                return false;
            }
            int p2 = offset;
            len += offset;
            int state = this.state;
            int value = this.value;
            int op2 = 0;
            byte[] output = this.output;
            int[] alphabet = this.alphabet;
            while (p2 < len) {
                if (state == 0) {
                    while (p2 + 4 <= len && (value = alphabet[input[p2] & 0xFF] << 18 | alphabet[input[p2 + 1] & 0xFF] << 12 | alphabet[input[p2 + 2] & 0xFF] << 6 | alphabet[input[p2 + 3] & 0xFF]) >= 0) {
                        output[op2 + 2] = (byte)value;
                        output[op2 + 1] = (byte)(value >> 8);
                        output[op2] = (byte)(value >> 16);
                        op2 += 3;
                        p2 += 4;
                    }
                    if (p2 >= len) break;
                }
                int d2 = alphabet[input[p2++] & 0xFF];
                switch (state) {
                    case 0: {
                        if (d2 >= 0) {
                            value = d2;
                            ++state;
                            break;
                        }
                        if (d2 == -1) break;
                        this.state = 6;
                        return false;
                    }
                    case 1: {
                        if (d2 >= 0) {
                            value = value << 6 | d2;
                            ++state;
                            break;
                        }
                        if (d2 == -1) break;
                        this.state = 6;
                        return false;
                    }
                    case 2: {
                        if (d2 >= 0) {
                            value = value << 6 | d2;
                            ++state;
                            break;
                        }
                        if (d2 == -2) {
                            output[op2++] = (byte)(value >> 4);
                            state = 4;
                            break;
                        }
                        if (d2 == -1) break;
                        this.state = 6;
                        return false;
                    }
                    case 3: {
                        if (d2 >= 0) {
                            value = value << 6 | d2;
                            output[op2 + 2] = (byte)value;
                            output[op2 + 1] = (byte)(value >> 8);
                            output[op2] = (byte)(value >> 16);
                            op2 += 3;
                            state = 0;
                            break;
                        }
                        if (d2 == -2) {
                            output[op2 + 1] = (byte)(value >> 2);
                            output[op2] = (byte)(value >> 10);
                            op2 += 2;
                            state = 5;
                            break;
                        }
                        if (d2 == -1) break;
                        this.state = 6;
                        return false;
                    }
                    case 4: {
                        if (d2 == -2) {
                            ++state;
                            break;
                        }
                        if (d2 == -1) break;
                        this.state = 6;
                        return false;
                    }
                    case 5: {
                        if (d2 == -1) break;
                        this.state = 6;
                        return false;
                    }
                }
            }
            if (!finish) {
                this.state = state;
                this.value = value;
                this.op = op2;
                return true;
            }
            switch (state) {
                case 0: {
                    break;
                }
                case 1: {
                    this.state = 6;
                    return false;
                }
                case 2: {
                    output[op2++] = (byte)(value >> 4);
                    break;
                }
                case 3: {
                    output[op2++] = (byte)(value >> 10);
                    output[op2++] = (byte)(value >> 2);
                    break;
                }
                case 4: {
                    this.state = 6;
                    return false;
                }
            }
            this.state = state;
            this.op = op2;
            return true;
        }
    }

    static abstract class Coder {
        public byte[] output;
        public int op;

        Coder() {
        }

        public abstract boolean process(byte[] var1, int var2, int var3, boolean var4);

        public abstract int maxOutputSize(int var1);
    }
}

