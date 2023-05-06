# sketch-tui
buggy and slow mess of code that somehow performs terminal ui magic

## getting started
this cross-platform library uses the terminal to draw nice user interfaces in a quick and dirty way.
```java
public static void main(@NotNull final String... args) {
        SketchLibrary.init(new File("native/sketch.so"));
        final LogPanel test = new PanelBuilder()
                .border(new Border(BorderType.ZERO_WIDTH))
                .toLogPanel();

        final DynamicTUI tui = DynamicTUI.of(test);
        tui.active(true);

        for (int i = 1; i <= 50; i++) test.log("hello! " + i);
    }
```
using this can help reduce testing times by making a simple debug log output look nicer - or if you really wanted to, you can also make a full project with this (not recommended however, since this is a really really slow library not really designed for production use).
resizable terminal windows are supported, as well as keypresses without changing any code when switching from windows to linux, multi panel terminal ui windows, multi screen applications, and much more is all included in one package

### maven
soon
