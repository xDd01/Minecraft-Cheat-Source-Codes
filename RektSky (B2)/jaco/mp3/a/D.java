package jaco.mp3.a;

public final class D
{
    private static int[][] a;
    private int b;
    private int c;
    private int d;
    private int e;
    private int f;
    private int g;
    private int h;
    private int i;
    private int j;
    private int k;
    private double[] l;
    private boolean m;
    private int n;
    private int o;
    private byte[] p;
    private byte q;
    private o r;
    private short s;
    private int t;
    private int u;
    private static int[][][] v;
    private static String[][][] w;
    
    static {
        D.a = new int[][] { { 22050, 24000, 16000, 1 }, { 44100, 48000, 32000, 1 }, { 11025, 12000, 8000, 1 } };
        D.v = new int[][][] { { { 0, 32000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 144000, 160000, 176000, 192000, 224000, 256000, 0 }, { 0, 8000, 16000, 24000, 32000, 40000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 144000, 160000, 0 }, { 0, 8000, 16000, 24000, 32000, 40000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 144000, 160000, 0 } }, { { 0, 32000, 64000, 96000, 128000, 160000, 192000, 224000, 256000, 288000, 320000, 352000, 384000, 416000, 448000, 0 }, { 0, 32000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 160000, 192000, 224000, 256000, 320000, 384000, 0 }, { 0, 32000, 40000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 160000, 192000, 224000, 256000, 320000, 0 } }, { { 0, 32000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 144000, 160000, 176000, 192000, 224000, 256000, 0 }, { 0, 8000, 16000, 24000, 32000, 40000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 144000, 160000, 0 }, { 0, 8000, 16000, 24000, 32000, 40000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 144000, 160000, 0 } } };
        D.w = new String[][][] { { { "free format", "32 kbit/s", "48 kbit/s", "56 kbit/s", "64 kbit/s", "80 kbit/s", "96 kbit/s", "112 kbit/s", "128 kbit/s", "144 kbit/s", "160 kbit/s", "176 kbit/s", "192 kbit/s", "224 kbit/s", "256 kbit/s", "forbidden" }, { "free format", "8 kbit/s", "16 kbit/s", "24 kbit/s", "32 kbit/s", "40 kbit/s", "48 kbit/s", "56 kbit/s", "64 kbit/s", "80 kbit/s", "96 kbit/s", "112 kbit/s", "128 kbit/s", "144 kbit/s", "160 kbit/s", "forbidden" }, { "free format", "8 kbit/s", "16 kbit/s", "24 kbit/s", "32 kbit/s", "40 kbit/s", "48 kbit/s", "56 kbit/s", "64 kbit/s", "80 kbit/s", "96 kbit/s", "112 kbit/s", "128 kbit/s", "144 kbit/s", "160 kbit/s", "forbidden" } }, { { "free format", "32 kbit/s", "64 kbit/s", "96 kbit/s", "128 kbit/s", "160 kbit/s", "192 kbit/s", "224 kbit/s", "256 kbit/s", "288 kbit/s", "320 kbit/s", "352 kbit/s", "384 kbit/s", "416 kbit/s", "448 kbit/s", "forbidden" }, { "free format", "32 kbit/s", "48 kbit/s", "56 kbit/s", "64 kbit/s", "80 kbit/s", "96 kbit/s", "112 kbit/s", "128 kbit/s", "160 kbit/s", "192 kbit/s", "224 kbit/s", "256 kbit/s", "320 kbit/s", "384 kbit/s", "forbidden" }, { "free format", "32 kbit/s", "40 kbit/s", "48 kbit/s", "56 kbit/s", "64 kbit/s", "80 kbit/s", "96 kbit/s", "112 kbit/s", "128 kbit/s", "160 kbit/s", "192 kbit/s", "224 kbit/s", "256 kbit/s", "320 kbit/s", "forbidden" } }, { { "free format", "32 kbit/s", "48 kbit/s", "56 kbit/s", "64 kbit/s", "80 kbit/s", "96 kbit/s", "112 kbit/s", "128 kbit/s", "144 kbit/s", "160 kbit/s", "176 kbit/s", "192 kbit/s", "224 kbit/s", "256 kbit/s", "forbidden" }, { "free format", "8 kbit/s", "16 kbit/s", "24 kbit/s", "32 kbit/s", "40 kbit/s", "48 kbit/s", "56 kbit/s", "64 kbit/s", "80 kbit/s", "96 kbit/s", "112 kbit/s", "128 kbit/s", "144 kbit/s", "160 kbit/s", "forbidden" }, { "free format", "8 kbit/s", "16 kbit/s", "24 kbit/s", "32 kbit/s", "40 kbit/s", "48 kbit/s", "56 kbit/s", "64 kbit/s", "80 kbit/s", "96 kbit/s", "112 kbit/s", "128 kbit/s", "144 kbit/s", "160 kbit/s", "forbidden" } } };
    }
    
