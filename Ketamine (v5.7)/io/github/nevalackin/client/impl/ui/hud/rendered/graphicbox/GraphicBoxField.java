package io.github.nevalackin.client.impl.ui.hud.rendered.graphicbox;

import java.util.function.Function;

public class GraphicBoxField<T> {

    public final String title;
    public final Function<T, String> valueFunc;
    public final Function<T, Integer> valueColorFunc;

    public GraphicBoxField(String title, Function<T, String> valueFunc, Function<T, Integer> valueColorFunc) {
        this.title = title;
        this.valueFunc = valueFunc;
        this.valueColorFunc = valueColorFunc;
    }
}
