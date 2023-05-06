package org.crayne.sketch.text;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class AnsiColor {

    private final Color fg;
    private final Color bg;

    private final float animationSpeed;

    private float frame;
    private final boolean[] flags;

    protected AnsiColor(final Color fg, final Color bg, final boolean[] flags, final float animationSpeed) {
        if (flags.length != 12) throw new IllegalArgumentException("Expected exactly 12 color flags (reset, bold, dim, italic, underline, blinking, inverse, hidden, strikethrough, obfuscated, rainbow, rainbow-pastel)");
        this.fg = fg;
        this.bg = bg;
        this.flags = flags;
        this.frame = 0;
        this.animationSpeed = animationSpeed;
    }

    public AnsiColor(final Color fg, final Color bg, final float animationSpeed) {
        this.fg = fg;
        this.bg = bg;
        this.flags = new boolean[12];
        this.frame = 0;
        this.animationSpeed = animationSpeed;
    }

    public AnsiColor(final Color fg, final Color bg) {
        this.fg = fg;
        this.bg = bg;
        this.flags = new boolean[12];
        this.frame = 0;
        this.animationSpeed = 0.2f;
    }

    public static AnsiColor foreground(@NotNull final Color fg) {
        return new AnsiColor(fg, null);
    }

    public static AnsiColor background(@NotNull final Color bg) {
        return new AnsiColor(null, bg);
    }

    public static final String ANSI_BEGIN = "\33[";

    private static final String RGB_BEGIN = "8;2;";
    public static final String RESET = ANSI_BEGIN + "0m";
    public static final String BOLD = ANSI_BEGIN + "1m";
    public static final String DIM = ANSI_BEGIN + "2m";
    public static final String ITALIC = ANSI_BEGIN + "3m";
    public static final String UNDERLINE = ANSI_BEGIN + "4m";
    public static final String BLINKING = ANSI_BEGIN + "5m";
    public static final String INVERSE = ANSI_BEGIN + "7m";
    public static final String HIDDEN = ANSI_BEGIN + "8m";
    public static final String STRIKETHROUGH = ANSI_BEGIN + "9m";
    // obfuscated and rainbow are custom-made, not included here
    public static final String RGB_FG_BEGIN = ANSI_BEGIN + "3" + RGB_BEGIN;
    public static final String RGB_BG_BEGIN = ANSI_BEGIN + "4" + RGB_BEGIN;

    public static final AnsiColor RESET_ANSI_COLOR = new AnsiColorBuilder().reset(true).build();

    private static String rgb(final int r, final int g, final int b) {
        return r + ";" + g + ";" + b + "m";
    }

    public static String fg(final int r, final int g, final int b) {
        return RGB_FG_BEGIN + rgb(r, g, b);
    }

    public static String fg(final Color color) {
        return color == null ? "" : fg(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static String bg(final int r, final int g, final int b) {
        return RGB_BG_BEGIN + rgb(r, g, b);
    }

    public static String bg(final Color color) {
        return color == null ? "" : bg(color.getRed(), color.getGreen(), color.getBlue());
    }

    private static String flag(final boolean flag, final int index) {
        return flag ? ANSI_BEGIN + index + "m" : "";
    }

    public boolean flag(final int index) {
        return flags[index];
    }

    private static String flags(final boolean[] flags) {
        return
                flag(flags[0], 0)
                        + flag(flags[1], 1)
                        + flag(flags[2], 2)
                        + flag(flags[3], 3)
                        + flag(flags[4], 4)
                        + flag(flags[5], 5)
                        + flag(flags[6], 7)
                        + flag(flags[7], 8)
                        + flag(flags[8], 9);
    }

    private String rainbowColor(final boolean pastel, @NotNull final String str) {
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            final String rainbowColor = new AnsiColorBuilder().fg(Color.decode("#" + switch ((int) (frame + i + 1) % 7) {
                default -> pastel ? "FF9F8C" : "FC2847";
                case 1 -> pastel ? "FFD493" : "FFA343";
                case 2 -> pastel ? "FFFFAD" : "FDFC74";
                case 3 -> pastel ? "18FFB1" : "71BC78";
                case 4 -> pastel ? "9AFFFF" : "0F4C81";
                case 5 -> pastel ? "A9BCFF" : "7442C8";
                case 6 -> pastel ? "FFBDDA" : "FB7EFD";
            })).build().toString();
            result.append(rainbowColor).append(str.charAt(i));
        }
        return result.toString();
    }

    public Color fg() {
        return fg;
    }

    public Color bg() {
        return bg;
    }

    public float animationSpeed() {
        return animationSpeed;
    }

    public String toString(@NotNull final String str) {
        final boolean rainbowNormal = flag(10);
        final boolean rainbowPastel = flag(11);
        frame = (frame + animationSpeed) % 7;
        return fg(fg) + bg(bg) + flags(flags) + (rainbowNormal ? rainbowColor(false, str) : rainbowPastel ? rainbowColor(true, str) : "");
    }

    public String toString() {
        return toString("");
    }
}
