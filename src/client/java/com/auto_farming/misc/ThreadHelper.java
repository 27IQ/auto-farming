package com.auto_farming.misc;

public class ThreadHelper {
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
