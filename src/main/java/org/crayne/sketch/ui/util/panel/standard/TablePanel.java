package org.crayne.sketch.ui.util.panel.standard;

import org.crayne.sketch.keyboard.KeyEvent;
import org.crayne.sketch.keyboard.Keylistener;
import org.crayne.sketch.text.AnsiColor;
import org.crayne.sketch.text.ComponentPart;
import org.crayne.sketch.text.TextComponent;
import org.crayne.sketch.ui.util.border.Border;
import org.crayne.sketch.ui.util.border.BorderType;
import org.crayne.sketch.ui.util.panel.PanelDisplay;
import org.crayne.sketch.ui.util.panel.PanelOrder;
import org.crayne.sketch.ui.util.table.TableBorder;
import org.crayne.sketch.ui.util.table.TableRowFormat;
import org.crayne.sketch.util.vec.Vec;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("unused")
public class TablePanel extends Panel {

    private final Map<KeyEvent, Runnable> keybinds;
    private volatile boolean active;
    private int currentSelection;
    private final AnsiColor whenSelected;
    private Keylistener keylistener;
    private final TableRowFormat tableFormat;
    private List<TextComponent[]> tableRows;

    public TablePanel(@NotNull final Border border, @NotNull final Vec<Float> formatSize,
                      @NotNull final TableRowFormat tableFormat, @NotNull final AnsiColor whenSelected) {
        super(PanelOrder.LEFT_TO_RIGHT, border, formatSize, new PanelDisplay(), false);
        this.keybinds = new HashMap<>();
        this.tableFormat = tableFormat;
        this.tableRows = new ArrayList<>();
        this.tableRows.add(this.tableFormat.format());
        this.whenSelected = whenSelected;
        keylistener = defaultKeyListener(this);
        active(active);
    }

    public void addRow(@NotNull final TextComponent... row) {
        if (tableFormat.length() != row.length) throw new IllegalArgumentException("Table size (" + tableFormat.length() + ") is not equal to the new row size: " + Arrays.toString(row));
        tableRows.add(row);
    }

    public void rows(@NotNull final TextComponent[][] rows) {
        tableRows = new ArrayList<>();
        tableRows.add(tableFormat.format());
        tableRows.addAll(Arrays.stream(rows).toList());
    }

    public List<TextComponent[]> rows() {
        return new ArrayList<>(tableRows).subList(1, tableRows.size());
    }

    public void clearRows() {
        tableRows.clear();
        tableRows.add(tableFormat.format());
    }

    public void removeRow(final int index) {
        if (index < 0) throw new IndexOutOfBoundsException(); // don't allow removing the table format
        tableRows.remove(index + 1);
    }

    public int length() {
        return tableRows.size() - 1;
    }

    public void update(final boolean sub) {
        update();
    }

    public void update() {
        if (currentSize() == null || content.hidden()) return; // content has been updated atleast once

        final int width = currentSize().x();

        final List<TextComponent> newContent = new ArrayList<>();

        newContent.add(tableTop(width));
        newContent.addAll(tableRows(width));

        newContent.remove(newContent.size() - 1);
        newContent.add(tableBottom(width));

        content = new PanelDisplay(currentSize(), newContent.stream().filter(t -> t.text().length() != 0).toList(), content.scroll(), content.hidden());
        autoscroll();
        render();
    }

    public Map<KeyEvent, Runnable> keybinds() {
        return keybinds;
    }

    public void addKeybind(@NotNull final KeyEvent keybind, @NotNull final Runnable action) {
        keybinds.put(keybind, action);
    }

    private TextComponent tableTop(final int panelWidth) {
        final TableBorder border = tableFormat.tableBorder();
        return tableConnector(panelWidth, border.topLeft(), border.topConnector(), border.topRight());
    }

    private TextComponent tableBottom(final int panelWidth) {
        final TableBorder border = tableFormat.tableBorder();
        return tableConnector(panelWidth, border.bottomLeft(), border.bottomConnector(), border.bottomRight());
    }

    private TextComponent tableCenter(final int panelWidth) {
        final TableBorder border = tableFormat.tableBorder();
        return tableConnector(panelWidth, border.leftConnector(), border.crossConnector(), border.rightConnector());
    }

    private TextComponent tableConnector(final int panelWidth, final String left, final String center, final String right) {
        final TableBorder border = tableFormat.tableBorder();
        final List<ComponentPart> result = new ArrayList<>();
        result.add(border.comp(left));
        int tempSize = 1;

        for (final float size : tableFormat.displayedSizesAuto()) {
            final String horiz = tableFormat.tableBorder().horizontal(calcColumnSize(panelWidth, size));
            tempSize += horiz.length() + 1;
            result.add(border.comp(horiz.substring(0, horiz.length() - (tempSize + 1 >= panelWidth ? 1 : 0))));
            result.add(border.comp(center));
        }
        result.remove(result.size() - 1);
        result.add(border.comp(right));
        return TextComponent.of(result);
    }

    private int calcColumnSize(final int panelWidth, final float size) {
        return (int) ((panelWidth - tableFormat.length() - 1) * size);
    }

