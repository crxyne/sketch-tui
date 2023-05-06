package org.crayne.sketch.keyboard;

import org.crayne.sketch.util.lib.SketchLibrary;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GlobalKeylistener {

    private static final Set<Keylistener> subscribed = new HashSet<>();
    private static final Set<Keycode> heldDown = new HashSet<>();
    private static volatile boolean active;
    private static final BlockingQueue<Runnable> listenerQueue = new LinkedBlockingQueue<>();

    private GlobalKeylistener() {}

    static {
        active = true;
        new Thread(() -> {
            while (active) {
                //noinspection deprecation
                final KeyEvent event = SketchLibrary.awaitKeyboardEvent();

                if (!event.keyDown())
                    heldDown.remove(event.keycode());

                while (!listenerQueue.isEmpty()) {
                    try {
                        listenerQueue.take().run();
                    } catch (final InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                new HashSet<>(subscribed).forEach(k -> {
                    if (k.closed()) return;
                    k.update(new KeyEvent(event.keycode(), heldDown, event.character().orElse(null), event.pressType()));
                });

                if (event.keyDown())
                    heldDown.add(event.keycode());
            }
        }).start();
    }

    public static void newKeylistener(@NotNull final Keylistener listener) {
        listenerQueue.add(() -> subscribed.add(listener));
    }

    public static void closeKeylistener(@NotNull final Keylistener listener) {
        listenerQueue.add(() -> subscribed.remove(listener));
    }

    public static void shutdown() {
        active = false;
    }

}
