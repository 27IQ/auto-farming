package com.auto_farming.misc;

public class RandNumberHelper {
    public static long Random(long min, long max) {
        if (min > max)
            throw new IllegalArgumentException("min must be <= max");

        return min + (long) (Math.random() * ((max - min) + 1));
    }

    public static int Random(int min, int max) {
        if (min > max)
            throw new IllegalArgumentException("min must be <= max");

        return min + (int) (Math.random() * ((max - min) + 1));
    }
}
