package net.minecraft.realms;

public class RealmsDefaultVertexFormat
{
    public static final RealmsVertexFormat BLOCK;
    public static final RealmsVertexFormat BLOCK_NORMALS;
    public static final RealmsVertexFormat ENTITY;
    public static final RealmsVertexFormat PARTICLE;
    public static final RealmsVertexFormat POSITION;
    public static final RealmsVertexFormat POSITION_COLOR;
    public static final RealmsVertexFormat POSITION_TEX;
    public static final RealmsVertexFormat POSITION_NORMAL;
    public static final RealmsVertexFormat POSITION_TEX_COLOR;
    public static final RealmsVertexFormat POSITION_TEX_NORMAL;
    public static final RealmsVertexFormat POSITION_TEX2_COLOR;
    public static final RealmsVertexFormat POSITION_TEX_COLOR_NORMAL;
    public static final RealmsVertexFormatElement ELEMENT_POSITION;
    public static final RealmsVertexFormatElement ELEMENT_COLOR;
    public static final RealmsVertexFormatElement ELEMENT_UV0;
    public static final RealmsVertexFormatElement ELEMENT_UV1;
    public static final RealmsVertexFormatElement ELEMENT_NORMAL;
    public static final RealmsVertexFormatElement ELEMENT_PADDING;
    
