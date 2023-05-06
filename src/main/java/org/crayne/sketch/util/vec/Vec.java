package org.crayne.sketch.util.vec;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Vec<T extends Number> {

    private final T v1;
    private final T v2;

    public Vec(@NotNull final T v1, @NotNull final T v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public T x() {return v1;}
    public T y() {return v2;}

    public boolean isAuto() {
        return (Float) v1 == -1f && (Float) v2 == -1f;
    }

    public static Vec<Float> auto() {
        return new Vec<>(-1f, -1f);
    }

    public static Vec<Float> zero() {
        return new Vec<>(0f, 0f);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec<?> vec = (Vec<?>) o;
        return Objects.equals(v1, vec.v1) && Objects.equals(v2, vec.v2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(v1, v2);
    }

    @Override
    public String toString() {
        return "x=" + x() + ", y=" + y();
    }
}
