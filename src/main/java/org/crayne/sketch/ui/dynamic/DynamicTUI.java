package org.crayne.sketch.ui.dynamic;

import org.crayne.sketch.keyboard.KeyEvent;
import org.crayne.sketch.keyboard.Keylistener;
import org.crayne.sketch.ui.dynamic.exception.ScreenNotFoundException;
import org.crayne.sketch.ui.util.panel.standard.Panel;
import org.crayne.sketch.ui.util.TUIRenderer;
import org.crayne.sketch.util.vec.Vec;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class DynamicTUI {

    private final Map<Long, Panel> screens;
    private final Map<KeyEvent, Runnable> keybinds;
    private final TUIRenderer tui;
    private volatile long currentScreen;
    private volatile boolean active;
    private Keylistener keylistener;

    public DynamicTUI(@NotNull final Panel mainPanel, @NotNull final Map<Long, Panel> screens, final long activeScreen, @NotNull final Map<KeyEvent, Runnable> keybinds) {
        this.tui = new TUIRenderer(mainPanel);
        this.screens = new ConcurrentHashMap<>(screens);
        this.keybinds = new HashMap<>(keybinds);
        switchTo(activeScreen);
    }

    public DynamicTUI(@NotNull final Panel mainPanel, @NotNull final Map<Long, Panel> screens, final long activeScreen) {
        this.tui = new TUIRenderer(mainPanel);
        this.screens = new ConcurrentHashMap<>(screens);
        this.keybinds = new HashMap<>();
        switchTo(activeScreen);
    }

    public DynamicTUI(@NotNull final Panel mainPanel) {
        this.tui = new TUIRenderer(mainPanel);
        this.screens = new ConcurrentHashMap<>();
        this.keybinds = new HashMap<>();
        currentScreen = -1;
    }

    public DynamicTUI(@NotNull final Panel mainPanel, @NotNull final Map<Long, Panel> screens, final long activeScreen, @NotNull final Map<KeyEvent, Runnable> keybinds, @NotNull final Vec<Integer> offset) {
        this.tui = new TUIRenderer(mainPanel, offset);
        this.screens = new ConcurrentHashMap<>(screens);
        this.keybinds = new HashMap<>(keybinds);
        switchTo(activeScreen);
    }

    public DynamicTUI(@NotNull final Panel mainPanel, @NotNull final Map<Long, Panel> screens, final long activeScreen, @NotNull final Vec<Integer> offset) {
        this.tui = new TUIRenderer(mainPanel, offset);
        this.screens = new ConcurrentHashMap<>(screens);
        this.keybinds = new HashMap<>();
        switchTo(activeScreen);
    }

    public DynamicTUI(@NotNull final Panel mainPanel, @NotNull final Vec<Integer> offset) {
        this.tui = new TUIRenderer(mainPanel, offset);
        this.screens = new ConcurrentHashMap<>();
        this.keybinds = new HashMap<>();
        currentScreen = -1;
    }

    public static DynamicTUI of(@NotNull final Panel mainPanel) {
        final DynamicTUI tui = new DynamicTUI(mainPanel);
        tui.addScreen(0, mainPanel);
        tui.switchTo(0);
        return tui;
    }

    public boolean active() {
        return active;
    }

    public void active(final boolean active) {
        final boolean wasActive = this.active;
        this.active = active;
        if (keylistener != null) keylistener.close();
        if (this.active && !wasActive) {
            keylistener = defaultKeyListener(this);
            start();
            updateScreen();
            return;
        }
        tui.stop();
        screens.keySet().forEach(l -> {
            screens.get(l).active(false);
            screens.get(l).content().hidden(true);
        });
    }

    private void start() {
        if (currentScreen == -1) throw new ScreenNotFoundException("Cannot start dynamic TUIRenderer, no screen was selected");
        tui.start();
    }

    private static Keylistener defaultKeyListener(@NotNull final DynamicTUI TUIRenderer) {
        return new Keylistener() {
            @Override
            public void onKeyEvent(@NotNull final KeyEvent event) {
                final Runnable action = TUIRenderer.keybinds.get(event);
                if (action != null && event.keyDown()) action.run();
            }
        };
    }

    public TUIRenderer tui() {
        return tui;
    }

    public long currentScreen() {
        return currentScreen;
    }

    public Map<KeyEvent, Runnable> keybinds() {
        return keybinds;
    }

    public void addKeybind(@NotNull final KeyEvent keybind, @NotNull final Runnable action) {
        keybinds.put(keybind, action);
    }

    public Map<Long, Panel> screens() {
        return screens;
    }

    public void addScreen(final long id, @NotNull final Panel panel) {
        panel.active(false);
        this.screens.put(id, panel);
    }

    public Panel screen(final long id) {
        validateScreen(id);
        return screens.get(id);
    }

    public Keylistener keylistener() {
        return keylistener;
    }

    public void switchTo(final long id) {
        validateScreen(id);
        currentScreen = id;
        updateScreen();
    }

    public void updateScreen() {
        synchronized (tui) {
            final Panel screen = screen(currentScreen);
            final Panel main = tui.mainPanel();
            main.renderPause(true);
            screens.keySet().forEach(l -> {
                screen(l).hidden(l != currentScreen);
                screen(l).active(l == currentScreen);
            });
            main.renderPause(false);
            screen.update();
            tui.render();
        }
    }

    public void validateScreen(final long id) {
        if (!screens.containsKey(id)) throw new ScreenNotFoundException("Dynamic TUIRenderer does not contain a screen with id " + id);
    }

}
