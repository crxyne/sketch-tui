package org.crayne.sketch.ui.util.panel.standard;

import org.apache.commons.lang3.CharUtils;
import org.crayne.sketch.keyboard.KeyEvent;
import org.crayne.sketch.keyboard.Keycode;
import org.crayne.sketch.keyboard.Keylistener;
import org.crayne.sketch.text.AnsiColor;
import org.crayne.sketch.text.AnsiColorBuilder;
import org.crayne.sketch.text.ComponentPart;
import org.crayne.sketch.text.TextComponent;
import org.crayne.sketch.ui.util.border.Border;
import org.crayne.sketch.ui.util.panel.PanelDisplay;
import org.crayne.sketch.ui.util.panel.PanelOrder;
import org.crayne.sketch.util.vec.Vec;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unused")
public class InputPanel extends Panel {

    private volatile boolean active;
    private String currentInput;
    private AnsiColor cursorColor;
    private Keylistener keylistener;
    private final Map<KeyEvent, Runnable> keybinds;
    private final Map<Integer, Runnable> characterInputEvents;
    private InputPanel nextPanel;
    private int cursor;
    private boolean hideInput;
    private boolean hideInputNextTime = false;

    public InputPanel(@NotNull final Border border, @NotNull final Vec<Float> formatSize, final InputPanel nextPanel, final boolean hideInput) {
        super(PanelOrder.LEFT_TO_RIGHT, border, formatSize, new PanelDisplay(), true);
        cursorColor = defaultCursorColor();
        keylistener = defaultKeyListener(this);
        this.hideInput = hideInput;
        this.nextPanel = nextPanel;
        this.keybinds = new HashMap<>();
        this.characterInputEvents = new HashMap<>();
        currentInput = "";
        cursor = 0;
    }

    public static AnsiColor defaultCursorColor() {
        return new AnsiColorBuilder().inverted(true).build();
    }

    public int cursor() {
        return cursor;
    }

    public AnsiColor cursorColor() {
        return cursorColor;
    }

    public void cursorColor(@NotNull final AnsiColor cursorColor) {
        this.cursorColor = cursorColor;
    }

    public void cursor(final int cursor) {
        this.cursor = cursor;
    }

    public boolean active() {
        return active;
    }

    public void active(final boolean active) {
        this.active = active;
        keylistener.close();
        keylistener = defaultKeyListener(this);
        if (!this.active) {
            if (currentInput != null) this.content = new PanelDisplay(List.of(TextComponent.plain(visibleInput())), this.content.scroll(), this.content.hidden());
        }
    }

    public Map<KeyEvent, Runnable> keybinds() {
        return keybinds;
    }

    public void addKeybind(@NotNull final KeyEvent keybind, @NotNull final Runnable action) {
        keybinds.put(keybind, action);
    }

    public Map<Integer, Runnable> characterInputEvents() {
        return characterInputEvents;
    }

    public void addKeybind(@NotNull final Integer character, @NotNull final Runnable action) {
        characterInputEvents.put(character, action);
    }

    public InputPanel nextPanel() {
        return nextPanel;
    }

    public void nextPanel(@NotNull final InputPanel nextPanel) {
        this.nextPanel = nextPanel;
    }

    public void clearInput() {
        this.currentInput = "";
        cursor = 0;
        update();
    }

    public String input() {
        return this.currentInput;
    }

    public void input(@NotNull final String string) {
        this.currentInput = string;
        moveCursorEnd();
        update();
    }

    public void appendInput(@NotNull final String string) {
        this.currentInput += string;
        update();
    }

    public void appendInput(final char c) {
        this.currentInput += c;
        update();
    }

    public void insertInput(final String s, final int offset) {
        currentInput = new StringBuilder(currentInput).insert(cursor - 1 + offset, s).toString();
        update();
    }

    public void insertInput(final String s) {
        insertInput(s, 0);
    }

    public void insertInput(final char c, final int offset) {
        currentInput = new StringBuilder(currentInput).insert(Math.min(currentInput.length(), cursor - 1 + offset), c).toString();
        update();
    }

    public void insertInput(final char c) {
        insertInput(c, 0);
    }

    public void backspace() {
        moveCursorLeft(1);
        delete();
    }

    public void delete() {
        currentInput = new StringBuilder(currentInput).replace(cursor, cursor + 1, "").toString();
        update();
    }

