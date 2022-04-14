package jaco.mp3.a;

public abstract class C
{
    public abstract void a(final int p0, final short p1);
    
    public void a(final int n, final float[] array) {
        int i = 0;
        while (i < 32) {
            final float n2;
            this.a(n, (short)(((n2 = array[i++]) > 32767.0f) ? 32767 : ((n2 < -32768.0f) ? -32768 : ((short)n2))));
        }
    }
    
    public abstract void c();
}