    D() {
        this.l = new double[] { -1.0, 384.0, 1152.0, 1152.0 };
        this.q = 0;
    }
    
    @Override
    public final String toString() {
        final StringBuffer sb;
        (sb = new StringBuffer(200)).append("Layer ");
        final StringBuffer sb2 = sb;
        String s = null;
        switch (this.b) {
            case 1: {
                s = "I";
                break;
            }
            case 2: {
                s = "II";
                break;
            }
            case 3: {
                s = "III";
                break;
            }
            default: {
                s = null;
                break;
            }
        }
        sb2.append(s);
        sb.append(" frame ");
        final StringBuffer sb3 = sb;
        String s2 = null;
        switch (this.h) {
            case 0: {
                s2 = "Stereo";
                break;
            }
            case 1: {
                s2 = "Joint stereo";
                break;
            }
            case 2: {
                s2 = "Dual channel";
                break;
            }
            case 3: {
                s2 = "Single channel";
                break;
            }
            default: {
                s2 = null;
                break;
            }
        }
        sb3.append(s2);
        sb.append(' ');
        final StringBuffer sb4 = sb;
        String s3 = null;
        switch (this.g) {
            case 1: {
                s3 = "MPEG-1";
                break;
            }
            case 0: {
                s3 = "MPEG-2 LSF";
                break;
            }
            case 2: {
                s3 = "MPEG-2.5 LSF";
                break;
            }
            default: {
                s3 = null;
                break;
            }
        }
        sb4.append(s3);
        if (this.c != 0) {
            sb.append(" no");
        }
        sb.append(" checksums");
        sb.append(' ');
        final StringBuffer sb5 = sb;
        final D d;
        String s4 = null;
        switch ((d = this).i) {
            case 2: {
                s4 = ((d.g == 1) ? "32 kHz" : ((d.g == 0) ? "16 kHz" : "8 kHz"));
                break;
            }
            case 0: {
                s4 = ((d.g == 1) ? "44.1 kHz" : ((d.g == 0) ? "22.05 kHz" : "11.025 kHz"));
                break;
            }
            case 1: {
                s4 = ((d.g == 1) ? "48 kHz" : ((d.g == 0) ? "24 kHz" : "12 kHz"));
                break;
            }
            default: {
                s4 = null;
                break;
            }
        }
        sb5.append(s4);
        sb.append(',');
        sb.append(' ');
        final StringBuffer sb6 = sb;
        final D d2;
        String string;
        if ((d2 = this).m) {
            int n4;
            if ((this = d2).m) {
                final float n = (float)(this.o << 3);
                final D d3;
                float n3;
                if ((d3 = this).m) {
                    double n2 = d3.l[d3.b] / d3.e();
                    if (d3.g == 0 || d3.g == 2) {
                        n2 /= 2.0;
                    }
                    n3 = (float)(n2 * 1000.0);
                }
                else {
                    n3 = (new float[][] { { 8.707483f, 8.0f, 12.0f }, { 26.12245f, 24.0f, 36.0f }, { 26.12245f, 24.0f, 36.0f } })[d3.b - 1][d3.i];
                }
                n4 = (int)(n / (n3 * this.n)) * 1000;
            }
            else {
                n4 = D.v[this.g][this.b - 1][this.d];
            }
            string = String.valueOf(Integer.toString(n4 / 1000)) + " kb/s";
        }
        else {
            string = D.w[d2.g][d2.b - 1][d2.d];
        }
        sb6.append(string);
        return sb.toString();
    }
    