    public void deleteWord() {
        if (currentInput.isEmpty()) return;
        final String before = currentInput.substring(0, cursor).trim();
        final String after = currentInput.substring(cursor);

        if (before.length() >= 1) {
            final int lastSpace = before.lastIndexOf(" ") == -1 ? before.lastIndexOf("\r") : before.lastIndexOf(" ");
            final String newBefore = before.substring(0, Math.max(0, lastSpace));
            currentInput = newBefore + after;
            cursor = newBefore.length();
        }
        update();
    }

    public void deleteAllBeforeCursor() {
        currentInput = currentInput.substring(cursor);
        moveCursorHome();
        update();
    }

    public void deleteAllAfterCursor() {
        currentInput = currentInput.substring(0, cursor);
        update();
    }

    public void moveCursorRight(final int amt) {
        this.cursor = Math.max(0, Math.min(this.currentInput.length(), this.cursor + amt));
        update();
    }

    public void moveCursorLeft(final int amt) {
        this.cursor = Math.max(0, this.cursor - amt);
        update();
    }

    public void moveCursorHome() {
        this.cursor = 0;
        update();
    }

    public void moveCursorEnd() {
        this.cursor = this.currentInput.length();
        update();
    }

    public void tabToNextPanel() {
        if (nextPanel == null) return;
        active(false);
        nextPanel.active(true);
        update();
        nextPanel.update();
    }

    public void moveCursorEndOfLine() {
        if (currentSize() == null) return; // content has been updated atleast once
        final int currentColumn = cursorPosition2D().y();
        final String lineAtCursor = lineAtCursor();

        moveCursorRight(lineAtCursor.length() - currentColumn - 1);
        update();
    }

    public void moveCursorStartOfLine() {
        if (currentSize() == null) return; // content has been updated atleast once
        final int currentColumn = cursorPosition2D().y();

        moveCursorLeft(currentColumn);
        update();
    }

    public void moveCursorDown() {
        if (currentSize() == null) return;
        moveCursorEndOfLine();
        moveCursorRight(1);

        final String nextLine = lineAtCursor();
        final int newColumn = cursorPosition2D().y();
        moveCursorRight(nextLine.length() - newColumn - 1);
    }

    public void moveCursorUp() {
        if (currentSize() == null) return;
        moveCursorStartOfLine();
        moveCursorLeft(1);

        final String previousLine = lineAtCursor();
        final int newColumn = cursorPosition2D().y();
        moveCursorLeft(previousLine.length() - newColumn - 1);
    }

    public Vec<Integer> cursorPosition2D() {
        final int width = currentSize().x() - 1;
        int line = 0;
        int column = 0;

        for (int i = 0; i < cursor && i < currentInput.length(); i++) {
            column++;
            if (currentInput.charAt(i) == '\r' || column == width) {
                column = 0;
                line++;
            }
        }
        return new Vec<>(line, column);
    }

    public String lineAtCursor() {
        if (currentInput.isEmpty()) return "";
        final Vec<Integer> cursorPos = cursorPosition2D();
        final TextComponent[] split = TextComponent
                .plain(currentInput)
                .append(" ")
                .split(currentSize().x() - 1);

        return split[cursorPos.x()].text();
    }

    public int autoscroll() {
        if (currentSize() == null) return -1; // content has been updated atleast once
        final int width = currentSize().x();
        final int height = currentSize().y();

        return TextComponent
                .plain(currentInput.substring(0, Math.max(0, cursor - 1)))
                .append("  ")
                .split(width) // basically simulate how many lines it would take up from start of text to cursor
                .length - height;
    }

    public void hideInput(final boolean hideInput) {
        this.hideInput = hideInput;
    }

    public boolean hideInput() {
        return hideInput;
    }

    public String visibleInput() {
        return hideInput ? "*".repeat(currentInput.length()) : currentInput;
    }

    private void content(@NotNull final PanelDisplay newContent) {
        this.content = newContent;
        render();
    }

    public void update(final boolean sub) {
        update();
    }

