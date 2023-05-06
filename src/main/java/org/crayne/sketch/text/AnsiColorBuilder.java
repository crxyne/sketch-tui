package org.crayne.sketch.text;

import java.awt.*;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class AnsiColorBuilder {

    private Color fg;
    private Color bg;
    private final boolean[] flags;
    private float animationSpeed = 0.2f;

    public AnsiColorBuilder() {
        this.flags = new boolean[12];
    }

    public AnsiColorBuilder fg(final Color fg) {
        this.fg = fg;
        return this;
    }

    public AnsiColorBuilder bg(final Color bg) {
        this.bg = bg;
        return this;
    }

    public AnsiColorBuilder reset(final boolean b) {
        flags[0] = b;
        return this;
    }

    public AnsiColorBuilder bold(final boolean b) {
        flags[1] = b;
        return this;
    }

    public AnsiColorBuilder dim(final boolean b) {
        flags[2] = b;
        return this;
    }

    public AnsiColorBuilder italic(final boolean b) {
        flags[3] = b;
        return this;
    }

    public AnsiColorBuilder underline(final boolean b) {
        flags[4] = b;
        return this;
    }

    public AnsiColorBuilder blinking(final boolean b) {
        flags[5] = b;
        return this;
    }

    public AnsiColorBuilder inverted(final boolean b) {
        flags[6] = b;
        return this;
    }

    public AnsiColorBuilder hidden(final boolean b) {
        flags[7] = b;
        return this;
    }

    public AnsiColorBuilder strikethrough(final boolean b) {
        flags[8] = b;
        return this;
    }

    public AnsiColorBuilder obfuscated(final boolean b) {
        flags[9] = b;
        return this;
    }

    public AnsiColorBuilder rainbow(final boolean b) {
        flags[10] = b;
        return this;
    }

    public AnsiColorBuilder pastel_rainbow(final boolean b) {
        flags[11] = b;
        return this;
    }

    public AnsiColorBuilder animation_speed(final float s) {
        animationSpeed = s;
        return this;
    }

    public Color fg() {
        return fg;
    }

    public Color bg() {
        return bg;
    }

    public boolean reset() {
        return flags[0];
    }

    public boolean bold() {
        return flags[1];
    }

    public boolean dim() {
        return flags[2];
    }

    public boolean italic() {
        return flags[3];
    }

    public boolean underline() {
        return flags[4];
    }

    public boolean blinking() {
        return flags[5];
    }

    public boolean inverted() {
        return flags[6];
    }

    public boolean hidden() {
        return flags[7];
    }

    public boolean strikethrough() {
        return flags[8];
    }

    public boolean obfuscated() {
        return flags[9];
    }

    public boolean rainbow() {
        return flags[10];
    }

    public boolean pastel_rainbow() {
        return flags[11];
    }

    public float animation_speed() {
        return animationSpeed;
    }

    public AnsiColor build() {
        return new AnsiColor(fg, bg, flags, animationSpeed);
    }

}
