package org.crayne.sketch.ui.util.title;

import org.crayne.sketch.text.TextComponent;
import org.crayne.sketch.ui.util.border.Border;
import org.jetbrains.annotations.NotNull;

import static org.crayne.sketch.util.ansi.AnsiEscapeSequence.reset;

public record SimpleTitle(TextComponent title, SimpleTitleAlignment alignment) implements Title {

    public static SimpleTitle empty() {
        return new SimpleTitle(TextComponent.empty(), SimpleTitleAlignment.LEFT);
    }

    public static SimpleTitle of(@NotNull final TextComponent title, @NotNull final SimpleTitleAlignment alignment) {
        return new SimpleTitle(title, alignment);
    }

    public static SimpleTitle of(@NotNull final TextComponent title) {
        return new SimpleTitle(title, SimpleTitleAlignment.CENTER);
    }

    public static SimpleTitle of(@NotNull final String title, @NotNull final SimpleTitleAlignment alignment) {
        return new SimpleTitle(TextComponent.plain(title), alignment);
    }

    public static SimpleTitle of(@NotNull final String title) {
        return new SimpleTitle(TextComponent.plain(title), SimpleTitleAlignment.CENTER);
    }

    public TextComponent title() {
        return title;
    }

    public String rendered(final int givenWidth, @NotNull final Border border, final boolean top) {
        final int len = title.text().length();
        final String titleStr = title.toString().replace("\r", "");

        final TextComponent leftCorner = top ? border.topLeft() : border.bottomLeft();
        final TextComponent rightCorner = top ? border.topRight() : border.bottomRight();

        final int lrLen = Math.max(0, givenWidth - len - 2);
        final TextComponent lrTop = top ? border.top(lrLen) : border.bottom(lrLen);

        switch (alignment) {
            case CENTER -> {
                final int halfSize = Math.max(0, givenWidth / 2 - len / 2);
                final int shlen = Math.max(0, givenWidth - halfSize - len - 2);

                final TextComponent half = top ? border.top(halfSize) : border.bottom(halfSize);
                final TextComponent secondHalf = top ? border.top(shlen) : border.bottom(shlen);

                return reset(leftCorner)
                        + reset(half)
                        + reset(titleStr)
                        + reset(secondHalf)
                        + reset(rightCorner);
            }
            case LEFT -> {
                return reset(leftCorner)
                        + reset(titleStr)
                        + reset(lrTop)
                        + reset(rightCorner);
            }
            case RIGHT -> {
                return reset(leftCorner)
                        + reset(lrTop)
                        + reset(titleStr)
                        + reset(rightCorner);
            }
        }
        return "";
    }

    public String toString() {
        return title.toString();
    }
}
