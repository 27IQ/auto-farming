package com.auto_farming.misc;

import static com.auto_farming.misc.RandNumberHelper.Random;

import com.auto_farming.AutofarmingClient;
import com.auto_farming.farminglogic.AutoFarmHolder;

public class ThreadHelper {

    public static final long VERY_SHORT_DURATION = 75, SHORT_DURATION = 150, MEDIUM_DURATION = 300, LONG_DURATION = 500,
            VERY_LONG_DURATION = 1000;

    public static void randomSteapSleep(long maxDuration) {
        preciseSleep(Random(0, maxDuration));
    }

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
                AutofarmingClient.LOGGER.error(e.getMessage(), e);
                return;
            }
        }

        while (System.nanoTime() < end) {
            Thread.onSpinWait();
        }

        AutoFarmHolder.get().ifPresent((farm) -> {
            farm.reportDelay((System.nanoTime() - start) / 1_000_000);
        });
    }
}
