package jaco.mp3.a;

final class E extends i
{
    private int n;
    private int o;
    private float p;
    private float q;
    private float r;
    private int[] s;
    private float[] t;
    private float[] u;
    private float[] v;
    private float[] w;
    
    public E(final int n) {
        super(n);
        this.s = new int[1];
        this.t = new float[] { 0.0f };
        this.v = new float[] { 0.0f };
        this.w = new float[] { 0.0f };
        this.u = new float[3];
    }
    
    @Override
    public final void a(final z z, final D d, final o o) {
        final int a = this.a(d);
        this.b = z.d(a);
        this.n = z.d(a);
        if (o != null) {
            o.a(this.b, a);
            o.a(this.n, a);
        }
    }
    
    @Override
    public final void a(final z z, final o o) {
        if (this.b != 0) {
            this.c = z.d(2);
            if (o != null) {
                o.a(this.c, 2);
            }
        }
        if (this.n != 0) {
            this.o = z.d(2);
            if (o != null) {
                o.a(this.o, 2);
            }
        }
    }
    
    @Override
    public final void a(final z z, final D d) {
        super.a(z, d);
        if (this.n != 0) {
            switch (this.o) {
                case 0: {
                    this.p = E.m[z.d(6)];
                    this.q = E.m[z.d(6)];
                    this.r = E.m[z.d(6)];
                    break;
                }
                case 1: {
                    final float n = E.m[z.d(6)];
                    this.q = n;
                    this.p = n;
                    this.r = E.m[z.d(6)];
                    break;
                }
                case 2: {
                    final float p2 = E.m[z.d(6)];
                    this.r = p2;
                    this.q = p2;
                    this.p = p2;
                    break;
                }
                case 3: {
                    this.p = E.m[z.d(6)];
                    final float n2 = E.m[z.d(6)];
                    this.r = n2;
                    this.q = n2;
                    break;
                }
            }
            this.a(d, this.n, 1, this.t, this.s, this.v, this.w);
        }
    }
    
    @Override
    public final boolean a(final z z) {
        final boolean a = super.a(z);
        if (this.n != 0) {
            if (this.g[1] != null) {
                final int d = z.d(this.s[0]);
                final int n = d + (d << 1);
                final float[] u = this.u;
                final float[] array = this.g[1];
                int n2 = n;
                u[0] = array[n];
                ++n2;
                u[1] = array[n2];
                ++n2;
                u[2] = array[n2];
            }
            else {
                this.u[0] = (float)(z.d(this.s[0]) * this.t[0] - 1.0);
                this.u[1] = (float)(z.d(this.s[0]) * this.t[0] - 1.0);
                this.u[2] = (float)(z.d(this.s[0]) * this.t[0] - 1.0);
            }
        }
        return a;
    }
    
    @Override
    public final boolean a(final int n, final y y, final y y2) {
        final boolean a = super.a(n, y, y2);
        if (this.n != 0 && n != 1) {
            float n2 = this.u[this.i - 1];
            if (this.g[1] == null) {
                n2 = (n2 + this.w[0]) * this.v[0];
            }
            float n3;
            if (this.h <= 4) {
                n3 = n2 * this.p;
            }
            else if (this.h <= 8) {
                n3 = n2 * this.q;
            }
            else {
                n3 = n2 * this.r;
            }
            if (n == 0) {
                y2.a(n3, this.a);
            }
            else {
                y.a(n3, this.a);
            }
        }
        return a;
    }
}
