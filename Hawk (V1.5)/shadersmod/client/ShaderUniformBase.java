package shadersmod.client;

import org.lwjgl.opengl.ARBShaderObjects;

public abstract class ShaderUniformBase {
   private int program = -1;
   private String name;
   private int location = -1;

   public int getProgram() {
      return this.program;
   }

   public int getLocation() {
      return this.location;
   }

   public ShaderUniformBase(String var1) {
      this.name = var1;
   }

   public boolean isDefined() {
      return this.location >= 0;
   }

   protected abstract void onProgramChanged();

   public String getName() {
      return this.name;
   }

   public void setProgram(int var1) {
      if (this.program != var1) {
         this.program = var1;
         this.location = ARBShaderObjects.glGetUniformLocationARB(var1, this.name);
         this.onProgramChanged();
      }

   }
}
