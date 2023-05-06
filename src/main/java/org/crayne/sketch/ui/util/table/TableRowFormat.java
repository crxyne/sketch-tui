package org.crayne.sketch.ui.util.table;

import org.crayne.sketch.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TableRowFormat {

    private final TextComponent[] format;
    private final Float[] displayedSizes;
    private final TableBorder tableBorder;
    private int index;

    public TableRowFormat(final int length, @NotNull final TableBorder tableBorder) {
        format = new TextComponent[length];
        displayedSizes = new Float[length];
        index = 0;
        this.tableBorder = tableBorder;
    }

    public TableRowFormat addFormatRow(@NotNull final TextComponent text, final float displayedSize) {
        format[index] = text;
        displayedSizes[index] = displayedSize;
        index++;
        return this;
    }

    public TableRowFormat addFormatRow(@NotNull final TextComponent text) {
        format[index] = text;
        displayedSizes[index] = null;
        index++;
        return this;
    }

    public TableBorder tableBorder() {
        return tableBorder;
    }

    public int length() {
        return format.length;
    }

    public Float[] displayedSizes() {
        return displayedSizes;
    }

    public TextComponent[] format() {
        return format;
    }

    public Float[] displayedSizesAuto() {
        final List<Float> sizes = Arrays.stream(displayedSizes)
                .filter(Objects::isNull)
                .toList();
        final float currentSize = Arrays.stream(displayedSizes)
                .filter(Objects::nonNull)
                .reduce(0.0f, Float::sum);

        final float generalSize = (1.0f - currentSize) / sizes.size();

        return Arrays.stream(displayedSizes)
                .map(f -> f == null ? generalSize : f)
                .toList()
                .toArray(new Float[0]);
    }
}
