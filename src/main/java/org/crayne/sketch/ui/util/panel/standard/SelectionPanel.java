package org.crayne.sketch.ui.util.panel.standard;

import org.crayne.sketch.keyboard.KeyEvent;
import org.crayne.sketch.keyboard.Keylistener;
import org.crayne.sketch.text.AnsiColor;
import org.crayne.sketch.text.TextComponent;
import org.crayne.sketch.ui.util.border.Border;
import org.crayne.sketch.ui.util.panel.PanelDisplay;
import org.crayne.sketch.ui.util.panel.PanelOrder;
import org.crayne.sketch.util.vec.Vec;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("unused")
public class SelectionPanel extends Panel {

    private int currentSelection;
    private final AnsiColor whenSelected;
    private Keylistener keylistener;
    private final Map<KeyEvent, Runnable> keybinds;
    private volatile boolean active;
    private PanelDisplay actualContent;

    public SelectionPanel(@NotNull final Border border, @NotNull final Vec<Float> formatSize, @NotNull final AnsiColor whenSelected, @NotNull final TextComponent... selections) {
        super(PanelOrder.LEFT_TO_RIGHT, border, formatSize, new PanelDisplay(Arrays.stream(selections).toList()), false);
        this.actualContent = content.clone();
        this.whenSelected = whenSelected;
        this.keybinds = new HashMap<>();
        keylistener = defaultKeyListener(this);
        if (selections.length != 0) select(0);
    }

    public void select(final int index) {
        if (index >= actualContent.content().size()) throw new IndexOutOfBoundsException("Selection is out of bounds");
        currentSelection = index;
        update();
    }

    public void update(final boolean sub) {
        update();
    }

    public void update() {
        if (currentSize() == null || content.hidden()) return;
        content.usableSize(currentSize());

        if (actualContent.content().isEmpty()) {
            content.content(List.of(TextComponent.plain(" ")));
            content.scroll(0);
            render();
            content.content().clear();
            return;
        }

        content.content(actualContent.content().stream().map(t -> t.clone().replaceAll("[\n\r]", "")).toList());
        content.content().get(currentSelection).replaceAll("[\n\r]", "").prepend(whenSelected).append(AnsiColor.RESET_ANSI_COLOR);

        final int displayedHeight = currentSize().y();
        final int needed = content
                .content()
                .subList(0, currentSelection)
                .stream()
                .map(t -> t.split(currentSize().x()))
                .flatMap(t -> Arrays.stream(t).toList().stream()).toList().size();
        content.scroll(0);
        if (displayedHeight <= needed + 1) content.scrollDown(needed);
        render();
    }

    public void actualContent(@NotNull final PanelDisplay content) {
        actualContent = content;
    }

    public void selections(@NotNull final TextComponent... textComponents) {
        actualContent = new PanelDisplay(Arrays.stream(textComponents).toList());
        select(0);
    }

    public void selections(@NotNull final Collection<TextComponent> textComponents) {
        actualContent = new PanelDisplay(textComponents);
        select(0);
    }

    public PanelDisplay actualContent() {
        return actualContent;
    }

    public AnsiColor whenSelected() {
        return whenSelected;
    }

    public Map<KeyEvent, Runnable> keybinds() {
        return keybinds;
    }

    public void addKeybind(@NotNull final KeyEvent keybind, @NotNull final Runnable action) {
        keybinds.put(keybind, action);
    }

    public void active(final boolean active) {
        this.active = active;
        keylistener.close();
        keylistener = defaultKeyListener(this);
    }

    public boolean active() {
        return active;
    }

    private static Keylistener defaultKeyListener(@NotNull final SelectionPanel panel) {
        return new Keylistener() {
            @Override
            public void onKeyEvent(@NotNull final KeyEvent event) {
                if (!event.keyDown() || !panel.active) return;
                final Runnable action = panel.keybinds.get(event);
                if (action != null) {
                    action.run();
                    return;
                }

                switch (event.keycode()) {
                    case ARROW_DOWN -> {
                        if (event.ctrlOrAlt()) panel.select(panel.content.content().size() - 1);
                        else panel.selectLower();
                    }
                    case ARROW_UP -> {
                        if (event.ctrlOrAlt()) panel.select(0);
                        else panel.selectUpper();
                    }
                }
                panel.update();
            }
        };
    }

    public void selectUpper() {
        select(Math.max(currentSelection - 1, 0));
    }

    public void selectLower() {
        select(Math.min(currentSelection + 1, content.content().size() - 1));
    }

    public void scrollDown() {
        content.scrollDown(1);
    }

    public void scrollUp() {
        content.scrollUp(1);
    }

    public int currentSelection() {
        return currentSelection;
    }

    public Keylistener keylistener() {
        return keylistener;
    }
}
