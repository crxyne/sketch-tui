package org.crayne.sketch.text;

import java.awt.*;

@SuppressWarnings("unused")
public class ComponentBuilder {

    private final AnsiColorBuilder ansiColorBuilder;
    private String text;

    public ComponentBuilder() {
        ansiColorBuilder = new AnsiColorBuilder();
    }

    public ComponentBuilder fg(final Color fg) {
        ansiColorBuilder.fg(fg);
        return this;
    }

    public ComponentBuilder bg(final Color bg) {
        ansiColorBuilder.bg(bg);
        return this;
    }

    public ComponentBuilder reset(final boolean b) {
        ansiColorBuilder.reset(b);
        return this;
    }

    public ComponentBuilder bold(final boolean b) {
        ansiColorBuilder.bold(b);
        return this;
    }

    public ComponentBuilder dim(final boolean b) {
        ansiColorBuilder.dim(b);
        return this;
    }

    public ComponentBuilder italic(final boolean b) {
        ansiColorBuilder.italic(b);
        return this;
    }

    public ComponentBuilder underline(final boolean b) {
        ansiColorBuilder.underline(b);
        return this;
    }

    public ComponentBuilder blinking(final boolean b) {
        ansiColorBuilder.blinking(b);
        return this;
    }

    public ComponentBuilder inverted(final boolean b) {
        ansiColorBuilder.inverted(b);
        return this;
    }

    public ComponentBuilder hidden(final boolean b) {
        ansiColorBuilder.hidden(b);
        return this;
    }

    public ComponentBuilder strikethrough(final boolean b) {
        ansiColorBuilder.strikethrough(b);
        return this;
    }

    public ComponentBuilder obfuscated(final boolean b) {
        ansiColorBuilder.obfuscated(b);
        return this;
    }

    public ComponentBuilder rainbow(final boolean b) {
        ansiColorBuilder.rainbow(b);
        return this;
    }

    public ComponentBuilder pastel_rainbow(final boolean b) {
        ansiColorBuilder.pastel_rainbow(b);
        return this;
    }

    public ComponentBuilder animation_speed(final float s) {
        ansiColorBuilder.animation_speed(s);
        return this;
    }

    public Color fg() {
        return ansiColorBuilder.fg();
    }

    public Color bg() {
        return ansiColorBuilder.bg();
    }

    public AnsiColor color() {
        return ansiColorBuilder.build();
    }

    public boolean reset() {
        return ansiColorBuilder.reset();
    }

    public boolean bold() {
        return ansiColorBuilder.bold();
    }

    public boolean dim() {
        return ansiColorBuilder.dim();
    }

    public boolean italic() {
        return ansiColorBuilder.italic();
    }

    public boolean underline() {
        return ansiColorBuilder.underline();
    }

    public boolean blinking() {
        return ansiColorBuilder.blinking();
    }

    public boolean inverted() {
        return ansiColorBuilder.inverted();
    }

    public boolean hidden() {
        return ansiColorBuilder.hidden();
    }

    public boolean strikethrough() {
        return ansiColorBuilder.strikethrough();
    }

    public boolean obfuscated() {
        return ansiColorBuilder.obfuscated();
    }

    public boolean rainbow() {
        return ansiColorBuilder.rainbow();
    }

    public boolean pastel_rainbow() {
        return ansiColorBuilder.pastel_rainbow();
    }

    public float animation_speed() {
        return ansiColorBuilder.animation_speed();
    }

    public static ComponentBuilder builder(final String text) {
        return new ComponentBuilder().text(text);
    }

    public ComponentBuilder text(final String text) {
        this.text = text;
        return this;
    }

    public String text() {
        return text;
    }

    public ComponentPart build() {
        return new ComponentPart(color(), text);
    }
    public TextComponent textComponent() {return TextComponent.of(build());}

}
