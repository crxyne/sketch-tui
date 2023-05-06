package org.crayne.sketch.ui.util;

import org.crayne.sketch.text.AnsiColor;
import org.crayne.sketch.text.TextComponent;
import org.crayne.sketch.ui.util.border.Border;
import org.crayne.sketch.ui.util.panel.PanelOrder;
import org.crayne.sketch.ui.util.panel.standard.Panel;
import org.crayne.sketch.ui.util.title.Title;
import org.crayne.sketch.util.ansi.AnsiEscapeSequence;
import org.crayne.sketch.util.lib.SketchLibrary;
import org.crayne.sketch.util.vec.Vec;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.*;

import static org.crayne.sketch.util.ansi.AnsiEscapeSequence.reset;

@SuppressWarnings("unused")
public class TUIRenderer {

    private final Panel mainPanel;

    private volatile boolean active;
    private volatile boolean currentlyDisplaying = false;
    private volatile Vec<Integer> offset;

    private final BlockingQueue<Runnable> displayTaskQueue = new LinkedBlockingQueue<>();
    private final Set<Runnable> onResizeEvent = new HashSet<>();

    public void queueSynchronizedDisplayTask(@NotNull final Runnable run) {
        displayTaskQueue.add(run);
    }

    public void whenResizedDo(@NotNull final Runnable run) {onResizeEvent.add(run);}
    public void clearResizeEventListeners() {onResizeEvent.clear();}

    public void active(final boolean b) {
        if (b) start(); else stop();
    }

    private volatile int prevWidth = -1;
    private volatile int prevHeight = -1;

