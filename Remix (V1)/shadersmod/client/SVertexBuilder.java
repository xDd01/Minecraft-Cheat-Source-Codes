package shadersmod.client;

import net.minecraft.client.renderer.*;
import net.minecraft.block.state.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.block.*;
import net.minecraft.client.renderer.vertex.*;
import org.lwjgl.opengl.*;
import java.nio.*;

public class SVertexBuilder
{
    int vertexSize;
    int offsetNormal;
    int offsetUV;
    int offsetUVCenter;
    boolean hasNormal;
    boolean hasTangent;
    boolean hasUV;
    boolean hasUVCenter;
    long[] entityData;
    int entityDataIndex;
    
    public SVertexBuilder() {
        this.entityData = new long[10];
        this.entityDataIndex = 0;
        this.entityData[this.entityDataIndex] = 0L;
    }
    
    public static void initVertexBuilder(final WorldRenderer wrr) {
        wrr.sVertexBuilder = new SVertexBuilder();
    }
    
    public static void pushEntity(final IBlockState blockState, final BlockPos blockPos, final IBlockAccess blockAccess, final WorldRenderer wrr) {
        final Block block = blockState.getBlock();
        final int blockID = Block.getIdFromBlock(block);
        final int renderType = block.getRenderType();
        final int meta = block.getMetaFromState(blockState);
        final int dataLo = ((renderType & 0xFFFF) << 16) + (blockID & 0xFFFF);
        final int dataHi = meta & 0xFFFF;
        wrr.sVertexBuilder.pushEntity(((long)dataHi << 32) + dataLo);
    }
    
    public static void popEntity(final WorldRenderer wrr) {
        wrr.sVertexBuilder.popEntity();
    }
    
    public static boolean popEntity(final boolean value, final WorldRenderer wrr) {
        wrr.sVertexBuilder.popEntity();
        return value;
    }
    
    public static void endSetVertexFormat(final WorldRenderer wrr) {
        final SVertexBuilder svb = wrr.sVertexBuilder;
        final VertexFormat vf = wrr.func_178973_g();
        svb.vertexSize = vf.func_177338_f() / 4;
        svb.hasNormal = vf.func_177350_b();
        svb.hasTangent = svb.hasNormal;
        svb.hasUV = vf.func_177347_a(0);
        svb.offsetNormal = (svb.hasNormal ? (vf.func_177342_c() / 4) : 0);
        svb.offsetUV = (svb.hasUV ? (vf.func_177344_b(0) / 4) : 0);
        svb.offsetUVCenter = 8;
    }
    
    public static void beginAddVertex(final WorldRenderer wrr) {
        if (wrr.vertexCount == 0) {
            endSetVertexFormat(wrr);
        }
    }
    
    public static void endAddVertex(final WorldRenderer wrr) {
        final SVertexBuilder svb = wrr.sVertexBuilder;
        if (svb.vertexSize == 14) {
            if (wrr.drawMode == 7 && wrr.vertexCount % 4 == 0) {
                svb.calcNormal(wrr, wrr.rawBufferIndex - 4 * svb.vertexSize);
            }
            final long eData = svb.entityData[svb.entityDataIndex];
            final int pos = wrr.rawBufferIndex - 14 + 12;
            wrr.rawIntBuffer.put(pos, (int)eData);
            wrr.rawIntBuffer.put(pos + 1, (int)(eData >> 32));
        }
    }
    
    public static void beginAddVertexData(final WorldRenderer wrr, final int[] data) {
        if (wrr.vertexCount == 0) {
            endSetVertexFormat(wrr);
        }
        final SVertexBuilder svb = wrr.sVertexBuilder;
        if (svb.vertexSize == 14) {
            final long eData = svb.entityData[svb.entityDataIndex];
            for (int pos = 12; pos + 1 < data.length; pos += 14) {
                data[pos] = (int)eData;
                data[pos + 1] = (int)(eData >> 32);
            }
        }
    }
    
    public static void endAddVertexData(final WorldRenderer wrr) {
        final SVertexBuilder svb = wrr.sVertexBuilder;
        if (svb.vertexSize == 14 && wrr.drawMode == 7 && wrr.vertexCount % 4 == 0) {
            svb.calcNormal(wrr, wrr.rawBufferIndex - 4 * svb.vertexSize);
        }
    }
    
    public static void calcNormalChunkLayer(final WorldRenderer wrr) {
        if (wrr.func_178973_g().func_177350_b() && wrr.drawMode == 7 && wrr.vertexCount % 4 == 0) {
            final SVertexBuilder svb = wrr.sVertexBuilder;
            endSetVertexFormat(wrr);
            for (int indexEnd = wrr.vertexCount * svb.vertexSize, index = 0; index < indexEnd; index += svb.vertexSize * 4) {
                svb.calcNormal(wrr, index);
            }
        }
    }
    