    private List<List<TextComponent[]>> breakColumnLines(final int panelWidth) {
        final List<List<TextComponent[]>> temp = new ArrayList<>();

        for (final TextComponent[] row : tableRows) {
            final List<TextComponent[]> rowLineBreaking = new ArrayList<>();
            final Float[] sizes = tableFormat.displayedSizesAuto();
            for (int i = 0; i < row.length; i++) {
                final float size = sizes[i];
                final TextComponent col = row[i].replaceAll("[\r\n]", "");
                final int colSize = calcColumnSize(panelWidth, size);

                if (col == null) break;

                final TextComponent[] colLineBreaking = col.split(colSize - 1);
                rowLineBreaking.add(colLineBreaking);
            }
            temp.add(rowLineBreaking);
            temp.add(null);
        }
        return temp;
    }

    private TextComponent lineBrokenColumn(final int panelWidth, final int line, @NotNull final List<TextComponent[]> rowLineBreaking, final int current) {
        final TableBorder border = tableFormat.tableBorder();
        final TextComponent vertical = TextComponent.of(border.comp(border.vertical()));
        final TextComponent add = TextComponent.empty();
        final Float[] sizes = tableFormat.displayedSizesAuto();

        add.append(vertical);
        int tempSize = 1;

        for (int col = 0; col < rowLineBreaking.size(); col++) {
            final TextComponent[] columnLineBreaking = rowLineBreaking.get(col);
            final float size = sizes[col];
            final int colSize = calcColumnSize(panelWidth, size);
            tempSize += colSize + 1;
            final TextComponent columnSingle = line < columnLineBreaking.length ? columnLineBreaking[line] : TextComponent.empty();

            if (current - 1 == currentSelection) add.append(whenSelected);
            add.append(columnSingle)
                    .append(" ".repeat(Math.max(0, colSize - columnSingle.text().length() - (tempSize + 1 >= panelWidth ? 1 : 0))))
                    .append(AnsiColor.RESET_ANSI_COLOR)
                    .append(vertical);
        }
        return add.append(AnsiColor.RESET_ANSI_COLOR);
    }

    private List<TextComponent> tableRows(final int panelWidth) {
        final List<List<TextComponent[]>> linesBroken = breakColumnLines(panelWidth);

        final List<TextComponent> result = new ArrayList<>();
        int current = 0;
        for (final List<TextComponent[]> rowLineBreaking : linesBroken) {
            if (rowLineBreaking == null) {
                result.add(tableCenter(panelWidth));
                continue;
            }
            final int maxLineBreaksCol = rowLineBreaking
                    .stream()
                    .map(t -> t.length)
                    .mapToInt(i -> i)
                    .max()
                    .orElse(0); // get the amount of lines one row takes up

            for (int line = 0; line < maxLineBreaksCol; line++) {
                result.add(lineBrokenColumn(panelWidth, line, rowLineBreaking, current));
            }
            current++;
        }
        return result;
    }

    public boolean active() {
        return active;
    }

    public int currentSelection() {
        return currentSelection;
    }

    public void active(final boolean active) {
        this.active = active;
        currentSelection = active ? 0 : -1;
        keylistener.close();
        keylistener = defaultKeyListener(this);
    }

    public void select(final int s) {
        try {
            if (tableRows.size() == 1) return;
            // size - 2, because were adding the format to the table rows.
            currentSelection = Math.max(0, Math.min(tableRows.size() - 2, s));
            autoscroll();
        } catch (final Exception ignored) {}
    }

    public void autoscroll() {
        if (currentSize() == null || currentSelection == -1) return;

        final int panelWidth = currentSize().x();
        final List<TextComponent[]> rows = tableRows
                .subList(0, currentSelection)
                .stream()
                .toList();
        final int scroll = rows.stream().map(col -> {
            final List<Integer> sizes = new ArrayList<>();
            for (int i = 0; i < col.length; i++) {
                sizes.add(col[i].split(calcColumnSize(panelWidth, tableFormat.displayedSizesAuto()[i])).length);
            }
            return sizes.stream().mapToInt(i -> i).max().orElse(0);
        }).mapToInt(i -> i + 1).sum();

        content.scroll(scroll);
    }

    public void selectUpper() {
        select(currentSelection - 1);
    }

    public void selectLower() {
        select(currentSelection + 1);
    }

    private static Keylistener defaultKeyListener(@NotNull final TablePanel panel) {
        return new Keylistener() {
            @Override
            public void onKeyEvent(@NotNull final KeyEvent event) {
                if (!event.keyDown() || !panel.active) return;
                final Runnable action = panel.keybinds.get(event);
                if (action != null) {
                    action.run();
                    return;
                }

                switch (event.keycode()) {
                    case ARROW_DOWN -> {
                        if (event.ctrlOrAlt()) panel.select(panel.tableRows.size() - 2); else panel.selectLower();
                    }
                    case ARROW_UP -> {
                        if (event.ctrlOrAlt()) panel.select(0); else panel.selectUpper();
                    }
                }
                panel.update();
            }
        };
    }

    public Keylistener keylistener() {
        return keylistener;
    }

}
