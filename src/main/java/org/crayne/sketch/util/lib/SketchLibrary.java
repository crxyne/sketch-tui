package org.crayne.sketch.util.lib;

import org.crayne.sketch.keyboard.GlobalKeylistener;
import org.crayne.sketch.keyboard.KeyEvent;
import org.crayne.sketch.keyboard.KeyEventType;
import org.crayne.sketch.keyboard.Keycode;
import org.crayne.sketch.util.ansi.AnsiEscapeSequence;
import org.crayne.sketch.util.vec.Vec;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.PrintStream;

public class SketchLibrary {

    private SketchLibrary() {}

    public static void init(@NotNull final File nativeLibrary) {
        System.load(nativeLibrary.getAbsolutePath());
        NativeSketchLibrary.init();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NativeSketchLibrary.shutdown();
            GlobalKeylistener.shutdown();
            System.out.print("" + AnsiEscapeSequence.MOVE_CURSOR_HOME + AnsiEscapeSequence.CLEAR);
            cursorVisible(true);
        }));
    }

    public static void jump(@NotNull final Vec<Integer> coord) {
        System.out.print("\33[" + (coord.y() + 1) + ";" + (coord.x() + 1) + "H");
    }

    public static void jump(@NotNull final PrintStream out, @NotNull final Vec<Integer> coord) {
        out.print("\33[" + (coord.y() + 1) + ";" + (coord.x() + 1) + "H");
        out.flush();
    }

    public static int terminalWidth() {
        return NativeSketchLibrary.terminalWidth();
    }

    public static int terminalHeight() {
        return NativeSketchLibrary.terminalHeight();
    }

    public static void cursorVisible(final boolean b) {
        System.out.print("\33[?25" + (b ? "h" : "l"));
    }

    public static void cursorVisible(@NotNull final PrintStream out, final boolean b) {
        out.print("\33[?25" + (b ? "h" : "l"));
        out.flush();
    }

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public static KeyEvent awaitKeyboardEvent() {
        final int[] keyPress = NativeSketchLibrary.keyPress();
        if (keyPress == null) return KeyEvent.empty();
        final boolean keyDown = keyPress[2] != 0;

        final KeyEventType pressType = keyDown ? KeyEventType.PRESS : KeyEventType.RELEASE;

        return new KeyEvent(Keycode.of(keyPress[0]), keyPress[1], pressType);
    }

}
