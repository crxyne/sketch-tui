package org.crayne.sketch.ui.util.border;

public enum BorderType {

    NONE(       ' ', ' ', ' ', ' ', ' ', ' '),
    ZERO_WIDTH( '\r', '\r', '\r', '\r', '\r', '\r'),
    NORMAL(     '─', '│', '┌', '┐', '└', '┘'),
    BOLD(       '━', '┃', '┏', '┓', '┗', '┛'),
    DOUBLE(     '═', '║', '╔', '╗', '╚', '╝'),
    DASHED(     '┄', '┆', '┌', '┐', '└', '┘'),
    DASHED_BOLD('┅', '┇', '┏', '┓', '┗', '┛'),
    ROUND(      '─', '│', '╭', '╮', '╰', '╯'),
    DIAGONAL(   '─', '│', '╱', '╲', '╲', '╱');

    private final char[] asChars;

    BorderType(final char... asChars) {
        this.asChars = asChars;
    }

    public char horizontal() {
        return asChars[0];
    }

    public char vertical() {
        return asChars[1];
    }

    public char topLeft() {
        return asChars[2];
    }

    public char topRight() {
        return asChars[3];
    }

    public char bottomLeft() {
        return asChars[4];
    }

    public char bottomRight() {
        return asChars[5];
    }

    public Border border() {
        return new Border(Border.globalBorderTypes(this));
    }

}
