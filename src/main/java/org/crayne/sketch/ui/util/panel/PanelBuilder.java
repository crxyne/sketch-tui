package org.crayne.sketch.ui.util.panel;

import org.crayne.sketch.text.AnsiColor;
import org.crayne.sketch.text.TextComponent;
import org.crayne.sketch.ui.util.TUIRenderer;
import org.crayne.sketch.ui.util.border.Border;
import org.crayne.sketch.ui.util.border.BorderType;
import org.crayne.sketch.ui.util.panel.standard.*;
import org.crayne.sketch.ui.util.table.TableRowFormat;
import org.crayne.sketch.util.vec.Vec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class PanelBuilder {

    @NotNull
    private PanelOrder order;

    @NotNull
    private Border border;

    @NotNull
    private List<Panel> subPanels;

    @NotNull
    private Vec<Float> formatSize;

    @Nullable
    private PanelDisplay content;

    private boolean wrapLines;

    @Nullable
    private Panel parentPanel;

    public PanelBuilder() {
        order = PanelOrder.TOP_TO_BOTTOM;
        border = BorderType.NORMAL.border();
        subPanels = Collections.emptyList();
        formatSize = Vec.auto();
        wrapLines = true;
    }

    public PanelBuilder(@NotNull final PanelBuilder other) {
        order = other.order;
        border = other.border;
        subPanels = other.subPanels;
        formatSize = other.formatSize;
        wrapLines = other.wrapLines;
        content = other.content;
        parentPanel = other.parentPanel;
    }

    private void addToParent(@NotNull final Panel panel) {
        if (parentPanel != null) parentPanel.addSubPanel(panel);
    }

    @NotNull
    public Panel toParentPanel() {
        return new Panel(order, border, formatSize, subPanels, wrapLines) {{ addToParent(this); }};
    }

    @NotNull
    public Panel toPanel() {
        if (content == null) throw new NullPointerException("Panel content must not be null");
        return new Panel(order, border, formatSize, content, wrapLines) {{ addToParent(this); }};
    }

    @NotNull
    public InputPanel toInputPanel(final InputPanel nextPanel, final boolean hideInput) {
        return new InputPanel(border, formatSize, nextPanel, hideInput) {{ addToParent(this); }};
    }

    @NotNull
    public LogPanel toLogPanel() {
        return new LogPanel(border, formatSize) {{ addToParent(this); }};
    }

    @NotNull
    public LogPanel toLogPanel(final int maxLogSize) {
        return new LogPanel(border, formatSize, maxLogSize) {{ addToParent(this); }};
    }

    @NotNull
    public PopupPanel toPopupPanel(@NotNull final TextComponent text, @NotNull final PopupPanel.Button... buttons) {
        return new PopupPanel(border, formatSize, text, buttons) {{ addToParent(this); }};
    }

    @NotNull
    public PopupPanel toPopupPanel(@NotNull final TextComponent text, final int textButtonOffset,
                                   @NotNull final PopupPanel.Button... buttons) {

        return new PopupPanel(border, formatSize, text, textButtonOffset, buttons) {{ addToParent(this); }};
    }

    @NotNull
    public PopupPanel toPopupPanel(@NotNull final TextComponent text, final int textButtonOffset,
                                   final int buttonDistance, @NotNull final PopupPanel.Button... buttons) {

        return new PopupPanel(border, formatSize, text, textButtonOffset, buttonDistance, buttons) {{ addToParent(this); }};
    }

    @NotNull
    public PopupPanel toPopupPanel(@NotNull final TextComponent text, final int textButtonOffset,
                                   final int buttonDistance, final int globalOffset, @NotNull final PopupPanel.Button... buttons) {

        return new PopupPanel(border, formatSize, text, textButtonOffset, buttonDistance, globalOffset, buttons) {{ addToParent(this); }};
    }

    @NotNull
    public SelectionPanel toSelectionPanel(@NotNull final AnsiColor whenSelected, @NotNull final TextComponent... selections) {
        return new SelectionPanel(border, formatSize, whenSelected, selections) {{ addToParent(this); }};
    }

    @NotNull
    public TablePanel toTablePanel(@NotNull final TableRowFormat tableFormat, @NotNull final AnsiColor whenSelected) {
        return new TablePanel(border, formatSize, tableFormat, whenSelected) {{ addToParent(this); }};
    }

    @Nullable
    public Panel parentPanel() {
        return parentPanel;
    }

    @NotNull
    public PanelBuilder parentPanel(@Nullable final Panel parentPanel) {
        this.parentPanel = parentPanel;
        return this;
    }

    public boolean wrapLines() {
        return wrapLines;
    }

    @NotNull
    public PanelBuilder wrapLines(final boolean wrapLines) {
        this.wrapLines = wrapLines;
        return this;
    }

    @NotNull
    public List<Panel> subPanels() {
        return subPanels;
    }

    @NotNull
    public Border border() {
        return border;
    }

    @Nullable
    public PanelDisplay content() {
        return content;
    }

    @NotNull
    public PanelOrder order() {
        return order;
    }

    @NotNull
    public PanelBuilder border(@NotNull final Border border) {
        this.border = border;
        return this;
    }

    @NotNull
    public PanelBuilder order(@NotNull final PanelOrder order) {
        this.order = order;
        return this;
    }

    @NotNull
    public PanelBuilder content(@Nullable final PanelDisplay content) {
        this.content = content;
        return this;
    }

    @NotNull
    public PanelBuilder subPanels(@NotNull final List<Panel> subPanels) {
        this.subPanels = subPanels;
        return this;
    }

    @NotNull
    public Vec<Float> formatSize() {
        return formatSize;
    }

    @NotNull
    public PanelBuilder formatSize(@NotNull final Vec<Float> formatSize) {
        this.formatSize = formatSize;
        return this;
    }

}
