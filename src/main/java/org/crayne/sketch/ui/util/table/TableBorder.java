package org.crayne.sketch.ui.util.table;

import org.crayne.sketch.text.AnsiColor;
import org.crayne.sketch.text.ComponentPart;
import org.jetbrains.annotations.NotNull;

public class TableBorder {

    private final TableBorderType type;
    private final AnsiColor color;

    public TableBorder() {
        type = TableBorderType.NORMAL;
        color = null;
    }

    public TableBorder(@NotNull final TableBorderType type) {
        this.type = type;
        color = null;
    }

    public TableBorder(@NotNull final TableBorderType type, final AnsiColor color) {
        this.type = type;
        this.color = color;
    }

    private String asString(final char c, final int amt) {
        return Character.toString(c).repeat(amt);
    }

    public String horizontal(final int amt) {
        return asString(type.horizontal(), amt);
    }
    public String horizontal() {
        return horizontal(1);
    }

    public String vertical() {
        return asString(type.vertical(), 1);
    }

    public String topLeft() {
        return asString(type.topLeft(), 1);
    }

    public String topRight() {
        return asString(type.topRight(), 1);
    }

    public String bottomLeft() {
        return asString(type.bottomLeft(), 1);
    }

    public String bottomRight() {
        return asString(type.bottomRight(), 1);
    }
    public String leftConnector() {return asString(type.leftConnector(), 1);}
    public String rightConnector() {return asString(type.rightConnector(), 1);}
    public String topConnector() {return asString(type.topConnector(), 1);}
    public String bottomConnector() {return asString(type.bottomConnector(), 1);}
    public String crossConnector() {return asString(type.crossConnector(), 1);}

    public ComponentPart comp(@NotNull final String border) {
        return new ComponentPart(color(), border);
    }

    public AnsiColor color() {
        return color;
    }

    public TableBorderType type() {
        return type;
    }

}
