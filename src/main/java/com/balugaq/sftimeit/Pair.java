package com.balugaq.sftimeit;

import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.Objects;

@NullMarked
public class Pair<P, S> {
    private P firstValue;
    private S secondValue;

    public Pair(P a, S b) {
        this.firstValue = a;
        this.secondValue = b;
    }

    public Pair(Map.Entry<P, S> mapEntry) {
        this(mapEntry.getKey(), mapEntry.getValue());
    }

    public static <P, S> Pair<P, S> of(P a, S b) {
        return new Pair<>(a, b);
    }

    public static <P, S> Pair<P, S> of(Map.Entry<P, S> mapEntry) {
        return new Pair<>(mapEntry);
    }

    public P first() {
        return this.firstValue;
    }

    public S second() {
        return this.secondValue;
    }

    public void setFirstValue(P firstValue) {
        this.firstValue = firstValue;
    }

    public void setSecondValue(S secondValue) {
        this.secondValue = secondValue;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Pair<?, ?> other)) {
            return false;
        } else {
            return Objects.equals(this.firstValue, other.firstValue) && Objects.equals(this.secondValue, other.secondValue);
        }
    }

    public int hashCode() {
        int prime = 59;
        int result = 1;
        result = result * 59 + this.firstValue.hashCode();
        result = result * 59 + this.secondValue.hashCode();
        return result;
    }

    public String toString() {
        return "Pair(firstValue=" + this.firstValue + ", secondValue=" + this.secondValue + ")";
    }
}
