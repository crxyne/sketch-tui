package org.crayne.sketch.ui.util.panel;

import org.crayne.sketch.text.TextComponent;
import org.crayne.sketch.util.vec.Vec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PanelDisplay {

    private int scroll;
    private List<TextComponent> content;
    private boolean hidden;

    public PanelDisplay() {
        this.scroll = 0;
        this.content = new ArrayList<>();
        this.hidden = false;
    }

    public PanelDisplay(@NotNull final Collection<TextComponent> content) {
        this.scroll = 0;
        this.content = new ArrayList<>(content);
        this.hidden = false;
    }

    public PanelDisplay(@NotNull final Vec<Integer> usableSize, @NotNull final Collection<TextComponent> content) {
        this.scroll = 0;
        this.content = new ArrayList<>(content);
        this.hidden = false;
        this.usableSize(usableSize);
    }

    public PanelDisplay(@NotNull final Collection<TextComponent> content, final int scroll) {
        this.content = new ArrayList<>(content);
        this.scroll(scroll);
        this.hidden = false;
    }

    public PanelDisplay(@NotNull final Collection<TextComponent> content, final boolean hidden) {
        this.scroll = 0;
        this.content = new ArrayList<>(content);
        this.hidden = hidden;
    }

    public PanelDisplay(@NotNull final Collection<TextComponent> content, final int scroll, final boolean hidden) {
        this.content = new ArrayList<>(content);
        this.scroll(scroll);
        this.hidden = hidden;
    }

    public PanelDisplay(@NotNull final Vec<Integer> usableSize, @NotNull final Collection<TextComponent> content, final boolean hidden) {
        this.scroll = 0;
        this.content = new ArrayList<>(content);
        this.hidden = hidden;
        this.usableSize(usableSize);
    }

    public PanelDisplay(@NotNull final Vec<Integer> usableSize, @NotNull final Collection<TextComponent> content, final int scroll, final boolean hidden) {
        this.content = new ArrayList<>(content);
        this.hidden = hidden;
        this.usableSize(usableSize);
        this.scroll(scroll);
    }

    private int usableHeight = -1;
    private int usableWidth = -1;

    public void usableSize(@NotNull final Vec<Integer> size) {
        this.usableHeight = size.y();
        this.usableWidth = size.x();
    }

    public void scrollDown(final int amt) {
        scroll(scroll + amt);
    }

    public void scrollUp(final int amt) {
        scroll(scroll - amt);
    }

    public int scroll() {
        return scroll;
    }

    public void scroll(final int amt) {
        if (usableHeight == -1 || usableWidth == -1) return;

        final int endScroll = Math.max(split(usableWidth).size() - usableHeight, 0);
        int temp = Math.max(amt, 0);
        scroll = Math.min(temp, endScroll);
    }

    public List<TextComponent> content() {
        return content;
    }

    public void content(@NotNull final Collection<TextComponent> content) {
        this.content = new ArrayList<>(content);
    }

    public boolean hidden() {
        return hidden;
    }

    public void hidden(final boolean hidden) {
        this.hidden = hidden;
    }

    public List<TextComponent> split(final int maxWidthPerLine) {
        return new ArrayList<>(content)
                .stream()
                .map(t -> t.split(maxWidthPerLine))
                .flatMap(t -> Arrays.stream(t).toList().stream())
                .toList();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public PanelDisplay clone() {
        return new PanelDisplay(new ArrayList<>(content), scroll, hidden);
    }

    public String toString() {
        return "PanelDisplay{" +
                "scroll=" + scroll +
                ", content=" + content +
                ", hidden=" + hidden +
                '}';
    }
}
