package com.auto_farming.misc;

import static com.auto_farming.misc.RandNumberHelper.Random;

public class ThreadHelper {

    public static final long SHORT_DURATION = 50, MEDIUM_DURATION = 150, LONG_DURATION = 300;

    public static void randomSleep(long baseDuration) {
        preciseSleep(Random(baseDuration, (long) (baseDuration * 1.5)));
    }

    public static void preciseSleep(long ms) {
        long start = System.nanoTime();
        long end = start + ms * 1_000_000;

        if (ms > 20) {
            try {
                Thread.sleep(ms - 15);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        while (System.nanoTime() < end) {
            Thread.onSpinWait();
        }
    }
}