    public static void drawArrays(final int drawMode, final int first, final int count, final WorldRenderer wrr) {
        if (count != 0) {
            final VertexFormat vf = wrr.func_178973_g();
            final int vertexSizeByte = vf.func_177338_f();
            if (vertexSizeByte == 56) {
                final ByteBuffer bb = wrr.func_178966_f();
                bb.position(32);
                GL20.glVertexAttribPointer(Shaders.midTexCoordAttrib, 2, 5126, false, vertexSizeByte, bb);
                bb.position(40);
                GL20.glVertexAttribPointer(Shaders.tangentAttrib, 4, 5122, false, vertexSizeByte, bb);
                bb.position(48);
                GL20.glVertexAttribPointer(Shaders.entityAttrib, 3, 5122, false, vertexSizeByte, bb);
                bb.position(0);
                GL20.glEnableVertexAttribArray(Shaders.midTexCoordAttrib);
                GL20.glEnableVertexAttribArray(Shaders.tangentAttrib);
                GL20.glEnableVertexAttribArray(Shaders.entityAttrib);
                GL11.glDrawArrays(drawMode, first, count);
                GL20.glDisableVertexAttribArray(Shaders.midTexCoordAttrib);
                GL20.glDisableVertexAttribArray(Shaders.tangentAttrib);
                GL20.glDisableVertexAttribArray(Shaders.entityAttrib);
            }
            else {
                GL11.glDrawArrays(drawMode, first, count);
            }
        }
    }
    
    public static void startTexturedQuad(final WorldRenderer wrr) {
        wrr.setVertexFormat(SVertexFormat.defVertexFormatTextured);
    }
    
    public void pushEntity(final long data) {
        ++this.entityDataIndex;
        this.entityData[this.entityDataIndex] = data;
    }
    
    public void popEntity() {
        this.entityData[this.entityDataIndex] = 0L;
        --this.entityDataIndex;
    }
    
