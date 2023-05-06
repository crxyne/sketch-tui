package org.crayne.sketch.ui.util.table;

public enum TableBorderType {

    NORMAL(     '─', '│', '┌', '┐', '└', '┘', '├', '┤', '┬', '┴', '┼'),
    BOLD(       '━', '┃', '┏', '┓', '┗', '┛', '┝', '┥', '┳', '┻', '╋'),
    DOUBLE(     '═', '║', '╔', '╗', '╚', '╝', '╠', '╣', '╦', '╩', '╬'),
    DASHED(     '┄', '┆', '┌', '┐', '└', '┘', '├', '┤', '┬', '┴', '┼'),
    DASHED_BOLD('┅', '┇', '┏', '┓', '┗', '┛', '┝', '┥', '┳', '┻', '╋'),
    ROUND(      '─', '│', '╭', '╮', '╰', '╯', '├', '┤', '┬', '┴', '┼'),
    DIAGONAL(   '─', '│', '╱', '╲', '╲', '╱', '├', '┤', '┬', '┴', '┼');

    private final char[] asChars;

    TableBorderType(final char... asChars) {
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
    public char leftConnector() {return asChars[6];}
    public char rightConnector() {return asChars[7];}
    public char topConnector() {return asChars[8];}
    public char bottomConnector() {return asChars[9];}
    public char crossConnector() {return asChars[10];}

    public TableBorder border() {
        return new TableBorder(this);
    }



}
