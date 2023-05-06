package org.crayne.sketch.text;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class TextComponent {

    private final List<ComponentPart> parts;

    public TextComponent(@NotNull final ComponentPart... parts) {
        this.parts = new ArrayList<>(Arrays.stream(parts).toList());
    }

    public TextComponent(@NotNull final Collection<ComponentPart> parts) {
        this.parts = new ArrayList<>(parts);
    }

    public static TextComponent of(@NotNull final AnsiColor color, @NotNull final String text) {
        return new TextComponent(ComponentPart.of(color, text));
    }
    public static TextComponent of(@NotNull final Color color, @NotNull final String text) {
        return new TextComponent(ComponentPart.of(color, text));
    }

    public static TextComponent of(@NotNull final ComponentPart... parts) {
        return new TextComponent(parts);
    }

    public static TextComponent of(@NotNull final Collection<ComponentPart> parts) {
        return new TextComponent(parts);
    }

    public static TextComponent empty() {
        return new TextComponent();
    }

    public static TextComponent plain(@NotNull final String text) {
        return new TextComponent(ComponentPart.plain(text));
    }

    public TextComponent prepend(@NotNull final ComponentPart part) {
        parts.add(0, part);
        return this;
    }

    public TextComponent prepend(@NotNull final AnsiColor part) {
        parts.add(0, new ComponentPart(part, null));
        return this;
    }

    public TextComponent prepend(@NotNull final String plain) {
        parts.add(0, ComponentPart.plain(plain));
        return this;
    }

    public TextComponent prepend(@NotNull final ComponentBuilder part) {
        parts.add(0, part.build());
        return this;
    }

    public TextComponent prepend(@NotNull final TextComponent comp) {
        parts.addAll(0, comp.parts);
        return this;
    }

    public TextComponent append(@NotNull final ComponentPart part) {
        parts.add(part);
        return this;
    }

    public TextComponent append(@NotNull final AnsiColor part) {
        parts.add(new ComponentPart(part, null));
        return this;
    }

    public TextComponent append(@NotNull final String plain) {
        parts.add(ComponentPart.plain(plain));
        return this;
    }

    public TextComponent append(@NotNull final ComponentBuilder part) {
        parts.add(part.build());
        return this;
    }

    public TextComponent append(@NotNull final TextComponent comp) {
        parts.addAll(comp.parts);
        return this;
    }

    public TextComponent replace(@NotNull final String find, @NotNull final String replace) {
        final List<ComponentPart> replaced = parts.stream().map(c -> new ComponentPart(c.color(), c.text().replace(find, replace))).toList();
        parts.clear();
        parts.addAll(replaced);
        return this;
    }

    public TextComponent replaceAll(@NotNull final String regex, @NotNull final String replace) {
        final List<ComponentPart> replaced = parts.stream().map(c -> new ComponentPart(c.color(), c.text().replaceAll(regex, replace))).toList();
        parts.clear();
        parts.addAll(replaced);
        return this;
    }

    public List<ComponentPart> parts() {
        return parts;
    }

    public boolean isEmpty() {
        return parts.isEmpty();
    }

    public List<ComponentPart> splitByWord() {
        return parts
                .stream()
                .map(p -> Arrays.stream(p.text().split("(?<= )|(?<=\n)|(?<=\r)"))
                        .map(s -> new ComponentPart(p.color(), s)).toList())
                .flatMap(Collection::stream)
                .toList();
    }

    public List<Map.Entry<String, Optional<AnsiColor>>> splitByChar() {
        final List<ComponentPart> splitByWord = splitByWord();

        final List<Map.Entry<String, Optional<AnsiColor>>> splitByChar = new ArrayList<>();

        for (final ComponentPart p : splitByWord) {
            if (p.text().isEmpty() && p.color() != null) {
                splitByChar.add(Map.entry("", Optional.of(p.color()))); // case where there is only a color, without any text
                continue;
            }
            splitByChar.addAll(p
                    .text()
                    .chars()
                    .boxed()
                    .map(i -> Map.entry(
                            Character.toString(i),
                            Optional.ofNullable(p.color()))
                    )
                    .toList()); // the usual case WITH text

        }
        return splitByChar;
    }

    public TextComponent[] split(final int maxLengthPerLine) {
        final List<TextComponent> result = new ArrayList<>();

        final List<Map.Entry<String, Optional<AnsiColor>>> splitByChar = splitByChar();

        TextComponent temp = TextComponent.empty();
        for (final Map.Entry<String, Optional<AnsiColor>> entry : splitByChar) {
            final ComponentPart comp = new ComponentPart(entry.getValue().orElse(null), entry.getKey());

            final boolean hasNewline = temp.text().replaceAll("[\n\r]", "").length() != temp.text().length();
            if (temp.text().length() >= maxLengthPerLine || hasNewline) {
                if (temp.visible()) result.add(temp.append(AnsiColor.RESET_ANSI_COLOR));
                temp = TextComponent.empty();
            }
            temp.append(comp);
        }

        if (temp.visible()) result.add(temp);

        return result.stream().filter(TextComponent::visible).toList().toArray(new TextComponent[0]);
    }

    public boolean visible() {
        return !text().isEmpty() || !parts.stream().filter(c -> c.color() != null && c.color() != AnsiColor.RESET_ANSI_COLOR).toList().isEmpty();
    }

    public String text() {
        return parts.stream().map(ComponentPart::text).collect(Collectors.joining());
    }

    public String toString(final boolean autoResetColor) {
        return parts.stream().map(ComponentPart::toString).collect(Collectors.joining()) + (autoResetColor ? AnsiColor.RESET : "");
    }

    public TextComponent clone() {
        return new TextComponent(parts.stream().map(ComponentPart::clone).toList());
    }

    public String toString() {
        return toString(true);
    }
}
