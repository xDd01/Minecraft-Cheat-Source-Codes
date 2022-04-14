package shadersmod.client;

public class MultiTexID {
   public int spec;
   public int base;
   public int norm;

   public MultiTexID(int var1, int var2, int var3) {
      this.base = var1;
      this.norm = var2;
      this.spec = var3;
   }
}
