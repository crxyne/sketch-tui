package org.crayne.sketch.ui.dynamic.exception;

import org.jetbrains.annotations.NotNull;

public class ScreenNotFoundException extends RuntimeException {

    public ScreenNotFoundException() {super();}
    public ScreenNotFoundException(@NotNull final Throwable t) {super(t);}
    public ScreenNotFoundException(@NotNull final String s) {super(s);}

}
