package org.crayne.sketch.ui.util.border;

import org.crayne.sketch.text.AnsiColor;
import org.crayne.sketch.ui.util.title.Title;
import org.jetbrains.annotations.NotNull;

public class BorderBuilder {

    private final BorderType[] types = Border.DEFAULT_BORDER_TYPES;
    private final AnsiColor[] colors = Border.globalColors(AnsiColor.RESET_ANSI_COLOR);
    private final Title[] titles = new Title[2];

    public BorderBuilder() {}

    private void set(final int index, @NotNull final BorderType borderType, @NotNull final AnsiColor color) {
        types[index] = borderType;
        colors[index] = color;
    }

    private void set(final int index, @NotNull final BorderType borderType) {
        types[index] = borderType;
    }

    public BorderBuilder titleTop(@NotNull final Title title) {
        titles[0] = title;
        return this;
    }

    public BorderBuilder titleBottom(@NotNull final Title title) {
        titles[1] = title;
        return this;
    }

    public BorderBuilder top(@NotNull final BorderType borderType, @NotNull final AnsiColor color) {
        set(0, borderType, color);
        return this;
    }

    public BorderBuilder top(@NotNull final BorderType borderType) {
        set(0, borderType);
        return this;
    }

    public BorderBuilder bottom(@NotNull final BorderType borderType, @NotNull final AnsiColor color) {
        set(1, borderType, color);
        return this;
    }

    public BorderBuilder bottom(@NotNull final BorderType borderType) {
        set(1, borderType);
        return this;
    }

    public BorderBuilder left(@NotNull final BorderType borderType, @NotNull final AnsiColor color) {
        set(2, borderType, color);
        return this;
    }

    public BorderBuilder left(@NotNull final BorderType borderType) {
        set(2, borderType);
        return this;
    }

    public BorderBuilder right(@NotNull final BorderType borderType, @NotNull final AnsiColor color) {
        set(3, borderType, color);
        return this;
    }

    public BorderBuilder right(@NotNull final BorderType borderType) {
        set(3, borderType);
        return this;
    }

    public BorderBuilder topLeft(@NotNull final BorderType borderType, @NotNull final AnsiColor color) {
        set(4, borderType, color);
        return this;
    }

    public BorderBuilder topLeft(@NotNull final BorderType borderType) {
        set(4, borderType);
        return this;
    }

    public BorderBuilder topRight(@NotNull final BorderType borderType, @NotNull final AnsiColor color) {
        set(5, borderType, color);
        return this;
    }

    public BorderBuilder topRight(@NotNull final BorderType borderType) {
        set(5, borderType);
        return this;
    }

    public BorderBuilder bottomLeft(@NotNull final BorderType borderType, @NotNull final AnsiColor color) {
        set(6, borderType, color);
        return this;
    }

    public BorderBuilder bottomLeft(@NotNull final BorderType borderType) {
        set(6, borderType);
        return this;
    }

    public BorderBuilder bottomRight(@NotNull final BorderType borderType, @NotNull final AnsiColor color) {
        set(7, borderType, color);
        return this;
    }

    public BorderBuilder bottomRight(@NotNull final BorderType borderType) {
        set(7, borderType);
        return this;
    }

    public AnsiColor[] colors() {
        return colors;
    }

    public BorderType[] types() {
        return types;
    }

    public Border build() {
        final Border border = new Border(types, colors);
        border.titleTop(titles[0]);
        border.titleBottom(titles[1]);
        return border;
    }

}
