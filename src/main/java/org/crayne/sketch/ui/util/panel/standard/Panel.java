package org.crayne.sketch.ui.util.panel.standard;

import org.crayne.sketch.ui.util.TUIRenderer;
import org.crayne.sketch.ui.util.border.Border;
import org.crayne.sketch.ui.util.border.BorderType;
import org.crayne.sketch.ui.util.panel.PanelDisplay;
import org.crayne.sketch.ui.util.panel.PanelOrder;
import org.crayne.sketch.util.vec.Vec;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("unused")
public class Panel {
    private final PanelOrder order;
    private Border border;
    private final List<Panel> subPanels;
    private Vec<Integer> currentSize;
    private Vec<Float> formatSize;
    private TUIRenderer tuiRenderer;
    PanelDisplay content;
    private final boolean wrapLines;

    private void verifyformatSize(@NotNull final Vec<Float> formatSize) {
        if (formatSize.x() <= 0 || formatSize.x() > 1 || formatSize.y() <= 0 || formatSize.y() > 1) {
            if (!Objects.equals(formatSize, Vec.auto())) throw new IllegalArgumentException("Illegal panel size; x and y can only range from 0 to 1. " + formatSize.x() + "x" + formatSize.y());
        }
    }

    public Panel(@NotNull final PanelOrder order, @NotNull final Border border, @NotNull final Vec<Float> formatSize, @NotNull final Collection<Panel> subPanels, final boolean wrapLines) {
        verifyformatSize(formatSize);
        this.order = order;
        this.border = border;
        this.subPanels = new ArrayList<>(subPanels);
        this.content = new PanelDisplay();
        this.formatSize = formatSize;
        this.wrapLines = wrapLines;
    }

    public Panel(@NotNull final PanelOrder order, @NotNull final Border border, @NotNull final Vec<Float> formatSize, @NotNull final PanelDisplay content, final boolean wrapLines) {
        verifyformatSize(formatSize);
        this.order = order;
        this.border = border;
        this.subPanels = new ArrayList<>();
        this.content = content;
        this.formatSize = formatSize;
        this.wrapLines = wrapLines;
    }

    public Border border() {
        return border;
    }

    public void border(@NotNull final Border border) {
        this.border = border;
    }

    public PanelDisplay content() {
        return content;
    }
    public void actualContent(@NotNull final PanelDisplay content) {
        this.content = content;
    }

    public Panel[] subPanels() {
        return subPanels.toArray(new Panel[0]);
    }

    public void addSubPanel(@NotNull final Panel panel) {
        subPanels.add(panel);
    }

    public void removeSubPanel(@NotNull final Panel panel) {
        subPanels.remove(panel);
    }

    public void clearSubPanels() {
        subPanels.clear();
    }

    public Panel[] visibleSubPanels() {
        return subPanels.stream().filter(p -> !p.content.hidden()).toList().toArray(new Panel[0]);
    }

    public PanelOrder order() {
        return order;
    }

    public void update() {
        update(false);
    }

    public void update(final boolean sub) {
        if (!sub) renderPause(true);
        for (final Panel subPanel : subPanels) {
            subPanel.update(true);
        }
        if (sub) return;
        renderPause(false);
        render();
    }

    public void active(final boolean b) {
        if (!b) for (final Panel subPanel : subPanels) {
            subPanel.active(false);
        } else if (!subPanels.isEmpty()) {
            subPanels.get(0).active(true);
        }
    }

    public boolean active() {
        return content().hidden();
    }

    public void currentSize(@NotNull final Vec<Integer> currentSize) {
        final Vec<Integer> prevSize = this.currentSize;
        this.currentSize = currentSize;
        if (prevSize == null || !prevSize.equals(currentSize)) update();
    }

    public void renderer(@NotNull final TUIRenderer renderer) {
        this.tuiRenderer = renderer;
        for (final Panel sub : subPanels) sub.renderer(this.tuiRenderer);
    }

    public void hidden(final boolean b) {
        content().hidden(b);
        render();
    }

    public TUIRenderer renderer() {
        return tuiRenderer;
    }

    private boolean renderPause = false;

    public void renderPause(final boolean b) {
        renderPause = b;
        subPanels.forEach(p -> p.renderPause(b));
    }

    public void render() {
        if (tuiRenderer != null && !renderPause) tuiRenderer.render();
    }

    public Vec<Float> size() {
        return formatSize;
    }

    public void size(@NotNull final Vec<Float> formatSize) {
        this.formatSize = formatSize;
    }

    public Vec<Integer> currentSize() {
        return currentSize;
    }

    public boolean wrapLines() {
        return wrapLines;
    }

}
