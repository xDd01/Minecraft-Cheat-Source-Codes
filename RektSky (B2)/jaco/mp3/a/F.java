package jaco.mp3.a;

import jaco.mp3.a.a.*;
import javax.sound.sampled.*;

public final class F extends a
{
    private SourceDataLine a;
    private AudioFormat b;
    private byte[] c;
    
    public F() {
        this.a = null;
        this.b = null;
        this.c = new byte[4096];
    }
    
    @Override
    protected final void b() {
        if (this.a != null) {
            this.a.close();
        }
    }
    
    @Override
    protected final void b(final short[] array, final int n, final int n2) {
        if (this.a == null) {
            final RuntimeException ex = null;
            try {
                if (this.b == null) {
                    final b e = this.e();
                    this.b = new AudioFormat((float)e.a(), 16, e.b(), true, false);
                }
                final Line line;
                if ((line = AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, this.b))) instanceof SourceDataLine) {
                    (this.a = (SourceDataLine)line).open(this.b);
                    this.a.start();
                }
            }
            catch (RuntimeException ex) {}
            catch (LinkageError ex) {}
            catch (LineUnavailableException ex) {}
            if (this.a == null) {
                throw new q("cannot obtain source audio line", ex);
            }
        }
        this.a.write(this.c(array, n, n2), 0, n2 << 1);
    }
    
    private byte[] c(final short[] array, int n, int n2) {
        final F f = this;
        final int n3 = n2 << 1;
        this = f;
        if (f.c.length < n3) {
            this.c = new byte[n3 + 1024];
        }
        final byte[] c = this.c;
        int n4 = 0;
        while (n2-- > 0) {
            final short n5 = array[n++];
            c[n4++] = (byte)n5;
            c[n4++] = (byte)(n5 >>> 8);
        }
        return c;
    }
    
    @Override
    protected final void d() {
        if (this.a != null) {
            this.a.drain();
        }
    }
}
