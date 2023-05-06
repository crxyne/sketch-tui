package org.crayne.sketch.keyboard;

import org.jetbrains.annotations.NotNull;

public abstract class Keylistener {
    private boolean closed;

    public Keylistener() {
        this.closed = false;
        GlobalKeylistener.newKeylistener(this);
    }

    public static Keylistener empty() {
        return new Keylistener() {@Override public void onKeyEvent(@NotNull final KeyEvent keyEvent) {}};
    }

    protected void update(@NotNull final KeyEvent newKey) {
        onKeyEvent(newKey);
    }

    public abstract void onKeyEvent(@NotNull final KeyEvent keyEvent);

    public void close() {
        this.closed = true;
        GlobalKeylistener.closeKeylistener(this);
    }

    public boolean closed() {
        return closed;
    }
}