    public void calcNormal(final WorldRenderer wrr, final int baseIndex) {
        final FloatBuffer floatBuffer = wrr.rawFloatBuffer;
        final IntBuffer intBuffer = wrr.rawIntBuffer;
        final int rbi = wrr.rawBufferIndex;
        final float v0x = floatBuffer.get(baseIndex + 0 * this.vertexSize);
        final float v0y = floatBuffer.get(baseIndex + 0 * this.vertexSize + 1);
        final float v0z = floatBuffer.get(baseIndex + 0 * this.vertexSize + 2);
        final float v0u = floatBuffer.get(baseIndex + 0 * this.vertexSize + this.offsetUV);
        final float v0v = floatBuffer.get(baseIndex + 0 * this.vertexSize + this.offsetUV + 1);
        final float v1x = floatBuffer.get(baseIndex + 1 * this.vertexSize);
        final float v1y = floatBuffer.get(baseIndex + 1 * this.vertexSize + 1);
        final float v1z = floatBuffer.get(baseIndex + 1 * this.vertexSize + 2);
        final float v1u = floatBuffer.get(baseIndex + 1 * this.vertexSize + this.offsetUV);
        final float v1v = floatBuffer.get(baseIndex + 1 * this.vertexSize + this.offsetUV + 1);
        final float v2x = floatBuffer.get(baseIndex + 2 * this.vertexSize);
        final float v2y = floatBuffer.get(baseIndex + 2 * this.vertexSize + 1);
        final float v2z = floatBuffer.get(baseIndex + 2 * this.vertexSize + 2);
        final float v2u = floatBuffer.get(baseIndex + 2 * this.vertexSize + this.offsetUV);
        final float v2v = floatBuffer.get(baseIndex + 2 * this.vertexSize + this.offsetUV + 1);
        final float v3x = floatBuffer.get(baseIndex + 3 * this.vertexSize);
        final float v3y = floatBuffer.get(baseIndex + 3 * this.vertexSize + 1);
        final float v3z = floatBuffer.get(baseIndex + 3 * this.vertexSize + 2);
        final float v3u = floatBuffer.get(baseIndex + 3 * this.vertexSize + this.offsetUV);
        final float v3v = floatBuffer.get(baseIndex + 3 * this.vertexSize + this.offsetUV + 1);
        float x1 = v2x - v0x;
        float y1 = v2y - v0y;
        float z1 = v2z - v0z;
        float x2 = v3x - v1x;
        float y2 = v3y - v1y;
        float z2 = v3z - v1z;
        float vnx = y1 * z2 - y2 * z1;
        float vny = z1 * x2 - z2 * x1;
        float vnz = x1 * y2 - x2 * y1;
        float lensq = vnx * vnx + vny * vny + vnz * vnz;
        float mult = (lensq != 0.0) ? ((float)(1.0 / Math.sqrt(lensq))) : 1.0f;
        vnx *= mult;
        vny *= mult;
        vnz *= mult;
        x1 = v1x - v0x;
        y1 = v1y - v0y;
        z1 = v1z - v0z;
        final float u1 = v1u - v0u;
        final float v1 = v1v - v0v;
        x2 = v2x - v0x;
        y2 = v2y - v0y;
        z2 = v2z - v0z;
        final float u2 = v2u - v0u;
        final float v2 = v2v - v0v;
        final float d = u1 * v2 - u2 * v1;
        final float r = (d != 0.0f) ? (1.0f / d) : 1.0f;
        float tan1x = (v2 * x1 - v1 * x2) * r;
        float tan1y = (v2 * y1 - v1 * y2) * r;
        float tan1z = (v2 * z1 - v1 * z2) * r;
        float tan2x = (u1 * x2 - u2 * x1) * r;
        float tan2y = (u1 * y2 - u2 * y1) * r;
        float tan2z = (u1 * z2 - u2 * z1) * r;
        lensq = tan1x * tan1x + tan1y * tan1y + tan1z * tan1z;
        mult = ((lensq != 0.0) ? ((float)(1.0 / Math.sqrt(lensq))) : 1.0f);
        tan1x *= mult;
        tan1y *= mult;
        tan1z *= mult;
        lensq = tan2x * tan2x + tan2y * tan2y + tan2z * tan2z;
        mult = ((lensq != 0.0) ? ((float)(1.0 / Math.sqrt(lensq))) : 1.0f);
        tan2x *= mult;
        tan2y *= mult;
        tan2z *= mult;
        final float tan3x = vnz * tan1y - vny * tan1z;
        final float tan3y = vnx * tan1z - vnz * tan1x;
        final float tan3z = vny * tan1x - vnx * tan1y;
        final float tan1w = (tan2x * tan3x + tan2y * tan3y + tan2z * tan3z < 0.0f) ? -1.0f : 1.0f;
        final int bnx = (int)(vnx * 127.0f) & 0xFF;
        final int bny = (int)(vny * 127.0f) & 0xFF;
        final int bnz = (int)(vnz * 127.0f) & 0xFF;
        final int packedNormal = (bnz << 16) + (bny << 8) + bnx;
        intBuffer.put(baseIndex + 0 * this.vertexSize + this.offsetNormal, packedNormal);
        intBuffer.put(baseIndex + 1 * this.vertexSize + this.offsetNormal, packedNormal);
        intBuffer.put(baseIndex + 2 * this.vertexSize + this.offsetNormal, packedNormal);
        intBuffer.put(baseIndex + 3 * this.vertexSize + this.offsetNormal, packedNormal);
        final int packedTan1xy = ((int)(tan1x * 32767.0f) & 0xFFFF) + (((int)(tan1y * 32767.0f) & 0xFFFF) << 16);
        final int packedTan1zw = ((int)(tan1z * 32767.0f) & 0xFFFF) + (((int)(tan1w * 32767.0f) & 0xFFFF) << 16);
        intBuffer.put(baseIndex + 0 * this.vertexSize + 10, packedTan1xy);
        intBuffer.put(baseIndex + 0 * this.vertexSize + 10 + 1, packedTan1zw);
        intBuffer.put(baseIndex + 1 * this.vertexSize + 10, packedTan1xy);
        intBuffer.put(baseIndex + 1 * this.vertexSize + 10 + 1, packedTan1zw);
        intBuffer.put(baseIndex + 2 * this.vertexSize + 10, packedTan1xy);
        intBuffer.put(baseIndex + 2 * this.vertexSize + 10 + 1, packedTan1zw);
        intBuffer.put(baseIndex + 3 * this.vertexSize + 10, packedTan1xy);
        intBuffer.put(baseIndex + 3 * this.vertexSize + 10 + 1, packedTan1zw);
        final float midU = (v0u + v1u + v2u + v3u) / 4.0f;
        final float midV = (v0v + v1v + v2v + v3v) / 4.0f;
        floatBuffer.put(baseIndex + 0 * this.vertexSize + 8, midU);
        floatBuffer.put(baseIndex + 0 * this.vertexSize + 8 + 1, midV);
        floatBuffer.put(baseIndex + 1 * this.vertexSize + 8, midU);
        floatBuffer.put(baseIndex + 1 * this.vertexSize + 8 + 1, midV);
        floatBuffer.put(baseIndex + 2 * this.vertexSize + 8, midU);
        floatBuffer.put(baseIndex + 2 * this.vertexSize + 8 + 1, midV);
        floatBuffer.put(baseIndex + 3 * this.vertexSize + 8, midU);
        floatBuffer.put(baseIndex + 3 * this.vertexSize + 8 + 1, midV);
    }
}