    public void update() {
        if (currentSize() == null || content().hidden()) return;

        if (!active) {
            content(new PanelDisplay(currentSize(), List.of(TextComponent.plain(visibleInput())), 0, content().hidden()));
            return;
        }
        if (cursor >= currentInput.length()) {
            content(new PanelDisplay(currentSize(), List.of(
                    TextComponent.plain(visibleInput())
                            .append(cursorColor)
                            .append(" ")
                            .append(AnsiColor.RESET_ANSI_COLOR)
            ), autoscroll(), content().hidden()));
            return;
        }
        if (currentInput.charAt(cursor) == '\r') {
            content(new PanelDisplay(currentSize(), List.of(
                    TextComponent.plain(visibleInput().substring(0, cursor))
                            .append(TextComponent.of(List.of(new ComponentPart(cursorColor, " "), new ComponentPart(AnsiColor.RESET_ANSI_COLOR, null))))
                            .append(visibleInput().substring(cursor))
            ), autoscroll(), content().hidden()));
            return;
        }
        content(new PanelDisplay(currentSize(), List.of(
                TextComponent.plain(visibleInput().substring(0, cursor))
                        .append(cursorColor)
                        .append(visibleInput().substring(cursor, cursor + 1))
                        .append(AnsiColor.RESET_ANSI_COLOR)
                        .append(visibleInput().substring(cursor + 1))
        ), autoscroll(), content().hidden()));
    }

    private void handleCtrlKeybind(@NotNull final KeyEvent event) {
        switch (event.keycode()) {
            case U -> deleteAllBeforeCursor();
            case K -> deleteAllAfterCursor();
            case E -> moveCursorEnd();
            case A -> moveCursorHome();
            case L -> clearInput();
            case F -> moveCursorRight(1);
            case B -> moveCursorLeft(1);
        }
    }

    private void handleAltKeybind(@NotNull final KeyEvent event) {
        switch (event.keycode()) {
            case BACKSPACE -> deleteWord();
            case ARROW_LEFT -> moveCursorStartOfLine();
            case ARROW_RIGHT -> moveCursorEndOfLine();
            case ARROW_DOWN -> moveCursorEnd();
            case ARROW_UP -> moveCursorHome();
            case V -> {
                if (hideInput) {
                    hideInput(false);
                    update();
                    hideInputNextTime = true;
                }
            }
        }
    }

    private boolean handleKey(@NotNull final KeyEvent event) {
        switch (event.keycode()) {
            case BACKSPACE -> backspace();
            case TAB -> tabToNextPanel();
            case DELETE -> delete();
            case ARROW_LEFT -> moveCursorLeft(1);
            case ARROW_RIGHT -> moveCursorRight(1);
            case HOME -> moveCursorHome();
            case END -> moveCursorEnd();
            case ARROW_DOWN -> moveCursorDown();
            case ARROW_UP -> moveCursorUp();
            default -> {
                return false;
            }
        }
        return true;
    }

    private static Keylistener defaultKeyListener(@NotNull final InputPanel panel) {
        return new Keylistener() {
            @Override
            public void onKeyEvent(@NotNull final KeyEvent event) {
                if (!panel.active) return;
                if (!event.keyDown()) {
                    if (event.keycode() == Keycode.V && panel.hideInputNextTime) {
                        if (event.alt() || event.heldDown().contains(Keycode.V)) {
                            panel.hideInput(true);
                            panel.update();
                            panel.hideInputNextTime = false;
                        }
                    }
                    return;
                }
                final Runnable action = panel.keybinds.get(event);
                if (action != null) {
                    action.run();
                    return;
                }

                if (event.ctrlNotAlt()) {
                    panel.handleCtrlKeybind(event);
                    return;
                }
                if (event.altNotCtrl()) {
                    panel.handleAltKeybind(event);
                    return;
                }
                if (panel.handleKey(event)) return;
                final Optional<Integer> c = event.character();
                if (c.isEmpty()) return;
                final Runnable charInputAction = panel.characterInputEvents.get(c.get());
                if (charInputAction != null) {
                    charInputAction.run();
                    return;
                }
                char intChar = (char) c.get().intValue();
                if (CharUtils.isAsciiControl(intChar) || intChar == 157) return;
                if (event.heldDown().contains(Keycode.SHIFT)) intChar = Character.toUpperCase(intChar);

                panel.cursor++;
                panel.insertInput(intChar == 2176 ? 'n' : intChar);
                panel.update();
            }
        };
    }

    public Keylistener keylistener() {
        return keylistener;
    }
}
