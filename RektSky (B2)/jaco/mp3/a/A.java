package jaco.mp3.a;

public final class A extends C
{
    private short[] a;
    private int[] b;
    private int c;
    
    public A(int i, final int c) {
        this.a = new short[2304];
        this.b = new int[2];
        this.c = c;
        for (i = 0; i < c; ++i) {
            this.b[i] = (short)i;
        }
    }
    
    public final short[] a() {
        return this.a;
    }
    
    public final int b() {
        return this.b[0];
    }
    
    @Override
    public final void a(final int n, final short n2) {
        this.a[this.b[n]] = n2;
        final int[] b = this.b;
        b[n] += this.c;
    }
    
    @Override
    public final void a(final int n, final float[] array) {
        int n2 = this.b[n];
        float n3;
        float n4;
        for (int i = 0; i < 32; this.a[n2] = (short)(((n3 = array[i++]) > 32767.0f) ? (n4 = 32767.0f) : ((n3 < -32767.0f) ? (n4 = -32767.0f) : (n4 = n3))), n2 += this.c) {}
        this.b[n] = n2;
    }
    
    @Override
    public final void c() {
        for (int i = 0; i < this.c; ++i) {
            this.b[i] = (short)i;
        }
    }
}
