package org.crayne.sketch.ui.util.border;

import org.crayne.sketch.text.AnsiColor;
import org.crayne.sketch.text.ComponentPart;
import org.crayne.sketch.text.TextComponent;
import org.crayne.sketch.ui.util.title.Title;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class Border {

    private final BorderType[] types;
    private final AnsiColor[] colors;

    private final Title[] titles;

    public static final BorderType[] DEFAULT_BORDER_TYPES = globalBorderTypes(BorderType.NORMAL);

    public Border() {
        types = DEFAULT_BORDER_TYPES;
        this.titles = new Title[2];
        this.colors = null;
    }

    public Border(@NotNull final BorderType... types) {
        if (types.length != 8) {
            if (types.length == 1) this.types = globalBorderTypes(types[0]);
            else throw new IllegalArgumentException("Expected exactly 8 border types for border (top, bottom, left, right, + all corners for those)");
        } else this.types = types;

        this.titles = new Title[2];
        this.colors = null;
    }

    public Border(@NotNull final BorderType[] types, @NotNull final AnsiColor... colors) {
        if (colors.length != 8) throw new IllegalArgumentException("Expected exactly 8 colors for border (top, bottom, left, right, + all corners for those)");
        if (types.length != 8) throw new IllegalArgumentException("Expected exactly 8 border types for border (top, bottom, left, right, + all corners for those)");
        this.types = types;
        this.titles = new Title[2];
        this.colors = colors;
    }

    public static BorderType[] globalBorderTypes(@NotNull final BorderType type) {
        return Collections.nCopies(8, type).stream().toList().toArray(new BorderType[0]);
    }

    public static AnsiColor[] globalColors(@NotNull final AnsiColor color) {
        return Collections.nCopies(8, color).stream().toList().toArray(new AnsiColor[0]);
    }

    private TextComponent asComponent(final char c, final int amt, @NotNull final AnsiColor color) {
        return TextComponent.of(new ComponentPart(color, Character.toString(c).repeat(amt)));
    }

    private TextComponent asComponent(final char c, @NotNull final AnsiColor color) {
        return asComponent(c, 1, color);
    }

    public AnsiColor colorOrElse(final int index, @NotNull final AnsiColor orElse) {
        return colors == null ? orElse : colors[index];
    }

    public AnsiColor color(final int index) {
        return colorOrElse(index, AnsiColor.RESET_ANSI_COLOR);
    }

    public TextComponent top(final int amt) {
        return asComponent(types[0].horizontal(), amt, color(0));
    }

    public boolean topHasWidth() {
        return types[0] != BorderType.ZERO_WIDTH;
    }

    public TextComponent bottom(final int amt) {
        return asComponent(types[1].horizontal(), amt, color(1));
    }

    public boolean bottomHasWidth() {
        return types[1] != BorderType.ZERO_WIDTH;
    }

    public TextComponent left() {
        return asComponent(types[2].vertical(), color(2));
    }

    public boolean leftHasWidth() {
        return types[2] != BorderType.ZERO_WIDTH;
    }

    public TextComponent right() {
        return asComponent(types[3].vertical(), color(3));
    }

    public boolean rightHasWidth() {
        return types[3] != BorderType.ZERO_WIDTH;
    }

    public TextComponent topLeft() {
        return asComponent(types[4].topLeft(), color(4));
    }

    public TextComponent topRight() {
        return asComponent(types[5].topRight(), color(5));
    }

    public TextComponent bottomLeft() {
        return asComponent(types[6].bottomLeft(), color(6));
    }

    public TextComponent bottomRight() {
        return asComponent(types[7].bottomRight(), color(7));
    }

    public void titleTop(final Title title) {
        titles[0] = title;
    }

    public void titleBottom(final Title title) {
        titles[1] = title;
    }

    public Title titleTop() {
        return titles[0];
    }

    public Title titleBottom() {
        return titles[1];
    }

    public Title[] titles() {
        return titles;
    }

    public AnsiColor[] colors() {
        return colors;
    }

    public BorderType[] types() {
        return types;
    }
}