    final void a(final z z, final o[] array) {
        boolean b = false;
        int a;
        do {
            a = z.a(this.q);
            if (this.q == 0) {
                this.g = (a >>> 19 & 0x1);
                if ((a >>> 20 & 0x1) == 0x0) {
                    if (this.g != 0) {
                        throw z.b(256);
                    }
                    this.g = 2;
                }
                if ((this.i = (a >>> 10 & 0x3)) == 3) {
                    throw z.b(256);
                }
            }
            this.b = (4 - (a >>> 17) & 0x3);
            this.c = (a >>> 16 & 0x1);
            this.d = (a >>> 12 & 0xF);
            this.e = (a >>> 9 & 0x1);
            this.h = (a >>> 6 & 0x3);
            this.f = (a >>> 4 & 0x3);
            if (this.h == 1) {
                this.k = (this.f << 2) + 4;
            }
            else {
                this.k = 0;
            }
            if (this.b == 1) {
                this.j = 32;
            }
            else {
                int d = this.d;
                if (this.h != 3) {
                    if (d == 4) {
                        d = 1;
                    }
                    else {
                        d -= 4;
                    }
                }
                if (d == 1 || d == 2) {
                    if (this.i == 2) {
                        this.j = 12;
                    }
                    else {
                        this.j = 8;
                    }
                }
                else if (this.i == 1 || (d >= 3 && d <= 5)) {
                    this.j = 27;
                }
                else {
                    this.j = 30;
                }
            }
            if (this.k > this.j) {
                this.k = this.j;
            }
            Label_0606: {
                if (this.b == 1) {
                    this.t = 12 * D.v[this.g][0][this.d] / D.a[this.g][this.i];
                    if (this.e != 0) {
                        ++this.t;
                    }
                    this.t <<= 2;
                }
                else {
                    this.t = 144 * D.v[this.g][this.b - 1][this.d] / D.a[this.g][this.i];
                    if (this.g == 0 || this.g == 2) {
                        this.t >>= 1;
                    }
                    if (this.e != 0) {
                        ++this.t;
                    }
                    if (this.b == 3) {
                        if (this.g == 1) {
                            this.u = this.t - ((this.h == 3) ? 17 : 32) - ((this.c != 0) ? 0 : 2) - 4;
                            break Label_0606;
                        }
                        this.u = this.t - ((this.h == 3) ? 9 : 17) - ((this.c != 0) ? 0 : 2) - 4;
                        break Label_0606;
                    }
                }
                this.u = 0;
            }
            this.t -= 4;
            final int t = this.t;
            final int c = z.c(this.t);
            if (this.t >= 0 && c != this.t) {
                throw z.b(261);
            }
            if (z.a((int)this.q)) {
                if (this.q == 0) {
                    this.q = z.b;
                    z.e(a & 0xFFF80CC0);
                }
                b = true;
            }
            else {
                z.c();
            }
        } while (!b);
        z.e();
        if (this.c == 0) {
            this.s = (short)z.d(16);
            if (this.r == null) {
                this.r = new o();
            }
            this.r.a(a, 16);
            array[0] = this.r;
            return;
        }
        array[0] = null;
    }
    
    final void a(final byte[] array) {
        final String s = "Xing";
        final byte[] array2 = new byte[4];
        int n;
        if (this.g == 1) {
            if (this.h == 3) {
                n = 17;
            }
            else {
                n = 32;
            }
        }
        else if (this.h == 3) {
            n = 9;
        }
        else {
            n = 17;
        }
        try {
            System.arraycopy(array, n, array2, 0, 4);
            if (s.equals(new String(array2))) {
                this.m = true;
                this.n = -1;
                this.o = -1;
                this.p = new byte[100];
                final byte[] array3 = new byte[4];
                System.arraycopy(array, n + 4, array3, 0, array3.length);
                int n2 = 4 + array3.length;
                if ((array3[3] & 0x1) != 0x0) {
                    System.arraycopy(array, n + n2, array2, 0, array2.length);
                    this.n = ((array2[0] << 24 & 0xFF000000) | (array2[1] << 16 & 0xFF0000) | (array2[2] << 8 & 0xFF00) | (array2[3] & 0xFF));
                    n2 += 4;
                }
                if ((array3[3] & 0x2) != 0x0) {
                    System.arraycopy(array, n + n2, array2, 0, array2.length);
                    this.o = ((array2[0] << 24 & 0xFF000000) | (array2[1] << 16 & 0xFF0000) | (array2[2] << 8 & 0xFF00) | (array2[3] & 0xFF));
                    n2 += 4;
                }
                if ((array3[3] & 0x4) != 0x0) {
                    System.arraycopy(array, n + n2, this.p, 0, this.p.length);
                    n2 += this.p.length;
                }
                if ((array3[3] & 0x8) != 0x0) {
                    System.arraycopy(array, n + n2, array2, 0, array2.length);
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            throw new s("XingVBRHeader Corrupted", ex);
        }
        final String s2 = "VBRI";
        try {
            System.arraycopy(array, 32, array2, 0, 4);
            if (s2.equals(new String(array2))) {
                this.m = true;
                this.n = -1;
                this.o = -1;
                this.p = new byte[100];
                System.arraycopy(array, 42, array2, 0, array2.length);
                this.o = ((array2[0] << 24 & 0xFF000000) | (array2[1] << 16 & 0xFF0000) | (array2[2] << 8 & 0xFF00) | (array2[3] & 0xFF));
                System.arraycopy(array, 46, array2, 0, array2.length);
                this.n = ((array2[0] << 24 & 0xFF000000) | (array2[1] << 16 & 0xFF0000) | (array2[2] << 8 & 0xFF00) | (array2[3] & 0xFF));
            }
        }
        catch (ArrayIndexOutOfBoundsException ex2) {
            throw new s("VBRIVBRHeader Corrupted", ex2);
        }
    }
    
    public final int a() {
        return this.g;
    }
    
    public final int b() {
        return this.b;
    }
    
    public final int c() {
        return this.d;
    }
    
    public final int d() {
        return this.i;
    }
    
    public final int e() {
        return D.a[this.g][this.i];
    }
    
    public final int f() {
        return this.h;
    }
    
    public final boolean g() {
        return this.s == this.r.a();
    }
    
    public final int h() {
        return this.u;
    }
    
    public final int i() {
        return this.f;
    }
    
    public final int j() {
        return this.j;
    }
    
    public final int k() {
        return this.k;
    }
}
