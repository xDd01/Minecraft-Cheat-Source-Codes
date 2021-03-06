package shadersmod.client;

import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderProgramData {
   public int programIDGL;
   public int uniform_texture;
   public int uniform_lightmap;
   public int uniform_normals;
   public int uniform_specular;
   public int uniform_shadow;
   public int uniform_watershadow;
   public int uniform_shadowtex0;
   public int uniform_shadowtex1;
   public int uniform_depthtex0;
   public int uniform_depthtex1;
   public int uniform_shadowcolor;
   public int uniform_shadowcolor0;
   public int uniform_shadowcolor1;
   public int uniform_noisetex;
   public int uniform_gcolor;
   public int uniform_gdepth;
   public int uniform_gnormal;
   public int uniform_composite;
   public int uniform_gaux1;
   public int uniform_gaux2;
   public int uniform_gaux3;
   public int uniform_gaux4;
   public int uniform_colortex0;
   public int uniform_colortex1;
   public int uniform_colortex2;
   public int uniform_colortex3;
   public int uniform_colortex4;
   public int uniform_colortex5;
   public int uniform_colortex6;
   public int uniform_colortex7;
   public int uniform_gdepthtex;
   public int uniform_depthtex2;
   public int uniform_tex;
   public int uniform_heldItemId;
   public int uniform_heldBlockLightValue;
   public int uniform_fogMode;
   public int uniform_fogColor;
   public int uniform_skyColor;
   public int uniform_worldTime;
   public int uniform_moonPhase;
   public int uniform_frameTimeCounter;
   public int uniform_sunAngle;
   public int uniform_shadowAngle;
   public int uniform_rainStrength;
   public int uniform_aspectRatio;
   public int uniform_viewWidth;
   public int uniform_viewHeight;
   public int uniform_near;
   public int uniform_far;
   public int uniform_sunPosition;
   public int uniform_moonPosition;
   public int uniform_upPosition;
   public int uniform_previousCameraPosition;
   public int uniform_cameraPosition;
   public int uniform_gbufferModelView;
   public int uniform_gbufferModelViewInverse;
   public int uniform_gbufferPreviousProjection;
   public int uniform_gbufferProjection;
   public int uniform_gbufferProjectionInverse;
   public int uniform_gbufferPreviousModelView;
   public int uniform_shadowProjection;
   public int uniform_shadowProjectionInverse;
   public int uniform_shadowModelView;
   public int uniform_shadowModelViewInverse;
   public int uniform_wetness;
   public int uniform_eyeAltitude;
   public int uniform_eyeBrightness;
   public int uniform_eyeBrightnessSmooth;
   public int uniform_terrainTextureSize;
   public int uniform_terrainIconSize;
   public int uniform_isEyeInWater;
   public int uniform_hideGUI;
   public int uniform_centerDepthSmooth;
   public int uniform_atlasSize;

   public ShaderProgramData(int var1) {
      this.programIDGL = var1;
      this.uniform_texture = ARBShaderObjects.glGetUniformLocationARB(var1, "texture");
      this.uniform_lightmap = ARBShaderObjects.glGetUniformLocationARB(var1, "lightmap");
      this.uniform_normals = ARBShaderObjects.glGetUniformLocationARB(var1, "normals");
      this.uniform_specular = ARBShaderObjects.glGetUniformLocationARB(var1, "specular");
      this.uniform_shadow = ARBShaderObjects.glGetUniformLocationARB(var1, "shadow");
      this.uniform_watershadow = ARBShaderObjects.glGetUniformLocationARB(var1, "watershadow");
      this.uniform_shadowtex0 = ARBShaderObjects.glGetUniformLocationARB(var1, "shadowtex0");
      this.uniform_shadowtex1 = ARBShaderObjects.glGetUniformLocationARB(var1, "shadowtex1");
      this.uniform_depthtex0 = ARBShaderObjects.glGetUniformLocationARB(var1, "depthtex0");
      this.uniform_depthtex1 = ARBShaderObjects.glGetUniformLocationARB(var1, "depthtex1");
      this.uniform_shadowcolor = ARBShaderObjects.glGetUniformLocationARB(var1, "shadowcolor");
      this.uniform_shadowcolor0 = ARBShaderObjects.glGetUniformLocationARB(var1, "shadowcolor0");
      this.uniform_shadowcolor1 = ARBShaderObjects.glGetUniformLocationARB(var1, "shadowcolor1");
      this.uniform_noisetex = ARBShaderObjects.glGetUniformLocationARB(var1, "noisetex");
      this.uniform_gcolor = ARBShaderObjects.glGetUniformLocationARB(var1, "gcolor");
      this.uniform_gdepth = ARBShaderObjects.glGetUniformLocationARB(var1, "gdepth");
      this.uniform_gnormal = ARBShaderObjects.glGetUniformLocationARB(var1, "gnormal");
      this.uniform_composite = ARBShaderObjects.glGetUniformLocationARB(var1, "composite");
      this.uniform_gaux1 = ARBShaderObjects.glGetUniformLocationARB(var1, "gaux1");
      this.uniform_gaux2 = ARBShaderObjects.glGetUniformLocationARB(var1, "gaux2");
      this.uniform_gaux3 = ARBShaderObjects.glGetUniformLocationARB(var1, "gaux3");
      this.uniform_gaux4 = ARBShaderObjects.glGetUniformLocationARB(var1, "gaux4");
      this.uniform_colortex0 = ARBShaderObjects.glGetUniformLocationARB(var1, "colortex0");
      this.uniform_colortex1 = ARBShaderObjects.glGetUniformLocationARB(var1, "colortex1");
      this.uniform_colortex2 = ARBShaderObjects.glGetUniformLocationARB(var1, "colortex2");
      this.uniform_colortex3 = ARBShaderObjects.glGetUniformLocationARB(var1, "colortex3");
      this.uniform_colortex4 = ARBShaderObjects.glGetUniformLocationARB(var1, "colortex4");
      this.uniform_colortex5 = ARBShaderObjects.glGetUniformLocationARB(var1, "colortex5");
      this.uniform_colortex6 = ARBShaderObjects.glGetUniformLocationARB(var1, "colortex6");
      this.uniform_colortex7 = ARBShaderObjects.glGetUniformLocationARB(var1, "colortex7");
      this.uniform_gdepthtex = ARBShaderObjects.glGetUniformLocationARB(var1, "gdepthtex");
      this.uniform_depthtex2 = ARBShaderObjects.glGetUniformLocationARB(var1, "depthtex2");
      this.uniform_tex = ARBShaderObjects.glGetUniformLocationARB(var1, "tex");
      this.uniform_heldItemId = ARBShaderObjects.glGetUniformLocationARB(var1, "heldItemId");
      this.uniform_heldBlockLightValue = ARBShaderObjects.glGetUniformLocationARB(var1, "heldBlockLightValue");
      this.uniform_fogMode = ARBShaderObjects.glGetUniformLocationARB(var1, "fogMode");
      this.uniform_fogColor = ARBShaderObjects.glGetUniformLocationARB(var1, "fogColor");
      this.uniform_skyColor = ARBShaderObjects.glGetUniformLocationARB(var1, "skyColor");
      this.uniform_worldTime = ARBShaderObjects.glGetUniformLocationARB(var1, "worldTime");
      this.uniform_moonPhase = ARBShaderObjects.glGetUniformLocationARB(var1, "moonPhase");
      this.uniform_frameTimeCounter = ARBShaderObjects.glGetUniformLocationARB(var1, "frameTimeCounter");
      this.uniform_sunAngle = ARBShaderObjects.glGetUniformLocationARB(var1, "sunAngle");
      this.uniform_shadowAngle = ARBShaderObjects.glGetUniformLocationARB(var1, "shadowAngle");
      this.uniform_rainStrength = ARBShaderObjects.glGetUniformLocationARB(var1, "rainStrength");
      this.uniform_aspectRatio = ARBShaderObjects.glGetUniformLocationARB(var1, "aspectRatio");
      this.uniform_viewWidth = ARBShaderObjects.glGetUniformLocationARB(var1, "viewWidth");
      this.uniform_viewHeight = ARBShaderObjects.glGetUniformLocationARB(var1, "viewHeight");
      this.uniform_near = ARBShaderObjects.glGetUniformLocationARB(var1, "near");
      this.uniform_far = ARBShaderObjects.glGetUniformLocationARB(var1, "far");
      this.uniform_sunPosition = ARBShaderObjects.glGetUniformLocationARB(var1, "sunPosition");
      this.uniform_moonPosition = ARBShaderObjects.glGetUniformLocationARB(var1, "moonPosition");
      this.uniform_upPosition = ARBShaderObjects.glGetUniformLocationARB(var1, "upPosition");
      this.uniform_previousCameraPosition = ARBShaderObjects.glGetUniformLocationARB(var1, "previousCameraPosition");
      this.uniform_cameraPosition = ARBShaderObjects.glGetUniformLocationARB(var1, "cameraPosition");
      this.uniform_gbufferModelView = ARBShaderObjects.glGetUniformLocationARB(var1, "gbufferModelView");
      this.uniform_gbufferModelViewInverse = ARBShaderObjects.glGetUniformLocationARB(var1, "gbufferModelViewInverse");
      this.uniform_gbufferPreviousProjection = ARBShaderObjects.glGetUniformLocationARB(var1, "gbufferPreviousProjection");
      this.uniform_gbufferProjection = ARBShaderObjects.glGetUniformLocationARB(var1, "gbufferProjection");
      this.uniform_gbufferProjectionInverse = ARBShaderObjects.glGetUniformLocationARB(var1, "gbufferProjectionInverse");
      this.uniform_gbufferPreviousModelView = ARBShaderObjects.glGetUniformLocationARB(var1, "gbufferPreviousModelView");
      this.uniform_shadowProjection = ARBShaderObjects.glGetUniformLocationARB(var1, "shadowProjection");
      this.uniform_shadowProjectionInverse = ARBShaderObjects.glGetUniformLocationARB(var1, "shadowProjectionInverse");
      this.uniform_shadowModelView = ARBShaderObjects.glGetUniformLocationARB(var1, "shadowModelView");
      this.uniform_shadowModelViewInverse = ARBShaderObjects.glGetUniformLocationARB(var1, "shadowModelViewInverse");
      this.uniform_wetness = ARBShaderObjects.glGetUniformLocationARB(var1, "wetness");
      this.uniform_eyeAltitude = ARBShaderObjects.glGetUniformLocationARB(var1, "eyeAltitude");
      this.uniform_eyeBrightness = ARBShaderObjects.glGetUniformLocationARB(var1, "eyeBrightness");
      this.uniform_eyeBrightnessSmooth = ARBShaderObjects.glGetUniformLocationARB(var1, "eyeBrightnessSmooth");
      this.uniform_terrainTextureSize = ARBShaderObjects.glGetUniformLocationARB(var1, "terrainTextureSize");
      this.uniform_terrainIconSize = ARBShaderObjects.glGetUniformLocationARB(var1, "terrainIconSize");
      this.uniform_isEyeInWater = ARBShaderObjects.glGetUniformLocationARB(var1, "isEyeInWater");
      this.uniform_hideGUI = ARBShaderObjects.glGetUniformLocationARB(var1, "hideGUI");
      this.uniform_centerDepthSmooth = ARBShaderObjects.glGetUniformLocationARB(var1, "centerDepthSmooth");
      this.uniform_atlasSize = ARBShaderObjects.glGetUniformLocationARB(var1, "atlasSize");
   }
}
