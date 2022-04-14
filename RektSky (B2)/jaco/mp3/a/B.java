package jaco.mp3.a;

final class B extends w implements k
{
    public B() {
    }
    
    @Override
    protected final void b() {
        if (this.c == 3) {
            for (int i = 0; i < this.d; ++i) {
                this.e[i] = new i(i);
            }
            return;
        }
        if (this.c == 1) {
            int j;
            for (j = 0; j < this.b.k(); ++j) {
                this.e[j] = new E(j);
            }
            while (j < this.d) {
                this.e[j] = new v(j);
                ++j;
            }
            return;
        }
        for (int k = 0; k < this.d; ++k) {
            this.e[k] = new E(k);
        }
    }
    
    @Override
    protected final void c() {
        for (int i = 0; i < this.d; ++i) {
            ((i)this.e[i]).a(this.a, this.f);
        }
    }
}