    static {
        BLOCK = new RealmsVertexFormat(new bmu());
        BLOCK_NORMALS = new RealmsVertexFormat(new bmu());
        ENTITY = new RealmsVertexFormat(new bmu());
        PARTICLE = new RealmsVertexFormat(new bmu());
        POSITION = new RealmsVertexFormat(new bmu());
        POSITION_COLOR = new RealmsVertexFormat(new bmu());
        POSITION_TEX = new RealmsVertexFormat(new bmu());
        POSITION_NORMAL = new RealmsVertexFormat(new bmu());
        POSITION_TEX_COLOR = new RealmsVertexFormat(new bmu());
        POSITION_TEX_NORMAL = new RealmsVertexFormat(new bmu());
        POSITION_TEX2_COLOR = new RealmsVertexFormat(new bmu());
        POSITION_TEX_COLOR_NORMAL = new RealmsVertexFormat(new bmu());
        ELEMENT_POSITION = new RealmsVertexFormatElement(new bmv(0, bmv.a.a, bmv.b.a, 3));
        ELEMENT_COLOR = new RealmsVertexFormatElement(new bmv(0, bmv.a.b, bmv.b.c, 4));
        ELEMENT_UV0 = new RealmsVertexFormatElement(new bmv(0, bmv.a.a, bmv.b.d, 2));
        ELEMENT_UV1 = new RealmsVertexFormatElement(new bmv(1, bmv.a.e, bmv.b.d, 2));
        ELEMENT_NORMAL = new RealmsVertexFormatElement(new bmv(0, bmv.a.c, bmv.b.b, 3));
        ELEMENT_PADDING = new RealmsVertexFormatElement(new bmv(0, bmv.a.c, bmv.b.g, 1));
        RealmsDefaultVertexFormat.BLOCK.addElement(RealmsDefaultVertexFormat.ELEMENT_POSITION);
        RealmsDefaultVertexFormat.BLOCK.addElement(RealmsDefaultVertexFormat.ELEMENT_COLOR);
        RealmsDefaultVertexFormat.BLOCK.addElement(RealmsDefaultVertexFormat.ELEMENT_UV0);
        RealmsDefaultVertexFormat.BLOCK.addElement(RealmsDefaultVertexFormat.ELEMENT_UV1);
        RealmsDefaultVertexFormat.BLOCK_NORMALS.addElement(RealmsDefaultVertexFormat.ELEMENT_POSITION);
        RealmsDefaultVertexFormat.BLOCK_NORMALS.addElement(RealmsDefaultVertexFormat.ELEMENT_COLOR);
        RealmsDefaultVertexFormat.BLOCK_NORMALS.addElement(RealmsDefaultVertexFormat.ELEMENT_UV0);
        RealmsDefaultVertexFormat.BLOCK_NORMALS.addElement(RealmsDefaultVertexFormat.ELEMENT_NORMAL);
        RealmsDefaultVertexFormat.BLOCK_NORMALS.addElement(RealmsDefaultVertexFormat.ELEMENT_PADDING);
        RealmsDefaultVertexFormat.ENTITY.addElement(RealmsDefaultVertexFormat.ELEMENT_POSITION);
        RealmsDefaultVertexFormat.ENTITY.addElement(RealmsDefaultVertexFormat.ELEMENT_UV0);
        RealmsDefaultVertexFormat.ENTITY.addElement(RealmsDefaultVertexFormat.ELEMENT_NORMAL);
        RealmsDefaultVertexFormat.ENTITY.addElement(RealmsDefaultVertexFormat.ELEMENT_PADDING);
        RealmsDefaultVertexFormat.PARTICLE.addElement(RealmsDefaultVertexFormat.ELEMENT_POSITION);
        RealmsDefaultVertexFormat.PARTICLE.addElement(RealmsDefaultVertexFormat.ELEMENT_UV0);
        RealmsDefaultVertexFormat.PARTICLE.addElement(RealmsDefaultVertexFormat.ELEMENT_COLOR);
        RealmsDefaultVertexFormat.PARTICLE.addElement(RealmsDefaultVertexFormat.ELEMENT_UV1);
        RealmsDefaultVertexFormat.POSITION.addElement(RealmsDefaultVertexFormat.ELEMENT_POSITION);
        RealmsDefaultVertexFormat.POSITION_COLOR.addElement(RealmsDefaultVertexFormat.ELEMENT_POSITION);
        RealmsDefaultVertexFormat.POSITION_COLOR.addElement(RealmsDefaultVertexFormat.ELEMENT_COLOR);
        RealmsDefaultVertexFormat.POSITION_TEX.addElement(RealmsDefaultVertexFormat.ELEMENT_POSITION);
        RealmsDefaultVertexFormat.POSITION_TEX.addElement(RealmsDefaultVertexFormat.ELEMENT_UV0);
        RealmsDefaultVertexFormat.POSITION_NORMAL.addElement(RealmsDefaultVertexFormat.ELEMENT_POSITION);
        RealmsDefaultVertexFormat.POSITION_NORMAL.addElement(RealmsDefaultVertexFormat.ELEMENT_NORMAL);
        RealmsDefaultVertexFormat.POSITION_NORMAL.addElement(RealmsDefaultVertexFormat.ELEMENT_PADDING);
        RealmsDefaultVertexFormat.POSITION_TEX_COLOR.addElement(RealmsDefaultVertexFormat.ELEMENT_POSITION);
        RealmsDefaultVertexFormat.POSITION_TEX_COLOR.addElement(RealmsDefaultVertexFormat.ELEMENT_UV0);
        RealmsDefaultVertexFormat.POSITION_TEX_COLOR.addElement(RealmsDefaultVertexFormat.ELEMENT_COLOR);
        RealmsDefaultVertexFormat.POSITION_TEX_NORMAL.addElement(RealmsDefaultVertexFormat.ELEMENT_POSITION);
        RealmsDefaultVertexFormat.POSITION_TEX_NORMAL.addElement(RealmsDefaultVertexFormat.ELEMENT_UV0);
        RealmsDefaultVertexFormat.POSITION_TEX_NORMAL.addElement(RealmsDefaultVertexFormat.ELEMENT_NORMAL);
        RealmsDefaultVertexFormat.POSITION_TEX_NORMAL.addElement(RealmsDefaultVertexFormat.ELEMENT_PADDING);
        RealmsDefaultVertexFormat.POSITION_TEX2_COLOR.addElement(RealmsDefaultVertexFormat.ELEMENT_POSITION);
        RealmsDefaultVertexFormat.POSITION_TEX2_COLOR.addElement(RealmsDefaultVertexFormat.ELEMENT_UV0);
        RealmsDefaultVertexFormat.POSITION_TEX2_COLOR.addElement(RealmsDefaultVertexFormat.ELEMENT_UV1);
        RealmsDefaultVertexFormat.POSITION_TEX2_COLOR.addElement(RealmsDefaultVertexFormat.ELEMENT_COLOR);
        RealmsDefaultVertexFormat.POSITION_TEX_COLOR_NORMAL.addElement(RealmsDefaultVertexFormat.ELEMENT_POSITION);
        RealmsDefaultVertexFormat.POSITION_TEX_COLOR_NORMAL.addElement(RealmsDefaultVertexFormat.ELEMENT_UV0);
        RealmsDefaultVertexFormat.POSITION_TEX_COLOR_NORMAL.addElement(RealmsDefaultVertexFormat.ELEMENT_COLOR);
        RealmsDefaultVertexFormat.POSITION_TEX_COLOR_NORMAL.addElement(RealmsDefaultVertexFormat.ELEMENT_NORMAL);
        RealmsDefaultVertexFormat.POSITION_TEX_COLOR_NORMAL.addElement(RealmsDefaultVertexFormat.ELEMENT_PADDING);
    }
}