    public void start() {
        active = true;
        render();

        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        prevWidth = displayedWidth();
        prevHeight = displayedHeight();

        executor.scheduleAtFixedRate(() -> {
            int newWidth = displayedWidth();
            int newHeight = displayedHeight();

            if (!displayTaskQueue.isEmpty()) {
                while (!displayTaskQueue.isEmpty()) {
                    try {
                        displayTaskQueue.take().run();
                    } catch (final InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                render();
            }
            if (newWidth != prevWidth || newHeight != prevHeight) {
                prevWidth = newWidth;
                prevHeight = newHeight;
                for (final Runnable whenResized : new ArrayList<>(onResizeEvent)) whenResized.run();
                render();
            }
        }, 0, 200, TimeUnit.MILLISECONDS);

    }

    public void stop() {
        active = false;
    }

    public TUIRenderer(@NotNull final Panel mainPanel) {
        this.mainPanel = mainPanel;
        this.mainPanel.renderer(this);
        this.offset = new Vec<>(0, 0);
    }

    public TUIRenderer(@NotNull final Panel mainPanel, @NotNull final Vec<Integer> offset) {
        this.mainPanel = mainPanel;
        this.mainPanel.renderer(this);
        this.offset = offset;
    }

    public Vec<Integer> offset() {
        return offset;
    }

    public void offset(@NotNull final Vec<Integer> offset) {
        this.offset = offset;
    }

    public int displayedWidth() {
        return 1 + (mainPanel.size().isAuto() ? SketchLibrary.terminalWidth() - offset().x() : (int) (SketchLibrary.terminalWidth() * mainPanel.size().x()));
    }

    public int displayedHeight() {
        return mainPanel.size().isAuto() ? SketchLibrary.terminalHeight() - offset().y() : (int) (SketchLibrary.terminalHeight() * mainPanel.size().y());
    }

    public void render() {
        if (currentlyDisplaying) return;
        render(new Vec<>(displayedWidth(), displayedHeight()), mainPanel, offset);
    }

    private static final PrintStream sysout = new PrintStream(new BufferedOutputStream(System.out));

    private static void print(final Object obj) {
        sysout.print(obj);
        sysout.flush();
    }

    public Panel mainPanel() {
        return mainPanel;
    }

    private static void offsetCursor(@NotNull final Vec<Integer> offset) {
        print(AnsiEscapeSequence.MOVE_CURSOR_HOME);
        SketchLibrary.jump(sysout, offset);
    }

    private static void offsetCursor(@NotNull final Vec<Integer> offset, @NotNull final Vec<Integer> add) {
        print(AnsiEscapeSequence.MOVE_CURSOR_HOME);
        final Vec<Integer> res = new Vec<>(offset.x() + add.x(), offset.y() + add.y());
        SketchLibrary.jump(sysout, res);
    }

    private static void displayTitle(@NotNull final Panel panel, final int dWidth, final boolean top) {
        final Border border = panel.border();
        final Title title = Optional.ofNullable(border.titles()[top ? 0 : 1]).orElse(Title.empty());

        if (panel.content().hidden()) {
            print(AnsiColor.RESET + reset(" ".repeat(dWidth)));
            return;
        }
        print(title.rendered(dWidth, border, top));
    }

    private static void displayBorder(@NotNull final Panel panel, @NotNull final Vec<Integer> dSize, @NotNull final Vec<Integer> offset) {
        final int dWidth = dSize.x();
        final int dHeight = dSize.y();

        displayTitle(panel, dWidth, true);

        for (int i = 1; i < dHeight; i++) {
            offsetCursor(offset, new Vec<>(0, i));
            print(panel.border().left() + AnsiColor.RESET);
            offsetCursor(offset, new Vec<>(dWidth - 1, i));
            print(panel.border().right() + AnsiColor.RESET);
        }
        offsetCursor(offset, new Vec<>(0, dHeight));

        displayTitle(panel, dWidth, false);
    }

    private void displaySubpanels(@NotNull final Panel panel, final int uWidth, final int uHeight, @NotNull final Vec<Integer> offset) {
        final Panel[] subs = panel.visibleSubPanels();
        final int len = subs.length;

        int offX = 1;
        int offY = 1;

        final boolean ltr = panel.order() == PanelOrder.LEFT_TO_RIGHT;

        for (int i = 0; i < len; i++) {
            final Panel sub = subs[i];
            final boolean autoSize = Objects.equals(sub.size(), Vec.auto());

            int subWidth = ltr ? (autoSize ? (int) Math.round(uWidth / (double) len) : (int) (uWidth * sub.size().x())) : uWidth;
            int subHeight = !ltr ? (autoSize ? (int) Math.round(uHeight / (double) len) : (int) (uHeight * sub.size().y())) : uHeight;

            final boolean last = i == len - 1;

            final int wTweak = last ? uWidth - offX - subWidth + 1 : 0; // use either as little or as much space as needed, to 100% fill out every gap but not exceed the border
            final int hTweak = last ? uHeight - offY - subHeight + 1 : 0;

            final Vec<Integer> offNew = new Vec<>(offset.x() + offX, offset.y() + offY);

            offX += ltr ? subWidth + wTweak : 0;
            offY += !ltr ? subHeight + hTweak + 1 : 0;

            render(new Vec<>(subWidth + wTweak, subHeight + hTweak), sub, offNew);
        }
    }

    private void displayContent(@NotNull final Panel panel, final int uWidth, final int uHeight, @NotNull final Vec<Integer> offset) {
        if (panel.visibleSubPanels().length != 0 || panel.content().hidden()) return;

        final List<TextComponent> content = panel.wrapLines() ? panel.content().split(uWidth) : panel.content().content();
        final int scroll = panel.content().scroll();

        for (int i = 0; i < uHeight + 1; i++) {
            final int textLineIndex = i + scroll;
            final TextComponent contentLine = textLineIndex < content.size() && textLineIndex >= 0 ? content.get(textLineIndex) : TextComponent.empty();

            SketchLibrary.jump(new Vec<>(offset.x() + (panel.border().leftHasWidth() ? 1 : 0), offset.y() + (panel.border().topHasWidth() ? 1 : 0) + i));
            final String withoutColor = contentLine.text().replaceAll("[\n\r]", "").replace("\t", "    ");

            if (withoutColor.length() == 0 || panel.content().hidden()) {
                print(" ".repeat(uWidth));
                continue;
            }
            final String toPrint = contentLine.toString(false).replaceAll("[\n\r]", "").replace("\t", "    ");
            print(toPrint + // fill panel with content, line by line
                    " ".repeat(Math.max(0, uWidth - withoutColor.length()))); // fill unused space with literal spaces, to clear old junk from display
        }
        print(AnsiColor.RESET);
    }

    private void render(@NotNull final Vec<Integer> usableSpace, @NotNull final Panel panel, @NotNull final Vec<Integer> offset) {
        currentlyDisplaying = true;
        offsetCursor(offset);

        final int dWidth = usableSpace.x();
        final int dHeight = usableSpace.y();
        final int uWidth = dWidth - (panel.border().leftHasWidth() ? 1 : 0) - (panel.border().rightHasWidth() ? 1 : 0);
        // width and height - 2 because of the 2 border characters at the left and right / top and bottom
        // unless the border is zero width, meaning we allow using that extra space
        final int uHeight = dHeight - (panel.border().topHasWidth() ? 1 : 0) - (panel.border().bottomHasWidth() ? 1 : 0);

        final Vec<Integer> dSize = new Vec<>(dWidth, dHeight);
        panel.currentSize(new Vec<>(uWidth, uHeight + 1));
        SketchLibrary.cursorVisible(sysout, false);

        displayBorder(panel, dSize, offset);
        displaySubpanels(panel, uWidth, uHeight, offset);
        displayContent(panel, uWidth, uHeight, offset);
        currentlyDisplaying = false;
    }

}
