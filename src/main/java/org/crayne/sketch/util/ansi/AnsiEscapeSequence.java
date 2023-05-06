package org.crayne.sketch.util.ansi;

import org.crayne.sketch.text.AnsiColor;
import org.jetbrains.annotations.NotNull;

public enum AnsiEscapeSequence {

    CR("\r"),
    CLEAR("\33[J"),
    MOVE_CURSOR_HOME("\33[H");

    private final String str;

    AnsiEscapeSequence(final String str) {
        this.str = str;
    }

    public String toString() {
        return str;
    }
    public static String clear() {
        return "" + MOVE_CURSOR_HOME + CLEAR;
    }

    public static String reset(@NotNull final Object obj) {
        return obj + AnsiColor.RESET;
    }

}
