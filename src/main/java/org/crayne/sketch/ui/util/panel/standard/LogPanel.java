package org.crayne.sketch.ui.util.panel.standard;

import org.crayne.sketch.text.ComponentPart;
import org.crayne.sketch.text.TextComponent;
import org.crayne.sketch.ui.util.border.Border;
import org.crayne.sketch.ui.util.panel.PanelDisplay;
import org.crayne.sketch.ui.util.panel.PanelOrder;
import org.crayne.sketch.util.vec.Vec;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@SuppressWarnings("unused")
public class LogPanel extends Panel {

    private final int logMaxSize;
    private boolean autoscroll;
    private boolean autobuffer;

    public LogPanel(@NotNull final Border border, @NotNull final Vec<Float> formatSize) {
        super(PanelOrder.LEFT_TO_RIGHT, border, formatSize, new PanelDisplay(), true);
        this.logMaxSize = 100;
        this.autoscroll = true;
        this.autobuffer = true;
    }

    public LogPanel(@NotNull final Border border, @NotNull final Vec<Float> formatSize, final int logMaxSize) {
        super(PanelOrder.LEFT_TO_RIGHT, border, formatSize, new PanelDisplay(), true);
        this.logMaxSize = logMaxSize;
        this.autoscroll = true;
        this.autobuffer = true;
    }

    public void log(@NotNull final String str) {
        content.content().add(TextComponent.of(Arrays.stream(str.replace("\t", "    ").split("\r")).map(ComponentPart::plain).toList()));
        if (content.content().size() > logMaxSize) content.content().remove(0);
        if (autoscroll) {
            scrollToEnd();
            if (autobuffer) update();
        }
    }

    public void log(@NotNull final TextComponent text) {
        content.content().add(text.replace("\t", "    "));
        if (content.content().size() > logMaxSize) content.content().remove(0);
        if (autoscroll) {
            scrollToEnd();
            if (autobuffer) update();
        }
    }

    public boolean autoscroll() {
        return autoscroll;
    }

    public boolean autobuffer() {
        return autobuffer;
    }

    public void autobuffer(final boolean autobuffer) {
        this.autobuffer = autobuffer;
    }

    public void autoscroll(final boolean autoscroll) {
        this.autoscroll = autoscroll;
    }

    public void scrollDown() {
        content.scrollDown(1);
    }

    public void scrollUp() {
        content.scrollUp(1);
    }

    public void scrollToStart() {
        content.scroll(0);
    }

    public void update(final boolean sub) {
        update();
    }

    public void update() {
        if (content().hidden()) return;
        if (currentSize() != null) content.usableSize(currentSize());

        render();
    }

    public void scrollToEnd() {
        content.scroll(Integer.MAX_VALUE); // the panel display function sets this back to the actual max
    }

}
