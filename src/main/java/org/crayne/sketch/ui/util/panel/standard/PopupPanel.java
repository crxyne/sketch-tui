package org.crayne.sketch.ui.util.panel.standard;

import org.crayne.sketch.keyboard.KeyEvent;
import org.crayne.sketch.keyboard.Keylistener;
import org.crayne.sketch.text.AnsiColor;
import org.crayne.sketch.text.TextComponent;
import org.crayne.sketch.ui.util.border.Border;
import org.crayne.sketch.ui.util.border.BorderType;
import org.crayne.sketch.ui.util.panel.PanelDisplay;
import org.crayne.sketch.ui.util.panel.PanelOrder;
import org.crayne.sketch.util.vec.Vec;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PopupPanel extends Panel {

    private final TextComponent text;
    private final int globalOffset;
    private final int textButtonOffset;
    private final int buttonDistance;
    private final Button[] buttons;

    private final Map<KeyEvent, Runnable> keybinds;
    private volatile int selected;
    private volatile boolean active;
    private Keylistener keylistener;

    public PopupPanel(@NotNull final Border border, @NotNull final Vec<Float> formatSize, @NotNull final TextComponent text, @NotNull final Button... buttons) {
        super(PanelOrder.LEFT_TO_RIGHT, border, formatSize, new PanelDisplay(), false);
        this.text = text;
        this.textButtonOffset = 1;
        this.buttonDistance = 1;
        this.globalOffset = 0;
        this.buttons = buttons;
        this.keybinds = new HashMap<>();
        keylistener = defaultKeyListener(this);
    }

    public PopupPanel(@NotNull final Border border, @NotNull final Vec<Float> formatSize, @NotNull final TextComponent text,
                      final int textButtonOffset, @NotNull final Button... buttons) {
        super(PanelOrder.LEFT_TO_RIGHT, border, formatSize, new PanelDisplay(), false);
        if (textButtonOffset < 0) throw new IllegalArgumentException("Invalid text button offset: " + textButtonOffset);
        this.text = text;
        this.textButtonOffset = textButtonOffset;
        this.buttonDistance = 1;
        this.globalOffset = 0;
        this.buttons = buttons;
        this.keybinds = new HashMap<>();
        keylistener = defaultKeyListener(this);
    }

    public PopupPanel(@NotNull final Border border, @NotNull final Vec<Float> formatSize, @NotNull final TextComponent text,
                      final int textButtonOffset, final int buttonDistance, @NotNull final Button... buttons) {

        super(PanelOrder.LEFT_TO_RIGHT, border, formatSize, new PanelDisplay(), false);
        if (textButtonOffset < 0) throw new IllegalArgumentException("Invalid text button offset: " + textButtonOffset);
        if (buttonDistance < 0) throw new IllegalArgumentException("Invalid button distance: " + buttonDistance);
        this.text = text;
        this.textButtonOffset = textButtonOffset;
        this.buttonDistance = buttonDistance;
        this.globalOffset = 0;
        this.buttons = buttons;
        this.keybinds = new HashMap<>();
        keylistener = defaultKeyListener(this);
    }

    public PopupPanel(@NotNull final Border border, @NotNull final Vec<Float> formatSize, @NotNull final TextComponent text,
                      final int textButtonOffset, final int buttonDistance, final int globalOffset, @NotNull final Button... buttons) {

        super(PanelOrder.LEFT_TO_RIGHT, border, formatSize, new PanelDisplay(), false);
        if (textButtonOffset < 0) throw new IllegalArgumentException("Invalid text button offset: " + textButtonOffset);
        if (buttonDistance < 0) throw new IllegalArgumentException("Invalid button distance: " + buttonDistance);
        this.text = text;
        this.textButtonOffset = textButtonOffset;
        this.buttonDistance = buttonDistance;
        this.globalOffset = globalOffset;
        this.buttons = buttons;
        this.keybinds = new HashMap<>();
        keylistener = defaultKeyListener(this);
    }

    public void addKeybind(@NotNull final KeyEvent keybind, @NotNull final Runnable action) {
        keybinds.put(keybind, action);
    }

    public void update(final boolean sub) {
        update();
    }

    public void update() {
        if (content().hidden() || currentSize() == null) return;

        final int titlePosX = currentSize().x() / 2 - text.text().length() / 2;
        final int beforeTitleY = Math.max(0, currentSize().y() / 2 - textButtonOffset - 2 + globalOffset);
        final int buttonX = Math.max(0,
                currentSize().x() / 2 -
                        (Arrays.stream(buttons)
                        .map(b -> b.text.text().length() + 2)
                        .mapToInt(i -> i).sum() +
                        (buttons.length - 1) * buttonDistance) / 2 - 1 + buttons.length / 2
        );

        content.content().clear();
        content.content().addAll(Collections.nCopies(beforeTitleY, TextComponent.plain(" ".repeat(currentSize().x()))));
        content.content().add(TextComponent.plain(" ".repeat(titlePosX)).append(text));
        content.content().addAll(Collections.nCopies(textButtonOffset, TextComponent.plain(" ".repeat(currentSize().x()))));

        if (buttons.length == 0) {
            render();
            return;
        }

        TextComponent buttonTopBottom = TextComponent.plain(" ".repeat(buttonX));
        TextComponent buttonText = TextComponent.plain(" ".repeat(buttonX));

        int i = 0;
        for (@NotNull final Button button : buttons) {
            final AnsiColor color = button.accessible ? (i == selected ? button.color : button.unfocusedColor) : button.inaccessibleColor;

            buttonTopBottom = buttonTopBottom
                    .append(color)
                    .append(" ".repeat(2 + button.text.text().length()))
                    .append(AnsiColor.RESET_ANSI_COLOR)
                    .append(" ".repeat(buttonDistance));

            buttonText = buttonText
                    .append(color)
                    .append(" ")
                    .append(button.text)
                    .append(color)
                    .append(" ")
                    .append(AnsiColor.RESET_ANSI_COLOR)
                    .append(" ".repeat(buttonDistance));
            i++;
        }

        content.content().add(buttonTopBottom);
        content.content().add(buttonText);
        content.content().add(buttonTopBottom);
        render();
    }

    public void select(final int index) {
        if (index < 0 || index >= buttons.length) throw new IndexOutOfBoundsException();
        this.selected = index;
    }

    public synchronized void selectNext() {
        if (buttons.length == 0 || Arrays.stream(buttons).filter(Button::accessible).findFirst().isEmpty()) return;

        do {
            selected = (selected + 1) % buttons.length;
        } while (!buttons[selected].accessible);
    }

    public synchronized void selectPrevious() {
        if (buttons.length == 0 || Arrays.stream(buttons).filter(Button::accessible).findFirst().isEmpty()) return;

        do {
            selected = selected - 1 < 0 ? buttons.length - 1 : (selected - 1) % buttons.length;
        } while (!buttons[selected].accessible);
    }

    public TextComponent text() {
        return text;
    }

    public int buttonDistance() {
        return buttonDistance;
    }

    public int textButtonOffset() {
        return textButtonOffset;
    }

    public boolean active() {
        return active;
    }

    public void active(final boolean active) {
        this.active = active;
        keylistener.close();
        keylistener = defaultKeyListener(this);
    }

    private void handleKey(@NotNull final KeyEvent event) {
        switch (event.keycode()) {
            case ARROW_LEFT, ARROW_DOWN -> selectPrevious();
            case ARROW_RIGHT, ARROW_UP, TAB -> selectNext();
        }
    }

    private static Keylistener defaultKeyListener(@NotNull final PopupPanel panel) {
        return new Keylistener() {
            @Override
            public void onKeyEvent(@NotNull final KeyEvent event) {
                if (!panel.active || !event.keyDown()) return;

                final Runnable action = panel.keybinds.get(event);
                if (action != null) {
                    action.run();
                    return;
                }
                panel.handleKey(event);
                panel.update();
            }
        };
    }

    public int selectedButton() {
        return selected;
    }

    public Button[] buttons() {
        return buttons;
    }

    public static class Button {

        private final AnsiColor color;
        private final AnsiColor unfocusedColor;

        private final AnsiColor inaccessibleColor;

        private final TextComponent text;
        private final TextComponent unfocusedText;
        private final TextComponent inaccessibleText;

        private boolean accessible = true;

        public Button(@NotNull final AnsiColor color, @NotNull final AnsiColor unfocusedColor, @NotNull final TextComponent text) {
            if (color.bg() == null || unfocusedColor.bg() == null) throw new IllegalArgumentException("Expected background ansi colors for button");
            this.color = color;
            this.unfocusedColor = unfocusedColor;
            this.inaccessibleColor = AnsiColor.background(Color.GRAY);
            this.text = text;
            this.unfocusedText = text;
            this.inaccessibleText = text;
        }

        public Button(@NotNull final AnsiColor color, @NotNull final AnsiColor unfocusedColor, @NotNull final AnsiColor inaccessibleColor, @NotNull final TextComponent text) {
            if (color.bg() == null || unfocusedColor.bg() == null || inaccessibleColor.bg() == null) throw new IllegalArgumentException("Expected background ansi colors for button");
            this.color = color;
            this.unfocusedColor = unfocusedColor;
            this.inaccessibleColor = inaccessibleColor;
            this.text = text;
            this.unfocusedText = text;
            this.inaccessibleText = text;
        }

        public Button(@NotNull final AnsiColor color, @NotNull final AnsiColor unfocusedColor, @NotNull final AnsiColor inaccessibleColor,
                      @NotNull final TextComponent text, @NotNull final TextComponent unfocusedText, @NotNull final TextComponent inaccessibleText) {
            if (color.bg() == null || unfocusedColor.bg() == null || inaccessibleColor.bg() == null) throw new IllegalArgumentException("Expected background ansi colors for button");
            this.color = color;
            this.unfocusedColor = unfocusedColor;
            this.inaccessibleColor = inaccessibleColor;
            this.text = text;
            this.unfocusedText = unfocusedText;
            this.inaccessibleText = inaccessibleText;
        }

        public static Button of(@NotNull final AnsiColor color, @NotNull final AnsiColor unfocusedColor, @NotNull final TextComponent text) {
            return new Button(color, unfocusedColor, text);
        }

        public static Button of(@NotNull final AnsiColor color, @NotNull final AnsiColor unfocusedColor, @NotNull final AnsiColor inaccessibleColor, @NotNull final TextComponent text) {
            return new Button(color, unfocusedColor, inaccessibleColor, text);
        }

        public static Button of(@NotNull final AnsiColor color, @NotNull final AnsiColor unfocusedColor, @NotNull final AnsiColor inaccessibleColor,
                                @NotNull final TextComponent text, @NotNull final TextComponent unfocusedText, @NotNull final TextComponent inaccessibleText) {
            return new Button(color, unfocusedColor, inaccessibleColor, text, unfocusedText, inaccessibleText);
        }

        public static Button of(@NotNull final Color color, @NotNull final Color unfocusedColor, @NotNull final TextComponent text) {
            return new Button(AnsiColor.background(color), AnsiColor.background(unfocusedColor), text);
        }

        public static Button of(@NotNull final Color color, @NotNull final Color unfocusedColor, @NotNull final Color inaccessibleColor, @NotNull final TextComponent text) {
            return new Button(AnsiColor.background(color), AnsiColor.background(unfocusedColor), AnsiColor.background(inaccessibleColor), text);
        }

        public static Button of(@NotNull final Color color, @NotNull final Color unfocusedColor, @NotNull final Color inaccessibleColor,
                                @NotNull final TextComponent text, @NotNull final TextComponent unfocusedText, @NotNull final TextComponent inaccessibleText) {
            return new Button(AnsiColor.background(color), AnsiColor.background(unfocusedColor), AnsiColor.background(inaccessibleColor), text, unfocusedText, inaccessibleText);
        }

        public AnsiColor color() {
            return color;
        }

        public TextComponent text() {
            return text;
        }

        public boolean accessible() {
            return accessible;
        }

        public void accessible(final boolean accessible) {
            this.accessible = accessible;
        }

        public AnsiColor inaccessibleColor() {
            return inaccessibleColor;
        }

        public AnsiColor unfocusedColor() {
            return unfocusedColor;
        }
    }


}
