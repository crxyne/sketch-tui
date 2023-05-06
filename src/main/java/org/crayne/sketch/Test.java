package org.crayne.sketch;

import org.crayne.sketch.ui.dynamic.DynamicTUI;
import org.crayne.sketch.ui.util.border.Border;
import org.crayne.sketch.ui.util.border.BorderType;
import org.crayne.sketch.ui.util.panel.PanelBuilder;
import org.crayne.sketch.ui.util.panel.standard.LogPanel;
import org.crayne.sketch.util.lib.NativeSketchLibrary;
import org.crayne.sketch.util.lib.SketchLibrary;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Test {

    public static void main(@NotNull final String... args) {
        SketchLibrary.init(new File("native/sketch.so"));
        final LogPanel test = new PanelBuilder()
                .border(new Border(BorderType.ZERO_WIDTH))
                .toLogPanel();

        final DynamicTUI tui = DynamicTUI.of(test);
        tui.active(true);

        for (int i = 1; i <= 50; i++) test.log("hello! " + i);
    }

}