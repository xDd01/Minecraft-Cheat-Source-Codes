package io.github.nevalackin.client.impl.ui.hud.rendered.graphicbox;

import io.github.nevalackin.client.api.ui.cfont.CustomFontRenderer;
import io.github.nevalackin.client.impl.core.KetamineClient;
import io.github.nevalackin.client.util.render.BloomUtil;
import io.github.nevalackin.client.util.render.ColourUtil;
import io.github.nevalackin.client.util.render.DrawUtil;

import java.util.List;

public class GraphicBoxUtils {

    private static final float OUTLINE_WIDTH = 0.5f;
    private static final float SEPARATOR_WIDTH = 1.0f;
    private static final float HEADER = 10.0f;

    private GraphicBoxUtils() {

    }

    public static <T> float[] drawGraphicBox(
            List<GraphicBoxField<T>> fields,
            List<T> objects,
            float x,
            float y) {
        final CustomFontRenderer fontRenderer = KetamineClient.getInstance().getFontRenderer();

        float[] dimensions = new float[2];

        float width = 0.0f;
        float margin = 2.0f;
        float spacing = margin * 3.0f;

        int fs = fields.size();
        int n = objects.size();

        String[][] values = new String[fs][n];
        double[] fieldWidths = new double[fs];

        for (int i = 0; i < fs; i++) {
            fieldWidths[i] = fontRenderer.getWidth(fields.get(i).title) + spacing;
        }

        for (int j = 0; j < n; j++) {
            T object = objects.get(j);

            for (int i = 0; i < fs; i++) {
                values[i][j] = fields.get(i).valueFunc.apply(object);
                fieldWidths[i] = Math.max(fieldWidths[i], fontRenderer.getWidth(values[i][j]) + spacing);
            }
        }

        float fieldWidthsAccumulator = 0.0f;

        for (int i = 0; i < fs; i++) {
            fieldWidthsAccumulator += fieldWidths[i];
        }

        width = Math.max(width, fieldWidthsAccumulator);

        dimensions[0] = width;

        GraphicBoxUtils.drawGraphicBox(null, x, y, width,
                (outlineWidth, separatorWidth, header) -> (n + 1) * header + separatorWidth,
                (gbx, gby, gbwidth, gbheight, color, outlineWidth, separatorWidth, header) -> {
                    float xAccumulator = x + margin;
                    for (int i = 0; i < fs; i++) {
                        fontRenderer.drawWithShadow(fields.get(i).title, xAccumulator, y, .5, 0xFFFFFFFF);
                        xAccumulator += fieldWidths[i];
                    }

                    for (int i = 0; i < n; i++) {
                        float elementY = y + header + separatorWidth + i * header;

                        xAccumulator = x + margin;
                        for (int j = 0; j < fs; j++) {
                            fontRenderer.drawWithShadow(values[j][i], xAccumulator, elementY, .5, fields.get(j).valueColorFunc.apply(objects.get(i)));
                            xAccumulator += fieldWidths[j];
                        }
                    }
                    dimensions[1] = gbheight;
                });

        return dimensions;
    }

    public static void drawGraphicBox(
            String title,
            float x,
            float y,
            float width,
            float height,
            GraphicBoxContent content) {
        final CustomFontRenderer fontRenderer = KetamineClient.getInstance().getFontRenderer();
        final int startColour = ColourUtil.fadeBetween(ColourUtil.getClientColour(), ColourUtil.getSecondaryColour(), 0);
        final int endColour = ColourUtil.fadeBetween(ColourUtil.getSecondaryColour(), ColourUtil.getClientColour(), 250);

        int color = 0x44c17b;

        // Draw background
        DrawUtil.glDrawFilledQuad(x, y, width, height, 0x80 << 24);

        // Draw header background
        DrawUtil.glDrawFilledQuad(x, y, width, HEADER, 0x80 << 24);

        // Draw separator
        BloomUtil.drawAndBloom(() -> DrawUtil.glDrawSidewaysGradientRect(x, y + HEADER, width, 1, startColour, endColour));

        // Draw title
        if (title != null) {
            fontRenderer.drawWithShadow(title, x + 2.0f, y + 2, .5, 0xFFFFFFFF);
        }

        // Draw content
        if (content != null) {
            content.drawContent(x, y, width, height, color, OUTLINE_WIDTH, SEPARATOR_WIDTH, HEADER);
        }
    }

    public static void drawGraphicBox(
            String title,
            float x,
            float y,
            float width,
            GraphicBoxHeightSupplier heightSupplier,
            GraphicBoxContent content
    ) {
        float height = heightSupplier.get(OUTLINE_WIDTH, SEPARATOR_WIDTH, HEADER);
        drawGraphicBox(title, x, y, width, height, content);
    }

}
