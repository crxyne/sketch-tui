package org.crayne.sketch.ui.util.title;

import org.crayne.sketch.text.TextComponent;
import org.crayne.sketch.ui.util.border.Border;
import org.jetbrains.annotations.NotNull;

public interface Title {

    TextComponent title();
    String rendered(final int givenWidth, @NotNull final Border border, final boolean top);

    static Title empty() {
        return SimpleTitle.empty();
    }

}
