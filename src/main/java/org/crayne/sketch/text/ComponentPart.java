package org.crayne.sketch.text;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ComponentPart {

    private AnsiColor color;
    private final String text;

    public ComponentPart(final AnsiColor color, final String text) {
        this.color = color;
        this.text = text == null ? "" : text;
    }

    public static ComponentPart empty() {
        return new ComponentPart(null, null);
    }

    public static ComponentPart plain(@NotNull final String text) {
        return new ComponentPart(null, text);
    }

    public static ComponentPart of(final AnsiColor color, final String text) {
        return new ComponentPart(color, text);
    }

    public static ComponentPart of(final Color color, final String text) {
        return new ComponentPart(AnsiColor.foreground(color), text);
    }

    public String text() {
        return text;
    }

    public AnsiColor color() {
        return color;
    }

    public ComponentPart color(final AnsiColor color) {
        this.color = color;
        return this;
    }

    public ComponentPart clone() {
        return new ComponentPart(color, text);
    }

    public String toString() {
        final boolean colorFound = color != null;
        final String resultText = text;
        final boolean obfuscated = colorFound && color.flag(9);

        return (!colorFound ? resultText : color.toString(resultText) + (!color.flag(10) && !color.flag(11) ?
                obfuscated ? RandomString.randomString(resultText.length(), 126, 32) : resultText : ""));
    }
}
